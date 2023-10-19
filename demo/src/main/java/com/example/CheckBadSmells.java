package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.nimbus.State;

public class CheckBadSmells {
    ArrayList<ClassOrInterfaceDeclaration> allClasses = new ArrayList<>();
    ArrayList<MethodDeclaration> allMethods = new ArrayList<>();
    ArrayList<VariableDeclarator> allGlobalFields = new ArrayList<>();

    public void addDeclaration(ClassOrInterfaceDeclaration c){
        allClasses.add(c);
    }

    public void addDeclaration(MethodDeclaration c){
        allMethods.add(c);
    }

    public void addDeclaration(VariableDeclarator c){
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
            System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Parameter List");
            }
        }
    }

    public void temporaryField(){
        
    }


    //Long Method checker with additional methods for functionality
    public void longMethodMedium(){
        for(MethodDeclaration m : allMethods){
            int statementC = 0;
            for(Statement statement : m.getBody().get().getStatements()){
                if(!(statement instanceof BlockStmt)){
                    statementC++;
                }
             //   System.out.println(statement);
                statementC = childChecker(statement, statementC);
                }
            if(statementC > 20){
                System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Method (medium)");
            }
        }
    }

    public int childChecker(Statement s, int count){
        for(Node child : s.getChildNodes()){
            if(child instanceof Statement){
                if(!(child instanceof BlockStmt)){
                    count++;
                    //System.out.println(child);
                }
                count = childChecker(child, count);
            }
        }
        return count;
    }

    public int childChecker(Node c, int count) {
        for(Node child : c.getChildNodes()){
            if(child instanceof Statement || child instanceof FieldDeclaration || child instanceof ClassOrInterfaceDeclaration || child instanceof MethodDeclaration || child instanceof ConstructorDeclaration){
                if(!(child instanceof BlockStmt || child instanceof ClassOrInterfaceDeclaration || child instanceof MethodDeclaration || child instanceof ConstructorDeclaration)){
                    count++;
                    //System.out.println(child + " - " + count);
                }
                count = childChecker(child, count);
            }
        }
        return count;
    }

    public void longClassMedium(){
        //field declaration statement checker
        for (ClassOrInterfaceDeclaration c : allClasses) {
            int statementC = 0;
            for(Node node : c.getChildNodes()){
                if(node instanceof Statement || node instanceof FieldDeclaration){
                    statementC++;
                    //System.out.println(node + " - " + statementC);
                    statementC = childChecker(node, statementC);
                } else if(node instanceof MethodDeclaration || node instanceof ClassOrInterfaceDeclaration || node instanceof ConstructorDeclaration){
                    statementC = childChecker(node, statementC);
                }
            }
           if(statementC > 100){
                System.out.println("BAD SMELL ("+ c.getNameAsString() +") - Long Class (medium)");
            }

        }
    }

    public void messageChain() {
        for (MethodDeclaration m : allMethods) {
            int messageChainLength = getMessageChainLength(m.getBody().get(), 0);
            if (messageChainLength > 2) {
                System.out.println("BAD SMELL (" + m.getNameAsString() + ") - Message Chain");
            }
        }
    }

    private int getMessageChainLength(Node node, int chainC) {
        if (node instanceof MethodCallExpr) {
            chainC++;
        }
        for (Node child : node.getChildNodes()) {
            chainC = getMessageChainLength(child, chainC);
        }
        return chainC;
    }
}
