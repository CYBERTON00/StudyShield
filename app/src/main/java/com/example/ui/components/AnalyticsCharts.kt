package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeeklyUsageChart(
    focusMinutes: List<Int>, // 7 values for Mon to Sun
    screenMinutes: List<Int>, // 7 values for Mon to Sun Comparison
    daysOfWeek: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(com.example.ui.theme.BentoSurface, RoundedCornerShape(24.dp))
            .border(androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoBorder), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Focus vs Screen Time (m)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(com.example.ui.theme.BentoEmerald, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Study", fontSize = 10.sp, color = Color(0xFF71717A))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFEF4444), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Screen", fontSize = 10.sp, color = Color(0xFF71717A))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Draw double comparative bar chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val width = size.width
            val height = size.height

            val gridLines = 4
            val gridSpacingY = height / (gridLines + 1)
            val maxVal = (focusMinutes.maxOrNull() ?: 60).coerceAtLeast(screenMinutes.maxOrNull() ?: 60).coerceAtLeast(60).toFloat()

            // Draw clean background horizontal divider lines
            for (i in 0..gridLines) {
                val y = gridSpacingY * (i + 1)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.15f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 2f
                )
            }

            val itemsCount = focusMinutes.size
            if (itemsCount <= 0) {
                return@Canvas
            }
            val sectionWidth = width / itemsCount
            val barGroupWidth = sectionWidth * 0.7f
            val singleBarWidth = barGroupWidth * 0.40f

            for (i in 0 until itemsCount) {
                val centerSecX = sectionWidth * i + sectionWidth / 2f
                
                // Focus hours bar (Primary green)
                val focusHeight = ((focusMinutes[i] / maxVal) * (height - 20f)).coerceAtLeast(0f)
                val focusX = centerSecX - singleBarWidth - 2f
                val focusY = height - focusHeight

                drawRoundRect(
                    color = Color(0xFF10B981), // Emerald study color
                    topLeft = Offset(focusX, focusY),
                    size = Size(singleBarWidth.coerceAtLeast(0f), focusHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Screen time bar (Distraction error color)
                val screenHeight = ((screenMinutes[i] / maxVal) * (height - 20f)).coerceAtLeast(0f)
                val screenX = centerSecX + 2f
                val screenY = height - screenHeight

                drawRoundRect(
                    color = Color(0xFFEF4444), // Crimson screen limit color
                    topLeft = Offset(screenX, screenY),
                    size = Size(singleBarWidth.coerceAtLeast(0f), screenHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            for (day in daysOfWeek) {
                Text(
                    text = day,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.width(36.dp),
                    maxLines = 1,
                    onTextLayout = {}
                )
            }
        }
    }
}

@Composable
fun CategoryDistributionChart(
    categories: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(com.example.ui.theme.BentoSurface, RoundedCornerShape(24.dp))
            .border(androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BentoBorder), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Focus Distribution",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val totalMinutes = categories.sumOf { it.second }.coerceAtLeast(1)
        val colorsPalette = listOf(
            Color(0xFF3B82F6), // Blue
            Color(0xFF10B981), // Emerald
            Color(0xFFEC4899), // Pink
            Color(0xFF8B5CF6), // Purple
            Color(0xFFF59E0B)  // Orange
        )

        categories.take(5).forEachIndexed { index, pair ->
            val percentage = (pair.second.toFloat() / totalMinutes * 100).toInt()
            val color = colorsPalette[index % colorsPalette.size]

            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${pair.first} (${pair.second}m)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Progress line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color.LightGray.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (pair.second.toFloat() / totalMinutes).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}
