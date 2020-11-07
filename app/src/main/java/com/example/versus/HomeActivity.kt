package com.example.versus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class HomeActivity: Activity(), View.OnClickListener {

    var btnLogin: SignInButton? = null
    var loginApp: LoginApp? = null


    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnLogin = findViewById(R.id.sign_in_button)
        loginApp = LoginApp()

        btnLogin!!.setOnClickListener(this)
    }

    private fun login() {
        Toast.makeText(this, "LOGIN", Toast.LENGTH_LONG).show()
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                for (i in 0..btnLogin!!.childCount) {
                    var v: View? = btnLogin!!.getChildAt(i)

                    if (v is TextView) {
                        var tv: TextView = v
                        tv.text = "Bentornato, ${user!!.displayName}"
                    }
                }
            }
        }
    }

    fun arcade(v: View) {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(v: View) {
        finish()
    }

    override fun onClick(v: View?) {
        if (v!!.id == btnLogin!!.id) {
            login()
        }
    }

}