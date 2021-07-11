package com.example.cryptoclub1.CoinsValues;

import androidx.annotation.Nullable;

import java.util.Objects;

public class CoinObject {
    private int id;
    private String name;
    private String symbol;
    private String date_added;
    double price;
    double change1h;
    double change24h;
    private boolean isFavorite= false;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public CoinObject(){

    }




    public CoinObject(int id, String name, String symbol){
        this.id = id;
        this.name = name;
        this.symbol = symbol;

    }

    public CoinObject(int id, String name, String symbol, String date_added, double price, double change1h, double change24h) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.date_added = date_added;
        this.price = price;
        this.change1h = change1h;
        this.change24h = change24h;

    }

    @Override
    public int hashCode() {
//        return Objects.hash(id); //hash code by id, it's unique
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;


    }

    @Override
    public boolean equals(@Nullable Object obj) {
          if (this == obj)
        return true;
    if (obj == null)
        return false;
    if (getClass() != obj.getClass())
        return false;
    CoinObject other = (CoinObject) obj;
    if (id != other.id)
        return false;
    return true;
    }

    public CoinObject(int id, String name, String symbol, double price, double change1h, double change24h) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;

        this.price = price;
        this.change1h = change1h;
        this.change24h = change24h;

    }
    public CoinObject(int id, String name, String symbol, String date_added, double price, double change1h, double change24h,boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.date_added = date_added;
        this.price = price;
        this.change1h = change1h;
        this.change24h = change24h;
        this.isFavorite = isFavorite;

    }



    public double getChange1h() {
        return change1h;
    }

    public void setChange1h(double change1h) {
        this.change1h = change1h;
    }

    public double getChange24h() {
        return change24h;
    }

    public void setChange24h(double change24h) {
        this.change24h = change24h;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
