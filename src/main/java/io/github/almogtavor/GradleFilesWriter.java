package io.github.almogtavor;

import org.gradle.internal.impldep.com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GradleFilesWriter {

    public static final String QUOTATION_MARK = "\"";
    public static final String MODULES_PATHS_VARIABLE_NAME = "modulesPaths";
    public static final String BACKSLASH = "\\";
    public static final String FORWARD_SLASH = "/";
    public static final String COMMA = ",";
    public static final String DROP_LINE = "\n";
    public static final int INDENTATION = 4;

    public GradleFilesWriter() {
    }

    public void appendCurrentRepoPathToCompositeBuildFile(File compositeBuildGradleFile,
                                                          String currentGitRepoPath,
                                                          boolean isFirstRun) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(compositeBuildGradleFile, true))) {
            if (isFirstRun) {
                bw.append("ext {")
                        .append(System.lineSeparator());
                String pathVariableAssignment = String.format("%s = List.of(%s%s%s)",
                        MODULES_PATHS_VARIABLE_NAME,
                        QUOTATION_MARK,
                        currentGitRepoPath.replace("\\", "/"),
                        QUOTATION_MARK);
                bw.append(pathVariableAssignment.indent(4));
                bw.append("}")
                        .append(System.lineSeparator());
            } else {
                List<String> fileLines = Files.readLines(compositeBuildGradleFile, StandardCharsets.UTF_8);
                String pathVariableAssignment = String.format("%s = List.of(%s%s%s)",
                        MODULES_PATHS_VARIABLE_NAME,
                        QUOTATION_MARK,
                        currentGitRepoPath.replace(BACKSLASH, FORWARD_SLASH),
                        QUOTATION_MARK);
                bw.append(pathVariableAssignment.indent(4));
            }
        }
    }

    public void createCompositeBuildGradleFileFromModulesPaths(List<String> listOfModulesPathsToInclude,
                                                               File compositeBuildGradleFile, DslLang dslLang) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(compositeBuildGradleFile, true))) {
            if (dslLang == DslLang.GROOVY) {
                bw.append("ext.");
            } else {
                bw.append("val ");
            }
            StringBuilder multilineModulesPathsDeclaration = new StringBuilder();
            boolean isFirstLine = true;
            for (var modulePath : listOfModulesPathsToInclude) {
                if (!isFirstLine) {
                    multilineModulesPathsDeclaration.append(COMMA + DROP_LINE);
                }
                multilineModulesPathsDeclaration.append(QUOTATION_MARK).append(modulePath.replace("\\", "/")).append(QUOTATION_MARK);
                isFirstLine = false;
            }
            bw.append(getPathVariableAssignment(dslLang, multilineModulesPathsDeclaration));
        }
    }

    private String getPathVariableAssignment(DslLang dslLang, StringBuilder multilineModulesPathsDeclaration) {
        String groovyEqualitySignature = "= ";
        String groovyListDeclaration = "List.of";
        String kotlinExtrasAssignment = "by extra(";
        String kotlinListDeclaration = "listOf<String>";
        String variableAssignment = dslLang == DslLang.GROOVY ? groovyEqualitySignature : kotlinExtrasAssignment;
        String listDeclaration = dslLang == DslLang.GROOVY ? groovyListDeclaration : kotlinListDeclaration;
        String endSignature = dslLang == DslLang.GROOVY ? "" : ")";
        return String.format("%s %s%s(%s%s)%s",
                MODULES_PATHS_VARIABLE_NAME,
                variableAssignment,
                listDeclaration,
                DROP_LINE,
                multilineModulesPathsDeclaration.toString().indent(INDENTATION * 2),
                endSignature);
    }
}
