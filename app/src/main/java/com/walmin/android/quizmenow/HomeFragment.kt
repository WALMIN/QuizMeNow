package com.walmin.android.quizmenow

import android.os.Bundle
import android.util.Log
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
        quizList.clear()

        quizList.add(HomeItemData("Animals", "0", "607D8B", "0", false))
        quizList.add(HomeItemData("General knowledge", "1", "F44336", "0", false))
        quizList.add(HomeItemData("Geography", "2", "FFC107", "0", false))
        quizList.add(HomeItemData("History", "3", "D81B60", "0", false))
        quizList.add(HomeItemData("Sports", "4", "FFEB3B", "0", false))
        quizList.add(HomeItemData(getString(R.string.goOnline), "-1", "4CAF50", "0", false))

        quizListAdapter.notifyDataSetChanged()

    }

    fun fetchList(){
        quizListViewRefreshLayout.isRefreshing = true

        if(!Tools.isNetworkConnected(requireContext())){
            quizListViewRefreshLayout.isRefreshing = false

            online = false
            fetchOfflineList()

        }else {
            online = true
            requestQueue = Volley.newRequestQueue(context)

            val request = JsonObjectRequest(Request.Method.GET, resources.getString(R.string.homeListURL), null, {
                response ->
                    try {
                        quizList.clear()

                        val listArray = response.getJSONArray("quiz")
                        val lockArray = MainActivity.unlocked.substring(0, MainActivity.unlocked.length - 1).split("|").toTypedArray()

                        for (i in 0 until listArray.length()) {
                            var locked = false

                            if(lockArray[i].endsWith("l")){
                                locked = true

                            }

                            val item = listArray.getJSONObject(i)
                            quizList.add(
                                HomeItemData(
                                    item.getString("title"),
                                    item.getString("value"),
                                    item.getString("color"),
                                    item.getString("price"),
                                    locked
                                )
                            )

                        }

                        quizListAdapter.notifyDataSetChanged();

                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }
                    quizListViewRefreshLayout.isRefreshing = false

                },
                {
                    it.printStackTrace()

                    quizListViewRefreshLayout.isRefreshing = false

                    online = false
                    fetchOfflineList()
                })
            requestQueue?.add(request)

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

                Log.d("HomeFragment", QuizFragment.questionList[i].toString())

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

                    Log.d("HomeFragment", QuizFragment.questionList[i].toString())

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