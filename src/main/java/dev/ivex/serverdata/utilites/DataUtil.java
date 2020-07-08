package dev.ivex.serverdata.utilites;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DataUtil {

    private static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy h:mma");
    public static long PERMANENT = Long.MAX_VALUE;
    private static ThreadLocal SECONDS = ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));
    private static ThreadLocal TRAILING = ThreadLocal.withInitial(() -> new DecimalFormat("0"));

    public static String formatDuration(long input) {
        return DurationFormatUtils.formatDurationWords(input, true, true);
    }

    public static String formatDate(long value) {
        return FORMAT.format(new Date(value));
    }

    public static long getDuration(String input) {
        input = input.toLowerCase();
        if (Character.isLetter(input.charAt(0))) {
            return Long.MAX_VALUE;
        } else {
            long result = 0L;
            StringBuilder number = new StringBuilder();

            for(int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if (Character.isDigit(c)) {
                    number.append(c);
                } else {
                    String str = number.toString();
                    if (Character.isLetter(c) && !str.isEmpty()) {
                        result += convert(Integer.parseInt(str), c);
                        number = new StringBuilder();
                    }
                }
            }

            return result;
        }
    }

    private static long convert(int value, char charType) {
        switch(charType) {
            case 'M':
                return (long)value * TimeUnit.DAYS.toMillis(30L);
            case 'd':
                return (long)value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return (long)value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return (long)value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return (long)value * TimeUnit.SECONDS.toMillis(1L);
            case 'w':
                return (long)value * TimeUnit.DAYS.toMillis(7L);
            case 'y':
                return (long)value * TimeUnit.DAYS.toMillis(365L);
            default:
                return -1L;
        }
    }

    public static String niceTime(int i) {
        int r = i * 1000;
        int sec = r / 1000 % 60;
        int min = r / '\uea60' % 60;
        int h = r / 3600000 % 24;
        return (h > 0 ? (h < 10 ? "0" : "") + h + ":" : "") + (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
    }

    public static String niceTime(long millis, boolean milliseconds) {
        return niceTime(millis, milliseconds, true);
    }

    public static String niceTime(long duration, boolean milliseconds, boolean trail) {
        return milliseconds && duration < TimeUnit.MINUTES.toMillis(1L) ? ((DecimalFormat)(trail ? TRAILING : SECONDS).get()).format((double)duration * 0.001D) + 's' : DurationFormatUtils.formatDuration(duration, (duration >= TimeUnit.HOURS.toMillis(1L) ? "HH:" : "") + "mm:ss");
    }

    public static String formatSimplifiedDateDiff(long date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(date);
        return formatSimplifiedDateDiff(new GregorianCalendar(), calendar);
    }

    public static String formatSimplifiedDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return "now";
        } else {
            if (toDate.after(fromDate)) {
                future = true;
            }

            StringBuilder sb = new StringBuilder();
            int[] types = new int[]{1, 2, 5, 11, 12, 13};
            String[] names = new String[]{"y", "y", "m", "m", "d", "d", "h", "h", "m", "m", "s", "s"};
            int accuracy = 0;

            for(int i = 0; i < types.length && accuracy <= 2; ++i) {
                int diff = dateDiff(types[i], fromDate, toDate, future);
                if (diff > 0) {
                    ++accuracy;
                    sb.append(diff).append(names[i * 2 + (diff > 1 ? 1 : 0)]);
                }
            }

            return sb.length() == 0 ? "now" : sb.toString().trim();
        }
    }

    static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;

        long savedDate;
        for(savedDate = fromDate.getTimeInMillis(); future && !fromDate.after(toDate) || !future && !fromDate.before(toDate); ++diff) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
        }

        fromDate.setTimeInMillis(savedDate);
        --diff;
        return diff;
    }

    public static String millisToRoundedTime(long millis) {
        if (millis == Long.MAX_VALUE) {
            return "Permanent";
        }
        millis += 1L;

        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        if (years > 0) {
            return years + " year" + (years == 1 ? "" : "s");
        } else if (months > 0) {
            return months + " month" + (months == 1 ? "" : "s");
        } else if (weeks > 0) {
            return weeks + " week" + (weeks == 1 ? "" : "s");
        } else if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s");
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
    }

    public static Collection<?extends String> getOnlinePlayersNames() {
        Collection names = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers())
            names.add(player.getName());

        return names;
    }
}