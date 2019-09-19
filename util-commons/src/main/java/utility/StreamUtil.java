package utility;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {
    /*public static <T> List<T> filterToClass(List<?> list, Class<T> clazz) {
        return filterToClass(list.stream(), clazz).collect(Collectors.toList());
    }

    public static <T> Collection<T> filterToClass(Collection<?> collection, Class<T> clazz) {
        return filterToClass(collection.stream(), clazz).collect(Collectors.toList());
    }

    public static <T> Set<T> filterToClass(Set<?> set, Class<T> clazz) {
        return filterToClass(set.stream(), clazz).collect(Collectors.toSet());
    }

    public static <T> Iterable<T> filterToClass(Iterable<?> iterable, Class<T> clazz) {
        return filterToClass(toStream(iterable), clazz)::iterator;
    }

    public static Stream<?> toStream(Iterable<?> iterable) {
        Spliterator<?> spliterator = Spliterators.spliteratorUnknownSize(iterable.iterator(), Spliterator.ORDERED);
        return StreamSupport.stream(spliterator, false);
    }

    public static <T> Stream<T> filterToClass(Stream<?> stream, Class<T> clazz) {
        return stream.filter(clazz::isInstance).map(clazz::cast);
    }

    public static <L, R> void zip(List<L> first, List<R> next, BiConsumer<L, R> zipper) {
        IntStream.range(0, Math.min(first.size(), next.size()))
                .forEach(i -> zipper.accept(first.get(i), next.get(i)));
    }*/
}
