package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.math.cos

@Composable
fun VirtualPlantView(
    plantType: String, // "Bonsai", "Sunflower", "Succulent"
    plantLevel: Float, // 0.0 to 1.0 (growth stage)
    plantHealth: Float, // 0.0 to 1.0 (color vibrancy)
    modifier: Modifier = Modifier
) {
    // Elegant swaying/wind animation to make the plant alive!
    val infiniteTransition = rememberInfiniteTransition(label = "wind_sway")
    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val safePlantLevel = if (plantLevel.isNaN() || plantLevel.isInfinite()) 0.5f else plantLevel.coerceIn(0f, 1f)
        val safePlantHealth = if (plantHealth.isNaN() || plantHealth.isInfinite()) 0.5f else plantHealth.coerceIn(0f, 1f)

        val width = size.width
        val height = size.height
        if (width <= 0f || height <= 0f) {
            return@Canvas
        }

        val centerX = width / 2f
        val bottomY = height - 30f

        // 1. Draw the Pot (Modern elegant terracotta clay pot)
        val potWidth = 90f
        val potHeight = 60f
        val potColor = Color(0xFFD97706) // Terracotta base
        val potHighlight = Color(0xFFF59E0B)

        // Pot soil/top ridge
        drawRoundRect(
            color = Color(0xFF78350F), // Rich dark soil
            topLeft = Offset(centerX - potWidth - 10f, bottomY - potHeight - 5f),
            size = Size((potWidth + 10f) * 2, 10f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Main Pot body with slight shadow
        val potPath = Path().apply {
            moveTo(centerX - potWidth, bottomY - potHeight)
            lineTo(centerX + potWidth, bottomY - potHeight)
            lineTo(centerX + potWidth * 0.7f, bottomY)
            lineTo(centerX - potWidth * 0.7f, bottomY)
            close()
        }
        drawPath(path = potPath, color = potColor)
        
        // Draw pot bottom highlight
        drawRoundRect(
            color = potHighlight,
            topLeft = Offset(centerX - potWidth * 0.65f, bottomY - 12f),
            size = Size((potWidth * 0.65f) * 2, 8f),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Define plant colors adjusted dynamically by health
        // Healthy plant is emerald/mint, dry plant is brown/grey
        val basePlantGreen = Color(0xFF10B981)
        val healthyColor = when (plantType) {
            "Bonsai" -> Color(0xFF047857)
            "Sunflower" -> Color(0xFF059669)
            else -> Color(0xFF14B8A6) // Succulent teal
        }
        val unhealthyColor = Color(0xFF8B7355) // Dry wood/withered leaf
        
        // Linear Interpolation for health color (safely coerced)
        val leafRed = (unhealthyColor.red + (healthyColor.red - unhealthyColor.red) * safePlantHealth).coerceIn(0f, 1f)
        val leafGreen = (unhealthyColor.green + (healthyColor.green - unhealthyColor.green) * safePlantHealth).coerceIn(0f, 1f)
        val leafBlue = (unhealthyColor.blue + (healthyColor.blue - unhealthyColor.blue) * safePlantHealth).coerceIn(0f, 1f)
        val leafColor = Color(
            red = leafRed,
            green = leafGreen,
            blue = leafBlue,
            alpha = 1.0f
        )

        val stemRed = (0.45f + (0.1f - 0.45f) * safePlantHealth).coerceIn(0f, 1f)
        val stemGreen = (0.35f + (0.6f - 0.35f) * safePlantHealth).coerceIn(0f, 1f)
        val stemBlue = (0.25f + (0.28f - 0.25f) * safePlantHealth).coerceIn(0f, 1f)
        val stemColor = Color(
            red = stemRed,
            green = stemGreen,
            blue = stemBlue,
            alpha = 1.0f
        )

        // Scale max plant height by growth level (min height is 40dp sprout to max 180dp full plant)
        val maxAvailableHeight = (height - potHeight - 60f).coerceAtLeast(0f)
        val currentPlantHeight = (40f + (maxAvailableHeight - 40f) * safePlantLevel).coerceAtLeast(10f)

        // Dynamic sway offset at the top of the plant
        val swayOffset = swayAngle * (currentPlantHeight / 80f)

        // 2. Render Based on Plant Type
        when (plantType) {
            "Bonsai" -> {
                // Trunk coordinates
                val trunkStart = Offset(centerX, bottomY - potHeight)
                val trunkEnd = Offset(centerX + swayOffset, bottomY - potHeight - currentPlantHeight)
                
                // Draw majestic winding trunk path
                val trunkPath = Path().apply {
                    moveTo(centerX - 12f, bottomY - potHeight)
                    cubicTo(
                        centerX - 20f, bottomY - potHeight - currentPlantHeight * 0.4f,
                        centerX + 25f + swayOffset * 0.5f, bottomY - potHeight - currentPlantHeight * 0.7f,
                        centerX + swayOffset, bottomY - potHeight - currentPlantHeight
                    )
                    lineTo(centerX + 4f + swayOffset, bottomY - potHeight - currentPlantHeight)
                    cubicTo(
                        centerX + 33f + swayOffset * 0.5f, bottomY - potHeight - currentPlantHeight * 0.7f,
                        centerX - 10f, bottomY - potHeight - currentPlantHeight * 0.4f,
                        centerX + 12f, bottomY - potHeight
                    )
                    close()
                }
                drawPath(path = trunkPath, color = stemColor)

                // Grow leaf clusters depending on Level
                if (safePlantLevel >= 0.15f) {
                    // Left branch cluster
                    val leftBranchX = centerX - currentPlantHeight * 0.25f + swayOffset * 0.5f
                    val leftBranchY = bottomY - potHeight - currentPlantHeight * 0.5f
                    drawOval(
                        color = leafColor,
                        topLeft = Offset(leftBranchX - 25f, leftBranchY - 15f),
                        size = Size(50f, 30f)
                    )
                    drawOval(
                        color = leafColor.copy(alpha = 0.7f),
                        topLeft = Offset(leftBranchX - 15f, leftBranchY - 22f),
                        size = Size(30f, 20f)
                    )
                }

                if (safePlantLevel >= 0.5f) {
                    // Right branch cluster
                    val rightBranchX = centerX + currentPlantHeight * 0.3f + swayOffset * 0.7f
                    val rightBranchY = bottomY - potHeight - currentPlantHeight * 0.75f
                    drawOval(
                        color = leafColor,
                        topLeft = Offset(rightBranchX - 30f, rightBranchY - 20f),
                        size = Size(60f, 35f)
                    )
                    drawOval(
                        color = leafColor.copy(alpha = 0.8f),
                        topLeft = Offset(rightBranchX - 20f, rightBranchY - 28f),
                        size = Size(40f, 25f)
                    )
                }

                if (safePlantLevel >= 0.8f) {
                    // Top crown majestic branch cluster (Bloomed!)
                    val topX = centerX + swayOffset
                    val topY = bottomY - potHeight - currentPlantHeight
                    drawOval(
                        color = leafColor,
                        topLeft = Offset(topX - 45f, topY - 25f),
                        size = Size(90f, 50f)
                    )
                    // Flower buds (glowing gold spots if hyper healthy!)
                    val goldColor = Color(0xFFFBBF24)
                    if (safePlantHealth >= 0.7f) {
                        drawCircle(color = goldColor, center = Offset(topX - 15f, topY - 15f), radius = 6f)
                        drawCircle(color = goldColor, center = Offset(topX + 20f, topY - 5f), radius = 5f)
                        drawCircle(color = goldColor, center = Offset(topX + 3f, topY - 22f), radius = 4f)
                    }
                }
            }
            "Sunflower" -> {
                // Tall straight central stem
                val stemPath = Path().apply {
                    moveTo(centerX - 6f, bottomY - potHeight)
                    lineTo(centerX - 4f + swayOffset, bottomY - potHeight - currentPlantHeight)
                    lineTo(centerX + 4f + swayOffset, bottomY - potHeight - currentPlantHeight)
                    lineTo(centerX + 6f, bottomY - potHeight)
                    close()
                }
                drawPath(path = stemPath, color = Color(0xFF10B981))

                // Left & Right Stem Leaves
                if (safePlantLevel >= 0.3f) {
                    val leafLeftY = bottomY - potHeight - currentPlantHeight * 0.4f
                    val leafLeftX = centerX - 5f + swayOffset * 0.4f
                    val pathLeaf = Path().apply {
                        moveTo(leafLeftX, leafLeftY)
                        quadraticTo(leafLeftX - 35f, leafLeftY - 15f, leafLeftX - 45f, leafLeftY)
                        quadraticTo(leafLeftX - 25f, leafLeftY + 15f, leafLeftX, leafLeftY)
                    }
                    drawPath(path = pathLeaf, color = leafColor)
                }

                if (safePlantLevel >= 0.55f) {
                    val leafRightY = bottomY - potHeight - currentPlantHeight * 0.65f
                    val leafRightX = centerX + 5f + swayOffset * 0.65f
                    val pathLeaf = Path().apply {
                        moveTo(leafRightX, leafRightY)
                        quadraticTo(leafRightX + 35f, leafRightY - 10f, leafRightX + 45f, leafRightY - 5f)
                        quadraticTo(leafRightX + 25f, leafRightY + 20f, leafRightX, leafRightY)
                    }
                    drawPath(path = pathLeaf, color = leafColor)
                }

                // Giant Golden Blooming flower crown!
                if (safePlantLevel >= 0.75f) {
                    val flowerX = centerX + swayOffset
                    val flowerY = bottomY - potHeight - currentPlantHeight
                    val petalRadius = 24f + (16f * safePlantLevel)
                    
                    // Draw 8 glowing petals
                    val petalGold = Color(0xFFFBBF24)
                    val currentPetalRed = (unhealthyColor.red + (petalGold.red - unhealthyColor.red) * safePlantHealth).coerceIn(0f, 1f)
                    val currentPetalGreen = (unhealthyColor.green + (petalGold.green - unhealthyColor.green) * safePlantHealth).coerceIn(0f, 1f)
                    val currentPetalBlue = (unhealthyColor.blue + (petalGold.blue - unhealthyColor.blue) * safePlantHealth).coerceIn(0f, 1f)
                    val currentPetalColor = Color(
                        red = currentPetalRed,
                        green = currentPetalGreen,
                        blue = currentPetalBlue,
                        alpha = 1.0f
                    )

                    for (i in 0 until 8) {
                        val angle = (i * Math.PI / 4).toFloat()
                        val petalOffset = Offset(
                            flowerX + (petalRadius * 0.6f) * sin(angle),
                            flowerY + (petalRadius * 0.6f) * cos(angle)
                        )
                        drawCircle(
                            color = currentPetalColor,
                            radius = 15f,
                            center = petalOffset
                        )
                    }

                    // Rich brown flower center
                    drawCircle(
                        color = Color(0xFF451A03), // Dark sunflower seed core
                        radius = 18f,
                        center = Offset(flowerX, flowerY)
                    )
                    // Yellow specks
                    if (safePlantHealth >= 0.8f) {
                        drawCircle(color = petalGold, center = Offset(flowerX - 5f, flowerY - 5f), radius = 2.5f)
                        drawCircle(color = petalGold, center = Offset(flowerX + 5f, flowerY + 4f), radius = 2.5f)
                        drawCircle(color = petalGold, center = Offset(flowerX + 4f, flowerY - 6f), radius = 2f)
                    }
                }
            }
            else -> { // Succulent ("Desert Succulent")
                // Grow layers of round, fleshy teal succulent leaves
                val centerSucculent = Offset(centerX + swayOffset * 0.3f, bottomY - potHeight - 12f)
                val baseSize = 30f + 50f * safePlantLevel

                // Succulent grew concentric nodes
                val layersCount = when {
                    safePlantLevel >= 0.8f -> 4
                    safePlantLevel >= 0.5f -> 3
                    safePlantLevel >= 0.25f -> 2
                    else -> 1
                }

                for (layer in layersCount downTo 1) {
                    val layerRadius = baseSize * (layer / layersCount.toFloat())
                    val leavesInLayer = 3 + layer * 2
                    
                    for (i in 0 until leavesInLayer) {
                        val angle = (i * 2 * Math.PI / leavesInLayer) + (layer * 0.5)
                        val offset = Offset(
                            centerSucculent.x + (layerRadius * 0.5f * sin(angle)).toFloat(),
                            centerSucculent.y + (layerRadius * 0.4f * cos(angle)).toFloat()
                        )
                        
                        // Draw fleshy succulent leaf
                        drawCircle(
                            color = leafColor.copy(alpha = 1f - (layer * 0.1f)),
                            radius = 12f + (8f * safePlantLevel),
                            center = offset
                        )
                        // Fleshy outline
                        drawCircle(
                            color = leafColor.copy(alpha = 0.5f),
                            radius = 12f + (8f * safePlantLevel),
                            center = offset,
                            style = Stroke(width = 2f)
                        )
                    }
                }

                // Crown center heart
                drawCircle(
                    color = leafColor,
                    radius = 8f + (4f * safePlantLevel),
                    center = centerSucculent
                )
            }
        }
    }
}
