package com.example.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.api.models.Media
import com.example.movieapp.api.models.NotificationData
import com.example.movieapp.api.models.PushNotification
import com.example.movieapp.api.models.ReviewRequest
import com.example.movieapp.api.models.UserRef
import com.example.movieapp.databinding.FragmentMediaDetailBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.ui.activities.MoviePlayerActivity
import com.example.movieapp.ui.activities.RoomMovieActivity
import com.example.movieapp.ui.adapters.BackdropSlideAdapter
import com.example.movieapp.ui.adapters.CastSlideAdapter
import com.example.movieapp.ui.adapters.PosterSlideAdapter
import com.example.movieapp.ui.adapters.ReviewMediaAdapter
import com.example.movieapp.ui.viewmodel.MediaDetailViewModel
import com.example.movieapp.ui.viewmodel.MediaDetailViewModelFactory
import com.example.movieapp.ui.viewmodel.MediaViewModel
import com.example.movieapp.ui.viewmodel.MediaViewModelFactory
import com.example.movieapp.utils.ApiHelper
import com.example.movieapp.utils.AuthTokenHelper
import com.example.movieapp.utils.Resource
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.random.Random

class MediaDetailFragment : Fragment(R.layout.fragment_media_detail) {


    private val TAG = "Media Detail"
    lateinit var mediaType : String
    lateinit var mediaId : String
    private lateinit var binding : FragmentMediaDetailBinding
    private lateinit var viewModel : MediaDetailViewModel
    private lateinit var mediaViewModel : MediaViewModel
    private lateinit var castAdapter : CastSlideAdapter
    private lateinit var backdropAdapter : BackdropSlideAdapter
    private lateinit var posterAdapter : PosterSlideAdapter
    private lateinit var reviewAdapter : ReviewMediaAdapter
    private var listFavorite = listOf<Media>()
    private var isFavorite = false
    private lateinit var authTokenHelper: AuthTokenHelper
    var mediaTitle : String? = null
    private lateinit var poster : String
    val actionsRef = FirebaseDatabase.getInstance().getReference("actions")
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMediaDetailBinding.bind(view)

        view.setOnClickListener { v ->
            Log.e("Media Detail", "Clickkkkkkkkk")
            hideKeyboard(v)
            // Clear focus from all EditText(s)
            view.clearFocus()
        }

