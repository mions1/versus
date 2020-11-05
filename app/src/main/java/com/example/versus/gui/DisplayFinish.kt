package com.example.versus.gui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.versus.HomeActivity
import com.example.versus.R
import kotlinx.android.synthetic.main.activity_finish.view.*
import java.lang.Integer.min
import kotlin.math.round
import kotlin.math.roundToLong

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


        var n_rights = rights!!.size.toFloat()
        var n_wrongs = wrongs!!.size.toFloat()
        var tot = n_rights+n_wrongs
        var score = (n_rights*tot)/n_wrongs

        tvScore!!.text = "Score: ${Math.round(score * 100) / 100.0}"

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