/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.generic.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Static methods to aid with date manipulation.
 */
public final class DateUtils {

    private DateUtils() {}

    /**
     * Specifies how time results should be rounded.
     */
    public enum TimeRound {
        ROUND_UP,
        ROUND_DOWN
    }

    /**
     * Get the difference between two dates in milliseconds.
     *
     * @param start  The start date
     * @param end    The end date
     */
    public static long getDeltaMilliseconds(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    /**
     * Get the difference between two dates in seconds.
     *
     * @param start  The start date
     * @param end    The end date
     */
    public static double getDeltaSeconds(Date start, Date end) {
        return (double)getDeltaMilliseconds(start, end) / 1000.0D;
    }

    /**
     * Get the difference between two dates in seconds.
     *
     * @param start     The start date
     * @param end       The end date
     * @param rounding  Specify how the result should be rounded
     */
    public static long getDeltaSeconds(Date start, Date end, TimeRound rounding) {
        double seconds = (double)getDeltaMilliseconds(start, end) / 1000.0D;

        switch (rounding) {
            case ROUND_UP:
                return (long)Math.ceil(seconds);
            case ROUND_DOWN:
                return (long)Math.floor(seconds);
        }

        return (long)seconds;
    }

    /**
     * Get the difference between two dates in minutes.
     *
     * @param start  The start date
     * @param end    The end date
     */
    public static double getDeltaMinutes(Date start, Date end) {
        return getDeltaSeconds(start, end) / 60.0D;
    }

    /**
     * Get the difference between two dates in minutes.
     *
     * @param start     The start date
     * @param end       The end date
     * @param rounding  Specify how the result should be rounded
     */
    public static long getDeltaMinutes(Date start, Date end, TimeRound rounding) {
        double seconds = getDeltaSeconds(start, end, rounding) / 60.0D;

        switch (rounding) {
            case ROUND_UP:
                return (long)Math.ceil(seconds);
            case ROUND_DOWN:
                return (long)Math.floor(seconds);
        }

        return (long)seconds;
    }

    /**
     * Get the difference between two dates in hours.
     *
     * @param start  The start date
     * @param end    The end date
     */
    public static double getDeltaHours(Date start, Date end) {
        return getDeltaMinutes(start, end) / 60.0D;
    }

    /**
     * Get the difference between two dates in hours.
     *
     * @param start     The start date
     * @param end       The end date
     * @param rounding  Specify how the result should be rounded
     */
    public static long getDeltaHours(Date start, Date end, TimeRound rounding) {
        double minutes = getDeltaMinutes(start, end, rounding) / 60.0D;

        switch (rounding) {
            case ROUND_UP:
                return (long)Math.ceil(minutes);
            case ROUND_DOWN:
                return (long)Math.floor(minutes);
        }

        return (long)minutes;
    }

    /**
     * Get the difference between two dates in days.
     *
     * @param start  The start date
     * @param end    The end date
     */
    public static double getDeltaDays(Date start, Date end) {
        return getDeltaHours(start, end) / 24.0D;
    }

    /**
     * Get the difference between two dates in days.
     *
     * @param start     The start date
     * @param end       The end date
     * @param rounding  Specify how the result should be rounded
     */
    public static long getDeltaDays(Date start, Date end, TimeRound rounding) {
        double hours = (double)getDeltaHours(start, end, rounding) / 24.0D;

        switch (rounding) {
            case ROUND_UP:
                return (long)Math.ceil(hours);
            case ROUND_DOWN:
                return (long)Math.floor(hours);
        }

        return (long)hours;
    }

    /**
     * Format a {@code Date} object using {@code SimpleDateFormat}.
     *
     * @param date    The date to format
     * @param format  The format to use
     */
    public static String format(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Add milliseconds to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of milliseconds to add.
     */
    public static Date addMilliseconds(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * Add seconds to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of seconds to add.
     */
    public static Date addSeconds(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.SECOND, amount);
    }

    /**
     * Add minutes to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of minutes to add.
     */
    public static Date addMinutes(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * Add hours to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of hours to add.
     */

    public static Date addHours(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * Add days to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of days to add.
     */
    public static Date addDays(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Add weeks to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of weeks to add.
     */
    public static Date addWeeks(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * Add months to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of months to add.
     */
    public static Date addMonths(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.MONTH, amount);
    }

    /**
     * Add years to the specified date and return a new
     * {@code Date} object.
     *
     * @param date    The date to modify.
     * @param amount  The number of years to add.
     */
    public static Date addYears(Date date, int amount) {
        PreCon.notNull(date);

        return add(date, Calendar.YEAR, amount);
    }


    private static Date add(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }


}
