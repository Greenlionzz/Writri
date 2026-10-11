package com.greenlionzz.writri.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greenlionzz.writri.data.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectList(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit,
    onDelete: (Project) -> Unit,
    modifier: Modifier = Modifier
) {
    if (projects.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No projects yet", style = MaterialTheme.typography.headlineSmall)
                Text("Create your first writing project", style = MaterialTheme.typography.bodyLarge)
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(projects) { project ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onProjectClick(project) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    project.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (project.subtitle.isNotBlank()) {
                                    Text(project.subtitle, style = MaterialTheme.typography.bodyMedium)
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (project.genre.isNotBlank()) {
                                        Text("Genre: ${project.genre}", style = MaterialTheme.typography.labelMedium)
                                    }
                                    Text(
                                        "Words: ${calculateTotalWords(project)}",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            IconButton(onClick = { onDelete(project) }) {
                                Text("🗑")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateTotalWords(project: Project): Int {
    return project.books.sumOf { book ->
        book.acts.sumOf { act ->
            act.chapters.sumOf { chapter ->
                chapter.scenes.sumOf { it.wordCount }
            }
        }
    }
}