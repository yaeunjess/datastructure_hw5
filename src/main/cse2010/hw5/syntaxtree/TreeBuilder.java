package cse2010.hw5.syntaxtree;

import cse2010.hw5.tree.binary.linked.LinkedBinaryTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TreeBuilder {
    private static Map<String, Integer> operators = new HashMap<>();
    static {
        // We will consider only four binary operators: *, /, +, -
        operators.put("(", 1);
        operators.put("*", 1);
        operators.put("/", 1);
        operators.put("+", 1);
        operators.put("-", 1);
        operators.put(")", 1);
        // you may put other entry/entries if needed ...
    }

    private static Stack<SyntaxTree> operandStack = new Stack<>();
    private static Stack<String> operatorStack = new Stack<>();

    /**
     * Construct a syntax tree from infix arithmetic expression.
     * @param expression an infix arithmetic expression
     * @return the syntax tree
     */
    public static SyntaxTree buildFromInfix(String expression) {
        initStacks();

        String[] tokens = Utils.parse(expression);

        for(String token : tokens){
            //token이 피연산자(숫자,operand)라면
            if(!operators.containsKey(token)){
                SyntaxTree operandTree = new SyntaxTree();
                operandTree.addRoot(token);
                operandStack.push(operandTree);
            } else if(operators.containsKey(token)){
                //token이 연산자(operator)라면
                if(token.equals("(")){
                    operatorStack.push(token);
                }else if(token.equals(")")){
                    while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")){
                        String operator = operatorStack.pop();

                        SyntaxTree rightOperand = operandStack.pop();
                        SyntaxTree leftOperand = operandStack.pop();

                        SyntaxTree operatorTree = new SyntaxTree();
                        operatorTree.addRoot(operator);
                        operatorTree.attach(operatorTree.root(), leftOperand, rightOperand);

                        operandStack.push(operatorTree);
                    }

                    if (!operatorStack.isEmpty() && operatorStack.peek().equals("(")) {
                        operatorStack.pop();
                    }
                }else{
                    while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                        String operator = operatorStack.pop();

                        SyntaxTree rightOperand = operandStack.pop();
                        SyntaxTree leftOperand = operandStack.pop();

                        SyntaxTree operatorTree = new SyntaxTree();
                        operatorTree.addRoot(operator);
                        operatorTree.attach(operatorTree.root(), leftOperand, rightOperand);

                        operandStack.push(operatorTree);
                    }

                    operatorStack.push(token);
                }
            }
        }

        while(!operatorStack.isEmpty()){
            String operator = operatorStack.pop();

            SyntaxTree rightOperand = operandStack.pop();
            SyntaxTree leftOperand = operandStack.pop();

            SyntaxTree operatorTree = new SyntaxTree();
            operatorTree.addRoot(operator);
            operatorTree.attach(operatorTree.root(), leftOperand, rightOperand);

            operandStack.push(operatorTree);
        }

        return operandStack.pop();
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * Construct a syntax free from postfix arithmetic expression.
     * @param expression a postfix arithmetic expression
     * @return the syntax tree
     */
    public static SyntaxTree buildFromPostfix(String expression) {
        initStacks();

        String[] tokens = Utils.parse(expression);

        for(String token : tokens){
            //token이 피연산자(숫자,operand)라면
            if(!operators.containsKey(token)){
                SyntaxTree operandTree = new SyntaxTree();
                operandTree.addRoot(token);
                operandStack.push(operandTree);
            }else if(operators.containsKey(token)){
                //token이 연산자(operator)라면
                SyntaxTree rightOperand = operandStack.pop();
                SyntaxTree leftOperand = operandStack.pop();

                SyntaxTree operatorTree = new SyntaxTree();
                operatorTree.addRoot(token);
                operatorTree.attach(operatorTree.root(), leftOperand, rightOperand);

                operandStack.push(operatorTree);
            }
        }

        return operandStack.pop();
    }

    /**
     * Reset stacks
     */
    private static void initStacks() {
        operandStack.clear();
        operatorStack.clear();
    }
}

