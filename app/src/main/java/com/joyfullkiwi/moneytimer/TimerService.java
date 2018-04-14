package com.joyfullkiwi.moneytimer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerService extends Service {

  public static final String TAG = TimerService.class.getName();

  Intent timerBtnIntent;

  private Intent timerValueIntent;

  private long time;
  private boolean isRunning;
  private Thread timerThread;

  @Override
  public void onCreate() {
    super.onCreate();

    timerBtnIntent = new Intent(Const.COMMON_TIMER_BTN_ACTION);
    timerValueIntent = new Intent(Const.BROADCAST_TIMER_VALUE_ACTION);

    Log.d(TAG, "onCreate");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction() != null) {
      switch (intent.getAction()) {
        case Const.STATUS_START:
          isRunning = true;
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_START);
          initCounterTime();
          break;
        case Const.STATUS_PAUSE:
          killThread();
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_PAUSE);
          break;
        case Const.STATUS_STOP:
          killThread();
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_STOP);
          stopForeground(true);
          stopSelf();
          break;
      }

      sendBroadcast(timerBtnIntent);
    } else {
      stopSelf();
    }

    return START_STICKY;
  }

  private void killThread() {
    isRunning = false;
    try {
      timerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void initCounterTime() {
    timerThread = new Thread() {
      @Override
      public void run() {
          while (isRunning) {

            timerValueIntent.putExtra(Const.TIMER_VALUE, time);

            sendBroadcast(timerBtnIntent);
            sendBroadcast(timerValueIntent);

            startForeground(Const.NOTIFICATION_ID, updateNotification(TimeUtils.formatTime(time)));

            time++;
          }
      }
    };
    timerThread.start();
  }

  private Notification updateNotification(String text) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("MoneyTimer")
        .setContentText(text)
        .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
        .setOngoing(true)
        .build();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
