package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Analyser {
    static CheckBadSmells checker = new CheckBadSmells();
    static ArrayList<CompilationUnit> ASTs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        String folderDir = "test_classes";
        try {
            Stream<Path> files = Files.walk(Paths.get(folderDir), FileVisitOption.FOLLOW_LINKS).filter(path -> path.toString().endsWith(".java"));
            files.forEach(file -> {
            CompilationUnit cu;
            try {
                cu = StaticJavaParser.parse(new FileInputStream(String.valueOf(file)));
                ASTs.add(cu);
            } catch (FileNotFoundException e) {
                e.printStackTrace();;
            }
        });
        } catch (NoSuchFileException e) {
            System.err.println("Error - Folder Not Found ");
        } catch (IOException e) {
            System.err.println("Error - Folder Not Found");
        }

        for (CompilationUnit cu : ASTs) {
            checker.run(cu, ASTs);
        }   
    }
}
