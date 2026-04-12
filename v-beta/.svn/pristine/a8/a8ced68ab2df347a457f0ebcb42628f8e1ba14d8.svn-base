package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_ApPrePayQueryReq;
import com.dsc.spos.json.cust.res.DCP_ApPrePayQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ApPrePayQuery   extends SPosBasicService<DCP_ApPrePayQueryReq, DCP_ApPrePayQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ApPrePayQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ApPrePayQueryReq> getRequestType() {
        return new TypeToken<DCP_ApPrePayQueryReq>(){};
    }

    @Override
    protected DCP_ApPrePayQueryRes getResponseType() {
        return new DCP_ApPrePayQueryRes();
    }

    @Override
    protected DCP_ApPrePayQueryRes processJson(DCP_ApPrePayQueryReq req) throws Exception {
        DCP_ApPrePayQueryRes res = this.getResponse();

        String accSql="select * from dcp_acount_setting where eid='"+req.geteId()+"' and accountid='"+req.getRequest().getAccountId()+"'" ;
        List<Map<String, Object>> list = this.doQueryData(accSql, null);
        if(CollUtil.isEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "accountId不存在");
        }
        String accountName=list.get(0).get("ACCOUNT").toString();

        String querySql = this.getQuerySql(req);
        List<Map<String, Object>> aplist = this.doQueryData(querySql, null);

        DCP_ApPrePayQueryRes.level1Elm level1Elm = res.new level1Elm();

        level1Elm.setAccountId(req.getRequest().getAccountId());
        level1Elm.setAccount(accountName);
        level1Elm.setTaskId(req.getRequest().getTaskId());
        level1Elm.setApList(new ArrayList<>());

        BigDecimal totfCYTATAmt = BigDecimal.ZERO;
        BigDecimal totfCYRevAmt = BigDecimal.ZERO;
        BigDecimal totunPaidAmt = BigDecimal.ZERO;

        if(aplist.size()>0){
            for (Map<String, Object> row : aplist){
                DCP_ApPrePayQueryRes.ApList apInfo = res.new ApList();
                apInfo.setApNo(row.get("APNO").toString());
                apInfo.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                apInfo.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                apInfo.setPDate(row.get("PDATE").toString());
                apInfo.setFCYTATAmt(row.get("FCYTATAMT").toString());
                apInfo.setFCYRevAmt(row.get("FCYREVAMT").toString());
                apInfo.setUnPaidAmt(row.get("UNPAIDAMT").toString());
                apInfo.setStatus(row.get("STATUS").toString());
                apInfo.setSourceNo(row.get("SOURCENO").toString());
                level1Elm.getApList().add(apInfo);

                totfCYTATAmt=totfCYTATAmt.add(new BigDecimal(apInfo.getFCYTATAmt()));
                totfCYRevAmt=totfCYRevAmt.add(new BigDecimal(apInfo.getFCYRevAmt()));
                totunPaidAmt=totunPaidAmt.add(new BigDecimal(apInfo.getUnPaidAmt()));

            }
        }

        level1Elm.setTotfCYRevAmt(totfCYRevAmt.toString());
        level1Elm.setTotfCYTATAmt(totfCYTATAmt.toString());
        level1Elm.setTotunPaidAmt(totunPaidAmt.toString());

        res.setDatas(level1Elm);

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_ApPrePayQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        String status = req.getRequest().getStatus();
        String accountId = req.getRequest().getAccountId();
        String taskId = req.getRequest().getTaskId();
        String keyTxt = req.getRequest().getKeyTxt();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();


        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with apbill as ("
                + " select a.apno from dcp_apbill a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.apno like '%"+keyTxt+"%'  "
                    + " ) ");
        }

        if(Check.NotNull(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if(Check.NotNull(taskId)){
            sqlbuf.append(" and a.taskid='"+taskId+"' ");
        }
        if(Check.NotNull(accountId)){
            sqlbuf.append(" and a.accountid='"+accountId+"' ");
        }
        if(Check.NotNull(beginDate)){
            sqlbuf.append(" and a.pdate>='"+beginDate+"' ");
        }
        if(Check.NotNull(endDate)){
            sqlbuf.append(" and a.pdate<='"+endDate+"' ");
        }

        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.CREATE_DATE desc,a.create_time desc ) as rn,"
                + " a.*,c.sname as bizpartnername  "
                + " from dcp_apbill a"
                + " inner join apbill b on a.apno=b.apno " +
                " left join dcp_bizpartner c on c.eid=a.eid and c.bizpartnerno=a.bizpartnerno "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }

}


