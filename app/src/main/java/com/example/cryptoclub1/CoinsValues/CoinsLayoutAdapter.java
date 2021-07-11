package com.example.cryptoclub1.CoinsValues;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
//import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoclub1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CoinsLayoutAdapter extends RecyclerView.Adapter<CoinsLayoutAdapter.ViewHolder> implements Filterable {
    LayoutInflater inflater;
    List<CoinObject> coins;
    List<CoinObject> coinsAll;
    CoinObject newCoin;





    public CoinsLayoutAdapter(Context context, List<CoinObject> coins){
        this.inflater=LayoutInflater.from(context);

        this.coins=coins;
        this.coinsAll = new ArrayList<>(coins);

    }
    public interface ViewClickListener {

        void onViewClick(View view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        CoinObject coin = coins.get(position);



        if(ValuesFragment.favList){ //if the boolean indicates that i want to see only favorite coins
            if(coin.isFavorite()){
                holder.itemView.setVisibility(View.VISIBLE);

            }
            else{
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams(); //if the coin object indicates that the coin is not favorite, hiding it
                params.height = 0;
                holder.itemView.setLayoutParams(params);
            }
        }
        else{
            holder.itemView.setVisibility(View.VISIBLE); //return all the list.
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.coins_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        //bind the data
        holder.nameTV.setText(coins.get(position).getName());
        holder.symbolTV.setText(coins.get(position).getSymbol());


        double i2=coins.get(position).getPrice();
        double i3 =coins.get(position).getChange1h();
        double i4 = coins.get(position).getChange24h();


        holder.priceTV.setText(new DecimalFormat("######.######").format(i2));
        if(coins.get(position).getChange1h()<=0){
            holder.pic_1h.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
            holder.precent_1hr.setText(new DecimalFormat("##.##").format(i3)+"%");
            holder.precent_1hr.setTextColor(Color.RED);
        }else{
            holder.pic_1h.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
            holder.precent_1hr.setText(new DecimalFormat("##.###").format(i3)+"%");
            holder.precent_1hr.setTextColor(Color.parseColor("#228B22"));

        }
        if(coins.get(position).getChange24h()<=0){
//            holder.cardView.setCardBackgroundColor(0xffdb3737);
            holder.pic_24h.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
            holder.precent_24hr.setText(new DecimalFormat("###.##").format(i4)+"%"); //todo check values
            holder.precent_24hr.setTextColor(Color.RED);


        }else{
//            holder.cardView.setCardBackgroundColor(0xff83d48d);
            holder.pic_24h.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
            holder.precent_24hr.setText(new DecimalFormat("###.##").format(i4)+"%");
            holder.precent_24hr.setTextColor(Color.parseColor("#228B22"));
        }

        holder.fav_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!coins.get(position).isFavorite()){
                    coins.get(position).setFavorite(true);
                    holder.fav_coin.setImageResource(R.drawable.ic_baseline_star_24);
                }

                else{
                    coins.get(position).setFavorite(false);
                    holder.fav_coin.setImageResource(R.drawable.ic_baseline_star_border_24);

                }

            }
        });

            if(coins.get(position).isFavorite()){

                holder.fav_coin.setImageResource(R.drawable.ic_baseline_star_24);
//                coins.remove(getAdapterPosition())
            }
            else{
                holder.fav_coin.setImageResource(R.drawable.ic_baseline_star_border_24);
            }
        }



    @Override
    public int getItemCount() {
        return coins.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    //background thread
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CoinObject> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(coinsAll);
            } else {
                for (CoinObject coin1 : coinsAll) { //check if the item is on the recyclerview
                    if (coin1.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            ||coin1.getSymbol().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(coin1);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            coins.clear();
            coins.addAll((Collection<? extends CoinObject>) results.values);
            notifyDataSetChanged();
        }

    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV,symbolTV,dateTV,priceTV,IdTV,time_tv,precent_1hr,precent_24hr;
        ImageView pic_1h,pic_24h,fav_coin;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.main_cardview_item);
            nameTV=itemView.findViewById(R.id.coin_name);


            symbolTV=itemView.findViewById(R.id.coin_symbol);
            fav_coin =itemView.findViewById(R.id.fav_coin);
            priceTV=itemView.findViewById(R.id.coin_price);
            pic_1h=itemView.findViewById(R.id.img_1h);
            pic_24h=itemView.findViewById(R.id.img_24h);
            precent_1hr = itemView.findViewById(R.id.precent_1hr);
            precent_24hr = itemView.findViewById(R.id.precent_24hr);
        }

        @Override
        public String toString() {
            return super.toString();
        }

    }
}
