package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FinanceRecord;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FinanceRecord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FinanceRecordRepository extends JpaRepository<FinanceRecord, Long> {
    @Query("select financeRecord from FinanceRecord financeRecord where financeRecord.user.login = ?#{authentication.name}")
    List<FinanceRecord> findByUserIsCurrentUser();
}
