package io.raztech.dictionary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.raztech.dictionary.model.Definition;
import io.raztech.dictionary.model.Dictionary;
import io.raztech.dictionary.services.AsyncResponse;
import io.raztech.dictionary.services.DefinitionService;
import io.raztech.dictionary.services.DictionaryService;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private EditText editView;
    private Button lookupButton;
    private ListView definitionView;

    private DictionaryService dictionaryService;
    private List<Dictionary> dictionaries;
    private List<Dictionary> selectedDictionaries;
    private long dictionaryFetchTime;

    private SharedPreferences sharedPref;
    public static Context contextOfApplication;

    private static final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        editView = (EditText) findViewById(R.id.editView);
        lookupButton = (Button) findViewById(R.id.lookUpButton);
        definitionView = (ListView) findViewById(R.id.definitionView);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        contextOfApplication = getApplicationContext();

        dictionaryService = new DictionaryService();

        // Get new dictionary list every 48h in case of new addition
        dictionaryFetchTime = sharedPref.getLong("dictionaryFetchTime", 0);
        long maxDuration = 2 * 24 * 60 * 60 * 1000;
        if (System.currentTimeMillis() - dictionaryFetchTime >= maxDuration) {
            dictionaryService.execute();
        }
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_dictionary:
                Log.d("menu item", "select dictionary");
                Intent i = new Intent(this, DictionaryActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("selectedDictList")) {
                String result = data.getExtras().getString("selectedDictList");
                if (result.contains("[]")) {
                    Toast.makeText(this, "Can't have 0 dictionaries selected", Toast.LENGTH_LONG).show();
                    return;
                }
                if (result != null && result.length() > 0) {
                    Log.d("te",result);
                    selectedDictionaries =
                            new Gson().fromJson(result, new TypeToken<List<Dictionary>>() {}.getType());
                    Log.d("te",selectedDictionaries.toString());
                    onPause(); //save data for next dictionary selection
                }
            }
        }
    }

    protected void onLookupClicked(View v) {
        Log.d("selectedDictionaries", selectedDictionaries.toString());
        String definition = editView.getText().toString();

        List<String> dictIDs = new ArrayList<>();
        for (Dictionary d : selectedDictionaries) {
            dictIDs.add(d.getId());
        }
        String dicts = new Gson().toJson(dictIDs);

        new DefinitionService(this).execute(definition, dicts);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String dictionaryList = new Gson().toJson(dictionaries);
        String selectedDictionaryList = new Gson().toJson(selectedDictionaries);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dictionaryList", dictionaryList);
        editor.putString("selectedDictionaryList", selectedDictionaryList);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onresume", "true");

        String dictionaryJSONList = sharedPref.getString("dictionaryList", "");
        dictionaries =
                new Gson().fromJson(dictionaryJSONList, new TypeToken<List<Dictionary>>() {}.getType());

        String selectedDictionaryJSONList = sharedPref.getString("selectedDictionaryList", new Gson().toJson(dictionaries));
        selectedDictionaries =
                new Gson().fromJson(selectedDictionaryJSONList, new TypeToken<List<Dictionary>>() {}.getType());
    }

    // Share application context with other classes
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    @Override
    public void processFinish(List<Definition> output){
        Log.d("finish", "ayyy");
    }

}
