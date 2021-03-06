/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Process;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.Calendar;
import org.joda.time.LocalDate;

/**
 *
 * @author raheelansari
 */
public class TEST {
    public static void main(String[] args) {

    // Before Java 8
    Date today = new Date(0);
    Calendar cal = Calendar.getInstance();
    cal.setTime(today); // don't forget this if date is arbitrary
    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 being Sunday
    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
    int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

    int month = cal.get(Calendar.MONTH); // 0 being January
    int year = cal.get(Calendar.YEAR);

    System.out.println("current date : " + today);
    System.out.println("dayOfWeek from date in Java 6 : " + dayOfWeek);
    System.out.println("dayOfMonth from a date in Java 7 : " + dayOfMonth);
    System.out.println("dayOfYear from a date in Java 1.6 : " + dayOfYear);
    System.out.println("month from a date in Java 1.7 : " + month);
    System.out.println("year from date in JDK 6 : " + year);

    // In Java 8
    LocalDate currentDate = LocalDate.now();
//    DayOfWeek dow = currentDate.getDayOfWeek();
    int dom = currentDate.getDayOfMonth();
    int doy = currentDate.getDayOfYear();
    int m = currentDate.getMonthOfYear();
    int y = currentDate.getYear();

    System.out.println("current local date : " + currentDate);
//    System.out.println("dayOfWeek from a date in Java 8 : " + dow);
    System.out.println("dayOfMonth from date in JDK 8: " + dom);
    System.out.println("dayOfYear from a date in Java SE 8 : " + doy);
    System.out.println("month from a date in Java 1.8 : " + m);
    System.out.println("year from date in JDK 1.8 : " + y);
  }

}
