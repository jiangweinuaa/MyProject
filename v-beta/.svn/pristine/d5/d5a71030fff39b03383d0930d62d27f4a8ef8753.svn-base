package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: 门店休息日删除
 * @author: wangzyc
 * @create: 2021-07-27
 */
@Data
public class DCP_HolidayDeleteReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;  // 所属门店
        private List<String> itemList;  // 项次
    }

}
