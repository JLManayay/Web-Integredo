package com.fitsync.application.supplement;

import java.util.UUID;

/** Consulta: todos los planes de suplementación de un usuario */
public record ListSupplementPlansByUserQuery(UUID userId) {
}
