package com.example.caretrack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CartBuyMedicineActivity extends AppCompatActivity {
    private static final String TAG = "CartBuyMedicineActivity";

    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;
    private TextView tvTotal, tvEmptyCart; // Added TextView for empty cart message
    private ListView lst;
    private DatePickerDialog datePickerDialog;
    private Button dateButton, btnCheckout,btnBack;
    private DatabaseReference cartReference;
    private float totalAmount = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_buy_medicine);

        // Initialize views
        dateButton = findViewById(R.id.buttonBMCartTime);
        btnCheckout = findViewById(R.id.buttonCartCheckout);
        btnBack = findViewById(R.id.buttonBack);
        tvTotal = findViewById(R.id.textViewBMCartTotalCost);
        lst = findViewById(R.id.listViewBMCart);
        tvEmptyCart = findViewById(R.id.textViewEmptyCart); // Initialize empty cart message

        list = new ArrayList<>();

        // Initialize Firebase reference to medicine_cart
        cartReference = FirebaseDatabase.getInstance().getReference("medicine_cart");

        loadCartData(); // Load cart data

        initDatePicker();
        dateButton.setOnClickListener(view -> datePickerDialog.show());


        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(CartBuyMedicineActivity.this, BuyMedicineActivity.class));
            }
        });

        btnCheckout.setOnClickListener(view -> {
            // Ensure data is loaded before checking cart status
            cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        // If cart is empty, show a message
                        Toast.makeText(CartBuyMedicineActivity.this, "Your cart is empty. Please add items before proceeding to checkout.", Toast.LENGTH_LONG).show();
                    } else {
                        // Proceed to checkout
                        String selectedDate = dateButton.getText().toString(); // Get the selected date
                        Intent intent = new Intent(CartBuyMedicineActivity.this, BuyMedicineBookActivity.class);
                        intent.putExtra("price", tvTotal.getText().toString()); // Pass total cost to the next activity
                        intent.putExtra("date", selectedDate); // Pass the selected date to the next activity

                        // Assuming you want to pass product details from cart as well
                        // For simplicity, let's pass the first product in the cart
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CartItem packageData = snapshot.getValue(CartItem.class);
                            if (packageData != null) {
                                intent.putExtra("product", packageData.getProduct()); // Pass product to the next activity
                                break; // Pass only the first product; remove if you want to pass all
                            }
                        }

                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "DatabaseError: " + databaseError.getMessage());
                }
            });
        });
    }

        private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            month = month + 1; // Months are indexed from 0
            dateButton.setText(dayOfMonth + "/" + month + "/" + year);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

        // Set minimum date to the next day
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() + 86400000); // One day in milliseconds
    }

    private void clearCart() {
        // Clear the cart items for the current user after order placement
        cartReference.removeValue().addOnSuccessListener(aVoid -> {
            // Optionally show a message that the cart was cleared
            Toast.makeText(CartBuyMedicineActivity.this, "Cart has been cleared.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Handle failure in clearing the cart
            Toast.makeText(CartBuyMedicineActivity.this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Load medicine cart data from Firebase
    private void loadCartData() {
        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // Clear previous data
                totalAmount = 0; // Reset total amount

                if (dataSnapshot.getChildrenCount() == 0) {
                    // Show empty cart message
                    tvEmptyCart.setVisibility(View.VISIBLE);
                    lst.setVisibility(View.GONE);
                    tvTotal.setText("Total Cost: ₹0");
                    if (sa != null) {
                        sa.notifyDataSetChanged(); // Notify adapter about data change
                    }
                    return; // Exit if no data
                } else {
                    // Hide empty cart message
                    tvEmptyCart.setVisibility(View.GONE);
                    lst.setVisibility(View.VISIBLE);
                }

                // Iterate through cart items in Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem packageData = snapshot.getValue(CartItem.class);

                    if (packageData != null) {
                        item = new HashMap<>();
                        item.put("line1", packageData.getProduct());
                        item.put("line5", "Total Cost: ₹" + packageData.getPrice()); // Product price
                        list.add(item);

                        // Add the package price to the total amount
                        totalAmount += packageData.getPrice();
                    }
                }

                // Display total amount
                tvTotal.setText("Total Cost : ₹" + totalAmount);

                // Set up SimpleAdapter to map data to the ListView
                sa = new SimpleAdapter(
                        CartBuyMedicineActivity.this,
                        list,
                        R.layout.multi_lines, // Layout with TextViews (for name, description, etc.)
                        new String[]{"line1", "line2", "line3", "line4", "line5"}, // Keys in the HashMap
                        new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e} // IDs of TextViews in multi_lines.xml
                );

                lst.setAdapter(sa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    // Inner class to represent cart item data (name, price)
    public static class CartItem {
        private String product;
        private int price;

        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
        public CartItem() {
        }

        // Parameterized constructor
        public CartItem(String product, int price) {
            this.product = product;
            this.price = price;
        }

        // Getters and setters
        public String getProduct() {
            return product;
        }

        public int getPrice() {
            return price;
        }
    }
}
