package demo.client.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ClientUtils {

	public static <T> ResponseEntity<T> getForEntity(RestTemplate template, 
		String url, Class<T> responseType, Object... urlVariables) {
		return template.getForEntity(url, responseType, urlVariables);
	}
	
	public static <T> ResponseEntity<T> getForEntity(RestTemplate template, 
		HttpHeaders headers, String url, Class<T> responseType, Object... urlVariables) {
		HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
		return template.exchange(url, HttpMethod.GET, request, responseType, urlVariables);
	}
	
}
