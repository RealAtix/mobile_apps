package io.raztech.dictionary.services;

import java.util.List;

import io.raztech.dictionary.model.Definition;

public interface AsyncResponse {
    void processFinish(List<Definition> output);
}
