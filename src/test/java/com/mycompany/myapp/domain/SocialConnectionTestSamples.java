package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SocialConnectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SocialConnection getSocialConnectionSample1() {
        return new SocialConnection().id(1L).friendUsername("friendUsername1").status("status1");
    }

    public static SocialConnection getSocialConnectionSample2() {
        return new SocialConnection().id(2L).friendUsername("friendUsername2").status("status2");
    }

    public static SocialConnection getSocialConnectionRandomSampleGenerator() {
        return new SocialConnection()
            .id(longCount.incrementAndGet())
            .friendUsername(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
