package com.walmin.android.quizmenow

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController

class GameFragment : Fragment() {

    companion object GameFragment {
        var questionList = ArrayList<QuestionItemData>()

    }

    // Stuff
    var currentQuestion = 0

    var nextQuestion = false
    var finished = false

    lateinit var timer: CountDownTimer

    // Views
    lateinit var layout: ConstraintLayout

    lateinit var currentQuestionView: TextView

    lateinit var questionView: TextView

    lateinit var answer0Card: CardView
    lateinit var answer0View: TextView

    lateinit var answer1Card: CardView
    lateinit var answer1View: TextView

    lateinit var answer2Card: CardView
    lateinit var answer2View: TextView

    lateinit var answer3Card: CardView
    lateinit var answer3View: TextView

    lateinit var tapToContinueView: TextView

    lateinit var gameTimerView: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stuff
        timer = object: CountDownTimer(10000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                gameTimerView.progress = (millisUntilFinished / 1000).toInt()

            }

            override fun onFinish() {
                answer0Card.isEnabled = false
                answer1Card.isEnabled = false
                answer2Card.isEnabled = false
                answer3Card.isEnabled = false

                if(answer0View.text.toString() == questionList[currentQuestion].correctAnswer){
                    answer0Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(answer1View.text.toString() == questionList[currentQuestion].correctAnswer){
                    answer1Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(answer2View.text.toString() == questionList[currentQuestion].correctAnswer){
                    answer2Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(answer3View.text.toString() == questionList[currentQuestion].correctAnswer){
                    answer3Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

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
        timer.start()

        // Views
        layout = view.findViewById(R.id.layout)
        layout.setOnClickListener{
            if(finished){
                findNavController().navigate(R.id.gameToHome)

            }else if(nextQuestion){
                nextQuestion = false
                tapToContinueView.visibility = View.GONE

                currentQuestion++
                setQuestion(currentQuestion)

            }

        }

        currentQuestionView = view.findViewById(R.id.currentQuestionView)

        questionView = view.findViewById(R.id.questionView)

        answer0Card = view.findViewById(R.id.answer0Card)
        answer0View = view.findViewById(R.id.answer0View)
            answer0Card.setOnClickListener{
                checkAnswer(it as CardView, currentQuestion, questionList[currentQuestion].answers[0])

            }

        answer1Card = view.findViewById(R.id.answer1Card)
        answer1View = view.findViewById(R.id.answer1View)
            answer1Card.setOnClickListener{
                checkAnswer(it as CardView, currentQuestion, questionList[currentQuestion].answers[1])
            }

        answer2Card = view.findViewById(R.id.answer2Card)
        answer2View = view.findViewById(R.id.answer2View)
            answer2Card.setOnClickListener{
                checkAnswer(it as CardView, currentQuestion, questionList[currentQuestion].answers[2])
            }

        answer3Card = view.findViewById(R.id.answer3Card)
        answer3View = view.findViewById(R.id.answer3View)
            answer3Card.setOnClickListener{
                checkAnswer(it as CardView, currentQuestion,questionList[currentQuestion].answers[3])
            }

        tapToContinueView = view.findViewById(R.id.tapToContinueView)
            tapToContinueView.visibility = View.GONE

        gameTimerView = view.findViewById(R.id.gameTimerView)
            gameTimerView.progress = 10

        setQuestion(currentQuestion)

    }

    fun setQuestion(question: Int){
        gameTimerView.progress = 10
        timer.start()

        currentQuestionView.text = getString(R.string.currentQuestion, (question + 1).toString())

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

    fun checkAnswer(view: CardView, question: Int, input: String){
        if(question <= (questionList.size - 1)){
            gameTimerView.progress = 10
            timer.cancel()

            answer0Card.isEnabled = false
            answer1Card.isEnabled = false
            answer2Card.isEnabled = false
            answer3Card.isEnabled = false

            if(input == questionList[question].correctAnswer){
                view.setCardBackgroundColor(requireContext().getColor(R.color.correct))

            }else{
                view.setCardBackgroundColor(requireContext().getColor(R.color.incorrect))

                if(questionList[question].answers[0] == questionList[question].correctAnswer){
                    answer0Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(questionList[question].answers[1] == questionList[question].correctAnswer){
                    answer1Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(questionList[question].answers[2] == questionList[question].correctAnswer){
                    answer2Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }else if(questionList[question].answers[3] == questionList[question].correctAnswer){
                    answer3Card.setCardBackgroundColor(requireContext().getColor(R.color.correct))

                }

            }

            if(question == (questionList.size - 1)){
                finished = true
                tapToContinueView.visibility = View.VISIBLE

            }else{
                nextQuestion = true
                tapToContinueView.visibility = View.VISIBLE

            }

        }

    }

}