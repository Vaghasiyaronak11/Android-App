package com.example.caretrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class BuyMedicineDetailsActivity extends AppCompatActivity {
    TextView tvPackageName, tvTotalCost;
    EditText edDetails;
    Button btnAddToCart,btnBack;

    private DatabaseReference databaseCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine_details);

        tvPackageName = findViewById(R.id.textViewBMDPackageName);
        edDetails = findViewById(R.id.editTextBMDMultiLine);
        tvTotalCost = findViewById(R.id.textViewBMDTotalCost);
        btnAddToCart = findViewById(R.id.buttonBMDAddToCart);
        btnBack = findViewById(R.id.buttonBMDBack);

        // Set EditText to be non-editable
        edDetails.setKeyListener(null);

        // Get data from Intent
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("text1");
        String details = intent.getStringExtra("text2");
        String totalCost = intent.getStringExtra("text3");

        tvPackageName.setText(packageName);
        edDetails.setText(details);
        tvTotalCost.setText("Total Cost: " + totalCost + "/-");

        // Initialize Firebase Realtime Database reference
        databaseCart = FirebaseDatabase.getInstance().getReference("medicine_cart");

        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(BuyMedicineDetailsActivity.this, BuyMedicineActivity.class));
            }
        });
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString("Email", ""); // Use user_id or another unique identifier
                String product = tvPackageName.getText().toString();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String userEmail = mAuth.getCurrentUser().getEmail();
                float price;

                try {
                    price = Float.parseFloat(totalCost);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generate a unique ID for the cart item
                String uniqueId = databaseCart.push().getKey();

                // Create a CartItem object
                CartItem cartItem = new CartItem( product, price, "medicines", userEmail,details);

                // Add the product to the cart in Firebase with the unique ID
                databaseCart.child(userId).child(uniqueId).setValue(cartItem).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Record Inserted to Cart", Toast.LENGTH_SHORT).show();
                        // Redirect to the cart page
                        startActivity(new Intent(BuyMedicineDetailsActivity.this, BuyMedicineActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to Add to Cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static class CartItem {
        private String product;
        private float price;
        private String type;
        private String userEmail;
        private String details; // New field to store additional details

        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
        public CartItem() {}





        public CartItem( String product, float price, String type, String userEmail, String details ) {

            this.product = product;
            this.price = price;
            this.type = type;
            this.userEmail = userEmail;
            this.details = details; // Set details in the constructor


        }

        // Getters and setters
        public String getProduct() { return product; }
        public void setProduct(String product) { this.product = product; }
        public float getPrice() { return price; }
        public void setPrice(float price) { this.price = price; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }


    }
}
