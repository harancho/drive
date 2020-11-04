package com.example.filetransfer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.IOException

class MainActivity2 : AppCompatActivity() {

    lateinit var filepath : Uri

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
                startActivityForResult(Intent.createChooser(i, "Choose Picture"), 111)
            }
            else if(filename.getText().toString() == ""){
                filename_status.text = "Enter Filename First!"
                //filename_status.visibility = View.VISIBLE
            }
            else{
                //write code here for passing the file
                //filename_status.visibility = View.VISIBLE

                val file_name = filename.getText().toString()

                val url = "http://052c5bec0670.ngrok.io"
                val formBody = FormBody.Builder()
                    .add("filename", file_name)
                    .build();

                val request = Request.Builder().method("POST", formBody).url(url).build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val body = response?.body?.string()

                        try {
                            val json = JSONObject(body)
                            println(json.getString("result"))

                            if(json.getString("result") == "Filename alteady exists"){
                                runOnUiThread{
                                    filename_status.text = json.getString("result")
                                    upload_button.visibility = View.VISIBLE
                                    cut_button.visibility = View.VISIBLE
                                    image_selected.visibility = View.VISIBLE
                                    filename.visibility = View.VISIBLE
                                }
                            }
                            else{
                                runOnUiThread {
                                    filename_status.text = json.getString("result")
                                    upload_button.visibility = View.VISIBLE
                                    upload_button.text = "Select File"
                                }
                            }
                        }catch(e : JSONException){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data!=null){

            filepath = data.data!!
            println(filepath)

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

//            var Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)                 for actual file
//            image_selected.setImageBitmap(Bitmap)

            image_selected.setImageURI(filepath)
            image_selected.visibility = View.VISIBLE


        }
    }
}
