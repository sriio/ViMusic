package it.vfsfitvnm.vimusic.enums

enum class MediaIDType {
    Song,
    Playlist;

    val prefix: String
        get() = when (this) {
            Song -> "VIMUSIC_SONG_ID_"
            Playlist -> "VIMUSIC_PLAYLIST_ID_"
        }
}