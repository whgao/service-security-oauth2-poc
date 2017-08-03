package demo.security.service.impls;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInMemoryStore<K,V> {
	private final Map<K, V> storeByKeys = buildStoreAsMap();

	abstract protected K getKey(V value);
	abstract protected K toKey(Object key);
	abstract protected V[] buildStore();
	
	protected Map<K, V> buildStoreAsMap() {
		V[] values = buildStore();
		Map<K, V> map = new HashMap<>();
		for (int idx = 0; idx < values.length; idx ++) {
			V value = values[idx];
			map.put( getKey(value), value);
		}
		return Collections.synchronizedMap(map);
	}
	
	public boolean addValue(V value) {
		K key = getKey(value);
		V existingValue = getValue(key);
		if (existingValue == null) {
			storeByKeys.put(key, value);
		}
		return existingValue == null;
	}

	

	public void updateValue(V value) {
		K key = getKey(value);
		V existingValue = getValue(key);
		if (existingValue != null) {
			storeByKeys.put(key, value);
		}
	}

	public V getValue(Object key) {
		return storeByKeys.get( toKey(key) );
	}

	public boolean removeValue(Object key) {
		V Value = storeByKeys.remove(toKey(key));
		return Value != null;
	}

	protected String toLowerCase(String in) {
		return in.toLowerCase();
	}
	
	public Collection<V> getValues() {
		return storeByKeys.values();
	}
}
