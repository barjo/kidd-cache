package fr.liglab.adele.kiddcache.implementation;

import fr.liglab.adele.kiddcache.ExpirationDate;

/**
 * The cached object is immutable.
 * TODO comment 
 * XXX what about the synchro ?
 */
public final class CachedObject {
	private final Object object;
	private final ExpirationDate date;
	
	
	public CachedObject(Object pObject, ExpirationDate pDate) {
		object = pObject;
		date = pDate;
	}
	
	public Object getObject() {
		return object;
	}
	
	public ExpirationDate getExpirationDate() {
		return date;
	}
}
