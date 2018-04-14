package com.joyfullkiwi.moneytimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private Toolbar mTopToolbar;
  private Button btnStart;
  private Button btnPause;
  private Button btnStop;
  private TextView statusTextView;
  private TextView moneyTextView;

  private BroadcastReceiver timerBroadcastReceiver;
  private Intent service;
  private IntentFilter intentFilter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initViews();

    initBroadcast();

    handleListeners();
  }


  private void initViews() {

    mTopToolbar = findViewById(R.id.my_toolbar);
    setSupportActionBar(mTopToolbar);

    btnStart = findViewById(R.id.btnStart);

    btnPause = findViewById(R.id.btnPause);

    btnStop = findViewById(R.id.btnStop);

    statusTextView = findViewById(R.id.status);

    moneyTextView = findViewById(R.id.money);

    statusTextView.setText(TimeUtils.formatTime(0));
    moneyTextView.setText(TimeUtils.formatMoney(8, 0));

  }

  private void initBroadcast() {
    intentFilter = new IntentFilter();

    intentFilter.addAction(Const.COMMON_TIMER_BTN_ACTION);
    intentFilter.addAction(Const.BROADCAST_TIMER_VALUE_ACTION);

    timerBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null) {
          return;
        }

        switch (intent.getAction()) {
          case Const.COMMON_TIMER_BTN_ACTION:
            String timerStatus = intent.getStringExtra(Const.RUNNING_STATUS);

            if (timerStatus.equals(Const.STATUS_START)) {
              btnStart.setEnabled(false);
              btnPause.setEnabled(true);
              btnStop.setEnabled(true);
            }

            if (timerStatus.equals(Const.STATUS_PAUSE)) {
              btnStart.setEnabled(true);
              btnPause.setEnabled(false);
              btnStop.setEnabled(true);
            }

            if (timerStatus.equals(Const.STATUS_STOP)) {
              btnStart.setEnabled(true);
              btnPause.setEnabled(false);
              btnStop.setEnabled(false);
            }


            break;
          case Const.BROADCAST_TIMER_VALUE_ACTION:
            long time = intent.getLongExtra(Const.TIMER_VALUE, 0);
            statusTextView.setText(TimeUtils.formatTime(time));
            moneyTextView.setText(TimeUtils.formatMoney(8,time));
            break;
        }
      }
    };
  }


  private void handleListeners() {
    service = new Intent(MainActivity.this, TimerService.class);

    btnStart.setOnClickListener(v -> {
      service.setAction(Const.STATUS_START);
      startService(service);
    });

    btnPause.setOnClickListener(v -> {
      service.setAction(Const.STATUS_PAUSE);
      startService(service);
    });

    btnStop.setOnClickListener(v -> {
      service.setAction(Const.STATUS_STOP);
      startService(service);
      statusTextView.setText(TimeUtils.formatTime(0));
      moneyTextView.setText(TimeUtils.formatMoney(8, 0));
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    registerReceiver(timerBroadcastReceiver, intentFilter);
  }

  @Override
  protected void onStop() {
    super.onStop();
    unregisterReceiver(timerBroadcastReceiver);
  }

  // Menu icons are inflated just as they were with actionbar
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }


}
