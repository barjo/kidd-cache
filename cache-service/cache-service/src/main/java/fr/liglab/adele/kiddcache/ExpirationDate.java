package fr.liglab.adele.kiddcache;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;

/**
 * Offers an easy way to create a expiration time, used in {@link CacheService}
 * @author barjo
 */
public final class ExpirationDate {
	private final Date date;
	
	private ExpirationDate(Date pDate){
		date = pDate;
	}
	
	public long getDateInMilliseconds(){
		return date.getTime();
	}
	
	public long getDateInSeconds(){
		return SECONDS.convert(getDateInMilliseconds(), MILLISECONDS);
	}
	/**Create an Expiration date from given date
	 * @param date expiration date  
	 * @return ExpirationDate
	 */
	public static ExpirationDate createFromDate(Date date){
		return new ExpirationDate(date);
	}
	/**Create an Expiration date from now on until specified milliseconds
	 * @param milliseconds number of milliseconds  
	 * @return ExpirationDate
	 */	
	public static ExpirationDate createFromDeltaMillis(int milliseconds){
		Date edate = new Date(currentTimeMillis() + milliseconds);
		return new ExpirationDate(edate);
	}
	
	/**Create an Expiration date from now on until specified seconds
	 * @param seconds number of seconds  
	 * @return ExpirationDate
	 */
	public static ExpirationDate createFromDeltaSeconds(int seconds){
		Date edate = new Date(currentTimeMillis() + MILLISECONDS.convert(seconds, SECONDS));
		return new ExpirationDate(edate);
	}
}
