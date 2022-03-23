package ru.practice.binapi.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ru.practice.binapi.web.rest.TestUtil;

public class TODOTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TODO.class);
        TODO tODO1 = new TODO();
        tODO1.setId(1L);
        TODO tODO2 = new TODO();
        tODO2.setId(tODO1.getId());
        assertThat(tODO1).isEqualTo(tODO2);
        tODO2.setId(2L);
        assertThat(tODO1).isNotEqualTo(tODO2);
        tODO1.setId(null);
        assertThat(tODO1).isNotEqualTo(tODO2);
    }
}
