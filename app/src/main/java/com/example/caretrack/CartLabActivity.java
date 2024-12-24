package com.example.caretrack;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CartLabActivity extends AppCompatActivity {

    private static final String TAG = "CartLabActivity";
    private HashMap<String, String> item;
    private ArrayList<HashMap<String, String>> list;
    private TextView tvTotal, tvCartEmpty;
    private ListView lst;
    private SimpleAdapter sa;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton, timeButton, btnCheckout,btnBack;
    private DatabaseReference cartReference;
    private float totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_lab);

        // Initialize views
        dateButton = findViewById(R.id.buttonCartDate);
        timeButton = findViewById(R.id.buttonBMCartTime);
        btnCheckout = findViewById(R.id.buttonCartCheckout);
        btnBack = findViewById(R.id.buttonBackLab);
        tvTotal = findViewById(R.id.textViewBMCartTotalCost);
        tvCartEmpty = findViewById(R.id.textViewEmptyCart); // Initialize empty cart message
        lst = findViewById(R.id.listViewBMCart);

        // Initialize Firebase Database reference
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get user ID
        cartReference = FirebaseDatabase.getInstance().getReference("Carts").child(userId);

        // Initialize ArrayList to store package details
        list = new ArrayList<>();

        // Load cart data from Firebase
        loadCartData();

        // Initialize DatePicker and TimePicker
        initDatePicker();
        initTimePicker();

        // Set onClickListeners for buttons to show the dialogs
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(CartLabActivity.this, LabTestActivity.class));
            }
        });


        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.isEmpty()) {
                    // If cart is empty, show a message to add items
                    Toast.makeText(CartLabActivity.this, "Your cart is empty. Please add items before proceeding to checkout.", Toast.LENGTH_LONG).show();
                } else {
                    // Collect package names from the cart list
                    StringBuilder packageNames = new StringBuilder();
                    for (HashMap<String, String> cartItem : list) {
                        String packageName = cartItem.get("line1"); // This is the package name
                        if (packageName != null && !packageName.isEmpty()) {
                            packageNames.append(packageName).append(", ");
                        }
                    }

                    // Remove the last comma and space if needed
                    if (packageNames.length() > 0) {
                        packageNames.setLength(packageNames.length() - 2); // Remove the trailing ", "
                    }

                    // Proceed to checkout
                    Intent it = new Intent(CartLabActivity.this, LabTestBookActivity.class);
                    it.putExtra("price", tvTotal.getText().toString());
                    it.putExtra("date", dateButton.getText().toString());
                    it.putExtra("time", timeButton.getText().toString());
                    it.putExtra("packageName", packageNames.toString()); // Pass the package names

                    startActivity(it);
                }
            }
        });
    }
        // Method to load cart data from Firebase and display in ListView
    private void loadCartData() {
        cartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear(); // Clear previous data
                totalAmount = 0; // Reset total amount

                if (dataSnapshot.getChildrenCount() == 0) {
                    // Show empty cart message and hide ListView
                    tvCartEmpty.setVisibility(View.VISIBLE);
                    lst.setVisibility(View.GONE);
                    tvTotal.setText("Total Cost: ₹0");
                    return; // Exit if no data
                } else {
                    // Hide empty cart message and show ListView
                    tvCartEmpty.setVisibility(View.GONE);
                    lst.setVisibility(View.VISIBLE);
                }

                // Iterate through cart items in Firebase and add them to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PackageData packageData = snapshot.getValue(PackageData.class);
                    if (packageData != null) {
                        item = new HashMap<>();
                        item.put("line1", packageData.getPackageName()); // Package Name
                        item.put("line5", "Total Cost : " + packageData.getTotalCost()); // Package Price
                        list.add(item);

                        // Parse and add the package price to the total amount
                        try {
                            // Remove any non-numeric characters before parsing
                            String totalCostString = packageData.getTotalCost().replaceAll("[^\\d.]", "");
                            totalAmount += Float.parseFloat(totalCostString);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Invalid total cost value: " + packageData.getTotalCost());
                        }
                    }
                }

                // Display total amount
                tvTotal.setText("Total Cost: ₹" + totalAmount);

                // Set up SimpleAdapter to map data to the ListView
                sa = new SimpleAdapter(
                        CartLabActivity.this,
                        list,
                        R.layout.multi_lines, // Layout with TextViews (for name, description, etc.)
                        new String[]{"line1", "line2","line3","line4", "line5"}, // Keys in the HashMap
                        new int[]{R.id.line_a, R.id.line_b,R.id.line_c, R.id.line_d,R.id.line_e} // IDs of TextViews in multi_lines.xml
                );

                lst.setAdapter(sa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    // Initialize the DatePicker
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1; // Months are indexed from 0
                dateButton.setText(dayOfMonth + "/" + month + "/" + year);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);

        // Set minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis()); // Current date in milliseconds
    }

    // Initialize the TimePicker
    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                timeButton.setText(hourOfDay + ":" + String.format("%02d", minute)); // Format time as HH:MM
            }
        };

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        int style = AlertDialog.THEME_HOLO_DARK;
        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hour, minute, false);
    }

    // Inner class to represent package data (name, description, code, price)
    public static class PackageData {
        private String details;
        private String packageName;
        private String totalCost;
        private String userEmail;

        public PackageData() {
            // Default constructor required for Firebase
        }

        public PackageData(String details, String packageName, String totalCost, String userEmail) {
            this.details = details;
            this.packageName = packageName;
            this.totalCost = totalCost;
            this.userEmail = userEmail;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getDetails() {
            return details;
        }

        public String getTotalCost() {
            return totalCost;
        }

        public String getUserEmail() {
            return userEmail;
        }
    }
}
