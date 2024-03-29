package io.raztech.aphasia.services;

import android.util.Pair;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;


public interface AsyncResponse {
    void processFinishTest1(Pair<Map<Integer, JSONObject>, Map<Integer, List<File>>> output);
    void processFinishTest2(List<File> output);
}
