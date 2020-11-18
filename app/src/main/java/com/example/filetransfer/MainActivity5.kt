package com.example.filetransfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.system.exitProcess

class MainActivity5 : AppCompatActivity() {

    override fun onBackPressed() {
        println("hello")

        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity5 )
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
        setContentView(R.layout.activity_main5)

        val username: EditText = findViewById(R.id.editTextTextPersonName2)
        val password: EditText = findViewById(R.id.editTextTextPassword2)
        val confirm_password: EditText = findViewById(R.id.editTextTextPassword3)
        val status: TextView = findViewById(R.id.textView6)
        val register_button: Button = findViewById(R.id.button6)
        val already_a_user: Button = findViewById(R.id.button8)

        already_a_user.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        register_button.setOnClickListener {
            if(username.getText().toString() == "" || password.getText().toString() == "" || confirm_password.getText().toString() == ""){
                status.text = "Please enter the Credentials First!!"
                status.visibility = View.VISIBLE
            }
            else if(password.getText().toString() != confirm_password.getText().toString()){
                status.text = "Passwords didn't match, please check again!"
                status.visibility = View.VISIBLE
            }
            else{
                //write code to register here

                username.visibility = View.INVISIBLE
                password.visibility = View.INVISIBLE
                confirm_password.visibility = View.INVISIBLE
                register_button.visibility = View.INVISIBLE
                already_a_user.visibility = View.INVISIBLE
                status.text = "LOADING..."
                status.visibility = View.VISIBLE

                var url = "http://c9084a47d38f.ngrok.io/register"
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
                                    status.text = result1
                                    username.setText("")
                                    password.setText("")
                                    confirm_password.setText("")
                                    username.visibility = View.VISIBLE
                                    password.visibility = View.VISIBLE
                                    confirm_password.visibility = View.VISIBLE
                                    register_button.visibility = View.VISIBLE
                                    already_a_user.visibility = View.VISIBLE
                                }
                            }else{
                                runOnUiThread {
                                    val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@MainActivity5 )
                                    builder.setMessage("Please login now..");
                                    builder.setTitle("Registered Successfully!");
                                    builder.setCancelable(false);

                                    builder.setPositiveButton("OK") { dialog, which ->
                                        var intent = Intent(this@MainActivity5,MainActivity::class.java)
                                        startActivity(intent)
                                    }

                                    val alertDialog = builder.create()
                                    alertDialog.show()
                                }
                            }


                        } catch (e: JSONException) {
                            println("Failed to execute request!")
                            println(e)
                            runOnUiThread {
                                status.text = "Server is down, Please try later!"
                                username.visibility = View.VISIBLE
                                password.visibility = View.VISIBLE
                                confirm_password.visibility = View.VISIBLE
                                register_button.visibility = View.VISIBLE
                                already_a_user.visibility = View.VISIBLE
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
                            confirm_password.visibility = View.VISIBLE
                            register_button.visibility = View.VISIBLE
                            already_a_user.visibility = View.VISIBLE
                        }
                    }
                })

                //
            }
        }

    }
}