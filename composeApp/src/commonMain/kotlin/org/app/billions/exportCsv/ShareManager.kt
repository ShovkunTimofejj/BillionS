package org.app.billions.exportCsv

interface ShareManager {
    fun shareTextFile(fileName: String, mimeType: String, content: String)
}

expect val share: ShareManager