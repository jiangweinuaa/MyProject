package com.dsc.spos.utils.price;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GoodsPrice {

    String orgNo;
    String pluNo;
    double qty;

    @Data
    public class Price {

        double price;

        PriceType type;

        int sort;
    }

    List<Price> prices = new ArrayList<>();

    public String getFirstValidPrice() {
        String price = "0";
        if (prices != null && !prices.isEmpty()) {

            prices = prices.stream().sorted(Comparator.comparingInt(Price::getSort)).collect(Collectors.toList());

            for (Price p : prices) {
                if (null != p && p.getPrice() != 0) {
                    price = String.valueOf(p.getPrice());
                    break;
                }
            }
        }
        return price;
    }

}

