package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ApWrtOffQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApWrtOffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApWrtOffQuery extends SPosBasicService<DCP_ApWrtOffQueryReq, DCP_ApWrtOffQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApWrtOffQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ApWrtOffQueryReq> getRequestType() {
        return new TypeToken<DCP_ApWrtOffQueryReq>(){};
    }

    @Override
    protected DCP_ApWrtOffQueryRes getResponseType() {
        return new DCP_ApWrtOffQueryRes();
    }

    @Override
    protected DCP_ApWrtOffQueryRes processJson(DCP_ApWrtOffQueryReq req) throws Exception {
        DCP_ApWrtOffQueryRes res = this.getResponse();
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
                DCP_ApWrtOffQueryRes.DatasLevel datasLevel = res.new DatasLevel();
                datasLevel.setTaskId(row.get("TASKID").toString());
                datasLevel.setStatus(row.get("STATUS").toString());
                datasLevel.setAccountId(row.get("ACCOUNTID").toString());
                datasLevel.setAccountName(row.get("ACCOUNTNAME").toString());
                datasLevel.setWrtOffNo(row.get("WRTOFFNO").toString());
                datasLevel.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                datasLevel.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                datasLevel.setBDate(row.get("BDATE").toString());
                datasLevel.setFCYTATAmt(row.get("FCYTATAMT").toString());
                datasLevel.setGlNo(row.get("GLNO").toString());
                res.getDatas().add(datasLevel);

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
    protected String getQuerySql(DCP_ApWrtOffQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with wrtoff as ("
                + " select a.wrtoffno from DCP_APWRTOFF a"
                + " where a.eid='"+eId+"' "
        );


        if (Check.NotNull(req.getRequest().getAccountId())){
            sqlbuf.append(" and a.accountid='"+req.getRequest().getAccountId()+"' ");
        }

        if (!Check.Null(req.getRequest().getBeginDate())){
            sqlbuf.append(" and to_char(a.bdate,'yyyyMMdd')>='"+req.getRequest().getBeginDate()+"' ");
        }
        if (!Check.Null(req.getRequest().getEndDate())){
            sqlbuf.append(" and to_char(a.bdate,'yyyyMMdd')<='"+req.getRequest().getEndDate()+"' ");
        }
        if (!Check.Null(req.getRequest().getBizPartnerNo())){
            sqlbuf.append(" and a.bizpartner='"+req.getRequest().getBizPartnerNo()+"' ");
        }
        if (!Check.Null(req.getRequest().getAccountId())){
            sqlbuf.append(" and a.accountid='"+req.getRequest().getAccountId()+"' ");
        }
        if(Check.NotNull(req.getRequest().getWrtOffNo())){
            sqlbuf.append(" and a.wrtoffno='"+req.getRequest().getWrtOffNo()+"' ");
        }
        if (!Check.Null(req.getRequest().getStatus())){
            sqlbuf.append(" and a.status='"+req.getRequest().getStatus()+"' ");
        }

        sqlbuf.append(" group by a.wrtoffno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.*,c.sname as bizpartnername,d.accountname "
                + " from DCP_APWRTOFF a"
                + " inner join wrtoff b on a.wrtoffno=b.wrtoffno " +
                " left join dcp_bizpartner c on a.bizpartnerno=c.bizpartnerno and a.eid=c.eid " +
                " left join dcp_acount_setting d on d.accountid=a.accountid and a.eid=d.eid  "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


