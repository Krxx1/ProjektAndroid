package com.example.lab3sem2.presentation.Database

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DatabaseScreen(
    imie: String,
    onImieChange: (String) -> Unit,
    indeks: String,
    onIndeksChange: (String) -> Unit,
    wydzial: String,
    onWydzialChange: (String) -> Unit,
    onGoBack: () -> Unit,
    onSave: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onSave) {
            Text(text = "Save record")
        }
        Button(onClick = onGoBack) {
            Text(text = "Go back")
        }
        TextField(
            value = imie,
            label = {
                androidx.compose.material3.Text("Wprowadź imie")
            },
            onValueChange = onImieChange)
        TextField(
            value = indeks,
            label = {
                androidx.compose.material3.Text("Wprowadź indeks")
            },
            onValueChange = onIndeksChange)
        TextField(
            value = wydzial,
            label = {
                androidx.compose.material3.Text("Wprowadź wydzial")
            },
            onValueChange = onWydzialChange)
    }

}
