package utility;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Collection;

public abstract class IterableFunction<T,U> implements Function<T,U> {
    public Collection<U> applyAll(Iterable<T> stuff){
        return FluentIterable.from(stuff).transform(this).toList();
    }
}
