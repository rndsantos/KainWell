package com.example.kainwell.data.nutrient

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.kainwell.NutritionalIntakesEntity
import java.io.InputStream
import java.io.OutputStream

object NutritionalIntakesEntitySerializer : Serializer<NutritionalIntakesEntity> {
    override val defaultValue: NutritionalIntakesEntity =
        NutritionalIntakesEntity.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NutritionalIntakesEntity {
        try {
            return NutritionalIntakesEntity.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: NutritionalIntakesEntity,
        output: OutputStream,
    ) = t.writeTo(output)
}