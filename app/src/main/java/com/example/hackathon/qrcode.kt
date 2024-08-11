package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Bitmap
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


data class User(val username: String, val email: String, val year: String)


class qrcode : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_qrcode)
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
                val intent = Intent(this,mywork::class.java)
                intent.putExtra("user", user)
                intent.putExtra("email", email)
                intent.putExtra("year",year)
                startActivity(intent)
            }
        }

        fun generateQRCode(text: String): Bitmap {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            return bmp
        }

        val name = findViewById<TextView>(R.id.name)
        name.text = user
        val qrCodeBitmap = generateQRCode(user!!)
        val imageView = findViewById<ImageView>(R.id.qrcode)
        imageView.setImageBitmap(qrCodeBitmap)
        val scanButton = findViewById<Button>(R.id.scanner)
        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan a QR code")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(false)
            integrator.initiateScan()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userservice = retrofit.create(UserService::class.java)
        var userData = UserData("John Doe", "supersecret","johndoemail.com","4")



        val logincheck = retrofit.create(LoginCheck::class.java)
        var emailn:String = "none"
        var yearn: String = "none"
        if (result.contents != null) {
            val usern = result.contents
            if(usern != null){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = logincheck.getUser(usern)
                        runOnUiThread {
                            if (response.isSuccessful) {
                                val user = response.body()
                                if (user == null) {
                                    Toast.makeText(this@qrcode, "User not found", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    emailn = user["email"]!!
                                    yearn = user["year"]!!
                                }
                            } else {
                                Log.e(
                                    "MYAPP",
                                    "API request failed with status code ${response.code()}"
                                )
                                Toast.makeText(this@qrcode, "User not found", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MYAPP", "Exception during API request", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@qrcode,
                                "Exception during API request 3",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                Toast.makeText(this, "User: "+usern!!+"\nEmail: "+emailn+"\nYear: "+yearn, Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Scan failed or canceled", Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}