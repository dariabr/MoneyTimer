package com.joyfullkiwi.moneytimer;

import static com.joyfullkiwi.moneytimer.Const.TIMER_NOTIFICATION_ID;
import static com.joyfullkiwi.moneytimer.Const.TIMER_PERMISSION;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerService extends Service {

  public static final String TAG = TimerService.class.getName();

  Intent timerIntent;
  private boolean isStartedService = false;
  private Thread timerThread;

  @Override
  public void onCreate() {
    super.onCreate();

    timerIntent = new Intent(Const.TIMER_ACTION);

    timerIntent.putExtra(Const.TIMER_STATUS, Const.STATUS_START);
    sendBroadcast(timerIntent);

    isStartedService = true;

    Log.d(TAG, "onCreate");
  }

  private Notification updateNotification(String text) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    return new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("MoneyTimer")
        .setContentText(text)
        .setContentIntent(PendingIntent.getActivity(this, 0,intent, 0))
        .setOngoing(true)
        .build();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {

    Log.d(TAG,
        "onStartCommand: intent: " + intent.getAction() + " flag: " + flags + " startId" + startId);

    initTimerThread();

    return super.onStartCommand(intent, flags, startId);
  }

  private void initTimerThread() {
    timerThread = new Thread() {
      @Override
      public void run() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:S", Locale.getDefault());

        while (isStartedService) {

          String value = dateFormat.format(new Date(System.currentTimeMillis()));
          timerIntent.putExtra(Const.TIMER_VALUE, value);

          startForeground(Const.TIMER_NOTIFICATION_ID, updateNotification(value));

          sendBroadcast(timerIntent, TIMER_PERMISSION);

        }
      }
    };

    timerThread.start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy");
    stopForeground(true);
    timerIntent.putExtra(Const.TIMER_STATUS, Const.STATUS_STOP);
    sendBroadcast(timerIntent);
    isStartedService = false;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
