package demo.grants;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.redirect.AbstractRedirectResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import demo.ClientApplication.OauthSslClientContext;


public abstract class AccessTokenClient {
	private AuthorizationCodeResourceDetails aServiceResourceDetails;
	
	protected abstract BaseOAuth2ProtectedResourceDetails createResource();
	
	private final BaseOAuth2ProtectedResourceDetails resource = createResource();

	public AccessTokenClient(AuthorizationCodeResourceDetails aServiceResourceDetails) {
		this(null, aServiceResourceDetails);
	}

	public AccessTokenClient(AuthenticationScheme authorizationScheme, 
		AuthorizationCodeResourceDetails aServiceResourceDetails) {
		setAuthenticationScheme(authorizationScheme);
		this.aServiceResourceDetails = aServiceResourceDetails;
	}

	private void setAuthenticationScheme(AuthenticationScheme authorizationScheme) {
		if (authorizationScheme != null) {
			resource.setAuthenticationScheme(authorizationScheme);
		}
	}

	protected void populateDefaultResource(BaseOAuth2ProtectedResourceDetails resource) {
		resource.setClientId("my-trusted-client");
		resource.setScope(Arrays.asList("read"));
		resource.setId(resource.getClientId());
	}
	
	
	public BaseOAuth2ProtectedResourceDetails getResource() {
		return resource;
	}

	public OAuth2AccessToken getAccessToken(OauthSslClientContext context) {
		OAuth2RestTemplate restTemplate = createOauth2RestTemplate(context);
		OAuth2AccessToken token = restTemplate.getAccessToken();
		return token;
		
	}
	
	public OAuth2RestTemplate createOauth2RestTemplate(OauthSslClientContext context) {
//		resource.setAccessTokenUri(TOKEN_URL);
		resource.setAccessTokenUri(aServiceResourceDetails.getAccessTokenUri());
		if (resource instanceof AbstractRedirectResourceDetails) {
//			((AbstractRedirectResourceDetails)resource).setUserAuthorizationUri(AUTH_URL);
			((AbstractRedirectResourceDetails)resource).setUserAuthorizationUri(aServiceResourceDetails.getUserAuthorizationUri());
		}
		OAuth2RestTemplate restTemplate = createRestTemplate(new DefaultAccessTokenRequest());
//		ResourceOwnerPasswordAccessTokenProvider accessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();
//		restTemplate.setAccessTokenProvider(accessTokenProvider);
		
		restTemplate.setAccessTokenProvider(context.getAccessTokenProvider());
		restTemplate.setRequestFactory(context.getClientHttpRequestFactory());
		return restTemplate;
	}
	
	public static RestTemplate createRestTemplate(OauthSslClientContext context) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(context.getClientHttpRequestFactory());
		return restTemplate;
	}
	
	private OAuth2RestTemplate createRestTemplate(AccessTokenRequest request) {
		OAuth2ClientContext context = new DefaultOAuth2ClientContext(request);
		OAuth2RestTemplate client = new OAuth2RestTemplate(resource, context);
//		setupConnectionFactory(client);
		client.setErrorHandler(new DefaultResponseErrorHandler() {
			// Pass errors through in response entity for status code analysis
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
//		if (accessTokenProvider != null) {
//			client.setAccessTokenProvider(accessTokenProvider);
//		}
		return client;
	}
	
}
