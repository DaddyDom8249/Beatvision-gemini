package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Stage
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinematicCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTertiary

@Composable
fun StageProgressBar(
    currentStage: Stage,
    onStageClick: (Stage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(CinematicCard, RoundedCornerShape(16.dp))
            .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("stage_progress_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val stages = Stage.values()
            stages.forEachIndexed { index, stage ->
                val isCurrent = stage == currentStage
                val isPassed = stage.index < currentStage.index
                val isClickable = stage.index <= currentStage.index

                val dotColor by animateColorAsState(
                    targetValue = when {
                        isCurrent -> ElectricCyan
                        isPassed -> ElectricCyan.copy(alpha = 0.7f)
                        else -> TextTertiary.copy(alpha = 0.4f)
                    },
                    label = "dotColor"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = isClickable) { onStageClick(stage) }
                        .padding(horizontal = 2.dp)
                        .testTag("stage_step_${stage.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) ElectricCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = dotColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPassed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = dotColor,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(if (isCurrent) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }

                    Text(
                        text = stage.displayName,
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) ElectricCyan else if (isPassed) TextPrimary else TextTertiary,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (index < stages.size - 1) {
                    val lineColor = if (stage.index < currentStage.index) {
                        ElectricCyan.copy(alpha = 0.5f)
                    } else {
                        BorderSubtle
                    }
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(0.4f)
                            .background(lineColor)
                            .align(Alignment.CenterVertically)
                            .padding(bottom = 14.dp)
                    )
                }
            }
        }
    }
}
