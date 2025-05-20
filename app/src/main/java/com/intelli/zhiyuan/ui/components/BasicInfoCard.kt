// com/intelli/zhiyuan/ui/components/ScoreQueryCard.kt
package com.intelli.zhiyuan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.intelli.zhiyuan.ui.viewmodel.ScoreDisplayItem
import com.intelli.zhiyuan.util.loadUniversityLogo

// ScoreQueryCard.kt
@Composable
fun BasicInfoCard(
    displayItem: ScoreDisplayItem,
    isChinese: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCardClick: (() -> Unit)? = null,
    isRecommendCard: Boolean = false,
    examScore: Int? = null
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
                    model = loadUniversityLogo(displayItem.basicInfo.uid),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (isChinese)
                            displayItem.basicInfo.chinese_name
                        else
                            displayItem.basicInfo.english_name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    val loc = if (isChinese)
                        displayItem.basicInfo.chinese_location
                    else
                        displayItem.basicInfo.english_location
                    val extra = when {
                        displayItem.basicInfo.is985 -> "·985"
                        displayItem.basicInfo.is211 -> "·211"
                        else -> ""
                    }
                    Text("$loc $extra", style = MaterialTheme.typography.bodyMedium)
                }
                displayItem.provinceData?.let { pd ->
                    Text(
                        text = "${pd.min}/${stringResource(R.string.rank)} ${pd.minSection}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // 非推荐界面时，把星星放在这里，垂直居中
                if (!isRecommendCard) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isFavorite) Color.Yellow else Color.DarkGray
                        )
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
            }

            // 推荐界面时，把星星移到右上角
            if (isRecommendCard) {
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
            }

            if (isRecommendCard && displayItem.provinceData != null && examScore != null) {
                val pd = displayItem.provinceData
                val (bgColor, label) = when {
                    pd.min > examScore + 4 -> Color.Red to stringResource(R.string.recommend_difficult)
                    pd.min >= examScore - 4 -> Color.Yellow to stringResource(R.string.recommend_normal)
                    else -> Color.Green to stringResource(R.string.recommend_easy)
                }
                Canvas(modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.BottomEnd)
                ) {
                    // 三角背景
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(w, h)
                        lineTo(w, 0f)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(path, bgColor)

                    // 先画黑色描边
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            style = android.graphics.Paint.Style.STROKE
                            strokeWidth = 4.dp.toPx()
                            color = android.graphics.Color.BLACK
                            textSize = 32.dp.toPx()
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                        drawText(label, w - 8.dp.toPx(), h - 8.dp.toPx(), paint)

                        // 再画白色填充
                        paint.style = android.graphics.Paint.Style.FILL
                        paint.color = android.graphics.Color.WHITE
                        drawText(label, w - 8.dp.toPx(), h - 8.dp.toPx(), paint)
                    }
                }
            }
        }
    }
}
