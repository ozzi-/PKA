package main.java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class CLIUtil {

  public static final String LISTENING = "LISTENING";
  public static final String SUCCESS = "SUCCESS";

  private CLIUtil() {
  }

  public static Optional<String> killProcess(Integer pid) throws IOException {
    Runtime rt = Runtime.getRuntime();
    String[] commands = {"taskkill", "/PID", String.valueOf(pid), "/F"};
    Output.output("Running command " + String.join(" ", commands));
    Process proc = rt.exec(commands);
    BufferedReader stdInput = getBufferedReader(proc.getInputStream());
    BufferedReader stdError = getBufferedReader(proc.getErrorStream());
    String s;
    if ((s = stdInput.readLine()) != null) {
      Output.output("Taskkill stdInput: " + s);
      if (s.contains(SUCCESS)) {
        return Optional.empty();
      } else {
        return Optional.of(s);
      }
    }
    if ((s = stdError.readLine()) != null) {
      Output.output("Taskkill stdError: " + s);
      return Optional.of(s);
    }
    return Optional.of("No output - this should not happen");
  }


  public static Optional<Integer> runNetStat(String port) throws IOException {
    Runtime rt = Runtime.getRuntime();
    String[] commands = {"netstat", "-ano"};
    Output.output("Running command " + String.join(" ", commands));
    Process proc = rt.exec(commands);
    BufferedReader stdInput = getBufferedReader(proc.getInputStream());
    String s;
    while ((s = stdInput.readLine()) != null) {
      if (s.contains(":" + port) && s.contains(LISTENING)) {
        Output.output("Netstat: " + s);
        String pidString = s.substring(s.indexOf(LISTENING) + LISTENING.length());
        pidString = pidString.trim();
        return Optional.of(Integer.valueOf(pidString));
      }
    }
    return Optional.empty();
  }

  private static BufferedReader getBufferedReader(InputStream proc) {
    return new BufferedReader(new InputStreamReader(proc));
  }
}
