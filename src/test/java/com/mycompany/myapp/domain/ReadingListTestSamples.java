package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReadingListTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReadingList getReadingListSample1() {
        return new ReadingList().id(1L).title("title1").status("status1");
    }

    public static ReadingList getReadingListSample2() {
        return new ReadingList().id(2L).title("title2").status("status2");
    }

    public static ReadingList getReadingListRandomSampleGenerator() {
        return new ReadingList().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).status(UUID.randomUUID().toString());
    }
}
