package demo.grant;

import org.springframework.security.oauth2.common.AuthenticationScheme;

import demo.grants.AccessTokenClient;

public abstract class AccessTokenClientTest {
	
	public abstract AccessTokenClient createAccessTokenClient();
	public abstract AccessTokenClient createAccessTokenClient(AuthenticationScheme authorizationScheme);
	
}
