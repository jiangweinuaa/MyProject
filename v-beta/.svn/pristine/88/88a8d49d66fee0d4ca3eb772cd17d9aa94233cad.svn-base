package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;


import lombok.Data;

import java.util.List;

@Data
public class MES_ErpStockTakeAddReq extends JsonBasicReq
{
    /**
     * null
     */
    private levelRequest request;

    @Data
    public class UnitList
    {
        /**
         * erp盘点明细项次
         */
        private String oItem;
        /**
         * 盘点单位
         */
        private String pUnit;
        /**
         * null
         */
        private String unitRatio;
    }

    @Data
    public class DetailList
    {
        /**
         * 商品编码
         */
        private String pluNo;
        /**
         * 特征码（要给空格）
         */
        private String featureNo;
        /**
         * erp盘点明细项次
         */
        private String oItem;
        /**
         * 单据日期
         */
        private String bDate;
        /**
         * 进货单价
         */
        private String distriPrice;
        /**
         * 基准单位
         */
        private String baseUnit;


        /**
         * null
         */
        private List<UnitList> unitList;
    }

    @Data
    public class levelRequest
    {
        /**
         * 组织编码
         */
        private String organizationNo;
        /**
         * 仓库编码
         */
        private String warehouseNo;
        /**
         * erp单号
         */
        private String ofNo;
        /**
         * 商品总数
         */
        private String totQty;
        /**
         * 商品种类
         */
        private String totCQty;
        /**
         * 0新建  2完成
         */
        private String status;
        /**
         * 单据日期
         */
        private String bDate;
        /**
         * 盘点明细
         */
        private List<DetailList> detailList;
        /**
         * null
         */
        private String eId;
    }
}