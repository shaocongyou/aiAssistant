package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.SocialConnectionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SocialConnectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SocialConnection.class);
        SocialConnection socialConnection1 = getSocialConnectionSample1();
        SocialConnection socialConnection2 = new SocialConnection();
        assertThat(socialConnection1).isNotEqualTo(socialConnection2);

        socialConnection2.setId(socialConnection1.getId());
        assertThat(socialConnection1).isEqualTo(socialConnection2);

        socialConnection2 = getSocialConnectionSample2();
        assertThat(socialConnection1).isNotEqualTo(socialConnection2);
    }
}
