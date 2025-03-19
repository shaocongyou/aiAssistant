package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Reminder;
import com.mycompany.myapp.repository.ReminderRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Reminder}.
 */
@RestController
@RequestMapping("/api/reminders")
@Transactional
public class ReminderResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReminderResource.class);

    private static final String ENTITY_NAME = "reminder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReminderRepository reminderRepository;

    public ReminderResource(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    /**
     * {@code POST  /reminders} : Create a new reminder.
     *
     * @param reminder the reminder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new reminder, or with status {@code 400 (Bad Request)} if the reminder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Reminder> createReminder(@Valid @RequestBody Reminder reminder) throws URISyntaxException {
        LOG.debug("REST request to save Reminder : {}", reminder);
        if (reminder.getId() != null) {
            throw new BadRequestAlertException("A new reminder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        reminder = reminderRepository.save(reminder);
        return ResponseEntity.created(new URI("/api/reminders/" + reminder.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, reminder.getId().toString()))
            .body(reminder);
    }

    /**
     * {@code PUT  /reminders/:id} : Updates an existing reminder.
     *
     * @param id the id of the reminder to save.
     * @param reminder the reminder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reminder,
     * or with status {@code 400 (Bad Request)} if the reminder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the reminder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Reminder reminder
    ) throws URISyntaxException {
        LOG.debug("REST request to update Reminder : {}, {}", id, reminder);
        if (reminder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reminder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reminderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        reminder = reminderRepository.save(reminder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reminder.getId().toString()))
            .body(reminder);
    }

    /**
     * {@code PATCH  /reminders/:id} : Partial updates given fields of an existing reminder, field will ignore if it is null
     *
     * @param id the id of the reminder to save.
     * @param reminder the reminder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated reminder,
     * or with status {@code 400 (Bad Request)} if the reminder is not valid,
     * or with status {@code 404 (Not Found)} if the reminder is not found,
     * or with status {@code 500 (Internal Server Error)} if the reminder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Reminder> partialUpdateReminder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Reminder reminder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Reminder partially : {}, {}", id, reminder);
        if (reminder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reminder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reminderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Reminder> result = reminderRepository
            .findById(reminder.getId())
            .map(existingReminder -> {
                if (reminder.getMessage() != null) {
                    existingReminder.setMessage(reminder.getMessage());
                }
                if (reminder.getReminderTime() != null) {
                    existingReminder.setReminderTime(reminder.getReminderTime());
                }
                if (reminder.getRepeatInterval() != null) {
                    existingReminder.setRepeatInterval(reminder.getRepeatInterval());
                }
                if (reminder.getCreatedAt() != null) {
                    existingReminder.setCreatedAt(reminder.getCreatedAt());
                }

                return existingReminder;
            })
            .map(reminderRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, reminder.getId().toString())
        );
    }

    /**
     * {@code GET  /reminders} : get all the reminders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of reminders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Reminder>> getAllReminders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Reminders");
        Page<Reminder> page = reminderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reminders/:id} : get the "id" reminder.
     *
     * @param id the id of the reminder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the reminder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Reminder : {}", id);
        Optional<Reminder> reminder = reminderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(reminder);
    }

    /**
     * {@code DELETE  /reminders/:id} : delete the "id" reminder.
     *
     * @param id the id of the reminder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Reminder : {}", id);
        reminderRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
