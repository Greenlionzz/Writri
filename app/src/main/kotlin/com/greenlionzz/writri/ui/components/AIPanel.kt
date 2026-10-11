package com.greenlionzz.writri.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenlionzz.writri.ai.GeminiAIClient
import com.greenlionzz.writri.data.Project
import com.greenlionzz.writri.data.Scene

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPanel(
    project: Project,
    scene: Scene?,
    apiKey: String,
    modifier: Modifier = Modifier
) {
    val aiClient = remember(apiKey) { GeminiAIClient(apiKey) }
    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    val actions = listOf(
        "Proofread" to { aiClient.proofread(scene?.content ?: project.synopsis) },
        "Tighten" to { aiClient.tightenProse(scene?.content ?: project.synopsis) },
        "Continue" to { aiClient.continueScene(scene ?: project.scenes.firstOrNull() ?: Scene("",""), project) },
        "Expand" to { aiClient.expandScene(scene ?: Scene("","")) },
        "Summarize" to { aiClient.summarizeScene(scene ?: Scene("","")) },
        "Dialogue" to { aiClient.writeDialogue(scene ?: Scene("",""), project) },
        "Plot Holes" to { aiClient.checkPlotHoles(project) },
        "World Build" to { aiClient.buildWorld(project) },
        "Suggest Titles" to { aiClient.suggestTitles(project) },
        "Style Critique" to { aiClient.proseStyle(scene?.content ?: "") },
        "Simplify Sentence" to { aiClient.simplifySentence(scene?.content?.take(100) ?: "") },
        "Smart Paragraph" to { aiClient.smartParagraph(scene?.content ?: "") }
    )

    Card(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("AI Co-Author (Gemini)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Model: gemini-2.0-flash | Key: ${if (apiKey.isBlank()) "Not set" else "***"}", style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(actions) { (label, action) ->
                    FilledTonalButton(
                        onClick = {
                            isLoading = true
                            showResult = true
                            result = ""

                            // Launch coroutine-like async (simplified for UI demo)
                            result = try {
                                // In real Kotlin: viewModelScope.launch { ... }
                                "AI result for '$label': ${action.invoke()}. Apply to scene or save to project."
                            } catch (e: Exception) {
                                "Error: ${e.message}"
                            }
                            isLoading = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text(label)
                    }
                }
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Result:", style = MaterialTheme.typography.labelMedium)
                        Text(
                            result,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}