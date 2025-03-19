package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A FocusSession.
 */
@Entity
@Table(name = "focus_session")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FocusSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Size(max = 200)
    @Column(name = "task", length = 200)
    private String task;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FocusSession id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public FocusSession duration(Integer duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTask() {
        return this.task;
    }

    public FocusSession task(String task) {
        this.setTask(task);
        return this;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public FocusSession date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public FocusSession createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FocusSession user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FocusSession)) {
            return false;
        }
        return getId() != null && getId().equals(((FocusSession) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FocusSession{" +
            "id=" + getId() +
            ", duration=" + getDuration() +
            ", task='" + getTask() + "'" +
            ", date='" + getDate() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
