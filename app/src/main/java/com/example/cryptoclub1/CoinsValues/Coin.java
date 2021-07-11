package com.example.cryptoclub1.CoinsValues;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Coin {
    public data data;

    public class data{
        public String getId() {
            return id;
        }

        public void setId(String id) {
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

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getNum_market_pairs() {
            return num_market_pairs;
        }

        public void setNum_market_pairs(String num_market_pairs) {
            this.num_market_pairs = num_market_pairs;
        }

        public String getDate_added() {
            return date_added;
        }

        public void setDate_added(String date_added) {
            this.date_added = date_added;
        }

        public String getMax_supply() {
            return max_supply;
        }

        public void setMax_supply(String max_supply) {
            this.max_supply = max_supply;
        }

        public String getTotal_supply() {
            return total_supply;
        }

        public void setTotal_supply(String total_supply) {
            this.total_supply = total_supply;
        }

        public String getCmc_rank() {
            return cmc_rank;
        }

        public void setCmc_rank(String cmc_rank) {
            this.cmc_rank = cmc_rank;
        }

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        public Coin.quote getQuote() {
            return quote;
        }

        public void setQuote(Coin.quote quote) {
            this.quote = quote;
        }

        private String id;
        private String name;
        private String symbol;
        private String slug;
        private String num_market_pairs;
        private String date_added;
        private String max_supply;
        private String total_supply;
        private String cmc_rank;
        private String last_updated;
        quote quote;

    }
    public class quote{
        public Coin.USD getUSD() {
            return USD;
        }

        public void setUSD(Coin.USD USD) {
            this.USD = USD;
        }

        public USD USD;
    }
    public class USD{
        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getVolume_24h() {
            return volume_24h;
        }

        public void setVolume_24h(String volume_24h) {
            this.volume_24h = volume_24h;
        }

        public String getPercent_change_1h() {
            return percent_change_1h;
        }

        public void setPercent_change_1h(String percent_change_1h) {
            this.percent_change_1h = percent_change_1h;
        }

        public String getPercent_change_24h() {
            return percent_change_24h;
        }

        public void setPercent_change_24h(String percent_change_24h) {
            this.percent_change_24h = percent_change_24h;
        }

        public String getPercent_change_7d() {
            return percent_change_7d;
        }

        public void setPercent_change_7d(String percent_change_7d) {
            this.percent_change_7d = percent_change_7d;
        }

        public String getPercent_change_30d() {
            return percent_change_30d;
        }

        public void setPercent_change_30d(String percent_change_30d) {
            this.percent_change_30d = percent_change_30d;
        }

        public String getPercent_change_60d() {
            return percent_change_60d;
        }

        public void setPercent_change_60d(String percent_change_60d) {
            this.percent_change_60d = percent_change_60d;
        }

        public String getPercent_change_90d() {
            return percent_change_90d;
        }

        public void setPercent_change_90d(String percent_change_90d) {
            this.percent_change_90d = percent_change_90d;
        }

        public String getMarket_cap() {
            return market_cap;
        }

        public void setMarket_cap(String market_cap) {
            this.market_cap = market_cap;
        }

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        private String price;
        private String volume_24h;
        private String percent_change_1h;
        private String percent_change_24h;
        private String percent_change_7d;
        private String percent_change_30d;
        private String percent_change_60d;
        private String percent_change_90d;
        private String market_cap;
        private String last_updated;
    }

}
