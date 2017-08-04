package demo.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
	
	@Bean
    public TokenStore tokenStore() throws IOException {
        return new JwtTokenStore(tokenEnhancer());
    }
	
	@Bean
    protected JwtAccessTokenConverter tokenEnhancer() throws IOException {
        JwtAccessTokenConverter converter =  new JwtAccessTokenConverter();
        converter.setVerifierKey(new String(FileCopyUtils.copyToByteArray(
        	new ClassPathResource("jwt-oauth-rsa.pub").getInputStream())));
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
