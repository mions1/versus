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
import com.example.versus.gui.FriendsActivity
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

/**
 * First activity showed
 *
 * There is the menu:
 *      - Arcade: start a math
 *      - Scores: show latest scores from greater to lower
 *      - Friends: show friends list
 *      - Exit: exit from the app
 *      - Accedi: login with google account
 *
 * Login is perform at the app start
 */
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

    /**
     * It performs login with google account
     */
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

    /**
     * Return from login
     *
     * On success, and it is the first login, save user in firestore
     */
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

    /**
     * Add user into firestore at the first login
     *
     * Save uid (unique), displayName and score_count to 0
     * score_count counts how many games the user did
     */
    private fun saveUser() {

        val db = Firebase.firestore
        val db_users = db.collection("users")

        db_users.document(user!!.uid).get().addOnSuccessListener { document ->
            if (document.get("uid") == null) {
                Log.d("TAG_ADD_USERS", "User not found: CREATE")
                var tmp_user = hashMapOf(
                    "uid" to user!!.uid,
                    "score_count" to 0,
                    "name" to user!!.displayName
                )
                db_users.document(user!!.uid).set(tmp_user)
            }
            else {
                Log.d("TAG_ADD_USERS", "User found: "+document.get("uid"))
            }
        }
    }

    /**
     * Start a game
     */
    fun arcade(v: View) {
        var intent = Intent(this, MainActivity::class.java)
        if (user != null)
            intent.putExtra("uid", user!!.uid)
        else
            intent.putExtra("uid", 0)
        startActivity(intent)
        finish()
    }

    /**
     * Show friends list
     */
    fun friends(v: View) {
        var intent = Intent(this, FriendsActivity::class.java)
        if (user != null)
            intent.putExtra("uid", user!!.uid)
        else
            intent.putExtra("uid", 0)
        startActivity(intent)
        finish()
    }

    /**
     * Show user's scores
     */
    fun scores(v: View) {
        var intent = Intent(this, ScoresActivity::class.java)
        if (user != null)
            intent.putExtra("uid", user!!.uid)
        else
            intent.putExtra("uid", 0)
        startActivity(intent)
        finish()
    }

    /**
     * Exit from the app
     */
    fun exit(v: View) {
        finish()
    }

    /**
     * Login
     */
    override fun onClick(v: View?) {
        if (v!!.id == btnLogin!!.id) {
            login()
        }
    }

}