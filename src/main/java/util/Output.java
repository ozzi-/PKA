package main.java.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Output {

  private Output() {
  }
  
  public static void output(String out) {
    out = formatOutput(out);
    System.out.println(out); // NOSONAR
  }

  public static void outputError(String out) {
    out = formatOutput(out);
    System.err.println(out); // NOSONAR
  }

  private static String formatOutput(String out) {
    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    Date cal = Calendar.getInstance(TimeZone.getDefault()).getTime();
    out = sdf.format(cal.getTime()) + " | " + out;
    return out;
  }
}
