package io.raztech.dictionary.services;

import android.os.AsyncTask;
import android.util.Log;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import io.raztech.dictionary.model.Dictionary;

public class DictionaryService extends AsyncTask<Void, Void, List<Dictionary>> {

    private SoapObject request;
    private SoapObject answer;

    public AsyncResponse delegate = null;

    private List<Dictionary> dictionaries;

    private final static String METHOD_NAME = "DictionaryList";
    private final static String SOAP_ACTION = "http://services.aonaware.com/webservices/DictionaryList";

    private final static String NAMESPACE = "http://services.aonaware.com/webservices/";
    private final static String SOAP_URL = "http://services.aonaware.com/DictService/DictService.asmx";

    public DictionaryService(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<Dictionary> doInBackground(Void... params) {

        request = new SoapObject(NAMESPACE, METHOD_NAME);
        dictionaries = new ArrayList<>();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URL);
        httpTransport.debug = false;
        try {
            httpTransport.call(SOAP_ACTION, envelope);
//            Log.d("request dump", httpTransport.requestDump);
//            Log.d("response dump", httpTransport.responseDump);
            answer = (SoapObject) envelope.getResponse();

            for (int i = 0; i < answer.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) answer.getProperty(i);

//                Log.d("id", obj.getProperty(0).toString());
//                Log.d("value", obj.getProperty(1).toString());
                String id = obj.getProperty(0).toString();
                String value = obj.getProperty(1).toString();

                Dictionary dict = new Dictionary(id, value);
                dictionaries.add(dict);
            }
        } catch (Exception e) {
            e.getMessage();
            Log.e("DictService", e.getMessage());
        }

        return dictionaries;
    }

    @Override
    protected void onPostExecute(List<Dictionary> result) {
        super.onPostExecute(result);

        delegate.processFinishDict(result);
    }

}
