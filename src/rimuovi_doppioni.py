import json


def carica_bloccate():
    with open('/Users/amarildo/Documents/GitHub/Spotify-Filter/canzoni_bloccate.json', 'r',
              encoding='utf-8') as openfile:
        json_object = json.load(openfile)
    return json_object


def salva(lista):
    with open("/Users/amarildo/Documents/GitHub/Spotify-Filter/canzoni_bloccate.json", "w+", encoding='utf-8') as file:
        json.dump(sorted(lista), file, indent=2)


def get_block_playlist_nametitle_attaccati(canzoni_playlist_block):
    """
    Questa funzione ritorna una lista che contiene liste di due elementi:
    - nome artista unito al nome della traccia
    - id della canzone
    :param canzoni_playlist_block:
    :return:
    """
    result = []
    for i in canzoni_playlist_block:
        result.append([f"{i['artist']}{i['title']}", i['id']])
    return result


# Testata
def get_list_canzoni_nuove(lista_canzoni_block_nome_unito, lista_bloccate):
    result = []
    for i in lista_canzoni_block_nome_unito:
        if i[0] not in lista_bloccate:
            result.append(i)
    return result


def controlla_doppioni(canzoni_playlist_block):
    lista_bloccate = carica_bloccate()
    lista_canzoni_block_nome_unito = get_block_playlist_nametitle_attaccati(canzoni_playlist_block)
    canzoni_nuove = get_list_canzoni_nuove(lista_canzoni_block_nome_unito, lista_bloccate)

    print(f"Canzoni nuove = {len(canzoni_nuove)}")

    # QUI MI DEVO RICORDARE DI SALVARE SOLO IL PRIMO VALORE (che contiene il nome della traccia)
    # e non tutta canzoni_nuove
    salva(lista_bloccate + [i[0] for i in canzoni_nuove])

    return [i[1] for i in canzoni_nuove]
