package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.JournalEntry;
import com.mycompany.myapp.repository.JournalEntryRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.JournalEntry}.
 */
@RestController
@RequestMapping("/api/journal-entries")
@Transactional
public class JournalEntryResource {

    private static final Logger LOG = LoggerFactory.getLogger(JournalEntryResource.class);

    private static final String ENTITY_NAME = "journalEntry";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryResource(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    /**
     * {@code POST  /journal-entries} : Create a new journalEntry.
     *
     * @param journalEntry the journalEntry to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new journalEntry, or with status {@code 400 (Bad Request)} if the journalEntry has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<JournalEntry> createJournalEntry(@Valid @RequestBody JournalEntry journalEntry) throws URISyntaxException {
        LOG.debug("REST request to save JournalEntry : {}", journalEntry);
        if (journalEntry.getId() != null) {
            throw new BadRequestAlertException("A new journalEntry cannot already have an ID", ENTITY_NAME, "idexists");
        }
        journalEntry = journalEntryRepository.save(journalEntry);
        return ResponseEntity.created(new URI("/api/journal-entries/" + journalEntry.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, journalEntry.getId().toString()))
            .body(journalEntry);
    }

    /**
     * {@code PUT  /journal-entries/:id} : Updates an existing journalEntry.
     *
     * @param id the id of the journalEntry to save.
     * @param journalEntry the journalEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalEntry,
     * or with status {@code 400 (Bad Request)} if the journalEntry is not valid,
     * or with status {@code 500 (Internal Server Error)} if the journalEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> updateJournalEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody JournalEntry journalEntry
    ) throws URISyntaxException {
        LOG.debug("REST request to update JournalEntry : {}, {}", id, journalEntry);
        if (journalEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        journalEntry = journalEntryRepository.save(journalEntry);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalEntry.getId().toString()))
            .body(journalEntry);
    }

    /**
     * {@code PATCH  /journal-entries/:id} : Partial updates given fields of an existing journalEntry, field will ignore if it is null
     *
     * @param id the id of the journalEntry to save.
     * @param journalEntry the journalEntry to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated journalEntry,
     * or with status {@code 400 (Bad Request)} if the journalEntry is not valid,
     * or with status {@code 404 (Not Found)} if the journalEntry is not found,
     * or with status {@code 500 (Internal Server Error)} if the journalEntry couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<JournalEntry> partialUpdateJournalEntry(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody JournalEntry journalEntry
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update JournalEntry partially : {}, {}", id, journalEntry);
        if (journalEntry.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, journalEntry.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!journalEntryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<JournalEntry> result = journalEntryRepository
            .findById(journalEntry.getId())
            .map(existingJournalEntry -> {
                if (journalEntry.getTitle() != null) {
                    existingJournalEntry.setTitle(journalEntry.getTitle());
                }
                if (journalEntry.getContent() != null) {
                    existingJournalEntry.setContent(journalEntry.getContent());
                }
                if (journalEntry.getContentContentType() != null) {
                    existingJournalEntry.setContentContentType(journalEntry.getContentContentType());
                }
                if (journalEntry.getMood() != null) {
                    existingJournalEntry.setMood(journalEntry.getMood());
                }
                if (journalEntry.getCreatedAt() != null) {
                    existingJournalEntry.setCreatedAt(journalEntry.getCreatedAt());
                }

                return existingJournalEntry;
            })
            .map(journalEntryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, journalEntry.getId().toString())
        );
    }

    /**
     * {@code GET  /journal-entries} : get all the journalEntries.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of journalEntries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntries(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of JournalEntries");
        Page<JournalEntry> page = journalEntryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /journal-entries/:id} : get the "id" journalEntry.
     *
     * @param id the id of the journalEntry to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the journalEntry, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getJournalEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to get JournalEntry : {}", id);
        Optional<JournalEntry> journalEntry = journalEntryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(journalEntry);
    }

    /**
     * {@code DELETE  /journal-entries/:id} : delete the "id" journalEntry.
     *
     * @param id the id of the journalEntry to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJournalEntry(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete JournalEntry : {}", id);
        journalEntryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
