package com.walmin.android.quizmenow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), OnHomeItemClickListener {

    var online = false
    var requestQueue: RequestQueue? = null

    // List
    lateinit var homeListView: RecyclerView
    lateinit var homeListAdapter: HomeListAdapter
    var homeList = ArrayList<HomeItemData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List
        homeListView = view.findViewById(R.id.quizListView)
        homeListView.setHasFixedSize(true);
        homeListView.layoutManager = GridLayoutManager(context, 2)

        homeListAdapter = HomeListAdapter(homeList, this)
        homeListView.adapter = homeListAdapter

        // Fetch list
        if(Tools.isNetworkConnected(requireContext())){
            online = true
            requestQueue = Volley.newRequestQueue(context)
            fetchList()

        }else{
            online = false
            fetchOfflineList()

        }

    }

    fun fetchOfflineList(){
        homeList.clear()

        homeList.add(HomeItemData("Animals", "0", "607D8B"))
        homeList.add(HomeItemData("General knowledge", "1", "F44336"))
        homeList.add(HomeItemData("Sports", "2", "FFEB3B"))
        homeList.add(HomeItemData(getString(R.string.goOnline), "-1", "4CAF50"))

        homeListAdapter.notifyDataSetChanged();

    }

    fun fetchList(){
        val request = JsonObjectRequest(Request.Method.GET, resources.getString(R.string.homeListURL), null,
        { response ->
            try {
                homeList.clear()

                val listArray = response.getJSONArray("quiz")

                for (i in 0 until listArray.length()) {
                    val item = listArray.getJSONObject(i)
                    homeList.add(HomeItemData(
                        item.getString("title"),
                        item.getString("value"),
                        item.getString("color")))

                }

                homeList.sortBy { it.title }
                homeListAdapter.notifyDataSetChanged();

            } catch (e: JSONException) {
                e.printStackTrace()

            }

        },
        {
            online = false
            fetchOfflineList()

            it.printStackTrace()
        })
        requestQueue?.add(request)

    }

    override fun onHomeItemClick(item: HomeItemData) {
        if(item.value == "-1"){
            if(Tools.isNetworkConnected(requireContext())){
                online = true
                requestQueue = Volley.newRequestQueue(context)
                fetchList()

            }else{
                online = false
                fetchOfflineList()

            }

        }else{
            if(online){
                Toast.makeText(context, getString(R.string.quizURL, item.value), Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(context, item.value, Toast.LENGTH_LONG).show()

            }

        }

    }

}