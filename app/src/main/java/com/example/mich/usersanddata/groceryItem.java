package com.example.mich.usersanddata;

import java.io.Serializable;

/**
 * Created by Mich on 8/3/16.
 */
public class GroceryItem implements Serializable{

    public String mGroceryItem;
    public int mQty;



    //creates the object
    public GroceryItem(String _GroceryItem, int _qty) {

        mGroceryItem = _GroceryItem;
        mQty = _qty;


    }


    // Getter methods.
    public String getmGroceryItem() { return mGroceryItem;}
    public int getmQty() { return mQty;}


    // Setter Methods
    public void setmGroceryItem(String _GroceryItem) { mGroceryItem = _GroceryItem; }
    public void setmQty(int _qty) { mQty = _qty; }
}
