package com.example.movieapp.ui.controllers

import android.app.PictureInPictureParams
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.lifecycle.Lifecycle
import com.example.movieapp.R
import com.example.movieapp.ui.activities.RoomMovieActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


class RoomPlayerUiController(val controlsUi: View, val youtubePlayer: YouTubePlayer, val youTubePlayerView: YouTubePlayerView, val roomId: String, val activity: RoomMovieActivity, val lifecycle: Lifecycle, val videoIds : List<String>) : AbstractYouTubePlayerListener() {
    private var playerTracker : YouTubePlayerTracker = YouTubePlayerTracker()
    private val actionsRef = FirebaseDatabase.getInstance().getReference("actions").child(roomId)
    companion object {
        var isFullScreen = false
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
                actionsRef.child("seekTo").setValue(time)
            }
        }

        var randomPos = 0

        nextVideo.setOnClickListener {
            Log.e("Controller next click", "aaaaaaaaaaaaaaaaa")
            randomPos = Random.nextInt(videoIds.size)
            pausePlay.setImageResource(R.drawable.action_pause)
            actionsRef.child("play").setValue(true)
            actionsRef.child("seekTo").setValue(0)
            actionsRef.child("currentPos").setValue(randomPos)
            youtubePlayer.loadOrCueVideo(lifecycle, videoIds[randomPos], 0f)
        }

        pausePlay.setOnClickListener {
            if(playerTracker.state == PlayerConstants.PlayerState.PLAYING) {
                pausePlay.setImageResource(R.drawable.action_play)
                youtubePlayer.pause()
                actionsRef.child("play").setValue(false)
                actionsRef.child("seekTo").setValue(playerTracker.currentSecond)
            } else {
                pausePlay.setImageResource(R.drawable.action_pause)
                youtubePlayer.play()
                actionsRef.child("play").setValue(true)
                actionsRef.child("seekTo").setValue(playerTracker.currentSecond)
            }
        }

        fullScreen.setOnClickListener {
            if(isFullScreen) {
                youTubePlayerView.wrapContent()
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            } else {
                youTubePlayerView.matchParent()
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            isFullScreen = !isFullScreen
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


        actionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val play = snapshot.child("play").getValue(Boolean::class.java)
                val seekTo = snapshot.child("seekTo").getValue(Float::class.java)
//                val currentPos = snapshot.child("currentPos").getValue(Long::class.java)

                play?.let {
                    if (it) {
                        pausePlay.setImageResource(R.drawable.action_pause)
                        youtubePlayer.play()
                    } else {
                        pausePlay.setImageResource(R.drawable.action_play)
                        youtubePlayer.pause()
                    }
                }

                seekTo?.let {
                    youtubePlayer.seekTo(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        actionsRef.child("currentPos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentPos = snapshot.getValue(Int::class.java)
                currentPos?.let {
                    Log.e("Controller current pos from realtime", "$currentPos")
                    youtubePlayer.loadOrCueVideo(lifecycle, videoIds[it], 0f)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun initPictureInPicture(view: View) {
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