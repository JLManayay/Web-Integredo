package com.fitsync.interfaces.coach;

import com.fitsync.application.coach.CoachCommandHandler;
import com.fitsync.application.coach.CoachQueryHandler;
import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.identity.ForbiddenException;
import com.fitsync.interfaces.identity.dto.IdentityDtos.BrandResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coach")
public class CoachController {

    public record AddAthleteRequest(@NotBlank String email) {}

    public record BrandRequest(@NotBlank String name, @NotBlank String color) {}

    public record AthleteResponse(UUID id, String fullName, String email, String plan,
                                  int programs, int plans, String programName, Integer weekPercent) {
        static AthleteResponse from(CoachQueryHandler.AthleteSummary s) {
            var a = s.athlete();
            return new AthleteResponse(a.id(), a.fullName(), a.email().value(), a.plan().name(),
                s.programs(), s.plans(), s.programName(), s.weekPercent());
        }
    }

    private final CoachCommandHandler commands;
    private final CoachQueryHandler queries;

    public CoachController(CoachCommandHandler commands, CoachQueryHandler queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping("/athletes")
    public List<AthleteResponse> athletes(CurrentUser me) {
        requireCoach(me);
        return queries.athletesOf(me.id()).stream().map(AthleteResponse::from).toList();
    }

    @PostMapping("/athletes")
    @ResponseStatus(HttpStatus.CREATED)
    public List<AthleteResponse> addAthlete(CurrentUser me, @Valid @RequestBody AddAthleteRequest request) {
        requireCoach(me);
        commands.linkAthlete(me.id(), request.email());
        return athletes(me);
    }

    @DeleteMapping("/athletes/{athleteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAthlete(CurrentUser me, @PathVariable UUID athleteId) {
        requireCoach(me);
        commands.unlinkAthlete(me.id(), athleteId);
    }

    @PutMapping("/brand")
    public BrandResponse brand(CurrentUser me, @Valid @RequestBody BrandRequest request) {
        requireCoach(me);
        return BrandResponse.from(commands.configureBrand(me.id(), request.name(), request.color()).brand().orElse(null));
    }

    private static void requireCoach(CurrentUser me) {
        if (!me.isCoach()) {
            throw new ForbiddenException("Esta sección es solo para cuentas de entrenador.");
        }
    }
}
