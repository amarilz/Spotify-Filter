package com.amarildo.spotifyfilter.viewmodel

import androidx.lifecycle.ViewModel
import com.amarildo.spotifyfilter.data.repository.FileStorageRepository
import com.amarildo.spotifyfilter.service.PlaylistService
import com.amarildo.spotifyfilter.service.PropertiesLoader
import com.amarildo.spotifyfilter.service.SpotifyHandler
import se.michaelthelin.spotify.SpotifyApi

class MyViewModel() : ViewModel() {
    private lateinit var properties: Map<String, String>
    private lateinit var fileStorageRepository: FileStorageRepository
    private var spotifyHandler: SpotifyHandler? = null

    fun selectedConfigurationFile(filePath: String) {
        properties = PropertiesLoader(filePath).load()
    }

    fun selectedDatabaseFile(filePath: String) {
        fileStorageRepository = FileStorageRepository(filePath)
    }

    fun openBrowser() {
        spotifyHandler = SpotifyHandler(
            properties[PropertiesLoader.SPOTIFY_CLIENT_ID],
            properties[PropertiesLoader.SPOTIFY_CLIENT_SECRET],
        )
        spotifyHandler?.openLinkOnBrowser()
    }

    fun finalizePlaylist(browserUrl: String) {
        val tokenApi: SpotifyApi = spotifyHandler?.getTokenApi(browserUrl) ?: throw IllegalStateException()

        val blockPlaylistId: String =
            properties[PropertiesLoader.SPOTIFY_PLAYLIST_BLOCK] ?: throw IllegalStateException()
        val listenPlaylistId: String =
            properties[PropertiesLoader.SPOTIFY_PLAYLIST_LISTEN] ?: throw IllegalStateException()

        PlaylistService(tokenApi, fileStorageRepository)
            .run(
                blockPlaylistId,
                listenPlaylistId
            )
    }
}
