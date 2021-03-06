package com.walmin.android.quizmenow

import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.mackhartley.roundedprogressbar.RoundedProgressBar
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {

    companion object GameFragment {
        var questionList = ArrayList<QuestionItemData>()
        var currentQuestion = -1

        var correct = 0
        var incorrect = 0
        var score = 0

        var correctList = mutableListOf("null", "null", "null", "null", "null", "null", "null", "null", "null", "null")

    }

    // Stuff
    var nextQuestion = false
    var finished = false

    lateinit var timer: CountDownTimer

    // Sounds
    lateinit var correctSound: MediaPlayer
    lateinit var incorrectSound: MediaPlayer

    // Views
    lateinit var layout: ConstraintLayout

    lateinit var questionView: AppCompatTextView

    lateinit var answer0Card: CardView
    lateinit var answer0View: AppCompatTextView

    lateinit var answer1Card: CardView
    lateinit var answer1View: AppCompatTextView

    lateinit var answer2Card: CardView
    lateinit var answer2View: AppCompatTextView

    lateinit var answer3Card: CardView
    lateinit var answer3View: AppCompatTextView

    lateinit var tapToContinueView: TextView

    lateinit var gameTimerView: RoundedProgressBar

    // Block back press
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stuff
        timer = object: CountDownTimer(20000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                gameTimerView.setProgressPercentage(gameTimerView.getProgressPercentage() - 0.5)

            }

            override fun onFinish() {
                if(MainActivity.sound){
                    incorrectSound.start()
                }

                gameTimerView.setProgressPercentage(0.0)

                answer0Card.isEnabled = false
                answer1Card.isEnabled = false
                answer2Card.isEnabled = false
                answer3Card.isEnabled = false

                incorrect++

                correctList[currentQuestion] = "false"

                when (currentQuestion) {
                    0 -> { questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    1 -> { questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    2 -> { questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    3 -> { questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    4 -> { questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    5 -> { questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    6 -> { questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    7 -> { questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    8 -> { questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    9 -> { questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                }

                when (questionList[currentQuestion].correctAnswer) {
                    questionList[currentQuestion].answers[0] -> {
                        answer0Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer0View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[currentQuestion].answers[1] -> {
                        answer1Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer1View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[currentQuestion].answers[2] -> {
                        answer2Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer2View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[currentQuestion].answers[3] -> {
                        answer3Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer3View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }
                }

                if(currentQuestion == (questionList.size - 1)){
                    finished = true
                    tapToContinueView.visibility = View.VISIBLE

                }else{
                    nextQuestion = true
                    tapToContinueView.visibility = View.VISIBLE

                }

            }

        }

        // Sounds
        correctSound = MediaPlayer.create(context, R.raw.correct)
        incorrectSound = MediaPlayer.create(context, R.raw.incorrect)

        // Views
        layout = view.findViewById(R.id.layout)
        layout.setOnClickListener{
            if(finished){
                findNavController().navigate(R.id.quizToQuizEnd)

            }else if(nextQuestion){
                nextQuestion = false
                tapToContinueView.visibility = View.GONE

                findNavController().navigate(R.id.quizToGetReady)

            }

        }

        questionView = view.findViewById(R.id.questionView)

        answer0Card = view.findViewById(R.id.answer0Card)
        answer0View = view.findViewById(R.id.answer0View)
            answer0Card.setOnClickListener{
                checkAnswer(it as CardView, answer0View, currentQuestion, questionList[currentQuestion].answers[0])

            }

        answer1Card = view.findViewById(R.id.answer1Card)
        answer1View = view.findViewById(R.id.answer1View)
            answer1Card.setOnClickListener{
                checkAnswer(it as CardView, answer1View, currentQuestion, questionList[currentQuestion].answers[1])
            }

        answer2Card = view.findViewById(R.id.answer2Card)
        answer2View = view.findViewById(R.id.answer2View)
            answer2Card.setOnClickListener{
                checkAnswer(it as CardView, answer2View, currentQuestion, questionList[currentQuestion].answers[2])
            }

        answer3Card = view.findViewById(R.id.answer3Card)
        answer3View = view.findViewById(R.id.answer3View)
            answer3Card.setOnClickListener{
                checkAnswer(it as CardView, answer3View, currentQuestion,questionList[currentQuestion].answers[3])
            }

        tapToContinueView = view.findViewById(R.id.tapToContinueView)
            tapToContinueView.visibility = View.GONE

        gameTimerView = view.findViewById(R.id.gameTimerView)
            gameTimerView.setProgressPercentage(100.0)

        setQuestion(currentQuestion)

    }

    // Set current question
    fun setQuestion(question: Int){
        gameTimerView.setProgressPercentage(100.0)
        timer.start()

        when (question) {
            0 -> { questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            1 -> { questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            2 -> { questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            3 -> { questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            4 -> { questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            5 -> { questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            6 -> { questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            7 -> { questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            8 -> { questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
            9 -> { questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY) }
        }

        for(i in 0 until correctList.size){
            if(correctList[0] == "true"){
                questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[0] == "false"){
                questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[1] == "true"){
                questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[1] == "false"){
                questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[2] == "true"){
                questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[2] == "false"){
                questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[3] == "true"){
                questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[3] == "false"){
                questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[4] == "true"){
                questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[4] == "false"){
                questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[5] == "true"){
                questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[5] == "false"){
                questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[6] == "true"){
                questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[6] == "false"){
                questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[7] == "true"){
                questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[7] == "false"){
                questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[8] == "true"){
                questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[8] == "false"){
                questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

            if(correctList[9] == "true"){
                questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY)

            }else if(correctList[9] == "false"){
                questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY)

            }

        }

        questionView.text = Html.fromHtml(questionList[question].question, Html.FROM_HTML_MODE_LEGACY)

        answer0View.text = Html.fromHtml(questionList[question].answers[0], Html.FROM_HTML_MODE_LEGACY)
        answer0Card.isEnabled = true
        answer0Card.setCardBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))

        answer1View.text = Html.fromHtml(questionList[question].answers[1], Html.FROM_HTML_MODE_LEGACY)
        answer1Card.isEnabled = true
        answer1Card.setCardBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))

        answer2View.text = Html.fromHtml(questionList[question].answers[2], Html.FROM_HTML_MODE_LEGACY)
        answer2Card.isEnabled = true
        answer2Card.setCardBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))

        answer3View.text = Html.fromHtml(questionList[question].answers[3], Html.FROM_HTML_MODE_LEGACY)
        answer3Card.isEnabled = true
        answer3Card.setCardBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))

    }

    // Check the answer
    fun checkAnswer(view: CardView, textView: TextView, question: Int, input: String){
        if(question <= (questionList.size - 1)){
            gameTimerView.setProgressPercentage(0.0)
            timer.cancel()

            answer0Card.isEnabled = false
            answer1Card.isEnabled = false
            answer2Card.isEnabled = false
            answer3Card.isEnabled = false

            if(input == questionList[question].correctAnswer){
                if(MainActivity.sound){
                    correctSound.start()
                }

                score++
                correct++

                view.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                textView.setBackgroundColor(requireContext().getColor(R.color.correct))

                correctList[question] = "true"

                when (question) {
                    0 -> { questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    1 -> { questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    2 -> { questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    3 -> { questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    4 -> { questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    5 -> { questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    6 -> { questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    7 -> { questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    8 -> { questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                    9 -> { questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.correct), PorterDuff.Mode.MULTIPLY) }
                }

            }else{
                if(MainActivity.sound) {
                    incorrectSound.start()
                }

                incorrect++

                view.setCardBackgroundColor(requireContext().getColor(R.color.incorrect))
                textView.setBackgroundColor(requireContext().getColor(R.color.incorrect))

                correctList[question] = "false"

                when (question) {
                    0 -> { questionDot0.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    1 -> { questionDot1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    2 -> { questionDot2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    3 -> { questionDot3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    4 -> { questionDot4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    5 -> { questionDot5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    6 -> { questionDot6.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    7 -> { questionDot7.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    8 -> { questionDot8.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                    9 -> { questionDot9.setColorFilter(ContextCompat.getColor(requireContext(), R.color.incorrect), PorterDuff.Mode.MULTIPLY) }
                }

                when (questionList[question].correctAnswer) {
                    questionList[question].answers[0] -> {
                        answer0Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer0View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[question].answers[1] -> {
                        answer1Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer1View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[question].answers[2] -> {
                        answer2Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer2View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }

                    questionList[question].answers[3] -> {
                        answer3Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))
                        answer3View.setBackgroundColor(requireContext().getColor(R.color.colorPrimaryDark))
                    }
                }

            }

            if(question == 9){
                finished = true
                tapToContinueView.visibility = View.VISIBLE

            }else{
                nextQuestion = true
                tapToContinueView.visibility = View.VISIBLE

            }

        }

    }

}