package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.watt_vision_mobile.ui.screens.dashboard.ComposicaoItem
import com.example.watt_vision_mobile.ui.theme.*
import java.util.Locale

@Composable
fun CalculoButtonAndModal(
    composicaoItems: List<ComposicaoItem>,
    isOpen: Boolean,
    onOpenModal: () -> Unit,
    onCloseModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Botão "Σ Ver composição"
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = InputBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.5f)),
            modifier = Modifier.clickable { onOpenModal() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Functions,
                    contentDescription = null,
                    tint = LinkBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ver composição",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = LinkBlue
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = LinkBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Modal de Composição do Cálculo
        if (isOpen) {
            Dialog(
                onDismissRequest = onCloseModal,
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = DarkBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Cabeçalho do Modal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Functions,
                                        contentDescription = null,
                                        tint = LinkBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Valor Calculado",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Sistema completo",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            IconButton(onClick = onCloseModal) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fechar",
                                    tint = TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val totalSoma = composicaoItems.sumOf { it.valor }
                        val monitorados = composicaoItems.count { it.origem != "Sem dados" }
                        val totalEntidades = composicaoItems.size
                        val coberturaCompleta = monitorados >= totalEntidades && totalEntidades > 0
                        val percentual = if (totalEntidades > 0) (monitorados * 100) / totalEntidades else 0

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 320.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Tabela de Composição
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(InputBackground)
                                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                            ) {
                                Column {
                                    // Cabeçalho da Tabela
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Slate800)
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "ENTIDADE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Text(
                                            text = "VALOR",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = "ORIGEM",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted,
                                            modifier = Modifier.weight(1f),
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    // Linhas dos itens
                                    composicaoItems.forEach { item ->
                                        val semDados = item.origem == "Sem dados"
                                        HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.entidade,
                                                fontSize = 12.sp,
                                                color = TextWhite,
                                                modifier = Modifier.weight(1.5f)
                                            )
                                            Text(
                                                text = if (semDados) "0 kWh" else String.format(Locale.getDefault(), "%.1f kWh", item.valor),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (semDados) TextMuted else TextWhite,
                                                modifier = Modifier.weight(1f),
                                                textAlign = TextAlign.End
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .wrapContentWidth(Alignment.End)
                                                    .clip(CircleShape)
                                                    .background(if (semDados) InputBackground else PrimaryBlue.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = item.origem,
                                                    fontSize = 9.sp,
                                                    color = if (semDados) TextMuted else LinkBlue
                                                )
                                            }
                                        }
                                    }

                                    // Soma Total
                                    HorizontalDivider(color = CardBorder, thickness = 1.dp)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CardBackground)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = String.format(Locale.getDefault(), "Soma total: %.1f kWh", totalSoma),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite,
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Cobertura Status
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (coberturaCompleta) ToneGreenBg else ToneYellowBg)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (coberturaCompleta) "Cobertura completa · $monitorados/$totalEntidades · $percentual%" else "Cobertura parcial · $monitorados/$totalEntidades · $percentual%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (coberturaCompleta) ToneGreenText else ToneYellowText
                                )
                            }

                            if (!coberturaCompleta) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(ToneYellowBg.copy(alpha = 0.4f))
                                        .border(1.dp, ToneYellowText.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = ToneYellowText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Algumas entidades não possuem medição ativa. O total reflete apenas os pontos monitorados.",
                                        fontSize = 10.sp,
                                        color = ToneYellowText
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onCloseModal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Fechar", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
