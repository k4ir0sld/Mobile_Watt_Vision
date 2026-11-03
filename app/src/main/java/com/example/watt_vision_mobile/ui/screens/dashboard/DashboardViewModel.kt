package com.example.watt_vision_mobile.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simula delay de carregamento da rede / API
            delay(300)

            val cards = listOf(
                StatCardData("Consumo Total", "1.284 kWh", "Consumo acumulado", "+12.5%", StatTone.BLUE, "cpu"),
                StatCardData("Dispositivos Ativos", "24", "Dispositivos conectados", "+8.2%", StatTone.GREEN, "check_circle"),
                StatCardData("Dispositivos Offline", "3", "Precisam de atenção", "-2.4%", StatTone.RED, "wifi_off"),
                StatCardData("Economia de Energia", "18.6%", "Economia estimada", "+5.7%", StatTone.YELLOW, "zap"),
                StatCardData("Tensão Atual", "220.4 V", "Rede elétrica nominal", "Estável", StatTone.BLUE, "flash"),
                StatCardData("Corrente Média", "14.2 A", "Carga nos alimentadores", "+1.1%", StatTone.GREEN, "activity")
            )

            val alarms = listOf(
                AlarmData("Sobretensão detectada", "Compressor HVAC-03", "há 2 min", AlarmType.CRITICAL),
                AlarmData("Fator de potência baixo", "Motor Bomba-07", "há 18 min", AlarmType.WARNING),
                AlarmData("Consumo acima do limite", "Linha de Produção B", "há 45 min", AlarmType.WARNING),
                AlarmData("Novo sensor registrado", "Painel Elétrico PE-12", "há 1 h", AlarmType.INFO),
                AlarmData("Equipamento restaurado", "Torno CNC-02", "há 2 h", AlarmType.SUCCESS)
            )

            val indicadores = listOf(
                EnergeticMetricData("Consumo Total", "487,3 kWh", "Consumo acumulado", "zap"),
                EnergeticMetricData("Consumo Médio", "69,6 kWh/dia", "Média diária", "activity"),
                EnergeticMetricData("Pico de Consumo", "94,2 kW", "às 14h22", "trending_up"),
                EnergeticMetricData("Maior Consumo", "10h – 16h", "Faixa horária", "clock"),
                EnergeticMetricData("Custo Estimado", "R$ 291,80", "R$ 0,598/kWh", "dollar"),
                EnergeticMetricData("Variação", "-7,3%", "vs. período anterior", "percent")
            )

            val composicao = listOf(
                ComposicaoItem("1", "Planta Industrial Jandira", 1685.3, "Calculado"),
                ComposicaoItem("2", "Planta Industrial Osasco", 2613.6, "Calculado"),
                ComposicaoItem("3", "Centro Logístico Alphaville", 0.0, "Sem dados"),
                ComposicaoItem("4", "Subestação Central", 245.8, "Calculado")
            )

            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            _uiState.update {
                it.copy(
                    isLoading = false,
                    summaryCards = cards,
                    alarms = alarms,
                    indicadores = indicadores,
                    composicaoItems = composicao,
                    lastUpdatedText = "Atualizado às $currentTime",
                    chartPoints = generateChartPoints(it.selectedMetric, it.selectedChartPeriod)
                )
            }
        }
    }

    fun onMetricSelected(metric: String) {
        _uiState.update {
            it.copy(
                selectedMetric = metric,
                chartPoints = generateChartPoints(metric, it.selectedChartPeriod)
            )
        }
    }

    fun onChartPeriodSelected(period: String) {
        _uiState.update {
            it.copy(
                selectedChartPeriod = period,
                chartPoints = generateChartPoints(it.selectedMetric, period)
            )
        }
    }

    fun setComposicaoModalVisible(visible: Boolean) {
        _uiState.update { it.copy(isComposicaoModalOpen = visible) }
    }

    fun onFilterChanged(key: String, value: String) {
        _uiState.update { state ->
            val updatedFilters = when (key) {
                "period" -> state.filters.copy(period = value)
                "location" -> state.filters.copy(location = value)
                "sector" -> state.filters.copy(sector = value)
                "equipment" -> state.filters.copy(equipment = value)
                else -> state.filters
            }
            state.copy(filters = updatedFilters)
        }
    }

    private fun generateChartPoints(metric: String, period: String): List<MedicaoPoint> {
        // Dados simulados realistas para o gráfico do Dashboard
        val times = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")

        val baseValues = when (metric) {
            "tensao" -> listOf(218f, 220f, 221f, 219f, 222f, 220.5f, 219.8f, 221.2f, 220f, 221f)
            "consumo" -> listOf(12f, 18f, 25f, 32f, 28f, 40f, 48f, 52f, 45f, 38f)
            "potencia" -> listOf(1200f, 1450f, 1800f, 2100f, 1950f, 2400f, 2600f, 2300f, 2050f, 1900f)
            "corrente" -> listOf(5.5f, 6.8f, 8.2f, 9.5f, 8.8f, 10.9f, 11.8f, 10.4f, 9.3f, 8.6f)
            else -> listOf(10f, 20f, 30f, 25f, 35f, 40f, 30f, 45f, 50f, 40f)
        }

        return times.zip(baseValues).map { (t, v) -> MedicaoPoint(t, v) }
    }
}
