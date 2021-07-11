package com.example.cryptoclub1.CoinsValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtil {
    static final Type COIN_ARRAY_LIST_CLASS_TYPE=(new ArrayList<Coin>()).getClass();
    static String BASE_URL ="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/";

    private static String apiKey = "9a1604a0-2e77-4388-8f4a-0cf65c36251f";
    public static CurrencyAPI getRetrofitApi(){
        Gson gson=new GsonBuilder().registerTypeAdapter(COIN_ARRAY_LIST_CLASS_TYPE,new CoinsJsonDeserializer()).create();

        Retrofit retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build();

        CurrencyAPI currencyAPI = retrofit.create(CurrencyAPI.class);
        return currencyAPI;
    }
}
