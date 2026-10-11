package com.greenlionzz.writri.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProjectRepository(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
    private val projectsDir = File(context.filesDir, "projects").apply { mkdirs() }
    private val backupDir = File(context.filesDir, "backups").apply { mkdirs() }

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _currentProject = MutableStateFlow<Project?>(null)
    val currentProject: StateFlow<Project?> = _currentProject.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        val list = projectsDir.listFiles { _, name -> name.endsWith(".json") }
            ?.mapNotNull { file ->
                try {
                    json.decodeFromString<Project>(file.readText())
                } catch (e: Exception) {
                    null
                }
            }?.sortedBy { it.title }
            ?: emptyList()
        _projects.value = list
    }

    suspend fun saveProject(project: Project) {
        val file = File(projectsDir, "${project.id}.json")
        file.writeText(json.encodeToString(project))
        val updated = _projects.value.toMutableList().apply {
            val idx = indexOfFirst { it.id == project.id }
            if (idx >= 0) set(idx, project) else add(project)
        }
        _projects.value = updated.sortedBy { it.title }
        _currentProject.value = project
    }

    suspend fun deleteProject(projectId: String) {
        File(projectsDir, "${projectId}.json").delete()
        _projects.value = _projects.value.filter { it.id != projectId }
        if (_currentProject.value?.id == projectId) {
            _currentProject.value = null
        }
    }

    suspend fun exportProjectBackup(project: Project): File {
        val file = File(backupDir, "backup_${project.id}_${System.currentTimeMillis()}.json")
        file.writeText(json.encodeToString(project))
        return file
    }

    suspend fun importProjectBackup(file: File): Project {
        val project = json.decodeFromString<Project>(file.readText())
        saveProject(project)
        return project
    }

    fun createNewProject(title: String, genre: String = ""): Project {
        return Project(
            id = "proj_${System.currentTimeMillis()}",
            title = title,
            genre = genre,
            books = mutableListOf(
                Book(id = "book_1", title = "Book 1", acts = mutableListOf(
                    Act(id = "act_1", title = "Act I", chapters = mutableListOf(
                        Chapter(id = "ch_1", title = "Chapter 1", scenes = mutableListOf(
                            Scene(id = "scene_1", title = "Scene 1", content = "")
                        ))
                    ))
                ))
            )
        )
    }

    fun getCurrentProject(): Project? = _currentProject.value
    fun setCurrentProject(project: Project?) { _currentProject.value = project }
}