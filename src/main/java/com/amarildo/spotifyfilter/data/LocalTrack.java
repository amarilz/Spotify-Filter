package com.amarildo.spotifyfilter.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class LocalTrack {
    private String uniqueId;
    private String firstArtist;
    private String title;
    private String spotifySongUri;

    public LocalTrack(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LocalTrack localTrack = (LocalTrack) o;
        return Objects.equals(uniqueId, localTrack.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uniqueId);
    }

    @Override
    public String toString() {
        return "Track{" +
                "uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
