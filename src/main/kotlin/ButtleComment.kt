import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alibaba.fastjson2.JSON
import com.lt.load_the_image.rememberImagePainter
import dto.BulletCommentMsgDTO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * @description
 * @author      jankinwu
 * @date        2024/3/17 15:01
 */

val bulletCommentState = mutableStateOf(BulletCommentMsgDTO())

val lock = Mutex()
val queue = Channel<BulletCommentMsgDTO>(capacity = 100)

@Preview
@Composable
fun BulletComment(durationMillis: Int = 15000, windowWidth: Int, windowHeight: Int) {
//    val infiniteTransition = rememberInfiniteTransition()
    val scrollSpeedRatio = 10.0
    val currentScrollSpeed by remember { mutableStateOf(windowWidth * scrollSpeedRatio / 100000) }
    var textWidth by remember { mutableStateOf(windowWidth.toFloat()) }
    var imageWidth by remember { mutableStateOf(0f) }
    val spacerWidth = 5
    val composableHeight by remember { mutableStateOf(windowHeight) }
    var maxOffset by remember { mutableStateOf(windowWidth.toFloat() + textWidth) }
    val bulletCommentDTOState by remember { mutableStateOf(bulletCommentState) }
    var textToDisplay by remember { mutableStateOf("") }
    var avatarToDisplay by remember { mutableStateOf("") }
    var isFirstToQueue by remember { mutableStateOf(true) }
    val durationMillisState by remember { mutableStateOf((textWidth / currentScrollSpeed).toInt()) }
    var moveToLeft by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = moveToLeft)
    val offsetTransition = transition.animateValue(
        Offset.VectorConverter,
        transitionSpec = {
            if (moveToLeft) {
                tween(durationMillis = durationMillisState, easing = LinearEasing)
            } else {
                snap()
            }
        },
        label = "ValueAnimation",
        targetValueByState = { state ->
            if (state) {
                Offset(-maxOffset, 0F)
            } else {
                Offset(maxOffset, 0F)
            }
        })
    var currentTask by remember { mutableStateOf<Boolean?>(false) }

    // 将消息加入队列
    LaunchedEffect(bulletCommentDTOState.value) {
        if (!isFirstToQueue) {
            launch {
                bulletCommentDTOState.let { value ->
                    queue.send(value.value)
                }
            }
            println("send queue: $queue")
        }
        isFirstToQueue = false
    }

    // 消费队列
    LaunchedEffect(Unit) {
        while (true) {
            println("consume queue: $queue")
            if (lock.tryLock()) { // 尝试获取互斥锁
                try {
                    if (currentTask == false) {
                        println("consume queue1: $queue")
                        val item = queue.receive()
                        println("consume queue2: $queue")
                        currentTask = true
                        textToDisplay = item.text
                        avatarToDisplay = item.avatarUrl.toString()
                        moveToLeft = true
                        // 在动画的持续时间加100毫秒，避免因为动画达到临界时间时切换状态导致动画一闪而过的问题
                        delay(durationMillisState.toLong() + 100)
                        moveToLeft = false
                        currentTask = false
                    }
                } catch (e: Exception) {
//                println(e)
                } finally {
                    lock.unlock() // 释放互斥锁
                }
            }
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = offsetTransition.value.x.dp)
        ) {
            // 圆形头像框
            Box(
                modifier = Modifier
                    .size(composableHeight.dp)
                    .background(Color.Transparent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
//                AsyncImage(
//                    modifier = Modifier
//                        .onSizeChanged { imageWidth = it.width.toFloat() }
//                        .clip(CircleShape),
//                    url = avatarToDisplay,
//                    placeHolderUrl = "image/prprpr.gif",
//                    errorUrl = "image/tiqiang.gif"
//                )
                Image(
                    painter = rememberImagePainter(avatarToDisplay),
                    contentDescription = "",
                    modifier = Modifier
                        .onSizeChanged { imageWidth = it.width.toFloat() }
                        .clip(CircleShape),
                )
            }

            Spacer(modifier = Modifier.width(spacerWidth.dp))


            Text(
                text = textToDisplay,
                color = Color(bulletCommentDTOState.value.fill),
                fontFamily = FontFamily.Monospace,
                fontSize = (composableHeight / 3 * 2).sp,
                softWrap = false,
                overflow = TextOverflow.Visible,
                style = TextStyle(
//                    textDecoration = TextDecoration.Underline,
                    shadow = Shadow(
                        color = Color(bulletCommentDTOState.value.stroke),
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )
                ),
                onTextLayout = { layoutResult ->
                    textWidth = layoutResult.size.width.toFloat()
                    maxOffset = windowWidth.toFloat() + textWidth
                },
                modifier = Modifier
                    .widthIn(min = textWidth.dp)
            )
        }
    }
}

fun handleBulletMsg(data: String) {
    val bulletCommentMsg = JSON.parseObject(data, BulletCommentMsgDTO::class.java)
    bulletCommentState.value = bulletCommentMsg
}

