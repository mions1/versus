package com.example.versus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlin.system.exitProcess

class HomeActivity: Activity() {

    var tvRight: TextView? = null
    var tvWrong: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvRight = findViewById(R.id.tvRight)
        tvWrong = findViewById(R.id.tvWrong)

        var right = intent.getIntExtra("Right", -1)
        var wrong = intent.getIntExtra("Wrong", -1)

        if (right != -1) {
            tvRight!!.text = right.toString()
            tvWrong!!.text = wrong.toString()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    fun arcade(v: View) {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun exit(v: View) {
        finish()
    }

}