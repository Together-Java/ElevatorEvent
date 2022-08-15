package org.togetherjava.event.elevator.util;

import java.util.Collection;
import java.util.Objects;

public final class CollectionUtils {
    private CollectionUtils() {}

    /**
     * Equality check for collections that {@link java.util.ArrayDeque do not} override {@link #equals(Object) equals} for some reason.<br>
     * Will fall back to checking {@link  #equals(Object) equals} first before doing the manual check,
     * so it's the caller responsibility to determine whether they actually need this method
     * and avoid potential double iteration.<br>
     * Not thread-safe, requires external synchronization,
     * exception generation is delegated to iterators themselves.
     */
    public static boolean equals(Collection<?> c1, Collection<?> c2) {
        if (c1 == null || c2 == null) {
            return c1 == c2;
        }

        if (c1.equals(c2)) {
            return true;
        }

        if (c1.size() != c2.size()) {
            return false;
        }

        var i1 = c1.iterator();
        var i2 = c2.iterator();
        while (i1.hasNext()) {
            if (!Objects.equals(i1.next(), i2.next())) {
                return false;
            }
        }

        return !i2.hasNext();
    }
}
