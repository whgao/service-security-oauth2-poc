package demo;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@SpringBootApplication
@EnableOAuth2Client
public class ClientApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ClientApplication.class);
	
	
	@Value("${config.oauth2.a-service.clientID}")
    private String clientID;

	@Value("${config.oauth2.a-service.clientSecret}")
    private String clientSecret;

	@Value("${config.oauth2.a-service.accessTokenUri}")
    private String accessTokenUri;

	@Value("${config.oauth2.a-service.userAuthorizationUri}")
    private String userAuthorizationUri;

	@Value("${config.oauth2.a-service.scopes}")
    private List<String> scopes;
	
	@Value("${config.oauth2.a-service.resourceUri}")
    private String resourceURI;

	@Value("${config.oauth2.a-service.redirectUri}")
    private String redirectURI;

	@Value("${config.oauth2.a-service.logoutUri}")
    private String logoutURI;

	@Value("${server.ssl.trust-store}")
    private Resource trustStore;
    
	@Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;
    
	@Value("${server.ssl.key-store}")
    private Resource serverSslKeyStore;
	
	@Value("${server.ssl.key-alias}")
    private String serverSslKeyAlias;
	
	
	public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
	
	
	@Bean
    public OAuth2RestTemplate restTemplate(OAuth2ClientContext oauth2ClientContext) 
    	throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		OAuth2RestTemplate template = new OAuth2RestTemplate(aService(), oauth2ClientContext);
		template.setAccessTokenProvider(accessTokenProviderChain());
		template.setRequestFactory(httpRequestFactory());
        return template;
    }

	@Bean
    @SuppressWarnings("unchecked")
	public AccessTokenProvider accessTokenProviderChain() 
		throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		List<OAuth2AccessTokenSupport> providers = Arrays.<OAuth2AccessTokenSupport> asList(
			new AuthorizationCodeAccessTokenProvider(), new ImplicitAccessTokenProvider(),
			new ResourceOwnerPasswordAccessTokenProvider(), new ClientCredentialsAccessTokenProvider());
		for (OAuth2AccessTokenSupport each : providers) {
			each.setRequestFactory(httpRequestFactory());
		}
		return new AccessTokenProviderChain((List<? extends AccessTokenProvider>) providers);
	}
	
	@Bean
	public ClientHttpRequestFactory httpRequestFactory() 
		throws KeyManagementException, NoSuchAlgorithmException, 
		KeyStoreException, CertificateException, IOException {
		SSLContext sslContext = SSLContexts.custom()
			.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
		
		LOG.info("server.ssl.trust-store={}, server.ssl.key-store={}, server.ssl.key-alias={}",
			trustStore, serverSslKeyStore, serverSslKeyAlias);
		
		CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}
	
	@Bean
	public OauthSslClientContext oauthSslClientContext() 
		throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, 
		CertificateException, IOException {
		return new OauthSslClientContext(accessTokenProviderChain(), httpRequestFactory());
	}
	
	@Bean
    public AuthorizationCodeResourceDetails aService() {
        AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
        resource.setClientId(clientID);
        resource.setClientSecret(clientSecret);
        resource.setAccessTokenUri(accessTokenUri);
        resource.setUserAuthorizationUri(userAuthorizationUri);
        resource.setScope(scopes);

        return resource;
    }
	
	@Bean 
	Config config() {
		Config config = Config.get();
		config.setResourceUri(resourceURI);
		config.setRedirectUri(redirectURI);
		config.setLogoutUri(logoutURI);
		return config;
	}

	public static class Config {
		static Config instance = new Config();
		
		private String resourceUri;
		private String redirectUri;
		private String logoutUri;
		
		private Config() {
		}
		
		public static Config get() {
			return instance;
		}

		public String getResourceUri() {
			return resourceUri;
		}

		public void setResourceUri(String resourceUri) {
			this.resourceUri = resourceUri;
		}

		public String getRedirectUri() {
			return redirectUri;
		}

		public void setRedirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
		}

		public String getLogoutUri() {
			return logoutUri;
		}

		public void setLogoutUri(String logoutUri) {
			this.logoutUri = logoutUri;
		}
		
		
	}
	
	public static class OauthSslClientContext {
		private final AccessTokenProvider accessTokenProvider;
		private final ClientHttpRequestFactory clientHttpRequestFactory;
		
		
		public OauthSslClientContext(AccessTokenProvider accessTokenProvider,
				ClientHttpRequestFactory clientHttpRequestFactory) {
			super();
			this.accessTokenProvider = accessTokenProvider;
			this.clientHttpRequestFactory = clientHttpRequestFactory;
		}


		public AccessTokenProvider getAccessTokenProvider() {
			return accessTokenProvider;
		}


		public ClientHttpRequestFactory getClientHttpRequestFactory() {
			return clientHttpRequestFactory;
		}
		
	}
}
