package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.components.filtros.*
import com.example.watt_vision_mobile.ui.screens.dashboard.DashboardFilterState
import com.example.watt_vision_mobile.ui.theme.*

// ==========================================
// NOTA TÉCNICA DE PAGINAÇÃO (Item 5):
// Se a lista de equipamentos ou setores crescer para centenas/milhares de registros,
// a busca e paginação (Paging 3 ou LazyPagingItems) deve ser conectada aqui através
// de um Flow do ViewModel consumindo uma API paginada no DashboardRepository:
// ex: GET /api/equipamentos?search=query&page=page&limit=20
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeaderAndFilters(
    filters: DashboardFilterState,
    lastUpdatedText: String,
    onFilterChanged: (DashboardFilterState) -> Unit,
    onRefreshClick: () -> Unit,
    onRemoveFilter: (String) -> Unit,
    onAlarmesClick: () -> Unit,
    onConfiguracoesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFilterSheetOpen by rememberSaveable { mutableStateOf(false) }

    // Estado temporário (draft) para manipulação no BottomSheet sem afetar o aplicado instantaneamente
    var draftFilters by remember { mutableStateOf(filters) }

    // Estados para controlar qual seletor de busca de 2º nível está aberto
    var activeSelector by remember { mutableStateOf<SelectorType?>(null) }

    // Sincroniza o draft sempre que os filtros aplicados mudarem externamente
    LaunchedEffect(filters) {
        draftFilters = filters
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Cabeçalho superior padrão limpo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onAlarmesClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Alarmes recentes",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onConfiguracoesClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações do sistema",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Barra compacta de status e botão "Filtros" com Badge (Sticky ao rolar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = InputBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onRefreshClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Atualizar dados",
                        tint = LinkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = lastUpdatedText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        draftFilters = filters
                        isFilterSheetOpen = true
                    }
                    .semantics { contentDescription = "Abrir painel de filtros com ${filters.activeCount} filtros ativos" }
            ) {
                Button(
                    onClick = {
                        draftFilters = filters
                        isFilterSheetOpen = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filtros",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (filters.activeCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${filters.activeCount}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }
        }

        // LazyRow de chips ativos com ícone ✕ para remoção individual
        if (!filters.isDefault) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (filters.period != "Hoje") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("period") },
                            label = { Text("Período: ${filters.period}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Período ${filters.period}",
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
                if (filters.location != "Todos") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("location") },
                            label = { Text("Local: ${filters.location}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Local ${filters.location}",
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
                if (filters.sector != "Todos") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("sector") },
                            label = { Text("Setor: ${filters.sector}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Setor ${filters.sector}",
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
                if (filters.equipment != "Todos") {
                    item {
                        InputChip(
                            selected = true,
                            onClick = { onRemoveFilter("equipment") },
                            label = { Text("Equip.: ${filters.equipment}", fontSize = 11.sp) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover filtro Equipamento ${filters.equipment}",
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

    // Modal Bottom Sheet Principal
    if (isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isFilterSheetOpen = false },
            containerColor = SurfaceDark,
            contentWindowInsets = { WindowInsets.navigationBars }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                // Cabeçalho do Bottom Sheet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtros",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(
                        onClick = { isFilterSheetOpen = false },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar painel de filtros",
                            tint = TextMuted
                        )
                    }
                }

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                // Conteúdo Rolável
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. Período: FilterChips em fluxo
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Período", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Hoje", "7 dias", "30 dias", "Personalizado").forEach { periodOpt ->
                                val isSelected = draftFilters.period == periodOpt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { draftFilters = draftFilters.copy(period = periodOpt) },
                                    label = { Text(periodOpt, fontSize = 11.sp) },
                                    modifier = Modifier.heightIn(min = 48.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = InputBackground,
                                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.25f),
                                        labelColor = TextMuted,
                                        selectedLabelColor = PrimaryBlue
                                    )
                                )
                            }
                        }
                    }

                    // 2. Seletor de Local (FiltroLinhaSeletor)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Local", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                        FiltroLinhaSeletor(
                            label = "Local selecionado",
                            valorAtual = draftFilters.location,
                            onClick = { activeSelector = SelectorType.LOCAL }
                        )
                    }

                    // 3. Seletor de Setor (FiltroLinhaSeletor com dependência em cascata do Local)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Setor", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                        FiltroLinhaSeletor(
                            label = "Setor selecionado",
                            valorAtual = draftFilters.sector,
                            onClick = { activeSelector = SelectorType.SETOR }
                        )
                    }

                    // 4. Seletor de Equipamento (FiltroLinhaSeletor com dependência em cascata do Setor)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Equipamento", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                        FiltroLinhaSeletor(
                            label = "Equipamento selecionado",
                            valorAtual = draftFilters.equipment,
                            onClick = { activeSelector = SelectorType.EQUIPAMENTO }
                        )
                    }
                }

                HorizontalDivider(color = CardBorder, thickness = 1.dp)

                // Rodapé Fixo: "Limpar" (só mexe no draft) e "Aplicar" (confirma)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            draftFilters = DashboardFilterState() // Restaura padrões apenas no draft
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = "Limpar", fontSize = 14.sp, color = TextMuted)
                    }

                    Button(
                        onClick = {
                            onFilterChanged(draftFilters) // Confirma e aplica o draft
                            isFilterSheetOpen = false
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
    activeSelector?.let { selector ->
        val (titulo, items) = when (selector) {
            SelectorType.LOCAL -> {
                val locais = listOf(
                    FiltroItemModel("Todos", "Todos"),
                    FiltroItemModel("1", "Planta Industrial Jandira", "Unidade Fabril SP"),
                    FiltroItemModel("2", "Planta Industrial Osasco", "Unidade Fabril SP"),
                    FiltroItemModel("3", "Centro Logístico Alphaville", "Centro de Distribuição"),
                    FiltroItemModel("4", "Subestação Central", "Energia Principal")
                )
                "Local" to locais
            }
            SelectorType.SETOR -> {
                // Cascata: Se Local = "Todos", mostra todos os setores com o nome do local como subtítulo
                val setoresRaw = if (draftFilters.location == "Todos") {
                    listOf(
                        FiltroItemModel("s1", "Produção", "Planta Industrial Jandira"),
                        FiltroItemModel("s2", "Manutenção", "Planta Industrial Jandira"),
                        FiltroItemModel("s3", "Caldeiraria", "Planta Industrial Osasco"),
                        FiltroItemModel("s4", "Logística Avançada", "Centro Logístico Alphaville")
                    )
                } else {
                    listOf(
                        FiltroItemModel("s1", "Produção", draftFilters.location),
                        FiltroItemModel("s2", "Manutenção", draftFilters.location)
                    )
                }
                val setoresList = mutableListOf(FiltroItemModel("Todos", "Todos"))
                setoresList.addAll(setoresRaw)
                "Setor" to setoresList
            }
            SelectorType.EQUIPAMENTO -> {
                // Cascata: Se Setor = "Todos", exibe equipamentos gerais
                val eqsRaw = if (draftFilters.sector == "Todos") {
                    listOf(
                        FiltroItemModel("e1", "Motor Elétrico WEG 15HP", "Setor Produção"),
                        FiltroItemModel("e2", "Compressor de Ar Industrial", "Setor Manutenção"),
                        FiltroItemModel("e3", "Ponte Rolante 10T", "Caldeiraria")
                    )
                } else {
                    listOf(
                        FiltroItemModel("e1", "Motor Elétrico WEG 15HP", draftFilters.sector),
                        FiltroItemModel("e2", "Compressor Principal", draftFilters.sector)
                    )
                }
                val eqsList = mutableListOf(FiltroItemModel("Todos", "Todos"))
                eqsList.addAll(eqsRaw)
                "Equipamento" to eqsList
            }
        }

        val selectedValue = when (selector) {
            SelectorType.LOCAL -> draftFilters.location
            SelectorType.SETOR -> draftFilters.sector
            SelectorType.EQUIPAMENTO -> draftFilters.equipment
        }

        FiltroSeletorBusca(
            titulo = titulo,
            items = items,
            selectedId = selectedValue,
            onItemSelected = { selectedTitle ->
                draftFilters = when (selector) {
                    SelectorType.LOCAL -> {
                        // Mudar Local reseta Setor e Equipamento para "Todos"
                        draftFilters.copy(
                            location = selectedTitle,
                            sector = "Todos",
                            equipment = "Todos"
                        )
                    }
                    SelectorType.SETOR -> {
                        // Mudar Setor reseta Equipamento para "Todos"
                        draftFilters.copy(
                            sector = selectedTitle,
                            equipment = "Todos"
                        )
                    }
                    SelectorType.EQUIPAMENTO -> {
                        draftFilters.copy(equipment = selectedTitle)
                    }
                }
                activeSelector = null
            },
            onDismiss = { activeSelector = null }
        )
    }
}

enum class SelectorType {
    LOCAL, SETOR, EQUIPAMENTO
}

@Composable
fun FiltroLinhaSeletor(
    label: String,
    valorAtual: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = InputBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp) // Requisito Mínimo 48dp
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .semantics {
                contentDescription = "$label, selecionado: $valorAtual. Toque para alterar"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = valorAtual,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==========================================
// PREVIEWS (Conforme solicitado)
// ==========================================

@Preview(name = "Sem filtros", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewDashboardHeaderWithoutFilters() {
    WattVisionTheme {
        DashboardHeaderAndFilters(
            filters = DashboardFilterState(),
            lastUpdatedText = "Atualizado às 10:28",
            onFilterChanged = {},
            onRefreshClick = {},
            onRemoveFilter = {},
            onAlarmesClick = {},
            onConfiguracoesClick = {}
        )
    }
}

@Preview(name = "Com filtros ativos", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewDashboardHeaderWithActiveFilters() {
    WattVisionTheme {
        DashboardHeaderAndFilters(
            filters = DashboardFilterState(period = "7 dias", location = "Planta Industrial Jandira", sector = "Produção"),
            lastUpdatedText = "Atualizado às 10:28",
            onFilterChanged = {},
            onRefreshClick = {},
            onRemoveFilter = {},
            onAlarmesClick = {},
            onConfiguracoesClick = {}
        )
    }
}
