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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

/**
 * @description 通知框组件
 * @author      jankinwu
 * @date        2024/3/28 20:44
 */

const val displayTime: Long = 10000

@Serializable
data class Message(
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expireTime: Long = System.currentTimeMillis() + displayTime,
    var isExpired: Boolean = false
)


@Composable
fun MessageItem(
    message: Message,
    windowWidthState: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (currentTime.value < message.expireTime) {
            delay(500)
            currentTime.value = System.currentTimeMillis()
            isVisible = currentTime.value <= message.expireTime
            message.isExpired = currentTime.value > message.expireTime
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000)) + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut(animationSpec = tween(durationMillis = 1000)) + slideOutVertically(targetOffsetY = { 0 })
    ) {
        TextContent(message, windowWidthState)
    }
}

@Composable
fun MessageList(
    messages: List<Message>,
    windowWidthState: Int,
    listState: LazyListState
) {
    val visibleStates = remember { mutableStateMapOf<Message, Boolean>() }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp)
        ,
        reverseLayout = false,
        state = listState
    ) {
        items(messages, key = { message ->
            // 使用消息的时间戳作为唯一标识符
            message.timestamp
        }) { message ->
            Row(
                modifier = Modifier
                    .width(300.dp)
                    .animateContentSize(tween(1000, easing = LinearEasing))
            ) {
                MessageItem(message, windowWidthState)
            }
            visibleStates[message] = true
        }
    }
}

@Composable
fun TextContent(message: Message, windowWidthState: Int) {
    val currentTime = SimpleDateFormat("HH:mm:ss").format(Date(message.timestamp))
    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
//                .width((windowWidthState / 4).dp)
                .background(Color.Black.copy(0.3f), shape = RoundedCornerShape(2.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "[$currentTime] ${message.content}",
//                fontFamily = FontFamily.Cursive,
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


@Composable
fun ChatBox() {
    var windowWidthState by remember { mutableStateOf(0) }
    val messages = remember { mutableStateListOf<Message>() }
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
            MessageList(messages, windowWidthState, listState) // 将 listState 传递给 MessageList
        }
    }

    LaunchedEffect(Unit) {
        var i = 0
        while (true) {
            i += 1
            val msg = "测试文本测试文本ABCDabcd$i"
            messages.add(Message(msg))
            val messagesString = Json.encodeToString<List<Message>>(messages)
//            println("messages: $messagesString")
            delay(2000)
        }
    }
    // 定时清理 messages 中的过期消息
    LaunchedEffect(Unit) {
        while (true) {
            if (messages.isNotEmpty()) {
                messages.removeFirst()
            }
            val messagesString = Json.encodeToString<List<Message>>(messages)
            println("remove messages: $messagesString")
            delay(displayTime + 1000)
        }
    }
}