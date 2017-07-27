package demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
        	.withUser("user")
        		.password("testpass")
        		.roles("USER")
        		.and()
        	.withUser("admin")
	    		.password("testpass")
	    		.roles("USER", "ADMIN")
	    ;

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
            throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
	public void configure(HttpSecurity http) throws Exception {
    	super.configure(http);
    	
		// @formatter:off	
		http
//			.anonymous().disable()
			.csrf().disable()
//			.authorizeRequests()
//				.antMatchers("/**").fullyAuthenticated()
//				.and()
//            .sessionManagement()
//            	.sessionCreationPolicy(SessionCreationPolicy.NEVER);
		;
		// @formatter:on
	}
}
