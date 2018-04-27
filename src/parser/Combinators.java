package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import utils.StringUtils;
import utils.Tuple;

import parser.Primitives;

public abstract class Combinators {

    /**
     * <code>
     * bind :: Parser a -> (a -> Parser b) -> Parser b<br>
     * p ‘bind‘ f = \inp -> concat [f v inp’ | (v,inp’) <- p inp]
     * </code>
     * 
     * @param p
     * @param q
     * @return
     */
    public static <A, B> Parser<B> bind(Parser<A> p, Function<A, Parser<B>> f) {
        return input -> p.apply(input).concatMap(f);
    }

    /**
     * * <code>
     * seq :: Parser a -> Parser b -> Parser (a,b)
     * p ‘seq‘ q = p ‘bind‘ \x -> q ‘bind‘ \y -> result (x,y)
     * </code>
     * 
     * @param p
     * @param q
     * @return
     */
    public static <A, B> Parser<Tuple<A, B>> seq(Parser<A> p, Parser<B> q) {
        return bind(p, x -> bind(q, y -> Primitives.result(Tuple.of(x, y))));
    }

    /**
     * <code>
     * sat :: (Char -> Bool) -> Parser Char <br>
     * sat p = item ‘bind‘ \x -> if p x then result x else zero
     * </code>
     * 
     * @param p
     * @return
     */
    public static Parser<Character> sat(Predicate<Character> p) {
        return bind(Primitives.item(), x -> {
            if (p.test(x)) {
                return Primitives.result(x);
            } else {
                return Primitives.zero();
            }
        });
    }

    public static Parser<Character> character(Character c) {
        return sat(c::equals);
    }

    public static Parser<Character> digit() {
        return sat(Character::isDigit);
    }

    public static Parser<Character> lower() {
        return sat(Character::isLowerCase);
    }

    public static Parser<Character> upper() {
        return sat(Character::isUpperCase);
    }

    public static <A> Parser<A> plus(Parser<A> p, Parser<A> q) {
        return input -> {
            var x = p.apply(input);

            x.deck.addAll(q.apply(input).deck);

            return x;
        };
    }

    public static Parser<Character> letter() {
        return plus(lower(), upper());
    }

    public static Parser<Character> alphaNumeric() {
        return plus(letter(), digit());
    }

    public static Parser<String> word() {
        return bind(many(letter()), xs -> Primitives.result(StringUtils.asString(xs)));
    }

    public static Parser<String> string(String str) {
        Optional<Tuple<Character, String>> opt;

        if (StringUtils.isNullOrEmpty(str) || !(opt = StringUtils.uncons(str)).isPresent())
            return Primitives.result("");

        var tuple = opt.get();

        char x = tuple.fst;

        String xs = tuple.snd;

        return bind(character(x), a -> bind(string(xs), b -> Primitives.result(x + xs)));
    }

    public static <A> Parser<List<A>> many(Parser<A> p) {
        var q = bind(p, x -> bind(many(p), xs -> {
            List<A> ys = new ArrayList<>();
            ys.add(x);
            ys.addAll(xs);
            return Primitives.result(ys);
        }));

        return plus(q, Primitives.result(new ArrayList<>()));
    }

    public static Parser<String> ident() {
        return bind(lower(), x -> bind(many(alphaNumeric()), xs -> Primitives.result(x + StringUtils.asString(xs))));
    }
}
