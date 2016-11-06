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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.cketti.mailto.EmailIntentBuilder;
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
    private List<Definition> definitions;
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

        definitions = new ArrayList<>();
        dictionaries = new ArrayList<>();
        selectedDictionaries = new ArrayList<>();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        contextOfApplication = getApplicationContext();

        dictionaryService = new DictionaryService(this);

        // Get new dictionary list every 48h in case of new addition
        dictionaryFetchTime = sharedPref.getLong("dictionaryFetchTime", 0);
        long maxDuration = 2 * 24 * 60 * 60 * 1000;
        if (System.currentTimeMillis() - dictionaryFetchTime >= maxDuration) {
            dictionaryFetchTime = System.currentTimeMillis();
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
                Intent i = new Intent(this, DictionaryActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
            case R.id.menu_share_email:
                if (!definitions.isEmpty()) {
                    String defBody = "";
                    for (Definition def : definitions) {
                        defBody += def.toString();
                    }

                    Intent emailIntent = EmailIntentBuilder.from(this)
                            .subject(getResources().getString(R.string.output_definition_for) + " \"" + definitions.get(0).getWord() + "\"")
                            .body(defBody)
                            .build();
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.output_no_definitions), Toast.LENGTH_SHORT).show();
                }
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
                    Toast.makeText(this, getResources().getString(R.string.output_no_dictionaries), Toast.LENGTH_LONG).show();
                    return;
                }
                if (result != null && result.length() > 0) {
                    selectedDictionaries =
                            new Gson().fromJson(result, new TypeToken<List<Dictionary>>() {}.getType());

                    onPause(); //save data for next dictionary selection
                }
            }
        }
    }

    protected void onLookupClicked(View v) {
        if (editView.getText().length() == 0) {
            Toast.makeText(this, getResources().getString(R.string.output_need_input), Toast.LENGTH_SHORT).show();
            return;
        }
//        Log.d("selectedDictionaries", selectedDictionaries.toString());
        String definition = editView.getText().toString();

        List<String> dictIDs = new ArrayList<>();
        for (Dictionary d : selectedDictionaries) {
            dictIDs.add(d.getId());
        }
        String dicts = new Gson().toJson(dictIDs);

        new DefinitionService(this, this).execute(definition, dicts);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String dictionaryList = new Gson().toJson(dictionaries);
        String selectedDictionaryList = new Gson().toJson(selectedDictionaries);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dictionaryList", dictionaryList);
        editor.putString("selectedDictionaryList", selectedDictionaryList);
        editor.putLong("dictionaryFetchTime", dictionaryFetchTime);

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    public void processFinishDef(List<Definition> output){
        if (output.size() == 0 || output == null) {
            Toast.makeText(this, getResources().getString(R.string.output_word_not_found), Toast.LENGTH_SHORT).show();
            definitionView.setAdapter(new DefinitionAdapter(this, new ArrayList<Definition>()));
            return;
        }

        definitions = new ArrayList<>(output);

        hideKeyboard();

        DefinitionAdapter adapter = new DefinitionAdapter(this, output);
        definitionView.setAdapter(adapter);
    }

    protected void onTextViewClicked(View v) {
        editView.setText("");
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        editView.clearFocus();
    }

    @Override
    public void processFinishDict(List<Dictionary> output){
        // Reset selected dictionaries after update in case one is removed (lazy error handling)
        dictionaries = output;
        selectedDictionaries = output;

        String dictionaryList = new Gson().toJson(dictionaries);
        String selectedDictionaryList = new Gson().toJson(dictionaries);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("dictionaryList", dictionaryList);
        editor.putString("selectedDictionaryList", selectedDictionaryList);
        editor.apply();
    }

}
