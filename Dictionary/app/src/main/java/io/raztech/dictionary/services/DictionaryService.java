package io.raztech.dictionary.services;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import io.raztech.dictionary.model.Dictionary;

public class DictionaryService extends AsyncTask<Void, Void, Void> {

    private SoapObject request;
    private SoapObject answer;

    private static String METHOD_NAME = "DictionaryList";
    private static String SOAP_ACTION = "http://services.aonaware.com/webservices/DictionaryList";

    private static String NAMESPACE = "http://services.aonaware.com/webservices/";
    private static String SOAP_URL = "http://services.aonaware.com/DictService/DictService.asmx";

    @Override
    protected Void doInBackground(Void... params) {

        request = new SoapObject(NAMESPACE, METHOD_NAME);
        //request.addProperty("Celsius", tempValue);

        Log.d("doInBackground", "true");

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

            for (int i = 0; i < answer.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) answer.getProperty(i);
                Log.d("property", obj.toString());
                Log.d("id", obj.getProperty(0).toString());
                Log.d("value", obj.getProperty(1).toString());
            }

            //Log.d("dictionary", answer.toString());
        } catch (Exception e) {
            e.getMessage();
        }
        Log.d("doInBackground", "end");
        return null;
    }

//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//        pdialog.dismiss();
//        Toast.makeText(getApplicationContext(), celtofah.toString() + " Celsius", Toast.LENGTH_SHORT).show();    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        pdialog = new ProgressDialog(MainActivity.this);
//        pdialog.setMessage("Converting...");
//        pdialog.show();
//    }

}
