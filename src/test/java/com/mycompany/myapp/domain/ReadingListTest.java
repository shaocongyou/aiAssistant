package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ReadingListTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReadingListTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReadingList.class);
        ReadingList readingList1 = getReadingListSample1();
        ReadingList readingList2 = new ReadingList();
        assertThat(readingList1).isNotEqualTo(readingList2);

        readingList2.setId(readingList1.getId());
        assertThat(readingList1).isEqualTo(readingList2);

        readingList2 = getReadingListSample2();
        assertThat(readingList1).isNotEqualTo(readingList2);
    }
}
