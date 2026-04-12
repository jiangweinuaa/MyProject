package com.dsc.spos.thirdpart.duandian;

import lombok.Data;

@Data
public class queryOrderReq {
    private String companyCode;
    private String orderCode;
    private String beginTime;
    private String endTime;
    private String queryType;
    private Integer pageNo;
    private Integer pageSize;

}
