package examples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import parser.Combinators;
import parser.Parser;
import parser.Primitives;
import parser.Result;
import utils.Tuple;

/**
 * <b>Grammar:</b><br>
 * <code>
 * expr   ::= expr addop factor | factor<br>
 * addop  ::= + | -<br>
 * factor ::= nat | ( expr )<br>
 * </code>
 * 
 * @author Jean Lopes
 */
public class IntegerCalculator {
    
    public static Parser<BigInteger> expr() {
        return Combinators.chainl1(factor(), addOp());
    }

    public static Parser<BigInteger> factor() {
        return input -> Combinators.plus(Combinators.nat(), Combinators.parens(expr())).apply(input);
    }

    public static Parser<BinaryOperator<BigInteger>> addOp() {
        return Combinators.operators(List.of(Tuple.of(Combinators.character('+'), BigInteger::add),
                Tuple.of(Combinators.character('-'), BigInteger::subtract)));
    }

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
