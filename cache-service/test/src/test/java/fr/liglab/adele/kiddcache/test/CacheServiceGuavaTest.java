package fr.liglab.adele.kiddcache.test;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;


import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class CacheServiceGuavaTest extends CacheServiceTestAbstract {

	@Override
	String getInstanceName() {
		return "cache.service.guava";
	}

	@Override
	String getCacheServiceFactory() {
		return "cache.service.component.guava";
	}

	@Override
	Option[] getPaxExamBundleOptions() {
		return options(
				provision(
						mavenBundle().groupId("org.apache.felix")
								.artifactId("org.apache.felix.ipojo")
								.versionAsInProject(),
						mavenBundle().groupId("org.ow2.chameleon.testing")
								.artifactId("osgi-helpers")
								.versionAsInProject(),
						mavenBundle().groupId("org.osgi")
								.artifactId("org.osgi.compendium")
								.versionAsInProject(),
						mavenBundle().groupId("org.slf4j")
								.artifactId("slf4j-api").versionAsInProject(),
						mavenBundle().groupId("org.slf4j")
								.artifactId("slf4j-simple")
								.versionAsInProject(),
						mavenBundle().groupId("fr.liglab.adele.kidd-cache")
								.artifactId("cache-service")
								.versionAsInProject(),
						// The target
						mavenBundle().groupId("fr.liglab.adele.kidd-cache")
								.artifactId("cache-service-guava")
								.versionAsInProject()),
				mavenBundle().groupId("com.google.guava").artifactId("guava")
						.version("r08"));
	}

}
