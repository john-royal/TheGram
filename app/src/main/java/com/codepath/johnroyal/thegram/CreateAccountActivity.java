package com.codepath.johnroyal.thegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class CreateAccountActivity extends AppCompatActivity {

    private static String TAG = "SignInActivity";

    EditText etUsername;
    EditText etPassword;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateAccount();
            }
        });
    }

    private void handleCreateAccount() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        Log.d(TAG, String.format("Creating account with username: %s", username));

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, String.format("Created account successfully with username: %s", user.getUsername()));
                    Intent i = new Intent();
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    Log.e(TAG, String.format("Cannot create account: %s", e.getLocalizedMessage()), e);
                    Toast.makeText(CreateAccountActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}