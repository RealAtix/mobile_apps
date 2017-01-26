package io.raztech.aphasia;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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

public class Test_1 extends AppCompatActivity implements AsyncResponse, TextToSpeech.OnInitListener {

    private Map<Integer, List<File>> images;
    private Map<Integer, JSONObject> info;
    private String name;
    private TextView wordText;
    private int counter;
    private TextToSpeech tts;
    private int MY_DATA_CHECK_CODE = 0;
    private JSONArray jsonAnswerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);

        counter = 0;
        jsonAnswerArray = new JSONArray();

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Log.i("name", name);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        //data
        File rootDataDir = new File(this.getExternalFilesDir(null).getAbsolutePath() + "/test_1");
        Log.d("Path", rootDataDir.toString());

        File testDir = new File(rootDataDir.getAbsolutePath() + "/0");
        if(!testDir.exists()) {
            Toast.makeText(this, "Test data is missing from external storage", Toast.LENGTH_LONG).show();
            //return;
        } else {
            File[] dirFiles = testDir.listFiles();
            Log.d("files", dirFiles.toString());
            //getImageList(rootDataDir);

            Log.d("pat", rootDataDir.getAbsolutePath());
            new Test1Loader(this, this).execute(rootDataDir.getAbsolutePath());
        }

        wordText = (TextView) findViewById(R.id.txtWord);

    }

    @Override
    public void onInit(int status) {
        //check for successful instantiation
        if (status == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.US);
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                tts = new TextToSpeech(this, this);
            }
            else {
                //no data, install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void processFinishTest1(Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> output) {
        Log.d("output", output.toString());
        info = output.first;
        images = output.second;

        showNextQuestion();
    }

    private void showNextQuestion() {

        if(counter >= info.size()) {
            testDone();
            return;
        }

        //Log.d("showNextQuestion", info.get(counter).toString());

        try {
            Log.d("wordNextQuestion", info.get(counter).getString("word"));
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

        tts.shutdown();
        finish();
    }

    protected void onPlayClicked(View v) {
        for (Voice tmpVoice : tts.getVoices()) {
            //Log.d("Voice: ", tmpVoice.toString());
            if (tmpVoice.getName().equals("en-gb-x-fis#female_1-local")) {
                tts.setVoice(tmpVoice);
            }
        }
        tts.speak(wordText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
        //tts.speak(wordText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onImg1Clicked(View v) {
        try {
            JSONObject answer = new JSONObject();
            answer.put("number", info.get(counter-1).getString("number"));
            answer.put("answer", "1");
            jsonAnswerArray.put(answer);

            showNextQuestion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onImg2Clicked(View v) {
        try {
            JSONObject answer = new JSONObject();
            answer.put("number", info.get(counter-1).getString("number"));
            answer.put("answer", "2");
            jsonAnswerArray.put(answer);

            showNextQuestion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onImg3Clicked(View v) {
        try {
            JSONObject answer = new JSONObject();
            answer.put("number", info.get(counter-1).getString("number"));
            answer.put("answer", "3");
            jsonAnswerArray.put(answer);

            showNextQuestion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onImg4Clicked(View v) {
        try {
            JSONObject answer = new JSONObject();
            answer.put("number", info.get(counter-1).getString("number"));
            answer.put("answer", "4");
            jsonAnswerArray.put(answer);

            showNextQuestion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
