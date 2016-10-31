package io.raztech.dictionary.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import io.raztech.dictionary.MainActivity;
import io.raztech.dictionary.model.Definition;
import io.raztech.dictionary.model.Dictionary;

public class DefinitionService extends AsyncTask<String, Void, List<Definition>> {

    public AsyncResponse delegate = null;

    private SoapObject request;
    private SoapObject answer;

    private List<Definition> definitions;

    private final static String METHOD_NAME = "Define";
    private final static String SOAP_ACTION = "http://services.aonaware.com/webservices/Define";

    private final static String NAMESPACE = "http://services.aonaware.com/webservices/";
    private final static String SOAP_URL = "http://services.aonaware.com/DictService/DictService.asmx";

    public DefinitionService(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<Definition> doInBackground(String... params) {

        String definition = params[0];
        String dicts = params[1];
        List<String> selectedDictionaries =
                new Gson().fromJson(dicts, new TypeToken<List<String>>() {}.getType());

        request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("word", "tree");
        request.addProperty("dictId", "devils");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URL);
        httpTransport.debug = true;
        Log.d("request dump", "????????????????????????");

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Log.d("request dump", httpTransport.requestDump);
            Log.d("response dump", httpTransport.responseDump);
            answer = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.getMessage();
            Log.e("Definition service", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Definition> result) {
        delegate.processFinish(result);
    }

}