        authTokenHelper = AuthTokenHelper(requireContext())

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 50) {
                (activity as MainActivity).binding.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_bar_black))
            } else {
                (activity as MainActivity).binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        mediaType = arguments?.getString("mediaType",  "movie").toString()
        mediaId = arguments?.getString("mediaId",  "").toString()
        val shouldScroll = arguments?.getBoolean("isScroll",  false)
        Log.e("Detail Fragment", shouldScroll.toString())
        if(shouldScroll != null && shouldScroll) {
            binding.nestedScrollView.post {
                binding.nestedScrollView.smoothScrollTo(0, binding.rcvReviews.top)
            }
        }

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val viewModelProviderFactory = MediaDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[MediaDetailViewModel::class.java]
        val mediaViewModelProviderFactory = MediaViewModelFactory(repository)
        mediaViewModel = ViewModelProvider(this, mediaViewModelProviderFactory)[MediaViewModel::class.java]

        setupViewPager(requireContext())
        setupRecyclerView(requireContext())
        viewModel.getMediaDetail(mediaType, mediaId)

        castAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaType", mediaType)
                putString("mediaId", it.id.toString())
            }

            findNavController().navigate(R.id.action_mediaDetailFragment_to_personFragment, bundle)

        }

        binding.ivWatch.setOnClickListener {
            val videos = viewModel.mediaDetail.value?.data?.videos!!.results
            val videoId = videos[0].key
            val videosId = videos.map { it.key }
            val currentPos = Random.nextInt(videos.size)
            val intent = Intent(requireContext(), MoviePlayerActivity::class.java)
            intent.putExtra("videoId", videoId)
            intent.putExtra("videos", videosId.toTypedArray())
            intent.putExtra("currentPos", currentPos)
            startActivity(intent)
        }

        binding.startRoom.setOnClickListener {
            val timestamp = System.currentTimeMillis().toString()
            val roomId = Random.nextInt(100000, 1000000).toString()
            val videoId = viewModel.mediaDetail.value?.data?.videos!!.results[0].key
            val videoIds = viewModel.mediaDetail.value?.data?.videos!!.results.map { it.key }
            val currentPos = Random.nextInt(videoIds.size)


            val userId = authTokenHelper.getUserId().toString()
            val displayName = authTokenHelper.getDisplayName().toString()
            val userRef = UserRef(userId, displayName)
            usersRef.child(roomId).child(userId).setValue(userRef)

//            actionsRef.child(roomId).child("play").setValue(true)
//            actionsRef.child(roomId).child("seekTo").setValue(0f)
//            actionsRef.child(roomId).child("videoId").setValue(videoId)

            actionsRef.child(roomId).child("play").setValue(true)
            actionsRef.child(roomId).child("seekTo").setValue(0f)
            actionsRef.child(roomId).child("videoIds").setValue(videoIds)
            actionsRef.child(roomId).child("currentPos").setValue(currentPos)

            val intent = Intent(requireContext(), RoomMovieActivity::class.java)
            intent.putExtra("roomId", roomId)
            intent.putExtra("videoId", videoId)
            intent.putExtra("videoIds", videoIds.toTypedArray())
            intent.putExtra("currentPos", currentPos)
            startActivity(intent)
        }

        binding.ivFavorite.setOnClickListener { handleFavorite() }
        binding.btnSend.setOnClickListener {
            if(binding.etComment.text.toString().isNotEmpty()) {
                val displayName = authTokenHelper.getDisplayName()
                val title = "$displayName has commented on $mediaTitle"
                val message = binding.etComment.text.toString().trim()

                val authorization = ApiHelper.getAuthorizationHeader(authTokenHelper.getToken()!!)
                val reviewRequest = ReviewRequest(binding.etComment.text.toString().trim(), mediaId, mediaType, mediaTitle!!, poster)
                mediaViewModel.addReview(reviewRequest, authorization)

                binding.etComment.setText("")

                PushNotification(
                    NotificationData(title, message, poster, mediaId, mediaType),
                    "/topics/$mediaId"
                ).also {
                    sendNotification(it)
                }
            } else {
                Snackbar.make(view, "Please enter your review", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnTwitterShare.setOnClickListener {
            shareOnTwitter(mediaTitle!!, ApiHelper.getPosterPath(poster))
        }

        mediaViewModel.checkResponse.observe(viewLifecycleOwner, Observer {
            if(it) {
                viewModel.getMediaDetail(mediaType, mediaId)
            }
        })

        viewModel.mediaDetail.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideGlobalLoading()
                    response.data?.let { media ->
                        mediaTitle = media.title ?: media.name
                        poster = media.posterPath

                        castAdapter.differ.submitList(media.credits.cast)
                        backdropAdapter.differ.submitList(media.images.backdrops)
                        posterAdapter.differ.submitList(media.images.posters)

                        Log.e("reviews", media.reviews.size.toString())

                        if(media.reviews.isNotEmpty()) {
                            reviewAdapter.differ.submitList(media.reviews)
                            binding.tvNoComment.visibility = View.GONE
                        } else {
                            reviewAdapter.differ.submitList(listOf())
                            binding.tvNoComment.visibility = View.VISIBLE
                        }

                        binding.apply {
                            tvTitle.text = media.title
                            tvCircularRate.text = DecimalFormat("#.#").format(media.voteAverage).toString()
                            tvOverview.text = media.overview

                            listFavorite.find { it.mediaId == mediaId }?.let {
                                isFavorite = true
                                ivFavorite.setImageResource(R.drawable.ic_favorite_fill)
                            }


                            genresGroup.removeAllViews()
                            media.genres.subList(0, minOf(2, media.genres.size)).mapIndexed { _, i ->
                                val chip = createChip(i.name)
                                genresGroup.addView(chip)
                            }

                            Glide.with(requireContext())
                                .load(ApiHelper.getPosterPath((media.posterPath)))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                                .into(heroImage)

                            Glide.with(requireContext())
                                .load(ApiHelper.getBackdropPath((media.backdropPath)))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                                .into(backgroundImage)

                        }
                    }
                }
                is Resource.Error -> {
                    (requireActivity() as MainActivity).hideGlobalLoading()

                    response.message?.let {
                        Log.e("TAG", "An error occurred $it")
                    }
                }
                is Resource.Loading -> {
                    (requireActivity() as MainActivity).showGlobalLoading()

                }
            }
        })

        mediaViewModel.getAllFavorite().observe(viewLifecycleOwner, Observer {
            listFavorite = it
        })
    }

    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.notifyApi.postNotification(notification)
            if (response.isSuccessful) {
                Log.d(TAG, response.body().toString())
            } else {
                Log.e(TAG, response.errorBody().toString())

            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun handleFavorite() {
        if(isFavorite) {
            isFavorite = false
            mediaViewModel.removeFavoriteById(mediaId)
            binding.ivFavorite.setImageResource(R.drawable.ic_favorite)
            Snackbar.make(requireView(), "Remove favorite successful", Snackbar.LENGTH_SHORT).show()
        } else {
            val title = viewModel.mediaDetail.value?.data?.title ?: viewModel.mediaDetail.value?.data?.name
            val voteAverage = viewModel.mediaDetail.value?.data?.voteAverage
            val poster = viewModel.mediaDetail.value?.data?.posterPath
            val media = Media(mediaType, mediaId, title!!, poster!!, voteAverage.toString())
            isFavorite = true
            mediaViewModel.addFavorite(media)
            binding.ivFavorite.setImageResource(R.drawable.ic_favorite_fill)
            Snackbar.make(requireView(), "Add favorite successful", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupViewPager(context: Context) {
        backdropAdapter = BackdropSlideAdapter(context)
        binding.vpBackdrop.apply {
            adapter = backdropAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            binding.backdropIndicator.tintIndicator(ContextCompat.getColor(requireContext(), R.color.white), ContextCompat.getColor(requireContext(), R.color.grey))
            binding.backdropIndicator.setViewPager(this)
        }
    }

    private fun setupRecyclerView(context: Context) {
        castAdapter = CastSlideAdapter(context)
        binding.rcvCast.apply {
            adapter = castAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        posterAdapter = PosterSlideAdapter(context)
        binding.rcvPoster.apply {
            adapter = posterAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }

        reviewAdapter = ReviewMediaAdapter()
        binding.rcvReviews.apply { 
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }

    private fun createChip(name: String) : Chip {
        val chip = Chip(requireContext())
        chip.text = name
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20, 0, 0, 0)

        chip.gravity = Gravity.CENTER
        chip.layoutParams = layoutParams
        chip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
        chip.minHeight = 48
        chip.isChipIconVisible = false
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))

        return chip
    }

//    private fun hideKeyboard() {
//        val view: View? = requireActivity().currentFocus
//        if (view != null) {
//            val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun shareOnTwitter(text: String, url: String) {
        val encodedText = Uri.encode(text)
        val encodedUrl = Uri.encode(url)
        val tweetIntentUrl = "https://twitter.com/intent/tweet?text=$encodedText&url=$encodedUrl"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetIntentUrl))
        requireActivity().startActivity(intent)
    }

}