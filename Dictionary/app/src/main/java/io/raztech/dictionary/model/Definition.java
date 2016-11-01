package io.raztech.dictionary.model;

public class Definition {

    private String word;
    private String dictionaryName;
    private String definition;

    public Definition(String word, String definition, String dictionaryName) {
        this.word = word;
        this.definition = definition;
        this.dictionaryName = dictionaryName;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

}
