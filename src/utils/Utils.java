package utils;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public abstract class Utils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static Optional<Tuple<Character, String>> uncons(String s) {
        if (isNullOrEmpty(s))
            return Optional.empty();

        char x = s.charAt(0);

        String xs = s.substring(1);

        return Optional.of(Tuple.of(x, xs));
    }

    public static <A> List<A> cons(A head, List<A> tail) {
        tail.add(0, head);
        return tail;
    }

    public static String asString(List<Character> xs) {
        return xs.stream().map(Object::toString).collect(Collectors.joining());
    }
    
    public static <A> A foldl(BinaryOperator<A> f, A x, List<A> xs) {        
        return xs.stream().reduce(x, f);
    }
}
