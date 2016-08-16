package com.example.mich.usersanddata;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    int position;
    int qty;
    String item;
    EditText itemText;
    EditText qtyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemText = (EditText) findViewById(R.id.editText);
        qtyText = (EditText) findViewById(R.id.editText2);

        Intent intent = getIntent();
        item = intent.getStringExtra("item");
        qty = intent.getIntExtra("qty", 0 );
        position =  intent.getIntExtra("position",0);

        itemText.setText(item);

        qtyText.setText(String.valueOf(qty));



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              saveNewText();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            saveNewText();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void saveNewText(){
        final String itemName = itemText.getText().toString();
        final String itemQty =  qtyText.getText().toString();


        // Item name is required
        if (TextUtils.isEmpty(itemName)) {
            itemText.setError("REQUIRED");
            return;
        }

        // qty is required
        if (TextUtils.isEmpty(itemQty)) {
            qtyText.setError("REQUIRED");
            return;
        }

        if(itemName != ""){
            if (itemQty != ""){

                int parsedInt =  Integer.parseInt( qtyText.getText().toString());





                Intent returnIntent = new Intent();
                returnIntent.putExtra("item", itemName);
                returnIntent.putExtra("qty",parsedInt);
                returnIntent.putExtra("position",position);
                setResult(RESULT_OK, returnIntent);
                finish();



            }

        }
    }
}
