package com.example.movieapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.api.models.UserRef
import com.example.movieapp.databinding.FragmentHandleRoomBinding
import com.example.movieapp.ui.activities.RoomMovieActivity
import com.example.movieapp.utils.AuthTokenHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class HandleRoomFragment : Fragment(R.layout.fragment_handle_room) {
    private lateinit var authTokenHelper: AuthTokenHelper

    private lateinit var binding : FragmentHandleRoomBinding
    val usersRef = FirebaseDatabase.getInstance().getReference("users")
    val actionsRef = FirebaseDatabase.getInstance().getReference("actions")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHandleRoomBinding.bind(view)
        authTokenHelper = AuthTokenHelper(requireContext())
        binding.btnJoin.setOnClickListener { handleJoin() }

    }

    private fun handleJoin() {
        val roomId = binding.etRoomId.text.toString()
        if(roomId.isNotEmpty()) {
            actionsRef.get().addOnSuccessListener {
                it.children.forEach {
                    if(it.key == roomId) {
                        val userId = authTokenHelper.getUserId().toString()
                        val displayName = authTokenHelper.getDisplayName().toString()
                        val userRef = UserRef(userId, displayName)
                        usersRef.child(it.key.toString()).child(userId).setValue(userRef)

                        val intent = Intent(requireContext(), RoomMovieActivity::class.java)
                        intent.putExtra("roomId", it.key)
                        intent.putExtra("userId", userId)
                        intent.putExtra("displayName", displayName)
                        startActivity(intent)
                    } else {
                        Snackbar.make(requireView(), "Room ID not exist", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Snackbar.make(requireView(), "Please enter room ID", Snackbar.LENGTH_SHORT).show()
        }
    }
}