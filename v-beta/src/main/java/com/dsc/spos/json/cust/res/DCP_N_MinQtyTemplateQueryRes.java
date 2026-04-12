package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_N_MinQtyTemplateQuery
 * 服务说明：N-起售量模板查询
 * @author jinzma
 * @since  2024-04-18
 */
@Data
public class DCP_N_MinQtyTemplateQueryRes extends JsonRes {
    private List<Datas> datas;
    @Data
    public class Datas {
        private String templateId; // 模板编码
        private String templateName; // 模板名称
        private String memo; // 备注
        private String status; // 状态：-1未启用100已启用 0已禁用
        private String restrictShop; // 适用门店：0-所有门店1-指定门店
        private List<Shop> shopList; // 适用门店
        private List<Plu> pluList; // 商品列表
        private String createtime; // 创建时间yyyy-MM-dd HH:mm:ss，降序第一列
        private String createopid; // 创建人编号
        private String createopname; // 创建人编号
        private String updateTime; // 最后修改时间yyyyMMddHH:mm:ss
        private String lastmodiopid; // 最后修改人编号
        private String lastmodiname; // 最后修改人名称
    }
    @Data
    public class Shop {
        private String id; // 编号
        private String name;// 名称
    }
    @Data
    public class Plu {
        private String pluNo; // 商品编码
        private String pluName;// 商品名称
        private String punitName; // 单位名称
        private String price; // 零售价
        private String minQty; // 起售量
        private String maxQty; // 限售量
    }



}
