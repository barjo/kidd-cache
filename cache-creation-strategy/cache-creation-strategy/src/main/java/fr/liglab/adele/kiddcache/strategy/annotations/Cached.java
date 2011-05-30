package fr.liglab.adele.kiddcache.strategy.annotations;

import java.lang.annotation.*;

import fr.liglab.adele.kiddcache.CacheService;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Cached {

	CacheService.PutPolicy policy() default CacheService.PutPolicy.ALWAYS;
	int expireSeconds() default 0;
	String keyValue() default "";
	
}