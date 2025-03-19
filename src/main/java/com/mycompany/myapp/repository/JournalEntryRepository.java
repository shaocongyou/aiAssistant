package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JournalEntry;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the JournalEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    @Query("select journalEntry from JournalEntry journalEntry where journalEntry.user.login = ?#{authentication.name}")
    List<JournalEntry> findByUserIsCurrentUser();
}
