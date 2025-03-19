package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FinanceRecordTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FinanceRecord getFinanceRecordSample1() {
        return new FinanceRecord().id(1L).description("description1").category("category1");
    }

    public static FinanceRecord getFinanceRecordSample2() {
        return new FinanceRecord().id(2L).description("description2").category("category2");
    }

    public static FinanceRecord getFinanceRecordRandomSampleGenerator() {
        return new FinanceRecord()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString());
    }
}
