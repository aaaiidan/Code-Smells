package com.example;

import java.util.ArrayList;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CheckBadSmells{

    VoidVisitorAdapter<?> getNameOfClass = new getNameOfClass();
    VoidVisitorAdapter<?> lcs = new LargeClassEasy();
    VoidVisitorAdapter<?> lme = new LongMethodEasy();
    VoidVisitorAdapter<?> lpl = new LongParameterList();
    VoidVisitorAdapter<?> lmm = new LongMethodMedium();
    VoidVisitorAdapter<?> lcm = new LongClassMedium();
    VoidVisitorAdapter<?> mc = new MessageChain();
    VoidVisitorAdapter<?> dc = new DataClass();
    VoidVisitorAdapter<ArrayList<CompilationUnit>> rb = new RefusedBequest();


    public void run(CompilationUnit cu, ArrayList<CompilationUnit> ASTs){

        getNameOfClass.visit(cu, null);
        lcs.visit(cu, null);
        lme.visit(cu, null);
        lpl.visit(cu, null);
        lmm.visit(cu, null);
        lcm.visit(cu, null);
        mc.visit(cu, null);
        dc.visit(cu, null);
        rb.visit(cu, ASTs);
     
    }

    private static class LargeClassEasy extends VoidVisitorAdapter{
        
        @Override
        public void visit(ClassOrInterfaceDeclaration c, Object arg){
            if ((c.getEnd().get().line - c.getBegin().get().line) > 100){
                    System.out.println("BAD SMELL ("+ c.getNameAsString() +") - Long Class (easy)");
                }
            super.visit(c, arg);
        }
    }

    private static class LongMethodEasy extends VoidVisitorAdapter{

        @Override
        public void visit(MethodDeclaration m, Object arg){
            if ((m.getEnd().get().line - m.getBegin().get().line) > 20){
                System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Method (easy)");
            }
             super.visit(m, arg);
        }
    }

    private static class LongParameterList extends VoidVisitorAdapter{

        @Override
        public void visit(MethodDeclaration m, Object arg){
            if(m.getParameters().size() >= 5){
                System.out.println("BAD SMELL ("+ m.getNameAsString() +") - Long Parameter List");
            }
            super.visit(m, arg);
        }
    }

    private static class LongMethodMedium extends VoidVisitorAdapter{

        @Override
        public void visit(MethodDeclaration m, Object arg){
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
            super.visit(m, arg);
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
    }

    private static class LongClassMedium extends VoidVisitorAdapter{

        @Override
        public void visit(ClassOrInterfaceDeclaration c, Object arg){
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
            super.visit(c, arg);
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
    }

    private static class MessageChain extends VoidVisitorAdapter{

        @Override
        public void visit(MethodCallExpr m, Object arg){
            int chainC = 0;
            if (messageChainLength(m, chainC) > 1){
                System.out.println("BAD SMELL ( " + m.toString() +" ) - Message Chain");
            }
            super.visit(m, arg);
        }

        private int messageChainLength(MethodCallExpr m, int chainC){
            if(m.getScope().isPresent() &&  m.getScope().get().isMethodCallExpr()){
                chainC++;
                chainC = messageChainLength((MethodCallExpr) m.getScope().get(), chainC);
            }
            return chainC;
        }
    }

    private static class DataClass extends VoidVisitorAdapter{

        @Override
        public void visit(ClassOrInterfaceDeclaration c, Object arg){
            boolean isDataClass = false;
            ArrayList<VariableDeclarator> globalFields = new ArrayList<>();
            for (FieldDeclaration field : c.getFields()) {
                for (VariableDeclarator v : field.getVariables()) {
                    globalFields.add(v);
                    //System.out.println(v);
                }
             }

            for (ConstructorDeclaration constuctor : c.getConstructors()) {
                for(Statement st : constuctor.getBody().getStatements()){
                    if(st.isExpressionStmt()){
                        ExpressionStmt expressionStmt = (ExpressionStmt) st;
                        for (VariableDeclarator v : globalFields) {
                            if(expressionStmt.toString().contains(v.getNameAsString())){
                                isDataClass = true;
                            }
                        }
                    }
                }
            }
            for(MethodDeclaration m : c.getMethods()){
                if(m.getBody().get().getStatements().size() == 1){
                    for(Statement statement : m.getBody().get().getStatements()){
                        if(statement.isReturnStmt()){
                            ReturnStmt returnStmt = (ReturnStmt) statement;
                            for (VariableDeclarator v : globalFields) {
                                if(returnStmt.getExpression().get().toString().contains(v.getNameAsString())){
                                    isDataClass = true;
                                }
                            }
                        } else if(statement.isExpressionStmt() && m.getParameters().size() == 1){
                            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
                            for (VariableDeclarator v : globalFields) {
                                if(expressionStmt.toString().contains(v.getNameAsString())){
                                    isDataClass = true;
                                }
                            }
                        } else {
                            isDataClass = false;
                        }
                    }
                } else {
                    isDataClass = false;
                    break;
                }

                if(!isDataClass){
                    break;
                }
            }

            if(isDataClass){
                System.out.println("BAD SMELL ("+ c.getNameAsString() +") - Data Class");
            }
            super.visit(c, arg);
        }
    }

    private static class RefusedBequest extends VoidVisitorAdapter<ArrayList<CompilationUnit>>{

        @Override
        public void visit(ClassOrInterfaceDeclaration c, ArrayList<CompilationUnit> ASTs){
            for (ClassOrInterfaceType extendedClass : c.getExtendedTypes()){
                //System.out.println(c.getNameAsString() + " extends " + extendedClass);
                ArrayList<MethodDeclaration> subClassMethods = new ArrayList<>();
                for (MethodDeclaration method : c.getMethods()) {
                    subClassMethods.add(method);
                }
                for (CompilationUnit cu : ASTs) {
                    //System.out.println(extendedClass.asString());
                    if(cu.getClassByName(extendedClass.getNameAsString()).isPresent()){
                        if (!subClassMethods.containsAll(cu.getClassByName(extendedClass.getNameAsString()).get().getMethods())){
                            System.out.println("BAD SMELL (" + c.getNameAsString() + " extends " + extendedClass.getNameAsString() +") - RefusedBequest");
                        }
                    }
                }
            }
                
            
            super.visit(c, ASTs);
        }
    }

    private static class getNameOfClass extends VoidVisitorAdapter{

        @Override
        public void visit(ClassOrInterfaceDeclaration c, Object args){
            System.out.println();
            System.out.println("---------- " + c.getNameAsString() + ".java ----------");
            System.out.println();
        }
    }
}