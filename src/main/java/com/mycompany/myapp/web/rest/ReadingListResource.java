package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.ReadingList;
import com.mycompany.myapp.repository.ReadingListRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ReadingList}.
 */
@RestController
@RequestMapping("/api/reading-lists")
@Transactional
public class ReadingListResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReadingListResource.class);

    private static final String ENTITY_NAME = "readingList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReadingListRepository readingListRepository;

    public ReadingListResource(ReadingListRepository readingListRepository) {
        this.readingListRepository = readingListRepository;
    }

    /**
     * {@code POST  /reading-lists} : Create a new readingList.
     *
     * @param readingList the readingList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new readingList, or with status {@code 400 (Bad Request)} if the readingList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReadingList> createReadingList(@Valid @RequestBody ReadingList readingList) throws URISyntaxException {
        LOG.debug("REST request to save ReadingList : {}", readingList);
        if (readingList.getId() != null) {
            throw new BadRequestAlertException("A new readingList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        readingList = readingListRepository.save(readingList);
        return ResponseEntity.created(new URI("/api/reading-lists/" + readingList.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, readingList.getId().toString()))
            .body(readingList);
    }

    /**
     * {@code PUT  /reading-lists/:id} : Updates an existing readingList.
     *
     * @param id the id of the readingList to save.
     * @param readingList the readingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated readingList,
     * or with status {@code 400 (Bad Request)} if the readingList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the readingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReadingList> updateReadingList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReadingList readingList
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReadingList : {}, {}", id, readingList);
        if (readingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, readingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!readingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        readingList = readingListRepository.save(readingList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, readingList.getId().toString()))
            .body(readingList);
    }

    /**
     * {@code PATCH  /reading-lists/:id} : Partial updates given fields of an existing readingList, field will ignore if it is null
     *
     * @param id the id of the readingList to save.
     * @param readingList the readingList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated readingList,
     * or with status {@code 400 (Bad Request)} if the readingList is not valid,
     * or with status {@code 404 (Not Found)} if the readingList is not found,
     * or with status {@code 500 (Internal Server Error)} if the readingList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReadingList> partialUpdateReadingList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReadingList readingList
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReadingList partially : {}, {}", id, readingList);
        if (readingList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, readingList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!readingListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReadingList> result = readingListRepository
            .findById(readingList.getId())
            .map(existingReadingList -> {
                if (readingList.getTitle() != null) {
                    existingReadingList.setTitle(readingList.getTitle());
                }
                if (readingList.getStatus() != null) {
                    existingReadingList.setStatus(readingList.getStatus());
                }
                if (readingList.getStartDate() != null) {
                    existingReadingList.setStartDate(readingList.getStartDate());
                }
                if (readingList.getEndDate() != null) {
                    existingReadingList.setEndDate(readingList.getEndDate());
                }
                if (readingList.getCreatedAt() != null) {
                    existingReadingList.setCreatedAt(readingList.getCreatedAt());
                }

                return existingReadingList;
            })
            .map(readingListRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, readingList.getId().toString())
        );
    }

    /**
     * {@code GET  /reading-lists} : get all the readingLists.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of readingLists in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReadingList>> getAllReadingLists(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of ReadingLists");
        Page<ReadingList> page = readingListRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reading-lists/:id} : get the "id" readingList.
     *
     * @param id the id of the readingList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the readingList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadingList> getReadingList(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReadingList : {}", id);
        Optional<ReadingList> readingList = readingListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(readingList);
    }

    /**
     * {@code DELETE  /reading-lists/:id} : delete the "id" readingList.
     *
     * @param id the id of the readingList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReadingList(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReadingList : {}", id);
        readingListRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
