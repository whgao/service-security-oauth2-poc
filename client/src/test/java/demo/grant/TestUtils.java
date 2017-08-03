package demo.grant;

import java.util.Map;

import org.junit.Assert;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.web.client.RestTemplate;

import demo.client.AServiceClient;
import demo.grants.AccessTokenClient;

public class TestUtils {
	public static void testGetMe(AuthenticationScheme scheme, AccessTokenClientTest test) throws Exception {
		testGetMeInternal(null, scheme, null, test);
	}
	
	public static void testGetMe(AccessTokenClientTest test) throws Exception {
		testGetMeInternal(null, null, null, test);
	}

	
	public static void testGetMe(RestTemplate template, HttpHeaders headers) throws Exception {
		testGetMeInternal(template, null, headers, null);
	}
		
	public static void testGetMe(RestTemplate template) throws Exception {
		testGetMeInternal(template, null, null, null);
	}
	
	private static void testGetMeInternal(RestTemplate template, AuthenticationScheme scheme, HttpHeaders headers, AccessTokenClientTest test) throws Exception {
		if (template == null) {
			AccessTokenClient tokenClient = test.createAccessTokenClient(scheme);
			template = tokenClient.createOauth2RestTemplate();
		}
		
		AServiceClient client = new AServiceClient(template);
		Map<String, Object> result = (headers == null) ? client.getEntries() : client.getEntries(headers);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isEmpty());

	}
}
