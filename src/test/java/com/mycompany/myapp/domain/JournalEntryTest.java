package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.JournalEntryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalEntryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalEntry.class);
        JournalEntry journalEntry1 = getJournalEntrySample1();
        JournalEntry journalEntry2 = new JournalEntry();
        assertThat(journalEntry1).isNotEqualTo(journalEntry2);

        journalEntry2.setId(journalEntry1.getId());
        assertThat(journalEntry1).isEqualTo(journalEntry2);

        journalEntry2 = getJournalEntrySample2();
        assertThat(journalEntry1).isNotEqualTo(journalEntry2);
    }
}
