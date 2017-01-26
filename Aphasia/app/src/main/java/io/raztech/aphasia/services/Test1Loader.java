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

import io.raztech.aphasia.R;

public class Test1Loader extends AsyncTask<String, Void, Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>>> {


    private final AsyncResponse delegate;
    private final Context context;
    private ProgressDialog progDialog;

    public Test1Loader(AsyncResponse delegate, Context context){
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> doInBackground(String[] params) {
        String parentPath = params[0];
        File parentDir = new File(parentPath);

        Map<Integer, List<File>> data = new HashMap<>();
        Map<Integer, JSONObject> jsonData = new HashMap<>();

        for (int i = 0; i < parentDir.listFiles().length; i++) {
            //Log.d("parentdirlength", String.valueOf(parentDir.listFiles().length));
            List<File> images = new ArrayList<>();

            File path = new File(parentDir.getAbsolutePath() + "/" + i);
            File[] files = path.listFiles();

            for (int j = 0; j < path.list().length; j++) {
                //Log.d("pathlistlength", String.valueOf(path.list().length));
                if (files[j].getName().contains(".jpg")) {
                    Log.d("files", files[j].getName());
                    images.add(files[j]);
                } else if (files[j].getName().equals("data")) {
                    try {
                        String jsonStr = null;
                        FileInputStream stream = new FileInputStream(files[j]);
                        try {
                            FileChannel fc = stream.getChannel();
                            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                            jsonStr = Charset.defaultCharset().decode(bb).toString();
                        } catch(Exception e){
                            e.printStackTrace();
                        } finally {
                            stream.close();
                        }

                        JSONObject jsonObj = new JSONObject(jsonStr);

                        jsonData.put(i, jsonObj);
                        Log.d("data", jsonStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            data.put(i, images);
        }
        Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> pair = new Pair<>(jsonData, data);

        return pair;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progDialog = new ProgressDialog(context);
        progDialog.setMessage(context.getResources().getString(R.string.loader_loading));
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }

    @Override
    protected void onPostExecute(Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> result) {
        super.onPostExecute(result);

        progDialog.dismiss();
        delegate.processFinishTest1(result);

    }
}
