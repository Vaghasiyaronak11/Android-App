package com.example.caretrack;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Calendar;

public class BookAppointmentActivity extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4;
    TextView tv;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton, timeButton, btnBook;
    private DatabaseReference mDatabase;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference("Appointments");

        ed1 = findViewById(R.id.editTextLTPAppFullName);
        ed2 = findViewById(R.id.editTextLTPAddress);
        ed3 = findViewById(R.id.editTextLTPPinCode);
        ed4 = findViewById(R.id.editTextLTPContactNumber);
        dateButton = findViewById(R.id.buttonAppDate);
        timeButton = findViewById(R.id.buttonAppTime);
        btnBook = findViewById(R.id.buttonBookAppointment);

        // Initialize DatePicker and TimePicker
        initDatePicker();
        initTimePicker();

        dateButton.setOnClickListener(view -> datePickerDialog.show());
        timeButton.setOnClickListener(view -> timePickerDialog.show());

        btnBook.setOnClickListener(view -> {
            // Show progress dialog while booking the appointment
            ProgressDialog progressDialog = new ProgressDialog(BookAppointmentActivity.this);
            progressDialog.setMessage("Booking Appointment...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            getUserDataAndSaveAppointment(progressDialog);
        });

        // Get data from Intent
        Intent it = getIntent();
        String title = it.getStringExtra("text1");
        String fullname = it.getStringExtra("text2");
        String address = it.getStringExtra("text3");
        String contact = it.getStringExtra("text4");
        String fees = it.getStringExtra("text5");

        // Set the data in the UI
        tv = findViewById(R.id.textViewAppTitle);
        tv.setText(title);
        ed1.setText(fullname);
        ed2.setText(address);
        ed3.setText(contact);
        ed4.setText("Cons Fees: " + fees + "/-");

        // Disable editing
        ed1.setKeyListener(null);
        ed2.setKeyListener(null);
        ed3.setKeyListener(null);
        ed4.setKeyListener(null);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month += 1; // Months are indexed from 0
            dateButton.setText(day + "/" + month + "/" + year);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() + 86400000); // One day in future
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hourOfDay, minute) -> {
            timeButton.setText(hourOfDay + ":" + minute);
        };

        Calendar cal = Calendar.getInstance();
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);

        int style = AlertDialog.THEME_HOLO_DARK;
        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hrs, mins, true);
    }

    private void getUserDataAndSaveAppointment(ProgressDialog progressDialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();

            // Save appointment to Firebase
            saveAppointmentToFirebase(progressDialog);
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();  // Dismiss the dialog if there's an error
        }
    }

    private void saveAppointmentToFirebase(ProgressDialog progressDialog) {
        String name = ed1.getText().toString().trim();
        String address = ed2.getText().toString().trim();
        String contact = ed3.getText().toString().trim();
        String fees = ed4.getText().toString().trim();
        String date = dateButton.getText().toString().trim();
        String time = timeButton.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || fees.isEmpty() || date.isEmpty() || time.isEmpty() || email == null) {
            Toast.makeText(BookAppointmentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss(); // Dismiss the dialog if validation fails
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(BookAppointmentActivity.this, "User is not signed in", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();


        // Create Appointment Object
        Appointment appointment = new Appointment(email, name, address, contact, fees, date, time);

        // Generate a unique ID and save the data to Firebase
        String appointmentId = mDatabase.child(userId).push().getKey();
        if (appointmentId != null) {
            mDatabase.child(userId).child(appointmentId).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();  // Dismiss the dialog on success
                        Toast.makeText(BookAppointmentActivity.this, "Appointment Booked Successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back to the Family Physicians page
                        Intent intent = new Intent(BookAppointmentActivity.this, FindDoctorActivity.class);
                        startActivity(intent);

                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();  // Dismiss the dialog on failure
                        Toast.makeText(BookAppointmentActivity.this, "Failed to Book Appointment", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Appointment class to map the data
    public static class Appointment {
        public String email, name, address, contact, fees, date, time;

        public Appointment() { }  // Empty constructor for Firebase

        public Appointment(String email, String name, String address, String contact, String fees, String date, String time) {
            this.email = email;
            this.name = name;
            this.address = address;
            this.contact = contact;
            this.fees = fees;
            this.date = date;
            this.time = time;
        }
    }
}
