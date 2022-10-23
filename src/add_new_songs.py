import spotipy
from spotipy import SpotifyException
from spotipy.oauth2 import SpotifyOAuth

SCOPE = 'playlist-modify-public'


def add_songs(sp: spotipy.Spotify, new_song_ids: list, playlist_id: str) -> None:
    """
    Add new songs to specified playlist. Spotify API PUT (REST) is limited to 100 songs at a time.
    :param sp: Spotify client connection
    :param new_song_ids: List of new song ids
    :param playlist_id: Playlist in which to insert the songs
    :return:
    """
    # Check if there are more songs to add
    while len(new_song_ids) != 0:
        sp.playlist_add_items(playlist_id, new_song_ids[0:100])
        new_song_ids = new_song_ids[100:]


def add_new_songs_in_playlist(new_song_ids: list, client_id: str, client_secret: str, playlist_id: str) -> None:
    """
    If there are new songs, add them to the playlist.
    :param new_song_ids: List of new song ids
    :param client_id: id from Spotify App in dashboard
    :param client_secret: id from Spotify App in dashboard
    :param playlist_id: if of the playlist in which to insert the songs
    """
    sp = spotipy.Spotify(auth_manager=SpotifyOAuth(
        client_id=client_id,
        client_secret=client_secret,
        scope=SCOPE,
        redirect_uri='https://www.google.com/'))

    try:
        if len(new_song_ids) > 0:
            add_songs(sp, new_song_ids, playlist_id)
            print("Songs added.")
        else:
            print("There are no songs to add.")
    except SpotifyException:
        print(
            "An error occurred while adding songs to the playlist.\n" +
            "Delete the songs you tried to insert from the database (.json) and try again.\n" +
            "If you don't, these songs will be block but you will not actually hear them.")
