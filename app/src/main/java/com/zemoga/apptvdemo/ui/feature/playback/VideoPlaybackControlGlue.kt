/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.zemoga.apptvdemo.ui.feature.playback

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import androidx.leanback.widget.PlaybackRowPresenter
import java.util.concurrent.TimeUnit

class VideoPlaybackControlGlue(context: Context, mediaPlayerAdapter: MediaPlayerAdapter) :
    PlaybackTransportControlGlue<MediaPlayerAdapter>(context,mediaPlayerAdapter) {

  @VisibleForTesting
  private lateinit var repeatAction: PlaybackControlsRow.RepeatAction
  @VisibleForTesting
  private lateinit var pipAction: PlaybackControlsRow.PictureInPictureAction
  @VisibleForTesting
  private lateinit var thumbsUpAction: PlaybackControlsRow.ThumbsUpAction
  @VisibleForTesting
  private lateinit var thumbsDownAction: PlaybackControlsRow.ThumbsDownAction
  @VisibleForTesting
  private lateinit var skipPreviousAction: PlaybackControlsRow.SkipPreviousAction
  @VisibleForTesting
  private lateinit var skipNextAction: PlaybackControlsRow.SkipNextAction
  @VisibleForTesting
  private lateinit var fastForwardAction: PlaybackControlsRow.FastForwardAction
  @VisibleForTesting
  private lateinit var rewindAction: PlaybackControlsRow.RewindAction

  override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter?) {
    super.onCreatePrimaryActions(primaryActionsAdapter)

    skipPreviousAction = PlaybackControlsRow.SkipPreviousAction(context)
    rewindAction = PlaybackControlsRow.RewindAction(context)
    fastForwardAction = PlaybackControlsRow.FastForwardAction(context)
    skipNextAction = PlaybackControlsRow.SkipNextAction(context)
    thumbsUpAction = PlaybackControlsRow.ThumbsUpAction(context)
    thumbsDownAction = PlaybackControlsRow.ThumbsDownAction(context)
    repeatAction = PlaybackControlsRow.RepeatAction(context)
    pipAction = PlaybackControlsRow.PictureInPictureAction(context)

    primaryActionsAdapter?.apply {
      add(skipPreviousAction)
      add(rewindAction)
      add(fastForwardAction)
      add(skipNextAction)
    }
  }

  override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter?) {
    super.onCreateSecondaryActions(secondaryActionsAdapter)
    secondaryActionsAdapter?.apply {
      add(thumbsDownAction)
      add(thumbsUpAction)
    }
  }

  override fun next() {
    super.next()
    playerAdapter.next()
  }

  override fun previous() {
    super.previous()
    playerAdapter.previous()
  }

  override fun onActionClicked(action: Action?) {
    Log.d("--->LOG PRESSED ", action.toString())
    when(action) {
      rewindAction -> {
        Log.d("REWIND---> ", action.toString())
        skipBackward()
      }
      fastForwardAction -> {
        Log.d("FASTFORWARD---> ", action.toString())
        skipForward()
      }
      else -> super.onActionClicked(action)
    }
  }

  /** Skips backward 30 seconds.  */
  private fun skipBackward() {
    var newPosition: Long = currentPosition - THIRTY_SECONDS
    newPosition = newPosition.coerceAtLeast(0L)
    playerAdapter.seekTo(newPosition)
  }

  /** Skips forward 30 seconds.  */
  private fun skipForward() {
    var newPosition: Long = currentPosition + THIRTY_SECONDS
    newPosition = newPosition.coerceAtMost(duration)
    playerAdapter.seekTo(newPosition)
  }

  companion object {
    private val THIRTY_SECONDS = TimeUnit.SECONDS.toMillis(30)
  }

}