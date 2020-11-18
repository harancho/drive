package com.example.filetransfer

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {
        println("hello")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
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
        setContentView(R.layout.activity_main)

        val sp: SharedPreferences = getApplicationContext().getSharedPreferences("myToken" , Context.MODE_PRIVATE)
        if(sp.getString("token","") != ""){
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        val username : EditText = findViewById(R.id.editTextTextPersonName)
        val password : EditText = findViewById(R.id.editTextTextPassword)
        val status : TextView = findViewById(R.id.textView5)
        val login_button : Button = findViewById(R.id.button5)
        val new_user : Button = findViewById(R.id.button7)

        new_user.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }

        login_button.setOnClickListener {
            if(username.getText().toString() == "" || password.getText().toString() == ""){
                status.text = "Please enter the Credentials First!!"
                status.visibility = View.VISIBLE
            }
            else{
                // write login code here

                username.visibility = View.INVISIBLE
                password.visibility = View.INVISIBLE
                login_button.visibility = View.INVISIBLE
                new_user.visibility = View.INVISIBLE
                status.text = "LOADING..."
                status.visibility = View.VISIBLE

                var url = "http://c9084a47d38f.ngrok.io/login"
                var formBody = FormBody.Builder()
                    .add("username", username.getText().toString())
                    .add("password", password.getText().toString())
                    .build();
                var request = Request.Builder().method("POST", formBody).url(url).build()
                var client = OkHttpClient()

                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val body = response?.body?.string()
                        println(body)

                        try {
                            val json = JSONObject(body)
                            var result1 = json.getString("result1")
                            var result2 = json.getString("result2")

                            if(result2 == "no"){
                                runOnUiThread {
                                    username.setText("")
                                    password.setText("")
                                    username.visibility = View.VISIBLE
                                    password.visibility = View.VISIBLE
                                    login_button.visibility = View.VISIBLE
                                    new_user.visibility = View.VISIBLE
                                    status.text = result1
                                }
                            }
                            else{
                                var token = json.getString("token")

                                val sharedPreferences : SharedPreferences = getSharedPreferences("myToken" , Context.MODE_PRIVATE)
                                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putString("token" , token)
                                editor.putString("username" , username.getText().toString())
                                editor.commit()

                                val intent = Intent(this@MainActivity, MainActivity4::class.java)
                                startActivity(intent)

                            }

                        } catch (e: JSONException) {
                            println("Failed to execute request!")
                            println(e)
                            runOnUiThread {
                                status.text = "Server is down, Please try later!"
                                username.visibility = View.VISIBLE
                                password.visibility = View.VISIBLE
                                login_button.visibility = View.VISIBLE
                                new_user.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to execute request!")
                        println(e)
                        runOnUiThread {
                            status.text = "Please check your Internet and try again!"
                            username.visibility = View.VISIBLE
                            password.visibility = View.VISIBLE
                            login_button.visibility = View.VISIBLE
                            new_user.visibility = View.VISIBLE
                        }
                    }
                })



                //
            }
        }

    }
}