package io.raztech.aphasia;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.raztech.aphasia.services.AsyncResponse;
import io.raztech.aphasia.services.Test1Loader;

public class Test_1 extends AppCompatActivity implements AsyncResponse {

    private Map<Integer, List<File>> images;
    private Map<Integer, JSONObject> info;
    private String name;
    private TextView wordText;
    private int counter;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);

        counter = 0;

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Log.i("name", name);

        File rootDataDir = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/test_1");
        Log.d("Path", rootDataDir.toString());

        File testDir = new File(rootDataDir.getAbsolutePath() + "/0");
        if(!testDir.exists()) {
            Toast.makeText(this, "Test data is missing from external storage", Toast.LENGTH_LONG).show();
            return;
        } else {
            File[] dirFiles = testDir.listFiles();
            Log.d("files", dirFiles.toString());
            //getImageList(rootDataDir);

            Log.d("pat", rootDataDir.getAbsolutePath());
            new Test1Loader(this, this).execute(rootDataDir.getAbsolutePath());
        }

        wordText = (TextView) findViewById(R.id.txtWord);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                    //Voice k = new Voice("l", Locale.UK, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW, true, null);
                    //tts.setVoice(k);
                }
            }
        });

    }

    @Override
    public void processFinishTest1(Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> output) {
        Log.d("output", output.toString());
        info = output.first;
        images = output.second;

        showNextQuestion();
    }

    private void showNextQuestion() {
        Log.d("info", info.get(counter).toString());

        try {
            Log.d("word", info.get(counter).getString("word"));
            wordText.setText(info.get(counter).getString("word"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton jpgView = (ImageButton)findViewById(R.id.img1);
        ImageButton jpgView2 = (ImageButton)findViewById(R.id.img2);
        ImageButton jpgView3 = (ImageButton)findViewById(R.id.img3);
        ImageButton jpgView4 = (ImageButton)findViewById(R.id.img4);
        BitmapDrawable d = new BitmapDrawable(getResources(), images.get(counter).get(0).getAbsolutePath());
        BitmapDrawable d2 = new BitmapDrawable(getResources(), images.get(counter).get(1).getAbsolutePath());
        BitmapDrawable d3 = new BitmapDrawable(getResources(), images.get(counter).get(2).getAbsolutePath());
        BitmapDrawable d4 = new BitmapDrawable(getResources(), images.get(counter).get(3).getAbsolutePath());
        jpgView.setImageDrawable(d);
        jpgView2.setImageDrawable(d2);
        jpgView3.setImageDrawable(d3);
        jpgView4.setImageDrawable(d4);

        //onPlayClicked(null);

        counter++;
    }

    protected void onPlayClicked(View v) {
        //tts.speak("hello", TextToSpeech.QUEUE_FLUSH, null, null);
        tts.speak(wordText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onImg1Clicked(View v) {
        showNextQuestion();
    }

    protected void onImg2Clicked(View v) {
        showNextQuestion();
    }

    protected void onImg3Clicked(View v) {
        showNextQuestion();
    }

    protected void onImg4Clicked(View v) {
        showNextQuestion();
    }

}
