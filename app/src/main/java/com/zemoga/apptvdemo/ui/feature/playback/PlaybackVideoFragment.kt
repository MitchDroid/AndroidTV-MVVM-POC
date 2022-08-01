package com.zemoga.apptvdemo.ui.feature.playback

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow
import androidx.lifecycle.asLiveData

import dagger.hilt.android.AndroidEntryPoint

/** Handles video playback with media controls. */
@AndroidEntryPoint
class PlaybackVideoFragment : VideoSupportFragment() {

    private val viewModel: PlayBackViewModel by viewModels()
    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.args.asLiveData().observe(viewLifecycleOwner) { _args ->
            _args?.let { args ->
                bindArgs(args)
            }
        }

    }

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

    }

    override fun onPause() {
        super.onPause()
        //mTransportControlGlue.pause()
    }

}
