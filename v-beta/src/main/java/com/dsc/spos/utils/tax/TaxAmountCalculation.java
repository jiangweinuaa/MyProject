package com.dsc.spos.utils.tax;

import com.dsc.spos.utils.BigDecimalUtils;
import com.dsc.spos.utils.Check;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxAmountCalculation {

    public static TaxAmount calculateAmount(boolean incTax, double amountWithTax, double taxRate, @NotNull String taxMethod, int scale) {
        taxRate = taxRate * 0.01;
        if (taxRate <= 0) {
            taxRate = 0;
        }

        if (scale <= 0) {
            scale = 2;
        }

        TaxAmount taxAmount = new TaxAmount();
        taxAmount.setIncTax(incTax);
        taxAmount.setTaxRate(taxRate);
        taxAmount.setAmount(BigDecimalUtils.round(amountWithTax, scale));
        if (incTax) { //单价含税，用含税算未税
            if ("1".equals(taxMethod)) {
                double denominator = 1 + taxRate;
                taxAmount.setPreAmount(BigDecimalUtils.div(amountWithTax, denominator, scale));
                taxAmount.setTaxAmount(BigDecimalUtils.sub(taxAmount.getAmount(), taxAmount.getPreAmount()));

            } else if ("2".equals(taxMethod)) {
                taxAmount.setTaxAmount(BigDecimalUtils.mul(amountWithTax, taxRate, scale));
                taxAmount.setPreAmount(BigDecimalUtils.round(taxAmount.getAmount() - taxAmount.getTaxAmount(), scale));
            } else if (Check.Null(taxMethod)) {
                taxAmount.setTaxAmount(Double.parseDouble("0"));
                taxAmount.setPreAmount(Double.parseDouble("0"));
            }

        } else {  //单价含税，用未税算含税
            taxAmount.setPreAmount(BigDecimalUtils.round(amountWithTax, scale));
            taxAmount.setTaxAmount(BigDecimalUtils.mul(amountWithTax, taxRate, scale)); //税额=税前金额*税率
            taxAmount.setAmount(BigDecimalUtils.add(taxAmount.getPreAmount(), taxAmount.getTaxAmount())); //含税金额=税前金额+税额

        }

        return taxAmount;
    }

    public static TaxAmount2 calculateAmount(String incTax, BigDecimal amountWithTax, BigDecimal taxRate, @NotNull String taxMethod, int scale) {
        //后面传整数  不传小数点
        taxRate = taxRate.multiply(new BigDecimal(0.01));

        if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
            taxRate = new BigDecimal(0);
        }

        if (scale <= 0) {
            scale = 2;
        }

        TaxAmount2 taxAmount = new TaxAmount2();
        taxAmount.setIncTax(incTax);
        taxAmount.setTaxRate(taxRate);
        taxAmount.setAmount(BigDecimalUtils.round(amountWithTax, scale));
        if ("Y".equals(incTax)) {
            if ("1".equals(taxMethod)) {
                BigDecimal denominator = taxRate.add(new BigDecimal(1));
                taxAmount.setPreAmount(amountWithTax.divide(denominator, scale, RoundingMode.HALF_UP));
                taxAmount.setTaxAmount(taxAmount.getAmount().subtract(taxAmount.getPreAmount()));

            } else if ("2".equals(taxMethod)) {
                taxAmount.setTaxAmount(amountWithTax.multiply(taxRate).setScale(scale, BigDecimal.ROUND_HALF_UP));
                taxAmount.setPreAmount(taxAmount.getAmount().subtract(taxAmount.getTaxAmount()));
            } else if (Check.Null(taxMethod)) {
                taxAmount.setTaxAmount(new BigDecimal(0));
                taxAmount.setPreAmount(new BigDecimal(0));
            }

        } else {

            //税前金额=未税单价*数量  ==amount
            //● 税额=税前金额*税率
            //● 含税金额=税前金额+税额

            taxAmount.setPreAmount(amountWithTax);
            taxAmount.setTaxAmount(amountWithTax.multiply(taxRate).setScale(scale, BigDecimal.ROUND_HALF_UP));
            taxAmount.setAmount(taxAmount.getPreAmount().add(taxAmount.getTaxAmount()));
        }

        return taxAmount;
    }


}
