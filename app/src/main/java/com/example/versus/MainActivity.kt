package com.example.versus

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.versus.arcade.Game
import com.example.versus.gui.DisplayFinish
import java.util.*


/**
 * This activity is for the game screen
 *
 * It is split into two columns: left (A) and right (B)
 * There is a Topic and two categories: on the left (A) and on the right (B)
 * There is also a Word and the goal is to put the word in the right category by swipe on the left or on the right
 * You have to do the right choose before the timer is expired: the most you do, the greater score you'll have
 *
 * In the end, DispalyFinish activity will be showed and the game is over
 *
 */
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

    /**
     * Retrive layout widgets and set listeners for timer and swipe detector
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG_VERBOSE", "MainActivity OnCreate")
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
        tvTimer!!.text = "00:17"
        gestureDetector = GestureDetector(this, this)
        gestureDetector!!.setOnDoubleTapListener(this)
    }

    /**
     * Retrive and set topic, categories and the first word
     */
    override fun onStart() {
        super.onStart()

        // get date for initialize timer
        val date = GregorianCalendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 10);
        date.set(Calendar.MINUTE, 49);

        // retrive category
        game!!.pickRandomCategory()

        // set up game
        tvTopic!!.setText(game!!.getCategory().topic)
        tvCategoryA!!.setText(game!!.getCategory().left)
        tvCategoryB!!.setText(game!!.getCategory().right)
        tvWord!!.setText(game!!.getWord())

        // initialize and start timer
        updateHandler = Handler()
        runnable = Runnable {
            getAndSetNewTime(tvTimer!!.text.toString()) // some action(s)
            updateHandler!!.postDelayed(runnable!!, 1000);
        }

        runnable!!.run()
    }

    override fun onDestroy() {
        super.onDestroy()
        // stop timer
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

    /**
     * Handle swipe
     *
     * if swipe on the left, the word goes on the left and vice versa
     */
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {

        var goOn: Boolean = false

        // add the word in the choosed side
        if (e1!!.x > e2!!.x + 120) {
            Log.d("TAG", "Swipe to left")
            goOn = onChoose("left")
        } else if (e1!!.x + 120 < e2!!.x) {
            Log.d("TAG", "Swipe to right")
            goOn = onChoose("right")
        }

        return goOn
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

    /**
     * Handle tap
     *
     * if tap on the left, the word goes on the left and vice versa
     */
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        Log.d("TAG", "Single Tap Confirmed")
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val width: Int = size.x
        val height: Int = size.y

        var goOn: Boolean = false
        if (e!!.y < height-200 && e!!.y > 200) {
            if (e!!.x < (width/2)-10 && e!!.x > 10) {
                goOn = onChoose("left")
            }
            else if (e!!.x < width-10 && e!!.x > (width/2)+10) {
                goOn = onChoose("right")
            }
        }

        return goOn
    }

    /**
     * If user taps or swipes on the left
     *
     * add the word on the left side
     */
    private fun onLeft(word: TextView): Boolean {
        game!!.getAnswers().left.add(tvWord!!.text.toString())
        llA!!.addView(word)
        return true
    }

    /**
     * If user taps or swipes on the right
     *
     * add the word on the right side
     */
    private fun onRight(word: TextView): Boolean {
        game!!.getAnswers().right.add(tvWord!!.text.toString())
        word.gravity = Gravity.RIGHT
        llB!!.addView(word)
        return true
    }

    /**
     * Accordly with the side, the word goes on the left or on the right
     */
    private fun onChoose(side: String): Boolean {

        var word: TextView = TextView(this)
        word.text = tvWord!!.text.toString()
        word.width = ViewGroup.LayoutParams.MATCH_PARENT

        var goOn: Boolean = false
        if (side == "left") {
            onLeft(word)
            goOn = true
        }
        else if (side == "right") {
            onRight(word)
            goOn = true
        }

        // if the game is over, finish, otherwise go on
        game!!.goOn()
        if (game!!.gameover) {
            finish()
        }

        // if go on, pick the next word
        if (goOn)
            tvWord!!.text = game!!.getWord()

        return goOn
    }

    /**
     * Every second, decrease timer and blink
     *
     * if timer is < 10, put a 0 before the actual second
     * if time is over, finish
     *
     * @param time current time
     * @return new time to set into the timer
     */
    fun getAndSetNewTime(time: String): String {
        var sec = time.substring(time.indexOf(":") + 1).toInt()
        sec--
        if (sec < 10 && sec >= 0)
            this.tvTimer!!.text = "00:0" + sec.toString()
        else
            this.tvTimer!!.text = "00:" + sec.toString()

        if (sec % 2 == 0) {
            tvTimer!!.setBackgroundResource(R.color.color_primary1)
            tvTimer!!.setTextColor(resources.getColor(R.color.color_secondary1))
        }
        else {
            tvTimer!!.setBackgroundResource(R.color.color_secondary1)
            tvTimer!!.setTextColor(resources.getColor(R.color.color_primary1))
        }
        if (sec <= 0) {
            game!!.gameover = true
            finish()
        }

        return "00:" + sec.toString()
    }

    /**
     * When the game is over, call DisplayFinish and pass the result
     */
    override fun finish() {
        var uid = intent.getStringExtra("uid")
        var rights = game!!.getResults().rights
        var wrongs = game!!.getResults().wrongs
        var intent: Intent = Intent(this, DisplayFinish::class.java).apply {
            putExtra("rights", rights)
            putExtra("wrongs", wrongs)
            putExtra("category_id", game!!.getCategory().id)
            putExtra("uid", uid)
        }
        startActivity(intent)
        super.finish()
    }
}