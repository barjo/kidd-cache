package fr.liglab.adele.kiddcache.implementation;

import static java.lang.System.currentTimeMillis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.liglab.adele.kiddcache.CacheService;
import fr.liglab.adele.kiddcache.ExpirationDate;

public class CacheServiceHashTable implements CacheService {

	private static HashMap<Object, CachedObject> cache = new HashMap<Object, CachedObject>();

	@Override
	public void put(Object key, Object value) {
		this.put(key, value, null, PutPolicy.ALWAYS);
	}

	@Override
	public void put(Object key, Object value, ExpirationDate expiration) {
		this.put(key, value, expiration, PutPolicy.ALWAYS);
	}

	@Override
	public boolean put(Object key, Object value, ExpirationDate expiration,
			PutPolicy policy) {

		boolean created = false;

		switch (policy) {
		case ALWAYS:
			cache.put(key, new CachedObject(value, expiration));
			created = true;
			break;
		case ONLY_IF_NOT_PRESENT:
			if (!cache.containsKey(key)) {
				cache.put(key, new CachedObject(value, expiration));
				created = true;
			}
			break;
		case UPDATE_ONLY_IF_CACHED:
			if (cache.containsKey(key)) {
				cache.put(key, new CachedObject(value, expiration));
				created = true;
			}
			break;
		}
		return created;
	}

	@Override
	public void putAll(Map<?, ?> values) {
		this.putAll(values, null, PutPolicy.ALWAYS);
	}

	@Override
	public void putAll(Map<?, ?> values, ExpirationDate expiration) {
		this.putAll(values, expiration, PutPolicy.ALWAYS);
	}

	@Override
	public <T> Set<T> putAll(Map<T, ?> values, ExpirationDate expiration,
			PutPolicy policy) {
		HashSet<T> addedKeys = new HashSet<T>();
		switch (policy) {
		case ALWAYS:
			for (T key : values.keySet()) {
				cache.put(key, new CachedObject(values.get(key), expiration));
				addedKeys.add( key);
			}
			break;
		case ONLY_IF_NOT_PRESENT:
			for (T key : values.keySet()) {
				if (!cache.containsKey(key)) {
					cache.put(key,
							new CachedObject(values.get(key), expiration));
					addedKeys.add((T) key);
				}
			}
			break;
		case UPDATE_ONLY_IF_CACHED:
			for (T key : values.keySet()) {
				if (cache.containsKey(key)) {
					cache.put(key,
							new CachedObject(values.get(key), expiration));
					addedKeys.add(key);
				}
			}
			break;
		}
		return addedKeys;
	}

	@Override
	public Object get(Object key) {
		Object returnObject = null;
		CachedObject cachedObject = cache.get(key);
		if (cachedObject != null) {
			if (cachedObject.getExpirationDate()!=null && cachedObject.getExpirationDate().getDateInSeconds() < currentTimeMillis()) {
				cache.remove(key);
			} else {
				returnObject = cachedObject.getObject();
			}
		}
		return returnObject;
	}

	@Override
	public <T> Map<T, Object> getAll(Collection<T> keys) {
		HashMap<T, Object> returnMap = new HashMap<T, Object>();
		for (T key : keys) {
			if (cache.containsKey(key)) {
				returnMap.put(key, cache.get(key).getObject());
			}
		}
		return returnMap;
	}

	@Override
	public boolean contains(Object key) {
		boolean answer = false;
		if (cache.containsKey(key))
			answer = true;
		return answer;
	}

	@Override
	public boolean delete(Object key) {
		boolean answer = false;
		if (cache.remove(key) != null)
			answer = true;
		return answer;
	}

	@Override
	public <T> Set<T> deleteAll(Collection<T> keys) {
		HashSet<T> returnSet = new HashSet<T>();
		for (T key : keys) {
			if (cache.remove(key) != null) {
				returnSet.add(key);
			}
		}
		return returnSet;
	}
}