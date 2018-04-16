package com.joyfullkiwi.moneytimer;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class TimerManager extends ContextWrapper {

  public static final int ZERO = 0;
  public static final String TAG = TimerManager.class.getName();

  public interface OnTimerListener {

    void onHandleRunningStatus(String runningStatus);

    void onTick(long time);
  }

  private String runningStatus;

  private long startTime;
  private long elapsedTime;

  private SharedPreferences sPref;
  private Handler handler;
  private Runnable timerTask;

  private OnTimerListener onTimerListener;


  public void setOnTimerListener(OnTimerListener onTimerListener) {
    this.onTimerListener = onTimerListener;
  }

  public OnTimerListener getOnTimerListener() {
    return onTimerListener;
  }

  public TimerManager(Context base) {
    super(base);
    sPref = getSharedPreferences(Const.SHARED_TIMER_NAME, MODE_PRIVATE);

    handler = new Handler();

    timerTask = () -> {

      elapsedTime = (System.currentTimeMillis() - startTime);

      if (getOnTimerListener() != null) {
        getOnTimerListener().onTick(elapsedTime);
      }

      handler.postDelayed(timerTask, 1000);

    };
  }

  private void startTimer() {
    Log.d(TAG, "startTimer");
    handler.postDelayed(timerTask, 1000);
  }

  private void stopTimer() {
    Log.d(TAG, "stopTimer");
    handler.removeCallbacks(timerTask);
  }

  public void setRunningStatus(String runningStatus) {
    this.runningStatus = runningStatus;
    checkRunningStatus();
  }

  private void checkRunningStatus() {

    Log.d(TAG, "checkRunningStatus");

    if (getRunningStatus() == null) return;

    switch (getRunningStatus()) {
      case Const.STATUS_START:
        startTime = System.currentTimeMillis() - getElapsedTime();
        startTimer();
        break;
      case Const.STATUS_PAUSE:
        stopTimer();
        saveTimeAndAction();
        break;
      case Const.STATUS_STOP:
        stopTimer();
        saveTimeAndAction(ZERO);
        if (onTimerListener != null) {
          onTimerListener.onTick(ZERO);
        }
        break;
    }
    if (onTimerListener != null) {
      onTimerListener.onHandleRunningStatus(getRunningStatus());
    }
  }

  private long getPrefTime() {
    return sPref.getLong(Const.TIMER_VALUE, ZERO);
  }

  private String getPrefRunningStatus() {
    return sPref.getString(Const.RUNNING_STATUS, Const.STATUS_STOP);
  }

  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public long getElapsedTime() {
    return elapsedTime;
  }

  private void saveTimeAndAction() {
    saveTimeAndAction(getElapsedTime());
  }

  private void saveTimeAndAction(long value) {
    Log.d(TAG, "saveTimeAndAction: " + value);
    sPref.edit()
        .putString(Const.RUNNING_STATUS, getRunningStatus())
        .putLong(Const.TIMER_VALUE, value)
        .apply();
  }

  public String getRunningStatus() {
    return runningStatus;
  }

  public void saveDataWhenExit() {
    Log.d(TAG, "saveDataWhenExit");
    saveTimeAndAction();
  }

  public void checkSavedData() {
    Log.d(TAG, "checkSavedData: getPrefTime - " + getPrefTime());
    Log.d(TAG, "checkSavedData: getPrefRunningStatus - " + getPrefRunningStatus());
    setElapsedTime(getPrefTime());
    if (onTimerListener != null) {
    onTimerListener.onTick(getElapsedTime());
    onTimerListener.onHandleRunningStatus(getPrefRunningStatus());
    }
    setRunningStatus(getPrefRunningStatus());

  }
}
