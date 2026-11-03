package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.dashboard.StatCardData
import com.example.watt_vision_mobile.ui.screens.dashboard.StatTone
import com.example.watt_vision_mobile.ui.theme.*

@Composable
fun StatCardsSection(
    cards: List<StatCardData>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Exibe os cards em duplas (2 por linha) no layout mobile
        cards.chunked(2).forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowCards.forEach { cardData ->
                    Box(modifier = Modifier.weight(1f)) {
                        StatCardItem(card = cardData)
                    }
                }
                if (rowCards.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun StatCardItem(card: StatCardData) {
    val (iconBgBrush, _, badgeBg, badgeText) = when (card.tone) {
        StatTone.GREEN -> Quadruple(
            Brush.linearGradient(listOf(Color(0xFF267447), Color(0xFF12372D))),
            ToneGreenText,
            ToneGreenBg,
            ToneGreenText
        )
        StatTone.RED -> Quadruple(
            Brush.linearGradient(listOf(Color(0xFF632632), Color(0xFF2A1822))),
            ToneRedText,
            ToneRedBg,
            ToneRedText
        )
        StatTone.YELLOW -> Quadruple(
            Brush.linearGradient(listOf(Color(0xFFA99A76), Color(0xFF51462F))),
            ToneYellowText,
            ToneYellowBg,
            ToneYellowText
        )
        StatTone.BLUE -> Quadruple(
            Brush.linearGradient(listOf(Color(0xFF315FAE), Color(0xFF17315F))),
            ToneBlueText,
            ToneBlueBg,
            ToneBlueText
        )
    }

    val iconVector: ImageVector = when (card.iconName) {
        "check_circle" -> Icons.Default.CheckCircle
        "wifi_off" -> Icons.Default.WifiOff
        "zap" -> Icons.Default.Bolt
        "flash" -> Icons.Default.FlashOn
        "activity" -> Icons.Default.Speed
        else -> Icons.Default.Memory
    }

    // Container principal do card com sombra, cantos arredondados, fundo e borda de 1px (#1B2A40)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(138.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.32f),
                ambientColor = Color.Black.copy(alpha = 0.32f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C141F))
            .border(1.dp, Color(0xFF1B2A40), RoundedCornerShape(16.dp))
    ) {
        // Camada 1: Gradiente azul interno superior (rgba(36,73,130,0.13) -> rgba(15,27,44,0.05) -> transparent)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF244982).copy(alpha = 0.13f),
                            Color(0xFF0F1B2C).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 140f
                    )
                )
        )

        // Camada 2: Reflexo azul difuso no canto superior esquerdo (#2856A0 com 7-14% opacidade)
        Box(
            modifier = Modifier
                .size(130.dp, 90.dp)
                .offset(x = (-32).dp, y = (-48).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2856A0).copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Camada 3: Linha de brilho inferior com gradiente horizontal (#31558A com 30% opacidade no centro)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF31558A).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Camada 4: Conteúdo do Card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone com gradiente
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBgBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Badge de variação
                card.change?.let { changeText ->
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeBg)
                            .border(0.5.dp, badgeText.copy(alpha = 0.4f), CircleShape)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = changeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = badgeText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Valor do indicador
            Text(
                text = card.value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Rótulo principal
            Text(
                text = card.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSubtle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Detalhe secundário no rodapé
            Text(
                text = card.detail,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
