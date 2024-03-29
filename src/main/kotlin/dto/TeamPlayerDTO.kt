package dto

import serializer.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * @author jankinwu
 * @description
 * @date 2024/3/18 11:18
 */
@Serializable
class TeamPlayerDTO {
    var openId: String? = null

    var uname: String? = null

    var avatarUrl: String = ""

    @Serializable(BigDecimalSerializer::class)
    var gold: BigDecimal? = null

    var refreshIntervalMs: Long? = null

    var refreshFlag: Long? = null
}
