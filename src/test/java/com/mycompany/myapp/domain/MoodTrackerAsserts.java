package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MoodTrackerAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMoodTrackerAllPropertiesEquals(MoodTracker expected, MoodTracker actual) {
        assertMoodTrackerAutoGeneratedPropertiesEquals(expected, actual);
        assertMoodTrackerAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMoodTrackerAllUpdatablePropertiesEquals(MoodTracker expected, MoodTracker actual) {
        assertMoodTrackerUpdatableFieldsEquals(expected, actual);
        assertMoodTrackerUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMoodTrackerAutoGeneratedPropertiesEquals(MoodTracker expected, MoodTracker actual) {
        assertThat(actual)
            .as("Verify MoodTracker auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMoodTrackerUpdatableFieldsEquals(MoodTracker expected, MoodTracker actual) {
        assertThat(actual)
            .as("Verify MoodTracker relevant properties")
            .satisfies(a -> assertThat(a.getMood()).as("check mood").isEqualTo(expected.getMood()))
            .satisfies(a -> assertThat(a.getIntensity()).as("check intensity").isEqualTo(expected.getIntensity()))
            .satisfies(a -> assertThat(a.getDate()).as("check date").isEqualTo(expected.getDate()))
            .satisfies(a -> assertThat(a.getCreatedAt()).as("check createdAt").isEqualTo(expected.getCreatedAt()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMoodTrackerUpdatableRelationshipsEquals(MoodTracker expected, MoodTracker actual) {
        // empty method
    }
}
