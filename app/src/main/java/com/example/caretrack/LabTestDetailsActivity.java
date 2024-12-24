package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LabTestDetailsActivity extends AppCompatActivity {

    TextView tvPackageName, tvTotalCost;
    EditText edDetails;
    Button btnAddToCart,btnBack;

    private DatabaseReference mCartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_details);

        // Initialize UI elements
        tvPackageName = findViewById(R.id.textViewLDPackageName);
        tvTotalCost = findViewById(R.id.textViewBMDTotalCost);
        edDetails = findViewById(R.id.editTextLDTextMultiline);
        btnAddToCart = findViewById(R.id.buttonLDAddToCart);
        btnBack = findViewById(R.id.buttonLDBack);

        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(LabTestDetailsActivity.this, LabTestActivity.class));
            }
        });



        // Initialize Firebase Realtime Database reference
        mCartDatabase = FirebaseDatabase.getInstance().getReference("Carts");

        edDetails.setKeyListener(null); // Make the EditText read-only

        // Retrieve data from the intent
        Intent intent = getIntent();
        tvPackageName.setText(intent.getStringExtra("text1"));
        edDetails.setText(intent.getStringExtra("text2"));
        tvTotalCost.setText(intent.getStringExtra("text3")+"/-");

        // Set onClick listener for the Add to Cart button
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
    }

    private void addToCart() {
        // Get the current user from Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail(); // Retrieve user email

        // Retrieve data from the UI
        String packageName = tvPackageName.getText().toString().trim();
        String details = edDetails.getText().toString().trim();
        String totalCost = tvTotalCost.getText().toString().trim();

        // Create a cart item object
        CartItem cartItem = new CartItem(packageName, details, totalCost, userEmail);

        // Generate a unique ID for the cart item
        String cartId = mCartDatabase.child(userId).push().getKey();
        if (cartId != null) {
            mCartDatabase.child(userId).child(cartId).setValue(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        // Show success message
                        Toast.makeText(LabTestDetailsActivity.this, "Added to Cart Successfully", Toast.LENGTH_SHORT).show();

                        // Redirect to LabTestActivity
                        startActivity(new Intent(LabTestDetailsActivity.this, LabTestActivity.class));
                    })
                    .addOnFailureListener(e -> {
                        // Show error message
                        Toast.makeText(LabTestDetailsActivity.this, "Failed to Add to Cart", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(LabTestDetailsActivity.this, "Failed to generate cart ID", Toast.LENGTH_SHORT).show();
        }
    }

    // Static inner class for CartItem
    public static class CartItem {
        public String packageName;
        public String details;
        public String totalCost;
        public String userEmail;

        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
        public CartItem() { }

        public CartItem(String packageName, String details, String totalCost, String userEmail) {
            this.packageName = packageName;
            this.details = details;
            this.totalCost = totalCost;
            this.userEmail = userEmail;

        }
    }
}
