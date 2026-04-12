package com.dsc.spos.waimai.meituanJBP;

import java.util.List;

public class sellStatusReq {
    /**
     * 售卖状态，1表下架，0表上架
     */
    private int sell_status;
    private List<FoodData> food_data;

    public int getSell_status() {
        return sell_status;
    }

    public void setSell_status(int sell_status) {
        this.sell_status = sell_status;
    }

    public List<FoodData> getFood_data() {
        return food_data;
    }

    public void setFood_data(List<FoodData> food_data) {
        this.food_data = food_data;
    }

    public static class FoodData
    {
        private String app_food_code;
        private List<Skus> skus;

        public String getApp_food_code() {
            return app_food_code;
        }

        public void setApp_food_code(String app_food_code) {
            this.app_food_code = app_food_code;
        }

        public List<Skus> getSkus() {
            return skus;
        }

        public void setSkus(List<Skus> skus) {
            this.skus = skus;
        }
    }
    public static class Skus
    {
        private String sku_id;

        public String getSku_id() {
            return sku_id;
        }

        public void setSku_id(String sku_id) {
            this.sku_id = sku_id;
        }
    }

}



