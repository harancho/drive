package com.example.filetransfer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity3 : AppCompatActivity() {
    var filename2 : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val download_button : Button = findViewById(R.id.button4)
        val filename_status : TextView = findViewById(R.id.textView3)
        val list :ListView = findViewById(R.id.listView)
        val heading : TextView = findViewById(R.id.textView2)
        val testing_image : ImageView = findViewById(R.id.imageView2)

        var mobileArray = mutableListOf<String>()
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mobileArray)
        list.adapter = adapter

        var url = "http://7051140d112a.ngrok.io/filenames"
        var formBody = FormBody.Builder()
            .build();
        var request = Request.Builder().method("POST", formBody).url(url).build()

        var client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body?.string()

                try {
                    val json = JSONObject(body)
                    var result = json.getJSONArray("result")
                    println(result)

                    for (i in 0 until result.length()) {
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

                } catch (e: JSONException) {

                    runOnUiThread {
                        println(e)
                        filename_status.text = "Server is down, Please try later!"
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request!")
                runOnUiThread {
                    val filename_status: TextView = findViewById(R.id.textView3)
                    filename_status.text = "Please check your Internet and try again!"
                }
                println(e)
            }
        })


        download_button.setOnClickListener {
            if(download_button.text == "Select File"){
                filename_status.text = "Select File First"
                filename_status.visibility = View.VISIBLE
                testing_image.visibility = View.INVISIBLE
            }
            else{

                val heading : TextView = findViewById(R.id.textView2)
                val filename : TextView = findViewById(R.id.textView3)
                val files : ListView = findViewById(R.id.listView)
                val download_button : Button = findViewById(R.id.button4)
                val image : ImageView = findViewById(R.id.imageView2)

                filename2 = filename.text.toString()
                // write code for downloading here

                var url = "http://7051140d112a.ngrok.io/download"
                var formBody = FormBody.Builder()
                        .add("filename", filename_status.text.toString())
                        .build();
                var request = Request.Builder().method("POST", formBody).url(url).build()

                var client = OkHttpClient()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {


                        val body = response.body!!.bytes()

                        runOnUiThread {

                            try {
                                val bitmap: Bitmap = BitmapFactory.decodeByteArray(body, 0, body?.size?.toInt())
                                testing_image.setImageBitmap(bitmap)
                                testing_image.visibility = View.VISIBLE
                                files.visibility = View.VISIBLE
                                heading.visibility = View.VISIBLE
                                download_button.text = "Select File"
                                download_button.visibility = View.VISIBLE

                                // write code to save file in pc here

                                val filepath : File = File(getExternalFilesDir(null),"/FileTransferApp/")
                                if(!filepath.exists()){
                                    filepath.mkdir()
                                }
                                println(filepath)
                                val file = File(filepath.absolutePath,filename2 + ".jpg")
                                if(!file.exists()){
                                    file.createNewFile()
                                }

                                val outputStream = FileOutputStream(file)
                                outputStream.write(body)
                                outputStream.close()

                                filename.text = "File Downloaded"
                                //

                            } catch (e: NullPointerException) {
                                println("Failed to execute request!")
                                println(e)
                                heading.visibility = View.VISIBLE
                                files.visibility = View.VISIBLE
                                download_button.visibility = View.VISIBLE
                                heading.text = "Server is down, Please try later!"
                                filename.text = ""
                                download_button.text = "Select File"
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to execute request!")
                        println(e)
                        runOnUiThread {
                            heading.visibility = View.VISIBLE
                            files.visibility = View.VISIBLE
                            download_button.visibility = View.VISIBLE
                            heading.text = "Please check your Internet and try again!"
                            filename.text = ""
                            download_button.text = "Select File"
                        }
                    }
                })

                heading.visibility = View.INVISIBLE
                files.visibility = View.INVISIBLE
                download_button.visibility = View.INVISIBLE
                image.visibility = View.INVISIBLE
                filename.text = "DOWNLOADING..."

                //
            }
        }

        list.setOnItemClickListener { parent, view, position, id ->
            filename_status.visibility = View.VISIBLE
            filename_status.text = mobileArray[position]
            testing_image.visibility = View.INVISIBLE
            download_button.text = "Download"
        }


    }

}