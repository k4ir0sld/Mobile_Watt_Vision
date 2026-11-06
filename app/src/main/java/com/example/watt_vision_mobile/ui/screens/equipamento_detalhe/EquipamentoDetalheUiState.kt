package com.example.watt_vision_mobile.ui.screens.equipamento_detalhe

enum class AlertaSeveridade {
    CRITICO, ALERTA, AVISO
}

enum class ConexaoStatus {
    ONLINE, OFFLINE
}

enum class ScreenState {
    INITIAL_LOADING,
    UPDATED,
    STALE,       // Falha de conexão recente, mantendo os dados antigos
    TOTAL_ERROR  // Erro sem nenhum dado disponível
}

data class AlertaDetalheItem(
    val id: String,
    val titulo: String,
    val severidade: AlertaSeveridade,
    val condicao: String,
    val dataHoraFormatada: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val isNovo: Boolean = false
)

data class EquipamentoDetalheData(
    val id: String,
    val codigo: String,
    val nome: String,
    val fabricante: String,
    val modelo: String,
    val numeroSerie: String,
    val tensaoNominalV: Double?,
    val dataInstalacao: String?, // ISO YYYY-MM-DD
    val localNome: String,
    val setorNome: String,
    val tipo: String,
    val status: String, // Ativo ou Inativo
    val descricao: String,
    val conexaoStatus: ConexaoStatus,
    val ultimaComunicacaoTimestampMs: Long,
    val alarmes: List<AlertaDetalheItem> = emptyList()
)

data class EquipamentoDetalheUiState(
    val screenState: ScreenState = ScreenState.INITIAL_LOADING,
    val data: EquipamentoDetalheData? = null,
    val ultimaAtualizacaoText: String = "",
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val hasNewCriticalAlarm: Boolean = false
)
