package com.example.cryptoclub1.CoinsValues;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoinsJsonDeserializer implements JsonDeserializer {
   private static  String TAG=CoinsJsonDeserializer.class.getSimpleName();
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ArrayList<Coin> coins=null;
        try{
            JsonObject jsonObject = json.getAsJsonObject();
            JsonArray coinsJsonArray = jsonObject.getAsJsonArray("data");
            coins = new ArrayList<>(coinsJsonArray.size());
            for(int i=0;i<coinsJsonArray.size();i++){
                //adding the converted wrapepr to our container
                Coin dematerialized= context.deserialize(coinsJsonArray.get(i),Coin.class);
                coins.add(dematerialized);
            }
        }catch (JsonParseException e){
            Log.e(TAG,String.format("Could not desrialize Coin elemnt: %s",json.toString()));
        }

        return coins;
    }
}
