package com.tuts.vijay.qikpic.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.tuts.vijay.qikpic.R;

/**
 * Activity which displays a login screen to the user.
 */
public class SignUpActivity extends Activity {
    private static final int REQUEST_CODE_EMAIL = 1;
    private static final String TAG = SignUpActivity.class.getSimpleName();;
    // UI references.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        // Set up the signup form.
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);

        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });
        populateEmailIfPossible();
    }

    private void populateEmailIfPossible() {
        try {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_EMAIL);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "Get account did not work. Move on");
        }
    }

    private void signup() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder();
        if (username.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_username) + " ");
        } else if (!validationError && !username.contains("@")) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_valid_email) + " ");
        }
        if (!validationError && password.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password) + " ");
        }
        if (!validationError && !password.equals(passwordAgain)) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_passwords) + " ");
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            usernameEditText.setText(accountName);
        }
    }
}