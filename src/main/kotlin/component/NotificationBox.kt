package component

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import java.text.SimpleDateFormat
import java.util.*

/**
 * @description 通知框组件
 * @author      jankinwu
 * @date        2024/3/28 20:44
 */

data class Message(
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expireTime: Long = System.currentTimeMillis() + 5_000,
    val isExpired: Boolean = false
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
            delay(1000)
            currentTime.value = System.currentTimeMillis()
            isVisible = currentTime.value <= message.expireTime
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
    listState: LazyListState // 添加 LazyListState 参数
) {
    val visibleStates = remember { mutableStateMapOf<Message, Boolean>() }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
//            .animateContentSize(animationSpec = tween(1000, easing = LinearEasing))
        ,
        reverseLayout = false,
        state = listState
    ) {
        itemsIndexed(messages) { _, message ->
            MessageItem(message, windowWidthState)
            visibleStates[message] = true
        }
    }
}

@Composable
fun TextContent(message: Message, windowWidthState: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .width((windowWidthState / 4).dp)
                .background(Color.Black.copy(0.3f), shape = RoundedCornerShape(2.dp)),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message.content,
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
            Text(
                text = SimpleDateFormat("HH:mm:ss").format(Date(message.timestamp)),
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
            val msg = "测试消息1111111$i"
            messages.add(Message(msg))
            delay(2000)
        }
    }
}