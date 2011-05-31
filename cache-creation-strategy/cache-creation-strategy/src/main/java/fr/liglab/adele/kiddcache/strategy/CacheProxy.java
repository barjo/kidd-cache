package fr.liglab.adele.kiddcache.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.handlers.providedservice.CreationStrategy;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fr.liglab.adele.kiddcache.CacheService;
import fr.liglab.adele.kiddcache.ExpirationDate;
import fr.liglab.adele.kiddcache.strategy.annotations.Cached;

public class CacheProxy extends CreationStrategy implements InvocationHandler,
		ServiceTrackerCustomizer {
	private volatile Object cachedpojo;
	private volatile CacheService cacheService;
	private InstanceManager manager;
	private ServiceTracker tracker;

	@Override
	public void onPublication(InstanceManager manager, String[] interfaces,
			Properties props) {

		this.manager = manager;
		tracker = new ServiceTracker(manager.getContext(),
				CacheService.class.getName(), this);
		if (tracker != null)
			tracker.open();

		cachedpojo = Proxy.newProxyInstance(
				manager.getClazz().getClassLoader(), manager.getClazz()
						.getInterfaces(), this);

	}

	@Override
	public void onUnpublication() {
		// manager.deletePojoObject(manager.getPojoObject());
		if (tracker != null)
			tracker.close();
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object returnObject = null;
		List<Object> key;
		// prevent of double invoke a method when it returned a null value
		boolean methodInvoked = false;
		Cached cacheAnnotation = method.getAnnotation(Cached.class);

		synchronized (tracker) {
			// check if cache service is available and method is cached
			if (cacheAnnotation != null && cacheService != null) {
				key = new ArrayList<Object>();
				if (cacheAnnotation.keyValue().equals("")) {
					// create a caching key from class name, method name and arguments
					key.add(manager.getClazz());
					key.add(method.getName());
					key.addAll(Arrays.asList(args));
				} else {
					key.add(cacheAnnotation.keyValue());
				}
				returnObject = cacheService.get(key);
				if (returnObject == null) {
					returnObject = method.invoke(manager.getPojoObject(), args);
					methodInvoked = true;
					// check if expiration time is set
					if (cacheAnnotation.expireSeconds() > 0) {
						cacheService.put(key, returnObject, ExpirationDate
								.createFromDeltaSeconds(cacheAnnotation
										.expireSeconds()), cacheAnnotation
								.policy());
					} else {
						cacheService.put(key, returnObject, null,
								cacheAnnotation.policy());
					}
				}
			}
		}

		if (returnObject == null && !methodInvoked) {
			returnObject = method.invoke(manager.getPojoObject(), args);
		}

		return returnObject;
	}

	public Object getService(Bundle arg0, ServiceRegistration arg1) {
		return cachedpojo;
	}

	public void ungetService(Bundle arg0, ServiceRegistration arg1, Object arg2) {
		//
	}

	// tracker methods

	public Object addingService(ServiceReference sref) {
		synchronized (tracker) {
			cacheService = (CacheService) manager.getContext().getService(sref);
		}
		return cacheService;
	}

	public void modifiedService(ServiceReference arg0, Object arg1) {
	}

	public void removedService(ServiceReference arg0, Object arg1) {
		synchronized (tracker) {
			cacheService = null;
		}
	}
}
