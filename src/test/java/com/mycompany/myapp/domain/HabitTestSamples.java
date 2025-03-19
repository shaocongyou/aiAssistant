package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HabitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Habit getHabitSample1() {
        return new Habit().id(1L).name("name1").frequency("frequency1");
    }

    public static Habit getHabitSample2() {
        return new Habit().id(2L).name("name2").frequency("frequency2");
    }

    public static Habit getHabitRandomSampleGenerator() {
        return new Habit().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).frequency(UUID.randomUUID().toString());
    }
}
