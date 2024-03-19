import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import dto.TeamDTO
import dto.TeamPlayerDTO
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @description
 * @author      jankinwu
 * @date        2024/3/16 23:14
 */

const val redTeam = "萨拉森人"
const val blueTeam = "十字军"
val blueStateMap = mutableStateMapOf<Int, TeamPlayerDTO>()
val redStateMap = mutableStateMapOf<Int, TeamPlayerDTO>()

@Preview
@Composable
fun TeamPlayer() {
    teamPlayerWebsocketClient()
    val teamPlayerDTO = TeamPlayerDTO()
    teamPlayerDTO.gold = BigDecimal(1432.54)
    teamPlayerDTO.uname = "友人Abandon友人Abandon"
    teamPlayerDTO.avatarUrl = "https://i1.hdslb.com/bfs/face/8dcda8cc51f125f739d0defb5d6e943a66e55669.jpg"
    blueStateMap[1] = teamPlayerDTO
    redStateMap[1] = teamPlayerDTO
    Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(modifier = Modifier) {
                val map = blueStateMap
                // 遍历map中的键值对，创建对应的组件
                map.forEach { (_, value) ->
                    item { PlayerCard(data = value, isLeftSide = true)}
                }
            }

            LazyColumn(modifier = Modifier) {
                val map = redStateMap
                // 遍历map中的键值对，创建对应的组件
                map.forEach { (_, value) ->
                    item { PlayerCard(data = value, isLeftSide = false) }
                }
            }
        }
    }
}

@Composable
fun PlayerCard(data: TeamPlayerDTO, isLeftSide: Boolean) {
    val componentState = remember { mutableStateOf(data) }
    componentState.value.gold = data.gold?.setScale(2, RoundingMode.HALF_UP)

    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(80.dp)
            .width(200.dp)
            .background(Color.Gray.copy(0.3f), shape = RoundedCornerShape(8.dp))
            .border(
                BorderStroke(2.dp, if (isLeftSide) Color.Blue.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp)
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (isLeftSide) Alignment.End else Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isLeftSide) Arrangement.End else Arrangement.Start,
            ) {
                if (isLeftSide) {
                    componentState.value.uname?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Blue.copy(alpha = 0.8f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 1f
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .clip(CircleShape),
                    url = componentState.value.avatarUrl,
                )
                if (!isLeftSide) {
                    Spacer(modifier = Modifier.height(16.dp))
                    componentState.value.uname?.let {
                        Text(
                            text = it,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Red.copy(alpha = 0.8f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 1f
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            HorizontalLine(if (isLeftSide) Color.Blue.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f))

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    componentState.value.gold.toString() + "g",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Blue.copy(alpha = 0.8f),
                            offset = Offset(1f, 1f),
                            blurRadius = 1f
                        )
                    ),
                )
            }
        }
    }
}

@Composable
fun HorizontalLine(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .height(2.dp)
            .background(color)
    )
}

@Composable
fun teamPlayerWebsocketClient() {
    LaunchedEffect(true) {
        val client = HttpClient {
            install(WebSockets)
        }

        var isConnected = false

        while (!isConnected) {
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = "localhost",
                    port = 8080,
                    path = "/websocket/plugin/2"
                ) {
                    isConnected = true
                    println("Connected to server.")

                    // 发送消息到服务器
                    send("Hello, server!")

                    // 接收服务器发送的消息
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        println("Received message: $receivedText")
                        handleWebSocketMessage(receivedText)
                    }
                }
                client.close()
            } catch (e: Throwable) {
                println("Connection attempt failed: ${e.message}")
                isConnected = false
                delay(8000)
            }
        }

    }
}

fun handleWebSocketMessage(receivedText: String) {
    val jsonArray: JSONArray = JSON.parseArray(receivedText)

    for (i in 0 until jsonArray.size) {
        val teamDTO: TeamDTO = JSON.parseObject(jsonArray.getJSONObject(i).toJSONString(), TeamDTO::class.java)
        val team = teamDTO.name
        val playerList = teamDTO.players

        val (teamMap, _) = when (team) {
            blueTeam -> Pair(blueStateMap, redStateMap)
            redTeam -> Pair(redStateMap, blueStateMap)
            else -> continue
        }

        processMapChanges(teamMap, playerList, team)
    }
}

fun processMapChanges(teamMap: MutableMap<Int, TeamPlayerDTO>, playerList: List<TeamPlayerDTO>?, team: String) {
    val uids = playerList?.map { it.uid }?.toSet() ?: emptySet()

    // Remove players not in the current list
    teamMap.keys.removeAll { it !in uids }

    // Add or update players in the current list
    playerList?.forEach { teamPlayerDTO ->
        teamPlayerDTO.uid?.let {
            teamMap[it] = teamPlayerDTO
        }
    }

    // Update component data
    teamMap.forEach { (id, playerDTO) ->
        playerDTO.gold?.let { updateComponentData(id, it, team) }
    }
}

fun updateComponentData(id: Int, newGold: BigDecimal, teamName: String) {
    val componentState = if (teamName == redTeam) redStateMap[id] else blueStateMap[id]
    componentState?.let {
        it.gold = newGold.setScale(2, RoundingMode.HALF_UP)
    }
}