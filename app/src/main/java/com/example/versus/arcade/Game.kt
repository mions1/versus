package com.example.versus.arcade
import android.util.Log
import kotlin.random.Random

data class Category(var topic: String, var left: String, var right: String)
data class Alternatives(var words: HashMap<String,Int>)
data class Answers(var left: ArrayList<String>, var right: ArrayList<String>)
data class Results(var right: Int, var wrong: Int)

class Game {

    private var category: Category? = null
    private var alternatives: ArrayList<String>? = null
    private var answers: Answers? = null
    private var results: Results? = null
    private var index: Int? = null
    private var word: String? = null

    public var gameover: Boolean = false

    companion object {
        private val CATEGORIES = arrayListOf<Category>(
            Category("Animali", "Fattoria", "Casa"),
            Category("Film&Series", "Film", "Serie TV")
        )
        private val ALTERNATIVES = arrayListOf<Alternatives>(
            Alternatives( hashMapOf(
                            "Oca" to 0, "Asino" to 0, "Pavone" to 0, "Papera" to 0, "Mucca" to 0,
                            "Pecora" to 0, "Cavallo" to 0,  "Maiale" to 0, "Gallina" to 0, "Quaglia" to 0,
                            "cane" to 1, "Gatto" to 1, "Criceto" to 1, "Pesciolino rosso" to 1, "Coniglietto" to 1,
                            "Porcellino D'India" to 1, "Sauro" to 1, "Canarino" to 1, "Furetto" to 1, "Cincillà" to 1
                        )
            ),
            Alternatives( hashMapOf(
                            "Il Signore degli Anelli: La Compagnia dell'Anello" to 0, "Batman: Begins" to 0, "Matrix Reloaded" to 0, "Una Settimana da Dio" to 0, "Tenet" to 0,
                            "Harry Potter e la Pietra Filosofale" to 0, "The Prestige" to 0, "The Ring" to 0, "Titanic" to 0, "Colazione da Tiffany" to 0,
                            "Stranger Things" to 1, "Umbrella Academy" to 1, "How I Met Your Mother" to 1, "Supernatural" to 1, "Game of Thrones" to 1,
                            "The Boys" to 1, "Breaking Bad" to 1, "Friends" to 1, "You" to 1, "Scrubs" to 1
                        )
            )
        )
    }

    fun pickRandomCategory() {
        var index: Int = Random.nextInt(Game.CATEGORIES.size)
        this.category = Game.CATEGORIES.get(index)
        this.alternatives = ArrayList(Game.ALTERNATIVES.get(index).words.keys)
        this.answers = Answers(ArrayList<String>(), ArrayList<String>())
        this.results = Results(0,0)
        this.index = index
        getRandomWord()
    }

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

    private fun computeResults(): Results {
        for ( (word,ans) in Game.ALTERNATIVES[this.index!!].words ) {
            Log.d("Results: ", word+": "+ans)
            if (word in this.answers!!.left && ans == 0)
                this.results!!.right ++
            else if (word in this.answers!!.right && ans == 1)
                this.results!!.right ++
            else if (word in this.answers!!.left && ans == 1)
                this.results!!.wrong ++
            else if (word in this.answers!!.right && ans == 0)
                this.results!!.wrong ++
        }
        Log.d("MyAnsw: ", this.answers!!.toString() )
        return this.results!!
    }

    fun getResults(): Results {
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