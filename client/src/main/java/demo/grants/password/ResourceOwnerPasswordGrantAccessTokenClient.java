package demo.grants.password;

import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import demo.grants.AccessTokenClient;


public class ResourceOwnerPasswordGrantAccessTokenClient extends AccessTokenClient {
	public ResourceOwnerPasswordGrantAccessTokenClient() {
		this(null);
	}

	public ResourceOwnerPasswordGrantAccessTokenClient(AuthenticationScheme authorizationScheme) {
		super(authorizationScheme);
	}

	@Override
	protected BaseOAuth2ProtectedResourceDetails createResource() {
		ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
		populateDefaultResource(resource);
		resource.setUsername("user");
		resource.setPassword("testpass");
		return resource;
	}
	
	@Override
	public ResourceOwnerPasswordResourceDetails getResource() {
		return (ResourceOwnerPasswordResourceDetails)super.getResource();
	}
	
}
