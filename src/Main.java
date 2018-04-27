import parser.Combinators;
import parser.Primitives;

public class Main {

    public static void main(String[] args) {
        String input = "abc def";

        var bind = Combinators.bind(Primitives.item(), c -> inp -> Primitives.result(c).apply(inp)).apply(input);

        var seq = Combinators.seq(Primitives.item(), Primitives.item()).apply("abc");
        
        var sat1 = Combinators.sat(Character::isLowerCase).apply(input);
        
        var sat2 = Combinators.sat(Character::isUpperCase).apply(input);

        System.out.println("result: " + Primitives.result("0").apply(input));
        System.out.println("zero..: " + Primitives.zero().apply(input));
        System.out.println("item..: " + Primitives.item().apply(input));
        System.out.println("bind..: " + bind);
        System.out.println("seq...: " + seq);
        System.out.println("sat1..: " + sat1);
        System.out.println("sat2..: " + sat2);
        System.out.println("char..: " + Combinators.character('a').apply(input));
        System.out.println("char..: " + Combinators.character('b').apply(input));
        System.out.println("plus..: " + Combinators.plus(Combinators.lower(), Combinators.lower()).apply(input));
        System.out.println("word..: " + Combinators.word().apply("Yes!"));
        System.out.println("string: " + Combinators.string("ab").apply(input));
        System.out.println("string: " + Combinators.string("abc").apply(input));
        System.out.println("string: " + Combinators.string("abcd").apply(input));
        System.out.println("many..: " + Combinators.many(Primitives.item()).apply(input));
        
        System.out.println("ident.: " + Combinators.ident().apply(""));
        System.out.println("ident.: " + Combinators.ident().apply("a"));
        System.out.println("ident.: " + Combinators.ident().apply("A"));
        System.out.println("ident.: " + Combinators.ident().apply("aa"));
        System.out.println("ident.: " + Combinators.ident().apply("aab c"));
        
        
    }

}
