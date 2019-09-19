package utility;

import java.util.function.Supplier;

public class Check {
    public static void throwIf(boolean condition, Supplier<RuntimeException> orElse){
        if(condition){
            throw orElse.get();
        }
    }
}
