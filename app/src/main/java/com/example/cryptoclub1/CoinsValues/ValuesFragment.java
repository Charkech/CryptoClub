package com.example.cryptoclub1.CoinsValues;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.repackaged.com.google.common.collect.Lists;
import com.example.cryptoclub1.MainActivity;
import com.example.cryptoclub1.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Sets;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;


public class ValuesFragment extends Fragment  {
        RecyclerView recyclerView;
        TextView time_tv;
        List<CoinObject> coins,favouritesList;
        Context context;
        private static String URL="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=9a1604a0-2e77-4388-8f4a-0cf65c36251f";
        CoinsLayoutAdapter coinsLayoutAdapter;
        FloatingActionButton favFab;
        static Boolean favList=false;
        DatabaseReference databaseReference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    public void onStart() {
        super.onStart();
        HashSet<CoinObject> hashSet = new HashSet<CoinObject>(); //remove duplicates
        hashSet.addAll(coins);
        coins.clear();
        coins.addAll(hashSet);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(coinsLayoutAdapter!=null)
            coinsLayoutAdapter.notifyDataSetChanged();


        favList = false;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("FavCoins");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for(CoinObject coin:coins){
            if(coin.isFavorite()){
                databaseReference.child(user.getUid()).push().setValue(coin); //on stop, im saving the user fav coins
            }
        }
        HashSet<CoinObject> hashSet = new HashSet<CoinObject>(); //remove duplicates
        hashSet.addAll(coins);
        coins.clear();
        coins.addAll(hashSet);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        favouritesList = new ArrayList<>();
        String uid = user.getUid();
        coins = new ArrayList<>();


        context = getContext();
        extractCoins();
        databaseReference = FirebaseDatabase.getInstance().getReference("FavCoins");
        favouritesList.clear();
        //this function loads favorites coin per user:
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                coinsLayoutAdapter = new CoinsLayoutAdapter(context, coins);
                favouritesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.child(uid).getChildren()) { //fetch every user
                    CoinObject coinObject = snapshot.getValue(CoinObject.class);

                    String date = (String) dataSnapshot.child("date_added").getValue();
                    String symbol = (String) dataSnapshot.child("symbol").getValue();
                    Double price = (Double) dataSnapshot.child("price").getValue();
                    Double change1h = (Double) dataSnapshot.child("change1h").getValue();
                    String name = (String) dataSnapshot.child("name").getValue();
                    Double change24h = (Double) dataSnapshot.child("change24h").getValue();
                    Long id = (Long) dataSnapshot.child("id").getValue();
                    boolean favorite = (boolean) dataSnapshot.child("favorite").getValue();

//                  creating new object from firebase database (fav coin per user)
                    CoinObject newCoin = new CoinObject(Math.toIntExact(id), name, symbol, date, price, change1h, change24h, favorite); //favorite == true
                    HashSet<CoinObject> hashSet2 = new HashSet<>();
                    hashSet2.addAll(favouritesList);
                    favouritesList.clear();
                    favouritesList.addAll(hashSet2);
                    favouritesList.add(newCoin);//adding to the main list

                }

                HashSet<CoinObject> hashSet = new HashSet<>(); //hash code is to id, uniquely
                hashSet.addAll(coins);
                coins.clear();
                coins.addAll(hashSet);
                coins.addAll(favouritesList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        HashSet<CoinObject> hashSet = new HashSet<CoinObject>(); //remove duplicates
        hashSet.addAll(coins);
        coins.clear();
        coins.addAll(hashSet);


    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.values_fragment,container,false);

        String uid = user.getUid();
        coins = new ArrayList<>();

        recyclerView=view.findViewById(R.id.recycler_values);
        time_tv = view.findViewById(R.id.time_tv);
        favFab = view.findViewById(R.id.favourite_coins_fab);



        favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favList = !favList; //in order to see fav/not fav lists

                coinsLayoutAdapter.notifyDataSetChanged();
            }

        });
        HashSet<CoinObject> hashSet = new HashSet<CoinObject>(); //remove duplicates
        hashSet.addAll(coins);
        coins.clear();
        coins.addAll(hashSet);


        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = viewHolder.getLayoutPosition();
                        switch(direction){

                            case ItemTouchHelper.LEFT:
                            case ItemTouchHelper.RIGHT:
                                String name = coins.get(pos).getName();
                                String url ="https://coinmarketcap.com/currencies/"+name.replace(" ","-")+'/';


                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                                coinsLayoutAdapter.notifyDataSetChanged();


                                break;
                        }

                    }
                };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




            return view;
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.search_bar,menu);
        MenuItem item = menu.findItem( R.id.search_bar1);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText==null){
                    return false;
                }
                else {
                    if(coinsLayoutAdapter!=null)
                        coinsLayoutAdapter.getFilter().filter(newText);

                    return false;
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void extractCoins() {
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray=response.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject coin = jsonArray.getJSONObject(i);
                        CoinObject coinObject;
                        double price=coin.getJSONObject("quote").getJSONObject("USD").getDouble("price");
                        double change1h=coin.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_1h");
                        double change24h=coin.getJSONObject("quote").getJSONObject("USD").getDouble("percent_change_24h");
                        int id = coin.getInt("id");
                        String name=coin.getString("name");
                        String date=coin.getString("last_updated");
                        String st1=date.replaceAll("T","  ");
                        String st2=st1.replaceAll("Z","");
                        String symbol=coin.getString("symbol");
                        coinObject=new CoinObject(id,name,symbol,st2,price,change1h,change24h);
                        coins.add(coinObject);
                        time_tv.setText(st2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setHasFixedSize(true);

                coinsLayoutAdapter =new CoinsLayoutAdapter(context,coins);
                recyclerView.setAdapter(coinsLayoutAdapter);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }
}
