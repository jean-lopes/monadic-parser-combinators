package utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class StringUtils {
  
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
    
    public static String asString(List<Character> xs) {
        return xs.stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }
} 
