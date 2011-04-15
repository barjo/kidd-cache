package fr.liglab.adele.kiddcache.implementation;

import fr.liglab.adele.kiddcache.ExpirationDate;

public class CachedObject {
	private Object object;
	private ExpirationDate date;
	
	
	public CachedObject(Object object, ExpirationDate date) {
		super();
		this.object = object;
		this.date = date;
	}
	
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public ExpirationDate getExpirationDate() {
		return date;
	}
	public void setExpirationDate(ExpirationDate date) {
		this.date = date;
	}
	

}
