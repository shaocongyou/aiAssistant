package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Goal;
import com.mycompany.myapp.repository.GoalRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Goal}.
 */
@RestController
@RequestMapping("/api/goals")
@Transactional
public class GoalResource {

    private static final Logger LOG = LoggerFactory.getLogger(GoalResource.class);

    private static final String ENTITY_NAME = "goal";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GoalRepository goalRepository;

    public GoalResource(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * {@code POST  /goals} : Create a new goal.
     *
     * @param goal the goal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new goal, or with status {@code 400 (Bad Request)} if the goal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) throws URISyntaxException {
        LOG.debug("REST request to save Goal : {}", goal);
        if (goal.getId() != null) {
            throw new BadRequestAlertException("A new goal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        goal = goalRepository.save(goal);
        return ResponseEntity.created(new URI("/api/goals/" + goal.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, goal.getId().toString()))
            .body(goal);
    }

    /**
     * {@code PUT  /goals/:id} : Updates an existing goal.
     *
     * @param id the id of the goal to save.
     * @param goal the goal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated goal,
     * or with status {@code 400 (Bad Request)} if the goal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the goal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Goal goal)
        throws URISyntaxException {
        LOG.debug("REST request to update Goal : {}, {}", id, goal);
        if (goal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, goal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!goalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        goal = goalRepository.save(goal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, goal.getId().toString()))
            .body(goal);
    }

    /**
     * {@code PATCH  /goals/:id} : Partial updates given fields of an existing goal, field will ignore if it is null
     *
     * @param id the id of the goal to save.
     * @param goal the goal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated goal,
     * or with status {@code 400 (Bad Request)} if the goal is not valid,
     * or with status {@code 404 (Not Found)} if the goal is not found,
     * or with status {@code 500 (Internal Server Error)} if the goal couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Goal> partialUpdateGoal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Goal goal
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Goal partially : {}, {}", id, goal);
        if (goal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, goal.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!goalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Goal> result = goalRepository
            .findById(goal.getId())
            .map(existingGoal -> {
                if (goal.getTitle() != null) {
                    existingGoal.setTitle(goal.getTitle());
                }
                if (goal.getDescription() != null) {
                    existingGoal.setDescription(goal.getDescription());
                }
                if (goal.getGoalType() != null) {
                    existingGoal.setGoalType(goal.getGoalType());
                }
                if (goal.getDeadline() != null) {
                    existingGoal.setDeadline(goal.getDeadline());
                }
                if (goal.getCompleted() != null) {
                    existingGoal.setCompleted(goal.getCompleted());
                }
                if (goal.getCreatedAt() != null) {
                    existingGoal.setCreatedAt(goal.getCreatedAt());
                }
                if (goal.getUpdatedAt() != null) {
                    existingGoal.setUpdatedAt(goal.getUpdatedAt());
                }

                return existingGoal;
            })
            .map(goalRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, goal.getId().toString())
        );
    }

    /**
     * {@code GET  /goals} : get all the goals.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of goals in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Goal>> getAllGoals(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Goals");
        Page<Goal> page = goalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /goals/:id} : get the "id" goal.
     *
     * @param id the id of the goal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the goal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoal(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Goal : {}", id);
        Optional<Goal> goal = goalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(goal);
    }

    /**
     * {@code DELETE  /goals/:id} : delete the "id" goal.
     *
     * @param id the id of the goal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Goal : {}", id);
        goalRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
