import json
import os.path


def load_listened_from_json():
    """
    Load the list of listened songs from file (listened.json)
    :return: List of songs played in which each string follows the pattern: {artist_name}{song_title}
    """
    with open(os.path.dirname(__file__) + '/../listened.json', 'r', encoding='utf-8') as openfile:
        result = json.load(openfile)
    return result


def save_listened(listened_songs):
    """
    Update the list of listened songs
    :param listened_songs:
    """
    with open(os.path.dirname(__file__) + '/../listened.json', "w+", encoding='utf-8') as file:
        json.dump(sorted(listened_songs), file, indent=2)


def get_formatted_songs_of_block_playlist(songs_in_block_playlist):
    """
    This function returns a list that contains a list of two elements:
      - artist name combined with the track name
      - song id
    Returns something in the following format:
    [[[artist_name + title_song], [id]], ...]
    :param songs_in_block_playlist: List of songs in the block playlist
    :return: List of songs in block playlist formatted (see above)
    """
    result = []
    for i in songs_in_block_playlist:
        result.append([f"{i['artist']}{i['title']}", i['id']])
    return result


def get_list_new_songs(list_block_songs_formatted, list_blocked_songs_json):
    """
    Get a list of new songs (never listened). Check if a new songs is already in the listened.json file
    :param list_block_songs_formatted: List of songs in block playlist formatted for comparison
    :param list_blocked_songs_json: List of songs already listened
    :return: List of new songs
    """
    result = []
    for i in list_block_songs_formatted:
        if i[0] not in list_blocked_songs_json:
            result.append(i)
    return result


def remove_listened_songs(songs_in_block_playlist):
    """
    Check if in the block playlist there are songs already listened. Return the ids of new songs.
    :param songs_in_block_playlist: List of songs in block playlist
    :return: List of ids of new songs
    """
    list_blocked_songs_json = load_listened_from_json()
    list_block_songs_formatted = get_formatted_songs_of_block_playlist(songs_in_block_playlist)
    new_songs = get_list_new_songs(list_block_songs_formatted, list_blocked_songs_json)
    print(f"New songs = {len(new_songs)}")

    # i[0] because is the position of songs artist+title
    save_listened(list_blocked_songs_json + [i[0] for i in new_songs])

    # i[1] because is the position of songs id
    return [i[1] for i in new_songs]
