package com.amarildo.spotifyfilter.service

import se.michaelthelin.spotify.model_objects.specification.User

data class TrackState(
    val tracks: List<User> = emptyList(),
) {
}
