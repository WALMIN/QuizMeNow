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
    lateinit var homeListViewRefreshLayout: SwipeRefreshLayout
    lateinit var homeListView: RecyclerView
    lateinit var homeListAdapter: HomeListAdapter
    var homeList = ArrayList<HomeItemData>()

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
        homeListViewRefreshLayout = view.findViewById(R.id.quizListViewRefreshLayout)
        homeListViewRefreshLayout.setOnRefreshListener {
            homeListViewRefreshLayout.isRefreshing = true
            fetchList()

        }

        homeListView = view.findViewById(R.id.quizListView)
        homeListView.setHasFixedSize(true);
        homeListView.layoutManager = GridLayoutManager(context, 3)

        homeListAdapter = HomeListAdapter(homeList, this)
        homeListView.adapter = homeListAdapter

        fetchList()

    }

    fun fetchOfflineList(){
        homeList.clear()

        homeList.add(HomeItemData("Animals", "0", "607D8B"))
        homeList.add(HomeItemData("General knowledge", "1", "F44336"))
        homeList.add(HomeItemData("Sports", "2", "FFEB3B"))
        homeList.add(HomeItemData(getString(R.string.goOnline), "-1", "4CAF50"))

        homeListAdapter.notifyDataSetChanged()

    }

    fun fetchList(){
        homeListViewRefreshLayout.isRefreshing = true

        if(!Tools.isNetworkConnected(requireContext())){
            homeListViewRefreshLayout.isRefreshing = false

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
                        homeList.clear()

                        val listArray = response.getJSONArray("quiz")

                        for (i in 0 until listArray.length()) {
                            val item = listArray.getJSONObject(i)
                            homeList.add(
                                HomeItemData(
                                    item.getString("title"),
                                    item.getString("value"),
                                    item.getString("color")
                                )
                            )

                        }

                        homeList.sortBy { it.title }
                        homeListAdapter.notifyDataSetChanged();

                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }
                    homeListViewRefreshLayout.isRefreshing = false

                },
                {
                    it.printStackTrace()

                    homeListViewRefreshLayout.isRefreshing = false

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
                fetchQuestions(getString(R.string.quizURL, item.value))

            }else{
                fetchOfflineQuestions()

            }

        }

    }

    fun fetchOfflineQuestions(){


    }

    fun fetchQuestions(url: String){
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    GameFragment.questionList.clear()

                    val questionArray = response.getJSONArray("results")

                    for (i in 0 until questionArray.length()) {
                        val item = questionArray.getJSONObject(i)

                        val answerList = ArrayList<String>()
                        answerList.add(item.getString("correct_answer"))
                        answerList.add(item.getJSONArray("incorrect_answers")[0].toString())
                        answerList.add(item.getJSONArray("incorrect_answers")[1].toString())
                        answerList.add(item.getJSONArray("incorrect_answers")[2].toString())

                        answerList.shuffle()

                        GameFragment.questionList.add(
                            QuestionItemData(
                                item.getString("question"),
                                item.getString("correct_answer"),
                                answerList
                            )
                        )

                        Log.d("HomeFragment", GameFragment.questionList[i].toString())

                    }

                    findNavController().navigate(R.id.homeToGame)

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