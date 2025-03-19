package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MoodTrackerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static MoodTracker getMoodTrackerSample1() {
        return new MoodTracker().id(1L).mood("mood1").intensity(1);
    }

    public static MoodTracker getMoodTrackerSample2() {
        return new MoodTracker().id(2L).mood("mood2").intensity(2);
    }

    public static MoodTracker getMoodTrackerRandomSampleGenerator() {
        return new MoodTracker().id(longCount.incrementAndGet()).mood(UUID.randomUUID().toString()).intensity(intCount.incrementAndGet());
    }
}
