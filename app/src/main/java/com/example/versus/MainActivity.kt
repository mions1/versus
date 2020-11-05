package com.example.versus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.versus.arcade.Game
import java.util.*
import kotlin.concurrent.timer


class MainActivity : Activity(), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var tvWord: TextView? = null
    private var tvTopic: TextView? = null
    private var tvCategoryA: TextView? = null
    private var tvCategoryB: TextView? = null
    private var layout: ConstraintLayout? = null
    private var llA: LinearLayout? = null
    private var llB: LinearLayout? = null


    private var updateHandler: Handler? = null
    private var runnable: Runnable? = null
    private var tvTimer: TextView? = null
    private var game: Game? = null

    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arcade)

        game = Game()

        tvWord = findViewById(R.id.tvNewWord)
        tvTopic = findViewById(R.id.tvTopic)
        tvCategoryA = findViewById(R.id.tvCategoryA)
        tvCategoryB = findViewById(R.id.tvCategoryB)
        layout = findViewById(R.id.layout)
        llA = findViewById(R.id.llA)
        llB = findViewById(R.id.llB)
        tvTimer = findViewById(R.id.tvTimer)
        tvTimer!!.text = "00:03"
        gestureDetector = GestureDetector(this, this)
        gestureDetector!!.setOnDoubleTapListener(this)
    }

    override fun onStart() {
        super.onStart()
        val date = GregorianCalendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 10);
        date.set(Calendar.MINUTE, 49);

        game!!.pickRandomCategory()

        tvTopic!!.setText(game!!.getCategory().topic)
        tvCategoryA!!.setText(game!!.getCategory().left)
        tvCategoryB!!.setText(game!!.getCategory().right)
        tvWord!!.setText(game!!.getWord())

        updateHandler = Handler()
        runnable = Runnable {
            getAndSetNewTime(tvTimer!!.text.toString()) // some action(s)
            updateHandler!!.postDelayed(runnable!!, 1000);
        }

        runnable!!.run()
    }

    override fun onDestroy() {
        super.onDestroy()
        updateHandler!!.removeCallbacks(runnable!!);
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetector?.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent?) {
        Log.d("TAG", "Show Press")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d("TAG", "Single Tap")
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        Log.d("TAG", "On Down")
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        var word: TextView = TextView(this)
        word.text = tvWord!!.text.toString()
        word.width = ViewGroup.LayoutParams.MATCH_PARENT


        var goOn: Boolean = false
        if (e1!!.x > e2!!.x + 120) {
            Log.d("TAG", "Swipe to left")
            game!!.getAnswers().left.add(tvWord!!.text.toString())
            llA!!.addView(word)
            goOn = true
        } else if (e1!!.x + 120 < e2!!.x) {
            Log.d("TAG", "Swipe to right")
            game!!.getAnswers().right.add(tvWord!!.text.toString())
            word.gravity = Gravity.RIGHT
            llB!!.addView(word)
            goOn = true
        }

        game!!.goOn()
        if (game!!.gameover) {
            finish()
        }

        if (goOn)
            tvWord!!.text = game!!.getWord()

        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d("TAG", "Scroll")
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        Log.d("TAG", "Long press")
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        Log.d("TAG", "Dobule tap")
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        Log.d("TAG", "DoubleTapEvent")
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.d("TAG", "Single Tap Confirmed")
        return true
    }

    fun getAndSetNewTime(time: String): String {
        var sec = time.substring(time.indexOf(":") + 1).toInt()
        sec--
        if (sec < 10 && sec >= 0)
            this.tvTimer!!.text = "00:0" + sec.toString()
        else
            this.tvTimer!!.text = "00:" + sec.toString()

        if (sec % 2 == 0) {
            tvTimer!!.setBackgroundResource(R.color.colorGameCatA)
            tvTimer!!.setTextColor(resources.getColor(R.color.colorGameCatB))
        }
        else {
            tvTimer!!.setBackgroundResource(R.color.colorGameCatB)
            tvTimer!!.setTextColor(resources.getColor(R.color.colorGameCatA))
        }
        if (sec <= 0) {
            game!!.gameover = true
            finish()
        }

        return "00:" + sec.toString()
    }

    override fun finish() {
        var right = game!!.getResults().right
        var wrong = game!!.getResults().wrong
        var intent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("Right", right)
            putExtra("Wrong", wrong)
        }
        startActivity(intent)
        super.finish()
    }
}