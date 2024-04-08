package com.example.movieapp.ui.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.databinding.FragmentMediaListBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.ui.adapters.HeroSlideAdapter
import com.example.movieapp.ui.adapters.MediaListAdapter
import com.example.movieapp.ui.viewmodel.MediaListViewModel
import com.example.movieapp.ui.viewmodel.MediaListViewModelFactory
import com.example.movieapp.utils.Resource


class MediaListFragment : Fragment(R.layout.fragment_media_list) {
//    val args : MediaListFragmentArgs by navArgs()

    private lateinit var binding : FragmentMediaListBinding
    private lateinit var mBinding : ActivityMainBinding
    private lateinit var mediaType : String
    lateinit var viewModel : MediaListViewModel
    lateinit var heroSlideAdapter: HeroSlideAdapter
    lateinit var mediaListAdapter: MediaListAdapter
    val mediaCategory = mapOf("popular" to "popular","topRate" to "top_rated")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMediaListBinding.bind(view)
        mBinding = (activity as MainActivity).binding

        mediaType = arguments?.getString("mediaType",  "movie").toString()

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val viewModelProviderFactory = MediaListViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[MediaListViewModel::class.java]

        binding.popularButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        binding.mediaType.text = if(mediaType == "movie") "Movies" else "TV Series"

        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > 50) {
                mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_bar_black))
            } else {
                mBinding.toolbar.setBackgroundColor(Color.TRANSPARENT)

            }
        }

        binding.popularButton.setOnClickListener {
            swipeCategory(mediaCategory["popular"] ?: "")
        }
        binding.topRatedButton.setOnClickListener {
            swipeCategory(mediaCategory["topRate"] ?: "")
        }

        binding.loadMoreButton.setOnClickListener {
            viewModel.getMediaList(mediaType, viewModel.currentCategory.value!!)
        }


        setupHeroSlide(requireContext())
        setupRecyclerView(requireContext())


        heroSlideAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", mediaType)
            }
            findNavController().navigate(R.id.action_mediaListFragment_to_mediaDetailFragment, bundle)
        }

        mediaListAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", mediaType)
            }
            findNavController().navigate(R.id.action_mediaListFragment_to_mediaDetailFragment, bundle)
        }

        viewModel.currentCategory.observe(viewLifecycleOwner) {
            swipeCategory(it)
        }

        viewModel.mediaHeroPager.observe(viewLifecycleOwner, Observer {
            heroSlideAdapter.differ.submitList(it)
        })

        viewModel.mediaList.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
//                    hideProgressBar()
                    (requireActivity() as MainActivity).hideGlobalLoading()

                    response.data?.let {
                        mediaListAdapter.differ.submitList(it.results.toList())
//                        isLastPage = viewModel.startFrom == 100
//                        if(isLastPage) {
//                            binding.rcvMediaList.setPadding(0,0,0,0)
//                        }
                    }
                }
                is Resource.Error -> {
//                    showProgressBar()
                    (requireActivity() as MainActivity).hideGlobalLoading()

                    response.message?.let {
                        Log.e("TAG", "An error occured $it")
                    }
                }
                is Resource.Loading -> {
                    (requireActivity() as MainActivity).showGlobalLoading()

//                    showProgressBar()
                }
            }

        })


    }

    var isLoading = false
    fun swipeCategory(currentCategory: String) {
        if(currentCategory == mediaCategory.getValue("popular")) {
            binding.topRatedButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            binding.popularButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            binding.topRatedButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            binding.popularButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
        }
        viewModel.page = 1
        viewModel.mediaListResponse = null
        viewModel.getHeroPager(mediaType, currentCategory)
        viewModel.getMediaList(mediaType, currentCategory)
    }

    fun setupHeroSlide(context: Context) {
        heroSlideAdapter = HeroSlideAdapter(mediaType, context)
        binding.vpHero.apply {
            adapter = heroSlideAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
    }

    fun setupRecyclerView(context: Context) {
        mediaListAdapter = MediaListAdapter(context)

        binding.rcvMediaList.apply {
            adapter = mediaListAdapter
            layoutManager = GridLayoutManager(context, 2)
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


}