import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension
import java.awt.Toolkit

@Composable
@Preview
fun App() {
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
    var windowHeight by remember { mutableStateOf(getScreenHeight() /2) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bullet Comment",
        state = WindowState(
            width = windowWidth.dp,
            height = windowHeight.dp,
            position = WindowPosition((getScreenWidth() / 4).dp, 80.dp)
        ),
        icon = painterResource("image/danmu.png"),
//        alwaysOnTop = true,
        transparent = true,
        undecorated = true,
    ) {

        windowWidth = this.window.size.width
        windowHeight = this.window.size.height
        WindowDraggableArea(
            Modifier
            .fillMaxSize()
            .clickable {}
        ) {
            App(15000, windowWidth, 60)
            TeamPlayer()
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
