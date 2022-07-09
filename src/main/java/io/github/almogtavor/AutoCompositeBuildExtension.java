package io.github.almogtavor;

import java.util.List;

public class AutoCompositeBuildExtension {
    private List<String> modulesNames;
    private DslLang dslLang;

    public List<String> getModulesNames() {
        return modulesNames;
    }

    public void setModulesNames(List<String> modulesNames) {
        this.modulesNames = modulesNames;
    }

    public DslLang getDslLang() {
        return dslLang;
    }

    public void setDslLang(DslLang dslLang) {
        this.dslLang = dslLang;
    }
}