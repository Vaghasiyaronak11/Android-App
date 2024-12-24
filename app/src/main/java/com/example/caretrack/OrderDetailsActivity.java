package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {

    private ListView listViewOD;
    private SimpleAdapter sa;
    private ArrayList<HashMap<String, String>> bookingList;
    private ArrayList<HashMap<String, String>> bookingsDataList;
    private ArrayList<HashMap<String, String>> appointmentsDataList;
    private DatabaseReference bookingReference, bookingsReference, appointmentsReference;
    private Button btnBack;
    private Spinner spinnerTables;
    private TextView textViewNoOrders;  // TextView for "No orders available" message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize views
        listViewOD = findViewById(R.id.listViewBM);
        btnBack = findViewById(R.id.buttonBMBack);
        spinnerTables = findViewById(R.id.spinnerTables);
        textViewNoOrders = findViewById(R.id.textViewNoOrders);  // Find the TextView

        // Initialize lists for storing data
        bookingList = new ArrayList<>();
        bookingsDataList = new ArrayList<>();
        appointmentsDataList = new ArrayList<>();

        // Get current user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Firebase references for different data
        bookingReference = FirebaseDatabase.getInstance().getReference("medicines").child(userId);
        bookingsReference = FirebaseDatabase.getInstance().getReference("Bookings").child(userId);
        appointmentsReference = FirebaseDatabase.getInstance().getReference("Appointments").child(userId);

        // Load data from Firebase
        loadBookingData();
        loadBookingsTableData();
        loadAppointmentsData();

        // Setup the spinner and handle its selection
        setupSpinner();

        // By default, display all data
        spinnerTables.setSelection(0);  // "All" is at position 0
        displayAllData();

        // Back button to navigate to HomeActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayAllData() {
        ArrayList<HashMap<String, String>> allDataList = new ArrayList<>();

        // Combine data from all tables
        allDataList.addAll(bookingList);
        allDataList.addAll(bookingsDataList);
        allDataList.addAll(appointmentsDataList);

        // Display the combined data
        displayData(allDataList);
    }

    private void setupSpinner() {
        // Create and set adapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.table_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTables.setAdapter(adapter);

        // Handle spinner item selection
        spinnerTables.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        listViewOD.setAdapter(null);  // Clear the ListView
                        textViewNoOrders.setVisibility(View.GONE);  // Hide "No orders available" message
                        break;
                    case 1:
                        displayAllData();
                        break;
                    case 2:
                        displayData(appointmentsDataList);
                        break;
                    case 3:
                        displayData(bookingsDataList);
                        break;
                    case 4:
                        displayData(bookingList);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadBookingData() {
        bookingReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingList.clear();  // Clear previous data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> bookingItem = new HashMap<>();

                    // Fetch data from Firebase
                    String product = snapshot.child("product").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String contact = snapshot.child("contact").getValue(String.class);
                    String pincode = snapshot.child("pincode").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);

                    bookingItem.put("line1", product);
                    bookingItem.put("line2", "Address : " + address);
                    bookingItem.put("line3", "Pin:" + pincode);
                    bookingItem.put("line4", "Contact: " + contact);
                    bookingItem.put("line5", "" + price);

                    // Add the bookingItem to the list
                    bookingList.add(bookingItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetailsActivity.this, "Failed to load booking data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookingsTableData() {
        bookingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingsDataList.clear();  // Clear previous bookings data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> bookingsItem = new HashMap<>();

                    // Fetch data from Firebase
                    String testName = snapshot.child("packageName").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String pincode = snapshot.child("pincode").getValue(String.class);
                    String contact = snapshot.child("contact").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);

                    // Add fetched data to bookingsItem map
                    bookingsItem.put("line1", testName);
                    bookingsItem.put("line2", "Address : " + address);
                    bookingsItem.put("line3", "Pin:" + pincode);
                    bookingsItem.put("line4", "contact: " + contact);
                    bookingsItem.put("line5", price);

                    // Add the bookingsItem to the list
                    bookingsDataList.add(bookingsItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetailsActivity.this, "Failed to load bookings data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointmentsData() {
        appointmentsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentsDataList.clear();  // Clear previous appointments data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> appointmentsItem = new HashMap<>();

                    // Fetch data from Firebase
                    String address = snapshot.child("address").getValue(String.class);
                    String contact = snapshot.child("contact").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String fees = snapshot.child("fees").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);

                    appointmentsItem.put("line1", address);
                    appointmentsItem.put("line2", "" + contact);
                    appointmentsItem.put("line3", "" + date);
                    appointmentsItem.put("line4", "" + fees);
                    appointmentsItem.put("line5", "" + name);

                    // Add the appointmentsItem to the list
                    appointmentsDataList.add(appointmentsItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderDetailsActivity.this, "Failed to load appointments data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(ArrayList<HashMap<String, String>> dataList) {
        if (dataList.isEmpty()) {
            // If no data is available, show the "No orders available" message
            listViewOD.setAdapter(null);  // Clear the ListView
            textViewNoOrders.setVisibility(View.VISIBLE);  // Show message
        } else {
            // If data is available, hide the "No orders available" message and display the data
            textViewNoOrders.setVisibility(View.GONE);  // Hide message
            sa = new SimpleAdapter(
                    OrderDetailsActivity.this,
                    dataList,
                    R.layout.multi_lines,  // Custom layout for multiple lines
                    new String[]{"line1", "line2", "line3", "line4", "line5"},
                    new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e}
            );
            listViewOD.setAdapter(sa);
        }
    }
}
