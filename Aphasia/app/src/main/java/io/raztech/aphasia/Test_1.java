package io.raztech.aphasia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class Test_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1);

        File rootDataDir = this.getExternalFilesDir(null);
        Log.d("Path", rootDataDir.toString());

        File testDir = new File(rootDataDir.toString()+ "/test_1/1");
        if(!testDir.exists()) {
            Toast.makeText(this, "Test data is missing from external storage", Toast.LENGTH_LONG).show();
            return;
        } else {
            
        }
        //File[] dirFiles = testDir.list();
        //Log.d("files", testDir.list()[0]);
    }
}
