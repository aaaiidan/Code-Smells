package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Analyser {
    static CheckBadSmells2 checker = new CheckBadSmells2();
    static ArrayList<CompilationUnit> ASTs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        String folderDir = "test_classes\\Abusers\\RefusedBequest";
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
     
        for (CompilationUnit cu : ASTs) {
            checker.run(cu, ASTs);
        }   
    }
}
