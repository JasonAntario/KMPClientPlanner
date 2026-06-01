package com.dsankovsky.kmpclientplanner.data.db.type_converters

import androidx.room.TypeConverter
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.ImagesListDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.SportServiceSpecificFieldsDbModel
import kotlinx.serialization.json.Json

class ServiceTypeConverter {

    @TypeConverter
    fun convertServiceImageListToJsonString(imageList: ImagesListDbModel): String {
        return Json.encodeToString(imageList)
    }

    @TypeConverter
    fun convertJsonStringToServiceImageList(jsonString: String): ImagesListDbModel {
        return Json.decodeFromString<ImagesListDbModel>(jsonString)
    }

    @TypeConverter
    fun convertExercisesListDbModelToJsonString(exercisesList: SportServiceSpecificFieldsDbModel.ExercisesListDbModel): String {
        return Json.encodeToString(exercisesList)
    }

    @TypeConverter
    fun convertJsonStringToExercisesListDbModel(jsonString: String): SportServiceSpecificFieldsDbModel.ExercisesListDbModel {
        return Json.decodeFromString<SportServiceSpecificFieldsDbModel.ExercisesListDbModel>(
            jsonString
        )
    }
}