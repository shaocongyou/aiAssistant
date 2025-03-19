package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Habit;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Habit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    @Query("select habit from Habit habit where habit.user.login = ?#{authentication.name}")
    List<Habit> findByUserIsCurrentUser();
}
