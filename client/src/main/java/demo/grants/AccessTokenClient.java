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
import org.springframework.security.oauth2.client.token.grant.redirect.AbstractRedirectResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.DefaultResponseErrorHandler;


public abstract class AccessTokenClient {
	public static final String AUTH_SERVER_PREFIX = "http://localhost.:9090";
	public static final String TOKEN_URL = AUTH_SERVER_PREFIX + "/oauth/token";
	public static final String AUTH_URL = AUTH_SERVER_PREFIX + "/oauth/authorize";
	
	protected abstract BaseOAuth2ProtectedResourceDetails createResource();
	
	private final BaseOAuth2ProtectedResourceDetails resource = createResource();

	public AccessTokenClient() {
		this(null);
	}

	public AccessTokenClient(AuthenticationScheme authorizationScheme) {
		setAuthenticationScheme(authorizationScheme);
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

	public OAuth2AccessToken getAccessToken() {
		OAuth2RestTemplate restTemplate = createOauth2RestTemplate();
		OAuth2AccessToken token = restTemplate.getAccessToken();
		return token;
		
	}
	
	public OAuth2RestTemplate createOauth2RestTemplate() {
		resource.setAccessTokenUri(TOKEN_URL);
		if (resource instanceof AbstractRedirectResourceDetails) {
			((AbstractRedirectResourceDetails)resource).setUserAuthorizationUri(AUTH_URL);
		}
		OAuth2RestTemplate restTemplate = createRestTemplate(new DefaultAccessTokenRequest());
//		ResourceOwnerPasswordAccessTokenProvider accessTokenProvider = new ResourceOwnerPasswordAccessTokenProvider();
//		restTemplate.setAccessTokenProvider(accessTokenProvider);
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
