import aggiungi_nuova_playlist
import canzoni_in_playlist_block
import duplicates
from config import SPOTIPY_CLIENT_ID, SPOTIPY_CLIENT_SECRET, PLAYLIST_BLOCK_ID, PLAYLIST_LISTEN_ID

if __name__ == '__main__':
    canzoni = canzoni_in_playlist_block.get_tracks_from_block_playlist(client_id=SPOTIPY_CLIENT_ID,
                                                                       client_secret=SPOTIPY_CLIENT_SECRET,
                                                                       playlist_id=PLAYLIST_BLOCK_ID)
    id_canzoni_nuove_da_mettere_in_playlist = duplicates.remove_listened_songs(canzoni)

    aggiungi_nuova_playlist.aggiungi_in_playlist_ascolta(
        id_canzoni_nuove_da_mettere_in_playlist,
        SPOTIPY_CLIENT_ID,
        SPOTIPY_CLIENT_SECRET,
        PLAYLIST_LISTEN_ID
    )
