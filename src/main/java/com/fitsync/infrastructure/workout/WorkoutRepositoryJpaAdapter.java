package com.fitsync.infrastructure.workout;

import com.fitsync.domain.workout.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WorkoutRepositoryJpaAdapter implements WorkoutRepository {

    private final SpringDataWorkoutJpaRepository jpaRepository;

    public WorkoutRepositoryJpaAdapter(SpringDataWorkoutJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WorkoutProgram save(WorkoutProgram program) {
        WorkoutProgramJpaEntity entity = toJpaEntity(program);
        WorkoutProgramJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<WorkoutProgram> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorkoutProgram> findAllByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByNameAsc(userId).stream().map(this::toDomain).toList();
    }

    // ---- Dominio -> JPA ----
    private WorkoutProgramJpaEntity toJpaEntity(WorkoutProgram program) {
        WorkoutProgramJpaEntity entity = new WorkoutProgramJpaEntity(
            program.id(), program.userId(), program.name(), program.splitType(), program.isPublished()
        );
        List<WorkoutDayJpaEntity> days = new ArrayList<>();
        for (WorkoutDay day : program.days()) {
            WorkoutDayJpaEntity dayEntity = new WorkoutDayJpaEntity(day.id(), entity, day.dayOfWeek());
            List<ExerciseJpaEntity> exercises = new ArrayList<>();
            for (Exercise exercise : day.exercises()) {
                exercises.add(new ExerciseJpaEntity(
                    exercise.id(), dayEntity, exercise.name(),
                    exercise.muscleGroup().name(),
                    exercise.reps().sets(), exercise.reps().repsPerSet(),
                    exercise.rest().seconds()
                ));
            }
            dayEntity.setExercises(exercises);
            days.add(dayEntity);
        }
        entity.setDays(days);
        return entity;
    }

    // ---- JPA -> Dominio ----
    private WorkoutProgram toDomain(WorkoutProgramJpaEntity entity) {
        WorkoutProgram program = WorkoutProgram.reconstruct(
            entity.getId(), entity.getUserId(), entity.getName(),
            entity.getSplitType(), entity.isPublished()
        );
        for (WorkoutDayJpaEntity dayEntity : entity.getDays()) {
            WorkoutDay day = program.reconstructDay(dayEntity.getId(), dayEntity.getDayOfWeek());
            for (ExerciseJpaEntity exerciseEntity : dayEntity.getExercises()) {
                day.reconstructExercise(
                    exerciseEntity.getId(),
                    exerciseEntity.getName(),
                    MuscleGroup.of(exerciseEntity.getMuscleGroup()),
                    Reps.of(exerciseEntity.getSets(), exerciseEntity.getRepsPerSet()),
                    RestPeriod.ofSeconds(exerciseEntity.getRestSeconds())
                );
            }
        }
        return program;
    }
}
