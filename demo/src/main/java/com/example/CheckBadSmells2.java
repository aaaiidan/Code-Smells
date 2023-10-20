package com.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CheckBadSmells2{

    VoidVisitorAdapter<?> lcs = new LargeClassEasy();
    VoidVisitorAdapter<?> lme = new LongMethodEasy();
    VoidVisitorAdapter<?> lpl = new LongParameterList();
    VoidVisitorAdapter<?> lmm = new LongMethodMedium();
    VoidVisitorAdapter<?> lcm = new LongClassMedium();
    VoidVisitorAdapter<?> mc = new MessageChain();

    public void run(CompilationUnit cu){
        lcs.visit(cu, null);
        lme.visit(cu, null);
        lpl.visit(cu, null);
        lmm.visit(cu, null);
        lcm.visit(cu, null);
        mc.visit(cu, null);
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
                    System.out.println(node + " - " + statementC);
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
                System.out.println("BAD SMELL (" + m.toString() +")");
            }
            super.visit(m, arg);
        }

        private int messageChainLength(MethodCallExpr m, int chainC){
            if(m.getScope().get().isMethodCallExpr()){
                chainC++;
                chainC = messageChainLength((MethodCallExpr) m.getScope().get(), chainC);
            }
            return chainC;
        }
    }
}