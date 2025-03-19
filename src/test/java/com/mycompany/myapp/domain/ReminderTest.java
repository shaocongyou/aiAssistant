package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ReminderTestSamples.*;
import static com.mycompany.myapp.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReminderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Reminder.class);
        Reminder reminder1 = getReminderSample1();
        Reminder reminder2 = new Reminder();
        assertThat(reminder1).isNotEqualTo(reminder2);

        reminder2.setId(reminder1.getId());
        assertThat(reminder1).isEqualTo(reminder2);

        reminder2 = getReminderSample2();
        assertThat(reminder1).isNotEqualTo(reminder2);
    }

    @Test
    void taskTest() {
        Reminder reminder = getReminderRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        reminder.setTask(taskBack);
        assertThat(reminder.getTask()).isEqualTo(taskBack);

        reminder.task(null);
        assertThat(reminder.getTask()).isNull();
    }
}
