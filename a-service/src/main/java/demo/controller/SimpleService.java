package demo.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces="application/json; charset=UTF-8")
public class SimpleService {
	
	private final Map<String,String> map = new HashMap<>();
	
	@RequestMapping(value = "/api/map/keys", method = RequestMethod.GET)
    public Collection<String> getKeys() {
        return map.keySet();
    }
	
	@RequestMapping(value = "/api/map/entries", method = RequestMethod.GET)
    public Map<String,String> getEntries() {
        return map;
    }
	
	@RequestMapping(value = "/api/map/key/{key}", method = RequestMethod.GET)
    public String getKey(@PathVariable("key") String key) {
		key = toKey(key);
		return map.containsKey(key) ? key : "";
    }
	
	@RequestMapping(value = "/api/map/value/{key}", method = RequestMethod.GET)
    public String getValue(@PathVariable("key") String key) {
		String value = map.get(toKey(key));
		return value == null ? value : "";
    }

	@RequestMapping(value = "/api/map/entry", method = RequestMethod.POST)
    public boolean addEntry(@RequestBody() String key, String value) {
		key = toKey(key);
		boolean canDo = (key != null) && ! key.isEmpty() &&
			value != null && !value.isEmpty() && ! map.containsKey(key);
		
		if (canDo) {
			map.put(key,  value);
		}
		return canDo;
    }

	@RequestMapping(value = "/api/map/entry", method = RequestMethod.PUT)
    public boolean updateEntry(@RequestBody() String key, String value) {
		key = toKey(key);
		boolean canDo = (key != null) && ! key.isEmpty() &&
			value != null && !value.isEmpty() && map.containsKey(key);
		if (canDo) {
			map.put(key,  value);
		}
		return canDo;
    }

	@RequestMapping(value = "/api/map/key/{key}", method = RequestMethod.DELETE)
    public boolean deleteEntry(@PathVariable("key") String key) {
		boolean canDo = map.containsKey(key);
		if (canDo) {
			map.remove(key);
		}
		return canDo;
    }
	
	private String toKey(String key) {
		return (key == null) ? "" : key.toLowerCase();
	}


}
