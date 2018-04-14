package com.joyfullkiwi.moneytimer;

import java.util.Locale;

public class TimeUtils {

  public static String formatTime(long time) {
    long hours = (time / 60 / 60 / 1000);
    long minutes = (time / 60 / 1000) % 60;
    long seconds = (time / 1000) % 60;
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
  }

  /*public static String formatTime(long time) {
    long hours = (time / 60 /60 / 1000) % 24;
    long minutes = (time / 60 / 1000) % 60;
    long seconds = (time % 3600) / 100;
    long millis = time % 100;
    return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
  }*/

  public static String formatMoney(double moneyDelta, long time) {
    double result = (time / 1000) * moneyDelta / 3600.0;
    return String.format(Locale.getDefault(), "%,.2f", result) + "$";
  }

}
