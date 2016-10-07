package io.raztech.chronometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int count;

    TextView timeView;
    Button startStopButton;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = 0;

        timeView = (TextView) findViewById(R.id.timeView);
        startStopButton = (Button) findViewById(R.id.startStopButton);
        resetButton = (Button) findViewById(R.id.resetButton);
    }

    public void onStartStopClicked(View v) {
        increment();
        updateCountView();
    }

    public void onResetClicked(View v) {
        reset();
        updateCountView();
    }

    private void updateCountView() {
        timeView.setText(String.valueOf(count));
        Log.d("showCount", timeView.getText().toString());
    }

    private void increment() {
        count++;
    }

    private void reset() {
        count = 0;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", count);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt("count");
        updateCountView();
    }
}
