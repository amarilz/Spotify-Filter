import spotipy
from spotipy.oauth2 import SpotifyClientCredentials


def get_song_in_dictionary(song: dict) -> dict:
    """
    Return a dictionary containing:
    - artist
    - title
    - id
    :param song: Song to get data from
    :return: Dictionary of a single song
    """
    if song is None:
        raise TypeError("Dictionary is None")
    if not song:
        raise TypeError("Dictionary is empty")
    if song['track'] is None:
        print("This song is broken...")
        return None
    artist = song['track']['artists'][0]['name']
    title = song['track']['name']
    song_id = song['track']['id']
    return {
        'artist': artist,
        'title': title,
        'id': song_id
    }


def get_songs_of_block_playlist(client_id: str, client_secret: str, playlist_id: str) -> list:
    """
    Get list of dictionary. In each dictionary there is a song from the block playlist. the result will be in the
    following format:
    [[{
    'artist': artist,
    'title': title,
    'id': song_id
    }],..]
    :param client_id: id from Spotify App in dashboard
    :param client_secret: id from Spotify App in dashboard
    :param playlist_id: id of block playlist
    :return: List of songs in block playlist (see above)
    """
    sp = spotipy.Spotify(
        client_credentials_manager=SpotifyClientCredentials(client_id=client_id, client_secret=client_secret))

    # Because Spotify API can get at most 100 songs with each GET (REST)
    offset = 0

    # list of dict
    result = []

    while True:
        response = sp.playlist_items(playlist_id=playlist_id, offset=offset, fields='items.track')
        if len(response['items']) == 0:
            # Obtained all songs of block playlist
            break
        for i in response['items']:
            canzone = get_song_in_dictionary(i)
            if canzone is not None:
                result.append(canzone)
        offset = offset + len(response['items'])
    return result
