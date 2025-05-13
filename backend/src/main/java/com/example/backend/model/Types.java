package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Types {
    RED("Червоне"),
    WHITE("Біле"),
    PINK("Рожеве"),
    SPARKLING("Іскристе"),
    DESSERT("Десертне"),
    PORTWEIN("Портвейн"),
    ORANGE("Апельсинове");

    private final String label;

    Types(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Types fromLabel(String label) {
        for (Types t : values()) {
            if (t.label.equalsIgnoreCase(label.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown wine type: " + label);
    }
}
