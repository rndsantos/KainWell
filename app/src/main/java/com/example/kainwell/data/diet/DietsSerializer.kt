package com.example.kainwell.data.diet

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.kainwell.Diets
import java.io.InputStream
import java.io.OutputStream

object DietsSerializer : Serializer<Diets> {
    override val defaultValue: Diets = Diets.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Diets {
        try {
            return Diets.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Diets,
        output: OutputStream,
    ) = t.writeTo(output)
}