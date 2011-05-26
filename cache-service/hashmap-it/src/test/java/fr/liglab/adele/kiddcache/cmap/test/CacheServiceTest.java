package fr.liglab.adele.kiddcache.cmap.test;

import static fr.liglab.adele.kiddcache.cmap.test.ITTools.waitForIt;
import static org.apache.felix.ipojo.ComponentInstance.VALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.ComponentInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.JUnitOptions;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

import fr.liglab.adele.kiddcache.CacheService;
import fr.liglab.adele.kiddcache.ExpirationDate;

@RunWith(JUnit4TestRunner.class)
public class CacheServiceTest {
	private static final String CACHE_SERVICE_FACTORY = "cache.service.component";

	private static final String DEFAULT_INSTANCE_NAME = "cache.service.hashtable";

	/*
	 * Number of mock object by test.
	 */
	private static final int MAX_MOCK = 10;

	@Mock
	LogService log;

	@Mock
	LogService log2;

	@Inject
	private BundleContext context;

	private OSGiHelper osgi;

	private IPOJOHelper ipojo;

	@Before
	public void setUp() {
		osgi = new OSGiHelper(context);
		ipojo = new IPOJOHelper(context);

		// initialise the annoted mock object
		initMocks(this);
	}

	@After
	public void tearDown() {
		osgi.dispose();
		ipojo.dispose();
	}

	@Configuration
	public static Option[] configure() {
		Option[] platform = options(felix());

		Option[] bundles = options(provision(
				mavenBundle().groupId("org.apache.felix")
						.artifactId("org.apache.felix.ipojo")
						.versionAsInProject(),
				mavenBundle().groupId("org.ow2.chameleon.testing")
						.artifactId("osgi-helpers").versionAsInProject(),
				mavenBundle().groupId("org.osgi")
						.artifactId("org.osgi.compendium").versionAsInProject(),
				mavenBundle().groupId("org.slf4j").artifactId("slf4j-api")
						.versionAsInProject(),
				mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple")
						.versionAsInProject(),
				mavenBundle().groupId("fr.liglab.adele.kidd-cache")
						.artifactId("cache-service").versionAsInProject(),

				// The target
				mavenBundle().groupId("fr.liglab.adele.kidd-cache")
						.artifactId("cache-service-hashtable")
						.versionAsInProject()));

		Option[] r = OptionUtils.combine(platform, bundles);

		return r;
	}

	/**
	 * Mockito bundles
	 */
	@Configuration
	public static Option[] mockitoBundle() {
		return options(JUnitOptions.mockitoBundles());
	}

	/**
	 * Test if the factory is valid and able to create instances.
	 */
	@Test
	public void testInstanceCreation() {
		ComponentInstance instance = createInstance();

		waitForIt(200);

		// The instance must be valid since there is an ExporterService ;)
		assertEquals(VALID, instance.getState());

	}

	/**
	 * Test if the CacheService is available.
	 */
	@Test
	public void testCacheServiceAvailability() {
		CacheService cache = getDefaultCacheService();

		// The Default CacheService must be available
		assertNotNull(cache);
	}

	/**
	 * Test {@link CacheService#put(Object, Object)} simple case.
	 */
	@Test
	public void testPut() {
		// Get the cache service
		CacheService cache = getDefaultCacheService();

		String key = "key";

		// Put the log in the cache
		cache.put(key, log);

		// Get the cached object
		Object logCached = cache.get(key);

		// verify that we get the Log object
		assertEquals(logCached, log);

		// verify that there is no side effect
		verifyZeroInteractions(log);
	}

	/**
	 * Test {@link CacheService#put(Object, Object)} simple case with overriding
	 * a cached object for the same key
	 */
	@Test
	public void testPutForSameKey() {
		// Get the cache service
		CacheService cache = getDefaultCacheService();

		String key = "key";

		// Put the log in the cache
		cache.put(key, log);

		// Put log in the cache on same key
		cache.put(key, log2);

		// Get the cached object
		Object cachedLog2 = cache.get(key);

		// Verify that we got log2 object and previous one has been deleted
		assertEquals(log2, cachedLog2);

		// verify that there is no side effect
		verifyZeroInteractions(log2);

	}

	/**
	 * Test {@link CacheService#put(Object, Object, ExpirationDate)} simple case
	 * with expiration time
	 */
	@Test
	public void testPutWithExpirationTime() {
		// Get the cache service
		CacheService cache = getDefaultCacheService();
		String key = "key";

		// Put the log in the cache in Expiration time of 1 seconds
		cache.put(key, log, ExpirationDate.createFromDeltaMillis(100));

		// Wait 150 milliseconds
		waitForIt(150);

		// Get the cached object
		Object logCached = cache.get(key);

		// verify that we get null, because of Expiration time has been reached
		assertEquals(null, logCached);
	}

