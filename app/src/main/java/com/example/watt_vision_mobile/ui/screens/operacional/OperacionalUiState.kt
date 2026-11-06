package com.example.watt_vision_mobile.ui.screens.operacional

data class LocalItem(
    val id: String,
    val codigo: String,
    val nome: String,
    val setoresVinculados: Int,
    val equipamentosVinculados: Int,
    val consumoAcumuladoKwh: Double,
    val status: String // Ativo ou Inativo
)

data class SetorItem(
    val id: String,
    val codigo: String,
    val nome: String,
    val localNome: String,
    val equipamentosVinculados: Int,
    val consumoAcumuladoKwh: Double,
    val status: String // Ativo ou Inativo
)

data class EquipamentoItem(
    val id: String,
    val codigo: String,
    val nome: String,
    val localNome: String,
    val setorNome: String,
    val potenciaW: Double,
    val consumoKwh: Double,
    val status: String // Ativo ou Inativo
)

data class TabFilterState(
    val searchQuery: String = "",
    val statusFilter: String = "Todos", // Todos, Ativo, Inativo
    val locationFilter: String = "Todos",
    val sectorFilter: String = "Todos",
    val equipmentFilter: String = "Todos"
) {
    val isDefault: Boolean
        get() = searchQuery.isBlank() &&
                statusFilter == "Todos" &&
                locationFilter == "Todos" &&
                sectorFilter == "Todos" &&
                equipmentFilter == "Todos"

    val activeCount: Int
        get() {
            var count = 0
            if (searchQuery.isNotBlank()) count++
            if (statusFilter != "Todos") count++
            if (locationFilter != "Todos") count++
            if (sectorFilter != "Todos") count++
            if (equipmentFilter != "Todos") count++
            return count
        }
}

data class OperacionalUiState(
    val activeTab: Int = 0, // 0 = Equipamentos, 1 = Setores, 2 = Locais
    val isLoading: Boolean = false,
    
    val equipamentos: List<EquipamentoItem> = emptyList(),
    val setores: List<SetorItem> = emptyList(),
    val locais: List<LocalItem> = emptyList(),

    // Filtros aplicados por aba
    val equipamentosFilters: TabFilterState = TabFilterState(),
    val setoresFilters: TabFilterState = TabFilterState(),
    val locaisFilters: TabFilterState = TabFilterState(),

    val errorMessage: String? = null
)
