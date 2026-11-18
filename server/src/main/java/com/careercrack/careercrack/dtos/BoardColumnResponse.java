package com.careercrack.careercrack.dtos;

import com.careercrack.careercrack.enums.ApplicationStatus;
import lombok.Getter;

import java.util.List;

/** One Kanban column. The board endpoint returns every stage in pipeline order, empty ones included. */
@Getter
public class BoardColumnResponse {
    private final ApplicationStatus status;
    private final int count;
    private final List<ApplicationSummaryResponse> applications;

    public BoardColumnResponse(ApplicationStatus status, List<ApplicationSummaryResponse> applications) {
        this.status = status;
        this.applications = applications;
        this.count = applications.size();
    }
}
