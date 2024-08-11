package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class UserData(
    val username: String,
    val password: String,
    val email: String,
    val year: String
)

interface UserService {
    @POST("/post_users")
    fun createUser(@Body userData: UserData): Call<String>
}

interface LoginCheck {
    @GET("/users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<Map<String, String>>
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userservice = retrofit.create(UserService::class.java)
        var userData = UserData("John Doe", "supersecret","johndoemail.com","4")



        val logincheck = retrofit.create(LoginCheck::class.java)

        val login = findViewById<Button>(R.id.login)
        login.setOnClickListener {
            val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            login.startAnimation(animationZoomIn)
            val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.scale_down)
            login.startAnimation(animationZoomOut)
            val builder =
                AlertDialog.Builder(this, R.style.AlertDialogCustom).create()
            val view = LayoutInflater.from(this).inflate(R.layout.login, null)
            builder.setView(view)
            builder.setCanceledOnTouchOutside(false)
            builder.show()
            val usernamel = view.findViewById<EditText>(R.id.username)
            val passwordl = view.findViewById<EditText>(R.id.password)
            val okl = view.findViewById<Button>(R.id.ok)
            okl.setOnClickListener {
                if (usernamel.text.toString() == "" || (passwordl.text.toString() == "")) {
                    Toast.makeText(this@MainActivity, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("MYAPP", "Button clicked!") // A clue!
                    val username = usernamel.text.toString()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = logincheck.getUser(username)
                            runOnUiThread {
                                if (response.isSuccessful) {
                                    val user = response.body()
                                    if (user == null) {
                                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        val password = user["password"]
                                        if (password == passwordl.text.toString()) {
                                            Log.i("MYAPP", "Correct password!")
                                            val intent = Intent(this@MainActivity, qrcode::class.java)
                                            intent.putExtra("user", username)
                                            intent.putExtra("email", user["email"])
                                            intent.putExtra("year",user["year"])
                                            startActivity(intent)
                                            builder.cancel()
                                        } else {
                                            Log.w("MYAPP", "Incorrect password")
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Incorrect password",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Log.e(
                                        "MYAPP",
                                        "API request failed with status code ${response.code()}"
                                    )
                                    Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MYAPP", "Exception during API request", e)
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Exception during API request 3",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        val signup = findViewById<Button>(R.id.signup)
        signup.setOnClickListener {
            val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            signup.startAnimation(animationZoomIn)
            val animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.scale_down)
            signup.startAnimation(animationZoomOut)
            val builder =
                AlertDialog.Builder(this, R.style.AlertDialogCustom).create()
            val view = LayoutInflater.from(this).inflate(R.layout.signup, null)
            builder.setView(view)
            builder.setCanceledOnTouchOutside(false)
            builder.show()
            val username = view.findViewById<EditText>(R.id.username)
            val pd1 = view.findViewById<EditText>(R.id.pd1)
            val pd2 = view.findViewById<EditText>(R.id.pd2)
            val email = view.findViewById<EditText>(R.id.email)
            val year = view.findViewById<EditText>(R.id.year)
            val ok = view.findViewById<Button>(R.id.ok)
            ok.setOnClickListener{
                if(username.text.toString() == "" || (pd1.text.toString() == "" ) || (pd2.text.toString() == "") || (email.text.toString() == "") || (year.text.toString() == "")){
                    Toast.makeText(this@MainActivity, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
                else if(pd1.text.toString() == pd2.text.toString()){
                    userData = UserData(username.text.toString(),pd1.text.toString(),email.text.toString(),year.text.toString())
                    val call = userservice.createUser(userData)
                    call.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MainActivity, "Record saved", Toast.LENGTH_SHORT).show()
                                builder.cancel()
                            } else {
                                Toast.makeText(this@MainActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.e("MYAPP", "Exception occurred", t)
                            Toast.makeText(this@MainActivity, "error in receiving response fail", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else{
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}