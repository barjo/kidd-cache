package fr.liglab.adele.kiddcache.cmap.test;

import static fr.liglab.adele.kiddcache.cmap.test.ITTools.waitForIt;
import static org.apache.felix.ipojo.ComponentInstance.VALID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import org.apache.felix.ipojo.ComponentInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.JUnitOptions;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.testing.helpers.IPOJOHelper;
import org.ow2.chameleon.testing.helpers.OSGiHelper;

import fr.liglab.adele.kiddcache.CacheService;

@RunWith(JUnit4TestRunner.class)
public class CacheServiceTest {
    private static final String CACHE_SERVICE_FACTORY = "cache.service.component";

	private static final String DEFAULT_INSTANCE_NAME = "cache.service.hashtable";

    /*
     * Number of mock object by test.
     */
    private static final int MAX_MOCK = 10;

    @Inject
    private BundleContext context;

    private OSGiHelper osgi;
    
    private IPOJOHelper ipojo;
    

    @Before
    public void setUp() {
        osgi = new OSGiHelper(context);
        ipojo = new IPOJOHelper(context);
        
        //initialise the annoted mock object
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
        		mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.ipojo").versionAsInProject(),
                mavenBundle().groupId("org.ow2.chameleon.testing").artifactId("osgi-helpers").versionAsInProject(), 
                mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").versionAsInProject(),
                mavenBundle().groupId("org.slf4j").artifactId("slf4j-api").versionAsInProject(),
                mavenBundle().groupId("org.slf4j").artifactId("slf4j-simple").versionAsInProject(),
                mavenBundle().groupId("fr.liglab.adele.kidd-cache").artifactId("cache-service").versionAsInProject(), 

                // The target
                mavenBundle().groupId("fr.liglab.adele.kidd-cache").artifactId("cache-service-hashtable").versionAsInProject())); 

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
		
		//The instance must be valid since there is an ExporterService ;)
		assertEquals(VALID, instance.getState()); 
		
    }

    /**
     * Test if the CacheService is available.
     */
    @Test 
    public void testCacheServiceAvailability(){
    	CacheService cache = getDefaultCacheService();
    	
    	//The Default CacheService must be available
    	assertNotNull(cache);
    }
        
    
    private ComponentInstance createInstance(){
		ComponentInstance instance = null;
		try {
			instance = ipojo.createComponentInstance(CACHE_SERVICE_FACTORY);
		} catch (Exception e) {
			fail("Unable to create an export-supervisor instance, "+e.getMessage());
		}
		
		return instance;
    }
    
    private CacheService getDefaultCacheService() {
		return (CacheService) osgi.getServiceObject(CacheService.class.getName(), "(instance.name="+DEFAULT_INSTANCE_NAME+")");
	}
}

