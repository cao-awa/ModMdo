package com.github.cao.awa.modmdo.lang;

public enum Language {
    ZH_CN("zh_cn"), EN_US("en_us"), ZH_TW("zh_tw"), JA_JP("ja_jp");

    private final String name;

    /**
     * init, set language
     *
     * @param name
     *         name of Languagel
     */
    Language(String name) {
        this.name = name;
    }

    public static Language ofs(String name) {
        name = name.toLowerCase();
        if (name.startsWith("zh_cn")) {
            return ZH_CN;
        }
        if (name.startsWith("zh_tw")) {
            return ZH_TW;
        }
        if (name.startsWith("en_us")) {
            return EN_US;
        }
        if (name.startsWith("ja_jp")) {
            return JA_JP;
        }
        return of(name);
    }

    public static Language of(String name) {
        if (name.startsWith("chinese")) {
            return ZH_CN;
        }
        if (name.startsWith("chinese_tw")) {
            return ZH_TW;
        }
        if (name.startsWith("english")) {
            return EN_US;
        }
        if (name.startsWith("japanese")) {
            return JA_JP;
        }
        return null;
    }

    /**
     * get language name
     */
    public String getName() {
        return name;
    }
}
