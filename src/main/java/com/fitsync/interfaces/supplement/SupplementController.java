package com.fitsync.interfaces.supplement;

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

    public SupplementController(SupplementCommandHandler commandHandler, SupplementQueryHandler queryHandler) {
        this.commandHandler = commandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    public ResponseEntity<SupplementPlanResponse> create(@Valid @RequestBody CreateSupplementPlanRequest request) {
        SupplementPlan plan = commandHandler.handle(new CreateSupplementPlanCommand(request.userId(), request.linkedProgramId()));
        return ResponseEntity.created(URI.create("/api/supplement-plans/" + plan.id()))
            .body(SupplementPlanResponse.from(plan));
    }

    @GetMapping
    public List<SupplementPlanResponse> listByUser(@RequestParam UUID userId) {
        return queryHandler.handle(new ListSupplementPlansByUserQuery(userId)).stream().map(SupplementPlanResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SupplementPlanResponse getById(@PathVariable UUID id) {
        return SupplementPlanResponse.from(queryHandler.handle(new GetSupplementPlanQuery(id)));
    }

    @PostMapping("/{id}/doses")
    public SupplementPlanResponse scheduleDose(@PathVariable UUID id, @Valid @RequestBody ScheduleDoseRequest request) {
        return SupplementPlanResponse.from(commandHandler.handle(new ScheduleDoseCommand(
            id, request.supplementName(), request.grams(), request.timeOfDay(), request.relativeToWorkout())));
    }
}
