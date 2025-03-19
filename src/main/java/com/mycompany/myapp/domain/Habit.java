package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.HabitType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Habit.
 */
@Entity
@Table(name = "habit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Habit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "habit_type", nullable = false)
    private HabitType habitType;

    @NotNull
    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Habit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Habit name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HabitType getHabitType() {
        return this.habitType;
    }

    public Habit habitType(HabitType habitType) {
        this.setHabitType(habitType);
        return this;
    }

    public void setHabitType(HabitType habitType) {
        this.habitType = habitType;
    }

    public String getFrequency() {
        return this.frequency;
    }

    public Habit frequency(String frequency) {
        this.setFrequency(frequency);
        return this;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public Habit startDate(LocalDate startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Habit active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Habit user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Habit)) {
            return false;
        }
        return getId() != null && getId().equals(((Habit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Habit{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", habitType='" + getHabitType() + "'" +
            ", frequency='" + getFrequency() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
