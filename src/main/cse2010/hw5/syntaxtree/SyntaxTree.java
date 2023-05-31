package cse2010.hw5.syntaxtree;

import cse2010.hw5.tree.Position;
import cse2010.hw5.tree.binary.linked.LinkedBinaryTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;


/**
 * Linked-based Arithmetic expression tree.
 */
public class SyntaxTree extends LinkedBinaryTree<String> {

    /**
     * Evaluate syntax tree.
     * @return evaluated arithmetic expression
     */
    public double evaluate() {
        Map<String, Integer> operators = new HashMap<>();
        operators.put("(", 1);
        operators.put("*", 1);
        operators.put("/", 1);
        operators.put("+", 1);
        operators.put("-", 1);
        operators.put(")", 1);

        String postfixExpression = toPostFix();
        String[] tokens = postfixExpression.split(" ");

        Stack<Double> operandStack = new Stack<>();

        for (String token : tokens) {
            if (Utils.isNumeric(token)) {
                double operand = Double.parseDouble(token);
                operandStack.push(operand);
            } else if (operators.containsKey(token)) {
                if (operandStack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression");
                }

                double rightOperand = operandStack.pop();
                double leftOperand = operandStack.pop();

                double result = performOperation(leftOperand, rightOperand, token);
                operandStack.push(result);
            }
        }

        if (operandStack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression");
        }

        return operandStack.pop();

    }

    private double performOperation(double leftOperand, double rightOperand, String operator) {
        switch (operator) {
            case "+":
                return leftOperand + rightOperand;
            case "-":
                return leftOperand - rightOperand;
            case "*":
                return leftOperand * rightOperand;
            case "/":
                if (rightOperand == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return leftOperand / rightOperand;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    /**
     * Returns postfix expression corresponding to this syntax tree.
     * @return postfix representation of this syntax three
     */
    public String toPostFix() {
        return cvtToString(postOrder());
    }

    /**
     * Returns prefix expression corresponding to this syntax tree.
     * @return prefix representation of this syntax three
     */
    public String toPreFix() {
        return cvtToString(preOrder());
    }

    /**
     * Returns fully parenthesized infix expression corresponding to this syntax tree.
     * @return fully parenthesized prefix representation of this syntax three
     */
    public String toInfix() {
        return toInfix(root());
    }

    /**
     * Returns fully parenthesized infix expression corresponding to this syntax subtree.
     * @return fully parenthesized infix representation of this syntax subtree
     */
    private String toInfix(Position<String> position) {
        Node<String> node = validate(position);
        StringBuilder builder = new StringBuilder();

        if(isOperator(node.getElement())){
            builder.append("(");
            builder.append(toInfix(left(node)));
            builder.append(" ");
            builder.append(node.getElement());
            builder.append(" ");
            builder.append(toInfix(right(node)));
            builder.append(")");
        }else{
            builder.append(node.getElement());
        }

        return builder.toString();
    }

    private boolean isOperator(String element){
        return element.equals("+") ||element.equals("-") || element.equals("*") || element.equals("/");
    }

    /**
     * Returns a formatted string representation of tree hierarchy.
     * The formatted string representation of the expression tree corresponsing
     * to {@code (a + b) * (c - d)} looks as follow:
     * *
     *   +
     *     a
     *     b
     *   -
     *     c
     *     d
     * @return a formatted string representation of tree hierarchy
     */
    public String showTree() {
        return indentTree(root(), 0);
    }

    /**
     * Returns a formatted string representation of the subtree hierarchy.
     * @param position a node position
     * @param level indentation level; 0 means no indentation; the unit of
     *              the indentation level is two spaces.
     * @return  a formatted string representation of the subtree hierarchy
     */
    private String indentTree(Position<String> position, int level) {
        Node<String> node = validate(position);
        StringBuilder builder = new StringBuilder();

        for(int i=0; i<level; i++){
            builder.append("  ");
        }

        builder.append(node.getElement());
        builder.append('\n');

        if(left(node) != null){
            builder.append(indentTree(left(node), level+1));
        }

        if (right(node) != null) {
            builder.append(indentTree(right(node), level + 1));
        }

        return builder.toString();
    }

    /**
     * Convert list of Positions to a serialized string in which
     * each element of the position is delimited by the ' ' character.
     * @param positions a list of Position<String>s
     * @return a string serialized as a list of elements
     */
    private String cvtToString(List<Position<String>> positions) {
        return positions.stream().map(Position::getElement).collect(Collectors.joining(" "));
    }
}

