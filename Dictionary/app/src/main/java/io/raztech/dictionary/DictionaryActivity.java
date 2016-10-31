package io.raztech.dictionary;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.raztech.dictionary.model.Dictionary;

public class DictionaryActivity extends AppCompatActivity {

    private List<Dictionary> dictionaries;
    private List<Dictionary> selectedDictionaries;

    private ListView definitionList;
    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_dictionary_selection);

        definitionList = (ListView) findViewById(R.id.list);

        Context applicationContext = MainActivity.getContextOfApplication();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        String dictionaryJSONList = sharedPref.getString("dictionaryList", "");
        String selectedDictionaryJSONList = sharedPref.getString("selectedDictionaryList", new Gson().toJson(dictionaries));

        dictionaries =
                new Gson().fromJson(dictionaryJSONList, new TypeToken<List<Dictionary>>() {}.getType());
        selectedDictionaries =
                new Gson().fromJson(selectedDictionaryJSONList, new TypeToken<List<Dictionary>>() {}.getType());

        List<String> dictValues = new ArrayList<>();
        for (Dictionary d : dictionaries) {
            dictValues.add(d.getName());
        }

        List<String> selectedDictValues = new ArrayList<>();
        for (Dictionary d : dictionaries) {
            selectedDictValues.add(d.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, dictValues);
        definitionList.setAdapter(adapter);
        definitionList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < definitionList.getCount(); i++) {
            for (int j = 0; j < selectedDictionaries.size(); j++) {
                if (definitionList.getItemAtPosition(i).toString().equals(selectedDictionaries.get(j).getName())) {
                    definitionList.setItemChecked(i, true);
                }
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {

        SparseBooleanArray checked = definitionList.getCheckedItemPositions();
        selectedDictionaries = new ArrayList<>();

        for (int i = 0; i < definitionList.getAdapter().getCount(); i++) {
            if (checked.get(i)) {
                //Log.d("item", definitionList.getItemAtPosition(i).toString());
                for (int j = 0; j < dictionaries.size(); j++) {
                    if (dictionaries.get(j).getName().equals(definitionList.getItemAtPosition(i).toString())) {
                        selectedDictionaries.add(dictionaries.get(j));
                        Log.d("dd", dictionaries.get(j).toString());
                    }
                }
            }
        }

        String selectedDictionaryList = new Gson().toJson(selectedDictionaries);

//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("selectedDictionaryList", selectedDictionaryList);
//        editor.apply();

        Intent intent = new Intent();
        intent.putExtra("selectedDictList", selectedDictionaryList);
        setResult(RESULT_OK, intent);
        super.finish();
    }

}
