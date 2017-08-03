package demo.security.jwt;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import demo.security.models.Role;
import demo.security.service.SecurityService;

public class MyDefaultUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
	private static final Logger LOG = LoggerFactory.getLogger(MyDefaultUserAuthenticationConverter.class);
	
	private final SecurityService securityService;
	
	
	public MyDefaultUserAuthenticationConverter(SecurityService securityService) {
		super();
		this.securityService = securityService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map<String, Object> response = (Map<String, Object>)super.convertUserAuthentication(authentication);
		
		if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
			Set<String> existing = (Set<String>)response.get(AUTHORITIES);
			
			assert( (existing != null) && !existing.isEmpty() );
			
			String[] groups = existing.toArray(new String[ existing.size()]);
			existing.clear();
			
			Role[] roles = securityService.getRolesByGroups(groups);
			String[] rights = securityService.getRights(roles);
			existing.addAll(Arrays.asList(rights));
			
			String[] roleIds = securityService.getRoleIds(roles);
			existing.addAll(Arrays.asList(roleIds));

			LOG.info("Final autorities after enrichment: {}", existing);
		}
		
		return response;
	}

}
