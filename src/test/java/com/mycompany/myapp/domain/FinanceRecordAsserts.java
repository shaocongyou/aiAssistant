package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AssertUtils.bigDecimalCompareTo;
import static org.assertj.core.api.Assertions.assertThat;

public class FinanceRecordAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertFinanceRecordAllPropertiesEquals(FinanceRecord expected, FinanceRecord actual) {
        assertFinanceRecordAutoGeneratedPropertiesEquals(expected, actual);
        assertFinanceRecordAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertFinanceRecordAllUpdatablePropertiesEquals(FinanceRecord expected, FinanceRecord actual) {
        assertFinanceRecordUpdatableFieldsEquals(expected, actual);
        assertFinanceRecordUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertFinanceRecordAutoGeneratedPropertiesEquals(FinanceRecord expected, FinanceRecord actual) {
        assertThat(actual)
            .as("Verify FinanceRecord auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertFinanceRecordUpdatableFieldsEquals(FinanceRecord expected, FinanceRecord actual) {
        assertThat(actual)
            .as("Verify FinanceRecord relevant properties")
            .satisfies(a -> assertThat(a.getDescription()).as("check description").isEqualTo(expected.getDescription()))
            .satisfies(a ->
                assertThat(a.getAmount()).as("check amount").usingComparator(bigDecimalCompareTo).isEqualTo(expected.getAmount())
            )
            .satisfies(a -> assertThat(a.getCategory()).as("check category").isEqualTo(expected.getCategory()))
            .satisfies(a -> assertThat(a.getDate()).as("check date").isEqualTo(expected.getDate()))
            .satisfies(a -> assertThat(a.getCreatedAt()).as("check createdAt").isEqualTo(expected.getCreatedAt()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertFinanceRecordUpdatableRelationshipsEquals(FinanceRecord expected, FinanceRecord actual) {
        // empty method
    }
}
