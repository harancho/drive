package com.example.filetransfer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.net.URI

class MainActivity : AppCompatActivity() {

    lateinit var filepath : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val select_file : Button = findViewById(R.id.button)
        val cut_button : Button = findViewById(R.id.button3)
        val image_selected : ImageView = findViewById(R.id.imageView)

        cut_button.setOnClickListener {
            select_file.text = "Select File"
            image_selected.visibility = View.INVISIBLE
            cut_button.visibility = View.INVISIBLE
        }

        select_file.setOnClickListener{
            if(select_file.text != "Upload"){
                var i = Intent()
                i.setType("image/*")
                i.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(i,"Choose Picture"),111)
            }
            else{

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data!=null){

            filepath = data.data!!
            println(filepath)

            val select_file : Button = findViewById(R.id.button)
            val image_selected : ImageView = findViewById(R.id.imageView)
            val cut_button : Button = findViewById(R.id.button3)

            select_file.text = "Upload"
            cut_button.visibility = View.VISIBLE


            var Bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            image_selected.setImageBitmap(Bitmap)
            image_selected.visibility = View.VISIBLE


        }
    }
}
