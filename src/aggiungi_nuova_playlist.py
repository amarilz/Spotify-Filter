import spotipy
from spotipy import SpotifyException
from spotipy.oauth2 import SpotifyOAuth

scope = 'playlist-modify-public'


def aggiungi_piu_di_100_canzoni_nuove_alla_volta(sp, id_canzoni_nuove, playlist_id):
    ## NON conosco lunghezza canzoni.
    while len(id_canzoni_nuove) != 0:
        sp.playlist_add_items(playlist_id, id_canzoni_nuove[0:100])
        id_canzoni_nuove = id_canzoni_nuove[100:]


def aggiungi_in_playlist_ascolta(id_canzoni_nuove, client_id, secret_id, playlist_id):
    sp = spotipy.Spotify(auth_manager=SpotifyOAuth(
        client_id=client_id,
        client_secret=secret_id,
        scope=scope,
        redirect_uri='https://www.google.com/'))

    try:
        if len(id_canzoni_nuove) > 0:
            aggiungi_piu_di_100_canzoni_nuove_alla_volta(sp, id_canzoni_nuove, playlist_id)
            print("Canzoni aggiunte.")
        else:
            print("Non ci sono canzoni da aggiungere.")
    except SpotifyException:
        print(
            "Si é verificato un errore durante l'inserimento delle canzoni nella playlist.\nCANCELLARE I TITOLI DELLE CANZONI CHE HAI PROVATO AD INSERIRE DAL DATABASE E TENTARE NUOVAMENTE L'INSERIMENTO.\nSE NON LO FAI, QUESTE CANZONI SARANNO BLOCCATE MA NON LE SENTIRAI EFFETTIVAMENTE.")
