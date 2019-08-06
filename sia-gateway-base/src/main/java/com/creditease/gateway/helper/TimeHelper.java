/*-
 * <<
 * sag
 * ==
 * Copyright (C) 2019 sia
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */


package com.creditease.gateway.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/***
 * 
 * @author admin
 *
 */
public class TimeHelper {

    public static String getCurTimeStr() {

        return getCurTimeStr("yyyy-MM-dd HH:mm");
    }

    public static String getCurTimeStr(String format) {

        return getDateTimeStr(new Date(), format);
    }

    public static String getDateTimeStr(Date date, String format) {

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(date);
        return dateString;
    }
    
    public static Date getDateTime(String timeStr, String format){
    	
    	Date date = null;
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
    	try {
			date = sdf.parse(timeStr);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
    	return date;
    }
    
    public static Calendar str2Calendar(String timeStr, String format){
    	
		return str2Calendar(getDateTime(timeStr, format)) ;
    }

    public static Calendar str2Calendar(Date date){
    	
    	Calendar calendar = null;
    	if(null == date){
    		return calendar;
    	}
    	
    	calendar = Calendar.getInstance();
    	calendar.setTime(date);
		return calendar;
    } 
    
    public static String calendar2Str(Calendar calendar, String format){
    	
    	return getDateTimeStr(calendar.getTime(), format);
    	
    }
    
}
