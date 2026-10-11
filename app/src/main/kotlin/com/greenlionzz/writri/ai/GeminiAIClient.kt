package com.greenlionzz.writri.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.greenlionzz.writri.data.*

class GeminiAIClient(private val apiKey: String, private val model: String = "gemini-2.0-flash") {

    private val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

    suspend fun testConnection(): Boolean = withContext(Dispatchers.IO) {
        apiKey.isNotBlank() && apiKey.length > 20
    }

    suspend fun generateOutline(project: Project, structure: String = "three-act"): String = withContext(Dispatchers.IO) {
        val prompt = "Generate a $structure story outline for '${project.title}' (${project.genre}). " +
                "Project synopsis: ${project.synopsis}. Characters: ${project.characters.joinToString { it.name }}. " +
                "Provide beat-by-beat structure with titles and summaries."
        callGemini(prompt)
    }

    suspend fun generateCharacter(
        genre: String,
        role: String,
        concept: String,
        aesthetic: String
    ): String = withContext(Dispatchers.IO) {
        val prompt = "Create a character profile for a $genre story. Role: $role. Concept: $concept. " +
                "Aesthetic: $aesthetic. Include name, archetype, appearance, traits, motivations, conflicts, backstory, goals."
        callGemini(prompt)
    }

    suspend fun proofread(text: String): String = withContext(Dispatchers.IO) {
        val prompt = "Proofread and improve this prose for clarity, grammar, and style. Preserve voice and meaning:\n\n$text"
        callGemini(prompt)
    }

    suspend fun tightenProse(text: String): String = withContext(Dispatchers.IO) {
        val prompt = "Tighten this prose. Remove wordiness, weak verbs, passive voice, clichés. Preserve meaning:\n\n$text"
        callGemini(prompt)
    }

    suspend fun adjustTone(text: String, tone: String): String = withContext(Dispatchers.IO) {
        val prompt = "Adjust the tone of this text to $tone. Preserve plot and dialogue:\n\n$text"
        callGemini(prompt)
    }

    suspend fun continueScene(scene: Scene, project: Project): String = withContext(Dispatchers.IO) {
        val prompt = "Continue this scene from '${scene.title}' in '${project.title}'. " +
                "Current content: ${scene.content}\n\nContinue naturally, maintaining character voices and plot continuity."
        callGemini(prompt)
    }

    suspend fun expandScene(scene: Scene, instructions: String = ""): String = withContext(Dispatchers.IO) {
        val prompt = "Expand this scene '${scene.title}': ${scene.content}\n\nInstructions: $instructions. " +
                "Add sensory detail, dialogue, and pacing without changing plot."
        callGemini(prompt)
    }

    suspend fun summarizeScene(scene: Scene): String = withContext(Dispatchers.IO) {
        val prompt = "Summarize this scene in 2-3 sentences: ${scene.content}"
        callGemini(prompt)
    }

    suspend fun writeDialogue(scene: Scene, project: Project): String = withContext(Dispatchers.IO) {
        val prompt = "Write natural dialogue for '${scene.title}' based on characters: " +
                project.characters.joinToString { it.name + " (${it.role})" } +
                ". Scene context: ${scene.content}"
        callGemini(prompt)
    }

    suspend fun checkPlotHoles(project: Project): String = withContext(Dispatchers.IO) {
        val plotSummary = project.plotBeats.joinToString("\n") {
            "- ${it.title} (${it.act}): ${it.summary}"
        }
        val prompt = "Analyze plot consistency for '${project.title}'. Plot beats:\n$plotSummary\n\n" +
                "Characters: ${project.characters.joinToString { it.name }}. Identify plot holes, inconsistencies, unresolved arcs."
        callGemini(prompt)
    }

    suspend fun buildWorld(project: Project): String = withContext(Dispatchers.IO) {
        val prompt = "Build world details for '${project.title}' (${project.genre}). " +
                "Synopsis: ${project.synopsis}. Locations: ${project.locations.joinToString { it.name }}. " +
                "Suggest lore, rules, history, sensory atmosphere."
        callGemini(prompt)
    }

    suspend fun suggestTitles(project: Project): List<String> = withContext(Dispatchers.IO) {
        val prompt = "Suggest 5 compelling titles for a ${project.genre} story called '${project.title}'. " +
                "Synopsis: ${project.synopsis}. Make them evocative and genre-appropriate."
        val result = callGemini(prompt)
        result.split("\n").map { it.trim().removePrefix("- ").removePrefix("• ").trim() }
            .filter { it.isNotBlank() }
            .take(5)
    }

    suspend fun simplifySentence(sentence: String): List<String> = withContext(Dispatchers.IO) {
        val prompt = "Simplify this sentence in 3 different ways, preserving meaning: $sentence"
        val result = callGemini(prompt)
        result.split("\n").map { it.trim().removePrefix("- ").trim() }
            .filter { it.isNotBlank() }.take(3)
    }

    suspend fun smartParagraph(text: String): String = withContext(Dispatchers.IO) {
        val prompt = "Suggest optimal paragraph breaks for this text to improve pacing and readability. " +
                "Return the text with paragraph breaks marked by [BREAK]. Preserve all words:\n\n$text"
        callGemini(prompt)
    }

    suspend fun proseStyle(text: String): List<String> = withContext(Dispatchers.IO) {
        val prompt = "Analyze this prose for weaknesses: passive voice, weak verbs, clichés, wordiness, purple prose. " +
                "List specific issues with suggestions. Text:\n$text"
        val result = callGemini(prompt)
        result.split("\n").map { it.trim() }.filter { it.isNotBlank() }.take(10)
    }

    // Internal API call — uses pure-Java HTTP on ART (no external library needed for basic POST)
    private suspend fun callGemini(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL(endpoint+"?key=$apiKey")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val body = """
            {"contents":[{"parts":[{"text":"$prompt"}]}],"generationConfig":{"temperature":0.7,"maxOutputTokens":2048}}
            """.trimIndent()

            conn.outputStream.use { os ->
                os.write(body.toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val response = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                // Simple extraction: take first text response
                val textStart = response.indexOf("\"text\":\"")
                if (textStart > 0) {
                    val textEnd = response.indexOf("\"", textStart + 8)
                    if (textEnd > textStart) {
                        return@withContext response.substring(textStart + 8, textEnd)
                            .replace("\\n", "\n")
                            .replace("\\t", "\t")
                            .replace("\\\"", "\"")
                    }
                }
                response
            } else {
                "Error: HTTP $responseCode. Check API key and model name."
            }
        } catch (e: Exception) {
            "Network/API error: ${e.message}. Check internet, key, and endpoint."
        }
    }
}