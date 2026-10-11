package com.greenlionzz.writri.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.greenlionzz.writri.data.Project

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManuscriptTree(
    project: Project,
    onSceneSelect: (String) -> Unit,
    selectedSceneId: String?,
    modifier: Modifier = Modifier
) {
    val allScenes = remember(project) {
        project.books.flatMap { book ->
            book.acts.flatMap { act ->
                act.chapters.flatMap { chapter ->
                    chapter.scenes.map { scene ->
                        Triple(book, Triple(act, chapter), scene)
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(allScenes) { (book, actChapter, scene) ->
            val (act, chapter) = actChapter
            val isSelected = scene.id == selectedSceneId
            val statusColor = when (scene.status) {
                "draft" -> Color(0xFF9E9E9E)
                "in_progress" -> Color(0xFFFF9800)
                "revised" -> Color(0xFF2196F3)
                "final" -> Color(0xFF4CAF50)
                else -> Color(0xFF9E9E9E)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                onClick = { onSceneSelect(scene.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "${book.title} › ${act.title} › ${chapter.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            scene.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(statusColor, RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${scene.wordCount}/${scene.targetWords}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    if (scene.synopsis.isNotBlank()) {
                        Text(
                            scene.synopsis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}