package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Everything the dashboard page needs in one request. */
@Getter
@AllArgsConstructor
public class DashboardResponse {
    private final FunnelResponse funnel;
    // every stage in pipeline order, zeros included
    private final List<StatusCountResponse> byStatus;
    private final List<WeeklyCountResponse> appliedPerWeek;
    private final List<EventResponse> upcomingEvents;
    private final List<EventResponse> overdueEvents;
    private final List<ApplicationSummaryResponse> recentApplications;
}
