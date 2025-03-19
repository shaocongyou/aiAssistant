package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.SocialConnection;
import com.mycompany.myapp.repository.SocialConnectionRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.SocialConnection}.
 */
@RestController
@RequestMapping("/api/social-connections")
@Transactional
public class SocialConnectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SocialConnectionResource.class);

    private static final String ENTITY_NAME = "socialConnection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SocialConnectionRepository socialConnectionRepository;

    public SocialConnectionResource(SocialConnectionRepository socialConnectionRepository) {
        this.socialConnectionRepository = socialConnectionRepository;
    }

    /**
     * {@code POST  /social-connections} : Create a new socialConnection.
     *
     * @param socialConnection the socialConnection to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new socialConnection, or with status {@code 400 (Bad Request)} if the socialConnection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SocialConnection> createSocialConnection(@Valid @RequestBody SocialConnection socialConnection)
        throws URISyntaxException {
        LOG.debug("REST request to save SocialConnection : {}", socialConnection);
        if (socialConnection.getId() != null) {
            throw new BadRequestAlertException("A new socialConnection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        socialConnection = socialConnectionRepository.save(socialConnection);
        return ResponseEntity.created(new URI("/api/social-connections/" + socialConnection.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, socialConnection.getId().toString()))
            .body(socialConnection);
    }

    /**
     * {@code PUT  /social-connections/:id} : Updates an existing socialConnection.
     *
     * @param id the id of the socialConnection to save.
     * @param socialConnection the socialConnection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialConnection,
     * or with status {@code 400 (Bad Request)} if the socialConnection is not valid,
     * or with status {@code 500 (Internal Server Error)} if the socialConnection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SocialConnection> updateSocialConnection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SocialConnection socialConnection
    ) throws URISyntaxException {
        LOG.debug("REST request to update SocialConnection : {}, {}", id, socialConnection);
        if (socialConnection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialConnection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialConnectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        socialConnection = socialConnectionRepository.save(socialConnection);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, socialConnection.getId().toString()))
            .body(socialConnection);
    }

    /**
     * {@code PATCH  /social-connections/:id} : Partial updates given fields of an existing socialConnection, field will ignore if it is null
     *
     * @param id the id of the socialConnection to save.
     * @param socialConnection the socialConnection to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated socialConnection,
     * or with status {@code 400 (Bad Request)} if the socialConnection is not valid,
     * or with status {@code 404 (Not Found)} if the socialConnection is not found,
     * or with status {@code 500 (Internal Server Error)} if the socialConnection couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SocialConnection> partialUpdateSocialConnection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SocialConnection socialConnection
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SocialConnection partially : {}, {}", id, socialConnection);
        if (socialConnection.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, socialConnection.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!socialConnectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SocialConnection> result = socialConnectionRepository
            .findById(socialConnection.getId())
            .map(existingSocialConnection -> {
                if (socialConnection.getFriendUsername() != null) {
                    existingSocialConnection.setFriendUsername(socialConnection.getFriendUsername());
                }
                if (socialConnection.getStatus() != null) {
                    existingSocialConnection.setStatus(socialConnection.getStatus());
                }
                if (socialConnection.getCreatedAt() != null) {
                    existingSocialConnection.setCreatedAt(socialConnection.getCreatedAt());
                }

                return existingSocialConnection;
            })
            .map(socialConnectionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, socialConnection.getId().toString())
        );
    }

    /**
     * {@code GET  /social-connections} : get all the socialConnections.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of socialConnections in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SocialConnection>> getAllSocialConnections(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SocialConnections");
        Page<SocialConnection> page;
        if (eagerload) {
            page = socialConnectionRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = socialConnectionRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /social-connections/:id} : get the "id" socialConnection.
     *
     * @param id the id of the socialConnection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the socialConnection, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SocialConnection> getSocialConnection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SocialConnection : {}", id);
        Optional<SocialConnection> socialConnection = socialConnectionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(socialConnection);
    }

    /**
     * {@code DELETE  /social-connections/:id} : delete the "id" socialConnection.
     *
     * @param id the id of the socialConnection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialConnection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SocialConnection : {}", id);
        socialConnectionRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
