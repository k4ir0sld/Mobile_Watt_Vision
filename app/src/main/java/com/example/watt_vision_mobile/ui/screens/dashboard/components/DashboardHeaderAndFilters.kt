package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.dashboard.DashboardFilterState
import com.example.watt_vision_mobile.ui.theme.*

@Composable
fun DashboardHeaderAndFilters(
    filters: DashboardFilterState,
    lastUpdatedText: String,
    onFilterChanged: (String, String) -> Unit,
    onRefreshClick: () -> Unit,
    onAlarmesClick: () -> Unit,
    onConfiguracoesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Linha 1: Título/Subtítulo à esquerda e botões pequenos de Alarmes e Configurações à direita
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = "Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Monitoramento Energético em Tempo Real",
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botão pequeno de Alarmes no Header
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = InputBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = onAlarmesClick, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alarmes",
                            tint = ToneYellowText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Botão pequeno de Configurações no Header
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = InputBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = onConfiguracoesClick, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Linha 2: Botão de atualização e demonstração ao vivo assumindo uma linha inteira
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = InputBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onRefreshClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar",
                    tint = LinkBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lastUpdatedText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Linha 3: Filtros organizados em 2 linhas de 2 itens cada
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdownChip(
                        label = "Período:",
                        selectedOption = filters.period,
                        options = listOf("Hoje", "Últimos 7 dias", "Últimos 30 dias"),
                        onOptionSelected = { onFilterChanged("period", it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdownChip(
                        label = "Local:",
                        selectedOption = filters.location,
                        options = listOf("Todos", "Unidade 1", "Unidade 2"),
                        onOptionSelected = { onFilterChanged("location", it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdownChip(
                        label = "Setor:",
                        selectedOption = filters.sector,
                        options = listOf("Todos", "Produção", "Manutenção"),
                        onOptionSelected = { onFilterChanged("sector", it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    FilterDropdownChip(
                        label = "Equipamento:",
                        selectedOption = filters.equipment,
                        options = listOf("Todos", "Motor", "Bomba"),
                        onOptionSelected = { onFilterChanged("equipment", it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdownChip(
    label: String,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = InputBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedOption,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(InputBackground)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (option == selectedOption) LinkBlue else TextWhite,
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
