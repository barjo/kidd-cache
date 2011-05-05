package fr.liglab.adele.kiddcache.implementation;

import static fr.liglab.adele.kiddcache.CacheService.PutPolicy.ALWAYS;
import static java.lang.System.currentTimeMillis;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import fr.liglab.adele.kiddcache.CacheService;
import fr.liglab.adele.kiddcache.ExpirationDate;

/**
 * {@link CacheService} implementation based on a {@link Map}.
 * 
 */
public class CacheServiceHashTable implements CacheService {
	
	private final ReadWriteLock rwlock = new ReentrantReadWriteLock();

	/**
	 * The {@link Map} which contains the cached value.
	 */
	private final Map<Object, CachedObject> cache = new HashMap<Object, CachedObject>();

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
			putted = syncPutAlways(key, new CachedObject(value, expiration));
			break;

		case ONLY_IF_NOT_PRESENT:
			putted = syncPutIfNotPresent(key, new CachedObject(value, expiration));
			break;

		case UPDATE_ONLY_IF_CACHED:
			putted = syncPutIfCached(key, new CachedObject(value, expiration));
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

		synchronized (cache) {
			switch (policy) {
			case ALWAYS:
				for (T key : values.keySet()) {
					if ( syncPutAlways(key, new CachedObject(values.get(key), expiration)) ){
							addedKeys.add(key);
					}
				}
				break;

			case ONLY_IF_NOT_PRESENT:
				for (T key : values.keySet()) {
					if ( syncPutIfNotPresent(key, new CachedObject(values.get(key), expiration)) ){
						addedKeys.add(key);
					}
				}
				break;

			case UPDATE_ONLY_IF_CACHED:
				for (T key : values.keySet()) {
					if ( syncPutIfCached(key, new CachedObject(values.get(key), expiration)) ){
						addedKeys.add(key);
					}
				}
				break;
			}

			return addedKeys;
		}
	}

	public Object get(Object key) {
			CachedObject cachedObject = syncGet(key);

			if (cachedObject == null) {
				return null; // Does not exist
			}

			if (cachedObject.getExpirationDate() != null
					&& cachedObject.getExpirationDate().getDateInMilliseconds() < currentTimeMillis()) {
				syncRemove(key);
				return null; // is expired
			}

			return cachedObject.getObject(); // OK
	}


	public <T> Map<T, Object> getAll(final Collection<T> keys) {
		final HashMap<T, Object> returnMap = new HashMap<T, Object>();
		final long currenttime = currentTimeMillis();

			for (T key : keys) {
				CachedObject cachedObject = syncGet(key);

				if (cachedObject == null) {
					continue;
				}

				if (cachedObject.getExpirationDate() != null && cachedObject.getExpirationDate().getDateInMilliseconds() < currenttime) {
					syncRemove(key);
					continue;
				}

				returnMap.put(key, cachedObject.getObject());
			}
			return returnMap;
	}

	public boolean contains(Object key) {
		try{
			rwlock.readLock().lock();
			return cache.containsKey(key);
		}finally{
			rwlock.readLock().unlock();
		}
	}

	public boolean delete(Object key) {
		return syncRemove(key);
	}

	public <T> Set<T> deleteAll(Collection<T> keys) {
		HashSet<T> returnSet = new HashSet<T>();
		for (T key : keys) {
			if (syncRemove(key)) { //TODO take the wlock before the for
				returnSet.add(key);
			}
		}
		return returnSet;
	}
	
	
	/*---------------------------------*
	 * Convenient synchronized methods *
	 *---------------------------------*/
	
	private boolean syncPutAlways(Object key,CachedObject value){
		try{
			rwlock.writeLock().lock();
			cache.put(key, value);
			return true;
		}finally{
			rwlock.writeLock().unlock();
		}
	}
	
	private boolean syncPutIfCached(Object key,CachedObject value){
		try{
			rwlock.writeLock().lock();
			if (cache.containsKey(key)){
				cache.put(key, value);
				return true;
			}
			else {
				return false;
			}
		}finally{
			rwlock.writeLock().unlock();
		}
	}
	
	private boolean syncPutIfNotPresent(Object key,CachedObject value){
		try{
			rwlock.writeLock().lock();
			if (!cache.containsKey(key)){
				cache.put(key, value);
				return true;
			}
			else {
				return false;
			}
		}finally{
			rwlock.writeLock().unlock();
		}
	}
	
	private CachedObject syncGet(Object key){
		try{
			rwlock.readLock().lock();
			return cache.get(key);
		}finally{
			rwlock.readLock().unlock();
		}
	}
	
	private boolean syncRemove(Object key) {
		try{
			rwlock.writeLock().lock();
			return cache.remove(key) != null;
		}finally{
			rwlock.writeLock().unlock();
		}
	}
	
}