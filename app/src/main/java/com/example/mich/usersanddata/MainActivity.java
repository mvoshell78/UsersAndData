package com.example.mich.usersanddata;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements getListPosition {




    private FirebaseAuth.AuthStateListener mAuthListener;
    private ValueEventListener valueEventListener;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ArrayList<GroceryItem> groceryList;
    FirebaseUser user;
    private EditText mEmailField;
    private EditText mPasswordField;
    TextView email;
    TextView passwordt;
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
        email = (TextView) findViewById(R.id.email);
        passwordt = (TextView) findViewById(R.id.pssword);

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

                 dataChangeListener();

                } else {

                    Intent loginIntent =  new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();



                }
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              submitPost();

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
        dataChangeListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        dataChangeListener();
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

        if(title != ""){
            if (body != ""){
                final int intBoby =  Integer.parseInt( mPasswordField.getText().toString());
                GroceryItem groceryItem = new GroceryItem(title,intBoby);

                groceryList.add(groceryItem);

                mDatabase.child(uuid).setValue(groceryList);

                dataChangeListener();
                mEmailField.setText("");
                mPasswordField.setText("");
            }

        }

//            mDatabase.child(uuid).child("qty").setValue(intBoby);
   }

   public void dataChangeListener() {

       // uuid = sharedPreferences.getString("uuid","");
       if (uuid != null) {


            valueEventListener  = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                   ArrayList<GroceryItem> grocery = new ArrayList<GroceryItem>();
                   groceryList.clear();
                   grocery.clear();

                   grocery = (ArrayList<GroceryItem>) dataSnapshot.getValue();
                        if (grocery != null){
                            for (int i = 0; i < grocery.size(); i++) {

                                Map<String, Object> x = new HashMap<>();
                                x = (Map<String, Object>) grocery.get(i);
                                Long lon = (Long) x.get("mQty");
                                int qty = (int) (long) lon;
                                String groceryListItem = (String) x.get("mGroceryItem");
                                GroceryItem groceryItem = new GroceryItem(groceryListItem, qty);

                                groceryList.add(groceryItem);

                            }
                        }

                   createFrag(groceryList);

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                   // Getting Post failed, log a message

                   // ...
               }
           };
           mDatabase.child(uuid).addValueEventListener(valueEventListener);

//             mDatabase.child(uuid).addListenerForSingleValueEvent (valueEventListener = new ValueEventListener() {
//                  @Override
//                  public void onDataChange(DataSnapshot dataSnapshot) {
//                      // Get user value
//
//                      ArrayList<GroceryItem> grocery = new ArrayList<GroceryItem>();
//                      groceryList.clear();
//
//                      grocery = (ArrayList<GroceryItem>) dataSnapshot.getValue();
//
//                      for(int i = 0; i< grocery.size(); i++){
//
//                          Map<String, Object> x = new HashMap<>();
//                          x = (Map<String, Object>) grocery.get(i);
//                          Long lon = (Long) x.get("mQty");
//                          int qty = (int) (long) lon;
//                          String groceryListItem = (String) x.get("mGroceryItem");
//                          GroceryItem groceryItem = new GroceryItem(groceryListItem,qty);
//
//                          groceryList.add(groceryItem);
//
//
//                      }
//
//                      //mDatabase.removeEventListener(this);
//                      createFrag();
//                  }
//
//                  @Override
//                  public void onCancelled(DatabaseError databaseError) {
////                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                  }
//              });


       }


   }

    public void createFrag(ArrayList<GroceryItem> grocery){


        getFragmentManager().beginTransaction().replace(R.id.list_container,new ListFragment()).commit();

        ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);

            fragment = ListFragment.newInstanceOf(grocery);
            getFragmentManager().beginTransaction().replace(R.id.list_container,fragment,ListFragment.TAG).commit();


            //fragment.setUpList(grocery);

    }

    @Override
    public void passBackPosition(final int position) {


        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        groceryList.remove(position);
                        mDatabase.child(uuid).setValue(groceryList);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

//        ListFragment fragment = (ListFragment) getFragmentManager().findFragmentByTag(ListFragment.TAG);
//        fragment.setUpList(groceryList);
    }
}
