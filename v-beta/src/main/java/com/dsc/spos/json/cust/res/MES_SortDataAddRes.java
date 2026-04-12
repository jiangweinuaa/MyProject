package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

@Data
public class MES_SortDataAddRes extends JsonBasicRes
{

    private String doc_no; //门店管理对应的单号
    private String org_no; //门店管理对应的门店

}
