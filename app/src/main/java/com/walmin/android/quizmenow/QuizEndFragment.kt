package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController

class QuizEndFragment : Fragment() {

    // Stuff
    var coins = 0

    // Views
    lateinit var accuracyCircleView: ProgressBar
    lateinit var accuracyView: TextView

    lateinit var correctView: TextView
    lateinit var incorrectView: TextView
    lateinit var scoreView: TextView

    lateinit var returnHomeBtn: CardView

    fun shouldInterceptBackPress() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(shouldInterceptBackPress()) {
                    findNavController().navigate(R.id.quizEndToHome)

                }else{
                    isEnabled = false
                    activity?.onBackPressed()

                }

            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz_end, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        giveResults()

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
            if(coins > 0){
                scoreView.text = resources.getString(R.string.score, coins.toString())

            }else{
                scoreView.text = coins.toString()

            }


        returnHomeBtn = view.findViewById(R.id.returnHomeBtn)
            returnHomeBtn.setOnClickListener {
                findNavController().navigate(R.id.quizEndToHome)

            }

    }

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

    }

}