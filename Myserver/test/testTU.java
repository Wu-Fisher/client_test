package test;

import java.util.Calendar;

public class testTU {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(TimeUnit.calenderToString(calendar));
        String str = TimeUnit.calenderToString(calendar);
        Calendar calendar2 = TimeUnit.stringToCalendar(str);
        System.out.println(TimeUnit.calenderToString(calendar2));

    }

}
