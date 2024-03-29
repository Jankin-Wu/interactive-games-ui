package utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @description 时间工具类
 * @author      jankinwu
 * @date        2024/3/29 22:10
 */
fun convertTimestampToTimeStr(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss")
    val date = Date(timestamp)
    return dateFormat.format(date)
}