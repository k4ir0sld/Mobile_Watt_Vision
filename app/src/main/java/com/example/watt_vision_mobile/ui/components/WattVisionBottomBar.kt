package com.example.watt_vision_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.watt_vision_mobile.ui.theme.*

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

val wattVisionBottomNavItems = listOf(
    BottomNavItem("Dashboard", Icons.Default.Dashboard),
    BottomNavItem("Operacional", Icons.Default.Memory),
    BottomNavItem("Medições", Icons.Default.Timeline),
    BottomNavItem("Conectividade", Icons.Default.Wifi),
    BottomNavItem("Relatórios", Icons.Default.Description)
)

@Composable
fun WattVisionBottomBar(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .height(64.dp)
            .background(InputBackground),
        containerColor = InputBackground,
        tonalElevation = 8.dp
    ) {
        wattVisionBottomNavItems.forEach { item ->
            val isSelected = selectedRoute == item.title

            NavigationBarItem(
                selected = isSelected,
                onClick = { onRouteSelected(item.title) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextWhite,
                    unselectedIconColor = TextMuted,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.3f)
                )
            )
        }
    }
}
