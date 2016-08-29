package com.example.mich.usersanddata;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
 * A login screen that offers login via itemTextField/password.
 */
public class LoginActivity extends AppCompatActivity  {



    // UI references.
    private AutoCompleteTextView mEmailView;
    private static final String TAG = "EmailPassword";
    private EditText mPasswordView;
    boolean valid = true;

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
                if (isOnline()){
                    username = mEmailView.getText().toString();
                    password = mPasswordView.getText().toString();
                    signIn(username,password);
                } else {
                        String signIn = "Signing in";
                    alertTheUser(signIn);
                }

            }
        });
        assert mEmailSignUpButton != null;
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOnline()){
                    username = mEmailView.getText().toString();
                    password = mPasswordView.getText().toString();

                    createAccount(username,password);
                } else {
                    String createNew = "Creating a new account";
                    alertTheUser(createNew);
                }

            }
        });



        mEmailView.setText(username);
        mPasswordView.setText(password);

    }

    private boolean validateForm() {
    valid = true;
        // for a username to be valid it must meet the following criteria
        String email = mEmailView.getText().toString();
        email.toLowerCase();

        // the text feild must not be empty
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("Required.");
            valid = false;
        } else {
            mEmailView.setError(null);
        }
        // the must contain an @ symbol
        if (email.contains("@")) {
            mEmailView.setError(null);

        } else {
            mEmailView.setError("Email address requires an @ symbol to be valid");
            valid = false;
        }

        // has to end in a .com, .org, or .edu
        if (email.endsWith(".com" )){
            if (email.endsWith(".org" )){
                if (email.endsWith(".edu" )){
                    mEmailView.setError(null);
                }

            }


        } else {
            mEmailView.setError("Not a valid domain name");
            valid= false;
        }


        // for a password to be valid it must meet the following criteria

        String password = mPasswordView.getText().toString();
        // the text feild must not be empty
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("Required.");
            valid = false;
        } else {
            mPasswordView.setError(null);
        }
        // the must contain an special charecter

//            mPasswordView.setError(null);
//        } else {
//            mPasswordView.setError("Password must contain at least one capitol letter");
//            valid = false;
//        }

        // the must contain a capitol letter
        if (password.matches(".*[A-Z].*")) {
            if (password.matches(".*[\\\\~\\\\!\\\\@\\\\#\\\\$\\\\%\\\\^\\\\&\\\\*\\\\(\\\\)\\\\_\\\\+].*")) {
                mPasswordView.setError(null);
            } else {
                mPasswordView.setError("Password must contain at least one capitol letter and a special charecter");
                valid = false;
            }
        }else {
            mPasswordView.setError("Password must contain at least one capitol letter and a special charecter");
            valid = false;
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
//                            Intent loginIntent =  new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(loginIntent);
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
//                            Intent loginIntent =  new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(loginIntent);
                            finish();

                            editor.commit();
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            String e = String.valueOf(task.getException());
                            e = e.substring(e.indexOf(":")+ ":".length());

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Trouble logging in")
                                    .setMessage(e)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })



                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();


                        }




                    }
                });

    }

    protected boolean isOnline() {

        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;

        } else{

            return false;
        }
    }

    public void alertTheUser(String messageType){
        new AlertDialog.Builder(this)
                .setTitle("No Firebase connection")
                .setMessage(messageType + " requires an active internet connection to complete")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })



                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

