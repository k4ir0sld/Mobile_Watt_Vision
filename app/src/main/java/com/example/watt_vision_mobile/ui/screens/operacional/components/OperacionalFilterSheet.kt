package com.example.watt_vision_mobile.ui.screens.operacional.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.components.filtros.FiltroItemModel
import com.example.watt_vision_mobile.ui.components.filtros.FiltroLinhaSeletor
import com.example.watt_vision_mobile.ui.components.filtros.FiltroSeletorBusca
import com.example.watt_vision_mobile.ui.screens.operacional.TabFilterState
import com.example.watt_vision_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperacionalFilterBar(
    tabIndex: Int, // 0 = Equipamentos, 1 = Setores, 2 = Locais
    filters: TabFilterState,
    onQueryChanged: (String) -> Unit,
    onStatusChanged: (String) -> Unit,
    onApplyFilters: (TabFilterState) -> Unit,
    onRemoveFilter: (String) -> Unit,
    locaisList: List<String>,
    setoresList: List<Pair<String, String>>, // Pair(setorNome, localNome)
    modifier: Modifier = Modifier
) {
    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    var draftFilters by remember { mutableStateOf(filters) }
    var activeSelectorType by remember { mutableStateOf<OperacionalSelectorType?>(null) }

    LaunchedEffect(filters) {
        draftFilters = filters
    }

    val searchPlaceholder = when (tabIndex) {
        0 -> "Pesquisar equipamento ou código..."
        1 -> "Pesquisar setor ou código..."
        else -> "Pesquisar local ou código..."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search TextField Sticky (min 48dp height)
        OutlinedTextField(
            value = filters.searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text(searchPlaceholder, color = TextMuted, fontSize = 13.sp) },
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
            },
            trailingIcon = {
                if (filters.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }, modifier = Modifier.size(48.dp)) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar busca", tint = TextMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .semantics { contentDescription = "Campo de busca para $searchPlaceholder" }
        )

        // Chips de Status (Todos / Ativo / Inativo) + Botão "Filtros" (para abas 0 e 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chips de Status (Visíveis em todas as abas)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Todos", "Ativo", "Inativo").forEach { statusOpt ->
                    val isSelected = filters.statusFilter.equals(statusOpt, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusChanged(statusOpt) },
                        label = { Text(statusOpt, fontSize = 11.sp) },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .semantics { contentDescription = "Filtro de status $statusOpt" },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = InputBackground,
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.25f),
                            labelColor = TextMuted,
                            selectedLabelColor = PrimaryBlue
                        )
                    )
                }
            }

            // Botão "Filtros Avançados" (exibido nas abas 0 - Equipamentos e 1 - Setores)
            if (tabIndex < 2) {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            draftFilters = filters
                            isSheetOpen = true
                        }
                        .semantics { contentDescription = "Abrir filtros avançados com ${filters.activeCount} ativos" }
                ) {
                    Button(
                        onClick = {
                            draftFilters = filters
                            isSheetOpen = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Filtros", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold)

                        if (filters.activeCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "${filters.activeCount}", fontSize = 10.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pílulas de Filtros Ativos (com remoção ✕ individual)
        if (!filters.isDefault) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (filters.locationFilter != "Todos") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("location") },
                            label = { Text("Local: ${filters.locationFilter}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Local ${filters.locationFilter}",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = SurfaceDark,
                                selectedContainerColor = SurfaceDark,
                                labelColor = TextWhite,
                                selectedLabelColor = TextWhite,
                                trailingIconColor = TextMuted
                            )
                        )
                    }
                }

                if (filters.sectorFilter != "Todos") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("sector") },
                            label = { Text("Setor: ${filters.sectorFilter}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Setor ${filters.sectorFilter}",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = SurfaceDark,
                                selectedContainerColor = SurfaceDark,
                                labelColor = TextWhite,
                                selectedLabelColor = TextWhite,
                                trailingIconColor = TextMuted
                            )
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet de Filtros Avançados
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            containerColor = SurfaceDark,
            contentWindowInsets = { WindowInsets.navigationBars }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                // Header Sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Filtros Avançados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = { isSheetOpen = false }, modifier = Modifier.size(48.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar filtros", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                // Rolável
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Seletor de Local
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Local de pertencimento", fontSize = 12.sp, color = TextSubtle)
                        FiltroLinhaSeletor(
                            label = "Local",
                            valorAtual = draftFilters.locationFilter,
                            onClick = { activeSelectorType = OperacionalSelectorType.LOCAL }
                        )
                    }

                    // 2. Seletor de Setor (Exibido na aba Equipamentos)
                    if (tabIndex == 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "Setor vinculado", fontSize = 12.sp, color = TextSubtle)
                            FiltroLinhaSeletor(
                                label = "Setor",
                                valorAtual = draftFilters.sectorFilter,
                                onClick = { activeSelectorType = OperacionalSelectorType.SETOR }
                            )
                        }
                    }
                }

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                // Rodapé Fixo: "Limpar" (só mexe no draft) e "Aplicar" (confirma)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = {
                            draftFilters = draftFilters.copy(
                                locationFilter = "Todos",
                                sectorFilter = "Todos",
                                equipmentFilter = "Todos"
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = "Limpar", fontSize = 14.sp, color = TextMuted)
                    }

                    Button(
                        onClick = {
                            onApplyFilters(draftFilters)
                            isSheetOpen = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = "Aplicar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Seletor de Segundo Nível (FiltroSeletorBusca)
    activeSelectorType?.let { selector ->
        val (titulo, items) = when (selector) {
            OperacionalSelectorType.LOCAL -> {
                val list = mutableListOf(FiltroItemModel("Todos", "Todos"))
                list.addAll(locaisList.mapIndexed { idx, loc -> FiltroItemModel("${idx + 1}", loc) })
                "Local" to list
            }
            OperacionalSelectorType.SETOR -> {
                // Cascata: Se Local = "Todos", mostra o nome do local como subtítulo
                val filteredSetores = if (draftFilters.locationFilter == "Todos") {
                    setoresList.mapIndexed { idx, pair ->
                        FiltroItemModel("${idx + 1}", pair.first, pair.second)
                    }
                } else {
                    setoresList.filter { it.second.equals(draftFilters.locationFilter, ignoreCase = true) }
                        .mapIndexed { idx, pair -> FiltroItemModel("${idx + 1}", pair.first, pair.second) }
                }
                val list = mutableListOf(FiltroItemModel("Todos", "Todos"))
                list.addAll(filteredSetores)
                "Setor" to list
            }
        }

        val selectedVal = when (selector) {
            OperacionalSelectorType.LOCAL -> draftFilters.locationFilter
            OperacionalSelectorType.SETOR -> draftFilters.sectorFilter
        }

        FiltroSeletorBusca(
            titulo = titulo,
            items = items,
            selectedId = selectedVal,
            onItemSelected = { selectedTitle ->
                draftFilters = when (selector) {
                    OperacionalSelectorType.LOCAL -> {
                        // Cascata: Mudar Local reseta Setor e Equipamento no draft para "Todos"
                        draftFilters.copy(
                            locationFilter = selectedTitle,
                            sectorFilter = "Todos",
                            equipmentFilter = "Todos"
                        )
                    }
                    OperacionalSelectorType.SETOR -> {
                        // Cascata: Mudar Setor reseta Equipamento no draft para "Todos"
                        draftFilters.copy(
                            sectorFilter = selectedTitle,
                            equipmentFilter = "Todos"
                        )
                    }
                }
                activeSelectorType = null
            },
            onDismiss = { activeSelectorType = null }
        )
    }
}

enum class OperacionalSelectorType {
    LOCAL, SETOR
}
