package com.example.movieapp.ui.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.api.models.ChatMessage
import com.example.movieapp.utils.AuthTokenHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("UNCHECKED_CAST")
class RoomMovieViewModelFactory(val roomId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomMovieViewModel(roomId) as T
    }
}

class RoomMovieViewModel(private val roomId: String) : ViewModel() {
    private val dbChatMessages: DatabaseReference = FirebaseDatabase.getInstance().getReference("messages").child(roomId)
    private val dbUsers: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(roomId)
    private val _messages: MutableLiveData<List<ChatMessage>> = MutableLiveData()
    private val _countMB: MutableLiveData<Int> = MutableLiveData()

    val messages: LiveData<List<ChatMessage>>
        get() = _messages

    val countMB: LiveData<Int>
        get() = _countMB

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val newMessage = dataSnapshot.getValue(ChatMessage::class.java)
            val currentMessages = _messages.value ?: emptyList()
            val updatedMessages = currentMessages + listOfNotNull(newMessage)
            _messages.postValue(updatedMessages)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            // ...
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            // ...
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // ...
        }
    }

    private fun getMessages() {
        dbChatMessages.get().addOnSuccessListener {
            if (it.exists()) {
                val messages = it.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                _messages.postValue(messages)
            }
        }
    }

    init {
        dbChatMessages.addChildEventListener(childEventListener)
        getMessages()
    }

    fun handleMemberCount() {
        dbUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val memberCount = snapshot.children.count()
                _countMB.value = memberCount
                Log.e("Seek get error", "cccccccccccccccccc: $memberCount")
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun handleInOut(view: View, authTokenHelper: AuthTokenHelper) {
        dbUsers.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("user add", snapshot.key.toString())
                val currentUserId = authTokenHelper.getUserId().toString()
                val userId = snapshot.key.toString()
                if(currentUserId != userId) {
                    val displayName = snapshot.child("displayName").getValue(String::class.java)
                    Log.e("user add", displayName.toString())
                    Snackbar.make(view, "$displayName has been joined room", Snackbar.LENGTH_SHORT).show()
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.e("user add", snapshot.key.toString())
                val currentUserId = authTokenHelper.getUserId().toString()
                val userId = snapshot.key.toString()
                if(currentUserId != userId) {
                    val displayName = snapshot.child("displayName").getValue(String::class.java)
                    Log.e("user add", displayName.toString())
                    Snackbar.make(view, "$displayName has been quit room", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun sendMessage(chatMessage: ChatMessage) {
        val ref = dbChatMessages.push()
        ref.setValue(chatMessage)
    }

    override fun onCleared() {
        super.onCleared()
        dbChatMessages.removeEventListener(childEventListener)
    }

}