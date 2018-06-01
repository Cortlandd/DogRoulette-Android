package com.dog_roulette.dogroulette

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.support.v4.uiThread
import org.jetbrains.anko.uiThread
import android.support.annotation.NonNull
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.pierfrancescosoffritti.youtubeplayer.player.*
import khttp.get
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import java.util.*

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread

/**
 * A simple [Fragment] subclass.
 *
 */
class PlayerFragment : Fragment() {

    //private val initializedYouTubePlayer: YouTubePlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_player, container, false)

        // TODO: Remove once app is completely ready. Figure out how to run in another thread
        // val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        // StrictMode.setThreadPolicy(policy)

        val fetch_button: Button = view.findViewById(R.id.fetch_button)
        val filter_text: EditText = view.findViewById(R.id.filter_text)

        val youTubePlayerView: YouTubePlayerView = view.findViewById(R.id.frag_player_view)

        lifecycle.addObserver(youTubePlayerView)
        // TODO: Test this
        //youTubePlayerView.getPlayerUIController().showFullscreenButton(false)

        youTubePlayerView.initialize(object : YouTubePlayerInitListener{
            override fun onInitSuccess(initializedYouTubePlayer: YouTubePlayer) {
                initializedYouTubePlayer.addListener(object : AbstractYouTubePlayerListener(){
                    var fetchedId = search_youtube()
                    override fun onReady() {
                        //super.onReady()
                        initializedYouTubePlayer.loadVideo(fetchedId, 0F)
                        doAsync {
                            fetch_button.setOnClickListener {
                                doAsyncResult {

                                    var filterText = search_youtube(filter_text.text.toString())

                                    uiThread {

                                        initializedYouTubePlayer.loadVideo(filterText, 0F)

                                    }
                                }
                            }
                        }
                    }
                })
            }
        }, true)

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun search_youtube(query_params: String = ""): String? {

        // Get reference to API Key
        val API_KEY: String = getString(R.string.API_KEY)

        // TODO: Figure out a way to randomize search results

        // API url to connect to
        val url = "https://www.googleapis.com/youtube/v3/search"

        // Video searches. Do this to minimize people seeing the same videos
        var query = arrayListOf<String>("dog videos ", "adorable dogs ", "funny dogs ", "nice dogs ")

        // Pick a random string from the query array
        var word: String = query[Random().nextInt(query.size)]

        // If a user doesn;t enter text in the filter bar, use the default query
        if (query_params != "") {
            word += query_params
        }
        // Show me what what searched
        Log.i("TAG", word)

        // Store search params results inside JSON Object
        var results_list = JSONObject()

        // Parameters/Header info with video search
        val search_params = mapOf(
                "q" to word,
                "part" to "id, snippet",
                "start_index" to (50-49).toString(),
                "safeSearch" to "none",
                "key" to API_KEY,
                "type" to "video",
                "maxResults" to 50.toString()
        )

        // HTTP request go the YouTube API along with specified parameters
        val req = get(url, params = search_params)

        // Log the requested link
        Log.i("Request URL", req.url)

        // If HTTP response code is good/200 get a videoId from the data
        if (req.statusCode == 200) {
            // Place the values under items to the variable rez
            var rez = req.jsonObject.getJSONArray("items")

            var randRange = (0..49).toList()
            var rand = Random()
            var randElement = randRange.get(rand.nextInt(randRange.size))
            println(randElement)
            //var searchRange = rand.nextInt(1000-900) + 900

            // Add values from items -> id -> videoId to the results_list
            results_list = rez.getJSONObject(randElement).getJSONObject("id")
            println(results_list["videoId"].toString())
        }
        return results_list["videoId"].toString()
    }
}
