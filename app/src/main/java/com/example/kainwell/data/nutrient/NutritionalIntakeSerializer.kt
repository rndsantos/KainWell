package com.example.kainwell.data.nutrient

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.kainwell.NutritionalIntakes
import java.io.InputStream
import java.io.OutputStream

object NutritionalIntakeSerializer : Serializer<NutritionalIntakes> {
    override val defaultValue: NutritionalIntakes = NutritionalIntakes.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): NutritionalIntakes {
        try {
            return NutritionalIntakes.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: NutritionalIntakes,
        output: OutputStream,
    ) = t.writeTo(output)
}