import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @description
 * @author      jankinwu
 * @date        2024/3/16 23:14
 */
@Preview
@Composable
fun TeamPlayer() {
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
            Column(modifier = Modifier) {
//                CardLeft()
//                CardLeft()
//                CardLeft()
//                CardLeft()
            }

            Column(modifier = Modifier) {
                CardRight()
                CardRight()
                CardRight()
            }
        }
    }
}

@Composable
fun CardRight() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(60.dp)
            .width(200.dp)
            .background(Color.Gray.copy(0.5f), shape = RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)), shape = RoundedCornerShape(8.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
                    .clip(CircleShape),
                url = "https://i1.hdslb.com/bfs/face/8dcda8cc51f125f739d0defb5d6e943a66e55669.jpg",
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "友人Abandonaaaaaaa",
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Red.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 1f
                    )
                ),
            )
        }
    }
}

@Composable
fun CardLeft() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .height(60.dp)
            .width(200.dp)
            .background(Color.Gray.copy(0.3f), shape = RoundedCornerShape(8.dp))
            .border(BorderStroke(2.dp, Color.Blue.copy(alpha = 0.3f)), shape = RoundedCornerShape(8.dp)),
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "友人Abandon",
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Blue.copy(alpha = 0.8f),
                            offset = Offset(1f, 1f),
                            blurRadius = 1f
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                        .clip(CircleShape),
                    url = "https://i1.hdslb.com/bfs/face/8dcda8cc51f125f739d0defb5d6e943a66e55669.jpg",
                )
            }
            Text("")
        }
    }
}

@Composable
fun OutlinedCardExample() {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Outlined",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
}