package com.joyfullkiwi.moneytimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import com.joyfullkiwi.moneytimer.TimerManager.OnTimerListener;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

  private Toolbar mTopToolbar;
  private Button btnStart;
  private Button btnPause;
  private Button btnStop;
  private TextView statusTextView;
  private TextView moneyTextView;

  private TimerManager timerManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    timerManager = new TimerManager(this);

    timerManager.setOnTimerListener(new OnTimerListener() {
      @Override
      public void onHandleRunningStatus(String runningStatus) {
        if (runningStatus.equals(Const.STATUS_START)) {
          btnStart.setEnabled(false);
          btnPause.setEnabled(true);
          btnStop.setEnabled(true);
        }

        if (runningStatus.equals(Const.STATUS_PAUSE)) {
          btnStart.setEnabled(true);
          btnPause.setEnabled(false);
          btnStop.setEnabled(true);
        }

        if (runningStatus.equals(Const.STATUS_STOP)) {
          btnStart.setEnabled(true);
          btnPause.setEnabled(false);
          btnStop.setEnabled(false);
        }
      }

      @Override
      public void onTick(long time) {
        statusTextView.setText(TimeUtils.formatTime(time));
        moneyTextView.setText(TimeUtils.formatMoney(8, time));
      }
    });

    initViews();

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


  private void handleListeners() {
    btnStart.setOnClickListener(v -> timerManager.setRunningStatus(Const.STATUS_START));

    btnPause.setOnClickListener(v -> timerManager.setRunningStatus(Const.STATUS_PAUSE));

    btnStop.setOnClickListener(v -> timerManager.setRunningStatus(Const.STATUS_STOP));
  }

  @Override
  protected void onStart() {
    timerManager.checkSavedData();
    super.onStart();
  }

  @Override
  protected void onStop() {
    super.onStop();
    timerManager.saveDataWhenExit();
  }

  // Menu icons are inflated just as they were with actionbar
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }


}
