package io.raztech.dictionary.model;

/**
 * Created by Raz on 28/10/16.
 */

public class Definition {

    private String word;
    private String dictionaryId;
    private String definition;

    public Definition(String word, String definition, String dictionaryId) {
        this.word = word;
        this.definition = definition;
        this.dictionaryId = dictionaryId;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

}
