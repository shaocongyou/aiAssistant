package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GoalTestSamples.*;
import static com.mycompany.myapp.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GoalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Goal.class);
        Goal goal1 = getGoalSample1();
        Goal goal2 = new Goal();
        assertThat(goal1).isNotEqualTo(goal2);

        goal2.setId(goal1.getId());
        assertThat(goal1).isEqualTo(goal2);

        goal2 = getGoalSample2();
        assertThat(goal1).isNotEqualTo(goal2);
    }

    @Test
    void taskTest() {
        Goal goal = getGoalRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        goal.addTask(taskBack);
        assertThat(goal.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getGoal()).isEqualTo(goal);

        goal.removeTask(taskBack);
        assertThat(goal.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getGoal()).isNull();

        goal.tasks(new HashSet<>(Set.of(taskBack)));
        assertThat(goal.getTasks()).containsOnly(taskBack);
        assertThat(taskBack.getGoal()).isEqualTo(goal);

        goal.setTasks(new HashSet<>());
        assertThat(goal.getTasks()).doesNotContain(taskBack);
        assertThat(taskBack.getGoal()).isNull();
    }
}
