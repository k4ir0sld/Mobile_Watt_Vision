package com.example.watt_vision_mobile.ui.screens.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.ui.theme.*
import com.example.watt_vision_mobile.util.StringNormalization
import kotlinx.coroutines.delay

data class FiltroItemModel(
    val id: String,
    val title: String,
    val subtitle: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroSeletorBusca(
    titulo: String,
    items: List<FiltroItemModel>,
    selectedId: String,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var debouncedQuery by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    // Debounce de 200ms para busca (preparado também para buscas assíncronas/servidor)
    LaunchedEffect(searchQuery) {
        delay(200)
        debouncedQuery = searchQuery
    }

    // Foco automático no campo de busca ao abrir
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Separa a opção "Todos" e filtra o restante usando normalização de acentos
    val filteredItems = remember(debouncedQuery, items) {
        val queryNormalized = StringNormalization.normalize(debouncedQuery)
        items.filter { item ->
            if (item.id == "Todos" || item.title == "Todos") {
                true
            } else {
                val titleNorm = StringNormalization.normalize(item.title)
                val subNorm = StringNormalization.normalize(item.subtitle)
                titleNorm.contains(queryNormalized) || subNorm.contains(queryNormalized)
            }
        }
    }

    // Rola automaticamente até o item selecionado ao abrir
    LaunchedEffect(selectedId, items) {
        val index = filteredItems.indexOfFirst { it.id == selectedId || it.title == selectedId }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        contentWindowInsets = { WindowInsets.navigationBars }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            // Cabeçalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Fechar seletor de $titulo",
                        tint = TextMuted
                    )
                }
            }

            HorizontalDivider(color = CardBorder, thickness = 1.dp)

            // Campo de Busca Fixo com Foco Automático
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar $titulo...", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar busca", tint = TextMuted)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            // Lista de Opções (LazyColumn com altura flexível)
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum resultado encontrado",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        val isSelected = item.id == selectedId || item.title == selectedId

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onItemSelected(item.title)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) PrimaryBlue else TextWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!item.subtitle.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Item selecionado",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS (Conforme solicitado)
// ==========================================

@Preview(name = "Lista Longa de Equipamentos", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewFiltroSeletorBuscaLongList() {
    val sampleItems = (1..35).map { i ->
        FiltroItemModel(id = "$i", title = "Equipamento Industrial XPTO-$i", subtitle = "Setor de Produção A")
    }.toMutableList().apply {
        add(0, FiltroItemModel("Todos", "Todos"))
    }

    WattVisionTheme {
        FiltroSeletorBusca(
            titulo = "Equipamento",
            items = sampleItems,
            selectedId = "Equipamento Industrial XPTO-12",
            onItemSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Resultado Filtrado", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewFiltroSeletorBuscaFiltered() {
    val sampleItems = listOf(
        FiltroItemModel("Todos", "Todos"),
        FiltroItemModel("1", "Motor Elétrico WEG 15HP", "Planta Jandira"),
        FiltroItemModel("2", "Motor de Indução 30HP", "Planta Osasco")
    )

    WattVisionTheme {
        FiltroSeletorBusca(
            titulo = "Setor",
            items = sampleItems,
            selectedId = "Todos",
            onItemSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(name = "Estado Vazio", showBackground = true, backgroundColor = 0xFF070C15)
@Composable
fun PreviewFiltroSeletorBuscaEmpty() {
    WattVisionTheme {
        FiltroSeletorBusca(
            titulo = "Local",
            items = listOf(FiltroItemModel("Todos", "Todos")),
            selectedId = "Todos",
            onItemSelected = {},
            onDismiss = {}
        )
    }
}
