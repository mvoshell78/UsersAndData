package com.example.mich.usersanddata;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mich on 8/3/16.
 */
public class ListFragment extends android.app.ListFragment {



    public static final String TAG = "ListFragment";
    private static String ARG = "arg";


    getListPosition listener;
    public ListFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof getListPosition){
            listener = (getListPosition) activity;
        } else {
            throw new IllegalArgumentException("not connected");
        }
    }

    public static ListFragment newInstanceOf(ArrayList<GroceryItem> fav ){
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG, fav);
        fragment.setArguments(args);
        return  fragment;
    }

    private ArrayList<GroceryItem> mPeople;

    private ArrayList<String> data;
    private ArrayList<String> qtyArray;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        data = new ArrayList<String>();
        qtyArray = new ArrayList<String>();

        Bundle args = getArguments();
        if (args != null) {
            setUpList((ArrayList<GroceryItem>) args.getSerializable(ARG));

        }
    }



    public void setUpList(final ArrayList<GroceryItem> fav) {
        data.clear();
        ArrayList<GroceryItem> people = fav;

        int size = people.size();

        String favName;
        for (int i = 0; i < size ; i++) {
            GroceryItem getFavName = people.get(i);
            favName = getFavName.getmGroceryItem();

            String qty = String.valueOf(getFavName.getmQty());


            data.add(favName);
            qtyArray.add(qty);


        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_2, android.R.id.text1 ,data){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(data.get(position) );
                text2.setText(qtyArray.get(position));
                return view;
            }
        };


        setListAdapter(adapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        listener.passBackPosition(position);


    }


}
