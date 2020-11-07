package com.example.filetransfer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import org.json.JSONException
import org.json.JSONObject
import java.io.*

@Throws(IOException::class)
fun getBytes(inputStream: InputStream): ByteArray? {
    val byteBuffer = ByteArrayOutputStream()
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var len = 0
    while (inputStream.read(buffer).also { len = it } != -1) {
        byteBuffer.write(buffer, 0, len)
    }
    return byteBuffer.toByteArray()
}

class MainActivity2 : AppCompatActivity(){

    lateinit var fileuri : Uri
    lateinit var actual_filename : String

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 109 && resultCode == RESULT_OK && data!=null){

            fileuri = data.data!!
            println(fileuri)

            val cursor = contentResolver.query(fileuri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                actual_filename = cursor.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))     //name of the file
                println(actual_filename)
            }

            val upload_button : Button = findViewById(R.id.button)
            val cut_button : Button = findViewById(R.id.button3)
            val image_selected : ImageView = findViewById(R.id.imageView)
            val filename : EditText = findViewById(R.id.fileName)
            val filename_status : TextView = findViewById(R.id.textView)

            upload_button.text = "Upload"
            cut_button.visibility = View.VISIBLE
            filename.visibility = View.VISIBLE

            filename_status.text = "File Selected"
            filename_status.visibility = View.VISIBLE

            image_selected.setImageURI(fileuri)
            image_selected.visibility = View.VISIBLE

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val upload_button : Button = findViewById(R.id.button)
        val cut_button : Button = findViewById(R.id.button3)
        val image_selected : ImageView = findViewById(R.id.imageView)
        val filename : EditText = findViewById(R.id.fileName)
        val filename_status : TextView = findViewById(R.id.textView)

        cut_button.setOnClickListener {
            upload_button.text = "Select File"
            image_selected.visibility = View.INVISIBLE
            cut_button.visibility = View.INVISIBLE
            filename.visibility = View.INVISIBLE
            filename.setText("")
            filename_status.visibility = View.INVISIBLE
        }

        upload_button.setOnClickListener{
            if(upload_button.text != "Upload"){

                var i = Intent()
                i.setType("image/*")
                i.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(i, "Choose Picture"), 109)
            }
            else if(filename.getText().toString() == ""){
                filename_status.text = "Enter Filename First!"

            }
            else{

                val file_name = filename.getText().toString()
                val url = "http://41483a7a2543.ngrok.io/upload"

                val MEDIA_TYPE = "image/*".toMediaType()

                val iStream = contentResolver.openInputStream(fileuri)
                val inputData = getBytes(iStream!!)

                val formBody = inputData?.let { it1 -> RequestBody.create(MEDIA_TYPE, it1) }?.let { it2 ->
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("file", file_name, it2)
                            .addFormDataPart("actual_file_name" , actual_filename )
                            .addFormDataPart("user_file_name", file_name)
                            .build()
                };

                val request = Request.Builder().method("POST", formBody).url(url).build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val body = response?.body?.string()

                        try {
                            val json = JSONObject(body)
                            println(json.getString("result"))

                            if (json.getString("result") == "Filename alteady exists") {
                                runOnUiThread {
                                    filename_status.text = json.getString("result")
                                    upload_button.visibility = View.VISIBLE
                                    cut_button.visibility = View.VISIBLE
                                    image_selected.visibility = View.VISIBLE
                                    filename.visibility = View.VISIBLE
                                }
                            } else {
                                runOnUiThread {
                                    filename_status.text = json.getString("result")
                                    upload_button.visibility = View.VISIBLE
                                    upload_button.text = "Select File"
                                }
                            }
                        } catch (e: JSONException) {
                            runOnUiThread {
                                filename_status.text = "Please check your Internet or Server is down"
                                upload_button.visibility = View.VISIBLE
                                cut_button.visibility = View.VISIBLE
                                image_selected.visibility = View.VISIBLE
                                filename.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to execute request!")
                        println(e)
                    }
                })

                filename_status.text = "LOADING..."
                cut_button.visibility = View.INVISIBLE
                image_selected.visibility = View.INVISIBLE
                filename.visibility = View.INVISIBLE
                filename.setText("")
                upload_button.visibility = View.INVISIBLE
                //
            }
        }

    }
}