package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FocusSession;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FocusSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {
    @Query("select focusSession from FocusSession focusSession where focusSession.user.login = ?#{authentication.name}")
    List<FocusSession> findByUserIsCurrentUser();
}
