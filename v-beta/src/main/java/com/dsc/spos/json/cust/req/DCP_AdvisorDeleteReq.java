package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 顾问删除
 * @author: wangzyc
 * @create: 2021-07-14
 */
@Data
public class DCP_AdvisorDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private List<String> opNo; // 员工编号
    }
}
