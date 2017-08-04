package demo.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces="application/json; charset=UTF-8")
public class SimpleService {
	
	private final Map<String,SecureValue> map = buildMap();
	
	
	@RequestMapping(value = "/api/map/keys", method = RequestMethod.GET)
    public Collection<String> getKeys(Authentication authentication) {
        return filterKeys(authentication);
    }
	

	@RequestMapping(value = "/api/map/entries", method = RequestMethod.GET)
    public Map<String,String> getEntries(Authentication authentication) {
        return filter(authentication);
    }
	
	

	@RequestMapping(value = "/api/map/key/{key}", method = RequestMethod.GET)
    public String getKey(@PathVariable("key") String key, Authentication authentication) {
		String[] roles = toRoles(authentication);
		key = toKey(key);
		return map.containsKey(key) && map.get(key).isVisible(roles) ? key : "";
    }
	
	@RequestMapping(value = "/api/map/value/{key}", method = RequestMethod.GET)
    public String getValue(@PathVariable("key") String key, Authentication authentication) {
		String[] roles = toRoles(authentication);
		SecureValue value = map.get(toKey(key));
		return ((value != null) && value.isVisible(roles)) ? value.getValue() : "";
    }

	@RequestMapping(value = "/api/map/entry/{key}", method = RequestMethod.POST)
    public boolean addEntry(@PathVariable("key") String key, @RequestBody() String value, 
    	Authentication authentication) {
		return addOrUpdateEntry(key, value, authentication, true);
    }

	@RequestMapping(value = "/api/map/entry/{key}", method = RequestMethod.PUT)
    public boolean updateEntry(@PathVariable("key") String key, @RequestBody() String value,
    	Authentication authentication) {
		return addOrUpdateEntry(key, value, authentication, false);
    }

	@RequestMapping(value = "/api/map/key/{key}", method = RequestMethod.DELETE)
    public boolean deleteEntry(@PathVariable("key") String key) {
		boolean canDo = map.containsKey(key);
		if (canDo) {
			map.remove(key);
		}
		return canDo;
    }

	private boolean addOrUpdateEntry(String key, String value, Authentication authentication, boolean isAdd) {
		String[] roles = toRoles(authentication);
		key = toKey(key);
		SecureValue value2 = map.get(key);
		boolean canDo = isAdd && (value2 == null) || !isAdd && (value2 != null) && value2.isVisible(roles);
		
		if (canDo) {
			map.put(key,  isAdd ? new SecureValue(value) : value2.newValue(value));
		}
		return canDo;
    }
	
	private String toKey(String key) {
		return (key == null) ? "" : key.toLowerCase();
	}


	private Map<String, SecureValue> buildMap() {
		Map<String, SecureValue> result = new HashMap<>();
		result.put("any1", new SecureValue("any-value1") );
		result.put("any2", new SecureValue("any-value2") );
		
		result.put("dev1", new SecureValue("dev-Value1", 
			new String[] { "ROLE_EDITOR", "ROLE_EDITOR_OTHER1", "ROLE_ADMIN" } ) );
		result.put("dev2", new SecureValue("dev-value2", 
				new String[] { "ROLE_EDITOR", "ROLE_EDITOR_OTHER2", "ROLE_ADMIN" } ) );
		
		result.put("super1", new SecureValue("super-value1", 
				new String[] { "ROLE_ADMIN", "ROLE_ADMIN_OTHER1" } ) );
		result.put("super2", new SecureValue("super-value2", 
				new String[] { "ROLE_ADMIN", "ROLE_ADMIN_OTHER2" } ) );
		
		return result;
	}

	private Map<String, String> filter(Authentication authentication) {
		String[] roles = toRoles(authentication);
		Map<String, String> result = new HashMap<>();
		
		for (Entry<String, SecureValue> each : map.entrySet()) {
			SecureValue value = each.getValue();
			if (value.isVisible(roles)) {
				result.put(each.getKey(), value.getValue());
			}
		}
		return result;
	}

	private Collection<String> filterKeys(Authentication authentication) {
		String[] roles = toRoles(authentication);
		Set<String> result = new HashSet<>();
		
		for (Entry<String, SecureValue> each : map.entrySet()) {
			SecureValue value = each.getValue();
			if (value.isVisible(roles)) {
				result.add(each.getKey());
			}
		}
		return result;
	}

	private String[] toRoles(Authentication authentication) {
		Set<String> result = new HashSet<>();
		for (GrantedAuthority each : authentication.getAuthorities()) {
			result.add(each.getAuthority());
		}
		return result.toArray(new String[result.size()]);
	}


	public static class SecureValue {
		private String value;
		private Set<String> roles = new HashSet<>();
		
		
		public SecureValue(String value, String... roles) {
			super();
			this.value = value;
			this.roles.addAll(Arrays.asList(roles));
		}


		public String getValue() {
			return value;
		}


		public void setValue(String value) {
			this.value = value;
		}
		
		public boolean isVisible(String[] roles) {
			if (this.roles.isEmpty()) {
				return true;
			}
			
			for (int idx = 0; idx < roles.length; idx ++) {
				if (this.roles.contains(roles[idx])) {
					return true;
				}
			}
			
			return false;
		}
		
		public SecureValue newValue(String value) {
			return new SecureValue(value, roles.toArray(new String[roles.size()]));
		}
	}
}
