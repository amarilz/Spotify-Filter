package com.amarildo.spotifyfilter

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker

class FileLocator {
    private val platform = getPlatform()

    suspend fun getDbFile(): String {
        val file = FileKit.openFilePicker()
        return file!!.file.absolutePath
    }
}
