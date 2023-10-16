package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckBadSmells {
    ArrayList<ClassOrInterfaceDeclaration> allClasses = new ArrayList<>();
    ArrayList<MethodDeclaration> allMethods = new ArrayList<>();
    ArrayList<FieldDeclaration> allGlobalFields = new ArrayList<>();

    public void addDeclaration(ClassOrInterfaceDeclaration c){
        allClasses.add(c);
    }

    public void addDeclaration(MethodDeclaration c){
        allMethods.add(c);
    }

    public void addDeclaration(FieldDeclaration c){
        allGlobalFields.add(c);
    }

    public void largeClassEasy(){
        for(ClassOrInterfaceDeclaration c : allClasses){
            if ((c.getEnd().get().line - c.getBegin().get().line) > 100){
                System.out.println("BAD SMELL ("+ c.getNameAsString() +") - Long Class (easy)");
            }
        }
    }

    public void longMethodEasy(){
        for(MethodDeclaration m : allMethods){
            if ((m.getEnd().get().line - m.getBegin().get().line) > 20){
                System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Method (easy)");
            }
        }
    }

    public void longParameterList(){
        for(MethodDeclaration m : allMethods){
            if(m.getParameters().size() >= 5){
            System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Paramteter List");
            }
        }
    }

    public void temporaryField(){

    }
}
