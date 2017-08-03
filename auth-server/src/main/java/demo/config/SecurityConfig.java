package demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import demo.security.models.User;
import demo.security.service.RoleStore;
import demo.security.service.SecurityService;
import demo.security.service.UserStore;
import demo.security.service.impls.InMemoryRoleStore;
import demo.security.service.impls.InMemoryUserStore;
import demo.security.service.impls.SecurityServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
            throws Exception {
        return super.authenticationManagerBean();
    }

    
	@Bean
	public RoleStore roleStore() {
		return new InMemoryRoleStore();
	}
	
	@Bean
	public UserStore userStore() {
		return new InMemoryUserStore();
	}

	@Bean
	public SecurityService securityService() {
		return new SecurityServiceImpl(roleStore(), userStore());
	}
	
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    	configure(auth, securityService());
    	

    }

    private void configure(AuthenticationManagerBuilder auth, SecurityService securityService2) throws Exception {
    	InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
    	User[] users = securityService2.getUsers();
    	for (int idx = 0; idx < users.length; idx ++) {
    		User user = users[idx];
    		configurer.withUser(user.getId())
    			.password( user.getPassword() )
    			.authorities( user.getGroups() )
    			.and();
    	}
    	
//      auth.inMemoryAuthentication()
//    	.withUser("user")
//    		.password("testpass")
//    		.roles("USER")
//    		.and()
//    	.withUser("admin")
//    		.password("testpass")
//    		.roles("USER", "ADMIN")
//    ;

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
