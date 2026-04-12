package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;


import lombok.Data;

import java.util.List;

@Data
public class MES_SalesOrderCreateReq extends JsonBasicReq
{
        /**
         * null
         */
        private levelRequest request;

        @Data
        public class OrderDetailList
        {
                /**
                 * 项次
                 */
                private String erpItem;
                /**
                 * 商品编码
                 */
                private String pluNo;
                /**
                 * 特征码
                 */
                private String featureNo;
                /**
                 * 商品名称
                 */
                private String pluName;
                /**
                 * 单位
                 */
                private String unit;
                /**
                 * 数量
                 */
                private String pQty;
                /**
                 * 预交货日期
                 */
                private String rDate;
                /**
                 * 基准单位
                 */
                private String baseUnit;
                /**
                 * 基准单位数量
                 */
                private String baseQty;

                private String warehouseNo;

        }

        @Data
        public class levelRequest
        {
                /**
                 * 组织编码
                 */
                private String organizationNo;
                /**
                 * 企业编号
                 */
                private String eId;
                /**
                 * 销售订单单号
                 */
                private String erpOrderNo;
                /**
                 * 单据日期
                 */
                private String bDate;
                /**
                 * 客户编号
                 */
                private String customer;
                /**
                 * 业务员
                 */
                private String salesMan;

                private String salesManTel;

                /**
                 * 部门
                 */
                private String department;
                /**
                 * 送货地址
                 */
                private String address;
                /**
                 * 联系人
                 */
                private String contactName;
                /**
                 * 联系电话
                 */
                private String contactTele;
                /**
                 * 备注
                 */
                private String memo;
                /**
                 * 商品数量总和
                 */
                private String tot_Qty;
                /**
                 * 商品种类数量
                 */
                private String tot_CQty;
                /**
                 * 创建人
                 */
                private String createBy;
                /**
                 * 创建人名称
                 */
                private String createByName;
                /**
                 * 创建时间
                 */
                private String createTime;
                /**
                 * 明细列表
                 */
                private List<OrderDetailList> orderDetailList;
        }
}