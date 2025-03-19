package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.HabitAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Habit;
import com.mycompany.myapp.domain.enumeration.HabitType;
import com.mycompany.myapp.repository.HabitRepository;
import com.mycompany.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link HabitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HabitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final HabitType DEFAULT_HABIT_TYPE = HabitType.HEALTH;
    private static final HabitType UPDATED_HABIT_TYPE = HabitType.LEARNING;

    private static final String DEFAULT_FREQUENCY = "AAAAAAAAAA";
    private static final String UPDATED_FREQUENCY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/habits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHabitMockMvc;

    private Habit habit;

    private Habit insertedHabit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habit createEntity() {
        return new Habit()
            .name(DEFAULT_NAME)
            .habitType(DEFAULT_HABIT_TYPE)
            .frequency(DEFAULT_FREQUENCY)
            .startDate(DEFAULT_START_DATE)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Habit createUpdatedEntity() {
        return new Habit()
            .name(UPDATED_NAME)
            .habitType(UPDATED_HABIT_TYPE)
            .frequency(UPDATED_FREQUENCY)
            .startDate(UPDATED_START_DATE)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        habit = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedHabit != null) {
            habitRepository.delete(insertedHabit);
            insertedHabit = null;
        }
    }

    @Test
    @Transactional
    void createHabit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Habit
        var returnedHabit = om.readValue(
            restHabitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Habit.class
        );

        // Validate the Habit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertHabitUpdatableFieldsEquals(returnedHabit, getPersistedHabit(returnedHabit));

        insertedHabit = returnedHabit;
    }

    @Test
    @Transactional
    void createHabitWithExistingId() throws Exception {
        // Create the Habit with an existing ID
        habit.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHabitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        habit.setName(null);

        // Create the Habit, which fails.

        restHabitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkHabitTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        habit.setHabitType(null);

        // Create the Habit, which fails.

        restHabitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFrequencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        habit.setFrequency(null);

        // Create the Habit, which fails.

        restHabitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        habit.setActive(null);

        // Create the Habit, which fails.

        restHabitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHabits() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        // Get all the habitList
        restHabitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(habit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].habitType").value(hasItem(DEFAULT_HABIT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].frequency").value(hasItem(DEFAULT_FREQUENCY)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getHabit() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        // Get the habit
        restHabitMockMvc
            .perform(get(ENTITY_API_URL_ID, habit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(habit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.habitType").value(DEFAULT_HABIT_TYPE.toString()))
            .andExpect(jsonPath("$.frequency").value(DEFAULT_FREQUENCY))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingHabit() throws Exception {
        // Get the habit
        restHabitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHabit() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the habit
        Habit updatedHabit = habitRepository.findById(habit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHabit are not directly saved in db
        em.detach(updatedHabit);
        updatedHabit
            .name(UPDATED_NAME)
            .habitType(UPDATED_HABIT_TYPE)
            .frequency(UPDATED_FREQUENCY)
            .startDate(UPDATED_START_DATE)
            .active(UPDATED_ACTIVE);

        restHabitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHabit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedHabit))
            )
            .andExpect(status().isOk());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHabitToMatchAllProperties(updatedHabit);
    }

    @Test
    @Transactional
    void putNonExistingHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(put(ENTITY_API_URL_ID, habit.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isBadRequest());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(habit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(habit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHabitWithPatch() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the habit using partial update
        Habit partialUpdatedHabit = new Habit();
        partialUpdatedHabit.setId(habit.getId());

        partialUpdatedHabit.name(UPDATED_NAME).habitType(UPDATED_HABIT_TYPE).frequency(UPDATED_FREQUENCY);

        restHabitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHabit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHabit))
            )
            .andExpect(status().isOk());

        // Validate the Habit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHabitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHabit, habit), getPersistedHabit(habit));
    }

    @Test
    @Transactional
    void fullUpdateHabitWithPatch() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the habit using partial update
        Habit partialUpdatedHabit = new Habit();
        partialUpdatedHabit.setId(habit.getId());

        partialUpdatedHabit
            .name(UPDATED_NAME)
            .habitType(UPDATED_HABIT_TYPE)
            .frequency(UPDATED_FREQUENCY)
            .startDate(UPDATED_START_DATE)
            .active(UPDATED_ACTIVE);

        restHabitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHabit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHabit))
            )
            .andExpect(status().isOk());

        // Validate the Habit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHabitUpdatableFieldsEquals(partialUpdatedHabit, getPersistedHabit(partialUpdatedHabit));
    }

    @Test
    @Transactional
    void patchNonExistingHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, habit.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(habit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(habit))
            )
            .andExpect(status().isBadRequest());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHabit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        habit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHabitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(habit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Habit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHabit() throws Exception {
        // Initialize the database
        insertedHabit = habitRepository.saveAndFlush(habit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the habit
        restHabitMockMvc
            .perform(delete(ENTITY_API_URL_ID, habit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return habitRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Habit getPersistedHabit(Habit habit) {
        return habitRepository.findById(habit.getId()).orElseThrow();
    }

    protected void assertPersistedHabitToMatchAllProperties(Habit expectedHabit) {
        assertHabitAllPropertiesEquals(expectedHabit, getPersistedHabit(expectedHabit));
    }

    protected void assertPersistedHabitToMatchUpdatableProperties(Habit expectedHabit) {
        assertHabitAllUpdatablePropertiesEquals(expectedHabit, getPersistedHabit(expectedHabit));
    }
}
