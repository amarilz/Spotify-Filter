package com.amarildo.spotifyfilter.db;

import com.amarildo.spotifyfilter.data.LocalTrack;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileStorageRepository implements TrackRepository {

    @Value("${db.file.path}")
    private String dbFilePath;

    public Set<LocalTrack> loadListenedSongs() throws IOException {
        Path path = Path.of(dbFilePath);
        if (!Files.exists(path)) {
            throw new IllegalStateException("The database file does not exist: %s".formatted(path));
        }

        return Files.readAllLines(path).stream()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(LocalTrack::new)
                .collect(Collectors.toSet());
    }

    public void saveAllSongs(Set<LocalTrack> localTracks) throws IOException {
        writeSongsToFile(localTracks, dbFilePath);
    }

    public void backupSongs(Set<LocalTrack> localTracks) throws IOException {
        writeSongsToFile(localTracks, createBackupFilePath());
    }

    @NotNull
    private String createBackupFilePath() {
        Objects.requireNonNull(dbFilePath, "dbFilePath cannot be null");
        if (!dbFilePath.endsWith(".txt")) {
            throw new IllegalArgumentException("The file path must end with '.txt'");
        }

        int dotIndex = dbFilePath.lastIndexOf('.');
        return dbFilePath.substring(0, dotIndex) + "_backup.txt";
    }

    private void writeSongsToFile(Set<LocalTrack> localTracks, String path) throws IOException {
        List<String> sortedTracks = localTracks.stream()
                .map(LocalTrack::getUniqueId)
                .sorted()
                .toList();

        Files.write(Path.of(path), sortedTracks, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
