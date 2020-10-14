package com.walmin.android.quizmenow

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mackhartley.roundedprogressbar.RoundedProgressBar
import java.util.*

class GetReadyFragment : Fragment() {

    companion object GetReadyFragment {
        var currentQuizValue = "0"
        var currentQuiz = "Animals"

    }

    // Stuff
    lateinit var timer: CountDownTimer
    var timerFinished = false

    // Views
    lateinit var getReadyTimerView: RoundedProgressBar

    fun shouldInterceptBackPress() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!shouldInterceptBackPress()){
                    isEnabled = false
                    activity?.onBackPressed()

                }

            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_get_ready, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Stuff
        timer = object: CountDownTimer(1500, 15) {
            override fun onTick(millisUntilFinished: Long) {
                getReadyTimerView.setProgressPercentage(getReadyTimerView.getProgressPercentage() + 1)

            }

            override fun onFinish() {
                timerFinished = true

                getReadyTimerView.setProgressPercentage(100.0)
                QuizFragment.currentQuestion++

                findNavController().navigate(R.id.getReadyToQuiz)

            }

        }

        // Views
        if(currentQuizValue.toInt() >= 9) {
            Glide.with(view.context)
                .load(view.context.getString(R.string.thumbnailURL,
                    currentQuiz.toLowerCase(Locale.ROOT).replace(" ", "_")))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(view.findViewById(R.id.quizIconView))

        }else{
            Glide.with(view.context)
                .load(Tools.getImage(view.context,
                    currentQuiz.toLowerCase(Locale.ROOT).replace(" ", "_")))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(view.findViewById(R.id.quizIconView))

        }

        getReadyTimerView = view.findViewById(R.id.getReadyTimerView)
            getReadyTimerView.setProgressPercentage(0.0)

        timer.start()

    }

    override fun onResume() {
        super.onResume()

        if(timerFinished){
            findNavController().navigate(R.id.getReadyToQuiz)

        }

    }

}