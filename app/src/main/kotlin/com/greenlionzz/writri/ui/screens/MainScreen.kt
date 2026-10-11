package com.greenlionzz.writri.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greenlionzz.writri.data.Project
import com.greenlionzz.writri.data.ProjectRepository
import com.greenlionzz.writri.ui.components.ProjectList
import com.greenlionzz.writri.ui.components.ProjectDialog
import com.greenlionzz.writri.ui.screens.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: ProjectRepository) {
    val projects by repository.projects.collectAsState()
    val currentProject by repository.currentProject.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(selectedProject) {
        selectedProject?.let { repository.setCurrentProject(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WriteAI") },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Text("+")
            }
        }
    ) { padding ->
        if (currentProject != null) {
            ManuscriptScreen(
                project = currentProject!!,
                repository = repository,
                onBack = { repository.setCurrentProject(null) }
            )
        } else {
            ProjectList(
                projects = projects,
                onProjectClick = { selectedProject = it },
                onDelete = { repository.deleteProject(it.id) },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
    }

    if (showCreateDialog) {
        ProjectDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, genre ->
                val newProject = repository.createNewProject(title, genre)
                repository.saveProject(newProject)
                showCreateDialog = false
            }
        )
    }

    if (showSettings) {
        val currentProject = repository.getCurrentProject()
        SettingsScreen(
            settings = currentProject?.settings ?: com.greenlionzz.writri.data.ProjectSettings(),
            onSave = { settings ->
                currentProject?.let {
                    repository.saveProject(it.copy(settings = settings))
                }
                showSettings = false
            },
            onBack = { showSettings = false }
        )
    }
}