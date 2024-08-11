import android.Manifest
import android.content.ContentValues
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

        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val addEventButton: Button = findViewById(R.id.addEventButton)

        // Check for calendar permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR),
                REQUEST_CODE_WRITE_CALENDAR)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Handle date selection
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }.timeInMillis

            // Example event details
            addEventButton.setOnClickListener {
                addEventToCalendar("Sample Event", "Event Description", "Event Location", selectedDate, selectedDate + 3600000)
            }
        }
    }

    private fun addEventToCalendar(title: String, description: String, location: String, startTime: Long, endTime: Long) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, 1) // Default calendar ID, adjust as needed
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            ?.let { uri ->
                Toast.makeText(this, "Event added: $uri", Toast.LENGTH_SHORT).show()
            } ?: run {
            Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_CALENDAR) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now add events
            } else {
                Toast.makeText(this, "Calendar permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
