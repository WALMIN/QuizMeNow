package com.walmin.android.quizmenow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HomeFragment : Fragment(), OnHomeItemClickListener {

    companion object HomeFragment {
        var coins = 100

    }

    var online = false
    var requestQueue: RequestQueue? = null

    // List
    lateinit var quizListViewRefreshLayout: SwipeRefreshLayout
    lateinit var quizListView: RecyclerView
    lateinit var quizListAdapter: HomeListAdapter
    var quizList = ArrayList<HomeItemData>()

    // Views
    lateinit var coinsView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestQueue = Volley.newRequestQueue(context)

        // List
        quizListViewRefreshLayout = view.findViewById(R.id.quizListViewRefreshLayout)
        quizListViewRefreshLayout.setOnRefreshListener {
            quizListViewRefreshLayout.isRefreshing = true
            fetchList()

        }

        quizListView = view.findViewById(R.id.quizListView)
        quizListView.setHasFixedSize(true);
        quizListView.layoutManager = GridLayoutManager(context, 3)

        quizListAdapter = HomeListAdapter(quizList, this)
        quizListView.adapter = quizListAdapter

        // Views
        coinsView = view.findViewById(R.id.coinsView)
        coinsView.text = coins.toString()

        fetchList()

    }

    fun fetchOfflineList(){
        quizListViewRefreshLayout.isRefreshing = false
        online = false

        quizList.add(HomeItemData("Animals", "0", "F44336", "0", false))
        quizList.add(HomeItemData("Art", "1", "9C27B0", "0", false))
        quizList.add(HomeItemData("Books", "2", "3F51B5", "0", false))
        quizList.add(HomeItemData("General knowledge", "3", "03A9F4", "0", false))
        quizList.add(HomeItemData("Geography", "4", "009688", "0", false))
        quizList.add(HomeItemData(getString(R.string.goOnline), "-1", "8BC34A", "0", false))

        quizListAdapter.notifyDataSetChanged()

    }

    fun fetchList(){
        quizListViewRefreshLayout.isRefreshing = true
        quizList.clear()

        if(!Tools.isNetworkConnected(requireContext())){
            fetchOfflineList()

        }else {
            quizListViewRefreshLayout.isRefreshing = false
            online = true

            val lockList = MainActivity.unlocked.split("|").toTypedArray()
            val lockedArray = mutableListOf<Boolean>()

            for (i in lockList) {
                if (i.endsWith("l")) {
                    lockedArray.add(true)

                }else{
                    lockedArray.add(false)

                }
            }

            quizList.add(HomeItemData("Animals", "27", "F44336", "0", lockedArray[0]))
            quizList.add(HomeItemData("Art", "25", "9C27B0", "0", lockedArray[1]))
            quizList.add(HomeItemData("Books", "10", "3F51B5", "0", lockedArray[2]))
            quizList.add(HomeItemData("General knowledge", "9", "03A9F4", "0", lockedArray[3]))
            quizList.add(HomeItemData("Geography", "22", "009688", "0", lockedArray[4]))
            quizList.add(HomeItemData("Film", "11", "8BC34A", "0", lockedArray[5]))
            quizList.add(HomeItemData("History", "23", "FFEB3B", "0", lockedArray[6]))
            quizList.add(HomeItemData("Music", "12", "FF9800", "0", lockedArray[7]))
            quizList.add(HomeItemData("Sports", "21", "795548", "0", lockedArray[8]))

            quizList.add(HomeItemData("Board Games", "16", "E32F2F", "10", lockedArray[9]))
            quizList.add(HomeItemData("Cartoon & Animations", "32", "7B1FA2", "10", lockedArray[10]))
            quizList.add(HomeItemData("Celebrities", "26", "303F9F", "10", lockedArray[11]))

            quizList.add(HomeItemData("Comics", "29", "0288D1", "15", lockedArray[12]))
            quizList.add(HomeItemData("Computers", "18", "00796B", "15", lockedArray[13]))
            quizList.add(HomeItemData("Gadgets", "30", "689F38", "15", lockedArray[14]))

            quizList.add(HomeItemData("Japanese Anime & Manga", "31", "FBC02D", "20", lockedArray[15]))
            quizList.add(HomeItemData("Mathematics", "19", "F57C00", "20", lockedArray[16]))
            quizList.add(HomeItemData("Musicals & Theatres", "13", "5D4037", "20", lockedArray[17]))

            quizList.add(HomeItemData("Mythology", "20", "B71C1C", "25", lockedArray[18]))
            quizList.add(HomeItemData("Politics", "24", "4A148C", "25", lockedArray[19]))
            quizList.add(HomeItemData("Science & Nature", "17", "1A237E", "25", lockedArray[20]))

            quizList.add(HomeItemData("Television", "14", "01579B", "30", lockedArray[21]))
            quizList.add(HomeItemData("Vehicles", "28", "004D40", "30", lockedArray[22]))
            quizList.add(HomeItemData("Video Games", "15", "33691E", "30", lockedArray[23]))

            quizListAdapter.notifyDataSetChanged()

        }

    }

    override fun onHomeItemClick(item: HomeItemData, position: Int) {
        if(!item.locked){
            if(item.value == "-1"){
                fetchList()

            }else{
                if(online){
                    fetchQuestions(item.title, item.value, getString(R.string.quizURL, item.value))

                }else{
                    fetchOfflineQuestions(item.title, item.value)

                }

            }

        }else{
            AlertDialog.Builder(requireContext())
                .setTitle(item.title)
                .setMessage(getString(R.string.purchaseMsg, item.price))
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    if(coins >= item.price.toInt()){
                        coins -= item.price.toInt()
                        coinsView.text = coins.toString()

                        MainActivity.unlocked = MainActivity.unlocked.replace(position.toString() + "l|", position.toString() + "u|")

                        fetchList()

                    }else{
                        Tools.showSnackbar(layout, getString(R.string.purchaseNotEnough))

                    }

                }
                .setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.cancel()

                }
                .show()

        }

    }

    fun fetchOfflineQuestions(title: String, value: String){
        try {
            QuizFragment.currentQuestion = -1
            QuizFragment.score = 0
            QuizFragment.correctList = mutableListOf("null", "null", "null", "null", "null", "null", "null", "null", "null", "null")
            QuizFragment.questionList.clear()

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
            findNavController().navigate(R.id.homeToGetReady)

        } catch (e: JSONException) {
            e.printStackTrace()

        }

    }

    fun fetchQuestions(title: String, value: String, url: String){
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                QuizFragment.currentQuestion = -1
                QuizFragment.score = 0
                QuizFragment.correctList = mutableListOf("null", "null", "null", "null", "null", "null", "null", "null", "null", "null")
                QuizFragment.questionList.clear()

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
                findNavController().navigate(R.id.homeToGetReady)

            } catch (e: JSONException) {
                e.printStackTrace()

            }

        },
        {
            it.printStackTrace()

            online = false
            fetchOfflineList()

        })
        requestQueue?.add(request)

    }

}