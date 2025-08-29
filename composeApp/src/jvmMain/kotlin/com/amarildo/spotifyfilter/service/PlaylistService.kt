package com.amarildo.spotifyfilter.service

import com.amarildo.spotifyfilter.data.model.LocalTrack
import com.amarildo.spotifyfilter.data.model.toLocalTrack
import com.amarildo.spotifyfilter.data.repository.FileStorageRepository
import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.math.min
import se.michaelthelin.spotify.SpotifyApi
import se.michaelthelin.spotify.model_objects.specification.Paging
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack
import se.michaelthelin.spotify.model_objects.specification.Track

private val log = KotlinLogging.logger {}

class PlaylistService(
    private val spotifyApi: SpotifyApi,
    private val fileStorageRepository: FileStorageRepository
) {

    fun run(
        blockPlaylistId: String,
        listenPlaylistId: String,
    ) {
        log.info { "Service start..." }

        val blockLocalTracks: MutableSet<LocalTrack> = fetchAllTracksFromPlaylist(blockPlaylistId)
        val listenedLocalTracks: MutableSet<LocalTrack> = fileStorageRepository.loadListenedSongs()
        val newLocalTracks: MutableSet<LocalTrack> = getNewTracks(blockLocalTracks, listenedLocalTracks)

        if (newLocalTracks.isEmpty()) {
            log.info { "No new songs found" }
            return
        }

        try {
            addTracksToPlaylist(listenPlaylistId, newLocalTracks)
        } catch (ex: Exception) {
            log.error { "Error adding songs to playlist" }
            throw ex
        }

        fileStorageRepository.backupSongs(listenedLocalTracks)
        listenedLocalTracks.addAll(newLocalTracks)
        fileStorageRepository.saveAllSongs(listenedLocalTracks)
    }

    private fun fetchAllTracksFromPlaylist(playlistId: String): MutableSet<LocalTrack> {
        val result: MutableSet<LocalTrack> = mutableSetOf()
        var offset = 0

        log.info { "I start fetching tracks from the playlist: $playlistId" }
        while (true) {
            log.info { "Get tracks with offset: $offset" }
            val page: Paging<PlaylistTrack>? = getPlaylistTracks(playlistId, offset)
            if (page == null || page.items == null || page.items.size == 0)
                break;

            val itemsBefore: Int = result.size

            page.items.asSequence()
                .map { it.track }
                .filter { t -> t is Track }
                .map { t -> (t as Track).toLocalTrack() }
                .forEach { result.add(it) }

            val itemAfter: Int = result.size
            log.info { "Added ${itemAfter - itemsBefore} new tracks. Total so far: $itemAfter" }

            offset += page.items.size
            if (offset >= page.total)
                break
        }
        log.info { "Total tracks recovered: ${result.size}" }
        return result
    }

    private fun getPlaylistTracks(playlistId: String, offset: Int): Paging<PlaylistTrack>? {
        try {
            return spotifyApi.getPlaylistsItems(playlistId)
                .offset(offset)
                .build()
                .execute();
        } catch (ex: Exception) {
            log.error { "Error retrieving songs from Spotify: ${ex.message}" }
            return null;
        }
    }


    private fun getNewTracks(
        blockLocalTracks: MutableSet<LocalTrack>,
        listenedLocalTracks: MutableSet<LocalTrack>
    ): MutableSet<LocalTrack> {
        return blockLocalTracks.asSequence()
            .filter { track -> !listenedLocalTracks.contains(track) }
            .toSet() as MutableSet<LocalTrack>;
    }

    private fun addTracksToPlaylist(
        playlistId: String,
        localTracks: MutableSet<LocalTrack>
    ) {
        val batchSize = 100
        val uris: List<String> = localTracks.asSequence()
            .map { it.spotifySongUri }
            .toList()

        log.info { "Total tracks to add: ${uris.size}" }
        for (i in 0 until uris.size step batchSize) {
            val batch: List<String> = uris.subList(i, min(i + batchSize, uris.size))
            val jsonArray: JsonArray = JsonArray()
            batch.forEach { uri -> jsonArray.add(JsonPrimitive(uri)) }

            log.info { "Adding batches: [$i, ${i + batch.size - 1}]" }
            spotifyApi.addItemsToPlaylist(playlistId, jsonArray)
                .build()
                .execute()
        }
        log.info { "All tracks have been added to the playlist" }
    }
}
