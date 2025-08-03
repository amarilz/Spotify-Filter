# Spotify Filter

Tired of listening to the same songs over and over again?\
**Spotify Filter** helps keep your music fresh by automatically moving new songs from a **Block** playlist to a **Listen** playlist, avoiding duplicates based on a local song history.


### How It Works

1. You create two playlists on Spotify:
   - **Block**: contains all songs you want to filter from
   - **Listen**: will contain only new (never listened) songs
2. The app fetches songs from the **Block** playlist
3. It compares these with songs already stored in a local file
4. New songs (not yet in the local file) are added to the **Listen** playlist
5. The local file is updated with the newly added songs

```mermaid
flowchart TD
id1(Add songs to Block playlist) --> id2("Run Spotify\nFilter")
id2 --GET--> id3(Fetch tracks from Block playlist)
id3 --> ide1
id4("Is track already\nin local file?")
id6(Add to Listen playlist)
id7[(db.txt)]
subgraph ide1 [For each track]
    id4 --Yes--> id5(Skip)
    id4 --No--> id6
    id6 --WRITE--> id7
end
````

### Setup Instructions

1. **Create two playlists** on Spotify:

   * One named **Block**
   * One named **Listen**

2. **Register an app** on the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/)

   * Add a redirect URI, e.g., `https://www.google.com/`

3. **Configure your `application.properties`** (or `.yml`) file:

```properties
# Spotify credentials
spotify.clientId=your_spotify_client_id
spotify.clientSecret=your_spotify_client_secret
spotify.redirectUri=https://www.google.com/
spotify.scope=playlist-modify-public

# Playlist IDs
spotify.playlistId.block=your_block_playlist_id
spotify.playlistId.listen=your_listen_playlist_id

# Path to local file database
db.file.path=/absolute/path/to/db.txt
```

### First-Time Authentication

On first run, the application will open your default browser and ask you to authorize Spotify access. You’ll be redirected to the URI you configured (e.g., Google). Copy the full redirect URL from the browser and paste it back into the terminal when prompted. After that, the app fetches access and refresh tokens and stores them in memory for further use during the session.


### Track Persistence and Backup

* Processed tracks are saved to a local `.txt` file (`db.txt`) based on a unique identifier (`artist + title` with whitespace removed).
* A backup file (`db_backup.txt`) is automatically created on each run before updating the file.

### Notes

* Each run only adds **new** songs to the **Listen** playlist
* The app uses a simple local `.txt` file for storing track history
* Authorization code must be entered manually when using a redirect URI like `https://www.google.com/`
