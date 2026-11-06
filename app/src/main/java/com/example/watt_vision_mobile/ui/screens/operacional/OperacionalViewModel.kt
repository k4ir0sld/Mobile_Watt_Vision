package com.example.watt_vision_mobile.ui.screens.operacional

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.watt_vision_mobile.data.repository.MockOperacionalRepository
import com.example.watt_vision_mobile.data.repository.OperacionalRepository
import com.example.watt_vision_mobile.util.StringNormalization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OperacionalViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: OperacionalRepository = MockOperacionalRepository()

    private val _uiState = MutableStateFlow(OperacionalUiState())
    val uiState: StateFlow<OperacionalUiState> = _uiState.asStateFlow()

    init {
        // Restaura o estado salvo das abas e filtros
        val savedTab = savedStateHandle.get<Int>(KEY_ACTIVE_TAB) ?: 0
        val savedEqFilters = savedStateHandle.get<TabFilterState>(KEY_EQ_FILTERS) ?: TabFilterState()
        val savedStFilters = savedStateHandle.get<TabFilterState>(KEY_ST_FILTERS) ?: TabFilterState()
        val savedLcFilters = savedStateHandle.get<TabFilterState>(KEY_LC_FILTERS) ?: TabFilterState()

        _uiState.update {
            it.copy(
                activeTab = savedTab,
                equipamentosFilters = savedEqFilters,
                setoresFilters = savedStFilters,
                locaisFilters = savedLcFilters
            )
        }

        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val eqList = repository.getEquipamentos()
                val stList = repository.getSetores()
                val lcList = repository.getLocais()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        equipamentos = eqList,
                        setores = stList,
                        locais = lcList
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados operacionais"
                    )
                }
            }
        }
    }

    fun setActiveTab(index: Int) {
        savedStateHandle[KEY_ACTIVE_TAB] = index
        _uiState.update { it.copy(activeTab = index) }
    }

    fun updateSearchQuery(tabIndex: Int, query: String) {
        when (tabIndex) {
            0 -> {
                val updated = _uiState.value.equipamentosFilters.copy(searchQuery = query)
                savedStateHandle[KEY_EQ_FILTERS] = updated
                _uiState.update { it.copy(equipamentosFilters = updated) }
            }
            1 -> {
                val updated = _uiState.value.setoresFilters.copy(searchQuery = query)
                savedStateHandle[KEY_ST_FILTERS] = updated
                _uiState.update { it.copy(setoresFilters = updated) }
            }
            2 -> {
                val updated = _uiState.value.locaisFilters.copy(searchQuery = query)
                savedStateHandle[KEY_LC_FILTERS] = updated
                _uiState.update { it.copy(locaisFilters = updated) }
            }
        }
    }

    fun updateStatusFilter(tabIndex: Int, status: String) {
        when (tabIndex) {
            0 -> {
                val updated = _uiState.value.equipamentosFilters.copy(statusFilter = status)
                savedStateHandle[KEY_EQ_FILTERS] = updated
                _uiState.update { it.copy(equipamentosFilters = updated) }
            }
            1 -> {
                val updated = _uiState.value.setoresFilters.copy(statusFilter = status)
                savedStateHandle[KEY_ST_FILTERS] = updated
                _uiState.update { it.copy(setoresFilters = updated) }
            }
            2 -> {
                val updated = _uiState.value.locaisFilters.copy(statusFilter = status)
                savedStateHandle[KEY_LC_FILTERS] = updated
                _uiState.update { it.copy(locaisFilters = updated) }
            }
        }
    }

    fun applyTabFilters(tabIndex: Int, newFilters: TabFilterState) {
        when (tabIndex) {
            0 -> {
                savedStateHandle[KEY_EQ_FILTERS] = newFilters
                _uiState.update { it.copy(equipamentosFilters = newFilters) }
            }
            1 -> {
                savedStateHandle[KEY_ST_FILTERS] = newFilters
                _uiState.update { it.copy(setoresFilters = newFilters) }
            }
            2 -> {
                savedStateHandle[KEY_LC_FILTERS] = newFilters
                _uiState.update { it.copy(locaisFilters = newFilters) }
            }
        }
    }

    fun clearTabFilters(tabIndex: Int) {
        applyTabFilters(tabIndex, TabFilterState())
    }

    fun removeSpecificFilter(tabIndex: Int, filterKey: String) {
        val current = when (tabIndex) {
            0 -> _uiState.value.equipamentosFilters
            1 -> _uiState.value.setoresFilters
            else -> _uiState.value.locaisFilters
        }

        val updated = when (filterKey) {
            "search" -> current.copy(searchQuery = "")
            "status" -> current.copy(statusFilter = "Todos")
            "location" -> current.copy(locationFilter = "Todos", sectorFilter = "Todos", equipmentFilter = "Todos")
            "sector" -> current.copy(sectorFilter = "Todos", equipmentFilter = "Todos")
            "equipment" -> current.copy(equipmentFilter = "Todos")
            else -> current
        }

        applyTabFilters(tabIndex, updated)
    }

    // Filtra lista de equipamentos com normalização de acentos e busca por nome ou código
    fun getFilteredEquipamentos(): List<EquipamentoItem> {
        val state = _uiState.value
        val filters = state.equipamentosFilters
        val q = StringNormalization.normalize(filters.searchQuery)

        return state.equipamentos.filter { eq ->
            val matchQuery = q.isEmpty() ||
                    StringNormalization.normalize(eq.nome).contains(q) ||
                    StringNormalization.normalize(eq.codigo).contains(q)

            val matchStatus = filters.statusFilter == "Todos" ||
                    eq.status.equals(filters.statusFilter, ignoreCase = true)

            val matchLocation = filters.locationFilter == "Todos" ||
                    eq.localNome.equals(filters.locationFilter, ignoreCase = true)

            val matchSector = filters.sectorFilter == "Todos" ||
                    eq.setorNome.equals(filters.sectorFilter, ignoreCase = true)

            matchQuery && matchStatus && matchLocation && matchSector
        }
    }

    // Filtra lista de setores
    fun getFilteredSetores(): List<SetorItem> {
        val state = _uiState.value
        val filters = state.setoresFilters
        val q = StringNormalization.normalize(filters.searchQuery)

        return state.setores.filter { st ->
            val matchQuery = q.isEmpty() ||
                    StringNormalization.normalize(st.nome).contains(q) ||
                    StringNormalization.normalize(st.codigo).contains(q)

            val matchStatus = filters.statusFilter == "Todos" ||
                    st.status.equals(filters.statusFilter, ignoreCase = true)

            val matchLocation = filters.locationFilter == "Todos" ||
                    st.localNome.equals(filters.locationFilter, ignoreCase = true)

            matchQuery && matchStatus && matchLocation
        }
    }

    // Filtra lista de locais
    fun getFilteredLocais(): List<LocalItem> {
        val state = _uiState.value
        val filters = state.locaisFilters
        val q = StringNormalization.normalize(filters.searchQuery)

        return state.locais.filter { lc ->
            val matchQuery = q.isEmpty() ||
                    StringNormalization.normalize(lc.nome).contains(q) ||
                    StringNormalization.normalize(lc.codigo).contains(q)

            val matchStatus = filters.statusFilter == "Todos" ||
                    lc.status.equals(filters.statusFilter, ignoreCase = true)

            matchQuery && matchStatus
        }
    }

    companion object {
        private const val KEY_ACTIVE_TAB = "operacional_active_tab"
        private const val KEY_EQ_FILTERS = "operacional_eq_filters"
        private const val KEY_ST_FILTERS = "operacional_st_filters"
        private const val KEY_LC_FILTERS = "operacional_lc_filters"
    }
}
