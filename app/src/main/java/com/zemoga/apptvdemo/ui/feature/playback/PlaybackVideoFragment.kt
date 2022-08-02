package com.zemoga.apptvdemo.ui.feature.playback

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.gms.cast.tv.CastReceiverContext
import com.google.android.gms.cast.tv.media.MediaManager
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.ui.feature.detail.DetailFragmentDirections

import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.Duration

/** Handles video playback with media controls. */
@AndroidEntryPoint
class PlaybackVideoFragment : VideoSupportFragment() {

    val viewModel: PlayBackViewModel by viewModels()
    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>
    private var exoplayer: ExoPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var mMediaManager: MediaManager
    private var mVideo : Movie? = null

    private val uiPlaybackStateListener = object : PlaybackStateListener {
        override fun onChanged(state: VideoPlaybackState) {
            // While a video is playing, the screen should stay on and the device should not go to
            // sleep. When in any other state such as if the user pauses the video, the app should
            // not prevent the device from going to sleep.
            view?.keepScreenOn = state is VideoPlaybackState.Play

            when (state) {
                is VideoPlaybackState.Prepare -> {
                    startPlaybackFromWatchProgress(state.startPosition)
                }
                is VideoPlaybackState.End -> {
                    // To get to playback, the user always goes through browse first. Deep links for
                    // directly playing a video also go to browse before playback. If playback
                    // finishes the entire video, the PlaybackFragment is popped off the back stack
                    // and the user returns to browse.
                    findNavController().popBackStack()
                }
                is VideoPlaybackState.Error ->
                    findNavController().navigate(
                        PlaybackVideoFragmentDirections
                            .actionPlaybackFragmentToPlaybackErrorFragment(
                                state.video,
                                state.exception
                            )
                    )
                else -> {
                    // Do nothing.
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mVideo = PlaybackVideoFragmentArgs.fromBundle(requireArguments()).movie

        // Create the MediaSession that will be used throughout the lifecycle of this Fragment.
        //createMediaSession()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  viewModel.args.asLiveData().observe(viewLifecycleOwner) { _args ->
            _args?.let { args ->
                //bindArgs(args)
                mVideo = args.movie
            }
        }*/

        viewModel.addPlaybackStateListener(uiPlaybackStateListener)
    }

    override fun onStart() {
        super.onStart()

        //mMediaManager = CastReceiverContext.getInstance().mediaManager
        //mMediaManager.setSessionCompatToken(mediaSession.sessionToken)

        initializePlayer()
    }

    private fun initializePlayer() {
        val dataSourceFactory = DefaultDataSource.Factory(requireContext())
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).
        createMediaSource(MediaItem.fromUri("https://storage.googleapis.com/atv-reference-app-videos/clips-supercharged/supercharged-jquery-and-closest.mp4"))
        exoplayer = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaSource(mediaSource)
            prepare()
            addListener(PlayerEventListener())
            prepareGlue(this)
            playWhenReady = true
            hideControlsOverlay(true)
            //mediaSession.isActive = true
        }

        mVideo?.let { VideoPlaybackState.Load(it) }?.let { viewModel.onStateChange(it) }
    }

    private fun prepareGlue(localExoplayer: ExoPlayer) {
        VideoPlaybackControlGlue(
            requireActivity(),
            requireContext(),
            LeanbackPlayerAdapter(
                requireContext(),
                localExoplayer,
                50
            ),
            onProgressUpdate
        ).apply {
            host = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
            title = mVideo?.name
            subtitle = mVideo?.desc
            // Enable seek manually since PlaybackTransportControlGlue.getSeekProvider() is null,
            // so that PlayerAdapter.seekTo(long) will be called during user seeking.
            // TODO(): Add a PlaybackSeekDataProvider to support video scrubbing.
            isSeekEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removePlaybackStateListener(uiPlaybackStateListener)
        //mediaSession.release()
        destroyPlayer()
    }

    private fun destroyPlayer() {
        //mediaSession.isActive = false
        //mediaSessionConnector.setPlayer(null)
        exoplayer?.let {
            // Pause the player to notify listeners before it is released.
            it.pause()
            it.release()
            exoplayer = null
        }
    }

    /*
     private fun bindArgs(args: PlaybackVideoFragmentArgs) {
         // Bind to detail
         val movie = args.movie
         val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
         val playerAdapter = MediaPlayerAdapter(requireActivity())
         val controlGlue = VideoPlaybackControlGlue(requireContext(), playerAdapter)

         controlGlue.apply {
             isControlsOverlayAutoHideEnabled = true
             host = glueHost
             title = movie.name
             subtitle = movie.desc
             isSeekEnabled = true
             playWhenPrepared()
         }

         playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        /* mTransportControlGlue = PlaybackTransportControlGlue(requireActivity(), playerAdapter)
         mTransportControlGlue.host = glueHost
         mTransportControlGlue.title = movie.name
         mTransportControlGlue.subtitle = movie.desc
         mTransportControlGlue.playWhenPrepared()*/

         playerAdapter.setDataSource(Uri.parse("http://commondatastorage.googleapis.com/android-tv/Sample%20videos/Zeitgeist/Zeitgeist%202010_%20Year%20in%20Review.mp4"))

     }*/

    override fun onPause() {
        super.onPause()
        //mTransportControlGlue.pause()
    }


    private val onProgressUpdate: () -> Unit = {
        // TODO(benbaxter): Calculate when end credits are displaying and show the next episode for
        //  episodic content.
    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(requireContext(), MEDIA_SESSION_TAG)
        mediaSessionConnector = MediaSessionConnector(mediaSession)

    }

    private fun startPlaybackFromWatchProgress(startPosition: Long) {
        Timber.v("Starting playback from $startPosition")
        exoplayer?.apply {
            seekTo(startPosition)
            playWhenReady = true
        }
    }



    inner class PlayerEventListener : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Timber.w(error, "Playback error")
            mVideo?.let { VideoPlaybackState.Error(it, error) }?.let { viewModel.onStateChange(it) }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            when {
                isPlaying -> mVideo?.let { VideoPlaybackState.Play(it) }?.let {
                    viewModel.onStateChange(
                        it
                    )
                }
                exoplayer!!.playbackState == Player.STATE_ENDED -> mVideo?.let { VideoPlaybackState.End(it) }
                    ?.let {
                        viewModel.onStateChange(
                            it
                        )
                    }
                else -> mVideo?.let { VideoPlaybackState.Pause(it, exoplayer!!.currentPosition) }?.let {
                    viewModel.onStateChange(
                        it
                    )
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
    }

    companion object {
        // Update the player UI fairly often. The frequency of updates affects several UI components
        // such as the smoothness of the progress bar and time stamp labels updating. This value can
        // be tweaked for better performance.

        //private val PLAYER_UPDATE_INTERVAL_MILLIS = Duration.ofMillis(50).toMillis()

        // A short name to identify the media session when debugging.
        private const val MEDIA_SESSION_TAG = "ReferenceAppKotlin"
    }

}
