package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentFavoriteBinding
import com.example.movieapp.db.MediaDatabase
import com.example.movieapp.repositories.MediaRepository
import com.example.movieapp.ui.adapters.MediaFavoriteAdapter
import com.example.movieapp.ui.viewmodel.MediaViewModel
import com.example.movieapp.ui.viewmodel.MediaViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private lateinit var binding : FragmentFavoriteBinding
    private lateinit var mediaViewModel : MediaViewModel
    private lateinit var favoriteAdapter: MediaFavoriteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentFavoriteBinding.bind(view)

        val repository = MediaRepository(MediaDatabase(requireContext()))
        val mediaViewModelProviderFactory = MediaViewModelFactory(repository)
        mediaViewModel = ViewModelProvider(this, mediaViewModelProviderFactory)[MediaViewModel::class.java]

        setupRecyclerView()

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
                val media = favoriteAdapter.differ.currentList[position]
                mediaViewModel.removeFavorite(media)

                Snackbar.make(view, "Successful delete media", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        mediaViewModel.addFavorite(media)
                    }
                    show()
                }
            }

        })

        itemTouchHelper.attachToRecyclerView(binding.rcvFavoriteMedia)

        mediaViewModel.getAllFavorite().observe(viewLifecycleOwner, Observer {
            favoriteAdapter.differ.submitList(it)
            binding.favoriteCount.text = getString(R.string.favorite_count, it.size.toString())
        })

    }

    fun setupRecyclerView() {
        favoriteAdapter = MediaFavoriteAdapter(requireContext())
        binding.rcvFavoriteMedia.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }


}