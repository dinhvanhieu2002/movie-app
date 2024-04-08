package com.example.movieapp.ui.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.api.models.ChatMessage
import com.example.movieapp.api.models.UserRef
import com.example.movieapp.databinding.ActivityRoomMovieBinding
import com.example.movieapp.ui.adapters.GroupMessageAdapter
import com.example.movieapp.ui.controllers.RoomPlayerUiController
import com.example.movieapp.ui.controllers.RoomPlayerUiController.Companion.isFullScreen
import com.example.movieapp.ui.viewmodel.RoomMovieViewModel
import com.example.movieapp.ui.viewmodel.RoomMovieViewModelFactory
import com.example.movieapp.utils.AuthTokenHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo


class RoomMovieActivity : AppCompatActivity() {
    lateinit var binding : ActivityRoomMovieBinding
    private lateinit var messageAdapter : GroupMessageAdapter
    private lateinit var viewModel : RoomMovieViewModel
    var seek = 0f
    lateinit var userInfo : UserRef

    private lateinit var roomId : String
    private var videoId : String? = null
    private lateinit var videoIds : List<String>
    private var currentPos : Int = 0
    private lateinit var authTokenHelper : AuthTokenHelper
    val actionsRef = FirebaseDatabase.getInstance().getReference("actions")
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullScreen) {
                binding.youtubePlayerView.wrapContent()
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                isFullScreen = !isFullScreen
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoomMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authTokenHelper = AuthTokenHelper(this)

        intent?.let { incomingIntent ->
            roomId = incomingIntent.extras?.getString("roomId").toString()
//            val videoIntent = incomingIntent.extras?.getString("videoId")

//            if(videoIntent != null) {
//                videoId = videoIntent
//                Log.e("Video from intent", "$videoId")
//            } else {
//                actionsRef.child(roomId).child("videoId").get().addOnSuccessListener {
//                    videoId = it.value.toString()
//                    Log.e("Video from data", "$videoId")
//                }.addOnFailureListener {
//                    Log.e("Video from data", "Errorrrrrrrrrrrrrrrrr")
//                }
//            }
            val videoIdsIntent = incomingIntent.extras?.getStringArray("videoIds")
            val currentPosIntent = incomingIntent.extras?.getInt("currentPos")
            if(videoIdsIntent != null && currentPosIntent != null) {
                videoIds = videoIdsIntent.toList()
                currentPos = currentPosIntent
                Log.e("Video size from intent", "${videoIds.size}")
                Log.e("Video pos from intent", "$currentPos")
            } else {
                actionsRef.child(roomId).get().addOnSuccessListener {
                    it.child("videoIds").getValue<List<String>>()?.let {
                        videoIds = it
                    }

                    it.child("currentPos").getValue<Int>()?.let {
                        currentPos = it
                    }
                    Log.e("Video size from data", "${videoIds.size}")
                    Log.e("Video pos from data", "$currentPos")
                }
            }


//            val userId = incomingIntent.extras?.getString("userId")
//            val displayName = incomingIntent.extras?.getString("displayName")

//            if(userId != null && displayName != null) {
//                userInfo = UserRef(userId, displayName)
//                usersRef.child(roomId).child(userInfo.id).setValue(userInfo)
//            }

            val viewModelProviderFactory = RoomMovieViewModelFactory(roomId)
            viewModel = ViewModelProvider(this, viewModelProviderFactory)[RoomMovieViewModel::class.java]
        }

        setupRecyclerView()

        viewModel.messages.observe(this, Observer {
            Log.e("Room Movie Messages", it.size.toString())
            messageAdapter.differ.submitList(it)
        })

        binding.ibSend.setOnClickListener {
            if(binding.etMessage.text.toString().isNotEmpty()) {
                val senderId = authTokenHelper.getUserId().toString()
                val senderName = authTokenHelper.getDisplayName().toString()

                val chatMessage = ChatMessage(senderId, senderName, binding.etMessage.text.toString().trim())
                viewModel.sendMessage(chatMessage)

                binding.etMessage.setText("")
            } else {
                Snackbar.make(binding.root, "Please enter your message", Snackbar.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        actionsRef.child(roomId).child("seekTo").get().addOnSuccessListener {
            seek = it.value.toString().toFloat()
            Log.e("Seek value from data", seek.toString())
        }.addOnFailureListener {
            Log.e("Seek get error", "Errorrrrrrrrrrrrrrrrr")
        }

        viewModel.handleInOut(binding.root, authTokenHelper)

        viewModel.handleMemberCount()
        viewModel.countMB.observe(this){
            binding.memberCount.text = it.toString()
        }

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.enableAutomaticInitialization = false
        val controlsUi = binding.youtubePlayerView.inflateCustomPlayerUi(R.layout.custom_controls)

        val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
//                this@RoomMovieActivity.youtubePlayer = youTubePlayer
                val controller = RoomPlayerUiController(controlsUi, youTubePlayer, binding.youtubePlayerView, roomId, this@RoomMovieActivity, lifecycle, videoIds)
                youTubePlayer.addListener(controller)

                Log.e("Room VideoID", "$videoId")
                Log.e("Room Seek", "$seek")
                youTubePlayer.loadOrCueVideo(lifecycle, videoIds[currentPos], seek)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)
            }
        }

        val options = IFramePlayerOptions.Builder().controls(0).fullscreen(1).build()
        binding.youtubePlayerView.initialize(youtubePlayerListener, options)

        binding.roomId.text = getString(R.string.room_id, roomId)

//        binding.btnCopy.setOnClickListener {
//            val url = "https://www.nixflet-movie.com/room?roomId=${roomId}"
//            copyTextToClipboard(applicationContext, roomId)
//        }
    }

    fun setupRecyclerView() {
        messageAdapter = GroupMessageAdapter()
        val rcvLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rcvLayoutManager.stackFromEnd = true
        binding.rcvChat.apply {
            adapter = messageAdapter
            layoutManager = rcvLayoutManager
            
        }
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val senderId = authTokenHelper.getUserId().toString()

        usersRef.child(roomId).child(senderId).removeValue()
    }
}
