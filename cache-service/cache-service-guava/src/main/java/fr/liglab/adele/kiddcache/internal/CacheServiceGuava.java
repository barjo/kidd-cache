package fr.liglab.adele.kiddcache.internal;

import static fr.liglab.adele.kiddcache.CacheService.PutPolicy.ALWAYS;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

import fr.liglab.adele.kiddcache.CacheService;
import fr.liglab.adele.kiddcache.ExpirationDate;

/**
 * {@link CacheService} implementation based on a {@link MapMaker}.
 * 
 */
public class CacheServiceGuava implements CacheService {

	/**
	 * The {@link ConcurrentMap} which contains the cached value.
	 */
	private final ConcurrentMap<Object, CachedObject> cache;

	private int concurrencyLevel = 4;

	private int defaultDuration;

	public CacheServiceGuava() {
		cache = new MapMaker().concurrencyLevel(concurrencyLevel)
				.expireAfterWrite(defaultDuration, SECONDS)
				.makeMap();
	}

	public void put(Object key, Object value) {
		put(key, value, null, ALWAYS);
	}

	public void put(Object key, Object value, ExpirationDate expiration) {
		put(key, value, expiration, ALWAYS);
	}

	public boolean put(Object key, Object value, ExpirationDate expiration,
			PutPolicy policy) {

		boolean putted = false;

		switch (policy) {
		case ALWAYS:
			cache.put(key, new CachedObject(value, expiration));
			putted = true;
			break;

		case ONLY_IF_NOT_PRESENT:
			putted = cache.putIfAbsent(key, new CachedObject(value, expiration)) == null;
			break;

		case UPDATE_ONLY_IF_CACHED:
			putted = cache.replace(key, new CachedObject(value, expiration)) != null;
			break;
		}

		return putted; // true if and only if a new entry has been put in
						// the cache (i.e hastable)
	}

	public void putAll(Map<?, ?> values) {
		this.putAll(values, null, ALWAYS);
	}

	public void putAll(Map<?, ?> values, ExpirationDate expiration) {
		this.putAll(values, expiration, ALWAYS);
	}

	public <T> Set<T> putAll(Map<T, ?> values, ExpirationDate expiration,
			PutPolicy policy) {

		final HashSet<T> addedKeys = new HashSet<T>();

		switch (policy) {
		case ALWAYS:
			for (T key : values.keySet()) {
				cache.put(key, new CachedObject(values.get(key), expiration));
				addedKeys.add(key);
			}
			break;

		case ONLY_IF_NOT_PRESENT:
			for (T key : values.keySet()) {
				if (cache.putIfAbsent(key, new CachedObject(values.get(key),
						expiration)) == null) {
					addedKeys.add(key);
				}
			}
			break;

		case UPDATE_ONLY_IF_CACHED:
			for (T key : values.keySet()) {
				if (cache.replace(key, new CachedObject(values.get(key),
						expiration)) != null) {
					addedKeys.add(key);
				}
			}
			break;
		}

		return addedKeys;
	}

	public Object get(Object key) {
		CachedObject cachedObject = cache.get(key);

		if (cachedObject == null) {
			return null; // Does not exist
		}

		if (cachedObject.getExpirationDate() != null
				&& cachedObject.getExpirationDate().getDateInMilliseconds() < currentTimeMillis()) {
			cache.remove(key);
			return null; // is expired
		}

		return cachedObject.getObject(); // OK
	}

	public <T> Map<T, Object> getAll(final Collection<T> keys) {
		final HashMap<T, Object> returnMap = new HashMap<T, Object>();
		final long currenttime = currentTimeMillis();

		for (T key : keys) {
			CachedObject cachedObject = cache.get(key);

			if (cachedObject == null) {
				continue;
			}

			if (cachedObject.getExpirationDate() != null
					&& cachedObject.getExpirationDate().getDateInMilliseconds() < currenttime) {
				cache.remove(key);
				continue;
			}

			returnMap.put(key, cachedObject.getObject());
		}
		return returnMap;
	}

	public boolean contains(Object key) {
		return cache.containsKey(key);
	}

	public boolean delete(Object key) {
		return cache.remove(key) != null;
	}

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
