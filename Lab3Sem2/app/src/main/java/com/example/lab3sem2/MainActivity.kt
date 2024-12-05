package com.example.lab3sem2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab3sem2.presentation.Database.DatabaseScreen
import com.example.lab3sem2.presentation.profile.ProfileScreen
import com.example.lab3sem2.presentation.sign_in.GoogleAuthUiClient
import com.example.lab3sem2.presentation.sign_in.SignInScreen
import com.example.lab3sem2.presentation.sign_in.SignInViewModel
import com.example.lab3sem2.ui.theme.Lab3Sem2Theme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab3Sem2Theme{
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val navController = rememberNavController()
                    val database = Firebase.database("Database link").reference
                    //val myRef = database.getReference("Users")

                    var imie by remember {
                        mutableStateOf("")
                    }
                    var indeks by remember {
                        mutableStateOf("")
                    }
                    var wydzial by remember {
                        mutableStateOf("")
                    }



                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if (state.isSignInSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Sign in successful",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(ProfileScr)
                            viewModel.resetState()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = SignIn
                    ){
                        composable<SignIn> {
                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable<ProfileScr> {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    navController.navigate(SignIn)
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()


                                    }
                                },
                                onDbPage = {
                                    lifecycleScope.launch {
                                        navController.navigate(DatabaseScreen)
                                    }
                                }
                            )
                        }
                        composable<DatabaseScreen> {

                            DatabaseScreen(
                                imie = imie,
                                onImieChange = { imie = it },
                                indeks = indeks,
                                onIndeksChange = { indeks = it },
                                wydzial = wydzial,
                                onWydzialChange = { wydzial = it },
                                onGoBack = {
                                    lifecycleScope.launch {
                                        navController.popBackStack()
                                    }
                                },
                                onSave = {
                                    lifecycleScope.launch {
                                        val user = User(indeks, wydzial)
                                        database.child("Users").child(imie).setValue(user)
                                    }
                                }
                            )
                        }

                    }

                }
            }
        }
    }
}

@Serializable
object SignIn

@Serializable
object ProfileScr

@Serializable
object DatabaseScreen

data class User(val indeks: String? = null, val wydzial: String? = null) {
}