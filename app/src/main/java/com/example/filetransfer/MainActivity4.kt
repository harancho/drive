package com.example.filetransfer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class MainActivity4 : AppCompatActivity() {

    override fun onBackPressed() {
        println("hello")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity4)
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
        setContentView(R.layout.activity_main4)

        val upload_button : Button = findViewById(R.id.button1)
        val download_button : Button = findViewById(R.id.button2)
        val logout_button : ImageButton = findViewById(R.id.imageButton2)

//        val sp: SharedPreferences = getApplicationContext().getSharedPreferences("myToken" , Context.MODE_PRIVATE)
//        if(sp.getString("token","") == ""){
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        logout_button.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity4)
            builder.setMessage("Do you want to logout ?");
            builder.setTitle("Alert !");
            builder.setCancelable(false);

            builder.setPositiveButton("Yes") { dialog, which ->
                val sharedPreferences : SharedPreferences = getSharedPreferences("myToken" , Context.MODE_PRIVATE)
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