package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ReadingListAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ReadingList;
import com.mycompany.myapp.repository.ReadingListRepository;
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
 * Integration tests for the {@link ReadingListResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReadingListResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reading-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReadingListRepository readingListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReadingListMockMvc;

    private ReadingList readingList;

    private ReadingList insertedReadingList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReadingList createEntity() {
        return new ReadingList()
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReadingList createUpdatedEntity() {
        return new ReadingList()
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        readingList = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReadingList != null) {
            readingListRepository.delete(insertedReadingList);
            insertedReadingList = null;
        }
    }

    @Test
    @Transactional
    void createReadingList() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReadingList
        var returnedReadingList = om.readValue(
            restReadingListMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReadingList.class
        );

        // Validate the ReadingList in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReadingListUpdatableFieldsEquals(returnedReadingList, getPersistedReadingList(returnedReadingList));

        insertedReadingList = returnedReadingList;
    }

    @Test
    @Transactional
    void createReadingListWithExistingId() throws Exception {
        // Create the ReadingList with an existing ID
        readingList.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReadingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isBadRequest());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        readingList.setTitle(null);

        // Create the ReadingList, which fails.

        restReadingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        readingList.setStatus(null);

        // Create the ReadingList, which fails.

        restReadingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        readingList.setCreatedAt(null);

        // Create the ReadingList, which fails.

        restReadingListMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReadingLists() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        // Get all the readingListList
        restReadingListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(readingList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getReadingList() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        // Get the readingList
        restReadingListMockMvc
            .perform(get(ENTITY_API_URL_ID, readingList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(readingList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReadingList() throws Exception {
        // Get the readingList
        restReadingListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReadingList() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingList
        ReadingList updatedReadingList = readingListRepository.findById(readingList.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReadingList are not directly saved in db
        em.detach(updatedReadingList);
        updatedReadingList
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .createdAt(UPDATED_CREATED_AT);

        restReadingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReadingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReadingList))
            )
            .andExpect(status().isOk());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReadingListToMatchAllProperties(updatedReadingList);
    }

    @Test
    @Transactional
    void putNonExistingReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, readingList.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(readingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(readingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReadingListWithPatch() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingList using partial update
        ReadingList partialUpdatedReadingList = new ReadingList();
        partialUpdatedReadingList.setId(readingList.getId());

        partialUpdatedReadingList.title(UPDATED_TITLE).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restReadingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReadingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReadingList))
            )
            .andExpect(status().isOk());

        // Validate the ReadingList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReadingListUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReadingList, readingList),
            getPersistedReadingList(readingList)
        );
    }

    @Test
    @Transactional
    void fullUpdateReadingListWithPatch() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the readingList using partial update
        ReadingList partialUpdatedReadingList = new ReadingList();
        partialUpdatedReadingList.setId(readingList.getId());

        partialUpdatedReadingList
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .createdAt(UPDATED_CREATED_AT);

        restReadingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReadingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReadingList))
            )
            .andExpect(status().isOk());

        // Validate the ReadingList in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReadingListUpdatableFieldsEquals(partialUpdatedReadingList, getPersistedReadingList(partialUpdatedReadingList));
    }

    @Test
    @Transactional
    void patchNonExistingReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, readingList.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(readingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(readingList))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReadingList() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        readingList.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReadingListMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(readingList)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReadingList in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReadingList() throws Exception {
        // Initialize the database
        insertedReadingList = readingListRepository.saveAndFlush(readingList);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the readingList
        restReadingListMockMvc
            .perform(delete(ENTITY_API_URL_ID, readingList.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return readingListRepository.count();
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

    protected ReadingList getPersistedReadingList(ReadingList readingList) {
        return readingListRepository.findById(readingList.getId()).orElseThrow();
    }

    protected void assertPersistedReadingListToMatchAllProperties(ReadingList expectedReadingList) {
        assertReadingListAllPropertiesEquals(expectedReadingList, getPersistedReadingList(expectedReadingList));
    }

    protected void assertPersistedReadingListToMatchUpdatableProperties(ReadingList expectedReadingList) {
        assertReadingListAllUpdatablePropertiesEquals(expectedReadingList, getPersistedReadingList(expectedReadingList));
    }
}
