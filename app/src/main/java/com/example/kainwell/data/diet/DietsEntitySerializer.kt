package com.example.kainwell.data.diet

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.example.kainwell.DietsEntity
import java.io.InputStream
import java.io.OutputStream

object DietsEntitySerializer : Serializer<DietsEntity> {
    override val defaultValue: DietsEntity = DietsEntity.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): DietsEntity {
        try {
            return DietsEntity.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: DietsEntity,
        output: OutputStream,
    ) = t.writeTo(output)
}