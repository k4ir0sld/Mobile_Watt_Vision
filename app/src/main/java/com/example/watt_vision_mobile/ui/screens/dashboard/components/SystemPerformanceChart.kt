package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.dashboard.MedicaoPoint
import com.example.watt_vision_mobile.ui.theme.*

@Composable
fun SystemPerformanceChart(
    points: List<MedicaoPoint>,
    selectedMetric: String,
    selectedPeriod: String,
    onMetricSelected: (String) -> Unit,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val (metricColor, metricLabel, metricUnit) = when (selectedMetric) {
        "consumo" -> Triple(LinkBlue, "Consumo", "kWh")
        "potencia" -> Triple(ToneGreenText, "Potência", "W")
        "corrente" -> Triple(ToneYellowText, "Corrente", "A")
        else -> Triple(Color(0xFF818CF8), "Tensão", "V")
    }

    var selectedPointIndex by remember(selectedMetric, points) { mutableStateOf<Int?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabeçalho e Título
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Desempenho do Sistema",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "$metricLabel ($metricUnit) ao longo do período",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Seletor de Período (1h, 6h, 24h, 7d, 30d)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InputBackground, RoundedCornerShape(10.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("1h", "6h", "24h", "7d", "30d").forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PrimaryBlue else Color.Transparent)
                            .clickable { onPeriodSelected(period) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = period,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Seletor de Métrica (Tensão, Consumo, Potência, Corrente)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "tensao" to "Tensão",
                    "consumo" to "Consumo",
                    "potencia" to "Potência",
                    "corrente" to "Corrente"
                ).forEach { (id, label) ->
                    val isSelected = selectedMetric == id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) PrimaryBlue.copy(alpha = 0.25f) else InputBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryBlue else Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onMetricSelected(id) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) PrimaryBlue else TextMuted,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tooltip de valor selecionado no toque
            selectedPointIndex?.let { idx ->
                if (idx in points.indices) {
                    val pt = points[idx]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(InputBackground, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Horário: ${pt.time}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "${pt.valor} $metricUnit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = metricColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Área do Gráfico Interativo com Compose Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (points.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum dado disponível", fontSize = 12.sp, color = TextMuted)
                    }
                } else {
                    val minVal = points.minOf { it.valor }
                    val maxVal = points.maxOf { it.valor }
                    val valRange = if (maxVal == minVal) 1f else maxVal - minVal

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(points) {
                                detectTapGestures { tapOffset ->
                                    val width = size.width
                                    val stepX = width / (points.size - 1).coerceAtLeast(1)
                                    val closestIndex = ((tapOffset.x + stepX / 2) / stepX).toInt()
                                        .coerceIn(0, points.size - 1)
                                    selectedPointIndex = closestIndex
                                }
                            }
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height - 24.dp.toPx() // Espaço para rótulos X

                        val stepX = canvasWidth / (points.size - 1).coerceAtLeast(1)

                        // Desenhar linhas de grade horizontais
                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val y = canvasHeight * (i.toFloat() / gridLines)
                            drawLine(
                                color = CardBorder.copy(alpha = 0.5f),
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Mapear pontos para coordenadas XY
                        val offsets = points.mapIndexed { index, pt ->
                            val x = index * stepX
                            val normalizedY = (pt.valor - minVal) / valRange
                            val y = canvasHeight - (normalizedY * (canvasHeight - 20.dp.toPx()) + 10.dp.toPx())
                            Offset(x, y)
                        }

                        // Criar Path da Curva e do Preenchimento em Gradiente
                        val strokePath = Path()
                        val fillPath = Path()

                        offsets.forEachIndexed { i, offset ->
                            if (i == 0) {
                                strokePath.moveTo(offset.x, offset.y)
                                fillPath.moveTo(offset.x, canvasHeight)
                                fillPath.lineTo(offset.x, offset.y)
                            } else {
                                val prev = offsets[i - 1]
                                val controlX1 = prev.x + (offset.x - prev.x) / 2
                                val controlY1 = prev.y
                                val controlX2 = prev.x + (offset.x - prev.x) / 2
                                val controlY2 = offset.y

                                strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, offset.x, offset.y)
                                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, offset.x, offset.y)
                            }
                        }

                        fillPath.lineTo(offsets.last().x, canvasHeight)
                        fillPath.close()

                        // Desenhar preenchimento em gradiente
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    metricColor.copy(alpha = 0.35f),
                                    metricColor.copy(alpha = 0.0f)
                                ),
                                startY = 0f,
                                endY = canvasHeight
                            )
                        )

                        // Desenhar linha de contorno
                        drawPath(
                            path = strokePath,
                            color = metricColor,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Desenhar pontos e destaque para o ponto selecionado
                        offsets.forEachIndexed { i, offset ->
                            val isSelected = selectedPointIndex == i
                            drawCircle(
                                color = if (isSelected) Color.White else metricColor,
                                radius = if (isSelected) 6.dp.toPx() else 3.5.dp.toPx(),
                                center = offset
                            )
                            if (isSelected) {
                                drawCircle(
                                    color = metricColor,
                                    radius = 8.dp.toPx(),
                                    center = offset,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                    }

                    // Rótulos do eixo X (Horários)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        points.filterIndexed { index, _ -> index % 2 == 0 || index == points.lastIndex }
                            .forEach { pt ->
                                Text(
                                    text = pt.time,
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                    }
                }
            }
        }
    }
}
