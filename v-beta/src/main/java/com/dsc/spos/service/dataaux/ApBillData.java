package com.dsc.spos.service.dataaux;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;

import java.util.List;
import java.util.Map;

public class ApBillData {

    public void insertApBillFromDcpBankPay(List<DataProcessBean> dbs, List<Map<String, Object>> detail) {

        Map<String, Object> oneData = detail.get(0);
        //生成预收待抵单
        ColumnDataValue dcp_apBill = new ColumnDataValue();
        dcp_apBill.add("EID", DataValues.newString(oneData.get("EID").toString()));
        dcp_apBill.add("CORP", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("ACCOUNTID", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("APNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("PDATE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("ORGANIZATIONNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("APTYPE", DataValues.newString("10"));
        dcp_apBill.add("ACCEMPLOYEENO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("BIZPARTNERNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("RECEIVER", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("TASKID", DataValues.newString("8"));
        dcp_apBill.add("PAYDATENO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("PAYDUEDATE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("TAXCODE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("TAXRATE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("INCLTAX", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("APPLICANT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("EMPLOYEENO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("DEPARTNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("SOURCETYPE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("SOURCENO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("PENDOFFSETNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FEESUBJECTID", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("APSUBJECTID", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("GLNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("GRPPMTNO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("MEMO", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("PAYLIST", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("CURRENCY", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("EXRATE", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYBTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYREVAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYTATAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYBTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYREVAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYTATAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYPMTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYPMTAMT", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("LCYCURRENCY", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("FCYCURRENCY", DataValues.newString(oneData.get("CORP").toString()));
        dcp_apBill.add("APDEPARTNO", DataValues.newString(oneData.get("CORP").toString()));

        dbs.add(new DataProcessBean(DataBeans.getInsBean("DCP_APBILL", dcp_apBill)));

        for (Map<String, Object> oneDetail : detail) {
            ColumnDataValue dcp_apBillDetail = new ColumnDataValue();
            dcp_apBillDetail.add("EID", DataValues.newString(oneDetail.get("EID").toString()));
            dcp_apBillDetail.add("STATUS", DataValues.newString(oneDetail.get("EID").toString()));
            dcp_apBillDetail.add("ACCOUNTID", DataValues.newString(oneDetail.get("EID").toString()));
            dcp_apBillDetail.add("APNO", DataValues.newString(oneDetail.get("EID").toString()));


            dbs.add((new DataProcessBean(DataBeans.getInsBean("DCP_APBILL", dcp_apBillDetail))));

        }


    }

}
