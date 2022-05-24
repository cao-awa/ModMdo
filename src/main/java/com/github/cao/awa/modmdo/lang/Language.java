package com.github.cao.awa.modmdo.lang;

public enum Language {
    CHINESE(0, "Chinese"), ENGLISH(1, "English"), CHINESE_TW(2, "Chinese_tw");

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

    public static Language of(String name) {
        return switch(name.toLowerCase()) {
            case "chinese", "zh_cn" -> CHINESE;
            case "chinese_tw" -> CHINESE_TW;
            default -> ENGLISH;
        };
    }

    public static Language ofs(String name) {
        if (name.startsWith("zh_cn")) {
            return CHINESE;
        }
        if (name.startsWith("zh_tw")) {
            return CHINESE_TW;
        }
        return ENGLISH;
    }

    public static String getNameForLanguage(Language language) {
        String name = "";
        switch(language) {
            case CHINESE -> name = "Chinese";
            case ENGLISH -> name = "English";
            case CHINESE_TW -> name = "Chinese_tw";
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