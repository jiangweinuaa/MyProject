package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurStockOutQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurStockOutQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;

import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurStockOutQuery extends SPosBasicService<DCP_PurStockOutQueryReq, DCP_PurStockOutQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurStockOutQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail)
        {
        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
        }

    @Override
    protected TypeToken<DCP_PurStockOutQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurStockOutQueryReq>(){};
    }

    @Override
    protected DCP_PurStockOutQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurStockOutQueryRes();
    }

    @Override
    protected DCP_PurStockOutQueryRes processJson(DCP_PurStockOutQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurStockOutQueryRes res = this.getResponse();

        int totalRecords;		//总笔数
        int totalPages;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_PurStockOutQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getQData)
            {
                DCP_PurStockOutQueryRes.level1Elm level1Elm = res.new level1Elm();

                level1Elm.setStatus(oneData.get("STATUS").toString());
                level1Elm.setPStockOutNo(oneData.get("PSTOCKINNO").toString());
                level1Elm.setBillType(oneData.get("BILLTYPE").toString());
                level1Elm.setOrgNo(oneData.get("ORGNO").toString());
                level1Elm.setOrgName(oneData.get("ORGNAME").toString());
                level1Elm.setBDate(oneData.get("BDATE").toString());
                level1Elm.setAccountDate(oneData.get("ACCOUNTDATE").toString());
                level1Elm.setSupplierNo(oneData.get("SUPPLIERNO").toString());
                level1Elm.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                level1Elm.setPayType(oneData.get("PAYTYPE").toString());
                level1Elm.setPayOrgNo(oneData.get("PAYORGNO").toString());
                level1Elm.setPayOrgName(oneData.get("PAYORGNAME").toString());
                level1Elm.setBillDateNo(oneData.get("BILLDATENO").toString());
                level1Elm.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
                level1Elm.setInvoiceCode(oneData.get("INVOICECODE").toString());
                level1Elm.setInvoiceName(oneData.get("INVOICENAME").toString());
                level1Elm.setCurrency(oneData.get("CURRENCY").toString());
                level1Elm.setCurrencyName(oneData.get("CURRENCYNAME").toString());
                level1Elm.setSourceType(oneData.get("SOURCETYPE").toString());
                level1Elm.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());
                level1Elm.setReceivingNo(oneData.get("RECEIVINGNO").toString());
                level1Elm.setRDate(oneData.get("RDATE").toString());
                level1Elm.setWareHouse(oneData.get("WAREHOUSE").toString());
                level1Elm.setWareHouseName(oneData.get("WAREHOUSENAME").toString());
                level1Elm.setTotCqty(oneData.get("TOTCQTY").toString());
                level1Elm.setTotPqty(oneData.get("TOTPQTY").toString());
                level1Elm.setTotPurAmt(oneData.get("TOTPURAMT").toString());
                level1Elm.setAccountBy(oneData.get("ACCOUNTBY").toString());
                level1Elm.setAccountDateTime(oneData.get("ACCOUNTDATETIME").toString());
                level1Elm.setCancelBy(oneData.get("CANCELBY").toString());
                level1Elm.setCancelDateTime(oneData.get("CANCELDATETIME").toString());
                level1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
                level1Elm.setConfirmBy(oneData.get("CONFIRMBY").toString());
                level1Elm.setConfirmDateTime(oneData.get("CONFIRMDATETIME").toString());
                level1Elm.setCreateBy(oneData.get("CREATEBY").toString());
                level1Elm.setCreateByName(oneData.get("CREATEBYNAME").toString());
                level1Elm.setCreateDateTime(oneData.get("CREATEDATETIME").toString());
                level1Elm.setDepartID(oneData.get("DEPARTID").toString());
                level1Elm.setDepartName(oneData.get("DEPARTNAME").toString());
                level1Elm.setEmployeeID(oneData.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
                level1Elm.setIsLocation(oneData.get("ISLOCATION").toString());
                level1Elm.setModifyBy(oneData.get("MODIFYBY").toString());
                level1Elm.setModifyDateTime(oneData.get("MODIFYDATETIME").toString());
                level1Elm.setModifyByName(oneData.get("MODIFYBYNAME").toString());
                level1Elm.setOwnDeptID(oneData.get("OWNDEPTID").toString());
                level1Elm.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
                level1Elm.setOwnOpID(oneData.get("OWNOPID").toString());
                level1Elm.setOwnOpName(oneData.get("OWNOPNAME").toString());
                res.getDatas().add(level1Elm);
                level1Elm=null;

            }

        }
        else
        {
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurStockOutQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String langType = req.getLangType();
        DCP_PurStockOutQueryReq.levelElm request = req.getRequest();
        String status = request.getStatus();
        String orgNo = request.getOrgNo();
        String dateType = request.getDateType();
        String beginDate = request.getBeginDate();
        String endDate = request.getEndDate();
        String keyTxt = request.getKeyTxt();
        String billType = request.getBillType();

        //計算起啟位置
        int pageSize=req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        sqlbuf.append(" select * from (");
        sqlbuf.append(" select count(*) over () num,rownum rn,ac.* from (");
        sqlbuf.append(" select pstockin.* from (");
        sqlbuf.append(" "
                + " select distinct a.status,a.PSTOCKINNO,a.billType,b.ORGANIZATIONNO as orgNo ,b.org_name as orgName,to_char(a.bdate,'yyyy-MM-dd') bdate,to_char(a.ACCOUNT_DATE,'yyyy-MM-dd') as accountDate," +
                "   c.BIZPARTNERNO as supplierNo ,c.sname as supplierName,a.paytype,a.PAYORGNO,d.org_name as payOrgName,e.BILLDATENO,e.name as billDateDesc,f.PAYDATENO,f.name as payDateDesc, g.invoiceCode,g.INVOICE_NAME as invoicename,h.currency,h.name as currencyname," +
                "   a.sourceType,a.sourceBillNo,a.receivingNo,nvl(to_char(i.rdate,'yyyy-MM-dd'),'') rdate,j.warehouse,j.warehouse_name as warehousename ,a.TOT_CQTY as totcqty,a.TOT_PQTY as totpqty,a.TOT_PURAMT as totpuramt,k.islocation,a.employeeID,em0.name as employeename," +
                "   a.departID,dd0.DEPARTNAME as departName,em1.employeeno as createBy,em1.name as createByName ,a.CREATETIME as createDateTime, em2.employeeno as modifyBy,em2.name as modifyByName,a.LASTMODITIME as modifyDateTime, em3.employeeno as confirmBy,em3.name as confirmByName,a.CONFIRMTIME as confirmDateTime," +
                "   em5.employeeno as cancelBy,em5.name as cancelByName,a.CANCELTIME as cancelDateTime,em6.employeeno as ownOpID,em6.name as ownOpName,em4.employeeno as accountBy,em4.name as accountByName,a.ACCOUNTTIME as accountDateTime,dd1.DEPARTNO as ownDeptID,dd1.DEPARTNAME as ownDeptName  "
                + " from DCP_PURSTOCKIN a "
                + " left join dcp_org_lang b on a.eid=b.eid and b.ORGANIZATIONNO=a.PURORGNO and b.lang_type='"+langType+"' "
                + " left join DCP_BIZPARTNER c on a.eid=c.eid and a.organizationno=c.organizationno and a.supplier=c.BIZPARTNERNO and (c.biztype='1' or c.biztype='3') "
                + " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.PAYORGNO and d.lang_type='"+langType+"' "
                + " left join DCP_BILLDATE_LANG e on e.eid=a.eid and e.BILLDATENO=a.BILLDATENO and e.lang_type='"+langType+"' "
                + " left join DCP_PAYDATE_LANG f on f.eid=a.eid and f.PAYDATENO=a.PAYDATENO and f.lang_type='"+langType+"' "
                + " left join DCP_INVOICETYPE_LANG g on g.eid=a.eid and g.INVOICECODE=a.INVOICECODE and g.lang_type='"+langType+"' "
                + " left join DCP_CURRENCY_LANG h on h.eid =a.eid and h.CURRENCY=a.CURRENCY and h.lang_type='"+langType+"' "
                + " left join DCP_STOCKOUTNOTICE i on i.eid=a.eid and a.organizationno=i.organizationno and i.billno=a.receivingno "
                + " left join DCP_WAREHOUSE_LANG j on j.warehouse=i.warehouse and j.eid=a.eid and j.lang_type='"+langType+"' "
                + " left join DCP_WAREHOUSE k on k.eid=j.eid and k.warehouse=j.warehouse "
                + " left join DCP_EMPLOYEE em0 on em0.eid=a.eid and em0.employeeno=a.employeeid "
                + " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and dd0.DEPARTNO =a.DEPARTID "
                + " left join DCP_EMPLOYEE em1 on em1.eid=a.eid and em1.employeeno=a.CREATEOPID "
                + " left join DCP_EMPLOYEE em2 on em2.eid=a.eid and em2.employeeno=a.LASTMODIOPID "
                + " left join DCP_EMPLOYEE em3 on em3.eid=a.eid and em3.employeeno=a.CONFIRMBY "
                + " left join DCP_EMPLOYEE em4 on em4.eid=a.eid and em4.employeeno=a.ACCOUNTBY "
                + " left join DCP_EMPLOYEE em5 on em5.eid=a.eid and em5.employeeno=a.CANCELBY "
                + " left join DCP_EMPLOYEE em6 on em6.eid=a.eid and em6.employeeno=a.OWNOPID "
                + " left join DCP_DEPARTMENT_LANG dd1 on dd1.eid=a.eid and dd1.DEPARTNO =a.OWNDEPTID "
                + " where  a.eid= '"+ eId +"' and a.organizationno='"+organizationNO+"' "
                + " "
                + " ");

        if(!Check.Null(status)){
            sqlbuf.append(" a.status='"+status+"' ");
        }
        if(!Check.Null(orgNo)){
            sqlbuf.append(" and a.PURORGNO='"+orgNo+"' ");
        }
        if(!Check.Null(billType)){
            sqlbuf.append(" and a.billType='"+billType+"' ");
        }
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" and ( a.PSTOCKINNO like %"+keyTxt+"% " +
                    "or a.receivingno like %"+keyTxt+"% " +
                    "or a.SOURCEBILLNO like %"+keyTxt+"% " +
                    "or a.supplier like %"+keyTxt+"% " +
                    "or c.sname like %"+keyTxt+"% ) ");
        }

        if("bdate".equals(dateType)){
            if(!Check.Null(beginDate)){
                sqlbuf.append(" and to_char(a.bdate,'yyyy-MM-dd') >='"+beginDate+"' ");
            }
            if(!Check.Null(endDate)){
                sqlbuf.append(" and to_char(a.bdate,'yyyy-MM-dd') <='"+endDate+"' ");
            }
        }
        if("receiptDate".equals(dateType)){
            if(!Check.Null(beginDate)){
                sqlbuf.append(" and to_char(a.RDATE,'yyyy-MM-dd') >='"+beginDate+"' ");
            }
            if(!Check.Null(endDate)){
                sqlbuf.append(" and to_char(a.RDATE,'yyyy-MM-dd') <='"+endDate+"' ");
            }
        }
        if("pdate".equals(dateType)){
            if(!Check.Null(beginDate)){
                sqlbuf.append(" and to_char(a.ACCOUNT_DATE,'yyyy-MM-dd') >='"+beginDate+"' ");
            }
            if(!Check.Null(endDate)){
                sqlbuf.append(" and to_char(a.ACCOUNT_DATE,'yyyy-MM-dd') <='"+endDate+"' ");
            }
        }



        sqlbuf.append(" ) pstockin");


        sqlbuf.append(" "
        + " order by pstockin.PSTOCKINNO"
        + " ) ac"
        + " ) where  rn>"+startRow+" and rn<=" + (startRow+pageSize) + "  "
        + " ");

        return sqlbuf.toString();
    }

}



