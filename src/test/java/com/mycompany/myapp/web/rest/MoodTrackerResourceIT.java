package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MoodTrackerAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.MoodTracker;
import com.mycompany.myapp.repository.MoodTrackerRepository;
import com.mycompany.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link MoodTrackerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MoodTrackerResourceIT {

    private static final String DEFAULT_MOOD = "AAAAAAAAAA";
    private static final String UPDATED_MOOD = "BBBBBBBBBB";

    private static final Integer DEFAULT_INTENSITY = 1;
    private static final Integer UPDATED_INTENSITY = 2;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/mood-trackers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MoodTrackerRepository moodTrackerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMoodTrackerMockMvc;

    private MoodTracker moodTracker;

    private MoodTracker insertedMoodTracker;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MoodTracker createEntity() {
        return new MoodTracker().mood(DEFAULT_MOOD).intensity(DEFAULT_INTENSITY).date(DEFAULT_DATE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MoodTracker createUpdatedEntity() {
        return new MoodTracker().mood(UPDATED_MOOD).intensity(UPDATED_INTENSITY).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        moodTracker = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMoodTracker != null) {
            moodTrackerRepository.delete(insertedMoodTracker);
            insertedMoodTracker = null;
        }
    }

    @Test
    @Transactional
    void createMoodTracker() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MoodTracker
        var returnedMoodTracker = om.readValue(
            restMoodTrackerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MoodTracker.class
        );

        // Validate the MoodTracker in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMoodTrackerUpdatableFieldsEquals(returnedMoodTracker, getPersistedMoodTracker(returnedMoodTracker));

        insertedMoodTracker = returnedMoodTracker;
    }

    @Test
    @Transactional
    void createMoodTrackerWithExistingId() throws Exception {
        // Create the MoodTracker with an existing ID
        moodTracker.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMoodTrackerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isBadRequest());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMoodIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moodTracker.setMood(null);

        // Create the MoodTracker, which fails.

        restMoodTrackerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIntensityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moodTracker.setIntensity(null);

        // Create the MoodTracker, which fails.

        restMoodTrackerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moodTracker.setDate(null);

        // Create the MoodTracker, which fails.

        restMoodTrackerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        moodTracker.setCreatedAt(null);

        // Create the MoodTracker, which fails.

        restMoodTrackerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMoodTrackers() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        // Get all the moodTrackerList
        restMoodTrackerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(moodTracker.getId().intValue())))
            .andExpect(jsonPath("$.[*].mood").value(hasItem(DEFAULT_MOOD)))
            .andExpect(jsonPath("$.[*].intensity").value(hasItem(DEFAULT_INTENSITY)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getMoodTracker() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        // Get the moodTracker
        restMoodTrackerMockMvc
            .perform(get(ENTITY_API_URL_ID, moodTracker.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(moodTracker.getId().intValue()))
            .andExpect(jsonPath("$.mood").value(DEFAULT_MOOD))
            .andExpect(jsonPath("$.intensity").value(DEFAULT_INTENSITY))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMoodTracker() throws Exception {
        // Get the moodTracker
        restMoodTrackerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMoodTracker() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moodTracker
        MoodTracker updatedMoodTracker = moodTrackerRepository.findById(moodTracker.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMoodTracker are not directly saved in db
        em.detach(updatedMoodTracker);
        updatedMoodTracker.mood(UPDATED_MOOD).intensity(UPDATED_INTENSITY).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);

        restMoodTrackerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMoodTracker.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMoodTracker))
            )
            .andExpect(status().isOk());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMoodTrackerToMatchAllProperties(updatedMoodTracker);
    }

    @Test
    @Transactional
    void putNonExistingMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, moodTracker.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moodTracker))
            )
            .andExpect(status().isBadRequest());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(moodTracker))
            )
            .andExpect(status().isBadRequest());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMoodTrackerWithPatch() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moodTracker using partial update
        MoodTracker partialUpdatedMoodTracker = new MoodTracker();
        partialUpdatedMoodTracker.setId(moodTracker.getId());

        partialUpdatedMoodTracker.date(UPDATED_DATE);

        restMoodTrackerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMoodTracker.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMoodTracker))
            )
            .andExpect(status().isOk());

        // Validate the MoodTracker in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMoodTrackerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMoodTracker, moodTracker),
            getPersistedMoodTracker(moodTracker)
        );
    }

    @Test
    @Transactional
    void fullUpdateMoodTrackerWithPatch() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the moodTracker using partial update
        MoodTracker partialUpdatedMoodTracker = new MoodTracker();
        partialUpdatedMoodTracker.setId(moodTracker.getId());

        partialUpdatedMoodTracker.mood(UPDATED_MOOD).intensity(UPDATED_INTENSITY).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);

        restMoodTrackerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMoodTracker.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMoodTracker))
            )
            .andExpect(status().isOk());

        // Validate the MoodTracker in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMoodTrackerUpdatableFieldsEquals(partialUpdatedMoodTracker, getPersistedMoodTracker(partialUpdatedMoodTracker));
    }

    @Test
    @Transactional
    void patchNonExistingMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, moodTracker.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moodTracker))
            )
            .andExpect(status().isBadRequest());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(moodTracker))
            )
            .andExpect(status().isBadRequest());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMoodTracker() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        moodTracker.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMoodTrackerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(moodTracker)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MoodTracker in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMoodTracker() throws Exception {
        // Initialize the database
        insertedMoodTracker = moodTrackerRepository.saveAndFlush(moodTracker);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the moodTracker
        restMoodTrackerMockMvc
            .perform(delete(ENTITY_API_URL_ID, moodTracker.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return moodTrackerRepository.count();
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

    protected MoodTracker getPersistedMoodTracker(MoodTracker moodTracker) {
        return moodTrackerRepository.findById(moodTracker.getId()).orElseThrow();
    }

    protected void assertPersistedMoodTrackerToMatchAllProperties(MoodTracker expectedMoodTracker) {
        assertMoodTrackerAllPropertiesEquals(expectedMoodTracker, getPersistedMoodTracker(expectedMoodTracker));
    }

    protected void assertPersistedMoodTrackerToMatchUpdatableProperties(MoodTracker expectedMoodTracker) {
        assertMoodTrackerAllUpdatablePropertiesEquals(expectedMoodTracker, getPersistedMoodTracker(expectedMoodTracker));
    }
}
