package com.seriesly.core.database

import androidx.room.TypeConverter
import com.seriesly.core.common.model.ContentType

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String =
        value.joinToString("|")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")

    @TypeConverter
    fun fromContentType(type: ContentType): String = type.name

    @TypeConverter
    fun toContentType(value: String): ContentType =
        ContentType.valueOf(value)
}
