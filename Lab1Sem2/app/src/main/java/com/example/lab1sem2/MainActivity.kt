package com.example.lab1sem2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.lab1sem2.ui.theme.Lab1Sem2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashScreen }

        lifecycleScope.launch {
            delay(1000)
            keepSplashScreen = false
        }
        setContent {
            Lab1Sem2Theme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = EkranA
                ){
                    composable<EkranA> {
                        var nazwa by remember{
                            mutableStateOf("")
                        }
                        Column (
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            TextField(
                                value = nazwa,
                                label = {
                                    Text("Wprowadź imie")
                                },
                                onValueChange = {nazwa = it})
                            Button(onClick = {
                                navController.navigate(EkranB(
                                    name = nazwa,
                                    age = 21
                                ))
                            }) {
                                Text(text = "Przejdź do ekranu B")
                            }
                        }
                    }
                    composable<EkranB> {
                        val args = it.toRoute<EkranB>()
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if(args.name == ""){
                                Text(text = "Jesteś bezimienny")
                            } else{
                                Text(text = "Masz na imie ${args.name}")
                            }
                            Button(onClick = {
                                navController.navigate(EkranA)
                            }) {
                                Text(text = "Przejdź do ekranu A")
                            }
                        }


                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Lab1Sem2Theme {
        Greeting("tests")
    }
}

@Serializable
object EkranA

@Serializable
data class EkranB(
    val name: String = "",
    val age: Int
)