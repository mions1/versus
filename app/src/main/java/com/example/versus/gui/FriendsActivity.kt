package com.example.versus.gui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.versus.HomeActivity
import com.example.versus.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Shows friends list and it allows to add a new one
 */
class FriendsActivity: Activity() {

    private var lvFriends: ListView? = null
    private var etFriend: EditText? = null
    private var friends: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_friends)
        lvFriends = findViewById(R.id.lvFriends)
        etFriend = findViewById(R.id.etFriend)

        refresh()
    }

    /**
     * Add a user as a friend if it exists
     *
     * Add the requested user in user's friends lists in firestore
     */
    fun addFriend(v: View) {
        var friend = etFriend!!.text.toString()

        if (searchFriend(friend)) {

            val db = Firebase.firestore
            var db_user_friends = db.collection("friends").document(intent.getStringExtra("uid")!!)

            var friends = db_user_friends.get()
            var friends_list = ArrayList<String>()
            var tmp_friend = hashMapOf(
                "friends" to friends_list
            )

            while (!friends.isComplete) { }
            if (friends.result?.data == null) {
                db.collection("friends").document(intent.getStringExtra("uid")!!).set(tmp_friend)
            }
            else {
                friends_list = friends.result!!.get("friends") as ArrayList<String>
            }

            friends_list.add(friend)

            tmp_friend = hashMapOf(
                "friends" to friends_list
            )

            db.collection("friends").document(intent.getStringExtra("uid")!!).set(tmp_friend)
        }
        else {
            Toast.makeText(this, "Friend $friend not found", Toast.LENGTH_LONG).show()
        }

        refresh()
    }

    /**
     * Check if the user that the user is trying to add exists
     *
     * @return true if the user exists, false otherwise
     */
    private fun searchFriend(friend: String): Boolean {
        val db = Firebase.firestore
        val db_users = db.collection("users")

        // search for user
        val query = db_users.whereEqualTo("name", friend)

        var query_result = query.get()

        while (!query_result.isComplete) { }

        if (query_result.result!!.isEmpty)  {
            Log.d("TAG_QUERY_FRIEND", "Friend not found")
            return false
        }
        else {
            Log.d("TAG_QUERY_FRIEND", "Friend found")
            return true
        }
    }

    /**
     * After adding a friends, refresh the list
     */
    private fun refresh() {
        val db = Firebase.firestore
        val db_results = db.collection("friends")

        var result = db_results.document(intent.getStringExtra("uid")!!).get()

        while (!result.isComplete) { }

        if (result.result?.data == null)
            friends = ArrayList()
        else
            friends = result.result?.get("friends") as ArrayList<String>

        var adapter_r = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friends!!)
        lvFriends!!.adapter = adapter_r
        adapter_r.notifyDataSetChanged()
    }

    fun close(v: View) {
        finish()
    }

    /**
     * Return in home
     */
    override fun finish() {
        var intent: Intent? = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        super.finish()
    }

}