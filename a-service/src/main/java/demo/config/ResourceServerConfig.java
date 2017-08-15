package demo.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceServerConfig.class);
	
	
	@Value("${security.oauth2.resource2.jwt.key-file}")
    private Resource jwtVerifierKeyFile;
    
	@Value("${server.ssl.key-store}")
    private Resource serverSslKeyStore;
	
	@Value("${server.ssl.key-alias}")
    private String serverSslKeyAlias;
	
	
	@Bean
    public TokenStore tokenStore() throws IOException {
        return new JwtTokenStore(tokenEnhancer());
    }
	
	@Bean
    protected JwtAccessTokenConverter tokenEnhancer() throws IOException {
        JwtAccessTokenConverter converter =  new JwtAccessTokenConverter();
        converter.setVerifierKey(new String(FileCopyUtils.copyToByteArray(
        	jwtVerifierKeyFile.getInputStream())));

        LOG.info("security.oauth2.resource2.jwt.key-file={}, server.ssl.key-store={}, server.ssl.key-alias={}",
        	jwtVerifierKeyFile, serverSslKeyStore, serverSslKeyAlias);
        return converter;
    }
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
        	.resourceId("a-service")
        	.tokenStore(tokenStore());
        
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
			.requestMatchers()
				.antMatchers("/**")
				.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "/api/**")
					.permitAll()
		        .antMatchers(HttpMethod.GET, "/api/**")
		        	.access("#oauth2.hasScope('read')")
		        .antMatchers(HttpMethod.PATCH, "/api/**")
		        	.access("#oauth2.hasScope('write')")
		        .antMatchers(HttpMethod.POST, "/api/**")
		        	.access("hasAuthority('RIGHT_ADMIN')")
		        .antMatchers(HttpMethod.PUT, "/api/**")
		        	.access("hasAnyAuthority('RIGHT_DEV', 'RIGHT_ADMIN')")
		        .antMatchers(HttpMethod.DELETE, "/api/**")
		        	.access("hasAuthority('RIGHT_ADMIN')")
		        .antMatchers("/admin/**")
		        	.access("hasAuthority('ROLE_ADMIN')");
	}

	
}
