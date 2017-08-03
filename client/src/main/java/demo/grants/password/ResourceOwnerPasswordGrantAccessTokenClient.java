package demo.grants.password;

import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import demo.grants.AccessTokenClient;


public class ResourceOwnerPasswordGrantAccessTokenClient extends AccessTokenClient {
	public ResourceOwnerPasswordGrantAccessTokenClient(AuthorizationCodeResourceDetails aServiceResourceDetails) {
		this(null, aServiceResourceDetails);
	}

	public ResourceOwnerPasswordGrantAccessTokenClient(AuthenticationScheme authorizationScheme
		,AuthorizationCodeResourceDetails aServiceResourceDetails) {
		super(authorizationScheme, aServiceResourceDetails);
	}

	@Override
	protected BaseOAuth2ProtectedResourceDetails createResource() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		populateDefaultResource(resource);
		resource.setUsername("user1");
		resource.setPassword("testpass");
		return resource;
	}
	
	@Override
	public ResourceOwnerPasswordResourceDetails getResource() {
		return (ResourceOwnerPasswordResourceDetails)super.getResource();
	}
	
}
