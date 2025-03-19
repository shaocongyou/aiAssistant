package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Task;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Task entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("select task from Task task where task.user.login = ?#{authentication.name}")
    List<Task> findByUserIsCurrentUser();
}
