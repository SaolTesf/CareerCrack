package com.careercrack.careercrack.services;

import com.careercrack.careercrack.exceptions.BadRequestException;
import com.careercrack.careercrack.utils.PageRequests;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageRequestsTest {

    private static final Set<String> FIELDS = Set.of("name", "createdAt", "id");

    @Test
    void buildsThePageWithTheRequestedSortAndAnIdTieBreaker() {
        Pageable pageable = PageRequests.of(2, 25, "name", false, FIELDS);

        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(25);
        assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void sortingByIdDoesNotAddASecondIdSort() {
        Pageable pageable = PageRequests.of(0, 10, "id", true, FIELDS);

        assertThat(pageable.getSort().stream().count()).isEqualTo(1);
    }

    @Test
    void rejectsANegativePage() {
        assertThatThrownBy(() -> PageRequests.of(-1, 10, "name", true, FIELDS))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsASizeOutsideOneToOneHundred() {
        assertThatThrownBy(() -> PageRequests.of(0, 0, "name", true, FIELDS))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> PageRequests.of(0, 101, "name", true, FIELDS))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectsAFieldThatIsNotOnTheAllowedList() {
        assertThatThrownBy(() -> PageRequests.of(0, 10, "hashedPassword", true, FIELDS))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("hashedPassword")
                .hasMessageContaining("createdAt, id, name");
    }

    @Test
    void rejectsAMissingSortField() {
        assertThatThrownBy(() -> PageRequests.of(0, 10, null, true, FIELDS))
                .isInstanceOf(BadRequestException.class);
    }
}
