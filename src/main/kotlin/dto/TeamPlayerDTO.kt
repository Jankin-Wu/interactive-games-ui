package dto

import java.math.BigDecimal

/**
 * @author jankinwu
 * @description
 * @date 2024/3/18 11:18
 */
class TeamPlayerDTO {
    var uid: Int? = null

    var uname: String? = null

    var avatarUrl: String? = null

    var gold: BigDecimal? = null

    var refreshIntervalMs: Long? = null

    var refreshFlag: Long? = null
}
