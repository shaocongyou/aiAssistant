package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Reminder;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reminder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @Query("select reminder from Reminder reminder where reminder.user.login = ?#{authentication.name}")
    List<Reminder> findByUserIsCurrentUser();
}
