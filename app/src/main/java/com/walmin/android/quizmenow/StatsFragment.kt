package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController

class StatsFragment : Fragment() {

    // Views
    lateinit var accuracyCircleView: ProgressBar
    lateinit var accuracyView: TextView

    lateinit var correctView: TextView
    lateinit var incorrectView: TextView
    lateinit var gamesView: TextView

    lateinit var closeBtn: CardView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views
        accuracyCircleView = view.findViewById(R.id.accuracyCircleView)
            accuracyCircleView.progress = (MainActivity.correct / (MainActivity.incorrect + MainActivity.correct) * 100).toInt()

        accuracyView = view.findViewById(R.id.accuracyView)
            accuracyView.text = (MainActivity.correct / (MainActivity.incorrect + MainActivity.correct) * 100).toInt().toString() + "%"

        correctView = view.findViewById(R.id.correctView)
            correctView.text = MainActivity.correct.toInt().toString()

        incorrectView = view.findViewById(R.id.incorrectView)
            incorrectView.text = MainActivity.incorrect.toInt().toString()

        gamesView = view.findViewById(R.id.gamesView)
            gamesView.text = MainActivity.games.toString()

        closeBtn = view.findViewById(R.id.closeBtn)
            closeBtn.setOnClickListener {
                findNavController().navigate(R.id.statsToHome)

            }

    }

}