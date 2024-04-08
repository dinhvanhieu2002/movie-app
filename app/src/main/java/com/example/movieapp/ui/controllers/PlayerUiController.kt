package com.example.movieapp.ui.controllers

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Rational
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import com.example.movieapp.R
import com.example.movieapp.ui.activities.MoviePlayerActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.utils.FadeViewHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.random.Random

class PlayerUiController(val controlsUi: View, val youtubePlayer: YouTubePlayer, val youTubePlayerView: YouTubePlayerView, val activity: MoviePlayerActivity, val lifecycle: Lifecycle, val videoIds : List<String>) : AbstractYouTubePlayerListener() {
    private var playerTracker : YouTubePlayerTracker = YouTubePlayerTracker()
    companion object {
        var isLandscapeMode = false
        val isInPip = false
    }
    init {
        youtubePlayer.addListener(playerTracker)
        initViews(controlsUi)
        initPictureInPicture(controlsUi)
    }

    private fun initViews(view: View) {
        val container: View = view.findViewById(R.id.container)
        val relativeLayout : RelativeLayout = view.findViewById(R.id.root)
        val seekBar : YouTubePlayerSeekBar = view.findViewById(R.id.playerSeekBar)
        val pausePlay : ImageButton = view.findViewById(R.id.pausePlay)
        val nextVideo : ImageButton = view.findViewById(R.id.nextVideo)
        val fullScreen : ImageButton = view.findViewById(R.id.toggleFullScreen)

        youtubePlayer.addListener(seekBar)

        seekBar.youtubePlayerSeekBarListener = object : YouTubePlayerSeekBarListener {
            override fun seekTo(time: Float) {
                youtubePlayer.seekTo(time)
            }
        }

        nextVideo.setOnClickListener {
            pausePlay.setImageResource(R.drawable.action_pause)
            youtubePlayer.loadOrCueVideo(lifecycle, videoIds[Random.nextInt(videoIds.size)], 0f)
        }

        pausePlay.setOnClickListener {
            if(playerTracker.state == PlayerConstants.PlayerState.PLAYING) {
                pausePlay.setImageResource(R.drawable.action_play)
                youtubePlayer.pause()
            } else {
                pausePlay.setImageResource(R.drawable.action_pause)
                youtubePlayer.play()
            }
        }

        fullScreen.setOnClickListener {
            if(isLandscapeMode) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            isLandscapeMode = !isLandscapeMode

//            youtubePlayer.toggleFullscreen()
        }

        val fadeViewHelper = FadeViewHelper(container)
        fadeViewHelper.animationDuration = FadeViewHelper.DEFAULT_ANIMATION_DURATION
        fadeViewHelper.fadeOutDelay = FadeViewHelper.DEFAULT_FADE_OUT_DELAY
        youtubePlayer.addListener(fadeViewHelper)

        relativeLayout.setOnClickListener {
            fadeViewHelper.toggleVisibility()
        }

        container.setOnClickListener {
            fadeViewHelper.toggleVisibility()
        }
    }

    private fun initPictureInPicture(view: View) {
        val enterPipMode : ImageButton = view.findViewById(R.id.pip)
        enterPipMode.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val aspectRatio = Rational(16,9)
                val params: PictureInPictureParams = PictureInPictureParams.Builder()
                    .setAspectRatio(aspectRatio)
                    .build()
                activity.enterPictureInPictureMode(params)
            }
        }
    }
}