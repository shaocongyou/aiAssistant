package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.JournalEntryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.JournalEntry;
import com.mycompany.myapp.repository.JournalEntryRepository;
import com.mycompany.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
 * Integration tests for the {@link JournalEntryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JournalEntryResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_MOOD = "AAAAAAAAAA";
    private static final String UPDATED_MOOD = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/journal-entries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJournalEntryMockMvc;

    private JournalEntry journalEntry;

    private JournalEntry insertedJournalEntry;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntry createEntity() {
        return new JournalEntry()
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE)
            .mood(DEFAULT_MOOD)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalEntry createUpdatedEntity() {
        return new JournalEntry()
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .mood(UPDATED_MOOD)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        journalEntry = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedJournalEntry != null) {
            journalEntryRepository.delete(insertedJournalEntry);
            insertedJournalEntry = null;
        }
    }

    @Test
    @Transactional
    void createJournalEntry() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the JournalEntry
        var returnedJournalEntry = om.readValue(
            restJournalEntryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntry)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            JournalEntry.class
        );

        // Validate the JournalEntry in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertJournalEntryUpdatableFieldsEquals(returnedJournalEntry, getPersistedJournalEntry(returnedJournalEntry));

        insertedJournalEntry = returnedJournalEntry;
    }

    @Test
    @Transactional
    void createJournalEntryWithExistingId() throws Exception {
        // Create the JournalEntry with an existing ID
        journalEntry.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntry)))
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalEntry.setTitle(null);

        // Create the JournalEntry, which fails.

        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntry)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalEntry.setCreatedAt(null);

        // Create the JournalEntry, which fails.

        restJournalEntryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntry)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJournalEntries() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get all the journalEntryList
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalEntry.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CONTENT))))
            .andExpect(jsonPath("$.[*].mood").value(hasItem(DEFAULT_MOOD)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        // Get the journalEntry
        restJournalEntryMockMvc
            .perform(get(ENTITY_API_URL_ID, journalEntry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journalEntry.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64.getEncoder().encodeToString(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.mood").value(DEFAULT_MOOD))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingJournalEntry() throws Exception {
        // Get the journalEntry
        restJournalEntryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry
        JournalEntry updatedJournalEntry = journalEntryRepository.findById(journalEntry.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJournalEntry are not directly saved in db
        em.detach(updatedJournalEntry);
        updatedJournalEntry
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .mood(UPDATED_MOOD)
            .createdAt(UPDATED_CREATED_AT);

        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJournalEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedJournalEntry))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJournalEntryToMatchAllProperties(updatedJournalEntry);
    }

    @Test
    @Transactional
    void putNonExistingJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalEntry.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalEntry)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJournalEntryWithPatch() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry using partial update
        JournalEntry partialUpdatedJournalEntry = new JournalEntry();
        partialUpdatedJournalEntry.setId(journalEntry.getId());

        partialUpdatedJournalEntry.mood(UPDATED_MOOD).createdAt(UPDATED_CREATED_AT);

        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalEntry))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalEntryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedJournalEntry, journalEntry),
            getPersistedJournalEntry(journalEntry)
        );
    }

    @Test
    @Transactional
    void fullUpdateJournalEntryWithPatch() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalEntry using partial update
        JournalEntry partialUpdatedJournalEntry = new JournalEntry();
        partialUpdatedJournalEntry.setId(journalEntry.getId());

        partialUpdatedJournalEntry
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .mood(UPDATED_MOOD)
            .createdAt(UPDATED_CREATED_AT);

        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalEntry))
            )
            .andExpect(status().isOk());

        // Validate the JournalEntry in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalEntryUpdatableFieldsEquals(partialUpdatedJournalEntry, getPersistedJournalEntry(partialUpdatedJournalEntry));
    }

    @Test
    @Transactional
    void patchNonExistingJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, journalEntry.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalEntry))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJournalEntry() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalEntry.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalEntryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(journalEntry)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalEntry in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJournalEntry() throws Exception {
        // Initialize the database
        insertedJournalEntry = journalEntryRepository.saveAndFlush(journalEntry);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the journalEntry
        restJournalEntryMockMvc
            .perform(delete(ENTITY_API_URL_ID, journalEntry.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return journalEntryRepository.count();
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

    protected JournalEntry getPersistedJournalEntry(JournalEntry journalEntry) {
        return journalEntryRepository.findById(journalEntry.getId()).orElseThrow();
    }

    protected void assertPersistedJournalEntryToMatchAllProperties(JournalEntry expectedJournalEntry) {
        assertJournalEntryAllPropertiesEquals(expectedJournalEntry, getPersistedJournalEntry(expectedJournalEntry));
    }

    protected void assertPersistedJournalEntryToMatchUpdatableProperties(JournalEntry expectedJournalEntry) {
        assertJournalEntryAllUpdatablePropertiesEquals(expectedJournalEntry, getPersistedJournalEntry(expectedJournalEntry));
    }
}
