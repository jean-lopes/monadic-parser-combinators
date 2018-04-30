package parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import utils.Tuple;
import utils.Utils;

public abstract class Combinators {

    public static <A, B> Parser<B> bind(Parser<A> p, Function<A, Parser<B>> f) {
        return input -> p.apply(input).concatMap(f);
    }

    public static <A, B> Parser<B> then(Parser<A> p, Parser<B> q) {
        return bind(p, ignored -> bind(q, x -> Primitives.result(x)));
    }

    public static <A, B> Parser<Tuple<A, B>> seq(Parser<A> p, Parser<B> q) {
        return bind(p, x -> bind(q, y -> Primitives.result(Tuple.of(x, y))));
    }

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
        return bind(many(letter()), xs -> Primitives.result(Utils.asString(xs)));
    }

    public static Parser<String> string(String str) {
        Optional<Tuple<Character, String>> opt;

        if (Utils.isNullOrEmpty(str) || !(opt = Utils.uncons(str)).isPresent())
            return Primitives.result("");

        var tuple = opt.get();

        char x = tuple.fst;

        String xs = tuple.snd;

        return then(character(x), then(string(xs), Primitives.result(x + xs)));
    }

    public static <A> Parser<List<A>> many(Parser<A> p) {
        return plus(bind(p, x -> bind(many(p), xs -> Primitives.result(Utils.cons(x, xs)))),
                Primitives.result(new ArrayList<>()));
    }

    public static Parser<String> ident() {
        return bind(lower(), x -> bind(many(alphaNumeric()), xs -> Primitives.result(x + Utils.asString(xs))));
    }

    public static <A> Parser<List<A>> many1(Parser<A> p) {
        return bind(p, x -> bind(many(p), xs -> Primitives.result(Utils.cons(x, xs))));
    }

    public static Parser<BigInteger> nat() {
        return bind(many1(digit()), n -> Primitives.result(new BigInteger(Utils.asString(n))));
    }

    public static Parser<BigInteger> integer() {
        Parser<Function<BigInteger, BigInteger>> op = plus(
                bind(character('-'), c -> Primitives.result(BigInteger::negate)),
                Primitives.result(Function.identity()));

        return bind(op, f -> bind(nat(), n -> Primitives.result(f.apply(n))));
    }

    public static <A, B> Parser<List<A>> sepBy1(Parser<A> p, Parser<B> sep) {
        return bind(p, x -> bind(many(then(sep, p)), xs -> Primitives.result(Utils.cons(x, xs))));
    }

    public static <A, B> Parser<List<A>> sepBy(Parser<A> p, Parser<B> sep) {
        return plus(sepBy1(p, sep), Primitives.result(new ArrayList<>()));
    }

    public static <A, B, C> Parser<B> bracket(Parser<A> open, Parser<B> p, Parser<C> close) {
        return bind(then(open, p), x -> bind(close, c -> Primitives.result(x)));
    }

    public static <A> Parser<A> parens(Parser<A> p) {
        return bracket(character('('), p, character(')'));
    }

    public static <A> Parser<A> chainl1(Parser<A> p, Parser<BinaryOperator<A>> op) {
        Parser<UnaryOperator<A>> fy = bind(op, f -> bind(p, y -> Primitives.result(x -> f.apply(x, y))));
        
        Parser<List<UnaryOperator<A>>> fys = many(fy);

        BiFunction<A, List<UnaryOperator<A>>, A> foldl = (x, fs) -> fs.stream()
                .reduce(i -> i, (f, g) -> n -> g.apply(f.apply(n)))
                .apply(x);
        
        return bind(p, x -> bind(fys, ops -> Primitives.result(foldl.apply(x, ops))));
    }

    public static <A> Parser<A> chainl(Parser<A> p, Parser<BinaryOperator<A>> op, A v) {
        return plus(chainl1(p, op), Primitives.result(v));
    }

    public static <A> Parser<A> chainr1(Parser<A> p, Parser<BinaryOperator<A>> op) {
        return bind(p, x -> plus(bind(op, f -> bind(chainr1(p, op), y -> Primitives.result(f.apply(x, y)))),
                Primitives.result(x)));
    }

    public static <A> Parser<A> chainr(Parser<A> p, Parser<BinaryOperator<A>> op, A v) {
        return plus(chainr1(p, op), Primitives.result(v));
    }
    
    public static <A> Parser<BinaryOperator<A>> operators(List<Tuple<String, BinaryOperator<A>>> xs) {
        return xs.stream()
                .map(x -> then(string(x.fst), Primitives.result(x.snd)))
                .reduce(Primitives.zero(), Combinators::plus);
    }
}