	/**
	 * Test
	 * {@link CacheService#put(Object, Object, ExpirationDate, fr.liglab.adele.kiddcache.CacheService.PutPolicy)}
	 * case with Always policy
	 */
	@Test
	public void testPutWithAlwaysPoliciy() {

		// Get the cache service
		CacheService cache = getDefaultCacheService();
		String key = "key";

		// Put the log in the cache with ALWAYS policy
		cache.put(key, log, null, CacheService.PutPolicy.ALWAYS);

		// Get the cached object
		Object logCached = cache.get(key);

		// verify that we get the Log object
		assertEquals(logCached, log);

		// verify that there is no side effect
		verifyZeroInteractions(log);

		// Put the log2 in the cache with ALWAYS  policy
		cache.put(key, log2, null, CacheService.PutPolicy.ALWAYS);

		// Get the cached object
		Object logCached2 = cache.get(key);

		// verify that we get the Log object
		assertEquals(logCached2, log2);

		// verify that there is no side effect
		verifyZeroInteractions(log2);

	}

	/**
	 * Test
	 * {@link CacheService#put(Object, Object, ExpirationDate, fr.liglab.adele.kiddcache.CacheService.PutPolicy)}
	 * case with ONLY_IF_NOT_PRESENT policy
	 */
	@Test
	public void testPutWithOnlyIfNotPresentPolicy() {

		boolean answer;

		// Get the cache service
		CacheService cache = getDefaultCacheService();
		String key = "key";

		// Put the log in the cache with ONLY_IF_NOT_PRESENT policy
		cache.put(key, log, null, CacheService.PutPolicy.ONLY_IF_NOT_PRESENT);

		// Get the cached object
		Object logCached = cache.get(key);

		// verify that we get the Log object
		assertEquals(logCached, log);

		// verify that there is no side effect
		verifyZeroInteractions(log);

		// Put the log2 in the cache with ONLY_IF_NOT_PRESENT policy
		answer = cache.put(key, log2, null, CacheService.PutPolicy.ONLY_IF_NOT_PRESENT);

		// check if did not put, already exists for given key
		assertEquals(false, answer);

		// Get the cached object
		Object logCached2 = cache.get(key);

		// verify that we get the Log object
		assertEquals(log, logCached2);

		// verify that there is no side effect
		verifyZeroInteractions(log2);

	}

	/**
	 * Test
	 * {@link CacheService#put(Object, Object, ExpirationDate, fr.liglab.adele.kiddcache.CacheService.PutPolicy)}
	 * case with UPDATE_ONLY_IF_CACHED policy
	 */
	@Test
	public void testPutWithUpdateOnlyIfCachedPolicy() {

		boolean answer;

		// Get the cache service
		CacheService cache = getDefaultCacheService();
		String key = "key";

		// Put the log in the cache with UPDATE_ONLY_IF_CACHED policy
		answer = cache.put(key, log, null, CacheService.PutPolicy.UPDATE_ONLY_IF_CACHED);

		// check if did not put, nothing cached for given key
		assertEquals(false, answer);

		// Get the cached object
		Object logCached = cache.get(key);

		// verify that we get nothing
		assertEquals(null, logCached);

		// verify that there is no side effect
		verifyZeroInteractions(log);

		// Put the log in the cache
		cache.put(key, log);

		// Put the log2 in the cache with UPDATE_ONLY_IF_CACHED policy
		answer = cache.put(key, log2, null, CacheService.PutPolicy.UPDATE_ONLY_IF_CACHED);

		// check if put
		assertEquals(true, answer);

		// Get the cached object
		Object logCached2 = cache.get(key);

		// verify that we get the Log2 object
		assertEquals(log2, logCached2);

		// verify that there is no side effect
		verifyZeroInteractions(log2);

	}

	/**
	 * Test {@link CacheService#putAll(java.util.Map)} simple putAll and getAll case
	 */
	@Test
	public void testPutGetAll() {
		
		// Get the cache service
		CacheService cache = getDefaultCacheService();
		String key1 = "key1";
		String key2 = "key2";
		
		//Preparing a hashmap
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key1, log);
		map.put(key2, log2);
		
		//Put a map into a cache
		cache.putAll(map);
		
		//get a cached map
		map = cache.getAll(map.keySet());
		
		//verify that we got correct objects
		assertEquals(log, map.get(key1));
		assertEquals(log2, map.get(key2));
		
		// verify that there is no side effect
		verifyZeroInteractions(log);
		verifyZeroInteractions(log2);
	}

	private ComponentInstance createInstance() {
		ComponentInstance instance = null;
		try {
			instance = ipojo.createComponentInstance(CACHE_SERVICE_FACTORY);
		} catch (Exception e) {
			fail("Unable to create an export-supervisor instance, "
					+ e.getMessage());
		}

		return instance;
	}

	private CacheService getDefaultCacheService() {
		return (CacheService) osgi.getServiceObject(
				CacheService.class.getName(), "(instance.name="
						+ DEFAULT_INSTANCE_NAME + ")");
	}
}
