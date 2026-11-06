package com.example.watt_vision_mobile.ui.screens.equipamento_detalhe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.watt_vision_mobile.data.repository.EquipamentoDetalheRepository
import com.example.watt_vision_mobile.data.repository.MockEquipamentoDetalheRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

class EquipamentoDetalheViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: EquipamentoDetalheRepository = MockEquipamentoDetalheRepository()

    private val _uiState = MutableStateFlow(EquipamentoDetalheUiState())
    val uiState: StateFlow<EquipamentoDetalheUiState> = _uiState.asStateFlow()

    // Flow isolado para o cronômetro local de idade relativa (evita recompor a tela inteira)
    private val _relativeTimeString = MutableStateFlow("há poucos segundos")
    val relativeTimeString: StateFlow<String> = _relativeTimeString.asStateFlow()

    private var pollingJob: Job? = null
    private var timerJob: Job? = null
    private var currentBackoffMs = 5000L

    val equipamentoId: String = savedStateHandle.get<String>(KEY_EQUIPAMENTO_ID) ?: "1"

    init {
        startPollingAndTimer()
    }

    fun setEquipamentoId(id: String) {
        savedStateHandle[KEY_EQUIPAMENTO_ID] = id
        startPollingAndTimer()
    }

    fun startPollingAndTimer() {
        pollingJob?.cancel()
        timerJob?.cancel()

        // 1. Loop de Polling em segundo plano com Backoff Exponencial
        pollingJob = viewModelScope.launch {
            var isFirst = true
            while (isActive) {
                fetchData(isInitial = isFirst)
                isFirst = false
                delay(currentBackoffMs)
            }
        }

        // 2. Cronômetro local independente para o texto "há X segundos/minutos"
        timerJob = viewModelScope.launch {
            while (isActive) {
                updateRelativeTimeText()
                val currentAgeMs = getCurrentAgeMs()
                val tickInterval = if (currentAgeMs < 60000L) 1000L else 60000L
                delay(tickInterval)
            }
        }
    }

    fun refreshManually() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            currentBackoffMs = 5000L // Reseta backoff ao forçar atualização
            fetchData(isInitial = false)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun fetchData(isInitial: Boolean) {
        if (isInitial && _uiState.value.data == null) {
            _uiState.update { it.copy(screenState = ScreenState.INITIAL_LOADING) }
        }

        val result = repository.getDetalheEquipamento(equipamentoId)

        if (result.isSuccess) {
            val newData = result.getOrNull()
            val oldData = _uiState.value.data

            // Detecta se entrou alarme crítico novo
            val oldAlarmIds = oldData?.alarmes?.map { it.id }?.toSet() ?: emptySet()
            val hasNewCritical = newData?.alarmes?.any { al ->
                al.severidade == AlertaSeveridade.CRITICO && al.id !in oldAlarmIds
            } ?: false

            val currentTimeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            currentBackoffMs = 5000L // Conexão bem-sucedida reseta o backoff

            _uiState.update {
                it.copy(
                    screenState = ScreenState.UPDATED,
                    data = newData,
                    ultimaAtualizacaoText = "Atualizado às $currentTimeStr",
                    errorMessage = null,
                    hasNewCriticalAlarm = hasNewCritical
                )
            }
            updateRelativeTimeText()
        } else {
            // Falha de Conexão: Retém os últimos dados e exibe aviso discreto STALE (com backoff crescente até 30s)
            currentBackoffMs = min(currentBackoffMs * 2, 30000L)
            val lastTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            _uiState.update { state ->
                if (state.data != null) {
                    state.copy(
                        screenState = ScreenState.STALE,
                        errorMessage = "Sem conexão. Última atualização às $lastTimeStr"
                    )
                } else {
                    state.copy(
                        screenState = ScreenState.TOTAL_ERROR,
                        errorMessage = "Erro ao carregar dados do equipamento"
                    )
                }
            }
        }
    }

    private fun getCurrentAgeMs(): Long {
        val lastComMs = _uiState.value.data?.ultimaComunicacaoTimestampMs ?: System.currentTimeMillis()
        return (System.currentTimeMillis() - lastComMs).coerceAtLeast(0L)
    }

    private fun updateRelativeTimeText() {
        val ageMs = getCurrentAgeMs()
        val seconds = ageMs / 1000L
        val minutes = seconds / 60L
        val hours = minutes / 60L

        val text = when {
            seconds < 5 -> "há 3 segundos"
            seconds < 60 -> "há $seconds segundos"
            minutes < 60 -> "há $minutes ${if (minutes == 1L) "minuto" else "minutos"}"
            else -> "há $hours ${if (hours == 1L) "hora" else "horas"}"
        }
        _relativeTimeString.value = text
    }

    companion object {
        private const val KEY_EQUIPAMENTO_ID = "selected_equipamento_id"
    }
}
