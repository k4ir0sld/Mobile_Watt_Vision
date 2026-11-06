package com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.*
import com.example.watt_vision_mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OnlineOfflineBadge(conexaoStatus: ConexaoStatus) {
    val isOnline = conexaoStatus == ConexaoStatus.ONLINE
    val bgColor = if (isOnline) ToneGreenBg else ToneRedBg
    val textColor = if (isOnline) ToneGreenText else ToneRedText
    val icon = if (isOnline) Icons.Default.CheckCircle else Icons.Default.WifiOff
    val textLabel = if (isOnline) "Online" else "Offline"

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f)),
        modifier = Modifier.semantics {
            contentDescription = "Status de conectividade do dispositivo: $textLabel"
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = textLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun EquipamentoResumoCard(
    data: EquipamentoDetalheData,
    relativeTimeString: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OnlineOfflineBadge(conexaoStatus = data.conexaoStatus)

                    val isAtivo = data.status.equals("Ativo", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAtivo) ToneBlueBg else ToneRedBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAtivo) ToneBlueText.copy(0.4f) else ToneRedText.copy(0.4f))
                    ) {
                        Text(
                            text = if (isAtivo) "● Cadastrado: Ativo" else "● Cadastrado: Inativo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAtivo) ToneBlueText else ToneRedText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Cronômetro local isolado ("Última comunicação: há X")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Última comunicação: ",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    text = relativeTimeString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            // Se estiver Offline, mostra também a data/hora absoluta
            if (data.conexaoStatus == ConexaoStatus.OFFLINE) {
                val absTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(data.ultimaComunicacaoTimestampMs))
                Text(
                    text = "Data/Hora absoluta: $absTime",
                    fontSize = 11.sp,
                    color = ToneRedText
                )
            }
        }
    }
}

@Composable
fun AlarmeCard(
    item: AlertaDetalheItem,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label, icon) = when (item.severidade) {
        AlertaSeveridade.CRITICO -> Quadruple(ToneRedBg, ToneRedText, "Crítico", Icons.Default.Error)
        AlertaSeveridade.ALERTA -> Quadruple(ToneYellowBg, ToneYellowText, "Alerta", Icons.Default.Info)
        AlertaSeveridade.AVISO -> Quadruple(ToneBlueBg, ToneBlueText, "Aviso", Icons.Default.Info)
    }

    val semanticDesc = "Alarme ${label.lowercase()}: ${item.titulo}, condição ${item.condicao}, horário ${item.dataHoraFormatada}"

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = semanticDesc }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.titulo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = textColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = item.condicao,
                fontSize = 12.sp,
                color = TextWhite.copy(alpha = 0.9f)
            )

            Text(
                text = item.dataHoraFormatada,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AlarmesSecao(
    alarmes: List<AlertaDetalheItem>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val sortedAlarmes = remember(alarmes) {
        alarmes.sortedWith(
            compareBy<AlertaDetalheItem> {
                when (it.severidade) {
                    AlertaSeveridade.CRITICO -> 0
                    AlertaSeveridade.ALERTA -> 1
                    AlertaSeveridade.AVISO -> 2
                }
            }.thenByDescending { it.timestampMs }
        )
    }

    val visibleAlarmes = if (isExpanded) sortedAlarmes else sortedAlarmes.take(3)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alarmes (${alarmes.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                if (sortedAlarmes.size > 3) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text(
                            text = if (isExpanded) "Ver menos" else "Ver todos (${sortedAlarmes.size})",
                            fontSize = 12.sp,
                            color = LinkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (sortedAlarmes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum alarme ativo",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    visibleAlarmes.forEach { alarme ->
                        AlarmeCard(item = alarme)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoCampo(
    rotulo: String,
    valor: String?,
    modifier: Modifier = Modifier,
    copiavel: Boolean = false
) {
    val context = LocalContext.current
    val valorExibido = if (valor.isNullOrBlank()) "—" else valor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (copiavel && !valor.isNullOrBlank()) {
                    Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(rotulo, valor)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "$rotulo copiado!", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else Modifier
            )
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = rotulo, fontSize = 11.sp, color = TextMuted)
        Text(
            text = valorExibido,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextWhite
        )
    }
}

@Composable
fun InfoGrupoCard(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 1.dp)
            content()
        }
    }
}

@Composable
fun DescricaoCard(
    descricao: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val descText = if (descricao.isBlank()) "—" else descricao

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Descrição",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            HorizontalDivider(color = CardBorder.copy(alpha = 0.6f), thickness = 1.dp)

            Text(
                text = descText,
                fontSize = 13.sp,
                color = TextWhite.copy(alpha = 0.9f),
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            if (descText.length > 80) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Ver menos" else "Ver mais",
                        fontSize = 12.sp,
                        color = LinkBlue,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = LinkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
