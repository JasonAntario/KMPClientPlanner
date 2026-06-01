package com.dsankovsky.kmpclientplanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dsankovsky.kmpclientplanner.data.db.dao.client.ClientsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.EducationClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.SportClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.client.TattooClientFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.BeautyServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.EducationServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.ServicesDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.SportServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.dao.service.TattooServiceFieldsDao
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseClientDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.EducationClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.SportClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.clients.TattooClientSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.BeautyServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.EducationServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.SportServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.models.specific_fields.services.TattooServiceSpecificFieldsDbModel
import com.dsankovsky.kmpclientplanner.data.db.type_converters.ClientTypeConverter
import com.dsankovsky.kmpclientplanner.data.db.type_converters.ServiceTypeConverter

@TypeConverters(
    value = [
        ClientTypeConverter::class,
        ServiceTypeConverter::class
    ]
)
@Database(
    entities = [
        BaseClientDbModel::class,
        BaseServiceDbModel::class,
        EducationClientSpecificFieldsDbModel::class,
        TattooClientSpecificFieldsDbModel::class,
        SportClientSpecificFieldsDbModel::class,
        EducationServiceSpecificFieldsDbModel::class,
        BeautyServiceSpecificFieldsDbModel::class,
        TattooServiceSpecificFieldsDbModel::class,
        SportServiceSpecificFieldsDbModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientsDao(): ClientsDao
    abstract fun educationClientFieldsDao(): EducationClientFieldsDao
    abstract fun sportClientFieldsDao(): SportClientFieldsDao
    abstract fun tattooClientFieldsDao(): TattooClientFieldsDao

    abstract fun servicesDao(): ServicesDao
    abstract fun beautyServiceFieldsDao(): BeautyServiceFieldsDao
    abstract fun tattooServiceFieldsDao(): TattooServiceFieldsDao
    abstract fun sportServiceFieldsDao(): SportServiceFieldsDao
    abstract fun educationServiceFieldsDao(): EducationServiceFieldsDao

    companion object {
        const val DB_NAME = "my_schedule_pal.db"
    }
}