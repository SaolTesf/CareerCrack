package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Body for dragging a card on the Kanban board: the column it lands in and its position there. */
@Getter
@Setter
@NoArgsConstructor
public class UpdateApplicationStatusRequest {

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    // position inside the column, 0 = top. null puts it at the bottom.
    @Min(value = 0, message = "Position cannot be negative")
    private Integer sortOrder;
}
