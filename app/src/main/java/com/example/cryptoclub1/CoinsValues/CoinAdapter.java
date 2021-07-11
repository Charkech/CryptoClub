package com.example.cryptoclub1.CoinsValues;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptoclub1.R;

import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {
    public CoinAdapter(List<Coin> coins, Context context) {
        this.coins = coins;
        this.context = context;
    }

          private List<Coin> coins;
            private Context context;

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coins_layout,parent,false);
        return new CoinViewHolder(view);
    }



    @Override
    public int getItemCount() {
        if(coins==null){
            return 0;
        }
        return coins.size();
    }

    public class CoinViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView nameTV,priceTV,time_tv;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
//            imageView=itemView.findViewById(R.id.coin_image);
            nameTV=itemView.findViewById(R.id.coin_name);
            priceTV=itemView.findViewById(R.id.coin_price);
//            time_tv = itemView.findViewById(R.id.time_tv);

        }
    }
    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        Coin coin = coins.get(position);
//        Glide.with(context).load(coin.getImageurl()).into(holder.imageView);
        holder.nameTV.setText(coin.data.getName());
//        holder.priceTV.setText(Double.toString(coin.getPrice()));




    }
}
