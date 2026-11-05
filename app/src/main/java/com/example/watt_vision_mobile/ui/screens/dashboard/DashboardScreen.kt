package com.example.watt_vision_mobile.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.watt_vision_mobile.ui.screens.dashboard.components.*
import com.example.watt_vision_mobile.ui.theme.DarkBackground
import com.example.watt_vision_mobile.ui.theme.TextWhite

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TextWhite)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Cabeçalho com Título, Botões Alarmes/Config no Header, Status em linha inteira e Filtros
                DashboardHeaderAndFilters(
                    filters = uiState.filters,
                    lastUpdatedText = uiState.lastUpdatedText,
                    onFilterChanged = { key, value -> viewModel.onFilterChanged(key, value) },
                    onRefreshClick = { viewModel.loadDashboardData() },
                    onAlarmesClick = { onNavigate("Alarmes") },
                    onConfiguracoesClick = { onNavigate("Configurações") }
                )

                // 2. Botão de Composição do Cálculo (Σ Ver Composição) e Modal
                CalculoButtonAndModal(
                    composicaoItems = uiState.composicaoItems,
                    isOpen = uiState.isComposicaoModalOpen,
                    onOpenModal = { viewModel.setComposicaoModalVisible(true) },
                    onCloseModal = { viewModel.setComposicaoModalVisible(false) }
                )

                // 3. Cards de Resumo (StatCards em grid 2 colunas para mobile)
                StatCardsSection(cards = uiState.summaryCards)

                // 4. Gráfico "Desempenho do Sistema" com rolagem horizontal para evitar compressão
                SystemPerformanceChart(
                    points = uiState.chartPoints,
                    selectedMetric = uiState.selectedMetric,
                    selectedPeriod = uiState.selectedChartPeriod,
                    onMetricSelected = { viewModel.onMetricSelected(it) },
                    onPeriodSelected = { viewModel.onChartPeriodSelected(it) }
                )

                // 5. Alarmes Recentes
                RecentAlarmsSection(
                    alarms = uiState.alarms,
                    onSeeAllClick = { onNavigate("Alarmes") }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
