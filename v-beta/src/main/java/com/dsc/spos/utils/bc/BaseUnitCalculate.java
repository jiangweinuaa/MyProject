package com.dsc.spos.utils.bc;

import lombok.Data;

@Data
public class BaseUnitCalculate {

    private String pluNo;
    private String unit;
    private String qty;

    private String baseUnit;
    private String baseQty;
    private String unitRatio;

}
