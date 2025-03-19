package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.MoodTracker;
import com.mycompany.myapp.repository.MoodTrackerRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.MoodTracker}.
 */
@RestController
@RequestMapping("/api/mood-trackers")
@Transactional
public class MoodTrackerResource {

    private static final Logger LOG = LoggerFactory.getLogger(MoodTrackerResource.class);

    private static final String ENTITY_NAME = "moodTracker";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MoodTrackerRepository moodTrackerRepository;

    public MoodTrackerResource(MoodTrackerRepository moodTrackerRepository) {
        this.moodTrackerRepository = moodTrackerRepository;
    }

    /**
     * {@code POST  /mood-trackers} : Create a new moodTracker.
     *
     * @param moodTracker the moodTracker to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new moodTracker, or with status {@code 400 (Bad Request)} if the moodTracker has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MoodTracker> createMoodTracker(@Valid @RequestBody MoodTracker moodTracker) throws URISyntaxException {
        LOG.debug("REST request to save MoodTracker : {}", moodTracker);
        if (moodTracker.getId() != null) {
            throw new BadRequestAlertException("A new moodTracker cannot already have an ID", ENTITY_NAME, "idexists");
        }
        moodTracker = moodTrackerRepository.save(moodTracker);
        return ResponseEntity.created(new URI("/api/mood-trackers/" + moodTracker.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, moodTracker.getId().toString()))
            .body(moodTracker);
    }

    /**
     * {@code PUT  /mood-trackers/:id} : Updates an existing moodTracker.
     *
     * @param id the id of the moodTracker to save.
     * @param moodTracker the moodTracker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moodTracker,
     * or with status {@code 400 (Bad Request)} if the moodTracker is not valid,
     * or with status {@code 500 (Internal Server Error)} if the moodTracker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MoodTracker> updateMoodTracker(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MoodTracker moodTracker
    ) throws URISyntaxException {
        LOG.debug("REST request to update MoodTracker : {}, {}", id, moodTracker);
        if (moodTracker.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moodTracker.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moodTrackerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        moodTracker = moodTrackerRepository.save(moodTracker);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, moodTracker.getId().toString()))
            .body(moodTracker);
    }

    /**
     * {@code PATCH  /mood-trackers/:id} : Partial updates given fields of an existing moodTracker, field will ignore if it is null
     *
     * @param id the id of the moodTracker to save.
     * @param moodTracker the moodTracker to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated moodTracker,
     * or with status {@code 400 (Bad Request)} if the moodTracker is not valid,
     * or with status {@code 404 (Not Found)} if the moodTracker is not found,
     * or with status {@code 500 (Internal Server Error)} if the moodTracker couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MoodTracker> partialUpdateMoodTracker(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MoodTracker moodTracker
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MoodTracker partially : {}, {}", id, moodTracker);
        if (moodTracker.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, moodTracker.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!moodTrackerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MoodTracker> result = moodTrackerRepository
            .findById(moodTracker.getId())
            .map(existingMoodTracker -> {
                if (moodTracker.getMood() != null) {
                    existingMoodTracker.setMood(moodTracker.getMood());
                }
                if (moodTracker.getIntensity() != null) {
                    existingMoodTracker.setIntensity(moodTracker.getIntensity());
                }
                if (moodTracker.getDate() != null) {
                    existingMoodTracker.setDate(moodTracker.getDate());
                }
                if (moodTracker.getCreatedAt() != null) {
                    existingMoodTracker.setCreatedAt(moodTracker.getCreatedAt());
                }

                return existingMoodTracker;
            })
            .map(moodTrackerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, moodTracker.getId().toString())
        );
    }

    /**
     * {@code GET  /mood-trackers} : get all the moodTrackers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of moodTrackers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MoodTracker>> getAllMoodTrackers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of MoodTrackers");
        Page<MoodTracker> page = moodTrackerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mood-trackers/:id} : get the "id" moodTracker.
     *
     * @param id the id of the moodTracker to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the moodTracker, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MoodTracker> getMoodTracker(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MoodTracker : {}", id);
        Optional<MoodTracker> moodTracker = moodTrackerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(moodTracker);
    }

    /**
     * {@code DELETE  /mood-trackers/:id} : delete the "id" moodTracker.
     *
     * @param id the id of the moodTracker to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoodTracker(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MoodTracker : {}", id);
        moodTrackerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
