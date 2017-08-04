package demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import demo.ClientApplication.Config;
import demo.client.AServiceClient;

@RestController
public class ClientController {
	private static final Logger LOG = LoggerFactory.getLogger(ClientController.class);
	
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
    public Map<String, Object> addOrUpdateEntry(@PathVariable("key") String key, 
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
		} catch (InsufficientScopeException | UserDeniedAuthorizationException e) {
			result.put("addOrUpdateEntry", e);
		}

		populateResult(client, result);
		return result;
    }
	
	@RequestMapping("/logout")
    public ModelAndView logout() {
		restTemplate.getOAuth2ClientContext().setAccessToken(null);

		String logoutUri = Config.get().getLogoutUri();
		LOG.info("Redirecting to {}", logoutUri);
		return new ModelAndView("redirect:" + logoutUri);
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
