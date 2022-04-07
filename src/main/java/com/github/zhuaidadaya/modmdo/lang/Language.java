package com.github.zhuaidadaya.modmdo.lang;

public enum Language {
    CHINESE(0, "Chinese"), ENGLISH(1, "English"), CHINESE_TW(2, "Chinese_tw"), AUTO(3, "Auto");

    private final int value;
    private final String name;

    /**
     * init, set language
     *
     * @param value
     *         value(ID) of language
     * @param name
     *         name of Languagel
     */
    Language(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Language getLanguageForName(String name) {
        Language language = null;
        switch(name.toLowerCase()) {
            case "chinese" -> language = CHINESE;
            case "english" -> language = ENGLISH;
            case "chinese_tw" -> language = CHINESE_TW;
            case "auto" -> language = AUTO;
        }
        return language;
    }

    public static String getNameForLanguage(Language language) {
        String name = "";
        switch(language) {
            case CHINESE -> name = "Chinese";
            case ENGLISH -> name = "English";
            case CHINESE_TW -> name = "Chinese_tw";
            case AUTO -> name = "Auto";
        }
        return name;
    }

    /**
     * get language value(ID)
     */
    public int getValue() {
        return value;
    }

    /**
     * get language name
     */
    public String getName() {
        return name;
    }
}
