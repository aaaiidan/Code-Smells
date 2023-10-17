package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;

public class Analyser {

    /* 
    public ArrayList<String> longClassEasyAL = new ArrayList<>();
    public ArrayList<String> longMethodEasyAL = new ArrayList<>();
    public ArrayList<String> longParameterListAL = new ArrayList<>();
    public ArrayList<String> longClassMediumAL = new ArrayList<>();
    public ArrayList<String> longMethodMediumAL = new ArrayList<>();
    */
    static CheckBadSmells checker = new CheckBadSmells();

    public static void main(String[] args) throws Exception {
        FileInputStream in = new FileInputStream("test_classes\\Bloaters\\Grid.java");

        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(in);
        } finally {
            in.close();
        }

        new ClassDiagramVisitor().visit(cu, checker);
        
        checker.largeClassEasy();
        checker.longParameterList();
        checker.longMethodEasy();
        checker.longMethodMedium();
        checker.longClassMedium();


        /*
        VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();
        methodNameVisitor.visit(cu, null);
        List<String> methodNames = new ArrayList<>();
        VoidVisitor<List<String>> methodNameCollector = new MethodNameCollector();
        methodNameCollector.visit(cu, methodNames);
        methodNames.forEach(n -> System.out.println("Method Name Collected: " + n));
        */

    }


    //Visitor implementation for Class
    private static class ClassDiagramVisitor extends VoidVisitorAdapter {

        public void visit(ClassOrInterfaceDeclaration visitingClass, Object arg){

            checker.addDeclaration(visitingClass);
            super.visit(visitingClass, arg);
        }

        public void visit(MethodDeclaration visitingMethod, Object arg) {

            checker.addDeclaration(visitingMethod);
            super.visit(visitingMethod, arg);
            }

        public void visit(FieldDeclaration visitingClass, Object arg){

            for(VariableDeclarator v : visitingClass.getVariables()){
                checker.addDeclaration(v);
            }
        }
    }

/* 
    private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            System.out.println("Method Name Printed: " + md.getName());
        }
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    */


}