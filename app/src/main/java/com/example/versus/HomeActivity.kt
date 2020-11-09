package com.example.versus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.versus.arcade.Alternatives
import com.example.versus.arcade.Category
import com.example.versus.gui.ScoresActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity: Activity(), View.OnClickListener {

    var btnLogin: SignInButton? = null
    var loginApp: LoginApp? = null

    private var user: FirebaseUser? = null

    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btnLogin = findViewById(R.id.sign_in_button)
        loginApp = LoginApp()

        btnLogin!!.setOnClickListener(this)
        login()
    }

    private fun login() {
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
                user = FirebaseAuth.getInstance().currentUser

                for (i in 0..btnLogin!!.childCount) {
                    var v: View? = btnLogin!!.getChildAt(i)

                    if (v is TextView) {
                        var tv: TextView = v
                        saveUser()
                        tv.text = "Bentornato, ${user!!.displayName}"
                    }
                }
            }
        }
    }

    private fun saveUser() {

        val db = Firebase.firestore
        val db_users = db.collection("users")

        db_users.document(user!!.uid).get().addOnSuccessListener { document ->
            if (document.get("uid") == null) {
                Log.d("TAG_ADD_USERS", "User not found: CREATE")
                var tmp_user = hashMapOf(
                    "uid" to user!!.uid,
                    "score_count" to 0
                )
                db_users.document(user!!.uid).set(tmp_user)
            }
            else {
                Log.d("TAG_ADD_USERS", "User found: "+document.get("uid"))
            }
        }
    }

    fun arcade(v: View) {
        var intent = Intent(this, MainActivity::class.java)
        if (user != null)
            intent.putExtra("uid", user!!.uid)
        else
            intent.putExtra("uid", 0)
        startActivity(intent)
        finish()
    }

    fun scores(v: View) {
        var intent = Intent(this, ScoresActivity::class.java)
        if (user != null)
            intent.putExtra("uid", user!!.uid)
        else
            intent.putExtra("uid", 0)
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