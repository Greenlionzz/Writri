package com.greenlionzz.writri.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greenlionzz.writri.data.Project
import com.greenlionzz.writri.data.ProjectRepository
import com.greenlionzz.writri.data.Scene

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneEditor(
    scene: Scene,
    project: Project,
    repository: ProjectRepository,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf(scene.content) }
    var showFormatting by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(true) }
    var showRevisions by remember { mutableStateOf(false) }

    val wordCount = remember(content) { content.split("\\s+".toRegex()).filter { it.isNotBlank() }.size }
    val charCount = remember(content) { content.length }

    Scaffold(
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Stats bar
            if (showStats) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                scene.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "Scene ${scene.id}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$wordCount / ${scene.targetWords}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "$charCount chars",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            // Editor
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            scene.content = it
                            scene.wordCount = wordCount
                        },
                        modifier = Modifier.fillMaxSize(),
                        placeholder = { Text("Start writing...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = Int.MAX_VALUE
                    )
                }
            }

            // AI quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizonta = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Proofread") }
                FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Tighten") }
                FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Continue") }
                FilledTonalButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Style") }
            }

            // Formatting toolbar
            if (showFormatting) {
                FormattingToolbar()
            }
        }
    }
}

@Composable
fun FormattingToolbar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {}) { Text("H1") }
            IconButton(onClick = {}) { Text("H2") }
            IconButton(onClick = {}) { Text("B") }
            IconButton(onClick = {}) { Text("I") }
            IconButton(onClick = {}) { Text("•") }
            IconButton(onClick = {}) { Text("»") }
            IconButton(onClick = {}) { Text("¶") }
        }
    }
}