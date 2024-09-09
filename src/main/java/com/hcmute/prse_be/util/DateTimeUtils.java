package com.hcmute.prse_be.util;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class DateTimeUtils {
    public static final long MINMILISECONDS = Long.parseLong("-2208988800000");
    public static final java.util.Date MINDATE = ConvertUtils.toDateTime(new java.util.Date(MINMILISECONDS));

    public static int compareDate(java.util.Date date1, java.util.Date date2)
    {
        return ConvertUtils.toDateSql(date1).compareTo(ConvertUtils.toDateSql(date2));
    }

    public static int compareDateTime(java.util.Date date1, java.util.Date date2)
    {
        return date1.compareTo(date2);
    }

    public static java.util.Date getNow()
    {
        return getDateTime(new java.util.Date());
    }

    public static String getNow(String format)
    {
        return getNow(format, "UTC");
    }

    public static String getNow(String format, String timezone)
    {
        return ConvertUtils.toString(new java.util.Date(), format, timezone);
    }

    public static long getNanoSecondNow()
    {
        return System.nanoTime();
    }

    public static long getMilisecondsNow()
    {
        return getNow().getTime();
    }

    public static long getMilisecondsMin()
    {
        return MINMILISECONDS;
    }

    public static long getMiliseconds(java.util.Date date)
    {
        return ConvertUtils.toDateTime(date).getTime();
    }

    public static long getMiliseconds(int year, int month, int day)
    {
        return getMiliseconds(year, month, day, "UTC");
    }

    public static long getMiliseconds(int year, int month, int day, String timezone)
    {
        return getMiliseconds(year, month, day, 0, 0, 0, timezone);
    }

    public static long getMiliseconds(int year, int month, int day, int hour, int minute, int second, String timezone)
    {
        try
        {
            String data = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d H:m:s");
            formatter.setTimeZone(TimeZone.getTimeZone(timezone));
            java.util.Date utilDate = formatter.parse(data);
            return utilDate.getTime();
        }
        catch (ParseException ex) {}
        return 0L;
    }

    public static Timestamp getTimestampSql(java.util.Date dt)
    {
        if (dt.getTime() < 0L) {
            return new Timestamp(Long.parseLong("2678400000"));
        }
        return Timestamp.valueOf(toString(dt));
    }

    public static java.sql.Date getDateSql(java.util.Date dt)
    {
        return new java.sql.Date(dt.getTime());
    }

    public static java.util.Date getDateTime(Timestamp timestamp)
    {
        if (timestamp == null) {
            return null;
        }
        return new java.util.Date(timestamp.getTime());
    }

    public static java.util.Date getDateTime(long miliseconds)
    {
        return getDateTime(new java.util.Date(miliseconds));
    }

    public static java.util.Date getDateTime(java.util.Date dt)
    {
        return getDateTime(dt, "yyyy-MM-dd HH:mm:ss.SSS", "UTC");
    }

    public static java.util.Date getDateTime(java.util.Date dt, String format)
    {
        return getDateTime(dt, format, "UTC");
    }

    public static java.util.Date getDateTime(java.util.Date dt, String format, String timeZone)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            return formatter.parse(formatter.format(dt));
        }
        catch (Exception e) {}
        return null;
    }

    public static java.util.Date getDateTime(java.sql.Date dt)
    {
        return getDateTime(dt, "UTC");
    }

    public static java.util.Date getDateTime(java.sql.Date dt, String timeZone)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            return formatter.parse(formatter.format(dt));
        }
        catch (Exception e) {}
        return null;
    }

    public static java.util.Date getDateTime(int year, int month, int day)
    {
        return getDateTime(getMiliseconds(year, month, day));
    }

    public static java.util.Date getDateTime(String data)
    {
        return getDateTime(data, "yyyy-MM-dd HH:mm:ss.SSS", "UTC");
    }

    public static java.util.Date getDateTime(String data, String format)
    {
        return getDateTime(data, format, "UTC");
    }

    public static java.util.Date getDateTime(String datetime, String format, String timeZone)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            return formatter.parse(datetime);
        }
        catch (Exception e) {}
        return null;
    }

    public static String toString(java.util.Date dt)
    {
        return toString(dt, "yyyy-MM-dd HH:mm:ss.SSS", "UTC");
    }

    public static String toString(java.util.Date dt, String format)
    {
        return toString(dt, format, "UTC");
    }

    public static String toString(java.util.Date dt, String format, String timeZone)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            return formatter.format(dt);
        }
        catch (Exception e) {}
        return null;
    }

    public static java.util.Date addWeeks(int value)
    {
        return DateUtils.addWeeks(getNow(), value);
    }

    public static java.util.Date addWeeks(java.util.Date d, int value)
    {
        return DateUtils.addWeeks(d, value);
    }

    public static java.util.Date addYears(int value)
    {
        return DateUtils.addYears(getNow(), value);
    }

    public static java.util.Date addYears(java.util.Date d, int value)
    {
        return DateUtils.addYears(d, value);
    }

    public static java.util.Date addMonths(int value)
    {
        return DateUtils.addMonths(getNow(), value);
    }

    public static java.util.Date addMonths(java.util.Date d, int value)
    {
        return DateUtils.addMonths(getNow(), value);
    }

    public static java.util.Date addDays(int value)
    {
        return DateUtils.addDays(getNow(), value);
    }

    public static java.util.Date addDays(java.util.Date d, int value)
    {
        return DateUtils.addDays(d, value);
    }

    public static java.util.Date addHours(int value)
    {
        return DateUtils.addHours(getNow(), value);
    }

    public static java.util.Date addHours(java.util.Date d, int value)
    {
        return DateUtils.addHours(d, value);
    }

    public static java.util.Date addMinutes(int value)
    {
        return DateUtils.addMinutes(getNow(), value);
    }

    public static java.util.Date addMinutes(java.util.Date d, int value)
    {
        return DateUtils.addMinutes(d, value);
    }

    public static java.util.Date addSeconds(int value)
    {
        return DateUtils.addSeconds(getNow(), value);
    }

    public static java.util.Date addSeconds(java.util.Date d, int value)
    {
        return DateUtils.addSeconds(d, value);
    }

    public static java.util.Date addMilliseconds(int value)
    {
        return DateUtils.addMilliseconds(getNow(), value);
    }

    public static java.util.Date addMilliseconds(java.util.Date d, int value)
    {
        return DateUtils.addMilliseconds(d, value);
    }

    public static int getDay(java.util.Date date)
    {
        String value = toString(date);
        return ConvertUtils.toInt(value.substring(8, 10));
    }

    public static int getMonth(java.util.Date date)
    {
        String value = toString(date);
        return ConvertUtils.toInt(value.substring(5, 7));
    }

    public static int getYear(java.util.Date date)
    {
        String value = toString(date);
        return ConvertUtils.toInt(value.substring(0, 4));
    }

    public static java.util.Date format(String value, String format, java.util.Date defValue)
    {
        try
        {
            return new SimpleDateFormat(format).parse(value);
        }
        catch (Exception ex) {}
        return defValue;
    }

    static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    static final String formatDate = "dd/MM/yyyy";

    public static java.util.Date getBeginDate(java.util.Date d)
    {
        return format(formatter.format(d), "dd/MM/yyyy", d);
    }

}
