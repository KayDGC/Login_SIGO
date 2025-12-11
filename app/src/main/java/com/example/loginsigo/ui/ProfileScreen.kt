package com.example.loginsigo.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginsigo.data.model.UserResponse
import com.example.loginsigo.ui.theme.UtmGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserResponse,
    navController: NavController
) {

    var nombre by remember { mutableStateOf(user.personFullName) }
    var curp by remember { mutableStateOf("") }
    var nss by remember { mutableStateOf("") }
    var sexoSelection by remember { mutableStateOf("Hombre") }
    var estadoNacimiento by remember { mutableStateOf("Michoacán de Ocampo") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = user.username.ifBlank { "Perfil" },
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UtmGreenPrimary)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = user.personFullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = user.active,
                    onCheckedChange = null
                )
                Text(text = "Perfil activo")
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            ReadOnlyRow("Perfil", user.profileName)
            ReadOnlyRow("Usuario", user.username)
            ReadOnlyRow("Correo", user.email)
            ReadOnlyRow("Roles", user.roles.joinToString())
            ReadOnlyRow("ID Persona", user.personId.toString())

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Información Personal",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileTextField("Nombre completo", nombre) { nombre = it }
            ProfileTextField("CURP", curp) { curp = it }
            ProfileTextField("Número de Seguridad Social", nss) { nss = it }

            Text("Sexo", fontWeight = FontWeight.Bold)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GenderButton("Hombre", sexoSelection == "Hombre") { sexoSelection = "Hombre" }
                GenderButton("Mujer", sexoSelection == "Mujer") { sexoSelection = "Mujer" }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Estado de nacimiento", fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = estadoNacimiento,
                onValueChange = { estadoNacimiento = it },
                modifier = Modifier.fillMaxWidth(),
                colors = inputColors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    println("Nombre: $nombre")
                    println("CURP: $curp")
                    println("NSS: $nss")
                    println("Sexo: $sexoSelection")
                    println("Estado: $estadoNacimiento")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UtmGreenPrimary)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}



@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = inputColors(),
            shape = RoundedCornerShape(6.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ReadOnlyRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) UtmGreenPrimary else Color(0xFF80CBC4)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.LightGray,
    unfocusedBorderColor = Color.LightGray
)