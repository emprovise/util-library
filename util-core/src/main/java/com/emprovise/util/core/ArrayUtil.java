package com.emprovise.util.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ArrayUtil {

    public static <T> Optional<T> tryGetAs(Object[] array, int position, Class<T> clazz) {
        if (array == null || array.length < position + 1 || !clazz.isInstance(array[position])) {
            return Optional.empty();
        }
        return Optional.of((T) array[position]);
    }

    public static <T> Collection<T> getPageFromCollection(Collection<T> originalSerialList, int pageSize, int whichPage) {
        int elementCountStartInclusive = whichPage * pageSize;

        if (null == originalSerialList || originalSerialList.size() < 1) {
            throw new IllegalArgumentException("originalSerialList must have at least one value");
        }
        if (elementCountStartInclusive > originalSerialList.size() - 1) {
            throw new IllegalArgumentException("page requested out of bounds");
        }

        int endAssumingFullPage = (whichPage + 1) * pageSize;
        int elementCountEndExclusive = endAssumingFullPage > originalSerialList.size()
                ? originalSerialList.size()
                : endAssumingFullPage;

        List<T> tempList = new ArrayList<T>(originalSerialList);
        List<T> subsetList = new ArrayList<T>(pageSize);
        subsetList.addAll(tempList.subList(elementCountStartInclusive, elementCountEndExclusive));
        return subsetList;
    }

    public static int determineNumberOfPages(Collection<?> fullList, int pageSize) {
        if (fullList == null || fullList.isEmpty()) {
            return 0;
        }
        int numPages = fullList.size() / pageSize;
        int remainder = fullList.size() % pageSize;
        return remainder > 0 ? (numPages + 1) : numPages;
    }
}
