package com.amarildo.spotifyfilter.db;

import com.amarildo.spotifyfilter.data.LocalTrack;

import java.io.IOException;
import java.util.Set;

public interface TrackRepository {

    Set<LocalTrack> loadListenedSongs() throws IOException;

    void saveAllSongs(Set<LocalTrack> localTracks) throws IOException;

    void backupSongs(Set<LocalTrack> localTracks) throws IOException;
}
