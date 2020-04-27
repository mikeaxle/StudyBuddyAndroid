package com.tecknologick.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    // declare ui elements
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextView registerTextButton;
    Intent intent;

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    // declare strings to hold email + password
    String email;
    String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // get edit texts
        editTextEmail =findViewById(R.id.loginEmailTextEdit);
        editTextPassword = findViewById(R.id.loginPasswordTextEdit);

        // get text inputs
        textInputEmail = findViewById(R.id.loginEmailTextInputLayout);
        textInputPassword = findViewById(R.id.loginPasswordTextInputLayout);

        // get signin button
        buttonSignIn = findViewById(R.id.loginButton);

        // add click listener
        buttonSignIn.setOnClickListener(click -> {
            loginUser();
        });

        // get register text view
        registerTextButton = findViewById(R.id.loginRegisterTextView);

        // set click listener
        registerTextButton.setOnClickListener(click -> {
            // create intent for register activity
            intent = new Intent(this, RegisterActivity.class);

            // start activity
            startActivity(intent);
        });

    }

    private void loginUser() {
        // get email
        email = editTextEmail.getText().toString().trim();

        // get password
        password = editTextPassword.getText().toString().trim();

        // check validity
        if (isEmailValid(email) & isPasswordValid(password)) {
            // sign in with firebase
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // if sign in successful
                        if (task.isSuccessful()) {
                            // show notification
                            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_LONG).show();

                            // create intent to module activity
                            intent = new Intent(this, ModuleActivity.class);

                            // start activity
                            startActivity(intent);

                            // finish Login activity
                            finish();
                        } else {
                            // show notification
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    /**
     * validates password edit text
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        if (TextUtils.isEmpty(password)) {
            // password is empty
            textInputPassword.setError("Please enter password");

            // return false
            return false;
        } else {
            // set error to null
            textInputPassword.setError(null);

            // return true
            return true;
        }
    }

    /**
     * validates email edit text
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {

            textInputEmail.setError("Please enter email");

            return false;

        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            textInputEmail.setError("Please enter a valid email address");

            return  false;

        } else {

            textInputEmail.setError(null);

            return true;
        }
    }
}
