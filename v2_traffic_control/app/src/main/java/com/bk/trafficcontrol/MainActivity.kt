package com.bk.trafficcontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.bk.trafficcontrol.ui.theme.TrafficControlV2Theme
import com.bk.trafficcontrol.ui.TrafficControlApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrafficControlV2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TrafficControlApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
