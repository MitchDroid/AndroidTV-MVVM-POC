/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zemoga.apptvdemo.ui.feature.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.zemoga.apptvdemo.R
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.databinding.FragmentPlaybackErrorBinding
import dagger.hilt.android.AndroidEntryPoint

/** Displays an error to the user when something unexpected occurred during playback. */
@AndroidEntryPoint
class PlaybackErrorFragment : Fragment() {

    private lateinit var video: Movie
    private lateinit var error: Exception
    private val viewModel: PlaybackErrorViewModel by viewModels()

    private var binding: FragmentPlaybackErrorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        error = PlaybackErrorFragmentArgs.fromBundle(requireArguments()).error
        video = PlaybackErrorFragmentArgs.fromBundle(requireArguments()).video

        val onBackPressedCallback = object : OnBackPressedCallback(/* enabled= */ true) {
            override fun handleOnBackPressed() {
                // Upon selecting back, return the user to the browse fragment and clean up the
                // parent node in the nav graph, PlaybackFragment, from the back stack when popping.
                findNavController().popBackStack(R.id.home_fragment, /* inclusive= */ true)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.ErrorUiTheme)
        val themedInflater = inflater.cloneInContext(contextThemeWrapper)
        binding = FragmentPlaybackErrorBinding.inflate(
            themedInflater,
            container,
            /* attachToParent= */ false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.run {
            title.text = getString(R.string.error_title_with_video, video.name)
            // The entire stack trace is printed to logcat. We only need to show the cause's message
            // in the UI to give enough context for the error. Search logcat for "Playback error" to
            // view the full exception.
            message.text = error.cause?.message ?: error.message

            actionRetry.setOnClickListener {
                findNavController().navigate(
                    PlaybackErrorFragmentDirections
                        .actionPlaybackErrorFragmentToPlaybackFragment()
                )
            }
            actionGoBack.setOnClickListener {
                findNavController().navigate(
                    PlaybackErrorFragmentDirections.actionPlaybackErrorFragmentToBrowseFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
