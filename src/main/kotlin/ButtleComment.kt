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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
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
var queue = Channel<BulletCommentMsgDTO>(capacity = 100)
var queueSize = 0

@Preview
@Composable
fun BulletComment(windowWidth: Int) {
//    val infiniteTransition = rememberInfiniteTransition()
    val scrollSpeedRatio = 200.0
    val currentScrollSpeed by derivedStateOf { scrollSpeedRatio / 1000 }
    var windowWidthState by remember { mutableStateOf(windowWidth) }
    var textWidth by remember { mutableStateOf(windowWidth.toFloat()) }
    var imageWidth by remember { mutableStateOf(0f) }
    val spacerWidth = 5
//    val composableHeight by remember { mutableStateOf(windowHeight) }
    var maxOffset by remember { mutableStateOf(windowWidthState.toFloat()) }
    // 刚启动时会出现 textWidth 为0的情况，如果为0，则将 contentFullWith 设置为窗口宽度
    val contentFullWith by derivedStateOf { if (textWidth.toInt() == 0)   windowWidth.toFloat() else  textWidth + imageWidth + spacerWidth }
    val bulletCommentDTOState by remember { mutableStateOf(bulletCommentState) }
    var consumeBulletCommentDTOState by remember { mutableStateOf(BulletCommentMsgDTO()) }
    var consumeRefreshFlag: Long by remember { mutableStateOf(0) }
    var consumeCompletedFlag: Long by remember { mutableStateOf(0) }
    var textToDisplay by remember { mutableStateOf("") }
    var avatarToDisplay by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(0) }
    var isFirstToQueue by remember { mutableStateOf(true) }
    val durationMillisState by derivedStateOf { (maxOffset / currentScrollSpeed).toInt() }
    var currentTask by remember { mutableStateOf<Boolean>(false) }
    var moveToLeft by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = moveToLeft)
    val targetOffset by derivedStateOf {
//        println("textWidth: $textWidth, contentFullWith: $contentFullWith")
//        println(windowWidth)
        // 在弹幕滚动时不更新偏移量，避免因为改变窗口大小导致弹幕消失
        if (!currentTask) {
            maxOffset = if (windowWidthState < contentFullWith) {
                windowWidthState + contentFullWith
            } else {
                windowWidthState.toFloat()
            }
        }
        Offset(-maxOffset, 0F)
    }
    val offsetTransition = transition.animateValue(
        Offset.VectorConverter,
        transitionSpec = {
            if (moveToLeft) {
//                println("durationMillis_2: $durationMillisState")
                tween(durationMillis = durationMillisState, easing = LinearEasing)
            } else {
                snap()
            }
        },
        label = "ValueAnimation",
        targetValueByState = { state ->
            if (state) {
//                Offset(-maxOffset, 0F)
                targetOffset
            } else {

                Offset(contentFullWith, 0F)
            }
        })

    // 将消息加入队列
    LaunchedEffect(bulletCommentDTOState.value) {
        if (!isFirstToQueue) {
            launch {
                bulletCommentDTOState.let { value ->
                    queue.send(value.value)
                    queueSize++
                    println("queue size: $queueSize")
                }
            }
        }
    }

    // 更新弹幕协程
    LaunchedEffect(consumeRefreshFlag) {
        if (isFirstToQueue) {
            return@LaunchedEffect
        }
        println("开始更新弹幕")
        textToDisplay = consumeBulletCommentDTOState.text
        avatarToDisplay = consumeBulletCommentDTOState.avatarUrl.toString()
        textColor = consumeBulletCommentDTOState.fill.toInt()
        moveToLeft = true
        println("durationMillis_1: $durationMillisState")
        // 在动画的持续时间基础上加延时，避免因为动画达到临界时间时切换状态导致动画一闪而过的问题
        delay(durationMillisState.toLong() + 500)
        println("弹幕执行完毕")
        moveToLeft = false
        currentTask = false
        consumeCompletedFlag++
    }

    // 消费队列协程
    LaunchedEffect(consumeCompletedFlag) {
        // 组件初始化时不执行任务
        if (isFirstToQueue) {
            isFirstToQueue = false
            return@LaunchedEffect
        }
        if (!currentTask) {
            println("开始消费队列")
            val item = queue.receive()
            currentTask = true
            println("consume msg: ${JSONObject.toJSONString(item)}")
            queueSize--
            println("queue size: $queueSize")
            consumeBulletCommentDTOState = item
            consumeRefreshFlag++
        }
    }

    // 看门狗协程
    LaunchedEffect(Unit) {
        while (true) {
            // 双重检测，防止已经执行后再次启动消费协程
            if (!currentTask && !moveToLeft) {
                delay(1000)
                if (!moveToLeft) {
                    println("看门狗尝试进行队列消费")
                    consumeCompletedFlag++
                }
            } else {
                delay(2000)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .onGloballyPositioned {
                windowWidthState = it.size.width
            }
            .widthIn(min = textWidth.dp)
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .wrapContentWidth()
                .widthIn(min = textWidth.dp)
                .offset(x = offsetTransition.value.x.dp)
        ) {
            // 圆形头像框
            Box(
                modifier = Modifier
                    .size(40.dp)
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
                color = Color(textColor),
                fontFamily = FontFamily.Monospace,
                fontSize = (30).sp,
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
//                    textWidth = layoutResult.size.width.toFloat()
                    textWidth = layoutResult.multiParagraph.width
//                    maxOffset = windowWidth.toFloat() + textWidth
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .width(IntrinsicSize.Max)
//                    .widthIn(min = textWidth.dp)
            )
        }
    }
}

fun handleBulletMsg(data: String) {
    val bulletCommentMsg = JSON.parseObject(data, BulletCommentMsgDTO::class.java)
    bulletCommentState.value = bulletCommentMsg
}

