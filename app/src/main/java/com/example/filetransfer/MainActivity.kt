package com.example.filetransfer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upload_button : Button = findViewById(R.id.button1)
        val download_button : Button = findViewById(R.id.button2)

        upload_button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)

        }

        download_button.setOnClickListener {
            var intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)

        }
    }
}