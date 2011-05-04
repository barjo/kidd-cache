package fr.liglab.adele.kiddcache.implementation;

import fr.liglab.adele.kiddcache.ExpirationDate;

/**
 * The cached object is mutable.
 * TODO comment 
 * XXX what about the synchro ?
 */
public class CachedObject {
	private Object object;
	private ExpirationDate date;
	
	
	public CachedObject(Object pObject, ExpirationDate pDate) {
		object = pObject;
		date = pDate;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object pObject) {
		object = pObject;
	}
	
	public ExpirationDate getExpirationDate() {
		return date;
	}
	
	public void setExpirationDate(ExpirationDate pDate) {
		date = pDate;
	}
}
