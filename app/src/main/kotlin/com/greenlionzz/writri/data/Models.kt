package com.greenlionzz.writri.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val author: String = "",
    val genre: String = "",
    val pointOfView: String = "",
    val synopsis: String = "",
    val targetWordCount: Int = 0,
    val dailyGoal: Int = 0,
    val deadline: Long? = null,
    val books: MutableList<Book> = mutableListOf(),
    val characters: MutableList<Character> = mutableListOf(),
    val locations: MutableList<Location> = mutableListOf(),
    val plotBeats: MutableList<PlotBeat> = mutableListOf(),
    val settings: ProjectSettings = ProjectSettings()
)

@Serializable
data class ProjectSettings(
    val theme: String = "dark",
    val fontFamily: String = "default",
    val fontSize: Int = 16,
    val spellcheck: Boolean = true,
    val dailyGoalEnabled: Boolean = false,
    val geminiApiKey: String = "",
    val geminiModel: String = "gemini-2.0-flash"
)

@Serializable
data class Book(
    val id: String,
    val title: String,
    val order: Int = 0,
    val acts: MutableList<Act> = mutableListOf()
)

@Serializable
data class Act(
    val id: String,
    val title: String,
    val order: Int = 0,
    val chapters: MutableList<Chapter> = mutableListOf()
)

@Serializable
data class Chapter(
    val id: String,
    val title: String,
    val order: Int = 0,
    val scenes: MutableList<Scene> = mutableListOf()
)

@Serializable
data class Scene(
    val id: String,
    val title: String,
    val order: Int = 0,
    var content: String = "",
    var status: String = "draft",
    var synopsis: String = "",
    var povCharacterId: String? = null,
    var locationId: String? = null,
    var notes: String = "",
    var targetWords: Int = 0,
    var wordCount: Int = 0,
    val revisions: MutableList<SceneRevision> = mutableListOf(),
    val comments: MutableList<RevisionComment> = mutableListOf()
)

@Serializable
data class SceneRevision(
    val id: String,
    val content: String,
    val timestamp: Long,
    val label: String = ""
)

@Serializable
data class RevisionComment(
    val id: String,
    val text: String,
    val startOffset: Int,
    val endOffset: Int,
    val timestamp: Long
)

@Serializable
data class Character(
    val id: String,
    val name: String,
    val role: String = "",
    val archetype: String = "",
    val appearance: String = "",
    val traits: MutableList<String> = mutableListOf(),
    val motivations: String = "",
    val conflicts: String = "",
    val backstory: String = "",
    val goals: String = "",
    val personalityTags: MutableList<String> = mutableListOf(),
    val imageUrl: String? = null,
    val relationships: MutableList<CharacterRelationship> = mutableListOf()
)

@Serializable
data class CharacterRelationship(
    val characterId: String,
    val relationship: String,
    val notes: String = ""
)

@Serializable
data class Location(
    val id: String,
    val name: String,
    val setting: String = "",
    val sensoryDetails: String = "",
    val lore: String = "",
    val notes: String = ""
)

@Serializable
data class PlotBeat(
    val id: String,
    val title: String,
    val summary: String = "",
    val act: String = "",
    val tags: MutableList<String> = mutableListOf(),
    val status: String = "draft",
    val characterIds: MutableList<String> = mutableListOf(),
    val locationIds: MutableList<String> = mutableListOf(),
    val sceneIds: MutableList<String> = mutableListOf(),
    val order: Int = 0
)

@Serializable
data class WritingSession(
    val date: Long,
    val wordsWritten: Int,
    val sceneId: String? = null
)

@Serializable
data class DailyStats(
    val date: Long,
    val wordsWritten: Int,
    val goalMet: Boolean = false
)