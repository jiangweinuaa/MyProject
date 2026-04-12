package com.dsc.spos.utils;


import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Data
public class DecimalFormatUtils {

    private int numberDigit;
    private RoundingMode roundingMode;

    public DecimalFormatUtils(int numberDigit, RoundingMode roundingMode) {
        this.numberDigit = numberDigit;
        this.roundingMode = roundingMode;
    }

    public String format(double number) {
        BigDecimal bd = new BigDecimal(number);
        return format(bd);
    }

    public String format(String number) {
        return format(Double.parseDouble(number));
    }

    public String format(BigDecimal number) {
        number = number.setScale(numberDigit, roundingMode);
        return number.toPlainString();
    }

}
