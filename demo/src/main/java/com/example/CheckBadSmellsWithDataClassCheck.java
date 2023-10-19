package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void dataClass(){

        //ArrayList<String> methodsNames = new ArrayList<>();
        //boolean isDataClass = false;
        /* 
        for(MethodDeclaration m: allMethods){
            if(m.getNameAsString().startsWith("get") || m.getNameAsString().startsWith("set")){
                methodsNames.add(m.getNameAsString());
                isDataClass = true;
            }else{
                isDataClass = false;
                methodsNames.clear();
                break;
            }
        }
        System.out.println(methodsNames);
        System.out.println(hasGettersAndSetters);
        */
        //All code above is just to check the name of method but I don't think it is needed anymore for the data class check.

        ArrayList<String> methodsNames = new ArrayList<>(); //Initializing an array list to store all methods that follow the data class structure.

        for(ClassOrInterfaceDeclaration cl: allClasses){ //Tries to iterate through every class and gets the class name to be outputted.
            //System.out.println("Class Name: " + cl.getNameAsString());
            boolean isDataClass = false;
            boolean hasComplexLogic = false;
        for(MethodDeclaration m: allMethods){
            for(Statement statement : m.getBody().get().getStatements()){
                if((statement instanceof ReturnStmt) || ((statement instanceof ExpressionStmt) && (m.getParameters().size() == 1))){ //Must change m.getParamter().size() to equal a count of the amount of statements in the method.
                    //System.out.println(statement);
                    isDataClass = true;
                    if(!methodsNames.contains(m.getNameAsString())){ //Adding methods that follow the data class structure to an array list.
                        methodsNames.add(m.getNameAsString());
                    }
                }else{
                    isDataClass = false;
                }

                if(!(statement instanceof ReturnStmt) || !(statement instanceof ExpressionStmt)){
                    hasComplexLogic = true;
                    isDataClass = false;
                }else{
                    isDataClass = true;
                    if(!methodsNames.contains(m.getNameAsString())){
                        methodsNames.add(m.getNameAsString());
                    }
                } 
            }
        }

        if(isDataClass == true && hasComplexLogic == false){
            System.out.println("BAD SMELL (" + cl.getNameAsString() + ") - is a Data Class");
        }
        System.out.println(isDataClass);
        System.out.println(methodsNames); //Helper print statements to see what methods follow the data class structure & if it is a data class.

    }


    }
}
