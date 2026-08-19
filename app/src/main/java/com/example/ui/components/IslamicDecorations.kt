package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent

@Composable
fun IslamicDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        ) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Center Islamic 8-point star / diamond
        Canvas(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(14.dp)
        ) {
            val cx = size.width / 2
            val cy = size.height / 2
            val radius = size.width / 2

            val path1 = Path().apply {
                moveTo(cx, cy - radius)
                lineTo(cx + radius, cy)
                lineTo(cx, cy + radius)
                lineTo(cx - radius, cy)
                close()
            }
            drawPath(path1, color = GoldAccent, style = Stroke(width = 1.5.dp.toPx()))

            val half = radius * 0.7f
            val path2 = Path().apply {
                moveTo(cx - half, cy - half)
                lineTo(cx + half, cy - half)
                lineTo(cx + half, cy + half)
                lineTo(cx - half, cy + half)
                close()
            }
            drawPath(path2, color = GoldAccent.copy(alpha = 0.6f), style = Stroke(width = 1.dp.toPx()))
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        ) {
            drawLine(
                color = color,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun BismillahHeader(
    modifier: Modifier = Modifier,
    color: Color = GoldAccent
) {
    Text(
        text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = color
        ),
        modifier = modifier
    )
}
