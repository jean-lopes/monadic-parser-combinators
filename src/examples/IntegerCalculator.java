package examples;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
import java.util.function.BinaryOperator;

import parser.Combinators;
import parser.Parser;
import parser.Result;
import utils.Tuple;

public class IntegerCalculator {         
    
    public static Parser<BigInteger> expr() {
        return Combinators.chainl1(term(), addOp());
    }

    public static Parser<BigInteger> term() {
        return Combinators.chainl1(factor(), mulOp());
    }

    public static Parser<BigInteger> factor() {
        return Combinators.chainr1(val(), expOp());
    }
    
    public static Parser<BigInteger> val() {
        return input -> Combinators.plus(Combinators.nat(), 
                Combinators.parens(expr())).apply(input);
    }

    public static Parser<BinaryOperator<BigInteger>> expOp() {
        return Combinators.operators(List.of(exp));
    }
    
    public static Parser<BinaryOperator<BigInteger>> mulOp() {
        return Combinators.operators(List.of(mul, div));
    }
    
    public static Parser<BinaryOperator<BigInteger>> addOp() {
        return Combinators.operators(List.of(add, sub));
    }
    
    static Tuple<String, BinaryOperator<BigInteger>> add = Tuple.of("+", BigInteger::add);
    
    static Tuple<String, BinaryOperator<BigInteger>> sub = Tuple.of("-", BigInteger::subtract);        
    
    static Tuple<String, BinaryOperator<BigInteger>> mul = Tuple.of("*", BigInteger::multiply);
    
    static Tuple<String, BinaryOperator<BigInteger>> div = Tuple.of("/", BigInteger::divide);  
    
    static Tuple<String, BinaryOperator<BigInteger>> exp = Tuple.of("^", (a, b) -> a.pow(b.intValue()));    
 
    public static void main(String[] args) {   
        System.out.println("Type expressions like: 1+1");
        
        System.out.print("Expression: ");
        try (Scanner scanner = new Scanner(System.in)) {
            String line;
            
            while (!"quit".equalsIgnoreCase(line = scanner.nextLine())) {
                Result<BigInteger> result = expr().apply(line);
                
                if (result.isEmpty()) {
                    System.out.println("> Parse failed!");
                } else {
                    System.out.println("> " + result.pop().fst);
                }
                
                System.out.print("Expression: ");
            }
        }
    }
}
