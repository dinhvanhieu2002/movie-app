package com.example.movieapp.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMoviePlayerBinding
import com.example.movieapp.ui.controllers.PlayerUiController
import com.example.movieapp.ui.controllers.PlayerUiController.Companion.isLandscapeMode
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import kotlin.random.Random


class MoviePlayerActivity : AppCompatActivity() {
    lateinit var binding : ActivityMoviePlayerBinding

    lateinit var videoId : String
    lateinit var videoIds : List<String>
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isLandscapeMode) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                isLandscapeMode = !isLandscapeMode
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoviePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        intent?.let { incomingIntent ->
            videoId = incomingIntent.extras?.getString("videoId").toString()
            videoIds = incomingIntent.extras?.getStringArray("videos")!!.toList()
        }

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.enableAutomaticInitialization = false
        val controlsUi = binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_controls)
        val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val controller = PlayerUiController(controlsUi, youTubePlayer, binding.youtubePlayerView, this@MoviePlayerActivity, lifecycle, videoIds)
                youTubePlayer.addListener(controller)
                youTubePlayer.loadOrCueVideo(lifecycle, videoIds[Random.nextInt(videoIds.size)], 0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
            }
        }

        binding.youtubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
//                isFullscreen = true

                binding.youtubePlayerView.visibility = View.GONE
                binding.fullScreenViewContainer.visibility = View.VISIBLE
                binding.fullScreenViewContainer.addView(fullscreenView)
                binding.fullScreenViewContainer.addView(controlsUi)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {
//                isFullscreen = false

                binding.youtubePlayerView.visibility = View.VISIBLE
                binding.fullScreenViewContainer.visibility = View.GONE
                binding.fullScreenViewContainer.removeAllViews()

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

        })


        val options = IFramePlayerOptions.Builder().controls(0).fullscreen(1).build()
        binding.youtubePlayerView.initialize(youtubePlayerListener, options)
    }
}