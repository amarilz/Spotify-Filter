package com.amarildo.spotifyfilter;

import com.amarildo.spotifyfilter.data.LocalTrack;
import com.amarildo.spotifyfilter.data.SpotifyTrackMapper;
import com.amarildo.spotifyfilter.db.FileStorageRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PlaylistSyncRunner implements CommandLineRunner {

    private final SpotifyTrackMapper mapper;
    private final SpotifyApi spotifyApi;
    private final FileStorageRepository fileStorageRepository;

    @Value("${spotify.playlistId.block}")
    private String blockPlaylistId;

    @Value("${spotify.playlistId.listen}")
    private String listenPlaylistId;

    @Autowired
    public PlaylistSyncRunner(SpotifyTrackMapper mapper, SpotifyApi spotifyApi, FileStorageRepository fileStorageRepository) {
        this.mapper = mapper;
        this.spotifyApi = spotifyApi;
        this.fileStorageRepository = fileStorageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Service start...");

        Set<LocalTrack> blockLocalTracks = fetchAllTracksFromPlaylist(blockPlaylistId);
        Set<LocalTrack> listenedLocalTracks = fileStorageRepository.loadListenedSongs();
        Set<LocalTrack> newLocalTracks = getNewTracks(blockLocalTracks, listenedLocalTracks);

        if (newLocalTracks.isEmpty()) {
            log.info("No new songs found");
            return;
        }

        try {
            addTracksToPlaylist(listenPlaylistId, newLocalTracks);
        } catch (Exception e) {
            log.error("Error adding songs to playlist", e);
            throw e;
        }

        fileStorageRepository.backupSongs(listenedLocalTracks);
        listenedLocalTracks.addAll(newLocalTracks);
        fileStorageRepository.saveAllSongs(listenedLocalTracks);
    }

    private Set<LocalTrack> getNewTracks(Set<LocalTrack> source, Set<LocalTrack> known) {
        return source.stream()
                .filter(track -> !known.contains(track))
                .collect(Collectors.toSet());
    }

    private void addTracksToPlaylist(String playlistId, @NotNull Set<LocalTrack> localTracks) throws IOException, SpotifyWebApiException, org.apache.hc.core5.http.ParseException {
        final int BATCH_SIZE = 100;
        List<String> uris = localTracks.stream()
                .map(LocalTrack::getSpotifySongUri)
                .toList();

        log.info("Total tracks to add: {}", uris.size());
        for (int i = 0; i < uris.size(); i += BATCH_SIZE) {
            List<String> batch = uris.subList(i, Math.min(i + BATCH_SIZE, uris.size()));
            JsonArray jsonArray = new JsonArray();
            batch.forEach(uri -> jsonArray.add(new JsonPrimitive(uri)));

            log.info("Adding batches: [{}, {}]", i, i + batch.size() - 1);
            spotifyApi.addItemsToPlaylist(playlistId, jsonArray).build().execute();
        }
        log.info("All tracks have been added to the playlist");
    }

    @NotNull
    private Set<LocalTrack> fetchAllTracksFromPlaylist(String playlistId) {
        Set<LocalTrack> result = new HashSet<>();
        int offset = 0;

        log.info("I start fetching tracks from the playlist: {}", playlistId);
        while (true) {
            log.info("Get Tracks with offset: {}", offset);
            Paging<PlaylistTrack> page = getPlaylistTracks(playlistId, offset);
            if (page == null || page.getItems() == null || page.getItems().length == 0) break;

            int itemsBefore = result.size();

            Arrays.stream(page.getItems())
                    .map(PlaylistTrack::getTrack)
                    .filter(Track.class::isInstance)
                    .map(track -> mapper.toTrack((Track) track))
                    .forEach(result::add);

            int itemsAfter = result.size();
            log.info("Added {} new tracks. Total so far: {}", itemsAfter - itemsBefore, itemsAfter);

            offset += page.getItems().length;
            if (offset >= page.getTotal()) break;
        }

        log.info("Total tracks recovered: {}", result.size());
        return result;
    }

    @Nullable
    private Paging<PlaylistTrack> getPlaylistTracks(String playlistId, int offset) {
        try {
            return spotifyApi.getPlaylistsItems(playlistId)
                    .offset(offset)
                    .build()
                    .execute();
        } catch (Exception e) {
            log.error("Error retrieving songs from Spotify: {}", e.getMessage(), e);
            return null;
        }
    }
}
