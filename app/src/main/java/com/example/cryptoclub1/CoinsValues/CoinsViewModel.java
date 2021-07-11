package com.example.cryptoclub1.CoinsValues;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinsViewModel extends ViewModel {

    private MutableLiveData<List<Coin>> coinsList;


    public LiveData<List<Coin>> getCoinList(){
        if(coinsList==null){
            coinsList=new MutableLiveData<>();
            loadCoins();
        }
        return coinsList;
    }
    private void loadCoins(){
        CurrencyAPI api=ApiUtil.getRetrofitApi();

        Call<List<Coin>> call=api.getCoins();

        call.enqueue(new Callback<List<Coin>>() {
            @Override
            public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                coinsList.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<Coin>> call, Throwable t) {

            }
        });
    }
}
