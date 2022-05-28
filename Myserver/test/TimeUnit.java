package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.print.CancelablePrintJob;

public class TimeUnit {
    public static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    public static String calenderToString(Calendar calendar) {
        return sdf.format(calendar.getTime());
    }

    public static Calendar stringToCalendar(String str) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
