package com.greenlionzz.writri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenlionzz.writri.data.Project
import com.greenlionzz.writri.data.ProjectRepository
import com.greenlionzz.writri.ui.components.AIPanel
import com.greenlionzz.writri.ui.components.ManuscriptTree
import com.greenlionzz.writri.ui.components.SceneEditor
import com.greenlionzz.writri.ui.components.ReferencePanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManuscriptScreen(
    project: Project,
    repository: ProjectRepository,
    onBack: () -> Unit
) {
    var selectedSceneId by remember { mutableStateOf<String?>(null) }
    var showReferencePanel by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) }

    val selectedScene = remember(project, selectedSceneId) {
        project.books.flatMap { it.acts }.flatMap { it.chapters }.flatMap { it.scenes }
            .find { it.id == selectedSceneId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project.title) },
                navigationIcon = {
                    IconButton(onBack) {
                        Text("←")
                    }
                },
                actions = {
                    IconButton({ showReferencePanel = !showReferencePanel }) {
                        Text(if (showReferencePanel) "✗" else "≡")
                    }
                }
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                TabRow(selectedTabPosition = currentTab) {
                    Tab(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        text = { Text("Outline") }
                    )
                    Tab(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        text = { Text("Editor") }
                    )
                }
                when (currentTab) {
                    0 -> ManuscriptTree(
                        project = project,
                        onSceneSelect = { selectedSceneId = it },
                        selectedSceneId = selectedSceneId,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> selectedScene?.let { scene ->
                        SceneEditor(
                            scene = scene,
                            project = project,
                            repository = repository,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("Select a scene to start writing", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            if (showReferencePanel) {
                ReferencePanel(
                    project = project,
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                )
            }

            // AI Panel wired in
            AIPanel(
                project = project,
                scene = selectedScene,
                apiKey = project.settings.geminiApiKey,
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
            )
        }
    }
}