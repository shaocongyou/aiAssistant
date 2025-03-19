package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MoodTracker;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MoodTracker entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MoodTrackerRepository extends JpaRepository<MoodTracker, Long> {
    @Query("select moodTracker from MoodTracker moodTracker where moodTracker.user.login = ?#{authentication.name}")
    List<MoodTracker> findByUserIsCurrentUser();
}
