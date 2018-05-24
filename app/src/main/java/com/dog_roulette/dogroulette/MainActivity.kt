package com.dog_roulette.dogroulette

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import khttp.get
import android.os.StrictMode
import org.json.JSONArray
import org.json.JSONObject
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerListener
import java.util.*

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity() {

    fun search_youtube(query_params: String = ""): String? {

        // Get reference to API Key
        val API_KEY: String = getString(R.string.API_KEY)

        // TODO: Figure out a way to randomize search results

        // API url to connect to
        val url = "https://www.googleapis.com/youtube/v3/search"

        // Video searches. Do this to minimize people seeing the same videos
        var query = arrayListOf<String>("dog videos", "adorable dogs", "funny dogs", "nice dogs")

        // Pick a random string from teh query array
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
                "start_index" to (50 - 49).toString(),
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

            // Add values from items -> id -> videoId to the results_list
            results_list = rez.getJSONObject(0).getJSONObject("id")
            println(results_list["videoId"].toString())
        }
        return results_list["videoId"].toString()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reference to the video player
        val youTubePlayerView: YouTubePlayerView = findViewById(R.id.player_view)

        // The player will automatically release itself when the activity is destroyed.
        // The player will automatically pause when the activity is paused
        lifecycle.addObserver(youTubePlayerView)

        // Do asynchronously
        doAsync {
            youTubePlayerView.initialize(object : YouTubePlayerInitListener {
                override fun onInitSuccess(initializedYouTubePlayer: YouTubePlayer) {
                    initializedYouTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                        var fetchedId: String? = search_youtube()
                        override fun onReady() {
                            // Do this back in the main thread
                            uiThread {
                                initializedYouTubePlayer.loadVideo(fetchedId, 0F)
                            }
                        }
                    })
                }
            }, true)
        }
    }

}
