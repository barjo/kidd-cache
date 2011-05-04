package fr.liglab.adele.kiddcache;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;

/**
 * TODO javadoc
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
	
	public static ExpirationDate createFromDate(Date date){
		return new ExpirationDate(date);
	}
	
	public static ExpirationDate createFromDeltaMillis(int milliseconds){
		Date edate = new Date(currentTimeMillis() + milliseconds);
		return new ExpirationDate(edate);
	}
	
	public static ExpirationDate createFromDeltaSeconds(int seconds){
		Date edate = new Date(currentTimeMillis() + MILLISECONDS.convert(seconds, SECONDS));
		return new ExpirationDate(edate);
	}
}
