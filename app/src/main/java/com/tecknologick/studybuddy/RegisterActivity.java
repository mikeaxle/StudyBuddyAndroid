package com.tecknologick.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    final String TAG = "Register";
    // declare ui elements
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private TextView loginTextButton;
    Intent intent;

    // firebase authentication
    private FirebaseAuth firebaseAuth;

    // firestore database
    // FireBase database instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    // declare strings to hold email + password
    String email;
    String password;
    String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // get edit texts
        editTextEmail =findViewById(R.id.registerEmailTextEdit);
        editTextPassword = findViewById(R.id.registerPasswordTextEdit);
        editTextConfirmPassword = findViewById(R.id.registerConfirmPasswordTextEdit);

        // get text inputs
        textInputEmail = findViewById(R.id.registerEmailTextInputLayout);
        textInputPassword = findViewById(R.id.registerPasswordTextInputLayout);
        textInputConfirmPassword = findViewById(R.id.registerConfirmPasswordTextInputLayout);

        // get register button
        buttonRegister = findViewById(R.id.registerButton);

        // add click listener
        buttonRegister.setOnClickListener(click -> {
            registerUser();
        });

        // get register text view
        loginTextButton = findViewById(R.id.registerLoginTextView);

        // set click listener
        loginTextButton.setOnClickListener(click -> {
            // create intent for login activity
            intent = new Intent(this, LoginActivity.class);

            // start activity
            startActivity(intent);
        });
    }

    /**
     * registers new user
     */
    private void registerUser() {
        // get email
        email = editTextEmail.getText().toString().trim();

        // get password
        password = editTextPassword.getText().toString().trim();

        // get confirm password
        confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // check validity
        if(isEmailValid(email) & isPasswordValid(password) & isConfirmPasswordValid(confirmPassword, password)) {
            // create user with firebase
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // check if operation was successful
                        if (task.isSuccessful()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("uid", task.getResult().getUser().getUid());
                            user.put("email", email);
                            user.put("userType", "TUTEE");

                            Toast.makeText(this, "Account has been registered for: " + email, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "new account registered.");

                            db.collection("Users").document(user.get("uid").toString())
                                    .set(user)
                                    .addOnCompleteListener(createUserTask -> {
                                        if (createUserTask.isSuccessful()) {
                                            Log.d(TAG, "user created.");
                                            intent = new Intent(this, ModuleActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(this, "Creating user error: " + createUserTask.getException(), Toast.LENGTH_SHORT).show();
                                            Log.e(TAG,"Creating user error: " + createUserTask.getException());
                                        }
                                    });
                            // create user profile
                        } else {
                            Toast.makeText(this, "Account registration error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG,"Account registration error: " + task.getException());
                        }
                    });
        }
    }

    /**
     * validates password edit text
     * @param password
     * @return boolean
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
     * validates password edit text
     * @param confirmPassword
     * @param password
     * @return boolean
     */
    private boolean isConfirmPasswordValid(String confirmPassword, String password) {
        if (TextUtils.isEmpty(confirmPassword)) {
            // password is empty
            textInputConfirmPassword.setError("Please confirm password");

            // return false
            return false;
        } else if (!password.equals(confirmPassword)) { // check if password == confirm password
            // password does not match confirm password
            textInputConfirmPassword.setError("Password does not match confirmation");

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
     * @return boolean
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
