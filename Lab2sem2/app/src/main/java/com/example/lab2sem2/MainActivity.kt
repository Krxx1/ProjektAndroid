package com.example.lab2sem2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab2sem2.ui.theme.Lab2sem2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ViewModel to handle sensor data
class PoziomicaViewModel(context: Context) : ViewModel(), SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightmeter = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    var xTilt by mutableStateOf(0f)
    var yTilt by mutableStateOf(0f)
    var light by mutableStateOf(0f)

    init {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, lightmeter, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT){
            light = event.values[0]
        }
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            xTilt = event.values[0]
            yTilt = event.values[1]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}

// Factory for ViewModel
class PoziomicaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PoziomicaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PoziomicaViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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
            Lab2sem2Theme {
                val viewModel: PoziomicaViewModel = viewModel(factory = PoziomicaViewModelFactory(applicationContext))
                PoziomicaScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PoziomicaScreen(viewModel: PoziomicaViewModel) {
    val xTilt = viewModel.xTilt
    val yTilt = viewModel.yTilt
    val light = viewModel.light

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "X Tilt: %.2f°".format(xTilt*9), style = MaterialTheme.typography.bodyLarge)
        FillBar(tilt = xTilt)

        Text(text = "Y Tilt: %.2f°".format(yTilt*9), style = MaterialTheme.typography.bodyLarge)
        FillBar(tilt = yTilt)

        Text(text = "Light level: %.2f".format(light), style = MaterialTheme.typography.bodyLarge)
        LightBar(lightLevel = light)
    }
}

@Composable
fun FillBar(tilt: Float) {
    val barWidth = ((tilt / 10f).coerceIn(-1f, 1f) + 1) / 2f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(barWidth)
                .background(Color.Green, shape = RoundedCornerShape(15.dp))
        )
    }
}

@Composable
fun LightBar(lightLevel: Float) {
    val barWidth = (lightLevel/5000f).coerceIn(0f, 1f) // Normalize tilt between 0 and 1
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(barWidth)
                .background(Color.Green, shape = RoundedCornerShape(15.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Lab2sem2Theme {
        PoziomicaScreen(viewModel = PoziomicaViewModel(context = MainActivity().applicationContext))
    }
}
