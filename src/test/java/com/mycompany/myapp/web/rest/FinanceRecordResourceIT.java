package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FinanceRecordAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.FinanceRecord;
import com.mycompany.myapp.repository.FinanceRecordRepository;
import com.mycompany.myapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link FinanceRecordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FinanceRecordResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/finance-records";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FinanceRecordRepository financeRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFinanceRecordMockMvc;

    private FinanceRecord financeRecord;

    private FinanceRecord insertedFinanceRecord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceRecord createEntity() {
        return new FinanceRecord()
            .description(DEFAULT_DESCRIPTION)
            .amount(DEFAULT_AMOUNT)
            .category(DEFAULT_CATEGORY)
            .date(DEFAULT_DATE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceRecord createUpdatedEntity() {
        return new FinanceRecord()
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .category(UPDATED_CATEGORY)
            .date(UPDATED_DATE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        financeRecord = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFinanceRecord != null) {
            financeRecordRepository.delete(insertedFinanceRecord);
            insertedFinanceRecord = null;
        }
    }

    @Test
    @Transactional
    void createFinanceRecord() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FinanceRecord
        var returnedFinanceRecord = om.readValue(
            restFinanceRecordMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FinanceRecord.class
        );

        // Validate the FinanceRecord in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFinanceRecordUpdatableFieldsEquals(returnedFinanceRecord, getPersistedFinanceRecord(returnedFinanceRecord));

        insertedFinanceRecord = returnedFinanceRecord;
    }

    @Test
    @Transactional
    void createFinanceRecordWithExistingId() throws Exception {
        // Create the FinanceRecord with an existing ID
        financeRecord.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        financeRecord.setDescription(null);

        // Create the FinanceRecord, which fails.

        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        financeRecord.setAmount(null);

        // Create the FinanceRecord, which fails.

        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        financeRecord.setCategory(null);

        // Create the FinanceRecord, which fails.

        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        financeRecord.setDate(null);

        // Create the FinanceRecord, which fails.

        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        financeRecord.setCreatedAt(null);

        // Create the FinanceRecord, which fails.

        restFinanceRecordMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFinanceRecords() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        // Get all the financeRecordList
        restFinanceRecordMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeRecord.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getFinanceRecord() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        // Get the financeRecord
        restFinanceRecordMockMvc
            .perform(get(ENTITY_API_URL_ID, financeRecord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financeRecord.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFinanceRecord() throws Exception {
        // Get the financeRecord
        restFinanceRecordMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFinanceRecord() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the financeRecord
        FinanceRecord updatedFinanceRecord = financeRecordRepository.findById(financeRecord.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFinanceRecord are not directly saved in db
        em.detach(updatedFinanceRecord);
        updatedFinanceRecord
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .category(UPDATED_CATEGORY)
            .date(UPDATED_DATE)
            .createdAt(UPDATED_CREATED_AT);

        restFinanceRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFinanceRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFinanceRecord))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFinanceRecordToMatchAllProperties(updatedFinanceRecord);
    }

    @Test
    @Transactional
    void putNonExistingFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(financeRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(financeRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFinanceRecordWithPatch() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the financeRecord using partial update
        FinanceRecord partialUpdatedFinanceRecord = new FinanceRecord();
        partialUpdatedFinanceRecord.setId(financeRecord.getId());

        partialUpdatedFinanceRecord
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .date(UPDATED_DATE)
            .createdAt(UPDATED_CREATED_AT);

        restFinanceRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFinanceRecord))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFinanceRecordUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFinanceRecord, financeRecord),
            getPersistedFinanceRecord(financeRecord)
        );
    }

    @Test
    @Transactional
    void fullUpdateFinanceRecordWithPatch() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the financeRecord using partial update
        FinanceRecord partialUpdatedFinanceRecord = new FinanceRecord();
        partialUpdatedFinanceRecord.setId(financeRecord.getId());

        partialUpdatedFinanceRecord
            .description(UPDATED_DESCRIPTION)
            .amount(UPDATED_AMOUNT)
            .category(UPDATED_CATEGORY)
            .date(UPDATED_DATE)
            .createdAt(UPDATED_CREATED_AT);

        restFinanceRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFinanceRecord))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRecord in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFinanceRecordUpdatableFieldsEquals(partialUpdatedFinanceRecord, getPersistedFinanceRecord(partialUpdatedFinanceRecord));
    }

    @Test
    @Transactional
    void patchNonExistingFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financeRecord.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(financeRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(financeRecord))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFinanceRecord() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        financeRecord.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRecordMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(financeRecord)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceRecord in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFinanceRecord() throws Exception {
        // Initialize the database
        insertedFinanceRecord = financeRecordRepository.saveAndFlush(financeRecord);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the financeRecord
        restFinanceRecordMockMvc
            .perform(delete(ENTITY_API_URL_ID, financeRecord.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return financeRecordRepository.count();
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

    protected FinanceRecord getPersistedFinanceRecord(FinanceRecord financeRecord) {
        return financeRecordRepository.findById(financeRecord.getId()).orElseThrow();
    }

    protected void assertPersistedFinanceRecordToMatchAllProperties(FinanceRecord expectedFinanceRecord) {
        assertFinanceRecordAllPropertiesEquals(expectedFinanceRecord, getPersistedFinanceRecord(expectedFinanceRecord));
    }

    protected void assertPersistedFinanceRecordToMatchUpdatableProperties(FinanceRecord expectedFinanceRecord) {
        assertFinanceRecordAllUpdatablePropertiesEquals(expectedFinanceRecord, getPersistedFinanceRecord(expectedFinanceRecord));
    }
}
