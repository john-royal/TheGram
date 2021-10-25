package com.codepath.johnroyal.thegram.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.johnroyal.thegram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignInActivity extends AppCompatActivity {

    public static String TAG = "SignInActivity";
    public final static int CREATE_ACCOUNT_REQUEST_CODE = 100;

    EditText etUsername;
    EditText etPassword;
    Button btnSignIn;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (ParseUser.getCurrentUser() != null) {
            launchMainActivity();
            return;
        }

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnCreateAccount = findViewById(R.id.btn_create_account);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignIn();
            }
        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCreateAccount();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
            assert(ParseUser.getCurrentUser() != null);
            launchMainActivity();
        }
    }

    private void handleSignIn() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        Log.d(TAG, String.format("Signing in with username: %s", username));

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.i(TAG, String.format("Signed in successfully with username: %s", user.getUsername()));
                    launchMainActivity();
                } else {
                    Log.e(TAG, String.format("Cannot sign in: %s", e.getLocalizedMessage()), e);
                    Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void launchCreateAccount() {
        Intent i = new Intent(SignInActivity.this, CreateAccountActivity.class);
        startActivityForResult(i, CREATE_ACCOUNT_REQUEST_CODE);
    }

    private void launchMainActivity() {
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}