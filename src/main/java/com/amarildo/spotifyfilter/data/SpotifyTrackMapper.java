package com.amarildo.spotifyfilter.data;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Mapper(componentModel = "spring")
public interface SpotifyTrackMapper {

    @Mapping(target = "uniqueId", ignore = true)
    @Mapping(target = "firstArtist", expression = "java(track.getArtists()[0].getName())")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "spotifySongUri", source = "uri")
    LocalTrack toTrack(Track track);

    @AfterMapping
    default void setUniqueId(@MappingTarget LocalTrack localTrack, Track trackLib) {
        String uniqueId = (trackLib.getArtists()[0].getName() + trackLib.getName()).replaceAll("\\s+", "");
        localTrack.setUniqueId(uniqueId);
    }
}
