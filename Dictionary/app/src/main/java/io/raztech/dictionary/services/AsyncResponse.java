package io.raztech.dictionary.services;

import java.util.List;

import io.raztech.dictionary.model.Definition;
import io.raztech.dictionary.model.Dictionary;

public interface AsyncResponse {
    void processFinishDef(List<Definition> output);
    void processFinishDict(List<Dictionary> output);
}
