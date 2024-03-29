package component

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import constants.blueTeam
import constants.blueTeamColor
import constants.redTeam
import constants.redTeamColor
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import utils.convertTimestampToTimeStr

/**
 * @description 通知框组件
 * @author      jankinwu
 * @date        2024/3/28 20:44
 */

const val displayTime: Long = 25000

const val default: String = "default"

@Serializable
data class NotifyBoxMessage(
    val content: String,
    var type: String = default,
    var notifyTimeStamp: Long = 0L,
    var playerName: String = "",
    var playerTeamColor: String = "",
    var playerTeamName: String = "",
    var unitName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val expireTime: Long = System.currentTimeMillis() + displayTime,
    var isExpired: Boolean = false
)

val notifyBoxMessages = mutableStateListOf<NotifyBoxMessage>()

@Composable
fun MessageList(
    notifyBoxMessages: List<NotifyBoxMessage>,
    listState: LazyListState
) {
    val visibleStates = remember { mutableStateMapOf<NotifyBoxMessage, Boolean>() }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp)
        ,
        reverseLayout = false,
        state = listState
    ) {
        items(notifyBoxMessages, key = { message ->
            // 使用消息的时间戳作为唯一标识符，解决因messages元素被删除导致的所有组件被重组的问题
            message.timestamp
        }) { message ->
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .animateContentSize(tween(1000, easing = LinearEasing))
            ) {
                MessageItem(message)
            }
            visibleStates[message] = true
        }
    }
}

@Composable
fun MessageItem(
    notifyBoxMessage: NotifyBoxMessage
) {
    var isVisible by remember { mutableStateOf(false) }
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (currentTime.value < notifyBoxMessage.expireTime) {
            delay(500)
            currentTime.value = System.currentTimeMillis()
            isVisible = currentTime.value <= notifyBoxMessage.expireTime
            notifyBoxMessage.isExpired = currentTime.value > notifyBoxMessage.expireTime
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(targetOffsetY = { 0 })
    ) {
        TextContent(notifyBoxMessage)
    }
}

@Composable
fun TextContent(notifyBoxMessage: NotifyBoxMessage) {
//    val currentTime = SimpleDateFormat("HH:mm:ss").format(Date(notifyBoxMessage.timestamp))
    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
//                .width((windowWidthState / 4).dp)
                .background(Color.Black.copy(0.3f), shape = RoundedCornerShape(5.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = getAnnotatedString(notifyBoxMessage),
                fontFamily = FontFamily.SansSerif,
//                overflow = TextOverflow.Visible,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )
                ),
                modifier = Modifier
                    .padding(3.dp)
            )
        }
    }
}

internal fun getAnnotatedString(notifyBoxMessage: NotifyBoxMessage) : AnnotatedString {
    return when(notifyBoxMessage.type) {
//        "dispatch_army" -> buildDefaultAnnotatedString(notifyBoxMessage)
        else -> buildDefaultAnnotatedString(notifyBoxMessage)
    }
}

internal fun buildDefaultAnnotatedString(notifyBoxMessage: NotifyBoxMessage): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.White)) {
            append("[${convertTimestampToTimeStr(notifyBoxMessage.notifyTimeStamp)}] ")
        }
        withStyle(style = SpanStyle(color = if(notifyBoxMessage.playerTeamColor == redTeam) Color(redTeamColor)
        else if (notifyBoxMessage.playerTeamColor == blueTeam) Color(blueTeamColor) else Color.Cyan)) {
            append(notifyBoxMessage.playerName)
        }
        withStyle(style = SpanStyle(color = Color.White)) {
            append(notifyBoxMessage.content)
        }
        withStyle(style = SpanStyle(color = Color(0xfff2be47))) {
            append(notifyBoxMessage.unitName)
        }
        withStyle(style = SpanStyle(color = if(notifyBoxMessage.playerTeamColor == redTeam) Color(redTeamColor) else Color(blueTeamColor))) {
            append(notifyBoxMessage.playerTeamName)
        }
    }
    return annotatedString
}

//internal fun buildDefaultAnnotatedString(notifyBoxMessage: NotifyBoxMessage) : AnnotatedString{
//    return buildAnnotatedString {
//        withStyle(style = SpanStyle(color = Color.White)) {
//            append("[${convertTimestampToTimeStr(notifyBoxMessage.notifyTimeStamp)}] ")
//        }
//        withStyle(style = SpanStyle(color = Color.Cyan)) {
//            append(notifyBoxMessage.playerName)
//        }
//        withStyle(style = SpanStyle(color = Color.White)) {
//            append(notifyBoxMessage.content)
//        }
//        withStyle(style = SpanStyle(color = if(notifyBoxMessage.playerTeamColor == redTeam) Color(redTeamColor) else Color(blueTeamColor))) {
//            append(notifyBoxMessage.playerTeamName)
//        }
//    }
//}

//internal fun buildJoinAnnotatedString(notifyBoxMessage: NotifyBoxMessage) : AnnotatedString{
//    return buildAnnotatedString {
//        withStyle(style = SpanStyle(color = Color.White)) {
//            append("[${convertTimestampToTimeStr(notifyBoxMessage.notifyTimeStamp)}] ")
//        }
//        withStyle(style = SpanStyle(color = Color.Cyan)) {
//            append(notifyBoxMessage.playerName)
//        }
//        withStyle(style = SpanStyle(color = Color.White)) {
//            append(notifyBoxMessage.content)
//        }
//        withStyle(style = SpanStyle(color = if(notifyBoxMessage.playerTeamColor == redTeam) Color(redTeamColor) else Color(blueTeamColor))) {
//            append(notifyBoxMessage.playerTeamName)
//        }
//    }
//}


@Composable
fun ChatBox() {
    var windowWidthState by remember { mutableStateOf(0) }
//    val messages = remember { mutableStateListOf<Message>() }
    val messagesState by remember { mutableStateOf(notifyBoxMessages) }
    val listState = rememberLazyListState() // 创建 LazyListState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onGloballyPositioned {
                windowWidthState = it.size.width
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {
            MessageList(messagesState, listState) // 将 listState 传递给 MessageList
        }
    }

//    LaunchedEffect(Unit) {
//        var i = 0
//        while (true) {
//            i += 1
//            val msg = "测试文本测试文本ABCDabcd$i"
//            notifyBoxMessages.add(NotifyBoxMessage(msg))
////            val messagesString = Json.encodeToString<List<NotifyBoxMessage>>(notifyBoxMessages)
////            println("messages: $messagesString")
//            delay(2000)
//        }
//    }
    // 定时清理 messages 中的过期消息
    LaunchedEffect(Unit) {
        while (true) {
            if (notifyBoxMessages.isNotEmpty() && notifyBoxMessages.first().isExpired) {
                notifyBoxMessages.removeFirst()
            }
//            val messagesString = Json.encodeToString<List<Message>>(messages)
//            println("remove messages: $messagesString")
//            println("messages size: ${messages.size}")
            delay(2000)
        }
    }
}

fun handleNotificationMsg(data: String) {
    val notifyBoxMessage = Json.decodeFromString<NotifyBoxMessage>(data)
    notifyBoxMessages.add(notifyBoxMessage)
    println("notifyBoxMessage: $notifyBoxMessage")
}