package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * @description: PDA标价签采集
 * @author: wangzyc
 * @create: 2021-11-18
 */
@Data
public class DCP_CreatePdaPricetagReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String pluNo; // 品号
        private String featureNo; // 特征码 (如果前端不传，后端 服务根据条码匹配特征码，有多条则生成多条供打印标签。)
        private String barcode; // 条码 (如果是PDA扫码，这个必然是PDA扫到的条码)
        private String terminalId; //终端设备号 (PDA的设备号)
        private String orgType; // 1=公司 2=门店 (总部：填公司 门店：填门店 第三方：填渠道)
        private String orgId; // 组织编号  如：公司编号 门店号
    }
}
