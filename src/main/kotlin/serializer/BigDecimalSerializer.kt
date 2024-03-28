package serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal

/**
 * @description
 * @author      wwg
 * @date        2024/3/28 11:05
 */
class BigDecimalSerializer : KSerializer<BigDecimal>{
    override val descriptor = PrimitiveSerialDescriptor("java.math.BigDecimal", PrimitiveKind.DOUBLE)

    /**
     * If decoding JSON uses [JsonDecoder.decodeJsonElement] to get the raw content,
     * otherwise decodes using [Decoder.decodeString].
     */
    override fun deserialize(decoder: Decoder): BigDecimal =
        when (decoder) {
            is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content.toBigDecimal()
            else           -> decoder.decodeString().toBigDecimal()
        }

    /**
     * If encoding JSON uses [JsonUnquotedLiteral] to encode the exact [BigDecimal] value.
     *
     * Otherwise, [value] is encoded using encodes using [Encoder.encodeString].
     */
    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: BigDecimal) =
        when (encoder) {
            is JsonEncoder -> encoder.encodeJsonElement(JsonUnquotedLiteral(value.toPlainString()))
            else           -> encoder.encodeString(value.toPlainString())
        }

}