package demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@SpringBootApplication
@EnableOAuth2Client
public class ClientApplication {
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

	public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
	
//	@Bean
//	public ConsumerTokenServices tokenServices() {
//	    DefaultTokenServices tokenServices = new DefaultTokenServices();
//	    return tokenServices;
//	}
	
	@Bean
    public OAuth2RestTemplate restTemplate(OAuth2ClientContext oauth2ClientContext) {
        return new OAuth2RestTemplate(aService(), oauth2ClientContext);
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
}
