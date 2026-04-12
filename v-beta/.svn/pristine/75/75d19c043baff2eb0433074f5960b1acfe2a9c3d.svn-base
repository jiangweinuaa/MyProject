package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurchaseApplyQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_PurchaseApplyQuery  extends SPosBasicService<DCP_PurchaseApplyQueryReq, DCP_PurchaseApplyQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PurchaseApplyQueryReq> getRequestType() {
        return new TypeToken<DCP_PurchaseApplyQueryReq>(){};
    }

    @Override
    protected DCP_PurchaseApplyQueryRes getResponseType() {
        return new DCP_PurchaseApplyQueryRes();
    }

    @Override
    protected DCP_PurchaseApplyQueryRes processJson(DCP_PurchaseApplyQueryReq req) throws Exception {
        DCP_PurchaseApplyQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            //算總頁數
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> row : getQData){
                DCP_PurchaseApplyQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setRDate(row.get("RDATE").toString());
                level1Elm.setTotPqty(row.get("TOTPQTY").toString());
                level1Elm.setTotAmt(row.get("TOTAMT").toString());
                level1Elm.setTotCqty(row.get("TOTCQTY").toString());
                level1Elm.setTotPurAmt(row.get("TOTPURAMT").toString());
                level1Elm.setEmployeeId(row.get("EMPLOYEEID").toString());
                level1Elm.setEmployeeName(row.get("EMPLOYEENAME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIMES").toString());
                level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setLastModiTime(row.get("LASTMODITIMES").toString());
                level1Elm.setSubmitOpId(row.get("SUBMITOPID").toString());
                level1Elm.setSubmitOpName(row.get("SUBMITOPNAME").toString());
                level1Elm.setSubmitTime(row.get("SUBMITTIMES").toString());
                level1Elm.setConfirmOpId(row.get("CONFIRMOPID").toString());
                level1Elm.setConfirmOpName(row.get("CONFIRMOPNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIMES").toString());
                level1Elm.setCancelOpId(row.get("CANCELOPID").toString());
                level1Elm.setCancelOpName(row.get("CANCELOPNAME").toString());
                level1Elm.setCancelTime(row.get("CANCELTIMES").toString());
                level1Elm.setCloseOpId(row.get("CLOSEOPID").toString());
                level1Elm.setCloseOpName(row.get("CLOSEOPNAME").toString());
                level1Elm.setCloseTime(row.get("CLOSETIMES").toString());

                res.getDatas().add(level1Elm);

            }

        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PurchaseApplyQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with purchaseApply as ("
                + " select distinct a.billno from DCP_PURCHASEAPPLY a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.billno like '%"+keyTxt+"%' "
                    + " ) ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and a.bdate<='"+endDate+"' ");
        }

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,e1.op_name as createopname,e2.op_name as lastModiOpName,c.name as employeename,d1.departname," +
                "e3.op_name as submitopname,e4.op_name as confirmopname,e5.op_name as closeopname,e6.op_name as cancelopname," +
                " to_char(a.CREATETIME,'yyyyMMdd HH:mm:ss') as createtimes,to_char(a.LASTMODITIME,'yyyyMMdd HH:mm:ss') as LASTMODITIMEs,to_char(a.SUBMITTIME,'yyyyMMdd HH:mm:ss') as SUBMITTIMEs,to_char(a.CONFIRMTIME,'yyyyMMdd HH:mm:ss') as CONFIRMTIMEs," +
                " to_char(a.CLOSETIME,'yyyyMMdd HH:mm:ss') as CLOSETIMEs,to_char(a.CANCELTIME,'yyyyMMdd HH:mm:ss') as CANCELTIMEs  "
                + " from DCP_PURCHASEAPPLY a"
                + " inner join purchaseApply b on a.billno=b.billno " +
                " left join dcp_employee c on c.eid=a.eid and c.employeeno=a.employeeid"
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e3 on e3.eid=a.eid and e3.opno=a.submitopid and e3.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e4 on e4.eid=a.eid and e4.opno=a.confirmopid and e4.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e5 on e5.eid=a.eid and e5.opno=a.closeopid and e5.lang_type='"+req.getLangType()+"' "
                + " left join PLATFORM_STAFFS_LANG e6 on e6.eid=a.eid and e6.opno=a.cancelopid and e6.lang_type='"+req.getLangType()+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


