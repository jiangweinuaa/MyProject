package com.dsc.spos.thirdpart.duandian;

import lombok.Data;

@Data
public class responseDTO {
    private boolean success;
    private int code;
    private String msg;
    private String errorCode;
    private String errorMsg;
}
