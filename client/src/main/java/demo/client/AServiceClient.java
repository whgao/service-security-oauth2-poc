package demo.client;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import demo.ClientApplication.Config;
import demo.client.utils.ClientUtils;

public class AServiceClient {
	
    private String resourceURI = Config.get().getResourceUri();
	private String meUri = resourceURI + "/api/me";
	private String entriesUri = resourceURI + "/api/map/entries";
	private String entryUri = resourceURI + "/api/map/entry";
	
	private RestTemplate template;
	
	public AServiceClient(RestTemplate template) {
		this.template = template;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getMe() throws Exception {
		return ClientUtils.getForEntity(template, meUri, Map.class).getBody();
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object> getMe(HttpHeaders headers) throws Exception {
		return ClientUtils.getForEntity(template, headers, meUri, Map.class).getBody();
	}

	@SuppressWarnings("unchecked")
	public Map<String,Object> getEntries() throws Exception {
		return ClientUtils.getForEntity(template, entriesUri, Map.class).getBody();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getEntries(HttpHeaders headers) throws Exception {
		return ClientUtils.getForEntity(template, headers, entriesUri, Map.class).getBody();
	}
	
	public boolean addEntry(String key, String value, HttpHeaders headers) throws Exception {
		return ClientUtils.postForEntity(template, headers, entryUri(key), value, Boolean.class).getBody();
	}

	public void updateEntry(String key, String value) throws Exception {
		ClientUtils.putForEntity(template, entryUri(key), value);
	}

	public boolean addEntry(String key, String value) throws Exception {
		return ClientUtils.postForEntity(template, entryUri(key), value, Boolean.class).getBody();
	}
	
	private String entryUri(String key) {
		return String.format("%s/%s", entryUri, key);
	}
}
