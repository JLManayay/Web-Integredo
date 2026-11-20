package com.fitsync.interfaces.supplement;

import com.fitsync.application.identity.AccessGuard;
import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.supplement.*;
import com.fitsync.domain.supplement.SupplementPlan;
import com.fitsync.interfaces.supplement.dto.SupplementDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supplement-plans")
public class SupplementController {

    private final SupplementCommandHandler commandHandler;
    private final SupplementQueryHandler queryHandler;
    private final AccessGuard guard;

    public SupplementController(SupplementCommandHandler commandHandler, SupplementQueryHandler queryHandler, AccessGuard guard) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
        this.guard = guard;
    }

    @PostMapping
    public ResponseEntity<SupplementPlanResponse> create(CurrentUser me, @Valid @RequestBody CreateSupplementPlanRequest request) {
        UUID owner = guard.resolveOwner(me, request.userId());
        SupplementPlan plan = commandHandler.handle(new CreateSupplementPlanCommand(owner, request.linkedProgramId()));
        return ResponseEntity.created(URI.create("/api/supplement-plans/" + plan.id()))
            .body(SupplementPlanResponse.from(plan));
    }

    @GetMapping
    public List<SupplementPlanResponse> listByUser(CurrentUser me, @RequestParam(required = false) UUID userId) {
        UUID owner = guard.resolveOwner(me, userId);
        return queryHandler.handle(new ListSupplementPlansByUserQuery(owner)).stream().map(SupplementPlanResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SupplementPlanResponse getById(CurrentUser me, @PathVariable UUID id) {
        return SupplementPlanResponse.from(accessible(me, id));
    }

    @PostMapping("/{id}/doses")
    public SupplementPlanResponse scheduleDose(CurrentUser me, @PathVariable UUID id, @Valid @RequestBody ScheduleDoseRequest request) {
        accessible(me, id);
        return SupplementPlanResponse.from(commandHandler.handle(new ScheduleDoseCommand(
            id, request.supplementName(), request.grams(), request.timeOfDay(), request.relativeToWorkout())));
    }

    private SupplementPlan accessible(CurrentUser me, UUID planId) {
        SupplementPlan plan = queryHandler.handle(new GetSupplementPlanQuery(planId));
        guard.ensureCanAccess(me, plan.userId());
        return plan;
    }
}
