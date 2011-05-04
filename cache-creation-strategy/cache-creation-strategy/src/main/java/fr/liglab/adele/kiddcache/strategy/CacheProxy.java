package fr.liglab.adele.kiddcache.strategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.handlers.providedservice.CreationStrategy;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import fr.liglab.adele.kiddcache.CacheService;

public class CacheProxy extends CreationStrategy implements InvocationHandler, ServiceTrackerCustomizer {
	private volatile Object cachedpojo;
	private volatile CacheService cacheService;
	private InstanceManager manager;
	private ServiceTracker tracker;
	

	@Override
	public void onPublication(InstanceManager manager, String[] interfaces,
			Properties props) {
		tracker = new ServiceTracker(manager.getContext(), CacheService.class.getName(), this);
		tracker.open(false);
		
		cachedpojo = Proxy.newProxyInstance(
				manager.getClazz().getClassLoader(), manager.getClazz()
						.getInterfaces(), this);
		this.manager = manager;
	}

	@Override
	public void onUnpublication() {
//		manager.deletePojoObject(manager.getPojoObject());
		if(tracker != null )tracker.close();
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object returnObject = null;
		
		//check if cache service is available
		synchronized (tracker) {

			if (cacheService != null) {
				returnObject = cacheService.get(args[0].toString());

				if (returnObject == null) {
					returnObject = method.invoke(manager.getPojoObject(), args);
					cacheService.put(args[0].toString(), returnObject);
				}
			}
		}
		
		if (returnObject == null) {
			returnObject = method.invoke(manager.getPojoObject(), args);
		}
		
		return returnObject;
	}

	//

	public Object getService(Bundle arg0, ServiceRegistration arg1) {
		return cachedpojo;
	}

	public void ungetService(Bundle arg0, ServiceRegistration arg1, Object arg2) {
		//
	}

	//tracker methods
	
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
