package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.MoodTrackerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MoodTrackerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MoodTracker.class);
        MoodTracker moodTracker1 = getMoodTrackerSample1();
        MoodTracker moodTracker2 = new MoodTracker();
        assertThat(moodTracker1).isNotEqualTo(moodTracker2);

        moodTracker2.setId(moodTracker1.getId());
        assertThat(moodTracker1).isEqualTo(moodTracker2);

        moodTracker2 = getMoodTrackerSample2();
        assertThat(moodTracker1).isNotEqualTo(moodTracker2);
    }
}
