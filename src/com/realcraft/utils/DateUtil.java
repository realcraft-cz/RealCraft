package com.realcraft.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

	private static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);

	public static String getDate(int timestamp){
		return getDate(timestamp,"dd.MM.yyyy HH:mm");
	}

	public static String getDate(int timestamp,String format){
		Date time = new Date((long)timestamp*1000);
		SimpleDateFormat dt = new SimpleDateFormat(format);
		return dt.format(time);
	}

	public static int parseDateDiff(String time, boolean future) throws Exception {
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find())
		{
			if (m.group() == null || m.group().isEmpty())
			{
				continue;
			}
			for (int i = 0; i < m.groupCount(); i++)
			{
				if (m.group(i) != null && !m.group(i).isEmpty())
				{
					found = true;
					break;
				}
			}
			if (found)
			{
				if (m.group(1) != null && !m.group(1).isEmpty())
				{
					years = Integer.parseInt(m.group(1));
				}
				if (m.group(2) != null && !m.group(2).isEmpty())
				{
					months = Integer.parseInt(m.group(2));
				}
				if (m.group(3) != null && !m.group(3).isEmpty())
				{
					weeks = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null && !m.group(4).isEmpty())
				{
					days = Integer.parseInt(m.group(4));
				}
				if (m.group(5) != null && !m.group(5).isEmpty())
				{
					hours = Integer.parseInt(m.group(5));
				}
				if (m.group(6) != null && !m.group(6).isEmpty())
				{
					minutes = Integer.parseInt(m.group(6));
				}
				if (m.group(7) != null && !m.group(7).isEmpty())
				{
					seconds = Integer.parseInt(m.group(7));
				}
				break;
			}
		}
		if (!found)
		{
			throw new Exception("illegalDate");
		}
		Calendar c = new GregorianCalendar();
		if (years > 0)
		{
			c.add(Calendar.YEAR, years * (future ? 1 : -1));
		}
		if (months > 0)
		{
			c.add(Calendar.MONTH, months * (future ? 1 : -1));
		}
		if (weeks > 0)
		{
			c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		}
		if (days > 0)
		{
			c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		}
		if (hours > 0)
		{
			c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		}
		if (minutes > 0)
		{
			c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		}
		if (seconds > 0)
		{
			c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		}
		Calendar max = new GregorianCalendar();
		max.add(Calendar.YEAR, 10);
		if (c.after(max))
		{
			return (int)(max.getTimeInMillis()/1000);
		}
		return (int)(c.getTimeInMillis()/1000);
	}

	public static String formatDateDiff(long date)
	{
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date+1000);
		Calendar now = new GregorianCalendar();
		return formatDateDiff(now, c);
	}

	public static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future)
	{
		int diff = 0;
		long savedDate = fromDate.getTimeInMillis();
		while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
		{
			savedDate = fromDate.getTimeInMillis();
			fromDate.add(type, future ? 1 : -1);
			diff++;
		}
		diff--;
		fromDate.setTimeInMillis(savedDate);
		return diff;
	}


	public static String formatDateDiff(Calendar fromDate, Calendar toDate)
	{
		boolean future = false;
		if (toDate.equals(fromDate))
		{
			return "now";
		}
		if (toDate.after(fromDate))
		{
			future = true;
		}
		StringBuilder sb = new StringBuilder();
		int[] types = new int[]
		{
			Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
		};
		String[] names = new String[]
		{
			("den"), ("dní"), ("hodinu"), ("hodin"), ("minutu"), ("minut"), ("sekundu"), ("sekund")
		};
		int accuracy = 0;
		for (int i = 0; i < types.length; i++)
		{
			if (accuracy > 2)
			{
				break;
			}
			int diff = dateDiff(types[i], fromDate, toDate, future);
			if (diff > 0)
			{
				accuracy++;
				sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
			}
		}
		if (sb.length() == 0)
		{
			return "now";
		}
		return sb.toString().trim();
	}

	private static final String[] timeFormat1 = new String[]{"Pred","sek","min","hod","Vcera","dny"};
	private static final String[] timeFormat2 = new String[]{"Za","sek","min","hod","Zitra","dní"};

	public static String lastTime(int timestamp){
		return lastTime(timestamp,false);
	}

	public static String lastTime(int timestamp,boolean lower){
		String date = "";
		if(timestamp == 0) return "Nikdy";
		int time = (int)(System.currentTimeMillis()/1000)-timestamp;
		boolean past = (time >= 0 ? true : false);
		if(time == 0) return "Prave ted";
		String[] format = (time >= 0 ? timeFormat1 : timeFormat2);
		time = Math.abs(time);
		if(time < 3600){
			int min = (int)Math.floor(time/60)%60;
			int sec = (int)Math.ceil(time%60);
			if(min == 0) date = format[0]+" "+sec+" "+format[1];
			else if(min > 0) date = format[0]+" "+min+" "+format[2];
		}
		else if(time < 86400*2){
			if(past == true){
				int yesterday = (int)(strtotime.strtotime("yesterday").getTime()/1000);
				if(timestamp >= yesterday && timestamp < yesterday+86400) date = format[4]+" v "+getDate(timestamp,"HH:mm");
				else {
					int hod = (int)Math.floor(time/3600);
					if(hod < 24) date = format[0]+" "+hod+" "+format[3];
					else date = format[0]+" 2 dny";
				}
			} else {
				int tomorrow = (int)(strtotime.strtotime("tomorrow").getTime()/1000);
				if(timestamp >= tomorrow && timestamp < tomorrow+86400) date = format[4]+" v "+getDate(timestamp,"HH:mm");
				else {
					int hod = (int)Math.floor(time/3600);
					if(hod < 24) date = format[0]+" "+hod+" "+format[3];
					else date = format[0]+" 2 dny";
				}
			}
		}
		else {
			int day = (int)Math.floor(time/86400);
			date = format[0]+" "+day+" "+format[5];
		}
		if(lower) date = date.toLowerCase();
		return date;
	}
}