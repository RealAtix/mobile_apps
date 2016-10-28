package io.raztech.dictionary;

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

import io.raztech.dictionary.services.DictionaryService;

public class MainActivity extends AppCompatActivity {

    private EditText editView;
    private Button lookupButton;
    private ListView definitionView;

    private DictionaryService dictionaryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);

        editView = (EditText) findViewById(R.id.editView);
        lookupButton = (Button) findViewById(R.id.lookUpButton);
        definitionView = (ListView) findViewById(R.id.definitionView);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onLookupClicked(View v) {
        dictionaryService = new DictionaryService();
        dictionaryService.execute();
    }

}
