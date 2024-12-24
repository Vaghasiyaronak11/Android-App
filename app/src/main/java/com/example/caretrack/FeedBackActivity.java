package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedBackActivity extends AppCompatActivity {

    private EditText edName, edEmail, edFeedback, edContact, edRating;
    private Button btnSubmit, btnBack;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        // Initialize Views
        edName = findViewById(R.id.ed_name);
        edEmail = findViewById(R.id.ed_email);
        edFeedback = findViewById(R.id.ed_feedback);
        edContact = findViewById(R.id.ed_contact);
        edRating = findViewById(R.id.ed_rating);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);

        // Set OnClickListener for "Back" button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to HomeActivity
                startActivity(new Intent(FeedBackActivity.this, HomeActivity.class));
            }
        });

        // Submit Button Click Listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        // Get user input
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String feedback = edFeedback.getText().toString().trim();
        String contact = edContact.getText().toString().trim();
        String rating = edRating.getText().toString().trim(); // Get the rating input

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            edName.setError("Name is required");
            edName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Email is required");
            edEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Invalid email format");
            edEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contact)) {
            edContact.setError("Contact number is required");
            edContact.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(feedback)) {
            edFeedback.setError("Feedback is required");
            edFeedback.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(rating)) { // Check if rating is provided
            edRating.setError("Rating is required");
            edRating.requestFocus();
            return;
        }

        // Create unique key for each feedback
        String feedbackId = databaseReference.push().getKey();

        // Create Feedback object with rating
        Feedback feedbackData = new Feedback(feedbackId, name, email, contact, feedback, rating);

        // Store in Firebase
        databaseReference.child(feedbackId).setValue(feedbackData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(FeedBackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally, clear the fields
                    edName.setText("");
                    edEmail.setText("");
                    edFeedback.setText("");
                    edContact.setText("");
                    edRating.setText("");
                } else {
                    Toast.makeText(FeedBackActivity.this, "Failed to submit feedback: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Feedback class to map feedback data
    public static class Feedback {
        public String feedbackId;
        public String name;
        public String email;
        public String contact;
        public String feedback;
        public String rating; // Add rating field

        // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
        public Feedback() {
        }

        public Feedback(String feedbackId, String name, String email, String contact, String feedback, String rating) {
            this.feedbackId = feedbackId;
            this.name = name;
            this.email = email;
            this.contact = contact;
            this.feedback = feedback;
            this.rating = rating; // Set rating
        }
    }
}
