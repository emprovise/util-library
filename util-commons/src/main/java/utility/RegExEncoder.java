package utility;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class RegExEncoder {
    private static final Set<Character> CHARS_TO_ESCAPE = newHashSet('\\', '.', '[', '{', '(', ')', '*', '+', '?', '^', '$', '|');

    public String encode(String input) {
        if(input == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for(char c : input.toCharArray()) {
            if(CHARS_TO_ESCAPE.contains(c)) {
                result.append('\\').append(c);
            }
            else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
