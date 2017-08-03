package demo.grant.code.test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseExtractor;

import demo.ClientApplication;
import demo.grant.AccessTokenClientTest;
import demo.grant.TestUtils;
import demo.grants.code.AuthorizationCodeGrantAccessTokenClient;
import demo.grants.code.AuthorizationCodeGrantAccessTokenClient.UserCredential;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientApplication.class)
public class AuthorizationCodeGrantAccessTokenClientTest extends AccessTokenClientTest {
	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationCodeGrantAccessTokenClientTest.class);
	
	@Autowired
	private AuthorizationCodeResourceDetails resource;
	
	@Override
	public AuthorizationCodeGrantAccessTokenClient createAccessTokenClient() {
		return new AuthorizationCodeGrantAccessTokenClient(resource);
	}

	@Override
	public AuthorizationCodeGrantAccessTokenClient createAccessTokenClient(AuthenticationScheme authorizationScheme) {
		return new AuthorizationCodeGrantAccessTokenClient(authorizationScheme, resource);
	}

	@Test
	public void testAuthorizationCodeGrantFlow() throws Exception {
		AuthorizationCodeGrantAccessTokenClient tokenClient = createAccessTokenClient();
		OAuth2RestTemplate template = tokenClient.createOauth2RestTemplate();
		
		// Once the request is ready and approved, we can continue with the access token
		approveAccessTokenGrant(null, true, tokenClient, template);
		
		AccessTokenRequest request = template.getOAuth2ClientContext().getAccessTokenRequest();
		Assert.assertNull(request.getAuthorizationCode());

		// Finally everything is in place for the grant to happen...
		OAuth2AccessToken accessToken = template.getAccessToken();
		Assert.assertNotNull(accessToken);
		Assert.assertNotNull(request.getAuthorizationCode());

		TestUtils.testGetMe(template);
	}

	@Test
	public void testMultipleCallsThroughTokenExpire() throws Exception {
		AuthorizationCodeGrantAccessTokenClient client = createAccessTokenClient();
		OAuth2RestTemplate template = client.createOauth2RestTemplate();
		
		// Once the request is ready and approved, we can continue with the access token
		approveAccessTokenGrant(null, true, client, template);
		
		AccessTokenRequest request = template.getOAuth2ClientContext().getAccessTokenRequest();
		Assert.assertNull(request.getAuthorizationCode());

		// Finally everything is in place for the grant to happen...
		OAuth2AccessToken accessToken = template.getAccessToken();
		Assert.assertNotNull(accessToken);
		Assert.assertNotNull(request.getAuthorizationCode());
				
		int count = 0;
		OAuth2AccessToken token = null;
		boolean stop = false;
		while (!stop && count < 10) {
			TestUtils.testGetMe(template);
			count ++;
			
			OAuth2AccessToken lastToken = template.getOAuth2ClientContext().getAccessToken();
			if (count == 1) {
				 token = lastToken;
			}

			stop = ! lastToken.getValue().equals(token.getValue());
			Thread.sleep(11000); 	//11 seconds
		}
		
		Assert.assertTrue(count > 5);
		Assert.assertFalse(stop);
	}
	
	private void approveAccessTokenGrant(String currentUri, boolean approved, 
			AuthorizationCodeGrantAccessTokenClient tokenClient, OAuth2RestTemplate template) {
		UserCredential userCeddential = tokenClient.getUserCredential();
		AccessTokenRequest request = template.getOAuth2ClientContext().getAccessTokenRequest();
		request.setHeaders( createAuthenticatedHeaders(template, userCeddential) );
		
		AuthorizationCodeResourceDetails resource = (AuthorizationCodeResourceDetails)tokenClient.getResource();
		
		if (currentUri != null) {
			request.setCurrentUri(currentUri);
		}

		Assert.assertNull(request.getStateKey());
		
		UserRedirectRequiredException udrException = null;
		try {
			// First try to obtain the access token...
			OAuth2AccessToken accessToken = template.getAccessToken();
			
			Assert.assertNotNull(accessToken);
			Assert.fail("Expected UserRedirectRequiredException");
		}
		catch (UserRedirectRequiredException e) {
			// Expected and necessary, so that the correct state is set up in the request...
			udrException = e;
			LOG.debug("Expected error:", e);
		}

		String location = udrException.getRedirectUri();
		Assert.assertTrue(location.startsWith(resource.getUserAuthorizationUri()));
		Assert.assertNull(request.getAuthorizationCode());
		Assert.assertNull(request.get(OAuth2Utils.USER_OAUTH_APPROVAL));
		Assert.assertNotNull(request.getStateKey());
		Assert.assertEquals(udrException.getStateKey(), request.getStateKey());
		Assert.assertEquals(udrException.getStateToPreserve(), request.getPreservedState());
		
		verifyAuthorizationPage(template, location);
		
		UserApprovalRequiredException uarException = null;
		try {
			// Now try again and the token provider will redirect for user approval...
			OAuth2AccessToken accessToken = template.getAccessToken();
			
			Assert.assertNotNull(accessToken);
			Assert.fail("Expected UserRedirectRequiredException");
		}
		catch (UserApprovalRequiredException e) {
			// Expected and necessary, so that the user can approve the grant...
			uarException = e;
		}

		location = uarException.getApprovalUri();

		Assert.assertTrue(location.startsWith(resource.getUserAuthorizationUri()));
		Assert.assertNull(request.getAuthorizationCode());

		// The approval (will be processed on the next attempt to obtain an access token)...
		request.set(OAuth2Utils.USER_OAUTH_APPROVAL, Boolean.toString(approved));
	}

	private void verifyAuthorizationPage(OAuth2RestTemplate template, String location) {
		final AtomicReference<String> confirmationPage = new AtomicReference<String>();
		AuthorizationCodeAccessTokenProvider provider = new AuthorizationCodeAccessTokenProvider() {
			@Override
			protected ResponseExtractor<ResponseEntity<Void>> getAuthorizationResponseExtractor() {
				return new ResponseExtractor<ResponseEntity<Void>>() {
					public ResponseEntity<Void> extractData(ClientHttpResponse response) throws IOException {
						confirmationPage.set(StreamUtils.copyToString(response.getBody(), Charset.forName("UTF-8")));
						return new ResponseEntity<Void>(response.getHeaders(), response.getStatusCode());
					}
				};
			}
		};
		try {
			provider.obtainAuthorizationCode(template.getResource(), template.getOAuth2ClientContext()
					.getAccessTokenRequest());
		}
		catch (UserApprovalRequiredException e) {
			// ignore
		}
		String page = confirmationPage.get();
		verifyAuthorizationPage(page);
		
	}

	private void verifyAuthorizationPage(String page) {
		LOG.info(page);
	}

	private HttpHeaders createAuthenticatedHeaders(OAuth2RestTemplate template, UserCredential userCeddential) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		headers.set("Authorization", getBasicAuthentication(userCeddential));
		template
			.getOAuth2ClientContext()
			.getAccessTokenRequest()
			.setHeaders(headers);
		return headers;
	}

	private String getBasicAuthentication(UserCredential userCeddential) {
		String user = (userCeddential == null) ? "" : userCeddential.getUser();
		String credential = (userCeddential == null) ? "" : userCeddential.getCredential();
		return String.format("Basic %s", 
			new String(Base64.encode(String.format("%s:%s", user, credential).getBytes())));
	}
	
}
