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
	
	public static <T> ResponseEntity<T> postForEntity(RestTemplate template, 
		HttpHeaders headers, String url, Object request, Class<T> responseType, 
		Object... urlVariables) {
		HttpEntity<Object> request2 = new HttpEntity<Object>(request, headers);
		return template.exchange(url, HttpMethod.POST, request2, responseType, urlVariables);
	}
	
	public static <T> ResponseEntity<T> postForEntity(RestTemplate template, 
		String url, Object request, Class<T> responseType, Object... urlVariables) {
		return template.postForEntity(url, request, responseType, urlVariables);
	}
	
	public static <T> ResponseEntity<T> putForEntity(RestTemplate template, 
		HttpHeaders headers, String url, Object request, Class<T> responseType, 
		Object... urlVariables) {
		HttpEntity<Object> request2 = new HttpEntity<Object>(request, headers);
		return template.exchange(url, HttpMethod.PUT, request2, responseType, urlVariables);
	}
	
	public static <T> ResponseEntity<T> putForEntity(RestTemplate template, 
		String url, Object request, Class<T> responseType, Object... urlVariables) {
		return template.postForEntity(url, request, responseType, urlVariables);
	}
	
	public static <T> ResponseEntity<T> deleteForEntity(RestTemplate template, 
		HttpHeaders headers, String url, Class<T> responseType, Object... urlVariables) {
		HttpEntity<Void> request = new HttpEntity<Void>(null, headers);
		return template.exchange(url, HttpMethod.DELETE, request, responseType, urlVariables);
	}
}
