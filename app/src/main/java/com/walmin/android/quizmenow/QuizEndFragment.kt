package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_quiz_end.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class QuizEndFragment : Fragment() {

    // Stuff
    var requestQueue: RequestQueue? = null

    var coins = 0

    // Views
    lateinit var accuracyCircleView: ProgressBar
    lateinit var accuracyView: TextView

    lateinit var correctView: TextView
    lateinit var incorrectView: TextView
    lateinit var scoreView: TextView

    lateinit var replayBtn: CardView
    lateinit var returnHomeBtn: CardView

    // Go to home on back press
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.quizEndToHome)

            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        giveResults()

        // Stop music if playing
        if(HomeFragment.backgroundMusic.isPlaying){
            HomeFragment.backgroundMusic.pause()

        }

        // Stuff
        requestQueue = Volley.newRequestQueue(context)

        // Views
        accuracyCircleView = view.findViewById(R.id.accuracyCircleView)
            accuracyCircleView.progress = QuizFragment.score

        accuracyView = view.findViewById(R.id.accuracyView)
            accuracyView.text = (QuizFragment.score * 10).toString() + "%"

        correctView = view.findViewById(R.id.correctView)
            correctView.text = QuizFragment.correct.toString()

        incorrectView = view.findViewById(R.id.incorrectView)
            incorrectView.text = QuizFragment.incorrect.toString()

        scoreView = view.findViewById(R.id.scoreView)
            scoreView.text = coins.toString()

        replayBtn = view.findViewById(R.id.replayBtn)
            replayBtn.setOnClickListener {
                replay()

            }

        returnHomeBtn = view.findViewById(R.id.returnHomeBtn)
            returnHomeBtn.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.QuizEndFragment) {
                    findNavController().navigate(R.id.quizEndToHome)

                }

            }

    }

    // Give points & stats to player
    fun giveResults(){
        if(QuizFragment.score == 10) {
            coins = (QuizFragment.score * 2)

        }else if(QuizFragment.score in 6..9){
            coins = (QuizFragment.score * 1.5).toInt()

        }else{
            coins = QuizFragment.score

        }
        MainActivity.correct += QuizFragment.correct
        MainActivity.incorrect += QuizFragment.incorrect

        MainActivity.coins += coins
        MainActivity.games++

        GlobalScope.launch {
            MainActivity.dataManager.saveCoins(MainActivity.coins)
            MainActivity.dataManager.saveGame(MainActivity.games, MainActivity.correct.toInt(), MainActivity.incorrect.toInt())
        }

    }

    // Replay quiz
    fun replay(){
        QuizFragment.questionList.clear()
        QuizFragment.currentQuestion = -1

        QuizFragment.correct = 0
        QuizFragment.incorrect = 0
        QuizFragment.score = 0

        QuizFragment.correctList = mutableListOf("null", "null", "null", "null", "null", "null", "null", "null", "null", "null")

        if(Tools.isNetworkConnected(requireContext())){
            fetchQuestions(GetReadyFragment.currentQuiz, GetReadyFragment.currentQuizValue, getString(R.string.quizURL, GetReadyFragment.currentQuizValue))

        }else {
            if(GetReadyFragment.currentQuizValue.toInt() in 0..4) {
                fetchOfflineQuestions(GetReadyFragment.currentQuiz, GetReadyFragment.currentQuizValue)

            }else{
                Tools.showSnackbar(layout, getString(R.string.noInternet))

            }

        }

    }

    // Get offline questions
    fun fetchOfflineQuestions(title: String, value: String){
        try {
            val response = JSONObject(requireContext().assets.open("${title.toLowerCase(Locale.ROOT).replace(" ", "_")}.json").bufferedReader().use { it.readText() })
            val questionArray = response.getJSONArray("results")

            for (i in 0 until questionArray.length()) {
                val item = questionArray.getJSONObject(i)

                val answerList = ArrayList<String>()
                answerList.add(item.getString("correct_answer"))
                answerList.add(item.getJSONArray("incorrect_answers")[0].toString())
                answerList.add(item.getJSONArray("incorrect_answers")[1].toString())
                answerList.add(item.getJSONArray("incorrect_answers")[2].toString())

                answerList.shuffle()

                QuizFragment.questionList.add(
                    QuestionItemData(
                        item.getString("question"),
                        item.getString("correct_answer"),
                        answerList
                    )
                )

                GetReadyFragment.currentQuiz = title
                GetReadyFragment.currentQuizValue = value

            }

            QuizFragment.questionList.shuffle()

            if (findNavController().currentDestination?.id == R.id.QuizEndFragment) {
                findNavController().navigate(R.id.quizEndToGetReady)

                if(MainActivity.music){
                    HomeFragment.backgroundMusic.seekTo(0)
                    HomeFragment.backgroundMusic.start()
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()

        }

    }

    // Get questions from url
    fun fetchQuestions(title: String, value: String, url: String){
        val request = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
            try {
                val questionArray = response.getJSONArray("results")

                for (i in 0 until questionArray.length()) {
                    val item = questionArray.getJSONObject(i)

                    val answerList = ArrayList<String>()
                    answerList.add(item.getString("correct_answer"))
                    answerList.add(item.getJSONArray("incorrect_answers")[0].toString())
                    answerList.add(item.getJSONArray("incorrect_answers")[1].toString())
                    answerList.add(item.getJSONArray("incorrect_answers")[2].toString())

                    answerList.shuffle()

                    QuizFragment.questionList.add(
                        QuestionItemData(
                            item.getString("question"),
                            item.getString("correct_answer"),
                            answerList
                        )
                    )

                    GetReadyFragment.currentQuiz = title
                    GetReadyFragment.currentQuizValue = value

                }

                if (findNavController().currentDestination?.id == R.id.QuizEndFragment) {
                    findNavController().navigate(R.id.quizEndToGetReady)

                    if(MainActivity.music){
                        HomeFragment.backgroundMusic.seekTo(0)
                        HomeFragment.backgroundMusic.start()
                    }

                }

            } catch (e: JSONException) {
                e.printStackTrace()

            }

        },
            {
                it.printStackTrace()
                Tools.showSnackbar(layout, getString(R.string.noInternet))

            })
        requestQueue?.add(request)

    }

}