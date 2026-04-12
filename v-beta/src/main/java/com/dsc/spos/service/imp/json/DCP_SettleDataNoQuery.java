package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SettleDataNoQueryReq;
import com.dsc.spos.json.cust.res.DCP_SettleDataNoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SettleDataNoQuery extends SPosBasicService<DCP_SettleDataNoQueryReq, DCP_SettleDataNoQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SettleDataNoQueryReq req) throws Exception {
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
    protected TypeToken<DCP_SettleDataNoQueryReq> getRequestType() {
        return new TypeToken<DCP_SettleDataNoQueryReq>(){};
    }

    @Override
    protected DCP_SettleDataNoQueryRes getResponseType() {
        return new DCP_SettleDataNoQueryRes();
    }

    @Override
    protected DCP_SettleDataNoQueryRes processJson(DCP_SettleDataNoQueryReq req) throws Exception {
        DCP_SettleDataNoQueryRes res = this.getResponse();
        int totalRecords=0;
        int totalPages=0;

        if(req.getPageNumber()==0){
            req.setPageNumber(1);
        }
        if(req.getPageSize()==0){
            req.setPageSize(10);
        }
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
                DCP_SettleDataNoQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setOrganizationNo(row.get("ORGANIZATIONNO").toString());
                level1Elm.setOrganizationName(row.get("ORGANIZATIONNAME").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setBizType(row.get("BIZTYPE").toString());
                level1Elm.setBType(row.get("BTYPE").toString());
                level1Elm.setBillNo(row.get("BILLNO").toString());
                level1Elm.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                level1Elm.setBizPartnerName(row.get("BIZPARTNERNAME").toString());

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
    protected String getQuerySql(DCP_SettleDataNoQueryReq req) throws Exception {
        String eId = req.geteId();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with settledata as ("
                + " select a.billno,a.item " +
                " from DCP_SETTLEDATA a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.payorgno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_FEE_LANG e on e.eid=a.eid and e.fee=a.fee and e.lang_type='"+req.getLangType()+"'" +
                " left join dcp_bizpartner f on f.eid=a.eid and f.bIzpartnerno=a.bizpartnerno "
                + " where a.eid='"+eId+"' "
        );

        if(Check.NotNull(req.getRequest().getBeginDate())){
            sqlbuf.append(" and a.bdate>='"+req.getRequest().getBeginDate()+"' ");
        }
        if(Check.NotNull(req.getRequest().getEndDate())){
            sqlbuf.append(" and a.bdate<='"+req.getRequest().getEndDate()+"' ");
        }
        if(Check.NotNull(req.getRequest().getOrganizationNo())){
            sqlbuf.append(" and a.organizationno='"+req.getRequest().getOrganizationNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getPayOrgNo())){
            sqlbuf.append(" and a.payorgno='"+req.getRequest().getPayOrgNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getPayOrgName())){
            sqlbuf.append(" and c.org_name like '%"+req.getRequest().getPayOrgName()+"%' ");
        }

        if(Check.NotNull(req.getRequest().getBizPartnerNo())){
            sqlbuf.append(" and a.bizpartnerno='"+req.getRequest().getBizPartnerNo()+"' ");
        }
        if(Check.NotNull(req.getRequest().getBizPartnerName())){
            sqlbuf.append(" and f.sname like '%"+req.getRequest().getBizPartnerName()+"%' ");
        }


        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.billno,a.item ");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.bdate desc) as rn, a.* from ("
                + " select distinct a.billno,a.organizationno,b.org_name as ORGANIZATIONNAME,a.btype,a.bdate,a.biztype " +
                " ,a.bizpartnerno,f.sname as bizpartnername"

                + " from DCP_SETTLEDATA a"
                + " inner join settledata p on a.billno=p.billno and a.item=p.item "+
                " left join dcp_org_lang b on a.eid=b.eid and a.organizationno=b.organizationno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.payorgno and c.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join DCP_FEE_LANG e on e.eid=a.eid and e.fee=a.fee and e.lang_type='"+req.getLangType()+"'" +
                " left join dcp_bizpartner f on f.eid=a.eid and f.bIzpartnerno=a.bizpartnerno "
                +  " where a.eid='"+eId+"' " +
                ") a "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }

}


