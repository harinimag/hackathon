package com.example.hackathon


import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hackathon.R
import java.util.*

class calendar : AppCompatActivity() {

    private val REQUEST_CODE_WRITE_CALENDAR = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val user = intent.getStringExtra("user")
        val email = intent.getStringExtra("email")
        val year = intent.getStringExtra("year")
        val qr = findViewById<Button>(R.id.qr)
        qr.setOnClickListener {
            val intent = Intent(this@calendar, qrcode::class.java)
            intent.putExtra("user", user)
            intent.putExtra("email", email)
            intent.putExtra("year",year)
            startActivity(intent)
        }

        val work = findViewById<Button>(R.id.work)
        work.setOnClickListener {
            if(year == "4"){
                val intent = Intent(this,work::class.java)
                intent.putExtra("user", user)
                intent.putExtra("email", email)
                intent.putExtra("year",year)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, mywork::class.java)
                intent.putExtra("user", user)
                intent.putExtra("email", email)
                intent.putExtra("year",year)
                startActivity(intent)
            }
        }
    }
}
