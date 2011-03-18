package fr.liglab.adele.kiddcache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The CacheService offers an easy way for caching commonly using data. 
 * TODO finish javadoc.
 * @author barjo
 */
public interface CacheService {
	
	/**
	 * Property name for the default expiration delta property (i.e {@code ExpirationDate = delta + creationTime}).
	 */
	String DEFAULT_EXPIRATION_DATE = "default.expiration.delta";
	
	/**
	 * Property name for the default put policy property.
	 */
	String DEFAULT_PUT_POLICY = "default.put.policy";
	
	/**
	 * Equivalent to {@link CacheService#put(Object, Object, ExpirationDate, PutPolicy) put(key,value,null,PutPolicy.ALWAYS)}
	 * 
	 * @param key The key for the new cache entry
	 * @param value The data to be cached
	 */
	void put(Object key, Object value);
	
	/**
	 * Put a new value in the cache, identified by key which expire at expiration.
	 * The cached policy is set to PutPolicy.ALWAYS.
	 * 
	 * @param key The key for the new cache entry
	 * @param value The data to be cached
	 * @param expiration The {@code ExpirationDate} of the cached entry or {@code null} for none.
	 */
	void put(Object key, Object value,ExpirationDate expiration);
	
	/**
	 * Put a new value in the cache, identified by key which expire at expiration.
	 * The cached policy is set to policy.
	 * 
	 * @param key The key for the new cache entry
	 * @param value The data to be cached
	 * @param expiration The {@code ExpirationDate} of the cached entry or {@code null} for none.
	 * @param policy {@code PutPolicy} storing strategy regarding pre-existing entries under the same key. 
	 * @return {@code true} if a new entry was created, {@code false} if not.
	 */
	boolean put(Object key, Object value,ExpirationDate expiration,PutPolicy policy);
	
	/**
	 * Put 
	 * @param values
	 */
	void putAll(Map<?,?> values);
	
	void putAll(Map<?,?> values,ExpirationDate expiration);
	
	<T> Set<T> putAll(Map<T,?> values,ExpirationDate expiration,PutPolicy policiy);
	
	Object get(Object key);
	
	<T> Map<T, Object> getAll(Collection<T> arg0);
	
	boolean contains(Object key);
	
	boolean delete(Object key);
	
	<T> Set<T> deleteAll(Collection<T> keys);
	
	
	
	/**
	 * Cache put strategies.
	 * @author barjo
	 */
	enum PutPolicy {
		/**
		 * Cache the data if and only if there is no existing data for the given key.
		 */
		ONLY_IF_NOT_PRESENT,
		
		/**
		 * Cache the data if and only if there is an existing data for the given key.
		 */
		UPDATE_ONLY_IF_CACHED,
		
		/**
		 * Always put.
		 */
		ALWAYS
	}
}
