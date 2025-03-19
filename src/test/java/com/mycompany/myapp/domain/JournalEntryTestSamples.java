package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JournalEntryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static JournalEntry getJournalEntrySample1() {
        return new JournalEntry().id(1L).title("title1").mood("mood1");
    }

    public static JournalEntry getJournalEntrySample2() {
        return new JournalEntry().id(2L).title("title2").mood("mood2");
    }

    public static JournalEntry getJournalEntryRandomSampleGenerator() {
        return new JournalEntry().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).mood(UUID.randomUUID().toString());
    }
}
