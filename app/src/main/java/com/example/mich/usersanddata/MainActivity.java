package com.example.mich.usersanddata;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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
    private EditText groceryItem;
    private EditText qtyField;
    TextView itemTextField;
    TextView qtyTextField;
    private static final String REQUIRED = "Required";
    SharedPreferences sharedPreferences;
    String username;
    String password;
    String uuid;
     int intBoby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groceryList = new ArrayList<GroceryItem>();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        groceryItem = (EditText) findViewById(R.id.itemFeild);
        qtyField = (EditText) findViewById(R.id.qtyFeild);
        itemTextField = (TextView) findViewById(R.id.email);
        qtyTextField = (TextView) findViewById(R.id.pssword);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString("username","");
        password = sharedPreferences.getString("password","");
        uuid = sharedPreferences.getString("uuid","");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.keepSynced(true);


        if(!isOnline()){
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("No Firebase connection")
                .setMessage("A connection to Firebase could not be established. You can continue to use the app and when one is establisehd your data will be synced automatically.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        }


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    uuid =  user.getUid();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("uuid", uuid);
                    editor.commit();

                 dataChangeListener();


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

        if (isOnline()){
             mAuth.signOut();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Firebase connection")
                    .setMessage("Loging out with out an internet connection will prevent you from accessing your data. You will require an active connection to back login.")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                        }
                    })


                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                         // do nothing

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }



    }
    private void submitPost() {
        final String itemName = groceryItem.getText().toString();
        final String itemQty =  qtyField.getText().toString();

        if (!isOnline()){
            Toast.makeText(this,"Data will sync when connection is established",Toast.LENGTH_SHORT).show();
        }

        // Item name is required
        if (TextUtils.isEmpty(itemName)) {
            groceryItem.setError(REQUIRED);
            return;
        }

        // qty is required
        if (TextUtils.isEmpty(itemQty)) {
            qtyField.setError(REQUIRED);
            return;
        }

        if(itemName != ""){
            if (itemQty != ""){


                intBoby =  Integer.parseInt( qtyField.getText().toString());

                GroceryItem groceryItem = new GroceryItem(itemName,intBoby);


                    groceryList.add(groceryItem);

                    mDatabase.child(uuid).setValue(groceryList);

                    dataChangeListener();

                this.groceryItem.setText("");
                qtyField.setText("");
            }

        }
   }

   public void dataChangeListener() {

       if (uuid != null) {


            valueEventListener  = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {


                       ArrayList<GroceryItem> grocery = new ArrayList<GroceryItem>();

                       groceryList.clear();


                       grocery.clear();

                       grocery = (ArrayList<GroceryItem>) dataSnapshot.getValue();

                           for (int i = 0; i < grocery.size(); i++) {

                               Map<String, Object> x = new HashMap<>();
                               x = (Map<String, Object>) grocery.get(i);
                               Long lon = (Long) x.get("mQty");
                               int qty = (int) (long) lon;
                               String groceryListItem = (String) x.get("mGroceryItem");
                               GroceryItem groceryItem = new GroceryItem(groceryListItem, qty);

                               groceryList.add(groceryItem);


                           }


                       createFrag(groceryList);

                   }

                   @Override
                   public void onCancelled (DatabaseError databaseError){
                       // Getting Post failed, log a message

                       // ...
                   }

           };
           mDatabase.child(uuid).addValueEventListener(valueEventListener);
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
                .setTitle("What would you like to do?")
                .setMessage("Would you like to edit or delete this entry?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        groceryList.remove(position);
                        mDatabase.child(uuid).setValue(groceryList);
                    }
                })

                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Intent editIntent =  new Intent(MainActivity.this,EditActivity.class);
                        editIntent.putExtra("item", groceryList.get(position).getmGroceryItem());
                        editIntent.putExtra("qty",groceryList.get(position).getmQty());
                        editIntent.putExtra("position",position);

                        startActivityForResult(editIntent, 1234);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 || resultCode == RESULT_OK){

             String reItem = data.getStringExtra("item");
            int reQty = data.getIntExtra("qty", 0 );
            int rePosition = data.getIntExtra("position",0);
            GroceryItem reGroceryItem = new GroceryItem(reItem,reQty);

            groceryList.set(rePosition, reGroceryItem);

            mDatabase.child(uuid).setValue(groceryList);

            dataChangeListener();

        }
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



}
