package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_MinQtyTemplateUpdate
 * 服务说明：N-起售量模板修改
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_MinQtyTemplateUpdateReq extends JsonBasicReq {

    private Request request;
    @Data
    public class Request{
        private String templateId; // 模板编码 必填
        private String templateName; // 模板名称
        private String memo; // 备注
        private String status; // 状态：-1未启用100已启用 0已禁用
        private String restrictShop; // 适用门店：0-所有门店1-指定门店
        private String restrictChannel; //适用渠道：0-所有渠道1-指定渠道2-排除渠道
        private String restrictPeriod; //适用时段：0-所有时段1-指定时段
        private List<Shop> shopList; // 适用门店
        private List<Plu> pluList; // 商品列表
    }
    @Data
    public class Shop{
        private String id; // 门店编号
        private String name;// 名称
    }
    @Data
    public class Plu{
        private String pluNo;  //商品编码
        private String minQty; //起售量
        private String maxQty; //限售量
    }

}
