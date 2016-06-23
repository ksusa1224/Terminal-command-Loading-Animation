package com.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author ksusa
 *
 */
public class Util {
	/**
	 * 
	 * @param format
	 * @return
	 */
	public static String getNow(String format)
	{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;	
	}	
}
