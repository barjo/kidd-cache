package fr.liglab.adele.kiddcache.implementation.cleaner;

import static java.lang.System.currentTimeMillis;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import fr.liglab.adele.kiddcache.implementation.CachedObject;

public class CleanigThread extends Thread {

	private ConcurrentMap<Object, CachedObject> cache;
	private boolean running;
	private int secondsCheck;

	public CleanigThread(ConcurrentMap<Object, CachedObject> cache,
			int secondsCheck) {
		super();
		this.cache = cache;
		this.secondsCheck = secondsCheck;
		running = true;
	}

	public void run() {
		while (running) {
			try {
				Thread.sleep(secondsCheck * 1000);
				for (Entry<Object, CachedObject> cachedObject : cache
						.entrySet()) {
					if (cachedObject.getValue().getExpirationDate()
							.getDateInMilliseconds() < currentTimeMillis()) {
						cache.remove(cachedObject.getKey());
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void safeStop() {
		running = false;
	}

}
