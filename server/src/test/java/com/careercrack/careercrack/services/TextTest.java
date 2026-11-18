package com.careercrack.careercrack.services;

import com.careercrack.careercrack.utils.Text;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextTest {

    @Test
    void nullStaysNull() {
        assertThat(Text.trimToNull(null)).isNull();
    }

    @Test
    void blankBecomesNull() {
        assertThat(Text.trimToNull("")).isNull();
        assertThat(Text.trimToNull("   ")).isNull();
    }

    @Test
    void surroundingSpacesAreRemovedAndInnerSpacesKept() {
        assertThat(Text.trimToNull("  Acme  Corp ")).isEqualTo("Acme  Corp");
    }
}
