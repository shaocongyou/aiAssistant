package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FocusSessionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.FocusSession;
import com.mycompany.myapp.repository.FocusSessionRepository;
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
 * Integration tests for the {@link FocusSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FocusSessionResourceIT {

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;

    private static final String DEFAULT_TASK = "AAAAAAAAAA";
    private static final String UPDATED_TASK = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/focus-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FocusSessionRepository focusSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFocusSessionMockMvc;

    private FocusSession focusSession;

    private FocusSession insertedFocusSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FocusSession createEntity() {
        return new FocusSession().duration(DEFAULT_DURATION).task(DEFAULT_TASK).date(DEFAULT_DATE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FocusSession createUpdatedEntity() {
        return new FocusSession().duration(UPDATED_DURATION).task(UPDATED_TASK).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        focusSession = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFocusSession != null) {
            focusSessionRepository.delete(insertedFocusSession);
            insertedFocusSession = null;
        }
    }

    @Test
    @Transactional
    void createFocusSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FocusSession
        var returnedFocusSession = om.readValue(
            restFocusSessionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FocusSession.class
        );

        // Validate the FocusSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFocusSessionUpdatableFieldsEquals(returnedFocusSession, getPersistedFocusSession(returnedFocusSession));

        insertedFocusSession = returnedFocusSession;
    }

    @Test
    @Transactional
    void createFocusSessionWithExistingId() throws Exception {
        // Create the FocusSession with an existing ID
        focusSession.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFocusSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isBadRequest());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDurationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        focusSession.setDuration(null);

        // Create the FocusSession, which fails.

        restFocusSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        focusSession.setDate(null);

        // Create the FocusSession, which fails.

        restFocusSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        focusSession.setCreatedAt(null);

        // Create the FocusSession, which fails.

        restFocusSessionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFocusSessions() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        // Get all the focusSessionList
        restFocusSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(focusSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)))
            .andExpect(jsonPath("$.[*].task").value(hasItem(DEFAULT_TASK)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFocusSession() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        // Get the focusSession
        restFocusSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, focusSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(focusSession.getId().intValue()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION))
            .andExpect(jsonPath("$.task").value(DEFAULT_TASK))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFocusSession() throws Exception {
        // Get the focusSession
        restFocusSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFocusSession() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the focusSession
        FocusSession updatedFocusSession = focusSessionRepository.findById(focusSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFocusSession are not directly saved in db
        em.detach(updatedFocusSession);
        updatedFocusSession.duration(UPDATED_DURATION).task(UPDATED_TASK).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);

        restFocusSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFocusSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFocusSession))
            )
            .andExpect(status().isOk());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFocusSessionToMatchAllProperties(updatedFocusSession);
    }

    @Test
    @Transactional
    void putNonExistingFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, focusSession.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(focusSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(focusSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFocusSessionWithPatch() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the focusSession using partial update
        FocusSession partialUpdatedFocusSession = new FocusSession();
        partialUpdatedFocusSession.setId(focusSession.getId());

        partialUpdatedFocusSession.duration(UPDATED_DURATION).task(UPDATED_TASK).date(UPDATED_DATE);

        restFocusSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFocusSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFocusSession))
            )
            .andExpect(status().isOk());

        // Validate the FocusSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFocusSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFocusSession, focusSession),
            getPersistedFocusSession(focusSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateFocusSessionWithPatch() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the focusSession using partial update
        FocusSession partialUpdatedFocusSession = new FocusSession();
        partialUpdatedFocusSession.setId(focusSession.getId());

        partialUpdatedFocusSession.duration(UPDATED_DURATION).task(UPDATED_TASK).date(UPDATED_DATE).createdAt(UPDATED_CREATED_AT);

        restFocusSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFocusSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFocusSession))
            )
            .andExpect(status().isOk());

        // Validate the FocusSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFocusSessionUpdatableFieldsEquals(partialUpdatedFocusSession, getPersistedFocusSession(partialUpdatedFocusSession));
    }

    @Test
    @Transactional
    void patchNonExistingFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, focusSession.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(focusSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(focusSession))
            )
            .andExpect(status().isBadRequest());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFocusSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        focusSession.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFocusSessionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(focusSession)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FocusSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFocusSession() throws Exception {
        // Initialize the database
        insertedFocusSession = focusSessionRepository.saveAndFlush(focusSession);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the focusSession
        restFocusSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, focusSession.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return focusSessionRepository.count();
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

    protected FocusSession getPersistedFocusSession(FocusSession focusSession) {
        return focusSessionRepository.findById(focusSession.getId()).orElseThrow();
    }

    protected void assertPersistedFocusSessionToMatchAllProperties(FocusSession expectedFocusSession) {
        assertFocusSessionAllPropertiesEquals(expectedFocusSession, getPersistedFocusSession(expectedFocusSession));
    }

    protected void assertPersistedFocusSessionToMatchUpdatableProperties(FocusSession expectedFocusSession) {
        assertFocusSessionAllUpdatablePropertiesEquals(expectedFocusSession, getPersistedFocusSession(expectedFocusSession));
    }
}
