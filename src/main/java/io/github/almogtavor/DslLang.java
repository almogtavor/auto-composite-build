package io.github.almogtavor;

public enum DslLang {
    GROOVY("groovy"),
    KOTLIN("kotlin");

    private String lang;

    DslLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
