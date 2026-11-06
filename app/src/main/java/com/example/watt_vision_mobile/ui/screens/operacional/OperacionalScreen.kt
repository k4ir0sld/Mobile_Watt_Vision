package com.example.watt_vision_mobile.ui.screens.operacional

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.screens.operacional.components.*
import com.example.watt_vision_mobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OperacionalScreen(
    viewModel: OperacionalViewModel,
    onEquipamentoClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = uiState.activeTab) { 3 }
    val coroutineScope = rememberCoroutineScope()

    // Sincroniza a aba ativa do ViewModel com o Pager
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setActiveTab(pagerState.currentPage)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            // TopAppBar com título "Operacional" e subtítulo "Visão geral do sistema" (Respeitando a status bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InputBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = "Operacional",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Visão geral do sistema",
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // PrimaryTabRow (Equipamentos, Setores, Locais)
            val tabs = listOf("Equipamentos", "Setores", "Locais")
            PrimaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = InputBackground,
                contentColor = PrimaryBlue,
                divider = { HorizontalDivider(color = CardBorder, thickness = 1.dp) }
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = pagerState.currentPage == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryBlue else TextMuted
                            )
                        },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .semantics { contentDescription = "Aba $title, selecionada: $isSelected" }
                    )
                }
            }

            // Pager das Abas
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val locaisList = remember(uiState.locais) { uiState.locais.map { it.nome } }
                val setoresList = remember(uiState.setores) { uiState.setores.map { it.nome to it.localNome } }

                when (page) {
                    0 -> {
                        // ABA 0: EQUIPAMENTOS
                        val filteredEqs = viewModel.getFilteredEquipamentos()
                        val filters = uiState.equipamentosFilters

                        Column(modifier = Modifier.fillMaxSize()) {
                            OperacionalFilterBar(
                                tabIndex = 0,
                                filters = filters,
                                onQueryChanged = { viewModel.updateSearchQuery(0, it) },
                                onStatusChanged = { viewModel.updateStatusFilter(0, it) },
                                onApplyFilters = { viewModel.applyTabFilters(0, it) },
                                onRemoveFilter = { viewModel.removeSpecificFilter(0, it) },
                                locaisList = locaisList,
                                setoresList = setoresList
                            )

                            if (uiState.isLoading) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(5) { OperacionalSkeletonCard() }
                                }
                            } else if (uiState.equipamentos.isEmpty()) {
                                ListaVaziaState(
                                    mensagem = "Nenhum registro cadastrado. Os cadastros são feitos pela versão web."
                                )
                            } else if (filteredEqs.isEmpty()) {
                                BuscaSemResultadoState(
                                    onLimparFiltros = { viewModel.clearTabFilters(0) }
                                )
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredEqs, key = { it.id }) { eq ->
                                        EquipamentoCard(
                                            item = eq,
                                            onClick = { onEquipamentoClick(eq.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // ABA 1: SETORES
                        val filteredSts = viewModel.getFilteredSetores()
                        val filters = uiState.setoresFilters

                        Column(modifier = Modifier.fillMaxSize()) {
                            OperacionalFilterBar(
                                tabIndex = 1,
                                filters = filters,
                                onQueryChanged = { viewModel.updateSearchQuery(1, it) },
                                onStatusChanged = { viewModel.updateStatusFilter(1, it) },
                                onApplyFilters = { viewModel.applyTabFilters(1, it) },
                                onRemoveFilter = { viewModel.removeSpecificFilter(1, it) },
                                locaisList = locaisList,
                                setoresList = setoresList
                            )

                            if (uiState.isLoading) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(5) { OperacionalSkeletonCard() }
                                }
                            } else if (uiState.setores.isEmpty()) {
                                ListaVaziaState(
                                    mensagem = "Nenhum registro cadastrado. Os cadastros são feitos pela versão web."
                                )
                            } else if (filteredSts.isEmpty()) {
                                BuscaSemResultadoState(
                                    onLimparFiltros = { viewModel.clearTabFilters(1) }
                                )
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredSts, key = { it.id }) { st ->
                                        SetorCard(item = st) // onClick = null (somente visualização)
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        // ABA 2: LOCAIS
                        val filteredLcs = viewModel.getFilteredLocais()
                        val filters = uiState.locaisFilters

                        Column(modifier = Modifier.fillMaxSize()) {
                            OperacionalFilterBar(
                                tabIndex = 2,
                                filters = filters,
                                onQueryChanged = { viewModel.updateSearchQuery(2, it) },
                                onStatusChanged = { viewModel.updateStatusFilter(2, it) },
                                onApplyFilters = { viewModel.applyTabFilters(2, it) },
                                onRemoveFilter = { viewModel.removeSpecificFilter(2, it) },
                                locaisList = locaisList,
                                setoresList = setoresList
                            )

                            if (uiState.isLoading) {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(5) { OperacionalSkeletonCard() }
                                }
                            } else if (uiState.locais.isEmpty()) {
                                ListaVaziaState(
                                    mensagem = "Nenhum registro cadastrado. Os cadastros são feitos pela versão web."
                                )
                            } else if (filteredLcs.isEmpty()) {
                                BuscaSemResultadoState(
                                    onLimparFiltros = { viewModel.clearTabFilters(2) }
                                )
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredLcs, key = { it.id }) { lc ->
                                        LocalCard(item = lc) // onClick = null (somente visualização)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Estado Vazio (Sem registros cadastrados no sistema)
@Composable
fun ListaVaziaState(mensagem: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = mensagem,
                fontSize = 13.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Estado de Busca sem Resultados
@Composable
fun BuscaSemResultadoState(onLimparFiltros: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nenhum resultado encontrado",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Button(
                onClick = onLimparFiltros,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text(text = "Limpar filtros", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// PREVIEWS (Conforme solicitado)
// ==========================================

@Preview(name = "Lista Vazia de Registros", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewListaVaziaState() {
    WattVisionTheme {
        ListaVaziaState(mensagem = "Nenhum registro cadastrado. Os cadastros são feitos pela versão web.")
    }
}

@Preview(name = "Busca sem Resultados", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewBuscaSemResultadoState() {
    WattVisionTheme {
        BuscaSemResultadoState(onLimparFiltros = {})
    }
}
