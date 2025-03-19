package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SocialConnectionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.SocialConnection;
import com.mycompany.myapp.repository.SocialConnectionRepository;
import com.mycompany.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SocialConnectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SocialConnectionResourceIT {

    private static final String DEFAULT_FRIEND_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_FRIEND_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/social-connections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SocialConnectionRepository socialConnectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SocialConnectionRepository socialConnectionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSocialConnectionMockMvc;

    private SocialConnection socialConnection;

    private SocialConnection insertedSocialConnection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialConnection createEntity() {
        return new SocialConnection().friendUsername(DEFAULT_FRIEND_USERNAME).status(DEFAULT_STATUS).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SocialConnection createUpdatedEntity() {
        return new SocialConnection().friendUsername(UPDATED_FRIEND_USERNAME).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        socialConnection = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSocialConnection != null) {
            socialConnectionRepository.delete(insertedSocialConnection);
            insertedSocialConnection = null;
        }
    }

    @Test
    @Transactional
    void createSocialConnection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SocialConnection
        var returnedSocialConnection = om.readValue(
            restSocialConnectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SocialConnection.class
        );

        // Validate the SocialConnection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSocialConnectionUpdatableFieldsEquals(returnedSocialConnection, getPersistedSocialConnection(returnedSocialConnection));

        insertedSocialConnection = returnedSocialConnection;
    }

    @Test
    @Transactional
    void createSocialConnectionWithExistingId() throws Exception {
        // Create the SocialConnection with an existing ID
        socialConnection.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSocialConnectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isBadRequest());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFriendUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialConnection.setFriendUsername(null);

        // Create the SocialConnection, which fails.

        restSocialConnectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialConnection.setStatus(null);

        // Create the SocialConnection, which fails.

        restSocialConnectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        socialConnection.setCreatedAt(null);

        // Create the SocialConnection, which fails.

        restSocialConnectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSocialConnections() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        // Get all the socialConnectionList
        restSocialConnectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(socialConnection.getId().intValue())))
            .andExpect(jsonPath("$.[*].friendUsername").value(hasItem(DEFAULT_FRIEND_USERNAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSocialConnectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(socialConnectionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSocialConnectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(socialConnectionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSocialConnectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(socialConnectionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSocialConnectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(socialConnectionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSocialConnection() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        // Get the socialConnection
        restSocialConnectionMockMvc
            .perform(get(ENTITY_API_URL_ID, socialConnection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(socialConnection.getId().intValue()))
            .andExpect(jsonPath("$.friendUsername").value(DEFAULT_FRIEND_USERNAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSocialConnection() throws Exception {
        // Get the socialConnection
        restSocialConnectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSocialConnection() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialConnection
        SocialConnection updatedSocialConnection = socialConnectionRepository.findById(socialConnection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSocialConnection are not directly saved in db
        em.detach(updatedSocialConnection);
        updatedSocialConnection.friendUsername(UPDATED_FRIEND_USERNAME).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restSocialConnectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSocialConnection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSocialConnection))
            )
            .andExpect(status().isOk());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSocialConnectionToMatchAllProperties(updatedSocialConnection);
    }

    @Test
    @Transactional
    void putNonExistingSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, socialConnection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(socialConnection))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(socialConnection))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSocialConnectionWithPatch() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialConnection using partial update
        SocialConnection partialUpdatedSocialConnection = new SocialConnection();
        partialUpdatedSocialConnection.setId(socialConnection.getId());

        partialUpdatedSocialConnection.friendUsername(UPDATED_FRIEND_USERNAME).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restSocialConnectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialConnection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSocialConnection))
            )
            .andExpect(status().isOk());

        // Validate the SocialConnection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSocialConnectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSocialConnection, socialConnection),
            getPersistedSocialConnection(socialConnection)
        );
    }

    @Test
    @Transactional
    void fullUpdateSocialConnectionWithPatch() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the socialConnection using partial update
        SocialConnection partialUpdatedSocialConnection = new SocialConnection();
        partialUpdatedSocialConnection.setId(socialConnection.getId());

        partialUpdatedSocialConnection.friendUsername(UPDATED_FRIEND_USERNAME).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restSocialConnectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSocialConnection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSocialConnection))
            )
            .andExpect(status().isOk());

        // Validate the SocialConnection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSocialConnectionUpdatableFieldsEquals(
            partialUpdatedSocialConnection,
            getPersistedSocialConnection(partialUpdatedSocialConnection)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, socialConnection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(socialConnection))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(socialConnection))
            )
            .andExpect(status().isBadRequest());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSocialConnection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        socialConnection.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSocialConnectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(socialConnection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SocialConnection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSocialConnection() throws Exception {
        // Initialize the database
        insertedSocialConnection = socialConnectionRepository.saveAndFlush(socialConnection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the socialConnection
        restSocialConnectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, socialConnection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return socialConnectionRepository.count();
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

    protected SocialConnection getPersistedSocialConnection(SocialConnection socialConnection) {
        return socialConnectionRepository.findById(socialConnection.getId()).orElseThrow();
    }

    protected void assertPersistedSocialConnectionToMatchAllProperties(SocialConnection expectedSocialConnection) {
        assertSocialConnectionAllPropertiesEquals(expectedSocialConnection, getPersistedSocialConnection(expectedSocialConnection));
    }

    protected void assertPersistedSocialConnectionToMatchUpdatableProperties(SocialConnection expectedSocialConnection) {
        assertSocialConnectionAllUpdatablePropertiesEquals(
            expectedSocialConnection,
            getPersistedSocialConnection(expectedSocialConnection)
        );
    }
}
