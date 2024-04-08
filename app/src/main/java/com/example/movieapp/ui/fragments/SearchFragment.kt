package com.example.movieapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentSearchBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.adapters.MediaAdapter
import com.example.movieapp.ui.viewmodel.SearchViewModel
import com.example.movieapp.ui.viewmodel.SearchViewModelFactory
import com.example.movieapp.utils.Constants.Companion.SEARCH_MEDIA_TIME_DELAY
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding : FragmentSearchBinding
    lateinit var viewModel : SearchViewModel
    lateinit var mediaListAdapter: MediaAdapter


    val mediaType = mapOf("movie" to "movie", "tv" to "tv", "people" to "people")
    var isLoading = false
//    var currentMediaType = mediaType.getValue("movie")

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSearchBinding.bind(view)

        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.etSearch.isFocused) {
                    val imm: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                    binding.etSearch.clearFocus()
                }
            }
            true
        }

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val viewModelProviderFactory = SearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[SearchViewModel::class.java]

        binding.movieButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        setupRecyclerView(requireContext())

        mediaListAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaType", viewModel.currentMediaType.value)
                putString("mediaId", it.id.toString())
            }

            if(viewModel.currentMediaType.value == "people") {
                findNavController().navigate(R.id.action_searchFragment_to_personFragment, bundle)
            } else {
                findNavController().navigate(R.id.action_searchFragment_to_mediaDetailFragment, bundle)
            }
        }

        binding.movieButton.setOnClickListener {
            viewModel.setCurrentMediaType(mediaType["movie"] ?: "")
        }
        binding.tvButton.setOnClickListener {
            viewModel.setCurrentMediaType(mediaType["tv"] ?: "")
        }

        binding.peopleButton.setOnClickListener {
            viewModel.setCurrentMediaType(mediaType["people"] ?: "")
        }

        var job : Job? = null
        binding.etSearch.addTextChangedListener { 
            job?.cancel()
            job = MainScope().launch { 
                delay(SEARCH_MEDIA_TIME_DELAY)
                it?.let {
                    if(it.toString().isNotEmpty()) {
                        viewModel.page = 1
                        viewModel.mediaListResponse = null
                        viewModel.getMediaSearch(viewModel.currentMediaType.value!!, it.toString())
                    }
                }
            }
        }

        viewModel.currentMediaType.observe(viewLifecycleOwner
        ) {
            swipeCategory(it)
        }

        viewModel.mediaList.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        mediaListAdapter.differ.submitList(it.results.toList())
                    }
                }
                is Resource.Error -> {
                    showProgressBar()
                    response.message?.let {
                        Log.e("TAG", "An error occured $it")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    fun swipeCategory(currentMediaType: String) {
        if(currentMediaType == mediaType.getValue("movie")) {
            binding.movieButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            binding.tvButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            binding.peopleButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
        } else if(currentMediaType == mediaType.getValue("tv")) {
            binding.movieButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            binding.tvButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            binding.peopleButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
        } else {
            binding.movieButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            binding.tvButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            binding.peopleButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        }
        viewModel.page = 1
        viewModel.mediaListResponse = null
        mediaListAdapter.differ.submitList(listOf())
        viewModel.getMediaSearch(currentMediaType, binding.etSearch.text.toString())
    }

    fun setupRecyclerView(context: Context) {
        mediaListAdapter = MediaAdapter(context)

        binding.rvSearch.apply {
            adapter = mediaListAdapter
            layoutManager = GridLayoutManager(context, 2)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLastPage = false
    var isScrolling = false


    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getMediaSearch(viewModel.currentMediaType.value!!, binding.etSearch.text.toString())
                isScrolling = false
            }
        }

    }


}