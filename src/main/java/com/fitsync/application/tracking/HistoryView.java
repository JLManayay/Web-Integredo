package com.fitsync.application.tracking;

import com.fitsync.domain.tracking.WeeklyAdherence;

import java.util.List;

/** limited = true cuando el plan Gratis solo deja ver la semana actual. */
public record HistoryView(boolean limited, List<WeeklyAdherence> weeks) {
}
