package com.dog_roulette.dogroulette

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import khttp.get

class MainActivity : AppCompatActivity() {

    val API_KEY: String = "AIzaSyCWDXs_WwDOK3XQu8MPUx9aSdfJ8KOPfRE"

    fun search_youtube(query_params: String = ""): List<String> {


        val url = "https://www.googleapis.com/youtube/v3/search"
        var query: String = "dog videos" + " "

        if (query_params != "") {
            query += query_params
        }
        Log.i("TAG", query)

        // Store search params results here
        var results_list = listOf<String>()
        val search_params = mapOf(
                "q" to query,
                "part" to "id, snippet",
                "start_index" to (50-49).toString(),
                "safeSearch" to "none",
                "key" to API_KEY,
                "type" to "video",
                "maxResults" to 50.toString()

        )

        val req = get(url, search_params)

        if (req.statusCode == 200) {
            results_list += req.jsonObject.getString("items")
        }

        println("FINISHED RUNNING CHECK IT OUT")
        return results_list
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // run inside a separate thread
        val t = Thread(Runnable {
            // Insert some method call here.
            search_youtube("ROTWEILER")
        })

    }


}
