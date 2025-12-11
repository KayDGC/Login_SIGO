package com.example.loginsigo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginsigo.data.model.UserResponse
import com.example.loginsigo.di.LoginViewModelFactory
import com.example.loginsigo.ui.login.DashboardScreen
import com.example.loginsigo.ui.login.HistoryScreen
import com.example.loginsigo.ui.login.LoginViewModel
import com.example.loginsigo.ui.login.ProfileScreen
import com.example.loginsigo.ui.theme.UtmGreenPrimary
import com.google.gson.Gson
import kotlinx.coroutines.launch


object Routes {
    const val LOGIN = "login_screen"
    const val DASHBOARD = "dashboard_screen/{userJson}"
    const val PROFILE = "profile_screen/{userJson}"
    const val HISTORY = "history_screen"

    fun dashboard(userJson: String) = "dashboard_screen/$userJson"
    fun profile(userJson: String) = "profile_screen/$userJson"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppScreenEntry()
            }
        }
    }
}

@Composable
fun AppScreenEntry() {
    val appContext = LocalContext.current.applicationContext
    val application = appContext as SigoLoginApplication

    val authRepository = application.container.authRepository
    val factory = LoginViewModelFactory(authRepository)

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            val viewModel: LoginViewModel = viewModel(factory = factory)

            LoginScreen(
                viewModel = viewModel,
                onNavigateToDashboard = { userResponse ->
                    val userJson = Gson().toJson(userResponse)
                    navController.navigate(Routes.dashboard(userJson)) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson")
            val userResponse = Gson().fromJson(userJson, UserResponse::class.java)

            if (userResponse != null) {
                DashboardScreen(
                    user = userResponse,
                    onNavigateToHistory = {
                        navController.navigate(Routes.HISTORY)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.profile(userJson!!))
                    },
                    onLogout = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = Routes.PROFILE,
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson")
            val userResponse = Gson().fromJson(userJson, UserResponse::class.java)

            if (userResponse != null) {
                ProfileScreen(
                    user = userResponse,
                    navController = navController
                )
            }
        }

        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToDashboard: (UserResponse) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    if (uiState.errorMessage != null) {
        Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
    }

    if (uiState.loginSuccess && uiState.user != null) {
        LaunchedEffect(Unit) {
            Toast.makeText(
                context,
                "Bienvenido ${uiState.user?.personFullName}",
                Toast.LENGTH_SHORT
            ).show()
            onNavigateToDashboard(uiState.user!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(R.drawable.logo_utm),
            contentDescription = "Logo UTM",
            modifier = Modifier.height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChange(it.uppercase()) },
            label = { Text("Usuario") },
            singleLine = true,
            enabled = !uiState.isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UtmGreenPrimary,
                focusedLabelColor = UtmGreenPrimary,
                cursorColor = UtmGreenPrimary,
                unfocusedBorderColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !uiState.isLoading,

            visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),

            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordVisible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                    )
                }
            },

            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UtmGreenPrimary,
                focusedLabelColor = UtmGreenPrimary,
                cursorColor = UtmGreenPrimary,
                unfocusedBorderColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = UtmGreenPrimary,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(color = UtmGreenPrimary)
        } else {
            Button(
                onClick = viewModel::login,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UtmGreenPrimary
                ),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
