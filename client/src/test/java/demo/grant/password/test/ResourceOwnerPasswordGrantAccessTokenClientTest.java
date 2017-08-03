package demo.grant.password.test;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import demo.ClientApplication;
import demo.grant.AccessTokenClientTest;
import demo.grant.TestUtils;
import demo.grants.AccessTokenClient;
import demo.grants.password.ResourceOwnerPasswordGrantAccessTokenClient;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ClientApplication.class)
public class ResourceOwnerPasswordGrantAccessTokenClientTest extends AccessTokenClientTest {
	@Autowired
	private AuthorizationCodeResourceDetails resource;
	
	@Override
	public ResourceOwnerPasswordGrantAccessTokenClient createAccessTokenClient() {
		return new ResourceOwnerPasswordGrantAccessTokenClient(resource);
	}

	@Override
	public AccessTokenClient createAccessTokenClient(AuthenticationScheme authorizationScheme) {
		return new ResourceOwnerPasswordGrantAccessTokenClient(authorizationScheme, resource);
	}
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	
	@Test
	public void testGetAccessToken() {
		AccessTokenClient tokenClient = createAccessTokenClient();
		OAuth2AccessToken token = tokenClient.getAccessToken();
		
		Assert.assertNotNull(token);
		Assert.assertNotNull(token.getValue());
		Assert.assertFalse(token.getValue().isEmpty());
		
		Assert.assertFalse(token.isExpired());
		token.getScope().contains("read");
		Assert.assertTrue(token.getAdditionalInformation().containsKey("jti"));
		Assert.assertNotNull(token.getAdditionalInformation().get("jti"));
		Assert.assertTrue(OAuth2AccessToken.BEARER_TYPE.equalsIgnoreCase(token.getTokenType()));
		
		OAuth2RefreshToken refreshToken = token.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		Assert.assertNotNull(refreshToken.getValue());
		Assert.assertFalse(refreshToken.getValue().isEmpty());
		
	}
	
	@Test
	public void testGetAccessTokenWithNoClientSecret() {
		expectAccessTokenError(thrown);
		
		AccessTokenClient tokenClient = createAccessTokenClient();
		BaseOAuth2ProtectedResourceDetails detail = tokenClient.getResource();
		detail.setClientId("my-client-with-secret");
		tokenClient.getAccessToken();
	}
	
	@Test
	public void testGetAccessTokenWithWrongClient() {
		expectAccessTokenError(thrown);
		
		AccessTokenClient tokenClient = createAccessTokenClient();
		BaseOAuth2ProtectedResourceDetails detail = tokenClient.getResource();
		detail.setClientId(detail.getClientId() + "wrong");
		tokenClient.getAccessToken();
	}
	
	
	@Test
	public void testTokenObtainedWithHeader() throws Exception {
		TestUtils.testGetMe(this);
	}
	
	@Test
	public void testTokenObtainedWithQuery() throws Exception {
		TestUtils.testGetMe(AuthenticationScheme.query, this);
	}
	
	@Test
	public void testTokenObtainedWithForm() throws Exception {
		TestUtils.testGetMe(AuthenticationScheme.form, this);
	}
	
	@Test
	public void testMultipleCallsThroughTokenExpire() throws Exception {
		AccessTokenClient client = createAccessTokenClient();
		OAuth2RestTemplate template = client.createOauth2RestTemplate();
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
		Assert.assertTrue(stop);
	}
	
	@Test
	public void testUnauthenticatedWithoutAccessToken1() throws Exception {
		expectRequireFullAuthenticationError(thrown);		
		TestUtils.testGetMe(new RestTemplate());
	}
	
	@Test
	public void testUnauthenticatedWithoutAccessToken2() throws Exception {
		expectRequireFullAuthenticationError(thrown);		
		TestUtils.testGetMe(AuthenticationScheme.none, this);
	}
	
	@Test
	public void testWrongSecretError() throws Exception {
		expectInvalidTokenError(thrown);		
		RestTemplate template = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer FOO");
		TestUtils.testGetMe(template, headers);
	}
	
	private void expectAccessTokenError(ExpectedException thrown) {
		thrown.expect(OAuth2AccessDeniedException.class);
		thrown.expectMessage("Error requesting access token.");
		thrown.expectCause(Matchers.allOf(Matchers.instanceOf(HttpClientErrorException.class), 
			Matchers.hasProperty("statusCode", Matchers.is(HttpStatus.UNAUTHORIZED)),
			Matchers.hasProperty("statusText", Matchers.is("Unauthorized"))));
	}

	private void expectRequireFullAuthenticationError(ExpectedException thrown) {
		expectHttpClientErrorException(thrown, "unauthorized", 
			"Full authentication is required to access this resource");
	}
	
	private void expectInvalidTokenError(ExpectedException thrown) {
		expectHttpClientErrorException(thrown, "invalid_token", "Cannot convert access token to JSON");
	}
	
	private void expectHttpClientErrorException(ExpectedException thrown, String error, String errorDescription) {
		thrown.expect(HttpClientErrorException.class);
		thrown.expectMessage("401 Unauthorized");
		thrown.expect(Matchers.hasProperty("statusCode", Matchers.is(HttpStatus.UNAUTHORIZED)));
		thrown.expect(Matchers.hasProperty("statusText", Matchers.is("Unauthorized")));
		thrown.expect(Matchers.hasProperty("responseBodyAsString", 
			Matchers.containsString(errorDescription)));

		thrown.expect(Matchers.hasProperty("responseHeaders", 
			IsMapContaining.hasEntry(Matchers.is("WWW-Authenticate"), 
			Matchers.hasToString(Matchers.containsString("error=\"" + error + "\""))))); 
		thrown.expect(Matchers.hasProperty("responseHeaders", 
			IsMapContaining.hasEntry(Matchers.is("WWW-Authenticate"), 
			Matchers.hasToString(Matchers.containsString(
			"error_description=\"" + errorDescription + "\"")))));
	}
	
	protected void expectAccessTokenError2(ExpectedException thrown) {
		thrown.expect(OAuth2AccessDeniedException.class);
		thrown.expectMessage("Access token denied.");
		thrown.expectCause(Matchers.allOf(Matchers.instanceOf(OAuth2Exception.class), 
			Matchers.hasProperty("message", Matchers.is("Bad credentials"))));
	}

	

	@Test
	public void testGetAccessTokenWithWrongUser() {
		expectAccessTokenError2(thrown);
		
		ResourceOwnerPasswordGrantAccessTokenClient tokenClient = createAccessTokenClient();
		ResourceOwnerPasswordResourceDetails detail = tokenClient.getResource();
		detail.setUsername(detail.getUsername() + "wrong");
		tokenClient.getAccessToken();
	}
	
	@Test
	public void testGetAccessTokenWithWrongPassword() {
		expectAccessTokenError2(thrown);
		
		ResourceOwnerPasswordGrantAccessTokenClient tokenClient = createAccessTokenClient();
		ResourceOwnerPasswordResourceDetails detail = tokenClient.getResource();
		detail.setPassword(detail.getPassword() + "wrong");
		tokenClient.getAccessToken();
	}


}
