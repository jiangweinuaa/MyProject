package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.List;

/**
 * @description: KDS传菜/出餐单据查询
 * @author: wangzyc
 * @create: 2021-09-28
 */
@Data
public class DCP_CdsDishQuery_OpenReq extends JsonBasicReq {
    public level1Elm request;

    @Data
    public class level1Elm{
        private String shopId; // 门店编号
        private String machineId; // 机台编号
        private List<String> goodsStatus; // 制作状态
        private List<String> repastType; // 就餐类型0堂食1打包2外卖；不传则查全部
    }
}
