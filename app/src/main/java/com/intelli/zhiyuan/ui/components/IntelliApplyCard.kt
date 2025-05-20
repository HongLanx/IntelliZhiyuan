package com.intelli.zhiyuan.ui.components

import androidx.compose.runtime.Composable
import com.intelli.zhiyuan.ui.viewmodel.IntelliApplyItem
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.intelli.zhiyuan.R
import com.intelli.zhiyuan.util.loadUniversityLogo

@Composable
fun IntelliApplyCard(
    item: IntelliApplyItem,
    isChinese: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    lowestSection: Int?    // 用户最低排名
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = loadUniversityLogo(item.basicInfo.uid),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (isChinese) item.basicInfo.chinese_name
                        else item.basicInfo.english_name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    val loc = if (isChinese) item.basicInfo.chinese_location
                    else item.basicInfo.english_location
                    val extra = when {
                        item.basicInfo.is985 -> "·985"
                        item.basicInfo.is211 -> "·211"
                        else -> ""
                    }
                    Text("$loc $extra", style = MaterialTheme.typography.bodyMedium)
                }

                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.ai_predict),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "${stringResource(R.string.rank_need)}:${item.minSectionPred}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Spacer(Modifier.width(48.dp))
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Yellow else Color.DarkGray
                )
            }

            // 右下角三角（±500区间）
            if (lowestSection != null) {
                val msp = item.minSectionPred
                val (bg, lbl) = when {
                    msp < lowestSection - 500 -> Color.Red    to stringResource(R.string.recommend_difficult)
                    msp > lowestSection + 500 -> Color.Green  to stringResource(R.string.recommend_easy)
                    else                       -> Color.Yellow to stringResource(R.string.recommend_normal)
                }
                Canvas(modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.BottomEnd)
                ) {
                    val w = size.width; val h = size.height
                    val path = Path().apply {
                        moveTo(w, h); lineTo(w, 0f); lineTo(0f, h); close()
                    }
                    drawPath(path, bg)
                    // 黑色描边
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 4.dp.toPx()
                            color = android.graphics.Color.BLACK
                            textSize = 32.dp.toPx()
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                        drawText(lbl, w - 8.dp.toPx(), h - 8.dp.toPx(), paint)
                        paint.style = android.graphics.Paint.Style.FILL
                        paint.color = android.graphics.Color.WHITE
                        drawText(lbl, w - 8.dp.toPx(), h - 8.dp.toPx(), paint)
                    }
                }
            }
        }
    }
}
