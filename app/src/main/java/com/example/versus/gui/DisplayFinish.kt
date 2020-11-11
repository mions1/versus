package com.example.versus.gui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.versus.HomeActivity
import com.example.versus.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_finish.view.*
import java.lang.Integer.min
import kotlin.math.round
import kotlin.math.roundToLong

data class Results(var uid: String?, var category_id: Int?, var score: Double?)

/**
 * It display game result after a match
 *
 * It shows the rights and the wrongs choices, and the score
 * The result is got from intent passed by MainActivity
 */
class DisplayFinish: Activity() {

    private var lvRight: ListView? = null
    private var lvWrong: ListView? = null

    private var tvTotalRight: TextView? = null
    private var tvTotalWrong: TextView? = null

    private var tvScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        lvRight = findViewById(R.id.lvRight)
        lvWrong = findViewById(R.id.lvWrong)

        tvTotalRight = findViewById(R.id.tvTotalRight)
        tvTotalWrong = findViewById(R.id.tvTotalWrong)

        tvScore = findViewById(R.id.tvScore)

    }

    /**
     * Get and show result
     *
     * Result is also stored in firestore with:
     *      - uid: user id
     *      - category_id: category id
     */
    override fun onStart() {
        super.onStart()

        // get result from intent
        val rights: ArrayList<String>? = intent.getStringArrayListExtra("rights")
        val wrongs = intent.getStringArrayListExtra("wrongs")

        // set result on ListViews
        var adapter_r = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rights!!)
        lvRight!!.adapter = adapter_r
        adapter_r.notifyDataSetChanged()

        var adapter_w = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wrongs!!)
        lvWrong!!.adapter = adapter_w
        adapter_w.notifyDataSetChanged()

        // set total
        tvTotalRight!!.text = "Totale: "+rights!!.size.toString()
        tvTotalWrong!!.text = "Totale: "+wrongs!!.size.toString()


        // compute score as: #rights*tot_ans/#wrongs
        val n_rights = rights!!.size.toDouble()
        val n_wrongs = Math.max(1.0, wrongs!!.size.toDouble())
        val tot = n_rights+n_wrongs
        val score = (n_rights*tot)/n_wrongs

        tvScore!!.text = "Score: ${Math.round(score * 100) / 100.0}"

        // save result on firestore
        var result = hashMapOf(
            "uid" to intent.getStringExtra("uid"),
            "category_id" to intent.getIntExtra("category_id", 0),
            "score" to score
        )

        val db = Firebase.firestore
        db.collection("results").document(intent.getStringExtra("uid")!!+"_"+getAndIncScoreCount().toString()).set(result)
        Log.d("TAG_ADD_RESULT", "ADD result to: "+intent.getStringExtra("uid")!!)
    }

    /**
     * Increase current user's score_count (how many games the user did) and return the current one
     *
     * @return current user's score_count
     */
    private fun getAndIncScoreCount(): Int {
        val db = Firebase.firestore
        var db_users = db.collection("users")
        var user = db_users.document(intent.getStringExtra("uid")!!).get()

        while (!user.isComplete) { }

        if (user.result?.data == null ) { return 0 }

        var count = user.result?.get("score_count")!!.toString().toInt()
        db_users.document(intent.getStringExtra("uid")!!).update("score_count", count+1)

        return user.result?.get("score_count")!!.toString().toInt()
    }

    fun close(v: View) {
        finish()
    }

    /**
     * In the end, return in home
     */
    override fun finish() {
        var intent: Intent? = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        super.finish()
    }

}