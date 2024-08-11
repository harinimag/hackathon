package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MeetingData(
    val details: String,
    val year: String
)
interface MeetingService {
    @POST("/post_meetings")
    fun createMeeting(@Body meetingData: MeetingData): Call<String>
}

class work : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work)
        val user = intent.getStringExtra("user")
        val email = intent.getStringExtra("email")
        val year = intent.getStringExtra("year")
        val calen = findViewById<Button>(R.id.calendar)
        calen.setOnClickListener {
            val intent = Intent(this, calendar::class.java)
            intent.putExtra("user", user)
            intent.putExtra("email", email)
            intent.putExtra("year",year)
            startActivity(intent)
        }
        val qr = findViewById<Button>(R.id.qr)
        qr.setOnClickListener {
            val intent = Intent(this@work, qrcode::class.java)
            intent.putExtra("user", user)
            intent.putExtra("email", email)
            intent.putExtra("year",year)
            startActivity(intent)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val meetingservice = retrofit.create(MeetingService::class.java)
        val create = findViewById<EditText>(R.id.create)
        val yeare = findViewById<EditText>(R.id.year)
        val okbutton = findViewById<Button>(R.id.okbutton)
        okbutton.setOnClickListener {
            if(create.text.toString() == "" || yeare.text.toString() == ""){
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
            else if(yeare.text.toString() != "1" && yeare.text.toString() != "2" && yeare.text.toString() != "3" && yeare.text.toString() != "4"){
                Toast.makeText(this, "Enter valid year", Toast.LENGTH_SHORT).show()
            }
            else{
                val meetingData = MeetingData(create.text.toString(),yeare.text.toString())
                val call = meetingservice.createMeeting(meetingData)
                call.enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@work, "Sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@work, "try again", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("MYAPP", "Exception occurred", t)
                        Toast.makeText(this@work, "error in receiving response fail", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}