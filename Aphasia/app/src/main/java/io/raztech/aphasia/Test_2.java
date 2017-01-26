package io.raztech.aphasia;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.raztech.aphasia.services.AsyncResponse;
import io.raztech.aphasia.services.Test2Loader;

public class Test_2 extends AppCompatActivity implements AsyncResponse {

    private List<File> images;
    private String name;
    private int counter;
    private JSONArray jsonAnswerArray;

    private ImageView btnStart;
    private ImageView btnStop;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_2);

        counter = 0;
        jsonAnswerArray = new JSONArray();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Log.i("name", name);

        btnStart = (ImageView) findViewById(R.id.btnStart);
        btnStop = (ImageView) findViewById(R.id.btnStop);

        btnStop.setEnabled(false);

        //data
        File rootDataDir = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/test_2");
        Log.d("Path", rootDataDir.toString());

        if(!rootDataDir.exists()) {
            Toast.makeText(this, "Test data is missing from external storage", Toast.LENGTH_LONG).show();
            return;
        } else {
            File[] dirFiles = rootDataDir.listFiles();
            Log.d("files", dirFiles.toString());
            //getImageList(rootDataDir);

            Log.d("path", rootDataDir.getAbsolutePath());
            new Test2Loader(this, this).execute(rootDataDir.getAbsolutePath());
        }

    }

    @Override
    public void processFinishTest1(Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> output) {}

    @Override
    public void processFinishTest2(List<File> output) {
        Log.d("output", output.toString());
        images = output;

        showNextQuestion();
    }

    private void showNextQuestion() {
        btnStart.setEnabled(true);

        if(counter >= images.size()) {
            //testDone();
            Log.d("showNextQuestion", "Done");
            return;
        }

        ImageView jpgView = (ImageView)findViewById(R.id.img);
        BitmapDrawable d = new BitmapDrawable(getResources(), images.get(counter).getAbsolutePath());
        jpgView.setImageDrawable(d);

        counter++;
    }

    private void testDone() {

        File resultDir = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/results/" + name.replaceAll("\\s+","_"));
        File resultFile = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/results/" + name.replaceAll("\\s+","_") +
                "/" + name.replaceAll("\\s+","_") + "_test1.json");
        JSONObject jsonObject = new JSONObject();

        boolean success = true;
        if (!resultDir.exists()) {
            success = resultDir.mkdirs();
        }
        if (success) {
            try {
                jsonObject.put("name", name);
                jsonObject.put("test", "1");
                jsonObject.put("answers", jsonAnswerArray);
                Log.d("jsonobj", jsonObject.toString());

                FileWriter file = new FileWriter(resultFile);
                file.write(jsonObject.toString(4));
                file.flush();
                file.close();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }
        finish();
    }

    protected void onStartClicked(View v) {
        btnStart.setEnabled(false);

        File resultDir = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/results/" + name.replaceAll("\\s+","_"));
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(this.getExternalFilesDir(null).getAbsolutePath() + "/results/" + name.replaceAll("\\s+","_") + "/" + (counter-1) + "_recording.3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("onStartClicked", "prepare() failed");
        }

        mRecorder.start();
        Toast.makeText(this, "Started recording...", Toast.LENGTH_SHORT).show();

        btnStop.setEnabled(true);
    }

    protected void onStopClicked(View v) {
        btnStop.setEnabled(false);

        mRecorder.stop();
        mRecorder.release();
        Toast.makeText(this, "Stopped recording...", Toast.LENGTH_SHORT).show();

        check();

        showNextQuestion();
    }

    private void check() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(this.getExternalFilesDir(null).getAbsolutePath() + "/results/" + name.replaceAll("\\s+","_") + "/" + (counter-1) + "_recording.3gp");
            mPlayer.prepare();
            Toast.makeText(this, "Checking recorded audio for 10 seconds", Toast.LENGTH_SHORT).show();
            mPlayer.start();

            Thread.sleep(10000);
            mPlayer.stop();
            mPlayer.release();

        } catch (IOException e) {
            Log.e("check", "prepare() failed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}