package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FinanceRecordTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceRecordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceRecord.class);
        FinanceRecord financeRecord1 = getFinanceRecordSample1();
        FinanceRecord financeRecord2 = new FinanceRecord();
        assertThat(financeRecord1).isNotEqualTo(financeRecord2);

        financeRecord2.setId(financeRecord1.getId());
        assertThat(financeRecord1).isEqualTo(financeRecord2);

        financeRecord2 = getFinanceRecordSample2();
        assertThat(financeRecord1).isNotEqualTo(financeRecord2);
    }
}
