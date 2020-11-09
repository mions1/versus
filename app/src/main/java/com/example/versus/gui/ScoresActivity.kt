package com.example.versus.gui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.versus.HomeActivity
import com.example.versus.R
import com.example.versus.arcade.Alternatives
import com.example.versus.arcade.Category
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ScoresActivity: Activity() {

    private var lvScores: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scores)
        lvScores = findViewById(R.id.lvScores)

        var scores: ArrayList<Double> = ArrayList()

        val db = Firebase.firestore
        val db_results = db.collection("results")

        var i: Int = 0
        while (true) {
            var result =
                db_results.document(intent.getStringExtra("uid") + "_" + i.toString()).get()

            while (!result.isComplete) { }

            if (result.result?.data == null) { break }

            scores.add(result.result?.get("score") as Double)
            i += 1
        }

        scores.sort()

        var adapter_r = ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, scores!!)
        lvScores!!.adapter = adapter_r
        adapter_r.notifyDataSetChanged()

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