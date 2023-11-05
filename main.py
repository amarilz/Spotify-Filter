import os.path

import spotipy
from spotipy import SpotifyException
from spotipy.oauth2 import SpotifyClientCredentials
from spotipy.oauth2 import SpotifyOAuth

SPOTIPY_CLIENT_ID = ''
SPOTIPY_CLIENT_SECRET = ''
REDIRECT_URI = 'https://www.google.com/'
PLAYLIST_BLOCK_ID = ''
PLAYLIST_LISTEN_ID = ''
SCOPE = 'playlist-modify-public'


def get_song_details(song_dict: dict):
    """
    Extracts spotify_song_id and song name and artist from a dictionary and returns them as a new dictionary.

    Args:
        song_dict (dict): A dictionary containing song information.

    Returns:
        dict: A dictionary with extracted song details.

    Raises:
        TypeError: If the input dictionary is None or empty, or if the 'track' key is missing.
    """
    # Check if the input dictionary is None
    if song_dict is None:
        raise TypeError("Input dictionary is None")

    # Check if the input dictionary is empty
    if not song_dict:
        raise TypeError("Input dictionary is empty")

    # Check if the 'track' key is present and not None
    if song_dict.get('track') is None:
        print("This song is broken...")
        return None

    # Extract relevant information from the 'track' sub-dictionary
    artist = song_dict['track']['artists'][0]['name']
    title = song_dict['track']['name']
    spotify_song_id = song_dict['track']['id']

    db_song_id = (artist + title).replace(" ", "")
    return {
        'spotify_song_id': spotify_song_id,
        'db_song_id': db_song_id
    }


def get_songs_of_block_playlist() -> list:
    sp = spotipy.Spotify(
        client_credentials_manager=SpotifyClientCredentials(
            client_id=SPOTIPY_CLIENT_ID,
            client_secret=SPOTIPY_CLIENT_SECRET))

    # Initialize an offset for pagination (Spotify API can get at most 100 songs with each GET)
    offset: int = 0

    # Initialize the result list
    result: list = []

    # Continuously fetch songs from the playlist until all songs are retrieved
    while True:

        # Get a batch of songs from the playlist using pagination
        response = sp.playlist_items(
            playlist_id=PLAYLIST_BLOCK_ID,
            fields='items.track',
            offset=offset)

        # Check if there are no more songs to retrieve from block playlist
        block_songs = response['items']
        if len(block_songs) == 0:
            break

        # Extract song details for each item and add to the result list
        for item in block_songs:
            song_details: dict = get_song_details(song_dict=item)
            if song_details is not None:
                result.append(song_details)

        offset += len(block_songs)

    return result


def load_db() -> list:
    # Initialize an empty list to store the songs
    result: list = []

    # Define the path to the text file
    file_path: str = os.path.dirname(__file__) + '/db.txt'

    # Open the text file and read it line by line
    with open(file=file_path, mode='r', encoding='utf-8') as openfile:
        for line in openfile:
            # Remove leading and trailing whitespace from each line
            cleaned_line = line.strip()

            # Append the cleaned line to the result list
            result.append(cleaned_line)

    return result


def save_db(listened_songs: list, file_name: str) -> None:
    # Define the path to the text file
    file_path: str = os.path.dirname(__file__) + file_name

    # Sort list
    listened_songs.sort()

    # Open the text file for writing
    with open(file=file_path, mode="w", encoding='utf-8') as file:
        # Write each song from the list to a separate line in the text file
        for song in listened_songs:
            file.write(song + '\n')


def get_new_songs(block_playlist_songs: list, listened_songs: list) -> list:
    # Use a set to keep track of unique song IDs
    unique_song_ids = set()
    result: list = []
    for block_song in block_playlist_songs:
        _, db_song_id = block_song  # Unpack the tuple
        if db_song_id not in listened_songs and db_song_id not in unique_song_ids:
            result.append(block_song)
            unique_song_ids.add(db_song_id)
    return result


def get_new_songs_from_block_playlist(listened_songs: list, songs_in_block_playlist: list) -> list:
    # Format the songs in the block playlist
    list_block_songs_formatted: list = [
        [i['spotify_song_id'], i['db_song_id']] for i in songs_in_block_playlist]

    # Identify new songs by comparing with the list of listened songs
    new_songs: list = get_new_songs(list_block_songs_formatted, listened_songs)

    # Print the number of new songs found
    return new_songs


def add_songs(sp: spotipy.Spotify, new_song_ids: list, playlist_id: str) -> None:
    # Remove "None" values from new_song_ids and print what was removed
    new_song_ids = [song_id for song_id in new_song_ids if song_id is not None]

    # Check if there are more songs to add
    while len(new_song_ids) != 0:
        # Add songs in batches of 100 to the specified playlist
        sp.playlist_add_items(playlist_id, new_song_ids[0:100])
        new_song_ids = new_song_ids[100:]


def add_new_songs_in_playlist(new_song_ids: list) -> None:
    # Initialize a Spotify client with OAuth2 authentication
    sp = spotipy.Spotify(
        auth_manager=SpotifyOAuth(
            client_id=SPOTIPY_CLIENT_ID,
            client_secret=SPOTIPY_CLIENT_SECRET,
            scope=SCOPE,
            redirect_uri=REDIRECT_URI))

    try:
        # Add the new songs to the playlist using the add_songs function
        add_songs(sp, new_song_ids, PLAYLIST_LISTEN_ID)
        print("Songs added.")
    except SpotifyException:
        print(
            "An error occurred while adding songs to the playlist.\n" +
            "Delete the songs you tried to insert from the database (.json) and try again.\n" +
            "If you don't, these songs will be blocked, but you will not actually hear them.")


if __name__ == '__main__':
    songs_in_block_playlist: list = get_songs_of_block_playlist()

    listened_songs = load_db()  # Load the list of listened songs

    new_songs: list = get_new_songs_from_block_playlist(
        listened_songs, songs_in_block_playlist)
    if len(new_songs) == 0:
        print("There are no new songs")
        exit()
    print(f"New songs = {len(new_songs)}")

    # Update the list of blocked songs to include the new songs
    new_songs_spotify_id = [e[0] for e in new_songs]
    new_songs_db_id = [e[1] for e in new_songs]

    add_new_songs_in_playlist(new_songs_spotify_id)
    save_db(listened_songs, '/db_old.txt')  # save old db
    save_db(listened_songs + new_songs_db_id, '/db.txt')  # save new db
