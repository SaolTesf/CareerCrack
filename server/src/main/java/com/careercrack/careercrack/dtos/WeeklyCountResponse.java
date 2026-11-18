package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/** Applications sent in one week, for a bar chart. weekStart is that week's Monday. */
@Getter
@AllArgsConstructor
public class WeeklyCountResponse {
    private final LocalDate weekStart;
    private final long count;
}
