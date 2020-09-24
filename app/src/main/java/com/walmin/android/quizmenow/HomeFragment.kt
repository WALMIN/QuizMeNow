package com.walmin.android.quizmenow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class HomeFragment : Fragment(), OnHomeItemClickListener {

    var online = false
    var requestQueue: RequestQueue? = null

    // List
    lateinit var quizListViewRefreshLayout: SwipeRefreshLayout
    lateinit var quizListView: RecyclerView
    lateinit var quizListAdapter: HomeListAdapter
    var quizList = ArrayList<HomeItemData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        fetchList()

    }

    fun fetchOfflineList(){
        quizList.clear()

        quizList.add(HomeItemData("Animals", "0", "607D8B"))
        quizList.add(HomeItemData("General knowledge", "1", "F44336"))
        quizList.add(HomeItemData("Sports", "2", "FFEB3B"))
        quizList.add(HomeItemData(getString(R.string.goOnline), "-1", "4CAF50"))

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

            val request = JsonObjectRequest(Request.Method.GET,
                resources.getString(R.string.homeListURL),
                null,
                { response ->
                    try {
                        quizList.clear()

                        val listArray = response.getJSONArray("quiz")

                        for (i in 0 until listArray.length()) {
                            val item = listArray.getJSONObject(i)
                            quizList.add(
                                HomeItemData(
                                    item.getString("title"),
                                    item.getString("value"),
                                    item.getString("color")
                                )
                            )

                        }

                        quizList.sortBy { it.title }
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

    override fun onHomeItemClick(item: HomeItemData) {
        if(item.value == "-1"){
            fetchList()

        }else{
            if(online){
                fetchQuestions(item.title, getString(R.string.quizURL, item.value))

            }else{
                fetchOfflineQuestions()

            }

        }

    }

    fun fetchOfflineQuestions(){


    }

    fun fetchQuestions(title: String, url: String){
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                QuizFragment.currentQuestion = -1
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