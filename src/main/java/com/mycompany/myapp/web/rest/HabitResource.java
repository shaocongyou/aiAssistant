package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Habit;
import com.mycompany.myapp.repository.HabitRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Habit}.
 */
@RestController
@RequestMapping("/api/habits")
@Transactional
public class HabitResource {

    private static final Logger LOG = LoggerFactory.getLogger(HabitResource.class);

    private static final String ENTITY_NAME = "habit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HabitRepository habitRepository;

    public HabitResource(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * {@code POST  /habits} : Create a new habit.
     *
     * @param habit the habit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new habit, or with status {@code 400 (Bad Request)} if the habit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Habit> createHabit(@Valid @RequestBody Habit habit) throws URISyntaxException {
        LOG.debug("REST request to save Habit : {}", habit);
        if (habit.getId() != null) {
            throw new BadRequestAlertException("A new habit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        habit = habitRepository.save(habit);
        return ResponseEntity.created(new URI("/api/habits/" + habit.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, habit.getId().toString()))
            .body(habit);
    }

    /**
     * {@code PUT  /habits/:id} : Updates an existing habit.
     *
     * @param id the id of the habit to save.
     * @param habit the habit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habit,
     * or with status {@code 400 (Bad Request)} if the habit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the habit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Habit> updateHabit(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Habit habit)
        throws URISyntaxException {
        LOG.debug("REST request to update Habit : {}, {}", id, habit);
        if (habit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!habitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        habit = habitRepository.save(habit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, habit.getId().toString()))
            .body(habit);
    }

    /**
     * {@code PATCH  /habits/:id} : Partial updates given fields of an existing habit, field will ignore if it is null
     *
     * @param id the id of the habit to save.
     * @param habit the habit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated habit,
     * or with status {@code 400 (Bad Request)} if the habit is not valid,
     * or with status {@code 404 (Not Found)} if the habit is not found,
     * or with status {@code 500 (Internal Server Error)} if the habit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Habit> partialUpdateHabit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Habit habit
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Habit partially : {}, {}", id, habit);
        if (habit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, habit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!habitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Habit> result = habitRepository
            .findById(habit.getId())
            .map(existingHabit -> {
                if (habit.getName() != null) {
                    existingHabit.setName(habit.getName());
                }
                if (habit.getHabitType() != null) {
                    existingHabit.setHabitType(habit.getHabitType());
                }
                if (habit.getFrequency() != null) {
                    existingHabit.setFrequency(habit.getFrequency());
                }
                if (habit.getStartDate() != null) {
                    existingHabit.setStartDate(habit.getStartDate());
                }
                if (habit.getActive() != null) {
                    existingHabit.setActive(habit.getActive());
                }

                return existingHabit;
            })
            .map(habitRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, habit.getId().toString())
        );
    }

    /**
     * {@code GET  /habits} : get all the habits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of habits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Habit>> getAllHabits(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Habits");
        Page<Habit> page = habitRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /habits/:id} : get the "id" habit.
     *
     * @param id the id of the habit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the habit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Habit : {}", id);
        Optional<Habit> habit = habitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(habit);
    }

    /**
     * {@code DELETE  /habits/:id} : delete the "id" habit.
     *
     * @param id the id of the habit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Habit : {}", id);
        habitRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
