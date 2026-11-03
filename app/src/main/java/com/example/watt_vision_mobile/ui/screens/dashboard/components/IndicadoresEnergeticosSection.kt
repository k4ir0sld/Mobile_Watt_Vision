package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.dashboard.EnergeticMetricData
import com.example.watt_vision_mobile.ui.theme.*

@Composable
fun IndicadoresEnergeticosSection(
    indicadores: List<EnergeticMetricData>,
    modifier: Modifier = Modifier,
    periodText: String = "24 h"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Indicadores Energéticos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Text(
                    text = "Período: $periodText",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Organização em grid mobile 2 colunas
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                indicadores.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                IndicadorMetricCard(metric = item)
                            }
                        }
                        if (rowItems.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IndicadorMetricCard(metric: EnergeticMetricData) {
    val (iconVector, iconColor) = when (metric.iconName) {
        "activity" -> Icons.Default.Speed to ToneGreenText
        "trending_up" -> Icons.AutoMirrored.Filled.TrendingUp to ToneYellowText
        "clock" -> Icons.Default.Schedule to ToneCyanText
        "dollar" -> Icons.Default.AttachMoney to ToneGreenText
        "percent" -> Icons.Default.Percent to ToneGreenText
        else -> Icons.Default.Bolt to LinkBlue
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(InputBackground)
            .padding(12.dp)
    ) {
        Column {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = metric.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = metric.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = metric.detail,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
