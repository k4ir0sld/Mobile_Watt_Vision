package com.example.watt_vision_mobile.data.repository

import com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface EquipamentoDetalheRepository {
    suspend fun getDetalheEquipamento(id: String): Result<EquipamentoDetalheData>
}

class MockEquipamentoDetalheRepository : EquipamentoDetalheRepository {

    private var simulatedTickCount = 0

    override suspend fun getDetalheEquipamento(id: String): Result<EquipamentoDetalheData> {
        return withContext(Dispatchers.IO) {
            simulatedTickCount++

            // Coerência total de IDs com os gerados no MockOperacionalRepository
            val idNum = id.toIntOrNull() ?: 1
            val codigo = "EQ-${idNum.toString().padStart(3, '0')}"
            
            val nome = when (idNum % 6) {
                1 -> "Motor Elétrico WEG $idNum"
                2 -> "Compressor de Ar Industrial $idNum"
                3 -> "Bomba Centrífuga $idNum"
                4 -> "Ponte Rolante $idNum"
                5 -> "Inversor de Frequência $idNum"
                else -> "Exaustor de Alta Pressão $idNum"
            }

            val localNome = when (idNum % 4) {
                1 -> "Planta Industrial Jandira"
                2 -> "Planta Industrial Osasco"
                3 -> "Centro Logístico Alphaville"
                else -> "Subestação Central"
            }

            val setorNome = when (idNum % 3) {
                1 -> "Produção B"
                2 -> "Manutenção Elétrica"
                else -> "Caldeiraria & Usinagem"
            }

            // Simula falha de conexão ocasional a cada 7 ticks para testar o estado STALE
            if (simulatedTickCount % 8 == 0) {
                return@withContext Result.failure(Exception("Erro temporário de conexão com a API"))
            }

            val status = if (idNum % 5 == 0) "Inativo" else "Ativo"
            val conexaoStatus = if (idNum % 3 == 0 || simulatedTickCount % 9 == 0) ConexaoStatus.OFFLINE else ConexaoStatus.ONLINE
            
            // Simula timestamp da última comunicação (se offline, há 12 min; se online, há alguns segundos)
            val ultimaComunicaoMs = if (conexaoStatus == ConexaoStatus.OFFLINE) {
                System.currentTimeMillis() - (12 * 60 * 1000L)
            } else {
                System.currentTimeMillis() - (simulatedTickCount * 1000L % 15000L)
            }

            // Simulação dos Casos Específicos:
            // idNum == 2: sem alarmes
            // idNum == 6: campos vazios
            // outros: 3 alarmes com simulação de um novo alarme chegando dinamicamente no tick 3
            val alarmesList = mutableListOf<AlertaDetalheItem>()

            if (idNum != 2 && idNum != 6) {
                alarmesList.add(
                    AlertaDetalheItem(
                        id = "a1",
                        titulo = "Sobretensão detectada",
                        severidade = AlertaSeveridade.CRITICO,
                        condicao = "Tensão > 250 V (atual: 263 V)",
                        dataHoraFormatada = "Hoje, 14:32"
                    )
                )
                alarmesList.add(
                    AlertaDetalheItem(
                        id = "a2",
                        titulo = "Corrente elevada",
                        severidade = AlertaSeveridade.ALERTA,
                        condicao = "Corrente > 80 A (atual: 84,3 A)",
                        dataHoraFormatada = "Hoje, 12:15"
                    )
                )
                alarmesList.add(
                    AlertaDetalheItem(
                        id = "a3",
                        titulo = "Temperatura interna alta",
                        severidade = AlertaSeveridade.AVISO,
                        condicao = "Temp. > 70°C (atual: 71,2°C)",
                        dataHoraFormatada = "Ontem, 23:47"
                    )
                )

                // Simula um alarme crítico novíssimo aparecendo no tick 3
                if (simulatedTickCount >= 3) {
                    alarmesList.add(
                        0,
                        AlertaDetalheItem(
                            id = "a4_novo",
                            titulo = "Falha crítica de fase R",
                            severidade = AlertaSeveridade.CRITICO,
                            condicao = "Desequilíbrio de Fase > 15% (atual: 18.4%)",
                            dataHoraFormatada = "Agora mesmo",
                            isNovo = true
                        )
                    )
                }
            }

            // Dados vazios para idNum == 6
            val fabricante = if (idNum == 6) "" else "WEG"
            val modelo = if (idNum == 6) "" else "W22 Super Premium"
            val numSerie = if (idNum == 6) "" else "WV-MOTOR-${idNum.toString().padStart(4, '0')}"
            val tensaoNominal = if (idNum == 6) null else 220.0
            val dataInstalacao = if (idNum == 6) null else "2026-11-06"
            val descricao = if (idNum == 6) "" else "Equipamento demonstrativo do TCC Watt Vision utilizado no monitoramento industrial de alta eficiência."

            Result.success(
                EquipamentoDetalheData(
                    id = id,
                    codigo = codigo,
                    nome = nome,
                    fabricante = fabricante,
                    modelo = modelo,
                    numeroSerie = numSerie,
                    tensaoNominalV = tensaoNominal,
                    dataInstalacao = dataInstalacao,
                    localNome = localNome,
                    setorNome = setorNome,
                    tipo = "Motor Elétrico",
                    status = status,
                    descricao = descricao,
                    conexaoStatus = conexaoStatus,
                    ultimaComunicacaoTimestampMs = ultimaComunicaoMs,
                    alarmes = alarmesList
                )
            )
        }
    }
}
