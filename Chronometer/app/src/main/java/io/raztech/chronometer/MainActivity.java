package io.raztech.chronometer;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int count;
    private boolean counting;

    private TextView timeView;
    private Button startStopButton;
    private Button resetButton;

    private SharedPreferences sharedPref;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = 0;

        timeView = (TextView) findViewById(R.id.timeView);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        handler = new Handler();

        Log.d("onCreate", "was called");
    }

    public void onStartStopClicked(View v) {
        if (counting) {
            counting = false;
            startStopButton.setText("Start");
        } else {
            counting = true;
            startStopButton.setText("Stop");
            scheduleNextTick();
        }
    }

    public void onResetClicked(View v) {
        reset();
        updateCountView();
    }

    private void updateCountView() {
        timeView.setText(String.valueOf(count));
    }

    private void increment() {
        count += 500;
    }

    private void reset() { count = 0; }

    private void scheduleNextTick() {
        if (counting) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onTick();
                }
            }, 500);
        }
    }

    private void onTick() {
        increment();
        updateCountView();

        scheduleNextTick();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("counting", counting);
        outState.putString("startStopButton", startStopButton.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        startStopButton.setText(savedInstanceState.getString("startStopButton"));

        counting = savedInstanceState.getBoolean("counting");
        scheduleNextTick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", "was called");
    }

    @Override
    protected void onResume() {
        super.onResume();

        count = sharedPref.getInt("count", 0);
        updateCountView();

        Log.d("onResume", "was called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt("count", count);
        editor.commit();

        Log.d("onPause", "was called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        counting = false;
        startStopButton.setText("Start");
        Log.d("onStop", "was called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "was called");
    }
}
