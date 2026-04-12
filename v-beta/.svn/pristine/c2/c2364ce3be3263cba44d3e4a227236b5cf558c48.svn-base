package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_InterSettBillAccountReq;
import com.dsc.spos.json.cust.res.DCP_InterSettBillAccountRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_InterSettBillAccount extends SPosAdvanceService<DCP_InterSettBillAccountReq, DCP_InterSettBillAccountRes> {
    @Override
    protected void processDUID(DCP_InterSettBillAccountReq req, DCP_InterSettBillAccountRes res) throws Exception {

        String corp = req.getRequest().getCorp();
        //查询当前法人的关账日期
        String accSql = "select a.*,to_char(a.CLOSINGDATE,'yyyyMMdd') as CLOSINGDATE  from DCP_ACOUNT_SETTING a where a.eid='" + req.geteId() + "' and a.corp='" + corp + "'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if (CollectionUtils.isEmpty(accList)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的法人账套" + corp);
        }
        String closingDate = accList.get(0).get("APCLOSINGDATE").toString();
        String bDate = req.getRequest().getBillDate();
        if (DateFormatUtils.compareDate(closingDate, bDate) < 0) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据日期不得小于应收关账日");
        }

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件单据！");
        }
        String accountId = accList.get(0).get("ACCOUNTID").toString();

        Map<String, Boolean> distinct = new HashMap<>();
        distinct.put("EID", true);
        distinct.put("BILLNO", true);
        List<Map<String, Object>> masterData = MapDistinct.getMap(qData, distinct);

        for (Map<String, Object> oneMaster : masterData) {

            String arNo = oneMaster.get("ARNO").toString();
            String apNo = oneMaster.get("APNO").toString();

            Map<String, Object> condition = new HashMap<>();
            condition.put("EID", oneMaster.get("EID"));
            condition.put("BILLNO", oneMaster.get("BILLNO"));

            String newArNo = "";
            String newApNo = "";
            if ("1".equals(req.getRequest().getAccType()) || "2".equals(req.getRequest().getAccType())) {
                newArNo = getNormalNO(req, "QTYS");
            }
            if ("1".equals(req.getRequest().getAccType()) || "3".equals(req.getRequest().getAccType())) {
                newApNo = getNormalNO(req, "QTYF");
            }

            List<Map<String, Object>> detailData = MapDistinct.getWhereMap(qData, condition, true);

            if (StringUtils.isNotEmpty(newApNo)) {
                insertDcpApBill(newApNo, accountId, "18", detailData, req);

            }
            if (StringUtils.isNotEmpty(newArNo)) {
                insertDcpArBill(newArNo, accountId, "18", detailData, req);
            }

            ColumnDataValue updateCondition = new ColumnDataValue();
            updateCondition.add("EID", oneMaster.get("EID").toString());
            updateCondition.add("BILLNO", oneMaster.get("BILLNO").toString());
            ColumnDataValue dcp_interSettBill = new ColumnDataValue();
            if (StringUtils.isNotEmpty(newArNo)) {
                dcp_interSettBill.add("ARNO", newArNo);
            }
            if (StringUtils.isNotEmpty(newApNo)) {
                dcp_interSettBill.add("APNO", newApNo);
            }
            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_INTERSETTBILL", updateCondition, dcp_interSettBill)));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    private void insertDcpApBill(String arNo, String accountId, String arType, List<Map<String, Object>> qData, DCP_InterSettBillAccountReq req) {

    }


    private void insertDcpArBill(String arNo, String accountId, String arType, List<Map<String, Object>> qData, DCP_InterSettBillAccountReq req) {

        Map<String, Object> oneMaster = qData.get(0);

        ColumnDataValue dcp_arBill = new ColumnDataValue();
        dcp_arBill.add("EID", oneMaster.get("EID").toString());
        dcp_arBill.add("ACCOUNTID", DataValues.newString(accountId));
        dcp_arBill.add("ARNO", DataValues.newString(arNo));
        dcp_arBill.add("PDATE", DataValues.newString(req.getRequest().getBillDate()));
//            dcp_arBill.add("TASKID",DataValues.newString(oneMaster.get("TASKID").toString()));
        dcp_arBill.add("ARTYPE", DataValues.newString(arType));
        dcp_arBill.add("CORP", DataValues.newString(oneMaster.get("CORP").toString()));
        dcp_arBill.add("STATUS", DataValues.newString("1"));
        dcp_arBill.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getCorp()));
        dcp_arBill.add("ACCEMPLOYEENO", DataValues.newString(req.getEmployeeNo()));
        dcp_arBill.add("BIZPARTNERNO", DataValues.newString(oneMaster.get("BIZPARTNER2").toString()));
        dcp_arBill.add("RECEIVER", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("PAYDATENO", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("PAYDUEDATE", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("TAXCODE", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("TAXRATE", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("INCLTAX", DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
//        dcp_arBill.add("EMPLOYEENO",DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
//        dcp_arBill.add("DEPARTNO",DataValues.newString(oneMaster.get("EMPLOYEENO").toString()));
        dcp_arBill.add("SOURCETYPE", DataValues.newString("14"));
        dcp_arBill.add("SOURCENO", DataValues.newString(oneMaster.get("BILLNO").toString()));
//        dcp_arBill.add("PENDOFFSETNO",DataValues.newString(oneMaster.get("BILLNO").toString()));
        dcp_arBill.add("ARSUBJECTID", DataValues.newString(oneMaster.get("ARSUBJECTID").toString()));
        dcp_arBill.add("REVSUBJECT", DataValues.newString(oneMaster.get("REVSUBJECT").toString()));
//        dcp_arBill.add("GLNO",DataValues.newString(oneMaster.get("REVSUBJECT").toString()));
//        dcp_arBill.add("GRPPMTNO",DataValues.newString(oneMaster.get("REVSUBJECT").toString()));
//        dcp_arBill.add("MEMO",DataValues.newString(oneMaster.get("REVSUBJECT").toString()));
        dcp_arBill.add("CURRENCY", DataValues.newString(oneMaster.get("CURRENCY").toString()));
        dcp_arBill.add("EXRATE", DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("FCYBTAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("FCYTAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("FCYREVAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("FCYTATAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("LCYBTAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("LCYTAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("LCYREVAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//        dcp_arBill.add("LCYTATAMT",DataValues.newString(oneMaster.get("EXRATE").toString()));
//
        dcp_arBill.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        dcp_arBill.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_arBill.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILL", dcp_arBill)));
        for (Map<String, Object> oneDetail : qData) {
            ColumnDataValue dcp_arBillDetail = new ColumnDataValue();
            dcp_arBillDetail.add("EID", DataValues.newString(oneDetail.get("EID")));
            dcp_arBillDetail.add("STATUS", DataValues.newString("1"));
            dcp_arBillDetail.add("ARNO", DataValues.newString(arNo));
            dcp_arBillDetail.add("ITEM", DataValues.newString(oneDetail.get("ITEM")));
            dcp_arBillDetail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getCorp()));
            dcp_arBillDetail.add("SOURCETYPE", DataValues.newString("14"));
            dcp_arBillDetail.add("BIZPARTNERNO", DataValues.newString(oneDetail.get("BIZPARTNER2").toString()));
            dcp_arBillDetail.add("RECEIVER", DataValues.newString(oneDetail.get("BIZPARTNER2").toString()));
            dcp_arBillDetail.add("SOURCENO", DataValues.newString(oneDetail.get("BILLNO").toString()));
            dcp_arBillDetail.add("SOURCEITEM", DataValues.newString(oneDetail.get("ITEM").toString()));
            dcp_arBillDetail.add("SOURCEORG", DataValues.newString(oneDetail.get("ORGANIZATIONNO").toString()));
            dcp_arBillDetail.add("PLUNO", DataValues.newString(oneDetail.get("PLUNO").toString()));
            dcp_arBillDetail.add("SPEC", DataValues.newString(oneDetail.get("SPEC").toString()));
            dcp_arBillDetail.add("PRICEUNIT", DataValues.newString(oneDetail.get("PRICEUNIT").toString()));
            dcp_arBillDetail.add("QTY", DataValues.newString(oneDetail.get("QTY").toString()));
            dcp_arBillDetail.add("OOFNO", DataValues.newString(oneDetail.get("SOURCENO").toString()));
            dcp_arBillDetail.add("OOITEM", DataValues.newString(oneDetail.get("SOURCENOSEQ").toString()));
            dcp_arBillDetail.add("CATEGORY", DataValues.newString(oneDetail.get("CATEGORY").toString()));
            dcp_arBillDetail.add("ISGIFT", DataValues.newString(oneDetail.get("ISGIFT").toString()));
            dcp_arBillDetail.add("BSNO", DataValues.newString(oneDetail.get("BSNO").toString()));
            dcp_arBillDetail.add("TAXCODE", DataValues.newString(oneDetail.get("TAXCODE").toString()));
            dcp_arBillDetail.add("REVSUBJECT", DataValues.newString(oneDetail.get("REVSUBJECT").toString()));
            dcp_arBillDetail.add("ARSUBJECTID", DataValues.newString(oneDetail.get("ARSUBJECTID").toString()));
//            dcp_arBillDetail.add("TAXSUBJECTID",DataValues.newString(oneDetail.get("ARSUBJECTID").toString()));
            dcp_arBillDetail.add("DIRECTION", DataValues.newString(oneDetail.get("DIRECTION").toString()));
            dcp_arBillDetail.add("ISREVEST", DataValues.newString(oneDetail.get("ISREVEST").toString()));
            dcp_arBillDetail.add("FREECHARS1", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FREECHARS2", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FREECHARS3", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FREECHARS4", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FREECHARS5", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("MEMO", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("CURRENCY", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("BILLPRICE", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("EXRATE", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FCYBTAMT", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FCYTAMT", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("FCYTATAMT", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("PRICE", DataValues.newString(oneDetail.get("FREECHARS1").toString()));
            dcp_arBillDetail.add("PRICE", DataValues.newString(oneDetail.get("PRICE").toString()));
            dcp_arBillDetail.add("LCYBTAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("LCYTAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("LCYTATAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("INVOICEQTY", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("INVOICEAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("INVCRNCYBTAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));
            dcp_arBillDetail.add("INVCRNCYATAMT", DataValues.newString(oneDetail.get("LCYBTAMT").toString()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAIL", dcp_arBillDetail)));
        }

        ColumnDataValue dcp_arBillDetailSum = new ColumnDataValue();
        dcp_arBillDetailSum.add("EID", DataValues.newString(oneMaster.get("EID").toString()));
        dcp_arBillDetailSum.add("ACCOUNTID", DataValues.newString(accountId));
        dcp_arBillDetailSum.add("APNO", DataValues.newString(arNo));
        dcp_arBillDetailSum.add("ITEM", DataValues.newString(1));
        dcp_arBillDetailSum.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getCorp()));
        dcp_arBillDetailSum.add("SOURCETYPE", DataValues.newString("14"));
        dcp_arBillDetailSum.add("BIZPARTNERNO", DataValues.newString(oneMaster.get("BIZPARTNER2").toString()));
        dcp_arBillDetailSum.add("RECEIVER", DataValues.newString(oneMaster.get("BIZPARTNER2").toString()));
        dcp_arBillDetailSum.add("SOURCENO", DataValues.newString(oneMaster.get("BILLNO").toString()));
        dcp_arBillDetailSum.add("SOURCEITEM", DataValues.newString(oneMaster.get("ITEM").toString()));
        dcp_arBillDetailSum.add("SOURCEORG", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FEE", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("CATEGORY", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("ISGIFT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("TAXRATE", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FEESUBJECTID", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("ARSUBJECTID", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("TAXSUBJECTID", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("DIRECTION", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("ISREVEST", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FREECHARS1", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FREECHARS2", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FREECHARS3", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FREECHARS4", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FREECHARS5", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("CURRENCY", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("EXRATE", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FCYBTAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FCYTAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("FCYTATAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("LCYBTAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("LCYTAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));
        dcp_arBillDetailSum.add("LCYTATAMT", DataValues.newString(oneMaster.get("ORGANIZATIONNO").toString()));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_ARBILLDETAILSUM", dcp_arBillDetailSum)));

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_InterSettBillAccountReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_InterSettBillAccountReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_InterSettBillAccountReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_InterSettBillAccountReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_InterSettBillAccountReq> getRequestType() {
        return new TypeToken<DCP_InterSettBillAccountReq>() {
        };
    }

    @Override
    protected DCP_InterSettBillAccountRes getResponseType() {
        return new DCP_InterSettBillAccountRes();
    }

    @Override
    protected String getQuerySql(DCP_InterSettBillAccountReq req) throws Exception {

        StringBuilder querySql = new StringBuilder();
        querySql.append(" SELECT a.APNO,a.ARNO,a.BIZPARTNER1,a.BIZPARTNER2,b.*  ")
                .append(" FROM  DCP_INTERSETTBILL a")
                .append(" LEFT JOIN DCP_INTERSETTBILLDETAIL b on a.eid=b.eid and a.BILLNO=b.BILLNO ")
        ;

        if (StringUtils.isNotEmpty(req.getRequest().getEId())) {
            querySql.append(" WHERE a.eid='").append(req.getRequest().getEId()).append("'");
        } else {
            querySql.append(" WHERE a.eid='").append(req.geteId()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getBillNo())) {
            querySql.append(" and a.BILLNO='").append(req.getRequest().getBillNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getCorp())) {
            querySql.append(" and a.CORP='").append(req.getRequest().getCorp()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBizpartner1())) {
            querySql.append(" and a.BIZPARTNER1=' ").append(req.getRequest().getBizpartner1()).append("'");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBizpartner2())) {
            querySql.append(" and a.BIZPARTNER2=' ").append(req.getRequest().getBizpartner1()).append("'");
        }

        querySql.append(" and a.status = '1' ");

//        if (StringUtils.isNotEmpty(req.getRequest().getAccType())) {
//
//            if ("2".equals(req.getRequest().getAccType()) || "1".equals(req.getRequest().getAccType())) {
//                querySql.append(" and a.APNO is null ");
//            } else if ("3".equals(req.getRequest().getAccType())) {
//                querySql.append(" and a.ARNO is null ");
//            }
//        }


        return querySql.toString();
    }
}
