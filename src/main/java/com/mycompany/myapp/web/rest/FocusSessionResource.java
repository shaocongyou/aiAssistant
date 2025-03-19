package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.FocusSession;
import com.mycompany.myapp.repository.FocusSessionRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.FocusSession}.
 */
@RestController
@RequestMapping("/api/focus-sessions")
@Transactional
public class FocusSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(FocusSessionResource.class);

    private static final String ENTITY_NAME = "focusSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FocusSessionRepository focusSessionRepository;

    public FocusSessionResource(FocusSessionRepository focusSessionRepository) {
        this.focusSessionRepository = focusSessionRepository;
    }

    /**
     * {@code POST  /focus-sessions} : Create a new focusSession.
     *
     * @param focusSession the focusSession to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new focusSession, or with status {@code 400 (Bad Request)} if the focusSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FocusSession> createFocusSession(@Valid @RequestBody FocusSession focusSession) throws URISyntaxException {
        LOG.debug("REST request to save FocusSession : {}", focusSession);
        if (focusSession.getId() != null) {
            throw new BadRequestAlertException("A new focusSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        focusSession = focusSessionRepository.save(focusSession);
        return ResponseEntity.created(new URI("/api/focus-sessions/" + focusSession.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, focusSession.getId().toString()))
            .body(focusSession);
    }

    /**
     * {@code PUT  /focus-sessions/:id} : Updates an existing focusSession.
     *
     * @param id the id of the focusSession to save.
     * @param focusSession the focusSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated focusSession,
     * or with status {@code 400 (Bad Request)} if the focusSession is not valid,
     * or with status {@code 500 (Internal Server Error)} if the focusSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FocusSession> updateFocusSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FocusSession focusSession
    ) throws URISyntaxException {
        LOG.debug("REST request to update FocusSession : {}, {}", id, focusSession);
        if (focusSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, focusSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!focusSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        focusSession = focusSessionRepository.save(focusSession);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, focusSession.getId().toString()))
            .body(focusSession);
    }

    /**
     * {@code PATCH  /focus-sessions/:id} : Partial updates given fields of an existing focusSession, field will ignore if it is null
     *
     * @param id the id of the focusSession to save.
     * @param focusSession the focusSession to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated focusSession,
     * or with status {@code 400 (Bad Request)} if the focusSession is not valid,
     * or with status {@code 404 (Not Found)} if the focusSession is not found,
     * or with status {@code 500 (Internal Server Error)} if the focusSession couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FocusSession> partialUpdateFocusSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FocusSession focusSession
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FocusSession partially : {}, {}", id, focusSession);
        if (focusSession.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, focusSession.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!focusSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FocusSession> result = focusSessionRepository
            .findById(focusSession.getId())
            .map(existingFocusSession -> {
                if (focusSession.getDuration() != null) {
                    existingFocusSession.setDuration(focusSession.getDuration());
                }
                if (focusSession.getTask() != null) {
                    existingFocusSession.setTask(focusSession.getTask());
                }
                if (focusSession.getDate() != null) {
                    existingFocusSession.setDate(focusSession.getDate());
                }
                if (focusSession.getCreatedAt() != null) {
                    existingFocusSession.setCreatedAt(focusSession.getCreatedAt());
                }

                return existingFocusSession;
            })
            .map(focusSessionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, focusSession.getId().toString())
        );
    }

    /**
     * {@code GET  /focus-sessions} : get all the focusSessions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of focusSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FocusSession>> getAllFocusSessions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of FocusSessions");
        Page<FocusSession> page = focusSessionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /focus-sessions/:id} : get the "id" focusSession.
     *
     * @param id the id of the focusSession to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the focusSession, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FocusSession> getFocusSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FocusSession : {}", id);
        Optional<FocusSession> focusSession = focusSessionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(focusSession);
    }

    /**
     * {@code DELETE  /focus-sessions/:id} : delete the "id" focusSession.
     *
     * @param id the id of the focusSession to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFocusSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FocusSession : {}", id);
        focusSessionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
