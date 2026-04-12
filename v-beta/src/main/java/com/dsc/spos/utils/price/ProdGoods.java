package com.dsc.spos.utils.price;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdGoods {
    String orgNo;
    String pluNo;
    double qty;

    public ProdGoods(String orgNo, String pluNo, double qty) {
        this.orgNo = orgNo;
        this.pluNo = pluNo;
        this.qty = qty;
    }

}
