package utility;

import com.google.common.base.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import static java.util.function.Function.identity;

public class Maps {
    /*public static Map<String, Object> map(Object... keysAndValues) {
        Preconditions.checkArgument(keysAndValues.length % 2 == 0, "Please provide an even number of arguments");
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i].toString(), keysAndValues[i + 1]);
        }
        return map;
    }

    public static <K, K2, V, V2> Map<K2, V2> convert(Map<K, V> map, Function<K, K2> keyFunction, Function<V, V2> valueFunction){
        return map.entrySet().stream().collect(Collectors.toMap(
                key_and_value -> keyFunction.apply(key_and_value.getKey()),
                key_and_value -> valueFunction.apply(key_and_value.getValue()),
                (older, newer) -> newer
        ));
    }

    public static <K, V, K2> Map<K2, V> convertKey(Map<K, V> map, Function<K, K2> keyFunction) {
        return convert(map, keyFunction, identity());
    }

    public static <K, V, V2> Map<K, V2> convertValue(Map<K, V> map, Function<V, V2> valueFunction) {
        return convert(map, identity(), valueFunction);
    }

    public static <K, V> Map<K, V> filterToValues(Map<K, V> map, Predicate<V> filter){
        return map.entrySet().stream()
                .filter(e -> filter.test(e.getValue()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
        ));
    }

    public static <K, V, V2> Map<K, V2> filterToValuesOfType(Map<K, V> map, Class<V2> clazz){
        Map<K, V> filtered = filterToValues(map, clazz::isInstance);
        return convertValue(filtered, clazz::cast);
    }*/
}
