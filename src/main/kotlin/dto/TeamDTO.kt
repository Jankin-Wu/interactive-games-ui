package dto

import kotlinx.serialization.Serializable

/**
 * @author jankinwu
 * @description
 * @date 2024/3/18 23:49
 */
@Serializable
class TeamDTO {
    var name: String? = null

    var players: List<TeamPlayerDTO>? = null
}
