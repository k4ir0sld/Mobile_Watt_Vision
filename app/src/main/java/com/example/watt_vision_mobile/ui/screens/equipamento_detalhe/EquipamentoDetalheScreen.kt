package com.example.watt_vision_mobile.ui.screens.equipamento_detalhe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.components.*
import com.example.watt_vision_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipamentoDetalheScreen(
    viewModel: EquipamentoDetalheViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val relativeTimeString by viewModel.relativeTimeString.collectAsStateWithLifecycle()

    // Trata o botão de voltar físico/gesto do sistema Android mantendo intactos os filtros da lista Operacional
    BackHandler {
        onBackClick()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            // TopAppBar com botão voltar (min 48dp), título = nome do equipamento e subtítulo = código · local · setor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InputBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Voltar para lista de equipamentos" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.data?.nome ?: "Detalhe do Equipamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val subtitulo = if (uiState.data != null) {
                            "${uiState.data!!.codigo}  ·  ${uiState.data!!.localNome}  ·  ${uiState.data!!.setorNome}"
                        } else "Carregando..."
                        Text(
                            text = subtitulo,
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.screenState) {
                ScreenState.INITIAL_LOADING -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceDark.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {}
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceDark.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {}
                    }
                }

                ScreenState.TOTAL_ERROR -> {
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
                                text = uiState.errorMessage ?: "Erro ao carregar equipamento",
                                fontSize = 14.sp,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.startPollingAndTimer() },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text("Tentar novamente", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                ScreenState.UPDATED, ScreenState.STALE -> {
                    uiState.data?.let { data ->
                        // Região ao vivo para Acessibilidade: Polite para Online/Offline, Assertive para alarme crítico novo
                        val liveRegionMode = if (uiState.hasNewCriticalAlarm) LiveRegionMode.Assertive else LiveRegionMode.Polite
                        val liveAnnouncement = if (uiState.hasNewCriticalAlarm) {
                            "Atenção: Novo alarme crítico registrado no equipamento ${data.nome}"
                        } else {
                            "Status de conexão atualizado para ${data.conexaoStatus.name}"
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics {
                                    this.liveRegion = liveRegionMode
                                    this.contentDescription = liveAnnouncement
                                }
                        ) {
                            // Aviso discreto de dados desatualizados (STALE) caso haja falha temporária de conexão
                            if (uiState.screenState == ScreenState.STALE && !uiState.errorMessage.isNullOrBlank()) {
                                Surface(
                                    color = ToneYellowBg,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = ToneYellowText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.errorMessage!!,
                                            fontSize = 11.sp,
                                            color = ToneYellowText,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Conteúdo Principal Rolável (LazyColumn)
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Card de Resumo (Status Online/Offline e Cronômetro Relativo Local)
                                item {
                                    EquipamentoResumoCard(
                                        data = data,
                                        relativeTimeString = relativeTimeString
                                    )
                                }

                                // 2. Seção de Alarmes do Equipamento
                                item {
                                    AlarmesSecao(alarmes = data.alarmes)
                                }

                                // 3. Seção Informações — Identificação
                                item {
                                    InfoGrupoCard(titulo = "Identificação") {
                                        InfoCampo(rotulo = "Nome do Equipamento", valor = data.nome)
                                        InfoCampo(rotulo = "Identificação / Código", valor = data.codigo, copiavel = true)
                                        InfoCampo(rotulo = "Fabricante", valor = data.fabricante)
                                        InfoCampo(rotulo = "Modelo", valor = data.modelo)
                                        InfoCampo(rotulo = "Número de Série", valor = data.numeroSerie, copiavel = true)
                                    }
                                }

                                // 4. Seção Informações — Especificações
                                item {
                                    InfoGrupoCard(titulo = "Especificações") {
                                        InfoCampo(rotulo = "Tipo de Equipamento", valor = data.tipo)
                                        val tensaoFormated = data.tensaoNominalV?.let { "${it.toInt()} V" } ?: "—"
                                        InfoCampo(rotulo = "Tensão Nominal (V)", valor = tensaoFormated)
                                        val dataFormated = if (!data.dataInstalacao.isNullOrBlank() && data.dataInstalacao.contains("-")) {
                                            val parts = data.dataInstalacao.split("-")
                                            if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else data.dataInstalacao
                                        } else "—"
                                        InfoCampo(rotulo = "Data de Instalação", valor = dataFormated)
                                    }
                                }

                                // 5. Seção Informações — Localização
                                item {
                                    InfoGrupoCard(titulo = "Localização") {
                                        InfoCampo(rotulo = "Local de Pertencimento", valor = data.localNome)
                                        InfoCampo(rotulo = "Setor Vinculado", valor = data.setorNome)
                                    }
                                }

                                // 6. Seção Informações — Descrição (com ver mais/menos)
                                item {
                                    DescricaoCard(descricao = data.descricao)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Estado Normal com Alarmes", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewEquipamentoDetalheNormal() {
    WattVisionTheme {
        EquipamentoResumoCard(
            data = EquipamentoDetalheData(
                id = "1",
                codigo = "EQ-001",
                nome = "Motor Elétrico WEG 15HP",
                fabricante = "WEG",
                modelo = "W22",
                numeroSerie = "WV-MOTOR-0001",
                tensaoNominalV = 220.0,
                dataInstalacao = "2026-11-06",
                localNome = "Planta Industrial Jandira",
                setorNome = "Produção B",
                tipo = "Motor Elétrico",
                status = "Ativo",
                descricao = "Equipamento demonstrativo do TCC Watt Vision",
                conexaoStatus = ConexaoStatus.ONLINE,
                ultimaComunicacaoTimestampMs = System.currentTimeMillis()
            ),
            relativeTimeString = "há 3 segundos"
        )
    }
}

@Preview(name = "Alarme Recém-Chegado", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewAlarmeRecemChegado() {
    WattVisionTheme {
        AlarmeCard(
            item = AlertaDetalheItem(
                id = "novo",
                titulo = "Falha crítica de fase R",
                severidade = AlertaSeveridade.CRITICO,
                condicao = "Desequilíbrio de Fase > 15% (atual: 18.4%)",
                dataHoraFormatada = "Agora mesmo",
                isNovo = true
            )
        )
    }
}

@Preview(name = "Aviso Desatualizado (STALE)", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewAvisoStale() {
    WattVisionTheme {
        Surface(color = ToneYellowBg) {
            Row(modifier = Modifier.padding(12.dp)) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = ToneYellowText)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sem conexão. Última atualização às 14:32", color = ToneYellowText, fontSize = 11.sp)
            }
        }
    }
}
