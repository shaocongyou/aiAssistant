package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GoalTestSamples.*;
import static com.mycompany.myapp.domain.ReminderTestSamples.*;
import static com.mycompany.myapp.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void reminderTest() {
        Task task = getTaskRandomSampleGenerator();
        Reminder reminderBack = getReminderRandomSampleGenerator();

        task.addReminder(reminderBack);
        assertThat(task.getReminders()).containsOnly(reminderBack);
        assertThat(reminderBack.getTask()).isEqualTo(task);

        task.removeReminder(reminderBack);
        assertThat(task.getReminders()).doesNotContain(reminderBack);
        assertThat(reminderBack.getTask()).isNull();

        task.reminders(new HashSet<>(Set.of(reminderBack)));
        assertThat(task.getReminders()).containsOnly(reminderBack);
        assertThat(reminderBack.getTask()).isEqualTo(task);

        task.setReminders(new HashSet<>());
        assertThat(task.getReminders()).doesNotContain(reminderBack);
        assertThat(reminderBack.getTask()).isNull();
    }

    @Test
    void goalTest() {
        Task task = getTaskRandomSampleGenerator();
        Goal goalBack = getGoalRandomSampleGenerator();

        task.setGoal(goalBack);
        assertThat(task.getGoal()).isEqualTo(goalBack);

        task.goal(null);
        assertThat(task.getGoal()).isNull();
    }
}
