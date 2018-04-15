package com.joyfullkiwi.moneytimer;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TimerService extends Service {

  public static final String TAG = "Service";

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
    Log.d(TAG, "onStartCommand");
    if (intent.getAction() != null) {
      switch (intent.getAction()) {
        case Const.STATUS_START:
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_START);
          startThread();
          break;
        case Const.STATUS_PAUSE:
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_PAUSE);
          killThread();
          break;
        case Const.STATUS_STOP:
          timerBtnIntent.putExtra(Const.RUNNING_STATUS, Const.STATUS_STOP);
          killThread();
          //Отправляем после остановки 0 значение
          timerValueIntent.putExtra(Const.TIMER_VALUE, 0);
          sendBroadcast(timerValueIntent);
          //убераем из главной задачи поток
          stopForeground(true);
          //полностью останавливаем его
          stopSelf();
          break;
      }
      //после нажатия на кнопки, отправляем сразу текущее состояние в активити
      sendBroadcast(timerBtnIntent);
    } else {
      stopSelf();
    }

    return START_STICKY;
  }

  private void startThread() {
    isRunning = true;
    timerThread = new Thread() {
      @Override
      public void run() {
        while (isRunning) {

          timerValueIntent.putExtra(Const.TIMER_VALUE, time);

          //постоянно отправляем текущее состояние и значение времени
          sendBroadcast(timerBtnIntent);
          sendBroadcast(timerValueIntent);

          startForeground(Const.NOTIFICATION_ID, updateNotification(TimeUtils.formatMoney(8, time)));

          time += 10;
        }
      }
    };
    timerThread.start();
  }

  private void killThread() {
    boolean retry = true;
    isRunning = false;
    while (retry) {
      try {
        timerThread.join();
        retry = false;
      } catch (InterruptedException e) {
      }
    }
  }

  private Notification updateNotification(String text) {
    return new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("MoneyTimer")
        .setContentText(text)
        .setOngoing(true)
        .setAutoCancel(false)
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
