package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
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

public class BuyMedicineBookActivity extends AppCompatActivity {
    EditText edName, edAddress, edContact, edPincode;
    Button btnBooking;

    private DatabaseReference medicinesReference;
    private DatabaseReference cartReference; // Reference to the cart

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine_book);

        edName = findViewById(R.id.editTextBMBFullName);
        edAddress = findViewById(R.id.editTextBMBAddress);
        edPincode = findViewById(R.id.editTextBMBPinCode);
        edContact = findViewById(R.id.editTextBMBContactNumber);
        btnBooking = findViewById(R.id.buttonBMBBooking);

        // Initialize Firebase Database references
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get user ID
        medicinesReference = FirebaseDatabase.getInstance().getReference("medicines").child(userId);

        cartReference = FirebaseDatabase.getInstance().getReference("medicine_cart"); // Reference to the cart

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMedicineData();
            }
        });
    }

    private void saveMedicineData() {
        // Retrieve data from input fields
        String name = edName.getText().toString().trim();
        String address = edAddress.getText().toString().trim();
        String pincode = edPincode.getText().toString().trim();
        String contact = edContact.getText().toString().trim();

        // Validate input
        if (name.isEmpty()) {
            edName.setError("Name is required");
            edName.requestFocus();
            return;
        }
        if (!name.matches("[a-zA-Z\\s]+")) {
            edName.setError("Name must only characters");
            edName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edAddress.setError("Address is required");
            edAddress.requestFocus();
            return;
        }

        if (pincode.isEmpty()) {
            edPincode.setError("Pincode is required");
            edPincode.requestFocus();
            return;
        }

        if (contact.isEmpty()) {
            edContact.setError("Contact number is required");
            edContact.requestFocus();
            return;
        }

        if (!contact.matches("\\d{10}")) {
            edContact.setError("Enter a valid 10-digit contact number");
            edContact.requestFocus();
            return;
        }

        if (!pincode.matches("\\d{6}")) {
            edPincode.setError("Enter a valid 6-digit pincode number");
            edPincode.requestFocus();
            return;
        }

        // Collect data from the intent
        Intent intent = getIntent();
        String price = intent.getStringExtra("price");
        String date = intent.getStringExtra("date");
        String product=intent.getStringExtra("product");

        // Create a map for storing medicine details
        Map<String, Object> medicineData = new HashMap<>();
        medicineData.put("name", name);
        medicineData.put("address", address);
        medicineData.put("pincode", pincode);
        medicineData.put("contact", contact);
        medicineData.put("price", price);
        medicineData.put("date", date);
        medicineData.put("product", product);

        // Save data to Firebase under "medicines"
        medicinesReference.push().setValue(medicineData)
                .addOnSuccessListener(aVoid -> {
                    // Clear the cart after successfully saving the order
                    clearCart();

                    // Show success message
                    Toast.makeText(BuyMedicineBookActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                    // Redirect to HomeActivity
                    Intent homeIntent = new Intent(BuyMedicineBookActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish(); // Finish current activity
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(BuyMedicineBookActivity.this, "Order failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearCart() {
        cartReference.removeValue().addOnSuccessListener(aVoid -> {
            // Optionally show a message that the cart was cleared
            Toast.makeText(BuyMedicineBookActivity.this, "Cart has been cleared.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle failure
            Toast.makeText(BuyMedicineBookActivity.this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
