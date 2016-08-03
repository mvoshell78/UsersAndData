package com.example.mich.usersanddata;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {




    private FirebaseAuth.AuthStateListener mAuthListener;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<GroceryItem> groceryList;
    FirebaseUser user;
    private EditText mEmailField;
    private EditText mPasswordField;
    private static final String REQUIRED = "Required";
    SharedPreferences sharedPreferences;
    String username;
    String password;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groceryList = new ArrayList<GroceryItem>();

        mEmailField = (EditText) findViewById(R.id.emailTextField);
        mPasswordField = (EditText) findViewById(R.id.passwordTextField);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    uuid =  user.getUid();
                } else {

                    Intent loginIntent =  new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);



                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }
        if (id == R.id.action_add) {
            submitPost();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }





    private void signOut() {
        username = "";
        password = "";

        mAuth.signOut();

    }
    private void submitPost() {
        final String title = mEmailField.getText().toString();
        final String body =  mPasswordField.getText().toString();
              final int intBoby =  Integer.parseInt( mPasswordField.getText().toString());

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mEmailField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mPasswordField.setError(REQUIRED);
            return;
        }
            GroceryItem groceryItem = new GroceryItem(title,intBoby);

            groceryList.add(groceryItem);

            mDatabase.child(uuid).setValue(groceryList);
//            mDatabase.child(uuid).child("qty").setValue(intBoby);
    }


}
