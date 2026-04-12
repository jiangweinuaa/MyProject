package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

/**
 * @description: KDS基础设置查询
 * @author: wangzyc
 * @create: 2021-09-18
 */
@Data
public class DCP_KdsBaseSetQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String overTime; // 超时预警时间(分钟)
        private String miniSendMsg; // 小程序取餐提醒 Y/N
    }
}
