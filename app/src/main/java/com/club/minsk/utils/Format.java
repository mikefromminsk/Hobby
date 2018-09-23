package com.club.minsk.utils;

import com.club.minsk.R;
import com.club.minsk.db.Owners;
import com.club.minsk.db.tables.OwnersTable;
import com.club.minsk.db.tables.StringsTable;
import com.club.minsk.db.Strings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {

    final static String hours_and_minutes = " HH:mm";

    public static String dateFormat(Long time_in_seconds) {
        Long time_in_milliseconds = time_in_seconds * 1000L;
        String format = "";

        Calendar eventDate = Calendar.getInstance();
        eventDate.setTimeInMillis(time_in_milliseconds);

        if (format.equals("")) {
            Calendar today = Calendar.getInstance();
            if (eventDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    eventDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                format = Strings.get(R.string.today_in);
                if (!(eventDate.get(Calendar.HOUR_OF_DAY) == 0 && eventDate.get(Calendar.MINUTE) == 0))
                    format += " " + Strings.get(R.string.in_time) + hours_and_minutes;
            } else {
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);
                if (eventDate.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                        eventDate.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                    format = Strings.get(R.string.yesterday_in);
                    if (!(eventDate.get(Calendar.HOUR_OF_DAY) == 0 && eventDate.get(Calendar.MINUTE) == 0))
                        format += " " + Strings.get(R.string.in_time) + hours_and_minutes;
                } else {
                    Calendar tomorrow = Calendar.getInstance();
                    tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                    if (eventDate.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                            eventDate.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                        format = Strings.get(R.string.tomorrow_in);
                        if (!(eventDate.get(Calendar.HOUR_OF_DAY) == 0 && eventDate.get(Calendar.MINUTE) == 0))
                            format += " " + Strings.get(R.string.in_time) + hours_and_minutes;
                    }/* else {

                        if (eventDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                eventDate.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR) &&
                                eventDate.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR)) {
                            switch (eventDate.get(Calendar.DAY_OF_WEEK)) {
                                case Calendar.MONDAY:
                                    format = Strings.get(R.string.monday_in);
                                    break;
                                case Calendar.TUESDAY:
                                    format = Strings.get(R.string.tuesday_in);
                                    break;
                                case Calendar.WEDNESDAY:
                                    format = Strings.get(R.string.wednesday_in);
                                    break;
                                case Calendar.THURSDAY:
                                    format = Strings.get(R.string.trusday_in);
                                    break;
                                case Calendar.FRIDAY:
                                    format = Strings.get(R.string.fryday_in);
                                    break;
                                case Calendar.SATURDAY:
                                    format = Strings.get(R.string.saturday_in);
                                    break;
                                case Calendar.SUNDAY:
                                    format = Strings.get(R.string.sunday_in);
                                    break;
                            }

                            if (!(eventDate.get(Calendar.HOUR_OF_DAY) == 0 && eventDate.get(Calendar.MINUTE) == 0))
                                format += " " + Strings.get(R.string.in_time) + hours_and_minutes;
                        }
                    }*/
                }
            }
        }
        if (format.equals("")) {
            String month = "";
            switch (eventDate.get(Calendar.MONTH)) {
                case Calendar.JANUARY:
                    month = Strings.get(R.string.JANUARY);
                    break;
                case Calendar.FEBRUARY:
                    month = Strings.get(R.string.FEBRUARY);
                    break;
                case Calendar.MARCH:
                    month = Strings.get(R.string.MARCH);
                    break;
                case Calendar.APRIL:
                    month = Strings.get(R.string.APRIL);
                    break;
                case Calendar.MAY:
                    month = Strings.get(R.string.MAY);
                    break;
                case Calendar.JUNE:
                    month = Strings.get(R.string.JUNE);
                    break;
                case Calendar.JULY:
                    month = Strings.get(R.string.JULY);
                    break;
                case Calendar.AUGUST:
                    month = Strings.get(R.string.AUGUST);
                    break;
                case Calendar.SEPTEMBER:
                    month = Strings.get(R.string.SEPTEMBER);
                    break;
                case Calendar.OCTOBER:
                    month = Strings.get(R.string.OCTOBER);
                    break;
                case Calendar.NOVEMBER:
                    month = Strings.get(R.string.NOVEMBER);
                    break;
                case Calendar.DECEMBER:
                    month = Strings.get(R.string.DECEMBER);
                    break;
            }
            format = "dd " + month + " yyyy";
            if (!(eventDate.get(Calendar.HOUR_OF_DAY) == 0 && eventDate.get(Calendar.MINUTE) == 0))
                format += " " + Strings.get(R.string.in_time) + hours_and_minutes;
        }
        return new SimpleDateFormat(format).format(new Date(time_in_milliseconds));
    }

    public static String distFormat(Long dist) {
        if (dist == null)
            return "";
        if (dist.equals(-1L))
            return "";
        if (dist < 10)
            return Strings.get(R.string.here);
        if (dist < 1000)
            return dist + " " + Strings.get(R.string.meters);
        else
            return String.format("%.1f", (double) dist / 1000.0) + " " + Strings.get(R.string.kilometers);
    }


    public static String zodiakFormat(Long owner_birthdate) {
        Date bdate = new Date(owner_birthdate * 1000);
        String zodiak = "";
        int day = bdate.getDay();
        int month = bdate.getMonth() + 1;
        int year = bdate.getYear();    // дата рождения
        // Вычисляем знак зодиака
        if ((month == 3 && day >= 21) || (month == 4 && day <= 20))
            zodiak = Strings.get(R.string.aries);
        else if ((month == 4 && day >= 21) || (month == 5 && day <= 20))
            zodiak = Strings.get(R.string.taurus);
        else if ((month == 5 && day >= 21) || (month == 6 && day <= 21))
            zodiak = Strings.get(R.string.twins);
        else if ((month == 6 && day >= 22) || (month == 7 && day <= 22))
            zodiak = Strings.get(R.string.cancer); // :)
        else if ((month == 7 && day >= 23) || (month == 8 && day <= 23))
            zodiak = Strings.get(R.string.lion);
        else if ((month == 8 && day >= 24) || (month == 9 && day <= 23))
            zodiak = Strings.get(R.string.virgin);
        else if ((month == 9 && day >= 24) || (month == 10 && day <= 22))
            zodiak = Strings.get(R.string.balance);
        else if ((month == 10 && day >= 23) || (month == 11 && day <= 22))
            zodiak = Strings.get(R.string.scorpio);
        else if ((month == 11 && day >= 23) || (month == 12 && day <= 21))
            zodiak = Strings.get(R.string.sagittarius);
        else if ((month == 12 && day >= 22) || (month == 1 && day <= 20))
            zodiak = Strings.get(R.string.capricorn);
        else if ((month == 1 && day >= 21) || (month == 2 && day <= 19))
            zodiak = Strings.get(R.string.aquarius);
        else if ((month == 2 && day >= 20) || (month == 3 && day <= 20))
            zodiak = Strings.get(R.string.fish);

        return zodiak;
    }

