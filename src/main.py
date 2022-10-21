import aggiungi_nuova_playlist
import canzoni_in_playlist_block
import rimuovi_doppioni

SPOTIPY_CLIENT_ID = '0ca9f8c8577241cc887387b287439978'
SPOTIPY_CLIENT_SECRET = '8942bd0d3d0848fbba413164d1a24d60'
PLAYLIST_BLOCK_ID = 'spotify:playlist:7yf9ANeYhjBjQ8jaY2sB0K'
PLAYLIST_ASCOLTA_ID = '6GVxbCD9eBQFAZJSv9x7ef'

if __name__ == '__main__':
    canzoni = canzoni_in_playlist_block.get_tracks_from_block_playlist(client_id=SPOTIPY_CLIENT_ID,
                                                                       client_secret=SPOTIPY_CLIENT_SECRET,
                                                                       playlist_id=PLAYLIST_BLOCK_ID)

    # La seguente funzione ritorna uan lista di id di canzoni nuove
    id_canzoni_nuove_da_mettere_in_playlist = rimuovi_doppioni.controlla_doppioni(canzoni)

    aggiungi_nuova_playlist.aggiungi_in_playlist_ascolta(
        id_canzoni_nuove_da_mettere_in_playlist,
        SPOTIPY_CLIENT_ID,
        SPOTIPY_CLIENT_SECRET,
        PLAYLIST_ASCOLTA_ID
    )
