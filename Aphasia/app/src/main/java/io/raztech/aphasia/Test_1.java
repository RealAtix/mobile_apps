package io.raztech.aphasia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.raztech.aphasia.services.AsyncResponse;
import io.raztech.aphasia.services.Test1Loader;

public class Test_1 extends AppCompatActivity implements AsyncResponse {

    private Map<String, List<File>> images;
    private Map<String, JSONObject> info;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
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

    }

    @Override
    public void processFinishTest1(Pair<Map<String, JSONObject>, Map<String, List<File>>> output) {
        Log.d("output", output.toString());
    }
}
