package com.example.movieapp.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.api.models.ResultX
import com.example.movieapp.utils.ApiHelper.Companion.getYoutubePath

class VideoSlideAdapter(val context: Context, val activity: Activity) : RecyclerView.Adapter<VideoSlideAdapter.VideoSlideViewHolder>() {
    inner class VideoSlideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val webView : WebView = view.findViewById(R.id.webView)
        val customViewContainer: FrameLayout = view.findViewById(R.id.customViewContainer)
    }

    private val differCallback = object : DiffUtil.ItemCallback<ResultX>() {
        override fun areItemsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResultX, newItem: ResultX): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoSlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_video_item, parent, false)
        return VideoSlideViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

//    @Suppress("DEPRECATION")
//    private fun enterFullScreen() {
//        val windowInsetsController: WindowInsetsController?
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            windowInsetsController = activity.window?.insetsController
//            windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//        } else {
//            // Use the old way for older devices
//            activity.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
//        // Hide the ActionBar, if available (use requireActivity() to get Activity from Fragment)
//        (activity as AppCompatActivity).supportActionBar?.hide()
//    }
//
//    @Suppress("DEPRECATION")
//    private fun exitFullScreen() {
//        val windowInsetsController: WindowInsetsController?
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//            windowInsetsController = activity.window?.insetsController
//            windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//        } else {
//            // Use the old way for older devices
//            activity.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
//        }
//        // Show the ActionBar
//        (activity as AppCompatActivity).supportActionBar?.show()
//    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onBindViewHolder(holder: VideoSlideViewHolder, position: Int) {
        val item = differ.currentList[position]
        val videoString = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"${getYoutubePath(item.key)}\" allow=\"picture-in-picture\" allowfullscreen></iframe></body></html>"
        val javascriptString = "" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <body>\n" +
                "    <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->\n" +
                "    <div id=\"player\"></div>\n" +
                "\n" +
                "    <script>\n" +
                "      // 2. This code loads the IFrame Player API code asynchronously.\n" +
                "      var tag = document.createElement('script');\n" +
                "\n" +
                "      tag.src = \"https://www.youtube.com/iframe_api\";\n" +
                "      var firstScriptTag = document.getElementsByTagName('script')[0];\n" +
                "      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n" +
                "\n" +
                "      // 3. This function creates an <iframe> (and YouTube player)\n" +
                "      //    after the API code downloads.\n" +
                "      var player;\n" +
                "      function onYouTubeIframeAPIReady() {\n" +
                "        player = new YT.Player('player', {\n" +
                "          height: '720',\n" +
                "          width: '1280',\n" +
                "          videoId: '${item.key}',\n" +
                "          playerVars: {\n" +
                "            'playsinline': 1\n" +
                "          },\n" +
                "          events: {\n" +
                "            'onReady': onPlayerReady,\n" +
                "            'onStateChange': onPlayerStateChange\n" +
                "          }\n" +
                "        });\n" +
                "      }\n" +
                "\n" +
                "      // 4. The API will call this function when the video player is ready.\n" +
                "      function onPlayerReady(event) {\n" +
                "        event.target.playVideo();\n" +
                "      }\n" +
                "\n" +
                "      // 5. The API calls this function when the player's state changes.\n" +
                "      //    The function indicates that when playing a video (state=1),\n" +
                "      //    the player should play for six seconds and then stop.\n" +
                "      var done = false;\n" +
                "      function onPlayerStateChange(event) {\n" +
                "        if (event.data == YT.PlayerState.PLAYING && !done) {\n" +
                "          setTimeout(stopVideo, 6000);\n" +
                "          done = true;\n" +
                "        }\n" +
                "      }\n" +
                "      function stopVideo() {\n" +
                "        player.stopVideo();\n" +
                "      }\n" +
                "    </script>\n" +
                "  </body>\n" +
                "</html>"

        holder.webView.loadData(javascriptString, "text/html", "utf-8")
        holder.webView.settings.javaScriptEnabled = true
//        holder.webView.settings.loadWithOverviewMode = true
//        holder.webView.settings.useWideViewPort = true

        holder.webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalOrientation: Int = 0
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                holder.webView.visibility = View.GONE
                holder.customViewContainer.visibility = View.VISIBLE
                holder.customViewContainer.addView(view)
                customView = view
                customViewCallback = callback

                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

//                enterFullScreen()
            }

            override fun onHideCustomView() {
                customView?.let {
                    holder.customViewContainer.visibility = View.GONE
                    holder.customViewContainer.removeView(it)
                    customView = null
                    holder.webView.visibility = View.VISIBLE

//                    exitFullScreen()

                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED


                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                }
            }
        }
    }
}