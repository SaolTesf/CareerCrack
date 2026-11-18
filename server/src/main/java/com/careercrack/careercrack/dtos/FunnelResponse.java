package com.careercrack.careercrack.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * How many applications reached each stage. "applied" counts everything that was ever sent, including ones that later
 * got an interview or were rejected. The rates are fractions (0.25 means 25%) and null when nothing has been sent yet.
 */
@Getter
@AllArgsConstructor
public class FunnelResponse {
    private final long total;
    private final long applied;
    private final long interviewed;
    private final long offers;
    private final long rejected;
    private final long withdrawn;
    // interviewed / applied
    private final Double interviewRate;
    // offers / applied
    private final Double offerRate;

    public static FunnelResponse of(long total, long applied, long interviewed, long offers, long rejected, long withdrawn) {
        Double interviewRate = applied == 0 ? null : (double) interviewed / applied;
        Double offerRate = applied == 0 ? null : (double) offers / applied;
        return new FunnelResponse(total, applied, interviewed, offers, rejected, withdrawn, interviewRate, offerRate);
    }

    public static FunnelResponse empty() {
        return of(0, 0, 0, 0, 0, 0);
    }
}
