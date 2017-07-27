package demo.grants.code;

import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import demo.grants.AccessTokenClient;

public class AuthorizationCodeGrantAccessTokenClient extends AccessTokenClient {
	private UserCredential userCredential;
	
	public AuthorizationCodeGrantAccessTokenClient() {
		super();
		setDefaultUserCredential();
	}

	public AuthorizationCodeGrantAccessTokenClient(AuthenticationScheme authorizationScheme) {
		super(authorizationScheme);
		setDefaultUserCredential();
	}

	@Override
	protected AuthorizationCodeResourceDetails createResource() {
		AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
		populateDefaultResource(resource);
		resource.setClientId("my-client-with-registered-redirect");
		resource.setPreEstablishedRedirectUri("http://anywhere?key=value");
		return resource;
	}

	public void setDefaultUserCredential() {
		setUserCredential(new UserCredential("user", "testpass"));
	}
	
	public UserCredential getUserCredential() {
		return userCredential;
	}

	public void setUserCredential(UserCredential userCredential) {
		this.userCredential = userCredential;
	}


	public static class UserCredential {
		private final String user;
		private final String credential;
		
		
		public UserCredential(String user, String credential) {
			super();
			this.user = user;
			this.credential = credential;
		}


		public String getUser() {
			return user;
		}


		public String getCredential() {
			return credential;
		}
		
		
	}
}