/*



    // перевод с полом
    public static String relationFormat(boolean man, Integer relation) {
        if (relation == null)
            return "";
        switch (relation) {
            case 1:
                return (man ? Strings.get(R.string.man_not_married) : Strings.get(R.string.woman_not_married));
            case 2:
                return (man ? Strings.get(R.string.man_best_frend) : Strings.get(R.string.woman_best_frend));
            case 3:
                return (man ? Strings.get(R.string.man_engaged) : Strings.get(R.string.woman_engaged));
            case 4:
                return (man ? Strings.get(R.string.man_married) : Strings.get(R.string.woman_married));
            case 5:
                return Strings.get(R.string.complicated);
            case 6:
                return Strings.get(R.string.active_search);
            case 7:
                return (man ? Strings.get(R.string.man_love) : Strings.get(R.string.woman_love));
        }
        return "";
    }


    public static String priceFormat(int price) {
        return price + " " + Strings.get(R.string.currency);
    }

    public static String goldFormat(int gold) {
        return Math.abs(gold) + " " + Strings.get(R.string.gold);
    }


    public static String friendFromat(int friendCount) {
        String friendCountStr = "" + friendCount;
        String resultStr = friendCountStr + " ";
        switch (friendCountStr.charAt(friendCountStr.length() - 1)) {
            case '1':
                resultStr += Strings.get(R.string.one_friend);
                break;
            case '2':
            case '3':
            case '4':
                resultStr += Strings.get(R.string.man_friend);
                break;
            default:
                resultStr += Strings.get(R.string.many_friends);
                break;
        }
        return resultStr;
    }

    public static String effectFormat(Integer sum_effect_value) {
        if (sum_effect_value == null || sum_effect_value == 0)
            return "";
        return (sum_effect_value > 0 ? "+" : "") + sum_effect_value;
    }
*/

    private static Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


    public static String extractUrl(String text) {
        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            return text.substring(matchStart, matchEnd);
        }
        return null;
    }

    public static String onlineFormat(Long login_time) {
        if (login_time == null)
            return "";
        if (new Date().getTime() / 1000 - login_time < 20 * 60)
            return Strings.get(R.string.online);
        else
            return Strings.get(R.string.old_online) + " " + dateFormat(login_time);
    }


    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String bdateFormat(Long bdate) {
        int diffYears = (int) (Math.abs(bdate - new Date().getTime() / 1000) / (60 * 60 * 24 * 365));
        if (bdate < 1)
            return "";
        return timeFormat(StringsTable.STRING_YEAR_PREFIX, diffYears);
    }


    public static String timeFormat(String prefix, int time_count) {
        String time_name = Strings.get(prefix + time_count);
        String time = "" + time_count;
        if (time_name.contains(Strings.unset))
            time_name = Strings.get(prefix + "x" + time.charAt(time.length() - 1));
        if (time_name.contains(Strings.unset))
            time_name = Strings.get(prefix + "xx");
        return time_count + " " + time_name;
    }

    public static String dayFormat(int days_count) {
        return timeFormat(StringsTable.STRING_DAY_PREFIX, days_count);
    }

    public static String hourFormat(int hour_count) {
        return timeFormat(StringsTable.STRING_HOUR_PREFIX, hour_count);
    }

    public static String minutsFormat(int minuts_count) {
        return timeFormat(StringsTable.STRING_MIN_PREFIX, minuts_count);
    }

    public static String secondsFormat(int seconds_count) {
        return timeFormat(StringsTable.STRING_SEC_PREFIX, seconds_count);
    }


    public static String chatName(List<Long> owners) {
        String chatName = "";
        for (Long owner_id : owners) {
            OwnersTable.Owner owner = Owners.get(owner_id);
            if (!owner.owner_id.equals(Owners.self().owner_id))
                chatName += "," + owner.owner_name;
        }
        if (chatName.isEmpty())
            return Owners.self().owner_name;
        return chatName.substring(1);
    }
}
