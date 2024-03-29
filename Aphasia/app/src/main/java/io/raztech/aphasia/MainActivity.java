package io.raztech.aphasia;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String name;

    private static final int REQUEST_STORAGE_PERMISSION = 114;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Log.d("oncreate mainacticity", "true");
        if (name == null) {
            getName();
        }
    }

    protected void getName() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_name, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.name);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (userInput.getText().length() == 0) {
                                    nameInputError();
                                    getName();
                                }

                                name = userInput.getText().toString();
                                //Log.d("name", userInput.getText().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void onTestAClicked(View v) {
        startTest1Activity();
    }
    protected void onTestBClicked(View v) {
        startTest2Activity();
    }

    protected void onEndClicked(View v) {
        // restart
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    protected void nameInputError() {
        Toast.makeText(this, "Need input", Toast.LENGTH_SHORT).show();
    }

    protected void startTest1Activity() {
        Intent i = new Intent(this, Test_1.class);
        i.putExtra("name", name);
        startActivity(i);
    }

    protected void startTest2Activity() {
        Intent i = new Intent(this, Test_2.class);
        i.putExtra("name", name);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.v("Permission","Permission is granted");
                } else {
                    Log.v("Permission","Permission is denied");
                    Toast.makeText(this, getResources().getString(R.string.main_RW_permission), Toast.LENGTH_LONG).show();

                    Thread closeActivity = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);

                                finish();
                                moveTaskToBack(true);
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        }
                    });
                }
            }
            case REQUEST_RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.v("Permission","Permission is granted");
                } else {
                    Log.v("Permission","Permission is denied");
                    Toast.makeText(this, getResources().getString(R.string.main_audio_permission), Toast.LENGTH_LONG).show();

                    Thread closeActivity = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);

                                finish();
                                moveTaskToBack(true);
                            } catch (Exception e) {
                                e.getLocalizedMessage();
                            }
                        }
                    });
                }
            }
        }

    }

}
