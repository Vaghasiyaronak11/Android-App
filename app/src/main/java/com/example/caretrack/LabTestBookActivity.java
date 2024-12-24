package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LabTestBookActivity extends AppCompatActivity {
    EditText edName, edAddress, edContact, edPincode;
    Button btnBooking;
    private DatabaseReference bookingReference;
    private DatabaseReference cartReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_book);

        edName = findViewById(R.id.editTextLTBFullName);
        edAddress = findViewById(R.id.editTextLTBAddress);
        edPincode = findViewById(R.id.editTextLTBPinCode);
        edContact = findViewById(R.id.editTextLTBContactNumber);
        btnBooking = findViewById(R.id.buttonLTBBooking);

        // Initialize Firebase Database references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get user ID
        bookingReference = FirebaseDatabase.getInstance().getReference("Bookings").child(userId);
        cartReference = FirebaseDatabase.getInstance().getReference("Carts").child(userId); // Replace with your cart path

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookAppointment();
            }
        });
    }

    private void bookAppointment() {
        // Retrieve data from input fields
        String name = edName.getText().toString().trim();
        String address = edAddress.getText().toString().trim();
        String pincode = edPincode.getText().toString().trim();
        String contact = edContact.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            edName.setError("Name is required");
            edName.requestFocus();
            return;
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            edName.setError("Name must only contain characters");
            edName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            edAddress.setError("Address is required");
            edAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pincode)) {
            edPincode.setError("Pincode is required");
            edPincode.requestFocus();
            return;
        }

        if (!pincode.matches("\\d{6}")) {
            edPincode.setError("Enter a valid 6-digit pincode");
            edPincode.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            edContact.setError("Contact number is required");
            edContact.requestFocus();
            return;
        }

        if (!contact.matches("\\d{10}")) {
            edContact.setError("Enter a valid 10-digit contact number");
            edContact.requestFocus();
            return;
        }

        // Collect data from the intent
        Intent intent = getIntent();
        String price = intent.getStringExtra("price");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String packageName = intent.getStringExtra("packageName"); // Retrieve package name

        // Create a map for storing booking details
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("name", name);
        bookingData.put("address", address);
        bookingData.put("pincode", pincode);
        bookingData.put("contact", contact);
        bookingData.put("price", price);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("packageName", packageName); // Add package name to booking data

        // Save data to Firebase
        bookingReference.push().setValue(bookingData)
                .addOnSuccessListener(aVoid -> {
                    // Clear cart after successful booking
                    clearCart();

                    // Show success message
                    Toast.makeText(LabTestBookActivity.this, "Appointment booked successfully!", Toast.LENGTH_LONG).show();

                    // Redirect to HomeActivity
                    Intent homeIntent = new Intent(LabTestBookActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish(); // Finish current activity
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(LabTestBookActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearCart() {
        cartReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Cart cleared successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(LabTestBookActivity.this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
