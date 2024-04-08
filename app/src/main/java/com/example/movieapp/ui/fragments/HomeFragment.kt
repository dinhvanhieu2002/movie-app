package com.example.movieapp.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.ui.adapters.HeroSlideAdapter
import com.example.movieapp.ui.adapters.MediaListHomeAdapter
import com.example.movieapp.ui.viewmodel.HomeViewModel
import com.example.movieapp.ui.viewmodel.HomeViewModelFactory
import com.example.movieapp.utils.Resource


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel : HomeViewModel
    private lateinit var heroSlideAdapter: HeroSlideAdapter
    private lateinit var mediaPopularMovieAdapter: MediaListHomeAdapter
    private lateinit var mediaPopularSeriesAdapter: MediaListHomeAdapter
    private lateinit var mediaTopRatedMovieAdapter: MediaListHomeAdapter
    private lateinit var mediaTopRatedSeriesAdapter: MediaListHomeAdapter
    lateinit var binding: FragmentHomeBinding
    lateinit var mBinding: ActivityMainBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val viewModelProviderFactory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]

        binding = FragmentHomeBinding.bind(view)
        mBinding = (activity as MainActivity).binding

        

        setupHeroSlide(requireContext())
        setupRecyclerView()

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 50) {
                mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_bar_black))
            } else {
                mBinding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        viewModel.mediaHeroPager.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideGlobalLoading()
                    response.data?.let {
                        heroSlideAdapter.differ.submitList(it.results)
                    }
                }
                is Resource.Error -> {
                    (requireActivity() as MainActivity).hideGlobalLoading()

                    response.message?.let {
                        Log.e("TAG", "An error occured $it")
                    }
                }
                is Resource.Loading -> {
                    (requireActivity() as MainActivity).showGlobalLoading()
                }
            }
        })

        viewModel.mediaPopularMovie.observe(viewLifecycleOwner, Observer {
            mediaPopularMovieAdapter.differ.submitList(it)

        })

        viewModel.mediaPopularSeries.observe(viewLifecycleOwner, Observer {
            mediaPopularSeriesAdapter.differ.submitList(it)

        })

        viewModel.mediaTopRatedMovie.observe(viewLifecycleOwner, Observer {
            mediaTopRatedMovieAdapter.differ.submitList(it)

        })

        viewModel.mediaTopRatedSeries.observe(viewLifecycleOwner, Observer {
            mediaTopRatedSeriesAdapter.differ.submitList(it)

        })

        heroSlideAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", "movie")
            }
            findNavController().navigate(R.id.action_homeFragment_to_mediaDetailFragment, bundle)
        }

        mediaPopularMovieAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", "movie")
            }
            findNavController().navigate(R.id.action_homeFragment_to_mediaDetailFragment, bundle)
        }

        mediaPopularSeriesAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", "tv")
            }
            findNavController().navigate(R.id.action_homeFragment_to_mediaDetailFragment, bundle)
        }

        mediaTopRatedMovieAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", "movie")
            }
            findNavController().navigate(R.id.action_homeFragment_to_mediaDetailFragment, bundle)
        }

        mediaTopRatedSeriesAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", "tv")
            }
            findNavController().navigate(R.id.action_homeFragment_to_mediaDetailFragment, bundle)
        }
    }


    private fun setupHeroSlide(context: Context) {
        heroSlideAdapter = HeroSlideAdapter("movie", context)
        binding.vpHero.apply {
            adapter = heroSlideAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            isNestedScrollingEnabled = false
        }
    }

    private fun setupRecyclerView() {
        mediaPopularSeriesAdapter = MediaListHomeAdapter()
        mediaPopularMovieAdapter = MediaListHomeAdapter()
        mediaTopRatedSeriesAdapter = MediaListHomeAdapter()
        mediaTopRatedMovieAdapter = MediaListHomeAdapter()
        binding.rcvPopularMovie.apply {
            adapter = mediaPopularMovieAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvPopularSeries.apply {
            adapter = mediaPopularSeriesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvTopRatedMovie.apply {
            adapter = mediaTopRatedMovieAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }

        binding.rcvTopRatedSeries.apply {
            adapter = mediaTopRatedSeriesAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            isNestedScrollingEnabled = false
        }
    }



}