package com.fitsync.application.supplement;

import java.util.UUID;

/** Consulta: el detalle de un plan de suplementación */
public record GetSupplementPlanQuery(UUID planId) {
}
