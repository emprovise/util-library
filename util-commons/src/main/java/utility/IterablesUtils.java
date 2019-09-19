package utility;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

//import javax.annotation.Nullable;
//import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class IterablesUtils { //NOSONAR
    /*public static <E> Iterable<E> distinct(Iterable<E> original) {
        return ImmutableSet.copyOf(original);
    }

    @NotNull
    public static <T extends MemberwiseCloneable> List<T> cloneAll(Iterable<T> original) {
       List<T> returnLi = Lists.newArrayList();

        try {
            for (T item : original){
                returnLi.add((T)item.memberwiseClone());
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return returnLi;
    }

    public static boolean isNullOrEmpty(@Nullable Iterable e){
         return e == null || Iterables.isEmpty(e);
    }

    @NotNull
    public static <T> List<T> unNullify(@Nullable List<T> tLIst) {
        if(tLIst == null){
            return newArrayList();
        }
        return tLIst;
    }

    @NotNull
    public static Iterable notNull(@Nullable Iterable tLIst) {
        if(tLIst == null){
            return newArrayList();
        }
        return tLIst;
    }

    public static boolean isNullOrEmpty(@Nullable Object[] stuff){
        return stuff == null || stuff.length < 1;
    }*/

}
