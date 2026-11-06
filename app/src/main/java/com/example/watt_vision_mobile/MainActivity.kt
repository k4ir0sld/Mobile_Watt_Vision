package com.example.watt_vision_mobile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.watt_vision_mobile.ui.components.WattVisionBottomBar
import com.example.watt_vision_mobile.ui.screens.dashboard.DashboardScreen
import com.example.watt_vision_mobile.ui.screens.dashboard.DashboardViewModel
import com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.EquipamentoDetalheScreen
import com.example.watt_vision_mobile.ui.screens.equipamento_detalhe.EquipamentoDetalheViewModel
import com.example.watt_vision_mobile.ui.screens.login.LoginScreen
import com.example.watt_vision_mobile.ui.screens.login.LoginViewModel
import com.example.watt_vision_mobile.ui.screens.operacional.OperacionalScreen
import com.example.watt_vision_mobile.ui.screens.operacional.OperacionalViewModel
import com.example.watt_vision_mobile.ui.screens.placeholder.PlaceholderScreen
import com.example.watt_vision_mobile.ui.theme.WattVisionTheme

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val operacionalViewModel: OperacionalViewModel by viewModels()
    private val equipamentoDetalheViewModel: EquipamentoDetalheViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WattVisionTheme {
                var isLoggedIn by remember { mutableStateOf(true) }
                var selectedRoute by remember { mutableStateOf("Dashboard") }
                var selectedEquipamentoId by rememberSaveable { mutableStateOf<String?>(null) }

                if (!isLoggedIn) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            isLoggedIn = true
                            selectedRoute = "Dashboard"
                        }
                    )
                } else {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            WattVisionBottomBar(
                                selectedRoute = selectedRoute,
                                onRouteSelected = { route ->
                                    selectedRoute = route
                                    selectedEquipamentoId = null // Reseta navegação interna ao trocar de aba principal
                                }
                            )
                        }
                    ) { innerPadding ->
                        val contentModifier = Modifier.padding(innerPadding)
                        when (selectedRoute) {
                            "Dashboard" -> {
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigate = { route -> selectedRoute = route },
                                    modifier = contentModifier
                                )
                            }
                            "Operacional" -> {
                                if (selectedEquipamentoId != null) {
                                    EquipamentoDetalheScreen(
                                        viewModel = equipamentoDetalheViewModel,
                                        onBackClick = { selectedEquipamentoId = null },
                                        modifier = contentModifier
                                    )
                                } else {
                                    OperacionalScreen(
                                        viewModel = operacionalViewModel,
                                        onEquipamentoClick = { id ->
                                            equipamentoDetalheViewModel.setEquipamentoId(id)
                                            selectedEquipamentoId = id
                                        },
                                        modifier = contentModifier
                                    )
                                }
                            }
                            else -> {
                                PlaceholderScreen(
                                    title = selectedRoute,
                                    onBackToDashboard = { selectedRoute = "Dashboard" },
                                    modifier = contentModifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
