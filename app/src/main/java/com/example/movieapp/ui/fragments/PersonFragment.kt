package com.example.movieapp.ui.fragments

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
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentPersonBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.activities.MainActivity
import com.example.movieapp.ui.adapters.MediaListAdapter
import com.example.movieapp.ui.viewmodel.MediaViewModel
import com.example.movieapp.ui.viewmodel.MediaViewModelFactory
import com.example.movieapp.utils.ApiHelper
import com.example.movieapp.utils.Resource


class PersonFragment : Fragment(R.layout.fragment_person) {

    private lateinit var binding : FragmentPersonBinding
    private lateinit var mediaViewModel : MediaViewModel
    private lateinit var personMediaAdapter: MediaListAdapter
    private lateinit var mediaId : String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentPersonBinding.bind(view)
        mediaId = arguments?.getString("mediaId",  "").toString()


        val repository = MediaRepository(MediaDatabase(requireContext()))
        val mediaViewModelProviderFactory = MediaViewModelFactory(repository)
        mediaViewModel = ViewModelProvider(this, mediaViewModelProviderFactory)[MediaViewModel::class.java]

        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > 50) {
                (requireActivity() as MainActivity).binding.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_bar_black))
            } else {
                (requireActivity() as MainActivity).binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        setupRecyclerView()
        mediaViewModel.getPersonDetail(mediaId)
        mediaViewModel.getPersonMedias(mediaId)
        mediaViewModel.personDetail.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    (requireActivity() as MainActivity).hideGlobalLoading()
                    response.data?.let {
                        binding.apply {
                            tvName.text = it.name
                            tvBio.text = it.biography
                            Glide.with(requireContext())
                                .load(ApiHelper.getPosterPath((it.profilePath)))
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                                .into(ivImage)
                        }
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

        mediaViewModel.personMedias.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    response.data?.let {
                        personMediaAdapter.differ.submitList(it.cast)
                    }
                }
                is Resource.Error -> {
                    binding.paginationProgressBar.visibility = View.GONE

                    response.message?.let {
                        Log.e("TAG", "An error occured $it")
                    }
                }
                is Resource.Loading -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE

                }
            }
        })

        personMediaAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mediaId", it.id.toString())
                putString("mediaType", it.mediaType)
            }
            findNavController().navigate(R.id.mediaDetailFragment, bundle)
        }
    }

    fun setupRecyclerView() {
        personMediaAdapter = MediaListAdapter(requireContext())
        binding.rvPersonMedias.apply {
            adapter = personMediaAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

//    private var isLoading = false
//    private fun hideProgressBar() {
//        binding.loadingLayout.visibility = View.GONE
//        isLoading = false
//    }
//
//    private fun showProgressBar() {
//        binding.loadingLayout.visibility = View.VISIBLE
//        isLoading = true
//    }

}