package it.vfsfitvnm.vimusic.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.service.media.MediaBrowserService
import android.util.Log

class PlayerMediaBrowserService : MediaBrowserService() {

    var playerServiceBinder: PlayerService.Binder? = null
    var isBound = false

    override fun onCreate() {
        super.onCreate()
        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, playerConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        Log.d("VI_MUSIC", "Get Root")
        if (!isCallerAllowed(clientPackageName, clientUid)) {
            return null
        }
        val extras = Bundle()
        extras.putInt(CONTENT_STYLE_BROWSABLE_HINT, CONTENT_STYLE_LIST_ITEM_HINT_VALUE)
        return BrowserRoot(MEDIA_ROOT_ID, extras)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        Log.d("VI_MUSIC", "Load children for $parentId")
        when (parentId) {
            MEDIA_ROOT_ID -> result.sendResult(createMenuMediaItem())
            MEDIA_PLAYLISTS_ID -> result.sendResult(createPlaylistsMediaItem())
            MEDIA_FAVORITES_ID -> result.sendResult(createFavoritesMediaItem())
        }
/*        if (isBound) {
            result.sendResult(mutableListOf(createPlaylistMediaItem()))
        } else {
            // TODO: impl async waiting task ? needed ??
            // Not sure cause no setToken = no call ?
        }        */
    }

    private fun createFavoritesMediaItem(): MutableList<MediaItem>? {
        return null
    }

    private fun createPlaylistsMediaItem(): MutableList<MediaItem>? {
        return null
    }

    private fun createMenuMediaItem(): MutableList<MediaItem>? {
        return mutableListOf(
            MediaItem(
                MediaDescription.Builder()
                    .setMediaId(MEDIA_PLAYLISTS_ID)
                    // TODO: ressource
                    .setTitle("Playlists")
                    // TODO
                    /*.setIconUri(Uri.parse("android.resource://" +
                            "com.example.android.mediabrowserservice/drawable/ic_by_genre"))*/
                    .build(), MediaItem.FLAG_BROWSABLE
            ), MediaItem(
                MediaDescription.Builder()
                    .setMediaId(MEDIA_FAVORITES_ID)
                    // TODO: ressource
                    .setTitle("Favorites")
                    // TODO
                    /*.setIconUri(Uri.parse("android.resource://" +
                            "com.example.android.mediabrowserservice/drawable/ic_by_genre"))*/
                    .build(), MediaItem.FLAG_BROWSABLE
            )
        )
    }

    private val playerConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            playerServiceBinder = service as PlayerService.Binder
            isBound = true
            sessionToken = playerServiceBinder?.mediaSession?.sessionToken
            Log.i("VI_MUSIC", "Service play bind")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    private fun isCallerAllowed(
        clientPackageName: String,
        clientUid: Int
    ): Boolean {
        return when {
            clientUid == Process.myUid() -> true
            clientUid == Process.SYSTEM_UID -> true
            ANDROID_AUTO_PACKAGE_NAME == clientPackageName -> true
            else -> false
        }
    }

    companion object {
        const val ANDROID_AUTO_PACKAGE_NAME = "com.google.android.projection.gearhead"
        const val CONTENT_STYLE_BROWSABLE_HINT = "android.media.browse.CONTENT_STYLE_BROWSABLE_HINT"
        const val CONTENT_STYLE_LIST_ITEM_HINT_VALUE = 1
        const val MEDIA_ROOT_ID = "VIMUSIC_MEDIA_ROOT_ID"
        const val MEDIA_PLAYLISTS_ID = "VIMUSIC_MEDIA_PLAYLISTS_ID"
        const val MEDIA_FAVORITES_ID = "VIMUSIC_MEDIA_FAVORITES_ID"
    }

}