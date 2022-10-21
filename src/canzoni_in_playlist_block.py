import spotipy
from spotipy.oauth2 import SpotifyClientCredentials


def get_data_of_song_in_dictionary(i: dict):
    if i is None:
        raise TypeError("Il valore del dizionario é None")
    if not i:
        raise TypeError("Il dizionario é vuoto")
    artist = i['track']['artists'][0]['name']
    title = i['track']['name']
    song_id = i['track']['id']
    return {
        'artist': artist,
        'title': title,
        'id': song_id
    }


def get_tracks_from_block_playlist(client_id: str, client_secret: str, playlist_id: str):
    sp = spotipy.Spotify(
        client_credentials_manager=SpotifyClientCredentials(client_id=client_id, client_secret=client_secret))

    offset = 0

    # Array di dizionari
    result = []

    while True:
        response = sp.playlist_items(playlist_id=playlist_id, offset=offset, fields='items.track')
        if len(response['items']) == 0:
            break
        else:
            for i in response['items']:
                result.append(get_data_of_song_in_dictionary(i))
            offset = offset + len(response['items'])
    return result
