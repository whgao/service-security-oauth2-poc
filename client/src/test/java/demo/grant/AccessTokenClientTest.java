package demo.grant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import demo.ClientApplication.OauthSslClientContext;
import demo.grants.AccessTokenClient;

public abstract class AccessTokenClientTest {
	@Autowired
	private OauthSslClientContext oauthSslClientContext;
	
	
	public abstract AccessTokenClient createAccessTokenClient();
	public abstract AccessTokenClient createAccessTokenClient(AuthenticationScheme authorizationScheme);
	
	protected OauthSslClientContext getOauthSslClientContext() {
		return oauthSslClientContext;
	}
}
