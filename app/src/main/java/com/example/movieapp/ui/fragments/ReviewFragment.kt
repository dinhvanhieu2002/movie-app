package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.api.models.ReviewRequest
import com.example.movieapp.databinding.FragmentReviewBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.adapters.ReviewAdapter
import com.example.movieapp.ui.viewmodel.MediaViewModel
import com.example.movieapp.ui.viewmodel.MediaViewModelFactory
import com.example.movieapp.utils.ApiHelper
import com.example.movieapp.utils.AuthTokenHelper
import com.example.movieapp.utils.Resource
import com.google.android.material.snackbar.Snackbar


class ReviewFragment : Fragment(R.layout.fragment_review) {

    private lateinit var binding : FragmentReviewBinding
    private lateinit var mediaViewModel : MediaViewModel
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var authTokenHelper: AuthTokenHelper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentReviewBinding.bind(view)

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val mediaViewModelProviderFactory = MediaViewModelFactory(repository)
        mediaViewModel = ViewModelProvider(this, mediaViewModelProviderFactory)[MediaViewModel::class.java]
        authTokenHelper = AuthTokenHelper(requireContext())
        setupRecyclerView()
        val authorization = ApiHelper.getAuthorizationHeader(authTokenHelper.getToken()!!)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val review = reviewAdapter.differ.currentList[position]

                mediaViewModel.removeReview(review.id, authorization)
                Snackbar.make(view, "Successful delete review", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        val reviewRequest = ReviewRequest(review.content, review.mediaId, review.mediaType, review.mediaTitle, review.mediaPoster)
                        mediaViewModel.addReview(reviewRequest, authorization)
                    }
                    show()
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.rcvReviewMedia)
        mediaViewModel.getAllReview(authorization)
        mediaViewModel.listReview.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
//                    hideProgressBar()
                    response.data?.let {
                        reviewAdapter.differ.submitList(it)
                        binding.reviewCount.text = getString(R.string.review_count, it.size.toString())
                    }
                }
                is Resource.Error -> {
//                    hideProgressBar()
                    response.message?.let {
                        Log.e("TAG", "An error occured $it")
                    }
                }
                is Resource.Loading -> {
//                    showProgressBar()
                }
            }
        })
    }

    fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter(requireContext())
        binding.rcvReviewMedia.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

}