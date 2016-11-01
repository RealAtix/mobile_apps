package io.raztech.dictionary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.raztech.dictionary.model.Definition;

public class DefinitionAdapter extends ArrayAdapter<Definition> {

    public DefinitionAdapter(Context context, List<Definition> definitions) {
        super(context, 0, definitions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Definition def = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);
        }
        // Lookup view for data population
        TextView dictName = (TextView) convertView.findViewById(R.id.dictName);
        TextView wordDef = (TextView) convertView.findViewById(R.id.wordDef);

        // Populate the data into the template view using the data object
        dictName.setText(def.getDictionaryName());
        wordDef.setText(def.getDefinition());

        // Return the completed view to render on screen
        return convertView;
    }

}
