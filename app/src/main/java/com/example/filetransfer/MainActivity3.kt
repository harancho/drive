package com.example.filetransfer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import kotlin.math.log
import kotlin.system.exitProcess

class MainActivity3 : AppCompatActivity() {
    var filename2 : String = ""

    override fun onBackPressed() {
        println("hello")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity3)
        builder.setMessage("Do you want to exit ?");
        builder.setTitle("Alert !");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes") { dialog, which ->
            finishAffinity();
            exitProcess(0);
        }

        builder.setNegativeButton("No") { dialog, which -> dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val user : TextView = findViewById(R.id.textView7)
        val home_button : ImageButton = findViewById(R.id.imageButton4)
        val logout_button : ImageButton = findViewById(R.id.imageButton5)
        val download_button : Button = findViewById(R.id.button4)
        val filename_status : TextView = findViewById(R.id.textView3)
        val list :ListView = findViewById(R.id.listView)
        val heading : TextView = findViewById(R.id.textView2)
        val testing_image : ImageView = findViewById(R.id.imageView2)

        home_button.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        logout_button.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity3 )
            builder.setMessage("Do you want to logout ?");
            builder.setTitle("Alert !");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes") { dialog, which ->
                val sharedPreferences : SharedPreferences = getSharedPreferences("myToken", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.remove("token")
                editor.remove("username")
                editor.commit()

                println("byeeeee")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            builder.setNegativeButton("No") { dialog, which -> dialog.cancel()
            }

            val alertDialog = builder.create()
            alertDialog.show()

        }

        val sp: SharedPreferences = getApplicationContext().getSharedPreferences("myToken" , Context.MODE_PRIVATE)
        user.text = sp.getString("username" , "").toString()

        var mobileArray = mutableListOf<String>()
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mobileArray)
        list.adapter = adapter

        var url = "http://c9084a47d38f.ngrok.io/filenames"
        var formBody = FormBody.Builder()
                .add("username" , sp.getString("username", "").toString())
                .add("token", sp.getString("token" ,"").toString())
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

                    if(result.length() == 0){
                        runOnUiThread {
                            heading.text = "No Files Available"
                        }
                    }

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

                var url = "http://c9084a47d38f.ngrok.io/download"
                var formBody = FormBody.Builder()
                        .add("filename", filename_status.text.toString())
                        .add("token", sp.getString("token","").toString())
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
                                home_button.visibility = View.VISIBLE
                                logout_button.visibility = View.VISIBLE
                                files.visibility = View.VISIBLE
                                heading.visibility = View.VISIBLE
                                download_button.text = "Select File"
                                download_button.visibility = View.VISIBLE

                                // write code to save file in pc here

                                val filepath : File = File(getExternalFilesDir(null),"/DriveApp/")
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
                                home_button.visibility = View.VISIBLE
                                logout_button.visibility = View.VISIBLE
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
                            logout_button.visibility = View.VISIBLE
                            home_button.visibility = View.VISIBLE
                            heading.visibility = View.VISIBLE
                            files.visibility = View.VISIBLE
                            download_button.visibility = View.VISIBLE
                            heading.text = "Please check your Internet and try again!"
                            filename.text = ""
                            download_button.text = "Select File"
                        }
                    }
                })

                logout_button.visibility = View.INVISIBLE
                home_button.visibility = View.INVISIBLE
                heading.text = "Available Files"
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