import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import component.*
import dto.BulletCommentMsgDTO
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.awt.Dimension
import java.awt.Toolkit

@Composable
@Preview
fun BulletComment() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {

    var windowWidth by remember { mutableStateOf((getScreenWidth() / 2)) }
    var windowHeight by remember { mutableStateOf(getScreenHeight() / 2) }
//    var windowWidth by remember { mutableStateOf(1920) }
//    var windowHeight by remember { mutableStateOf(1080) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bullet Comment",
        state = WindowState(
            width = windowWidth.dp,
            height = windowHeight.dp,
            position = WindowPosition((getScreenWidth() / 4).dp, 80.dp)
        ),
        icon = painterResource("image/game.png"),
//        alwaysOnTop = true,
        transparent = true,
        undecorated = true,
    ) {
        windowWidth = this.window.size.width
        windowHeight = this.window.size.height
//        Density().density
        WindowDraggableArea(
            Modifier
                .fillMaxSize()
                .clickable {}
        ) {
            WebsocketClient()
            BulletComment(windowWidth)
            TeamPlayer()
            ChatBox()
        }
    }
}

fun getScreenWidth(): Int {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    return screenSize.width
}

fun getScreenHeight(): Int {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    return screenSize.height
}

@Composable
fun WebsocketClient() {
    var connectionAttemptState by remember { mutableStateOf(0) }
    var connectionAttemptCount by remember { mutableStateOf(0) }
    LaunchedEffect(connectionAttemptState) {
        val client = HttpClient {
            install(WebSockets)
        }

        try {
            client.webSocket(
                method = HttpMethod.Get,
                host = "localhost",
                port = 8080,
                path = "/websocket/plugin/3"
            ) {
                println("Connected to server.")
                // 清空消息队列中的失败信息
                queue.cancel()
                queueSize = 0
                queue = Channel(capacity = 100)
                disPlayState("已成功连接至弹幕-按键映射器", "image/laugh.png")
                // 发送消息到服务器
                send("Hello, server!")
                connectionAttemptCount = 0
                // 接收服务器发送的消息
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
//                    println("Received message: $receivedText")
                    handleMessage(receivedText)
                }
            }
        } catch (e: Throwable) {
            println("Connection attempt failed: ${e.message}")
        } finally {
            client.close()
            if (connectionAttemptCount == 0) {
                disPlayState("连接弹幕-按键映射器失败, 即将尝试重新连接。。。", "image/cry.png")
            } else {
                disPlayState("第${connectionAttemptCount}次重新尝试连接弹幕-按键映射器失败", "image/cry.png")
            }
            delay(15000)
            // 当服务端关闭后尝试重连
            // 通过改变 connectionAttemptCount 的值让旧的协程被取消,新的协程被启动
            connectionAttemptState++
            connectionAttemptCount++
        }
    }
}

fun disPlayState(text: String, imageUrl: String) {
    disPlayState(text, imageUrl, 0xFFFFFFFF)
}

fun disPlayState(text: String, imageUrl: String, fillColor: Long) {
    disPlayState(text, imageUrl, fillColor, 0xFF000000)
}

fun disPlayState(text: String, imageUrl: String, fillColor: Long, stroke: Long) {
    val bulletCommentMsgDTO = BulletCommentMsgDTO()
    bulletCommentMsgDTO.avatarUrl = imageUrl
    bulletCommentMsgDTO.text = text
    bulletCommentMsgDTO.fill = fillColor
    bulletCommentMsgDTO.stroke = stroke
    bulletCommentState.value = bulletCommentMsgDTO
}

fun handleMessage(receivedText: String) {
    val jsonElement = Json.parseToJsonElement(receivedText)
    val pluginItemCode = jsonElement.jsonObject["pluginItemCode"].toString().toInt()
    val data = jsonElement.jsonObject["data"].toString()

    when (pluginItemCode) {
        1 -> handleBulletMsg(data)
        2 -> handlePlayCardMsg(data)
        4 -> handleNotificationMsg(data)
        else -> Unit
    }
}

