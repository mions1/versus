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

    override fun onStart() {
        super.onStart()

        val rights: ArrayList<String>? = intent.getStringArrayListExtra("rights")
        val wrongs = intent.getStringArrayListExtra("wrongs")

        var adapter_r = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rights!!)
        lvRight!!.adapter = adapter_r
        adapter_r.notifyDataSetChanged()

        var adapter_w = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wrongs!!)
        lvWrong!!.adapter = adapter_w
        adapter_w.notifyDataSetChanged()

        tvTotalRight!!.text = "Totale: "+rights!!.size.toString()
        tvTotalWrong!!.text = "Totale: "+wrongs!!.size.toString()


        val n_rights = rights!!.size.toDouble()
        val n_wrongs = Math.max(1.0, wrongs!!.size.toDouble())
        val tot = n_rights+n_wrongs
        val score = (n_rights*tot)/n_wrongs

        tvScore!!.text = "Score: ${Math.round(score * 100) / 100.0}"

        // FIXME: Finisci la roba dei dati
        var result = hashMapOf(
            "uid" to intent.getStringExtra("uid"),
            "category_id" to intent.getIntExtra("category_id", 0),
            "score" to score
        )

        val db = Firebase.firestore
        db.collection("results")
            .add(result)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG_STORE_DATA", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener {e ->
                Log.d("TAG_STORE_DATA", "Error adding document", e)
            }
    }

    fun close(v: View) {
        finish()
    }

    override fun finish() {
        var intent: Intent? = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        super.finish()
    }

}