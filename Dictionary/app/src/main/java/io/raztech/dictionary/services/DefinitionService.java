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
import org.ksoap2.serialization.SoapPrimitive;
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

    private final static String METHOD_NAME = "DefineInDict";
    private final static String SOAP_ACTION = "http://services.aonaware.com/webservices/DefineInDict";

    private final static String NAMESPACE = "http://services.aonaware.com/webservices/";
    private final static String SOAP_URL = "http://services.aonaware.com/DictService/DictService.asmx";

    public DefinitionService(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<Definition> doInBackground(String[] params) {

        definitions = new ArrayList<Definition>();

        String word = params[0].toString();
        String dicts = params[1];
        List<String> selectedDictionaries =
                new Gson().fromJson(dicts, new TypeToken<List<String>>() {}.getType());

        for (String dictionary : selectedDictionaries) {
            request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("word", word);
            request.addProperty("dictId", dictionary);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URL);
            httpTransport.debug = true;

            try {
                httpTransport.call(SOAP_ACTION, envelope);
//            Log.d("request dump", httpTransport.requestDump);
//            Log.d("response dump", httpTransport.responseDump);
                answer = (SoapObject) envelope.getResponse();

                SoapObject obj = (SoapObject) answer.getProperty(1);
//            Log.d("2", obj.toString());

                SoapObject obj2 = (SoapObject) obj.getProperty(0);
                SoapObject dictIdObj = (SoapObject) obj2.getProperty(1);

                String dictName = dictIdObj.getProperty(1).toString();
                String wordDef = obj2.getProperty(2).toString();

//                Log.i("dictName", dictName);
//                Log.i("wordDef", wordDef);

                Definition def = new Definition(word, wordDef, dictName);
                definitions.add(def);

            } catch (Exception e) {
                e.getMessage();
                Log.e("Definition service", "no definition for " + word + " in dictionary " + dictionary);
            }
        }

        return definitions;
    }

    @Override
    protected void onPostExecute(List<Definition> result) {
        delegate.processFinish(result);
    }

}
