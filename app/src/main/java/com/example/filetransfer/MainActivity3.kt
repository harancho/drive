package com.example.filetransfer

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val download_button : Button = findViewById(R.id.button4)
        val filename_status : TextView = findViewById(R.id.textView3)
        val list :ListView = findViewById(R.id.listView)
        val heading : TextView = findViewById(R.id.textView2)

        // write code to make request to download route for all filenames

        var mobileArray = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mobileArray)
        list.adapter = adapter

        val url = "http://41483a7a2543.ngrok.io/filenames"
        val formBody = FormBody.Builder()
            .build();
        val request = Request.Builder().method("POST", formBody).url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                try{
                    val json = JSONObject(body)
                    var result = json.getJSONArray("result")
                    println(result)

                    for(i in 0 until result.length()){
                        var filename = result.getString(i)

                        runOnUiThread {
                            mobileArray.add(filename);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    runOnUiThread {
                        download_button.visibility = View.VISIBLE
                        list.visibility = View.VISIBLE
                        heading.visibility = View.VISIBLE
                        filename_status.visibility = View.INVISIBLE
                    }

                }catch(e : JSONException){

                    runOnUiThread {
                        println(e)
                        filename_status.text = "Please check your Internet or Server is down"
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request!")
                println(e)
            }
        })


        download_button.setOnClickListener {
            if(download_button.text == "Select File"){
                filename_status.text = "Select File First"
                filename_status.visibility = View.VISIBLE
            }
            else{
                // write code for downloading here


                //
            }
        }

        list.setOnItemClickListener { parent, view, position, id ->
            filename_status.visibility = View.VISIBLE
            filename_status.text = mobileArray[position]
            download_button.text = "Download"
        }


    }

}