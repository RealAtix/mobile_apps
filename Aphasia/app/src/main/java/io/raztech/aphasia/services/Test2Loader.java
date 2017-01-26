package io.raztech.aphasia.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

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

public class Test2Loader extends AsyncTask<String, Void, List<File>> {


    private final AsyncResponse delegate;
    private final Context context;
    private ProgressDialog progDialog;

    public Test2Loader(AsyncResponse delegate, Context context){
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected List<File> doInBackground(String[] params) {
        String parentPath = params[0];
        File parentDir = new File(parentPath);

        //Log.d("parentdirlength", String.valueOf(parentDir.listFiles().length));
        List<File> data = new ArrayList<>();

        File[] files = parentDir.listFiles();

        for (int j = 0; j < parentDir.list().length; j++) {
            //Log.d("pathlistlength", String.valueOf(path.list().length));
            if (files[j].getName().contains(".jpg")) {
                Log.d("files", files[j].getName());
                data.add(files[j]);
            }
        }

        return data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progDialog = new ProgressDialog(context);
        progDialog.setMessage("Loading test");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }

    @Override
    protected void onPostExecute(List<File> result) {
        super.onPostExecute(result);

        progDialog.dismiss();
        delegate.processFinishTest2(result);

    }
}
