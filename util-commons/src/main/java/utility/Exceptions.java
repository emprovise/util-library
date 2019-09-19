package utility;

import java.util.Optional;
import java.util.function.Supplier;

public class Exceptions {
    public static <T, E extends RuntimeException> Optional<T> tryGet(Supplier<T> supplyer, Class<E> exceptionType) {
        try {
            return Optional.ofNullable(supplyer.get());
        } catch (RuntimeException e){
            if(exceptionType.isAssignableFrom(e.getClass())){
                return Optional.empty();
            }
            throw e;
        }
    }
}
