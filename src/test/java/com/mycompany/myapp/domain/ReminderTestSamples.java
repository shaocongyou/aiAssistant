package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReminderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Reminder getReminderSample1() {
        return new Reminder().id(1L).message("message1").repeatInterval("repeatInterval1");
    }

    public static Reminder getReminderSample2() {
        return new Reminder().id(2L).message("message2").repeatInterval("repeatInterval2");
    }

    public static Reminder getReminderRandomSampleGenerator() {
        return new Reminder()
            .id(longCount.incrementAndGet())
            .message(UUID.randomUUID().toString())
            .repeatInterval(UUID.randomUUID().toString());
    }
}
