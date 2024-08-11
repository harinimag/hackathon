package com.example.hackathon

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface MeetingYear {
    @GET("/meetings/{year}")
    suspend fun meetingYear(@Path("year") year: String): Response<List<String>>
}

class mywork : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mywork)
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
            val intent = Intent(this@mywork, qrcode::class.java)
            intent.putExtra("user", user)
            intent.putExtra("email", email)
            intent.putExtra("year",year)
            startActivity(intent)
        }
        var meetingdetails: List<String>? = null
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val meetingyear = retrofit.create(MeetingYear::class.java)
        val worktable = findViewById<TableLayout>(R.id.worktable)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = meetingyear.meetingYear(year!!)
                withContext(Dispatchers.Main) {
                    if (response != null && response.isSuccessful) {
                        meetingdetails = response.body()
                        if (meetingdetails != null) {
                            for (l in meetingdetails!!) {
                                val borderDrawable: Drawable? = ContextCompat.getDrawable(this@mywork, R.drawable.border)

                                val tableRow = TableRow(this@mywork)
                                tableRow.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@mywork,
                                        R.color.white
                                    )
                                )
                                tableRow.background = borderDrawable


                                val textView = TextView(this@mywork)
                                val layoutparams6 = TableRow.LayoutParams(
                                    TableRow.LayoutParams.WRAP_CONTENT,
                                    TableRow.LayoutParams.WRAP_CONTENT,
                                    1f
                                )
                                textView.background = borderDrawable

                                textView.layoutParams = layoutparams6
                                textView.gravity = Gravity.CENTER
                                textView.setTextColor(
                                    ContextCompat.getColor(
                                        this@mywork,
                                        R.color.black
                                    )
                                )
                                textView.setTextSize(25f)
                                textView.text = l



                                tableRow.addView(textView)
                                worktable.addView(tableRow)
                            }
                        } else {
                            Log.d("MYAPP", "meetingdetails is null for $user")
                        }
                    } else {
                        Log.e(
                            "MYAPP",
                            "API request failed with status code ${response?.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "MYAPP",
                    "Exception during API request for $user",
                    e
                ) // Log exception during API request
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@mywork,
                        "Exception during API request for $user",
                        Toast.LENGTH_SHORT
                    ).show() // Show toast message for exception
                }
            }
            catch (e: Exception) {
                Log.e("MYAPP", "Exception during API request", e) // Log exception during API request
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@mywork, "Exception during API request", Toast.LENGTH_SHORT).show() // Show toast message for exception
                }
            }
        }

    }
}