package com.mgacreative.mgaglobal.core.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object IntToBooleanSerializer : KSerializer<Boolean> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IntToBoolean", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Boolean {
        return try {
            // Try to decode as Int (0 or 1)
            val intValue = decoder.decodeInt()
            intValue != 0
        } catch (e: Exception) {
            // Fallback to Boolean if it's already a boolean literal
            try {
                decoder.decodeBoolean()
            } catch (e2: Exception) {
                false
            }
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 1 else 0)
    }
}

