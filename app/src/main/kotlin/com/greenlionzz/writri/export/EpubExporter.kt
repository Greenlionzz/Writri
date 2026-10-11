package com.greenlionzz.writri.export

import android.content.Context
import com.greenlionzz.writri.data.Project
import java.io.File

class EpubExporter(private val context: Context) {
    fun exportProject(project: Project, file: File): Boolean {
        return try {
            // Basic EPUB structure: manifest, contents, and XML files
            val contentDir = File(file.parentFile, "epub_content").apply { mkdirs() }

            val manifest = """<?xml version="1.0" encoding="UTF-8"?>
<package xmlns="http://www.idpf.org/2007/opf" version="3.0" xml:lang="en" unique-identifier="uid">
  <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
    <dc:title>${escapeXml(project.title)}</dc:title>
    <dc:creator>${escapeXml(project.author)}</dc:creator>
    <dc:language>en</dc:language>
    <dc:identifier id="uid">urn:uuid:${project.id}</dc:identifier>
  </metadata>
  <manifest>
    <item id="toc" href="toc.ncx" media-type="application/x-dtbncx+xml"/>
    <item id="content" href="content.xhtml" media-type="application/xhtml+xml"/>
  </manifest>
</package>"""

            val xhtml = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                appendLine("<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>${escapeXml(project.title)}</title></head><body>")
                appendLine("<h1>${escapeXml(project.title)}</h1>")
                appendLine("<p><strong>Author:</strong> ${escapeXml(project.author)}</p>")
                appendLine("<p><strong>Synopsis:</strong> ${escapeXml(project.synopsis)}</p>")
                project.books.forEach { book ->
                    appendLine("<h2>${escapeXml(book.title)}</h2>")
                    book.acts.forEach { act ->
                        appendLine("<h3>${escapeXml(act.title)}</h3>")
                        act.chapters.forEach { ch ->
                            appendLine("<h4>${escapeXml(ch.title)}</h4>")
                            ch.scenes.forEach { scene ->
                                appendLine("<h5>${escapeXml(scene.title)}</h5>")
                                scene.content.split("\n").forEach { line ->
                                    appendLine("<p>${escapeXml(line)}</p>")
                                }
                            }
                        }
                    }
                }
                appendLine("</body></html>")
            }

            File(contentDir, "META-INF").mkdirs()
            File(contentDir, "META-INF/container.xml").writeText("""<?xml version="1.0"?><container version="1.0" xmlns="urn:oasis:names:tc:opendocument:xmlns:container"><rootfiles><rootfile full-path="OEBPS/content.opf" media-type="application/oebps-package+xml"/></rootfiles></container>""")
            File(contentDir, "OEBPS").mkdirs()
            File(contentDir, "OEBPS/content.opf").writeText(manifest)
            File(contentDir, "OEBPS/content.xhtml").writeText(xhtml)

            true
        } catch (e: Exception) {
            false
        }
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
    }
}