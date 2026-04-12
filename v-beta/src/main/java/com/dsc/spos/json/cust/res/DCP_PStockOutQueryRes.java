package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PStockOutQueryRes extends JsonRes {
    private List<DCP_PStockOutQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String pStockInNo;
        private String bDate;
        private String memo;
        private String status;
        private String oType;
        private String ofNo;
        private String loadDocType;
        private String loadDocNo;
        private String createByName;
        private String pTemplateNo;
        private String pTemplateName;
        private String warehouse;
        private String warehouseName;
        private String materialWarehouseNo;
        private String materialWarehouseName;
        //2018-11-14 添加modifyBy 等参数
        private String createBy;
        private String createDate;
        private String createTime;
        private String submitBy;
        private String submitDate;
        private String submitTime;
        private String modifyBy;
        private String modifyDate;
        private String modifyTime;
        private String confirmBy;
        private String confirmDate;
        private String confirmTime;
        private String cancelBy;
        private String cancelDate;
        private String cancelTime;
        private String accountBy;
        private String accountDate;
        private String accountTime;
        private String modifyByName;
        private String cancelByName;
        private String confirmByName;
        private String submitByName;
        private String accountByName;
        private String totPqty;
        private String totAmt;
        private String totCqty;
        private String totDistriAmt;
        private String update_time;
        private String process_status;
        private String processERPNo;
        //2018-08-08添加docType
        private String docType; // 0-完工入库  1-组合单   2-拆解单
        //完工入库红冲 用到
        private String pStockInNo_origin;//原完工入库单号
        private String pStockInNo_refund;//关联的红冲完工入库单号，不想改查询语句了，直接加个字段吧
        private String refundStatus;//关联红冲的完工入库单状态 ，

        private String processPlanNo;
        private String task0No;
        private String dtNo;
        private String dtName;
        private String dtBeginTime;
        private String dtEndTime;

        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String createDeptId;
        private String createDeptName;

    }


}

