package com.example.watt_vision_mobile.ui.screens.operacional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.operacional.EquipamentoItem
import com.example.watt_vision_mobile.ui.screens.operacional.LocalItem
import com.example.watt_vision_mobile.ui.screens.operacional.SetorItem
import com.example.watt_vision_mobile.ui.theme.*
import java.util.Locale

@Composable
fun StatusBadge(status: String) {
    val isAtivo = status.equals("Ativo", ignoreCase = true)
    val bgColor = if (isAtivo) ToneGreenBg else ToneRedBg
    val textColor = if (isAtivo) ToneGreenText else ToneRedText

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Text(
            text = if (isAtivo) "● Ativo" else "● Inativo",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EquipamentoCard(
    item: EquipamentoItem,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val potenciaStr = String.format(Locale.getDefault(), "%.2f W", item.potenciaW)
    val consumoStr = String.format(Locale.getDefault(), "%.1f kWh", item.consumoKwh)
    val desc = "Equipamento ${item.nome}, código ${item.codigo}, status ${item.status}, potência $potenciaStr, consumo $consumoStr, local ${item.localNome}, setor ${item.setorNome}" +
            if (onClick != null) ", toque para ver detalhes" else ""

    val clickableModifier = if (onClick != null) {
        Modifier
            .heightIn(min = 48.dp)
            .clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .semantics { contentDescription = desc }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Nome, Código e Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nome.ifBlank { "—" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.codigo.ifBlank { "—" },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = item.status)
            }

            // Local · Setor
            Text(
                text = "${item.localNome.ifBlank { "—" }}  ·  ${item.setorNome.ifBlank { "—" }}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LinkBlue
            )

            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 1.dp)

            // Métricas: Potência e Consumo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Potência Nominal", fontSize = 10.sp, color = TextMuted)
                    Text(text = potenciaStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Consumo Acumulado", fontSize = 10.sp, color = TextMuted)
                    Text(text = consumoStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ToneCyanText)
                }
            }
        }
    }
}

@Composable
fun SetorCard(
    item: SetorItem,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val consumoStr = String.format(Locale.getDefault(), "%.1f kWh", item.consumoAcumuladoKwh)
    val desc = "Setor ${item.nome}, código ${item.codigo}, status ${item.status}, local ${item.localNome}, equipamentos vinculados ${item.equipamentosVinculados}, consumo $consumoStr" +
            if (onClick != null) ", toque para ver detalhes" else ""

    val clickableModifier = if (onClick != null) {
        Modifier
            .heightIn(min = 48.dp)
            .clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .semantics { contentDescription = desc }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nome.ifBlank { "—" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.codigo.ifBlank { "—" },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = item.status)
            }

            Text(
                text = "Local: ${item.localNome.ifBlank { "—" }}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LinkBlue
            )

            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Equipamentos Vinculados", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${item.equipamentosVinculados} dispositivos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Consumo Acumulado", fontSize = 10.sp, color = TextMuted)
                    Text(text = consumoStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ToneCyanText)
                }
            }
        }
    }
}

@Composable
fun LocalCard(
    item: LocalItem,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val consumoStr = String.format(Locale.getDefault(), "%.1f kWh", item.consumoAcumuladoKwh)
    val desc = "Local ${item.nome}, código ${item.codigo}, status ${item.status}, setores vinculados ${item.setoresVinculados}, equipamentos ${item.equipamentosVinculados}, consumo $consumoStr" +
            if (onClick != null) ", toque para ver detalhes" else ""

    val clickableModifier = if (onClick != null) {
        Modifier
            .heightIn(min = 48.dp)
            .clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .then(clickableModifier)
            .semantics { contentDescription = desc }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nome.ifBlank { "—" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.codigo.ifBlank { "—" },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                StatusBadge(status = item.status)
            }

            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Setores Vinculados", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${item.setoresVinculados} setores", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                Column {
                    Text(text = "Equipamentos", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${item.equipamentosVinculados} unidades", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Consumo Acumulado", fontSize = 10.sp, color = TextMuted)
                    Text(text = consumoStr, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ToneCyanText)
                }
            }
        }
    }
}

@Composable
fun OperacionalSkeletonCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = 0.5f))
            .border(1.dp, CardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    )
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Equipamento Card", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewEquipamentoCard() {
    WattVisionTheme {
        EquipamentoCard(
            item = EquipamentoItem(
                id = "1",
                codigo = "EQ-001",
                nome = "Motor Elétrico WEG 15HP",
                localNome = "Planta Industrial Jandira",
                setorNome = "Produção B",
                potenciaW = 1500.0,
                consumoKwh = 124.5,
                status = "Ativo"
            )
        )
    }
}

@Preview(name = "Setor Card", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewSetorCard() {
    WattVisionTheme {
        SetorCard(
            item = SetorItem(
                id = "1",
                codigo = "SET-001",
                nome = "Produção B",
                localNome = "Planta Industrial Jandira",
                equipamentosVinculados = 8,
                consumoAcumuladoKwh = 1685.3,
                status = "Ativo"
            )
        )
    }
}

@Preview(name = "Local Card", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewLocalCard() {
    WattVisionTheme {
        LocalCard(
            item = LocalItem(
                id = "1",
                codigo = "LOC-001",
                nome = "Planta Industrial Jandira",
                setoresVinculados = 12,
                equipamentosVinculados = 45,
                consumoAcumuladoKwh = 2613.6,
                status = "Ativo"
            )
        )
    }
}
