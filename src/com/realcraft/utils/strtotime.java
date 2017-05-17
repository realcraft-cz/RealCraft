package com.realcraft.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public final class strtotime {

    private static final List<Matcher> matchers;

    static {
        matchers = new LinkedList<Matcher>();
        matchers.add(new NowMatcher());
        matchers.add(new TomorrowMatcher());
        matchers.add(new YesterdayMatcher());
        // add as many format as you want
    }

    // not thread-safe
    public static void registerMatcher(Matcher matcher) {
        matchers.add(matcher);
    }

    public static interface Matcher {

        public Date tryConvert(String input);
    }

    private static class DateFormatMatcher implements Matcher {

        private final DateFormat dateFormat;

        public DateFormatMatcher(DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        public Date tryConvert(String input) {
            try {
                return dateFormat.parse(input);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    private static class NowMatcher implements Matcher {

        private final Pattern now = Pattern.compile("now");

        public Date tryConvert(String input) {
            if (now.matcher(input).matches()) {
                return new Date();
            } else {
                return null;
            }
        }
    }

    private static class YesterdayMatcher implements Matcher {

        private final Pattern yesterday = Pattern.compile("yesterday");

        public Date tryConvert(String input) {
            if (yesterday.matcher(input).matches()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                return calendar.getTime();
            } else {
                return null;
            }
        }
    }

    private static class TomorrowMatcher implements Matcher {

        private final Pattern tomorrow = Pattern.compile("tomorrow");

        public Date tryConvert(String input) {
            if (tomorrow.matcher(input).matches()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                calendar.add(Calendar.DAY_OF_YEAR, +1);
                return calendar.getTime();
            } else {
                return null;
            }
        }
    }

    public static Date strtotime(String input) {
        for (Matcher matcher : matchers) {
            Date date = matcher.tryConvert(input);

            if (date != null) {
                return date;
            }
        }

        return null;
    }

    private strtotime() {
        throw new UnsupportedOperationException();
    }
}