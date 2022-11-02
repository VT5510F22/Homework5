package hw5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import hw5.sets.CoarseSet;
import hw5.sets.FineSet;
import hw5.sets.LazySet;
import hw5.sets.LockFreeSet;
import hw5.sets.OptimisticSet;
import hw5.sets.Set;

public class Runner {
    private static final int UPPER_BOUND = 100;
    private static final int ITERATIONS = 10000;
    private static final int BYTE_PADDING = 64; // to avoid false sharing
    private static final float ADD_LIMIT = 0.1f;
    private static final float REMOVE_LIMIT = 0.2f;

    private static Set<Integer> mySet;
    private static boolean[] containsResults;

    public static void main(String[] args) throws Throwable {
        mySet = getSet(args[1]);
        int threadCount = Integer.parseInt(args[2]);
        List<Callable<Long>> calls = getCallables(threadCount);
        ExecutorService excs = Executors.newFixedThreadPool(threadCount);

        containsResults = new boolean[threadCount * BYTE_PADDING];

        // Blocks until everything is done; equivalent to joining threads
        long nanos = 0;
        for (Future<Long> f : excs.invokeAll(calls)) {
            try {
                nanos += f.get();
            } catch (ExecutionException e) {
                throw e.getCause(); // unwrapping the ExecutionException
            }
        }

        // nanos will give us total thread time spent operating, but is that what we want?

        // So contains() usage doesn't get optimized out
        System.out.println(containsResults);
    }

    private static Set<Integer> getSet(String setType) {
        switch(SetType.valueOf(setType)) {
            case CoarseSet:
                return new CoarseSet<>();
            case FineSet:
                return new FineSet<>();
            case LazySet:
                return new LazySet<>();
            case LockFreeSet:
                return new LockFreeSet<>();
            case OptimisticSet:
                return new OptimisticSet<>();
        }

        return null; // ERROR
    }

    private static List<Callable<Long>> getCallables(int threadCount) {
        List<Callable<Long>> calls = new ArrayList<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int x = i; // so compiler doesn't complain about variable capture
            calls.add(() -> doStuff(x));
        }

        return calls;
    }

    private static long doStuff(int index) {
        Random rand = ThreadLocalRandom.current();
        long start = System.nanoTime();

        for (int i = 0; i < ITERATIONS; i++) {
            int item = rand.nextInt(UPPER_BOUND);
            float activity = rand.nextFloat();

            if (activity < ADD_LIMIT) {
                mySet.add(item);
            } else if (activity < REMOVE_LIMIT) {
                mySet.remove(item);
            } else {
                containsResults[index * BYTE_PADDING] = mySet.contains(item);
            }
        }

        return System.nanoTime() - start;
    }
}
