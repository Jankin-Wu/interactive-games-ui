package dto

import lombok.Data
import java.math.BigDecimal

/**
 * @author jankinwu
 * @description
 * @date 2024/3/18 11:18
 */
@Data
class TeamPlayerDTO {
    var uid: Int? = null

    var uname: String? = null

    var avatarUrl: String? = null

    var gold: BigDecimal? = null
}
