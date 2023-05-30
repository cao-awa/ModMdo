package com.github.cao.awa.modmdo.lang;

public record Dictionary(String name) {
    public Dictionary(Language language) {
        this(language.getName());
    }
}
