package ca.brocku.cosc3p97_a1;

import java.util.Stack;

/** @title COSC 3P97 A1
 * @author Geoffrey Jensen
 * Student #: 7148710
 *
 * Evaluates an infix expression given in a String. Converts infix to postfix, then calculates.
 *  Some functions were derived from the pseudocode from
 *  https://www.geeksforgeeks.org/convert-infix-expression-to-postfix-expression/
 *
 */
public class Calculator {

    public Calculator(){}

    private enum TokenType{
        OPERATOR,
        OPERAND,
        LEFT_BRACKET,
        RIGHT_BRACKET;
    }
    private class Node {
        String token;
        TokenType type;
        Node next;
        public Node(String token, TokenType type, Node next){
            this.token = token;
            this.type = type;
            this.next = next;
        }

        public Node getSingleClone(){
            return new Node(this.token, this.type, null);
        }
    }

    /** Tokenize operands and operators. Converts a String expression to a linked list, with each
     *  node being a token
     *
     * @param formula
     * @return
     * @throws EmptyFormulaException
     */
    private Node tokenize(String formula) throws EmptyFormulaException{
        if(formula.isEmpty()) throw new EmptyFormulaException();

        Node head = new Node("", null, null);
        Node tail = head;
        String token = ""; token += parseNegative(formula.charAt(0));
        TokenType currType = getType(formula.charAt(0));
        char c;
        for(int i = 1; i <  formula.length(); i++){
            c = formula.charAt(i);
            TokenType compare = getType(c);
            if(currType == TokenType.OPERAND && compare == TokenType.OPERAND){
                token+=parseNegative(c);
            }
            else{
                tail.next = new Node(token, currType, null);
                System.out.println(token + " " + currType);
                tail = tail.next;
                token=""; token+=parseNegative(c);
                currType = compare;
            }
        }
        tail.next = new Node(token, currType, null);
        return head;
    }

    /** Used to replace n with -
     *
     * @param c
     * @return
     */
    private char parseNegative(char c){
        if(c=='n'){
            return '-';
        } else return c;
    }

    private TokenType getType(char c){
        switch (c){
            case'(':
                return TokenType.LEFT_BRACKET;
            case')':
                return TokenType.RIGHT_BRACKET;
            case'+':
            case'-':
            case'*':
            case'/':
                return TokenType.OPERATOR;
            default:
                return TokenType.OPERAND;
        }
    }

    /** Reorder formula list from infix to postfix
     *
     * @param head
     * @return
     */
    private Node infixToPostfix(Node head){
        Node curr = head.next;
        Node postfix = new Node("", null, null);
        Node tail = postfix;
        Stack<Node> stack = new Stack<>();
        while(curr!=null){
            if(curr.type==TokenType.OPERAND){
                tail.next = curr.getSingleClone();
                tail = tail.next;
            }
            else if(curr.token.equals("(")){
                stack.push(curr.getSingleClone());
            }
            else if(curr.token.equals(")")){
                while(!stack.isEmpty() && !stack.peek().token.equals("(")){
                    tail.next = stack.pop();
                    tail=tail.next;
                }
                stack.pop(); // remove "("
            }
            else{
                assert(curr.type==TokenType.OPERATOR);
                while(!stack.isEmpty() && precedence(curr.token.charAt(0)) <= precedence(stack.peek().token.charAt(0))){
                    tail.next = stack.pop();
                    tail = tail.next;
                }
                stack.push(curr.getSingleClone());
            }
            curr = curr.next;
        }

        while(!stack.isEmpty()){
            tail.next = stack.pop();
            tail = tail.next;
        }

        return postfix;
    }

    private int precedence(char c){
        switch(c){
            case'*':
            case'/':
                return 2;
            case'+':
            case'-':
                return 1;
        }
        return -1;
    }

    private boolean isBracketBalanced(String formula){
        int openBrackets = 0;
        for(char c : formula.toCharArray()){
            if(c=='(') openBrackets++;
            if(c==')'){
                if(openBrackets-- <= 0){
                    return false;
                }
            }
        }
        return openBrackets==0;
    }

    private boolean isOperatorBalanced(Node head){
        TokenType expected = TokenType.OPERAND;
        Node curr = head.next;
        while(curr!=null){
            if(curr.type == TokenType.OPERAND || curr.type == TokenType.OPERATOR){
                if(curr.type!=expected){
                    return false;
                }
                else{
                    expected = toggleExpectedTokenType(expected);
                }
            }
            curr = curr.next;
        }
        return expected==TokenType.OPERATOR ? true : false;
    }

    private TokenType toggleExpectedTokenType(TokenType type){
        switch(type){
            case OPERAND: return TokenType.OPERATOR;
            case OPERATOR: return TokenType.OPERAND;
            default: return null;
        }
    }

    /** Calculate postfix expression represented as a linked list
     *
     * @param head
     * @return
     */
    private double calculatePostfix(Node head) throws DivideByZeroException{
        Stack<Node> stack = new Stack<>();
        Node curr = head.next;
        while(curr!=null){
            if(curr.type == TokenType.OPERAND) {
                stack.push(curr.getSingleClone());
            }
            else if(curr.type == TokenType.OPERATOR){
                double op2 = Double.parseDouble(stack.pop().token);
                double op1 = Double.parseDouble(stack.pop().token);
                double result = 0.0;
                switch(curr.token){
                    case"+":
                        result = op1 + op2;
                        break;
                    case"-":
                        result = op1 - op2;
                        break;
                    case"*":
                        result = op1 * op2;
                        break;
                    case"/":
                        if(op2 == 0) throw new DivideByZeroException();
                        result = op1 / op2;
                        break;
                    default:
                        assert false;

                }
                stack.push(new Node(result+"", TokenType.OPERAND, null));
            }
            else{
                assert false;
            }
            curr = curr.next;
        }
        return Double.parseDouble(stack.pop().token);
    }

    private boolean isNegativeSyntaxCorrect(Node head) {
        Stack<Node> stack = new Stack<>();
        Node curr = head.next;
        int n;
        while(curr!=null){
            n = 0;
            for(int i = 0; i < curr.token.length(); i++){
                if(curr.token.charAt(i) == '-') n++;
                if(curr.token.charAt(i) == '-' && i > 0) return false;
            }
            if(n > 1){
                return false;
            }
            curr = curr.next;
        }
        return true;
    }
    private boolean isDecimalSyntaxCorrect(Node head) {
        Stack<Node> stack = new Stack<>();
        Node curr = head.next;
        int d;
        while(curr!=null){
            d = 0;
            for(int i = 0; i < curr.token.length(); i++){
                if(curr.token.charAt(i) == '.') d++;
            }
            if(d > 1){
                return false;
            }
            curr = curr.next;
        }
        return true;
    }
    public double calculate(String formula) throws CalculatorException{
        Node head = tokenize(formula);
        if(!isBracketBalanced(formula)) throw new SyntaxErrorException();
        if(!isOperatorBalanced(head)) throw new SyntaxErrorException();
        if(!isNegativeSyntaxCorrect(head)) throw new SyntaxErrorException();
        if(!isDecimalSyntaxCorrect(head)) throw new SyntaxErrorException();
        Node postfix = infixToPostfix(head);
        double result;
        try{
            result = calculatePostfix(postfix);
        }
        catch(DivideByZeroException e){
            throw new DivideByZeroException();
        }
        return result;
    }
}
