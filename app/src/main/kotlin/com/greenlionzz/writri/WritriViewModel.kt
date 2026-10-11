package com.greenlionzz.writri

import androidx.compose.runtime.*
import com.greenlionzz.writri.ai.GeminiAIClient
import com.greenlionzz.writri.data.*

class WritriViewModel(private val repository: ProjectRepository, private val apiKey: String) {
    val aiClient: GeminiAIClient = GeminiAIClient(apiKey)

    suspend fun applyAIAction(action: String, scene: Scene?, project: Project): String {
        return when (action) {
            "Proofread" -> aiClient.proofread(scene?.content ?: "")
            "Tighten" -> aiClient.tightenProse(scene?.content ?: "")
            "Continue" -> aiClient.continueScene(scene ?: Scene("", ""), project)
            "Expand" -> aiClient.expandScene(scene ?: Scene("", ""))
            "Summarize" -> aiClient.summarizeScene(scene ?: Scene("", ""))
            "Dialogue" -> aiClient.writeDialogue(scene ?: Scene("", ""), project)
            "Plot Holes" -> aiClient.checkPlotHoles(project)
            "World Build" -> aiClient.buildWorld(project)
            "Suggest Titles" -> aiClient.suggestTitles(project).joinToString("\n")
            else -> "Unknown action"
        }
    }

    fun saveScene(projectId: String, scene: Scene) {
        val p = repository.getCurrentProject() ?: return
        val updated = p.copy(
            books = p.books.map { book ->
                book.copy(acts = book.acts.map { act ->
                    act.copy(chapters = act.chapters.map { chapter ->
                        chapter.copy(scenes = chapter.scenes.map { s ->
                            if (s.id == scene.id) scene else s
                        })
                    })
                })
            }
        )
        repository.saveProject(updated)
    }
}