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
fun ReferencePanel(
    project: Project,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            TabRow(selectedTabPosition = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Characters") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Locations") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Plot") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (selectedTab) {
                    0 -> items(project.characters) { character ->
                        CharacterCard(character)
                    }
                    1 -> items(project.locations) { location ->
                        LocationCard(location)
                    }
                    2 -> items(project.plotBeats) { beat ->
                        PlotBeatCard(beat)
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterCard(character: com.greenlionzz.writri.data.Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(character.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (character.role.isNotBlank()) {
                Text(character.role, style = MaterialTheme.typography.labelMedium)
            }
            if (character.archetype.isNotBlank()) {
                Text("Archetype: ${character.archetype}", style = MaterialTheme.typography.labelSmall)
            }
            if (character.traits.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    character.traits.take(3).forEach { trait ->
                        AssistChip(
                            onClick = {},
                            label = { Text(trait, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocationCard(location: com.greenlionzz.writri.data.Location) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(location.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (location.setting.isNotBlank()) {
                Text(location.setting, style = MaterialTheme.typography.labelMedium)
            }
            if (location.sensoryDetails.isNotBlank()) {
                Text(
                    location.sensoryDetails,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun PlotBeatCard(beat: com.greenlionzz.writri.data.PlotBeat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(beat.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            if (beat.act.isNotBlank()) {
                Text("Act: ${beat.act}", style = MaterialTheme.typography.labelMedium)
            }
            if (beat.summary.isNotBlank()) {
                Text(beat.summary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}