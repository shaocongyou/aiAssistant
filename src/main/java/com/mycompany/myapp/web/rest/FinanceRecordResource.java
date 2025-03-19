package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.FinanceRecord;
import com.mycompany.myapp.repository.FinanceRecordRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.FinanceRecord}.
 */
@RestController
@RequestMapping("/api/finance-records")
@Transactional
public class FinanceRecordResource {

    private static final Logger LOG = LoggerFactory.getLogger(FinanceRecordResource.class);

    private static final String ENTITY_NAME = "financeRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinanceRecordRepository financeRecordRepository;

    public FinanceRecordResource(FinanceRecordRepository financeRecordRepository) {
        this.financeRecordRepository = financeRecordRepository;
    }

    /**
     * {@code POST  /finance-records} : Create a new financeRecord.
     *
     * @param financeRecord the financeRecord to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new financeRecord, or with status {@code 400 (Bad Request)} if the financeRecord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FinanceRecord> createFinanceRecord(@Valid @RequestBody FinanceRecord financeRecord) throws URISyntaxException {
        LOG.debug("REST request to save FinanceRecord : {}", financeRecord);
        if (financeRecord.getId() != null) {
            throw new BadRequestAlertException("A new financeRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        financeRecord = financeRecordRepository.save(financeRecord);
        return ResponseEntity.created(new URI("/api/finance-records/" + financeRecord.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, financeRecord.getId().toString()))
            .body(financeRecord);
    }

    /**
     * {@code PUT  /finance-records/:id} : Updates an existing financeRecord.
     *
     * @param id the id of the financeRecord to save.
     * @param financeRecord the financeRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeRecord,
     * or with status {@code 400 (Bad Request)} if the financeRecord is not valid,
     * or with status {@code 500 (Internal Server Error)} if the financeRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FinanceRecord> updateFinanceRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FinanceRecord financeRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to update FinanceRecord : {}, {}", id, financeRecord);
        if (financeRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        financeRecord = financeRecordRepository.save(financeRecord);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, financeRecord.getId().toString()))
            .body(financeRecord);
    }

    /**
     * {@code PATCH  /finance-records/:id} : Partial updates given fields of an existing financeRecord, field will ignore if it is null
     *
     * @param id the id of the financeRecord to save.
     * @param financeRecord the financeRecord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeRecord,
     * or with status {@code 400 (Bad Request)} if the financeRecord is not valid,
     * or with status {@code 404 (Not Found)} if the financeRecord is not found,
     * or with status {@code 500 (Internal Server Error)} if the financeRecord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinanceRecord> partialUpdateFinanceRecord(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FinanceRecord financeRecord
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FinanceRecord partially : {}, {}", id, financeRecord);
        if (financeRecord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeRecord.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeRecordRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinanceRecord> result = financeRecordRepository
            .findById(financeRecord.getId())
            .map(existingFinanceRecord -> {
                if (financeRecord.getDescription() != null) {
                    existingFinanceRecord.setDescription(financeRecord.getDescription());
                }
                if (financeRecord.getAmount() != null) {
                    existingFinanceRecord.setAmount(financeRecord.getAmount());
                }
                if (financeRecord.getCategory() != null) {
                    existingFinanceRecord.setCategory(financeRecord.getCategory());
                }
                if (financeRecord.getDate() != null) {
                    existingFinanceRecord.setDate(financeRecord.getDate());
                }
                if (financeRecord.getCreatedAt() != null) {
                    existingFinanceRecord.setCreatedAt(financeRecord.getCreatedAt());
                }

                return existingFinanceRecord;
            })
            .map(financeRecordRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, financeRecord.getId().toString())
        );
    }

    /**
     * {@code GET  /finance-records} : get all the financeRecords.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of financeRecords in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FinanceRecord>> getAllFinanceRecords(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of FinanceRecords");
        Page<FinanceRecord> page = financeRecordRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /finance-records/:id} : get the "id" financeRecord.
     *
     * @param id the id of the financeRecord to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the financeRecord, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FinanceRecord> getFinanceRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FinanceRecord : {}", id);
        Optional<FinanceRecord> financeRecord = financeRecordRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(financeRecord);
    }

    /**
     * {@code DELETE  /finance-records/:id} : delete the "id" financeRecord.
     *
     * @param id the id of the financeRecord to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinanceRecord(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FinanceRecord : {}", id);
        financeRecordRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
