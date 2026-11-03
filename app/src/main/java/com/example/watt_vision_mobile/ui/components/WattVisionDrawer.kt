package com.example.watt_vision_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watt_vision_mobile.R
import com.example.watt_vision_mobile.ui.theme.*

data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

val wattVisionNavigationItems = listOf(
    NavigationItem("Dashboard", Icons.Default.Dashboard),
    NavigationItem("Operacional", Icons.Default.Memory),
    NavigationItem("Medições", Icons.Default.Timeline),
    NavigationItem("Alarmes", Icons.Default.Notifications),
    NavigationItem("Conectividade", Icons.Default.Wifi),
    NavigationItem("Configurações", Icons.Default.Settings),
    NavigationItem("Relatórios", Icons.Default.Description)
)

@Composable
fun WattVisionDrawerContent(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp),
        drawerContainerColor = DarkBackground,
        drawerContentColor = TextWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Cabeçalho da Sidebar com Logo Watt Vision
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Watt Vision",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Lista de Itens de Navegação
                wattVisionNavigationItems.forEach { item ->
                    val isSelected = selectedRoute == item.title

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextMuted
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) TextWhite else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        selected = isSelected,
                        onClick = { onRouteSelected(item.title) },
                        modifier = Modifier.padding(vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PrimaryBlue,
                            unselectedContainerColor = InputBackground.copy(alpha = 0.5f),
                            selectedIconColor = TextWhite,
                            unselectedIconColor = TextMuted,
                            selectedTextColor = TextWhite,
                            unselectedTextColor = TextMuted
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Rodapé com botão Sair (Logout)
            Column {
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "Sair",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ToneRedText
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Sair",
                            tint = ToneRedText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    selected = false,
                    onClick = onLogout,
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = ToneRedBg.copy(alpha = 0.3f),
                        unselectedIconColor = ToneRedText,
                        unselectedTextColor = ToneRedText
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
