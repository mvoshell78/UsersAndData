package com.example.mich.usersanddata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {



    // UI references.
    private AutoCompleteTextView mEmailView;
    private static final String TAG = "EmailPassword";
    private EditText mPasswordView;

    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);


        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        assert mEmailSignInButton != null;
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                username = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                signIn(username,password);
            }
        });
        assert mEmailSignUpButton != null;
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();

                createAccount(username,password);
            }
        });



        mEmailView.setText(username);
        mPasswordView.setText(password);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailView.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Required.");
            valid = false;
        } else {
            mEmailView.setError(null);
        }

        String password = mPasswordView.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Required.");
            valid = false;
        } else {
            mPasswordView.setError(null);
        }

        return valid;
    }

    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            Toast.makeText(LoginActivity.this,"Registration Complete",
                                    Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", email);
                            editor.putString("password", password);
                            editor.commit();
                            Intent loginIntent =  new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish();

                        }


                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Registration failed",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    private void signIn(final String email, final String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            Toast.makeText(LoginActivity.this, "Sign in successful",
                                    Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", email);
                            editor.putString("password", password);
                            Intent loginIntent =  new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish();

                            editor.commit();
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Sign in failed",
                                    Toast.LENGTH_SHORT).show();
                        }




                    }
                });

    }
}

