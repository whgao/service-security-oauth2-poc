package demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.client.AServiceClient;

@RestController
public class ClientController {

	@Autowired
    private OAuth2RestTemplate restTemplate;
	
	@RequestMapping("/me")
    public Map<String, Object> home() throws Exception {
		AServiceClient client = new AServiceClient(restTemplate);
		Map<String, Object> result = new HashMap<>();
		populateResult(client, result);
		return result;
    }
	
	@RequestMapping("/entry/{key}/{value}")
    public Map<String, Object> addOrUpdateEntry(Authentication authentication, @PathVariable("key") String key, 
    	@PathVariable("value")String value,
    	@RequestParam("action")String action) throws Exception {
		
		AServiceClient client = new AServiceClient(restTemplate);
		Map<String, Object> result = new HashMap<>();

		try {
			if ("A".equalsIgnoreCase(action)) {
				client.addEntry(key, value);
			} else {
				client.updateEntry(key, value);
			}
		} catch (InsufficientScopeException e) {
			result.put("addOrUpdateEntry", e);
		}

		populateResult(client, result);
		return result;
    }
	
    @SuppressWarnings("unchecked")
	private void populateResult(AServiceClient client, Map<String, Object> result) throws Exception {
		Map<String, ?> meResult = client.getMe();
		
		Map<String,Object> auth = (Map<String,Object>)meResult.get("authentication");
		result.put("name", auth.get("name") );
		result.put("authorities", auth.get("authorities"));
		
		Map<String, ?> entriesResult = client.getEntries();
		result.put("entries", entriesResult);		
    }

}
