package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ReadingList;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReadingList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReadingListRepository extends JpaRepository<ReadingList, Long> {
    @Query("select readingList from ReadingList readingList where readingList.user.login = ?#{authentication.name}")
    List<ReadingList> findByUserIsCurrentUser();
}
