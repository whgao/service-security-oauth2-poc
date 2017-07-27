package demo.client;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import demo.client.utils.ClientUtils;


public class AServiceClient {
	
	public static final String A_SERVER_URL_PREFIX = "http://localhost.:9091";

	public static final String ME_URI = A_SERVER_URL_PREFIX + "/api/me";

	
	private final RestTemplate template;
	
	public AServiceClient(RestTemplate template) {
		this.template = template;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMe() throws Exception {
		return ClientUtils.getForEntity(template, ME_URI, Map.class).getBody();
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object> getMe(HttpHeaders headers) throws Exception {
		return ClientUtils.getForEntity(template, headers, ME_URI, Map.class).getBody();
	}

	
}
