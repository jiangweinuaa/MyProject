package com.dsc.spos.utils.bc;

import lombok.Data;

import java.math.RoundingMode;

@Data
public class UnitInfo {

    private String unit;
    private Integer udLength;

    //舍入类型：1四舍五入、2四舍六入五成双、3无条件舍弃、4无条件进位
    private String roundType;
    private RoundingMode roundingMode;


    public void setRoundingMode() {
        switch (this.roundType) {
            case "2":
                this.roundingMode = RoundingMode.HALF_EVEN;
                break;
            case "3":
                this.roundingMode = RoundingMode.DOWN;
                break;
            case "4":
                this.roundingMode = RoundingMode.UP;
                break;
            case "1":
            default:
                this.roundingMode = RoundingMode.HALF_UP;
            }
    }

    public void setRoundingMode(String roundType) {
        switch (roundType) {
            case "2":
                this.roundingMode = RoundingMode.HALF_EVEN;
                break;
            case "3":
                this.roundingMode = RoundingMode.DOWN;
                break;
            case "4":
                this.roundingMode = RoundingMode.UP;
                break;
            case "1":
            default:
                this.roundingMode = RoundingMode.HALF_UP;
        }
    }


}
