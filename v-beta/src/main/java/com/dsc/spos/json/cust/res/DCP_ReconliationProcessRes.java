package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReconliationProcessRes extends JsonBasicRes {
    private String reconNo;
    private Integer num;
    private String firstReconNo;
    private String lastReconNo;
    private List<String> reconNos;
}
