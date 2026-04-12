package com.dsc.spos.waimai.sftc;

import java.util.List;

public class order_detail {
    /**
     * 必须-用户订单商品总金额（单位：分）
     */
    private int total_price;
    /**
     * 必须-物品类型
     */
    private int product_type;
    /**
     * 非必须-用户实付商家金额（单位：分）
     */
    private int user_money;
    /**
     * 非必须-商家实收用户金额（单位：分）
     */
    private int shop_money;
    /**
     * 	必须-物品重量（单位：克）
     */
    private int weight_gram;
    /**
     * 非必须-物品体积（单位：升）
     */
    private int volume_litre;
    /**
     * 非必须-商家收取用户的配送费（单位：分）
     */
    private int delivery_money;//
    /**
     * 	必须-物品个数
     */
    private int product_num;
    /**
     * 	必须-物品种类个数
     */
    private int product_type_num;
    /**
     * 必须-商品信息
     */
    private List<product_detail> product_detail;

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getProduct_type() {
        return product_type;
    }

    public void setProduct_type(int product_type) {
        this.product_type = product_type;
    }

    public int getWeight_gram() {
        return weight_gram;
    }

    public void setWeight_gram(int weight_gram) {
        this.weight_gram = weight_gram;
    }

    public int getVolume_litre() {
        return volume_litre;
    }

    public void setVolume_litre(int volume_litre) {
        this.volume_litre = volume_litre;
    }

    public int getProduct_num() {
        return product_num;
    }

    public void setProduct_num(int product_num) {
        this.product_num = product_num;
    }

    public int getProduct_type_num() {
        return product_type_num;
    }

    public void setProduct_type_num(int product_type_num) {
        this.product_type_num = product_type_num;
    }

    public int getUser_money() {
        return user_money;
    }

    public void setUser_money(int user_money) {
        this.user_money = user_money;
    }

    public int getShop_money() {
        return shop_money;
    }

    public void setShop_money(int shop_money) {
        this.shop_money = shop_money;
    }

    public int getDelivery_money() {
        return delivery_money;
    }

    public void setDelivery_money(int delivery_money) {
        this.delivery_money = delivery_money;
    }

    public List<com.dsc.spos.waimai.sftc.product_detail> getProduct_detail() {
        return product_detail;
    }

    public void setProduct_detail(List<com.dsc.spos.waimai.sftc.product_detail> product_detail) {
        this.product_detail = product_detail;
    }
}
