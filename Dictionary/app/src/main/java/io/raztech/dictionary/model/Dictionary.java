package io.raztech.dictionary.model;

public class Dictionary {

    private String id;
    private String name;

    public Dictionary(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
