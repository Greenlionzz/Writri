package com.greenlionzz.writri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.greenlionzz.writri.data.ProjectSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: ProjectSettings,
    onSave: (ProjectSettings) -> Unit,
    onBack: () -> Unit
) {
    var theme by remember { mutableStateOf(settings.theme) }
    var fontFamily by remember { mutableStateOf(settings.fontFamily) }
    var fontSize by remember { mutableStateOf(settings.fontSize) }
    var spellcheck by remember { mutableStateOf(settings.spellcheck) }
    var dailyGoalEnabled by remember { mutableStateOf(settings.dailyGoalEnabled) }
    var geminiApiKey by remember { mutableStateOf(settings.geminiApiKey) }
    var geminiModel by remember { mutableStateOf(settings.geminiModel) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onBack) { Text("←") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                Text("Theme", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("dark", "light", "sepia").forEach { t ->
                        FilterChip(
                            selected = theme == t,
                            onClick = { theme = t },
                            label = { Text(t) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Font Size: ${fontSize}sp", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = fontSize.toFloat(),
                    onValueChange = { fontSize = it.toInt() },
                    valueRange = 12f..32f
                )
            }

            item {
                Text("Writing", style = MaterialTheme.typography.titleMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Spellcheck", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = spellcheck,
                        onCheckedChange = { spellcheck = it }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Daily Goal", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = dailyGoalEnabled,
                        onCheckedChange = { dailyGoalEnabled = it }
                    )
                }
            }

            item {
                Text("AI (Gemini)", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = geminiApiKey,
                    onValueChange = { geminiApiKey = it },
                    label = { Text("API Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = geminiModel,
                    onValueChange = { geminiModel = it },
                    label = { Text("Model") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("gemini-2.0-flash") }
                )
            }

            item {
                Button(
                    onClick = {
                        onSave(
                            settings.copy(
                                theme = theme,
                                fontFamily = fontFamily,
                                fontSize = fontSize,
                                spellcheck = spellcheck,
                                dailyGoalEnabled = dailyGoalEnabled,
                                geminiApiKey = geminiApiKey,
                                geminiModel = geminiModel
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }
        }
    }
}