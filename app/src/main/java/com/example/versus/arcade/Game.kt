package com.example.versus.arcade
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

data class Category(var id: Int, var topic: String, var left: String, var right: String)
data class Alternatives(var id: Int, var words: HashMap<String,Int>)
data class Answers(var left: ArrayList<String>, var right: ArrayList<String>)
data class Results(var right: Int, var wrong: Int, var rights: ArrayList<String>, var wrongs: ArrayList<String>)

/**
 * This class handle the game
 *
 * Retrive categories stored in firestore
 * When a game starts, pick a random category and start the game
 * If the words are finished, gameover (but maybe the game can over just for expired timer)
 */
class Game {

    private var category: Category? = null                  // category picked
    private var alternatives: ArrayList<String>? = null     // words list of the picked category
    private var answers: Answers? = null                    // user's answers list
    private var results: Results? = null                    // it store how many rights and wrongs, and list of them
    private var index: Int? = null                          // category/alternatives id
    private var word: String? = null                        // current word

    private var CATEGORIES: ArrayList<Category>? = null         // all possible categories
    private var ALTERNATIVES: ArrayList<Alternatives>? = null   // categories' words list

    var gameover: Boolean = false

    /**
     * Retrive categories and alternatives from firestore
     */
    init {
        Log.d("TAG_INIT_GAME","INIT_GAME")

        CATEGORIES = ArrayList()
        ALTERNATIVES = ArrayList()

        val db = Firebase.firestore
        val db_categories = db.collection("categories")
        val db_alternatives = db.collection("alternatives")

        var i: Int = 0
        var end = false
        while (!end) {
            var category = db_categories.document(i.toString()).get()
            var alternatives = db_alternatives.document(i.toString()).get()

            while (!category.isComplete) { }
            while (!alternatives.isComplete) { }

            if (category.result?.data == null) { break }

            var tmp_category = Category(i,
                                category.result?.get("topic")!!.toString(),
                                category.result?.get("left")!!.toString(),
                                category.result?.get("right")!!.toString()
                                )

            var tmp_alternatives = HashMap<String, Int>()
            for (left in alternatives.result?.get("left") as ArrayList<String>) {
                tmp_alternatives[left] = 0
            }
            for (right in alternatives.result?.get("right") as ArrayList<String>) {
                tmp_alternatives[right] = 1
            }

            CATEGORIES!!.add(tmp_category)
            ALTERNATIVES!!.add(Alternatives(i, tmp_alternatives))

            i += 1
        }

        Log.d("TAG_ALL_CATEGORIES", CATEGORIES.toString())
        Log.d("TAG_ALL_ALTERNATIVES", ALTERNATIVES.toString())
    }

    /**
     * Pick a random category from the pool and set all associated fields
     */
    fun pickRandomCategory() {
        var index: Int = Random.nextInt(CATEGORIES!!.size)
        this.category = this.CATEGORIES!!.get(index)
        this.alternatives = ArrayList(this.ALTERNATIVES!!.get(index).words.keys)
        this.answers = Answers(ArrayList<String>(), ArrayList<String>())
        this.results = Results(0,0, ArrayList(), ArrayList())
        this.index = index
        getRandomWord()
    }

    /**
     * Pick the next word in alternatives (by getRandomWord())
     *
     * @return true if there is another, false if they are finished (but maybe it can never happens)
     */
    fun goOn(): Boolean {
        if (this.word == null) {
            getRandomWord()
            return true
        }
        this.alternatives!!.remove(this.word!!)
        var res: Boolean = getRandomWord()
        if (!res) {
            gameover = true
            return false
        }
        return true
    }

    /**
     * Get the next word randomly
     *
     * @return true if there is another, false if they are finished (but maybe it can never happens)
     */
    private fun getRandomWord(): Boolean {
        if (alternatives == null)
            this.pickRandomCategory()
        if (alternatives!!.size == 0) {
            computeResults()
            return false
        }
        var index: Int = Random.nextInt(alternatives!!.size)
        this.word = alternatives!!.get(index)
        return true
    }

    /**
     * When the game is over, compute result
     *
     * Counts how many rights and wrongs it did, and store them into the related list
     */
    private fun computeResults(): Results {
        for ( (word,ans) in this.ALTERNATIVES!![this.index!!].words ) {
            Log.d("Results: ", word+": "+ans)
            if (word in this.answers!!.left && ans == 0) {
                this.results!!.right++
                this.results!!.rights.add(word)
            }
            else if (word in this.answers!!.right && ans == 1) {
                this.results!!.right++
                this.results!!.rights.add(word)
            }
            else if (word in this.answers!!.left && ans == 1) {
                this.results!!.wrong++
                this.results!!.wrongs.add(word)
            }
            else if (word in this.answers!!.right && ans == 0) {
                this.results!!.wrong++
                this.results!!.wrongs.add(word)
            }
        }
        Log.d("MyAnsw: ", this.answers!!.toString() )
        return this.results!!
    }

    /**
     * Return the result computed by computeResults()
     */
    fun getResults(): Results {
        if (results!!.right == 0 && results!!.wrong == 0)
            computeResults()
        return results!!
    }

    fun getWord(): String {
        return this.word!!
    }

    fun getCategory(): Category {
        if (category == null) {
            this.pickRandomCategory()
        }
        return this.category!!
    }

    fun getAlternatives(): ArrayList<String> {
        if (alternatives == null) {
            this.pickRandomCategory()
        }
        return this.alternatives!!
    }

    fun getAnswers(): Answers {
        return this.answers!!
    }

}