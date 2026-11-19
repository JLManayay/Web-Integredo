package com.fitsync.application.supplement;

import java.util.UUID;

/** usuario: crear un plan, opcionalmente vinculado a un programa */
public record CreateSupplementPlanCommand(UUID userId, UUID linkedProgramId) {
}
