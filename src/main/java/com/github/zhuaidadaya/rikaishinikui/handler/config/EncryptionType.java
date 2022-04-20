package com.github.zhuaidadaya.rikaishinikui.handler.config;

public enum EncryptionType {
    /**
     * Simplest Encryption
     * Fastest and Smaller File
     */
    RANDOM_SEQUENCE(0, "Random Sequence"),
    /**
     * Encryption Complicated than Random Sequence
     * Slower and Large File
     *
     * Max Save 620 Configs
     */
    COMPOSITE_SEQUENCE(1, "Composite Sequence"),
    /**
     * az
     */
    MIXED_SEQUENCE(2,"Mixed Sequence");

    final int id;
    final String name;

    EncryptionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static EncryptionType parseEncryptionType(String value) {
        switch(value) {
            case "Random Sequence" -> {
                return RANDOM_SEQUENCE;
            }
            case "Composite Sequence" -> {
                return COMPOSITE_SEQUENCE;
            }
        }
        return null;
    }
}
