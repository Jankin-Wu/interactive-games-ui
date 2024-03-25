import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
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
import com.lt.load_the_image.rememberImagePainter
import dto.TeamDTO
import dto.TeamPlayerDTO
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

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
    val teamPlayerDTO = TeamPlayerDTO()
    teamPlayerDTO.gold = BigDecimal(1432.54)
    teamPlayerDTO.uname = "友人Abandon友人Abandon"
    teamPlayerDTO.avatarUrl = "https://i1.hdslb.com/bfs/face/8dcda8cc51f125f739d0defb5d6e943a66e55669.jpg"
//    blueStateMap[1] = teamPlayerDTO
//    redStateMap[1] = teamPlayerDTO
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
                    item { PlayerCard(data = value, isLeftSide = true) }
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
    componentState.value.refreshIntervalMs = data.refreshIntervalMs
    componentState.value.refreshFlag = data.refreshFlag

    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(80.dp)
            .width(200.dp)
            .background(Color.Gray.copy(0.5f), shape = RoundedCornerShape(8.dp))
            .border(
                BorderStroke(2.dp, if (isLeftSide) Color.Blue.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f)),
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
//                    Spacer(modifier = Modifier.height(8.dp))
                }
                Image(
                    painter = rememberImagePainter(componentState.value.avatarUrl),
                    contentDescription = "",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .clip(CircleShape),
                )
//                AsyncImage(
//                    modifier = Modifier
//                        .size(50.dp)
//                        .padding(8.dp)
//                        .clip(CircleShape),
//                    url = componentState.value.avatarUrl,
//                )
                if (!isLeftSide) {
//                    Spacer(modifier = Modifier.height(8.dp))
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
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .padding(start = 20.dp, end = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress by animateFloatAsState(
                        targetValue = (componentState.value.gold?.remainder(BigDecimal.ONE)?.toFloat() ?: 0f)
                    )
                    componentState.value.gold?.remainder(BigDecimal.ONE)?.let {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize(),
                            progress = progress,
                            color = if (isLeftSide) Color.Blue.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f),
                            backgroundColor = Color.Transparent
                        )
                    }

                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    "可用出兵点数：" + componentState.value.gold?.toBigInteger().toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    style = TextStyle(
                        shadow = Shadow(
                            color = if (isLeftSide) Color.Blue.copy(alpha = 0.8f) else Color.Red.copy(alpha = 0.8f),
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

@Preview
@Composable
fun progress(color: Color, refreshIntervalMs: Long, refreshFlag: Long?, gold: BigDecimal) {
    var progress by remember { mutableStateOf(0f) }
    var goldState by remember { mutableStateOf(gold) }
    val refreshIntervalMsState by remember { mutableStateOf(refreshIntervalMs) }
    var refreshFlagState by remember { mutableStateOf(refreshFlag) }
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Box(
            modifier = Modifier
                .height(2.dp)
                .padding(start = 20.dp, end = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(),
                progress = progress,
                color = color,
                backgroundColor = Color.Transparent
            )
            LaunchedEffect(goldState, refreshIntervalMsState) {
                while (isActive) {
                    goldState = gold
                    progress = goldState.remainder(BigDecimal.ONE).toFloat()
                    delay(refreshIntervalMsState)
                }
            }


        }
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
//        playerDTO.gold?.let { updateComponentData(id, it, team) }
        updateComponentData(id, playerDTO, team)
    }
}

fun updateComponentData(id: Int, playerDTO: TeamPlayerDTO, teamName: String) {
    val componentState = if (teamName == redTeam) redStateMap[id] else blueStateMap[id]
    componentState?.let {
        it.gold = playerDTO.gold?.setScale(2, RoundingMode.HALF_UP)
        it.refreshIntervalMs = playerDTO.refreshIntervalMs
        it.refreshFlag = playerDTO.refreshFlag
    }
}

fun handlePlayCardMsg(data: String) {
    val jsonArray: JSONArray = JSON.parseArray(data)

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