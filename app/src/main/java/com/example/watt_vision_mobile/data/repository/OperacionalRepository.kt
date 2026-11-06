package com.example.watt_vision_mobile.data.repository

import com.example.watt_vision_mobile.ui.screens.operacional.EquipamentoItem
import com.example.watt_vision_mobile.ui.screens.operacional.LocalItem
import com.example.watt_vision_mobile.ui.screens.operacional.SetorItem

interface OperacionalRepository {
    suspend fun getLocais(): List<LocalItem>
    suspend fun getSetores(): List<SetorItem>
    suspend fun getEquipamentos(): List<EquipamentoItem>
}

class MockOperacionalRepository : OperacionalRepository {

    override suspend fun getLocais(): List<LocalItem> {
        return listOf(
            LocalItem(
                id = "1",
                codigo = "LOC-001",
                nome = "Planta Industrial Jandira",
                setoresVinculados = 12,
                equipamentosVinculados = 45,
                consumoAcumuladoKwh = 1685.3,
                status = "Ativo"
            ),
            LocalItem(
                id = "2",
                codigo = "LOC-002",
                nome = "Planta Industrial Osasco",
                setoresVinculados = 10,
                equipamentosVinculados = 38,
                consumoAcumuladoKwh = 2613.6,
                status = "Ativo"
            ),
            LocalItem(
                id = "3",
                codigo = "LOC-003",
                nome = "Centro Logístico Alphaville",
                setoresVinculados = 5,
                equipamentosVinculados = 18,
                consumoAcumuladoKwh = 840.0,
                status = "Ativo"
            ),
            LocalItem(
                id = "4",
                codigo = "LOC-004",
                nome = "Subestação Central",
                setoresVinculados = 3,
                equipamentosVinculados = 12,
                consumoAcumuladoKwh = 245.8,
                status = "Inativo"
            )
        )
    }

    override suspend fun getSetores(): List<SetorItem> {
        val locaisList = getLocais()
        val setoresList = mutableListOf<SetorItem>()

        locaisList.forEachIndexed { lIdx, local ->
            val numSetores = if (local.nome.contains("Jandira")) 12 else if (local.nome.contains("Osasco")) 10 else 5
            for (s in 1..numSetores) {
                val sId = "${lIdx + 1}-$s"
                val sNome = when (s % 5) {
                    1 -> "Produção B"
                    2 -> "Manutenção Elétrica"
                    3 -> "Caldeiraria & Usinagem"
                    4 -> "Pintura Industrial"
                    else -> "Logística & Expedição $s"
                }
                setoresList.add(
                    SetorItem(
                        id = sId,
                        codigo = "SET-0$sId",
                        nome = sNome,
                        localNome = local.nome,
                        equipamentosVinculados = (3..8).random(),
                        consumoAcumuladoKwh = (120..450).random().toDouble(),
                        status = if (s % 4 == 0) "Inativo" else "Ativo"
                    )
                )
            }
        }
        return setoresList
    }

    override suspend fun getEquipamentos(): List<EquipamentoItem> {
        val setores = getSetores()
        val equipamentosList = mutableListOf<EquipamentoItem>()

        var count = 1
        setores.forEach { setor ->
            val numEqs = (3..5).random()
            for (e in 1..numEqs) {
                val eqNome = when (count % 6) {
                    1 -> "Motor Elétrico WEG $count"
                    2 -> "Compressor de Ar Industrial $count"
                    3 -> "Bomba Centrífuga $count"
                    4 -> "Ponte Rolante $count"
                    5 -> "Inversor de Frequência $count"
                    else -> "Exaustor de Alta Pressão $count"
                }
                equipamentosList.add(
                    EquipamentoItem(
                        id = "$count",
                        codigo = "EQ-${count.toString().padStart(3, '0')}",
                        nome = eqNome,
                        localNome = setor.localNome,
                        setorNome = setor.nome,
                        potenciaW = (750..15000).random().toDouble(),
                        consumoKwh = (10..350).random().toDouble(),
                        status = if (count % 5 == 0) "Inativo" else "Ativo"
                    )
                )
                count++
            }
        }
        return equipamentosList
    }
}
