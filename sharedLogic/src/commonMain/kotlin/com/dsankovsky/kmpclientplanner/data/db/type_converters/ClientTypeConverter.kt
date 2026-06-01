package com.dsankovsky.kmpclientplanner.data.db.type_converters

import androidx.room.TypeConverter
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.ServiceDateTimeListDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.TattooClientSpecificFieldsDbModel
import kotlinx.serialization.json.Json

class ClientTypeConverter {

    @TypeConverter
    fun convertServiceDateTimeListToJsonString(lessonDateTimeList: ServiceDateTimeListDbModel): String {
        return Json.encodeToString(lessonDateTimeList)
    }

    @TypeConverter
    fun convertJsonStringToServiceDateTimeList(jsonString: String): ServiceDateTimeListDbModel {
        return Json.decodeFromString<ServiceDateTimeListDbModel>(
            jsonString
        )
    }

    @TypeConverter
    fun convertTattooProjectListToJsonString(tattooProjectList: TattooClientSpecificFieldsDbModel.TattooProjectsListDbModel): String {
        return Json.encodeToString(tattooProjectList)
    }

    @TypeConverter
    fun convertJsonStringToTattooProjectList(jsonString: String): TattooClientSpecificFieldsDbModel.TattooProjectsListDbModel {
        return Json.decodeFromString<TattooClientSpecificFieldsDbModel.TattooProjectsListDbModel>(
            jsonString
        )
    }

    @TypeConverter
    fun convertTattooProjectToJsonString(tattooProject: TattooClientSpecificFieldsDbModel.TattooProjectDbModel): String {
        return Json.encodeToString(tattooProject)
    }

    @TypeConverter
    fun convertJsonStringToTattooProject(jsonString: String): TattooClientSpecificFieldsDbModel.TattooProjectDbModel {
        return Json.decodeFromString<TattooClientSpecificFieldsDbModel.TattooProjectDbModel>(
            jsonString
        )
    }
}