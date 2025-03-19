package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FocusSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FocusSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FocusSession.class);
        FocusSession focusSession1 = getFocusSessionSample1();
        FocusSession focusSession2 = new FocusSession();
        assertThat(focusSession1).isNotEqualTo(focusSession2);

        focusSession2.setId(focusSession1.getId());
        assertThat(focusSession1).isEqualTo(focusSession2);

        focusSession2 = getFocusSessionSample2();
        assertThat(focusSession1).isNotEqualTo(focusSession2);
    }
}
