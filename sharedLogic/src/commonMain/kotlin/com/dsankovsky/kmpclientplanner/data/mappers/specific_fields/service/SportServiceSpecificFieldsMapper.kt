package com.dsankovsky.kmpclientplanner.data.mappers.specific_fields.service

import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.SportServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields

fun SportServiceSpecificFieldsDbModel.toSportServiceSpecificFields() =
    ServiceSpecificFields.SportServiceSpecificFields(
        id = id,
        serviceId = serviceId,
        isOnline = isOnline,
        exercises = exercises.exercises.map { it.toExercise() }
    )

fun SportServiceSpecificFieldsDbModel.ExerciseDbModel.toExercise() =
    ServiceSpecificFields.SportServiceSpecificFields.Exercise(
        title = title,
        sets = sets.sets.map { it.toExerciseSet() }
    )

fun SportServiceSpecificFieldsDbModel.ExerciseSetDbModel.toExerciseSet() =
    ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet(
        repeats = repeat.toString(),
        weight = weight.toString()
    )

fun ServiceSpecificFields.SportServiceSpecificFields.toSportServiceSpecificFieldsDbModel() =
    SportServiceSpecificFieldsDbModel(
        id = id,
        serviceId = serviceId,
        isOnline = isOnline,
        exercises = SportServiceSpecificFieldsDbModel.ExercisesListDbModel(
            exercises = exercises.map { it.toExerciseDbModel() }
        )
    )

fun ServiceSpecificFields.SportServiceSpecificFields.Exercise.toExerciseDbModel() =
    SportServiceSpecificFieldsDbModel.ExerciseDbModel(
        title = title,
        sets = SportServiceSpecificFieldsDbModel.ExerciseSetsListDbModel(
            sets = sets.map { it.toExerciseSetsDbModel() }
        )
    )

fun ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet.toExerciseSetsDbModel() =
    SportServiceSpecificFieldsDbModel.ExerciseSetDbModel(
        repeat = repeats.toIntOrNull() ?: 0,
        weight = weight.toFloatOrNull() ?: 0f
    )