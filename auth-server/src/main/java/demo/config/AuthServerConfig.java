package demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import demo.security.jwt.MyDefaultUserAuthenticationConverter;
import demo.security.service.SecurityService;


@Configuration
@EnableAuthorizationServer
@EnableWebSecurity
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(AuthServerConfig.class);
	
	
	@Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private SecurityService securityService;
	
	@Value("${security.oauth2.resource.jwt.key-store}")
    private Resource jwtSignerKeyStore;
    
	@Value("${security.oauth2.resource.jwt.key-alias}")
    private String jwtSignerKeyAlias;
	
	@Value("${security.oauth2.resource.jwt.key-store-password}")
    private String jwtSignerStorePass;
	
	@Value("${security.oauth2.resource.jwt.key-password}")
    private String jwtSignerKeyPass;
	
	@Value("${server.ssl.key-store}")
    private Resource serverSslKeyStore;
	
	@Value("${server.ssl.key-alias}")
    private String serverSslKeyAlias;
	
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory( jwtSignerKeyStore, jwtSignerStorePass.toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair(jwtSignerKeyAlias, jwtSignerKeyPass.toCharArray()));
		
		DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
		accessTokenConverter.setUserTokenConverter( new MyDefaultUserAuthenticationConverter(securityService) );
		converter.setAccessTokenConverter( accessTokenConverter);
		
		LOG.info( 
			String.format("%s, %s, %s, %s", "security.oauth2.resource.jwt.key-store={}", 
			"security.oauth2.resource.jwt.key-alias={}",  "server.ssl.key-store={}", 
			"server.ssl.key-alias={}"), jwtSignerKeyStore, jwtSignerKeyAlias, 
			serverSslKeyStore, serverSslKeyAlias );
		
		return converter;
	}
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//		security
//			.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
//			.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
//			.realm("sparklr2/client")
//			.sslOnly()
//		;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
			.inMemory()
				.withClient("my-trusted-client")
		            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
		            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
		            .scopes("read", "write", "trust")
		            .accessTokenValiditySeconds(60)
		            .refreshTokenValiditySeconds(160)
		            .and()
		        .withClient("my-client-with-registered-redirect")
		            .authorizedGrantTypes("authorization_code")
		            .authorities("ROLE_CLIENT")
		            .scopes("read", "trust")
//		            .redirectUris("https://localhost:8080/client/me", "https://localhost.:8080/client/me")
		            .redirectUris("https://localhost:8080/client/me")
		            .accessTokenValiditySeconds(3600)
		            .refreshTokenValiditySeconds(3600)
		            .and()
		        .withClient("my-client-with-secret")
		            .authorizedGrantTypes("client_credentials", "password")
		            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
		            .scopes("read", "write")
		            .secret("secret")
		            .accessTokenValiditySeconds(60)
		            .refreshTokenValiditySeconds(160)
		        ;
		
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.authenticationManager(authenticationManager)
			.accessTokenConverter(accessTokenConverter());
	}

}
