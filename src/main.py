from add_new_songs import add_new_songs_in_playlist
from block_playlist import get_songs_of_block_playlist
from config import SPOTIPY_CLIENT_ID, SPOTIPY_CLIENT_SECRET, PLAYLIST_BLOCK_ID, PLAYLIST_LISTEN_ID
from duplicates import remove_listened_songs

if __name__ == '__main__':
    songs_in_block_playlist = get_songs_of_block_playlist(
        client_id=SPOTIPY_CLIENT_ID,
        client_secret=SPOTIPY_CLIENT_SECRET,
        playlist_id=PLAYLIST_BLOCK_ID)

    new_song_ids = remove_listened_songs(
        songs_in_block_playlist=songs_in_block_playlist)

    add_new_songs_in_playlist(
        new_song_ids=new_song_ids,
        client_id=SPOTIPY_CLIENT_ID,
        client_secret=SPOTIPY_CLIENT_SECRET,
        playlist_id=PLAYLIST_LISTEN_ID
    )
