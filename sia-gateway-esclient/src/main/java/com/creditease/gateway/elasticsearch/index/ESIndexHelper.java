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


package com.creditease.gateway.elasticsearch.index;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author yongbiao
 */
public class ESIndexHelper {

    final private static String DATE_FORMAT = "yyyy-MM-dd";

    final private static long MILLIS_OF_DAY = 60 * 60 * 24 * 1000L;

    public static String getFirstDayOfMonth(String prefix) {

        return format(prefix) + "_" + LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    /**
     * 获取按周记录的索引名，以周的开始（周日）结尾
     *
     * @param prefix
     * @return
     */
    public static String getIndexOfWeek(String prefix) {

        return getIndexOfWeekByMillis(prefix, System.currentTimeMillis());
    }

    /**
     * 获取按周记录的索引名
     *
     * @param prefix
     * @param when   以当前周为基准第when周，可为负
     * @return
     */
    public static String getIndexOfWeek(String prefix, int when) {

        return getIndexOfWeekByMillis(prefix, System.currentTimeMillis() + 7 * when * MILLIS_OF_DAY);
    }

    /**
     * 获取日期对应的按周记录的索引名
     *
     * @param prefix
     * @param date   yyyy-MM-dd
     * @return
     */
    public static String getIndexOfWeek(String prefix, String date) {

        Calendar cld = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            cld.setTime(sdf.parse(date));
        } catch (ParseException e) {
            return null;
        }
        cld.set(Calendar.DAY_OF_WEEK, 1);

        return format(prefix) + "_" + sdf.format(cld.getTime());
    }

    /**
     * 获取时间戳对应的按周记录的索引名
     *
     * @param prefix
     * @param timestamp
     * @return
     */
    public static String getIndexOfWeekByMillis(String prefix, long timestamp) {

        Calendar cld = Calendar.getInstance();
        cld.setTimeInMillis(timestamp);
        cld.set(Calendar.DAY_OF_WEEK, 1);

        return format(prefix) + "_" + new SimpleDateFormat(DATE_FORMAT).format(cld.getTime());

    }

    /**
     * 获取按日记录的索引名
     *
     * @param prefix
     * @return
     */
    public static String getIndexOfDay(String prefix) {

        return getIndexOfDayByMillis(prefix, System.currentTimeMillis());
    }

    /**
     * 获取按日记录的索引名
     *
     * @param prefix
     * @param when   以当日为基准第when日，可为负
     * @return
     */
    public static String getIndexOfDay(String prefix, int when) {

        return getIndexOfDayByMillis(prefix, System.currentTimeMillis() + when * MILLIS_OF_DAY);
    }

    /**
     * 获取日期对应的按日记录的索引名
     *
     * @param prefix
     * @param date   yyyy-MM-dd
     * @return
     */
    public static String getIndexOfDay(String prefix, String date) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            return format(prefix) + "_" + sdf.format(sdf.parse(date));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取时间戳对应的按日记录的索引名
     *
     * @param prefix
     * @param timestamp
     * @return
     */
    public static String getIndexOfDayByMillis(String prefix, long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return format(prefix) + "_" + sdf.format(new Date(timestamp));
    }

    private static String format(String str) {

        return str.toLowerCase().replace('.', '_');
    }

}