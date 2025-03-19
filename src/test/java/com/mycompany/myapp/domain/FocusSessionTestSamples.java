package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FocusSessionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FocusSession getFocusSessionSample1() {
        return new FocusSession().id(1L).duration(1).task("task1");
    }

    public static FocusSession getFocusSessionSample2() {
        return new FocusSession().id(2L).duration(2).task("task2");
    }

    public static FocusSession getFocusSessionRandomSampleGenerator() {
        return new FocusSession().id(longCount.incrementAndGet()).duration(intCount.incrementAndGet()).task(UUID.randomUUID().toString());
    }
}
