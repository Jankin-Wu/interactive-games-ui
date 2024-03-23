package dto

/**
 * @author jankinwu
 * @description
 * @date 2024/3/9 2:59
 */
class BulletCommentMsgDTO {
    var text: String = ""
    var fontSize: String = "40"
    var fill: Long = 0xFFFFFFFF
    var stroke: Long = 0xFF000000
    var fontFamily: String = "Source Han Sans"
    var type: String? = null
    var avatarUrl: String? = ""
}
