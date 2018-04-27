package parser;

import java.util.Optional;

import utils.StringUtils;
import utils.Tuple;

public abstract class Primitives {

    /***
     * <code>
     * result :: a -> Parser a<br>
     * result v = \inp -> [(v,inp)]
     * </code>
     * @param value
     * @return
     */
    public static <A> Parser<A> result(A value) {
        return input -> Result.of(Tuple.of(value, input));
    }

    public static <A> Parser<A> zero() {
        return input -> Result.empty();
    }

    public static Parser<Character> item() {
        return input -> {
            Optional<Tuple<Character, String>> opt;
            
            if (input.isEmpty() || !(opt = StringUtils.uncons(input)).isPresent())
                return Result.empty();

            return Result.of(opt.get());
        };
    }

}
