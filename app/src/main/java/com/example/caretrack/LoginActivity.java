package com.example.caretrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsernameOrEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private TextView tvRegister;
    private SharedPreferences sharedPreferences;
    private ImageView eyeButton;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);

        String loggedIn = sharedPreferences.getString("loggedIn", "false");
        if (loggedIn.equals("true")) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // Initialize UI components
        editTextUsernameOrEmail = findViewById(R.id.editTextLoginUsername);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        tvRegister = findViewById(R.id.textView3);
        eyeButton = findViewById(R.id.eyeButton); // Add eye button

        // Set onClickListener for login button
        buttonLogin.setOnClickListener(v -> loginUser());

        // Set onClickListener for register TextView
        tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Set onClickListener for eye button
        eyeButton.setOnClickListener(v -> togglePasswordVisibility());
    }

//    private void togglePasswordVisibility() {
//        if (isPasswordVisible) {
//            // Set password as hidden
//            editTextPassword.setInputType(129); // InputType.TYPE_TEXT_VARIATION_PASSWORD
//            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your eye icon
//        } else {
//            // Set password as visible
//            editTextPassword.setInputType(144); // InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your eye icon
//        }
//        isPasswordVisible = !isPasswordVisible;
//        editTextPassword.setSelection(editTextPassword.length()); // Move cursor to the end
//    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Set password as hidden
            editTextPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your hidden eye icon
        } else {
            // Set password as visible
            editTextPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eyeButton.setImageResource(R.drawable.baseline_remove_eye_24); // Replace with your visible eye icon
        }
        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setSelection(editTextPassword.length()); // Move cursor to the end
    }


    private void loginUser() {
        String usernameOrEmail = editTextUsernameOrEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(usernameOrEmail)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
            // If the input is an email, log in with email
            loginWithEmail(usernameOrEmail, password);
        } else {
            // Otherwise, treat the input as a username
            loginWithUsername(usernameOrEmail, password);
        }
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loggedIn", "true");
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Please verify your email address", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginWithUsername(String username, String password) {
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String email = userSnapshot.child("email").getValue(String.class);
                        if (email != null) {
                            loginWithEmail(email, password);
                            return;
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
