package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：DCP_SStockInQuery
 * 說明：自采入库查询
 * 服务说明：自采入库查询
 * @author jinzma
 * @since  2018-11-21
 */
@Setter
@Getter
public class DCP_SStockInQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

    @Setter
    @Getter
    public class level1Elm {

		private String sStockInNo;
		private String docType;
		private String processERPNo;
		private String bDate;
		private String memo;
		private String status;
		private String supplier;
		private String supplierName;
		private String oType;
		private String ofNo;
		private String pTemplateNo;
		private String pTemplateName;
		private String loadDocType;
		private String loadReceiptNo;
		private String loadDocNo;
		private String warehouse;
		private String warehouseName;
		private String createBy;
		private String createByName;
		private String createDate;
		private String createTime;
		private String modifyBy;
		private String modifyByName;
		private String modifyDate;
		private String modifyTime;
		private String submitBy;
		private String submitByName;
		private String submitDate;
		private String submitTime;
		private String confirmBy;
		private String confirmByName;
		private String confirmDate;
		private String confirmTime;
		private String cancelBy;
		private String cancelByName;
		private String cancelDate;
		private String cancelTime;
		private String accountBy;
		private String accountByName;
		private String accountDate;
		private String accountTime;
		private String update_time;
		private String process_status;
		private String totPqty;
		private String totAmt;
		private String totCqty;
		private String totDistriAmt;

		private String deliveryNo;
		private String taxCode;//税别编码
		private String taxName;//税别名称
		private String buyerNo;
		private String buyerName;//采购员
		private String rDate;
		private String isBatchSStockIn;
		private String receiptDate;
		private String employeeId;
		private String employeeName;
		private String departId;
		private String departName;
		private String payType;
		private String payOrgNo;
		private String billDateNo;
		private String billDateDesc;
		private String payDateNo;
		private String payDateDesc;
		private String invoiceCode;
		private String invoiceName;
		private String currency;
		private String currencyName;
		private String stockInType;
		private String customer;
		private String customerName;
		private String totDistriPreTaxAmt;
		private String totDistriTaxAmt;
		private String orderOrgNo;
		private String orderOrgName;
		private String orderNo;
		private String expireDate;
		private String ooType;
		private String oofNo;
		private String originNo;
		private String returnType;

        private String payee;
        private String payeeName;
        private String payer;
        private String payerName;

        //corp,corpName,bizOrgNo,bizOrgName,bizCorp,bizCorpName,totAmount,totPreTaxAmt,totTaxAmt;
        private String corp;
        private String corpName;
        private String bizOrgNo;
        private String bizOrgName;
        private String bizCorp;
        private String bizCorpName;
        private String totAmount;
        private String totPreTaxAmt;
        private String totTaxAmt;

        private String taxPayerType;
        private String inputTaxCode;
        private String inputTaxRate;
        private String outputTaxCode;
        private String outputTaxRate;


    }


}
