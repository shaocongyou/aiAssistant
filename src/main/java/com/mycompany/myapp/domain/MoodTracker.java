package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * A MoodTracker.
 */
@Entity
@Table(name = "mood_tracker")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MoodTracker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "mood", length = 50, nullable = false)
    private String mood;

    @NotNull
    @Column(name = "intensity", nullable = false)
    private Integer intensity;

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

    public MoodTracker id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMood() {
        return this.mood;
    }

    public MoodTracker mood(String mood) {
        this.setMood(mood);
        return this;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public Integer getIntensity() {
        return this.intensity;
    }

    public MoodTracker intensity(Integer intensity) {
        this.setIntensity(intensity);
        return this;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public MoodTracker date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public MoodTracker createdAt(Instant createdAt) {
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

    public MoodTracker user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MoodTracker)) {
            return false;
        }
        return getId() != null && getId().equals(((MoodTracker) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MoodTracker{" +
            "id=" + getId() +
            ", mood='" + getMood() + "'" +
            ", intensity=" + getIntensity() +
            ", date='" + getDate() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
