package org.togetherjava.event.elevator.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

public final class ConcurrentUtils {
    private ConcurrentUtils() {}

    /**
     * Construct a {@link ForkJoinTask} for each member of the specified collection that performs the specified action,
     * then submit them to the common {@link java.util.concurrent.ForkJoinPool ForkJoinPool} and wait for their completion.
     */
    public static <V> void performTasksInParallel(Collection<V> targets, Consumer<V> action) {
        List<? extends ForkJoinTask<?>> tasks = targets.stream().map(target -> new RecursiveAction() {
            @Override
            protected void compute() {
                action.accept(target);
            }
        }).toList();
        ForkJoinTask.invokeAll(tasks);
    }
}
