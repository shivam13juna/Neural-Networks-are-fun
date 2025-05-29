package com.bk.trafficcontrol.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Audio Settings
        SettingsSection(title = "Audio Settings") {
            // Master Volume
            Text(
                text = "Master Volume: ${(uiState.masterVolume * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = uiState.masterVolume,
                onValueChange = viewModel::updateMasterVolume,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Clip Length Threshold
            OutlinedTextField(
                value = uiState.clipLengthThreshold.toString(),
                onValueChange = { value ->
                    value.toIntOrNull()?.let { viewModel.updateClipLengthThreshold(it) }
                },
                label = { Text("Clip Length Threshold (seconds)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // System Settings
        SettingsSection(title = "System Settings") {
            // Start on Boot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Start on Boot",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Automatically start scheduling after device restart",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = uiState.startOnBoot,
                    onCheckedChange = viewModel::updateStartOnBoot
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data Management
        SettingsSection(title = "Data Management") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::exportData,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Export Data")
                }
                OutlinedButton(
                    onClick = viewModel::importData,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Import Data")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Information
        SettingsSection(title = "App Information") {
            Text(
                text = "Version: ${uiState.appVersion}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Build: ${uiState.buildNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
