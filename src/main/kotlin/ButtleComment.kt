import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay

/**
 * @description
 * @author      jankinwu
 * @date        2024/3/17 15:01
 */
@Preview
@Composable
fun App(durationMillis: Int = 15000, windowWidth: Int, windowHeight: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val scrollSpeedRatio = 5.0
    val currentScrollSpeed by remember { mutableStateOf(windowWidth * scrollSpeedRatio / 100000 ) }
    var textWidth by remember { mutableStateOf(windowWidth.toFloat()) }
    var imageWidth by remember { mutableStateOf(0f) }
    val spacerWidth = 8
    val composableHeight by remember { mutableStateOf(windowHeight) }
    var maxOffset by remember { mutableStateOf(windowWidth.toFloat() + textWidth) }
    var text by remember { mutableStateOf("") }
    val offsetX by infiniteTransition.animateValue(
        initialValue = maxOffset,
        targetValue = -maxOffset,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (textWidth / currentScrollSpeed).toInt(), delayMillis = 0, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = offsetX.dp)
        ) {
            // 圆形头像框
            Box(
                modifier = Modifier
                    .size(composableHeight.dp)
                    .background(Color.Transparent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .onSizeChanged { imageWidth = it.width.toFloat() }
                        .clip(CircleShape),
                    url = "https://i1.hdslb.com/bfs/face/8dcda8cc51f125f739d0defb5d6e943a66e55669.jpg",
                    placeHolderUrl = "image/prprpr.gif",
                    errorUrl = "image/tiqiang.gif"
                )
            }

            Spacer(modifier = Modifier.width(spacerWidth.dp))


            Text(
                text = "这是一条循环滚动的弹幕啦啦啦啦啦asddfASDDFD",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = (composableHeight / 3 * 2).sp,
                softWrap = false,
                overflow = TextOverflow.Visible,
                style = TextStyle(
//                    textDecoration = TextDecoration.Underline,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f
                    )
                ),
                onTextLayout = { layoutResult ->
                    textWidth = layoutResult.size.width.toFloat()
                    maxOffset = windowWidth.toFloat() + textWidth.toFloat() + imageWidth + spacerWidth
                },
                modifier = Modifier
                    .widthIn(min = textWidth.dp)
//                    .width(textWidth.dp)
            )
        }
    }
    websocketClient()
}

@Composable
fun websocketClient() {
    LaunchedEffect(true) {
        val client = HttpClient {
            install(WebSockets)
        }

        var isConnected = false

        while (!isConnected) {
            try {
                client.webSocket(method = HttpMethod.Get, host = "localhost", port = 8080, path = "/websocket/plugin/1") {
                    isConnected = true
                    println("Connected to server.")

                    // 发送消息到服务器
                    send("Hello, server!")

                    // 接收服务器发送的消息
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        println("Received message: $receivedText")
                    }
                }
            } catch (e: Throwable) {
                println("Connection attempt failed: ${e.message}")
                isConnected = false
                delay(8000)
            }
        }

        client.close()
    }


}
