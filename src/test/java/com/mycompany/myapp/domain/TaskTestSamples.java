package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Task getTaskSample1() {
        return new Task().id(1L).title("title1").description("description1").priority(1);
    }

    public static Task getTaskSample2() {
        return new Task().id(2L).title("title2").description("description2").priority(2);
    }

    public static Task getTaskRandomSampleGenerator() {
        return new Task()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .priority(intCount.incrementAndGet());
    }
}
