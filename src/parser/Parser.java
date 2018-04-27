package parser;

import java.util.function.Function;

/***
 * <code>type Parser a = String -> [(a,String)]</code>
 * 
 * @author jeanl
 *
 * @param <A>
 */
public interface Parser<A> extends Function<String, Result<A>> {

}
