package com.joyfullkiwi.moneytimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private Toolbar mTopToolbar;
  private Button btnStart;
  private Button btnStop;
  private TextView textView;

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

    mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(mTopToolbar);

    btnStart = (Button) findViewById(R.id.btnStart);

    btnStop = (Button) findViewById(R.id.btnStop);

    textView = (TextView) findViewById(R.id.status);
  }

  private void initBroadcast() {
    intentFilter = new IntentFilter();

    intentFilter.addAction(Const.TIMER_BTN_ACTION);
    intentFilter.addAction(Const.TIMER_VALUE_ACTION);

    timerBroadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null) {
          return;
        }

        switch (intent.getAction()) {
          case Const.TIMER_BTN_ACTION:
            String timerStatus = intent.getStringExtra(Const.TIMER_STATUS);

            btnStart.setEnabled(timerStatus.equals(Const.STATUS_STOP));
            btnStop.setEnabled(timerStatus.equals(Const.STATUS_START));

            break;
          case Const.TIMER_VALUE_ACTION:
            String timerValue = intent.getStringExtra(Const.TIMER_VALUE);
            textView.setText(timerValue);
            break;
        }
      }
    };
  }


  private void handleListeners() {
    service = new Intent(MainActivity.this, TimerService.class);

    btnStart.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startService(service);
      }
    });

    btnStop.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO лучше посылать intent в серсис с флагом stop и внутри него остонавливать всю работу
        //https://gist.github.com/sunmeat/c7e824f9c1e83c85e987c70e1ef8bb35
        stopService(service);
      }
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
