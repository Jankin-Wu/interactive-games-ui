package dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

/**
 * @author jankinwu
 * @description
 * @date 2024/3/18 11:18
 */
@Serializable
class TeamPlayerDTO {
    var uid: Int? = null

    var uname: String? = null

    var avatarUrl: String = ""

    @Contextual
    var gold: BigDecimal? = null

    var refreshIntervalMs: Long? = null

    var refreshFlag: Long? = null
}
