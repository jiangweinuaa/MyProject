package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_CategorySubjectQueryReq;
import com.dsc.spos.json.cust.res.DCP_CategorySubjectQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_CategorySubjectQuery extends SPosBasicService<DCP_CategorySubjectQueryReq, DCP_CategorySubjectQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_CategorySubjectQueryReq req) throws Exception {
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
    protected TypeToken<DCP_CategorySubjectQueryReq> getRequestType() {
        return new TypeToken<DCP_CategorySubjectQueryReq>(){};
    }

    @Override
    protected DCP_CategorySubjectQueryRes getResponseType() {
        return new DCP_CategorySubjectQueryRes();
    }

    @Override
    protected DCP_CategorySubjectQueryRes processJson(DCP_CategorySubjectQueryReq req) throws Exception {
        DCP_CategorySubjectQueryRes res = this.getResponse();
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
                DCP_CategorySubjectQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setAccountId(row.get("ACCOUNTID").toString());
                level1Elm.setCoaRefID(row.get("COAREFID").toString());
                level1Elm.setAccountName(row.get("ACCOUNTNAME").toString());

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
    protected String getQuerySql(DCP_CategorySubjectQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String accountId = req.getRequest().getAccountId();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        String anotherCondition=" and 1=1 ";
        if(Check.NotNull(accountId)){
            anotherCondition=" and a.accountid='"+accountId+"' ";
        }
        if(Check.NotNull(status)){
            anotherCondition=" and a.status='"+status+"' ";
        }


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.accountid desc) as rn, a.* from ("
                + " select distinct a.accountid,a.coarefid,a.status,b.account as accountname "
                + " from DCP_CATEGORYSUBJECT  a " +
                " LEFT JOIN DCP_ACOUNT_SETTING b on a.eid=b.eid and a.accountid=b.accountid "
                + " where a.eid='"+eId+"' " +anotherCondition+
                ")a"
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}