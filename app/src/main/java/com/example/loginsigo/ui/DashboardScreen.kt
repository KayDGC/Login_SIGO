package com.example.loginsigo.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginsigo.data.model.UserResponse
import com.example.loginsigo.ui.theme.UtmGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: UserResponse,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit // Parámetro necesario para el menú
) {
    // Estado para controlar el menú desplegable
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                // --- MENÚ DE HAMBURGUESA (IZQUIERDA) ---
                navigationIcon = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mi Perfil") },
                                onClick = {
                                    showMenu = false
                                    onNavigateToProfile()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                // --- TÍTULO ---
                title = {
                    Column {
                        Text(
                            text = "SIGO UTM",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = user.username.ifBlank { "Alumno UTM" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                // --- ACCESO DIRECTO PERFIL (DERECHA) ---
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UtmGreenPrimary
                )
            )
        }
    ) { paddingValues ->

        // --- AQUÍ ESTÁ LA CORRECCIÓN DEL FONDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // 1. PRIMERO pintamos el fondo blanco completo
                .padding(paddingValues) // 2. LUEGO respetamos el espacio de la barra superior
                .padding(horizontal = 20.dp, vertical = 24.dp) // 3. FINALMENTE margen interno
        ) {

            // BIENVENIDA
            Text(
                text = "Bienvenido",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = user.personFullName.split(" ").firstOrNull() ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TARJETA HISTORIAL
            MenuCardFinal(
                title = "Mi historial académico",
                description = "Estatus, materias y calificaciones.",
                icon = Icons.Default.DateRange,
                iconColor = Color(0xFFE8F5E9),
                iconTint = UtmGreenPrimary,
                onClick = onNavigateToHistory
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Consulta periódicamente tu historial y mantente\n" +
                        "pendiente de tu estatus académico.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // TARJETA PERFIL
            MenuCardFinal(
                title = "Mi perfil",
                description = "Datos personales y de contacto.",
                icon = Icons.Default.Person,
                iconColor = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1E88E5),
                onClick = onNavigateToProfile
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Consulta periódicamente tu historial y mantente\n" +
                        "pendiente de tu estatus académico.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
fun MenuCardFinal(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}