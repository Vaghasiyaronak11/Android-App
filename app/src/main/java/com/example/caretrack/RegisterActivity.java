package com.example.caretrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextEmail, editTextPassword, editmobile;
    private Button buttonRegister;
    private TextView textViewAlreadyHaveAccount;
    private ImageView eyeButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userDetailsRef;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        userDetailsRef = FirebaseDatabase.getInstance().getReference("user_details");

        // Initialize UI components
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editmobile = findViewById(R.id.editmobile);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewAlreadyHaveAccount = findViewById(R.id.textViewAlreadyHaveAccount);
        eyeButton = findViewById(R.id.eyeButton);

        // Set onClickListener for the register button
        buttonRegister.setOnClickListener(v -> registerUser());

        // Set onClickListener for the "Already have an account?" link
        textViewAlreadyHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        // Set onClickListener for eye button
        eyeButton.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void registerUser() {
        // Get data from input fields
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String mobile = editmobile.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!username.matches("[a-zA-Z ]+")) {
            Toast.makeText(this, "name should contain only letters and spaces", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobile.length() != 10 || !mobile.matches("[0-9]+")) {
            Toast.makeText(this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register the user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            saveUserDetails(user.getUid(), username, email, password, mobile);
                                            Toast.makeText(RegisterActivity.this, "Please check your email to verify your account.", Toast.LENGTH_LONG).show();
                                            clearInputFields();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDetails(String userId, String username, String email, String password, String mobile) {
        String encodedEmail = encodeEmail(email);
        DatabaseReference userRef = userDetailsRef.child(encodedEmail);

        UserDetails userDetails = new UserDetails(username, email, password, mobile);
        userRef.setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User details saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Failed to save user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String encodeEmail(String email) {
        return email.replace(".", ",");
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your eye off icon
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your eye on icon
        }
        editTextPassword.setSelection(editTextPassword.length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void clearInputFields() {
        editTextUsername.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editmobile.setText("");
    }

    public static class UserDetails {
        private String username;
        private String userEmail;
        private String password;
        private String mobileNo;

        public UserDetails() {
            // Default constructor required for Firebase
        }

        public UserDetails(String username, String userEmail, String password, String mobileNo) {
            this.username = username;
            this.userEmail = userEmail;
            this.password = password;
            this.mobileNo = mobileNo;
        }

        // Getters and setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }
    }
}
