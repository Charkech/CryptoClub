package com.example.cryptoclub1.CoinsValues;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyAPI {
     static String apiKey = "9a1604a0-2e77-4388-8f4a-0cf65c36251f";
    @GET("latest?CMC_PRO_API_KEY="+apiKey)
    Call<List<Coin>> getCoins();


}
