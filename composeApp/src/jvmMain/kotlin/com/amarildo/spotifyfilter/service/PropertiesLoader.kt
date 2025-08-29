package com.amarildo.spotifyfilter.service

import java.io.FileInputStream
import java.util.Properties
import kotlinx.io.IOException

class PropertiesLoader(private val filePath: String) {

    companion object {
        const val SPOTIFY_CLIENT_ID = "spotify.clientId"
        const val SPOTIFY_CLIENT_SECRET = "spotify.clientSecret"
        const val SPOTIFY_PLAYLIST_BLOCK = "spotify.playlistId.block"
        const val SPOTIFY_PLAYLIST_LISTEN = "spotify.playlistId.listen"

        val REQUIRED_KEYS = setOf(
            SPOTIFY_CLIENT_ID,
            SPOTIFY_CLIENT_SECRET,
            SPOTIFY_PLAYLIST_BLOCK,
            SPOTIFY_PLAYLIST_LISTEN,
        )
    }

    fun load(): Map<String, String> {
        val props = Properties()

        try {
            FileInputStream(filePath).use { input ->
                props.load(input)
            }
        } catch (ex: IOException) {
            throw IllegalStateException("Impossibile leggere il file delle properties: $filePath", ex)
        }

        val propsMap = props.stringPropertyNames()
            .associateWith { props.getProperty(it) }

        // Validazione delle chiavi richieste
        val missingKeys = REQUIRED_KEYS.filter { !propsMap.containsKey(it) }
        if (missingKeys.isNotEmpty()) {
            throw IllegalStateException("Mancano le seguenti properties obbligatorie: $missingKeys")
        }

        return propsMap
    }
}
