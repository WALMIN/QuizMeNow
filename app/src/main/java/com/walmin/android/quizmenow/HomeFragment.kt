package com.walmin.android.quizmenow

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment(), OnHomeItemClickListener {

    companion object HomeFragment {
        lateinit var backgroundMusic: MediaPlayer

    }

    // Stuff
    var online = false
    var requestQueue: RequestQueue? = null

    // Sounds
    lateinit var buySound: MediaPlayer

    // List
    lateinit var quizListLoadingSpinLayout: LinearLayout

    lateinit var quizListViewRefreshLayout: SwipeRefreshLayout
    lateinit var quizListView: RecyclerView
    lateinit var quizListAdapter: HomeListAdapter
    var quizList = ArrayList<HomeItemData>()

    // Views
    lateinit var coinsView: TextView

    lateinit var musicBtn: ImageButton
    lateinit var soundBtn: ImageButton

    lateinit var statisticsBtn: ImageButton
    lateinit var menuBtn: ImageButton

    // Close app on back press
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity(requireActivity())

            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stuff
        requestQueue = Volley.newRequestQueue(context)

        // Sounds
        backgroundMusic = MediaPlayer.create(context, R.raw.background)
            backgroundMusic.isLooping = true

        buySound = MediaPlayer.create(context, R.raw.buy)

        // List
        quizListLoadingSpinLayout = view.findViewById(R.id.quizListLoadingSpinLayout)

        quizListViewRefreshLayout = view.findViewById(R.id.quizListViewRefreshLayout)
        quizListViewRefreshLayout.setOnRefreshListener {
            fetchList()
        }

        quizListView = view.findViewById(R.id.quizListView)
        quizListView.setHasFixedSize(true);
        quizListView.layoutManager = GridLayoutManager(context, 3)

        quizListAdapter = HomeListAdapter(quizList, this)
        quizListView.adapter = quizListAdapter

        // Views
        coinsView = view.findViewById(R.id.coinsView)
        coinsView.text = MainActivity.coins.toString()

        musicBtn = view.findViewById(R.id.musicBtn)
            musicBtn.setOnClickListener {
                if(MainActivity.music){
                    musicBtn.setImageResource(R.drawable.music_off)
                    MainActivity.music = false

                }else{
                    musicBtn.setImageResource(R.drawable.music_on)
                    MainActivity.music = true

                }

                GlobalScope.launch {
                    MainActivity.dataManager.saveMusic(MainActivity.music)

                }

            }

            if(MainActivity.music){
                musicBtn.setImageResource(R.drawable.music_on)

            }else{
                musicBtn.setImageResource(R.drawable.music_off)

            }

        soundBtn = view.findViewById(R.id.soundBtn)
            soundBtn.setOnClickListener {
                if(MainActivity.sound){
                    soundBtn.setImageResource(R.drawable.sound_off)
                    MainActivity.sound = false

                }else{
                    soundBtn.setImageResource(R.drawable.sound_on)
                    MainActivity.sound = true

                }

                GlobalScope.launch {
                    MainActivity.dataManager.saveSound(MainActivity.sound)

                }

            }

            if(MainActivity.sound){
                soundBtn.setImageResource(R.drawable.sound_on)

            }else{
                soundBtn.setImageResource(R.drawable.sound_off)

            }

        statisticsBtn = view.findViewById(R.id.statisticsBtn)
        statisticsBtn.setOnClickListener {
            findNavController().navigate(R.id.homeToStats)
        }

        menuBtn = view.findViewById(R.id.menuBtn)
        menuBtn.setOnClickListener {
            menu(it)
        }

        fetchList()

    }

    // Show menu
    fun menu(view: View){
        val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.inflate(R.menu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item!!.itemId) {
                    R.id.about -> {
                        about()
                    }

                    R.id.licenses -> {
                        licenses()
                    }

                    R.id.exit -> {
                        finishAffinity(requireActivity())
                    }

                }
                true
            }
            popupMenu.show()

    }

    // Show about dialog
    fun about(){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.about)
            .setMessage(getString(R.string.aboutMsg))
            .setNeutralButton(getString(R.string.close)) { dialog, which -> }
            .setNegativeButton(getString(R.string.sourceCode)) { dialog, which ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.sourceCodeURL))))

            }
            .setPositiveButton(getString(R.string.moreApps)) { dialog, which ->
                try{
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=" + resources.getString(R.string.devID))))

                }catch(e: ActivityNotFoundException){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=" + resources.getString(R.string.devID))))

                }

            }
            .show()

    }

    // Show license dialog
    fun licenses(){
        val licenseList = ArrayList<HashMap<String, String>>()

        for(i in resources.getStringArray(R.array.licenseTitle).indices){
            val item = HashMap<String, String>()
            item["title"] = resources.getStringArray(R.array.licenseTitle)[i]
            item["by"] = resources.getStringArray(R.array.licenseBy)[i]
            item["license"] = resources.getStringArray(R.array.licenseLicense)[i]
            item["link"] = resources.getStringArray(R.array.licenseLink)[i]

            licenseList.add(item)
        }

        val adapter = SimpleAdapter(context, licenseList, R.layout.license_item,
            arrayOf("title", "by", "license", "link"),
            intArrayOf(R.id.licenseTitle, R.id.licenseBy, R.id.licenseLicense, R.id.licenseLink)
        )

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.licenses)
            .setNeutralButton(getString(R.string.close)) { dialog, which -> }
            .setAdapter(adapter) { dialogInterface, i ->
                if(resources.getStringArray(R.array.licenseLink)[i].startsWith("https://") || resources.getStringArray(R.array.licenseLink)[i].startsWith("http://")){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(resources.getStringArray(R.array.licenseLink)[i])))

                }

            }
            .show()

    }

    // Get & show offline list of quizzes
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

    // Get & show list of quizzes
    fun fetchList(){
        quizListLoadingSpinLayout.visibility = View.GONE

        quizListViewRefreshLayout.isRefreshing = true
        quizList.clear()

        if(!Tools.isNetworkConnected(requireContext())){
            fetchOfflineList()

        }else {
            quizListViewRefreshLayout.isRefreshing = false
            online = true

            val lockedList = MainActivity.quizList.split("|").toTypedArray()
            val lockedArray = mutableListOf<Boolean>()

            for (i in lockedList) {
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

    // Click on quiz item
    override fun onHomeItemClick(item: HomeItemData, position: Int) {
        if(!item.locked){
            // Go online btn
            if(item.value == "-1"){
                fetchList()

            // Reset & get questions for the game
            }else{
                quizListLoadingSpinLayout.visibility = View.VISIBLE

                QuizFragment.questionList.clear()
                QuizFragment.currentQuestion = -1

                QuizFragment.correct = 0
                QuizFragment.incorrect = 0
                QuizFragment.score = 0

                QuizFragment.correctList = mutableListOf("null", "null", "null", "null", "null", "null", "null", "null", "null", "null")

                if(online){
                    fetchQuestions(item.title, item.value, getString(R.string.quizURL, item.value))

                }else{
                    fetchOfflineQuestions(item.title, item.value)

                }

            }

        // Buy new quiz
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle(item.title)
                .setMessage(getString(R.string.purchaseMsg, item.price))
                .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                    if(MainActivity.coins >= item.price.toInt()){
                        if(MainActivity.sound){
                            buySound.start()
                        }

                        MainActivity.coins -= item.price.toInt()
                        coinsView.text = MainActivity.coins.toString()

                        MainActivity.quizList = MainActivity.quizList.replace(position.toString() + "l|", position.toString() + "u|")

                        GlobalScope.launch {
                            MainActivity.dataManager.saveCoins(MainActivity.coins)
                            MainActivity.dataManager.saveQuizList(MainActivity.quizList)
                        }

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

    // Get offline questions
    fun fetchOfflineQuestions(title: String, value: String){
        try {
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

            if (findNavController().currentDestination?.id == R.id.HomeFragment) {
                findNavController().navigate(R.id.homeToGetReady)

                if(MainActivity.music){
                    backgroundMusic.seekTo(0)
                    backgroundMusic.start()
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()

        }

    }

    // Get questions from url
    fun fetchQuestions(title: String, value: String, url: String){
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
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

                if (findNavController().currentDestination?.id == R.id.HomeFragment) {
                    findNavController().navigate(R.id.homeToGetReady)

                    if(MainActivity.music){
                        backgroundMusic.seekTo(0)
                        backgroundMusic.start()
                    }

                }

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