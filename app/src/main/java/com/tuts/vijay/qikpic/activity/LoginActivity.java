package com.tuts.vijay.qikpic.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;
import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.databinding.ActivityLoginConstraintsBinding;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {
    // UI references.
    private ActivityLoginConstraintsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_constraints);

        // Set up the login form.
        binding.password.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == R.id.edittext_action_login ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    login();
                    return true;
                }
                return false;
        });

        // Set up the submit button click handler
        Button actionButton = (Button) findViewById(R.id.action_button);
        actionButton.setOnClickListener((view) -> {
                login();
        });
    }

    private void login() {
        String username = binding.username.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        // Validate the log in data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder();
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username) + " ");
        }
        if (!validationError && password.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        // Call the Parse login method
        ParseUser.logInInBackground(username, password, (user, e) -> {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
        });
    }
}