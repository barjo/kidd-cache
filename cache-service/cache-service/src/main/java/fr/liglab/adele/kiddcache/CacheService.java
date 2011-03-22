package fr.liglab.adele.kiddcache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The CacheService offers an easy way for caching commonly using data. 
 * TODO Reread and correct the javadoc.
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
	 * The cached policy is set to {@link PutPolicy#ALWAYS}.
	 * 
	 * @param key The key for the new cache entry
	 * @param value The data to be cached
	 * @param expiration The {@link ExpirationDate} of the cached entry or {@code null} for none.
	 */
	void put(Object key, Object value,ExpirationDate expiration);
	
	/**
	 * Put a new value in the cache, identified by key which expire at expiration.
	 * The cached policy is set to policy.
	 * 
	 * @param key The key for the new cache entry
	 * @param value The data to be cached
	 * @param expiration The {@link ExpirationDate} of the cached entry or {@code null} for none.
	 * @param policy {@link PutPolicy} storing strategy regarding pre-existing entries under the same key. 
	 * @return {@code true} if a new entry was created, {@code false} if not.
	 */
	boolean put(Object key, Object value,ExpirationDate expiration,PutPolicy policy);
	
	/**
	 * Equivalent to {@link CacheService#putAll(Map, ExpirationDate, PutPolicy) putAll(values,null,PutPolicy.ALWAYS)}. 
	 * @param entries (key/value) to be cached
	 */
	void putAll(Map<?,?> values);
	
	/**
	 * Equivalent to {@link CacheService#putAll(Map, ExpirationDate, PutPolicy) putAll(values,expiration,PutPolicy.ALWAYS)}. 
     *
	 * @param values entries (key/value) to be cached
	 * @param expiration expiration The {@link ExpirationDate} of the cached entry or {@code null} for none.
	 */
	void putAll(Map<?,?> values,ExpirationDate expiration);
	
	/**
	 * Variant of {@link CacheService#put(Object, Object, ExpirationDate, PutPolicy)}.
	 * 
	 * @param values values entries (key/value) to be cached
	 * @param expiration expiration expiration The {@link ExpirationDate} of the cached entry or {@code null} for none.
	 * @param policy {@link PutPolicy} storing strategy regarding pre-existing entries under the same key. 
	 * @return the set of keys for which entries were created. Keys in <code>values</code> may not be in the returned set because of the policy regarding pre-existing entries.
	 */
	<T> Set<T> putAll(Map<T,?> values,ExpirationDate expiration,PutPolicy policiy);
	
	/**
	 * Fetch a cache value or null if unset.
	 * @param key used to store the cache entry.
	 * @return the value previously cached or <code>null</code> 
	 */
	Object get(Object key);
	
	/**
	 * Multiple keys variant of {@link CacheService#get(Object)}.
	 * @param keys used to store the cache entries 
	 * @return A mapping of key to value of any cached entries found.
	 */
	<T> Map<T, Object> getAll(Collection<T> keys);
	
	/**
	 * Test if there is a cache entry with key.
	 * @param key used to store the cache entry
	 * @return <code>true</code> if the cache contains an entry for the key.
	 */
	boolean contains(Object key);
	
	/**
	 * Remove <code>key</code> from he cache.
	 * @param key to be removed.
	 * @return <code>true</code> if the entry existed and has been removed.
	 */
	boolean delete(Object key);
	
	/**
	 * Multiple keys variant of {@link CacheService#delete(Object)}.
	 * @param keys to be removed.
	 * @return The keys found in the cache that have been deleted through this call.
	 */
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
