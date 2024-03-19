package dto

import lombok.Data

/**
 * @author wwg
 * @description
 * @date 2024/3/18 23:49
 */
@Data
class TeamDTO {
    var name: String? = null

    var players: List<TeamPlayerDTO>? = null
}
