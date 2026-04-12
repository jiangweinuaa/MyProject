package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReconliationQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReconliationQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ReconliationQuery extends SPosBasicService<DCP_ReconliationQueryReq, DCP_ReconliationQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ReconliationQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ReconliationQueryReq> getRequestType() {
        return new TypeToken<DCP_ReconliationQueryReq>(){};
    }

    @Override
    protected DCP_ReconliationQueryRes getResponseType() {
        return new DCP_ReconliationQueryRes();
    }

    @Override
    protected DCP_ReconliationQueryRes processJson(DCP_ReconliationQueryReq req) throws Exception {
        DCP_ReconliationQueryRes res = this.getResponse();
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
                DCP_ReconliationQueryRes.Level1Elm level1Elm = res.new Level1Elm();

                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setReconNo(row.get("RECONNO").toString());
                level1Elm.setCorp(row.get("CORP").toString());
                level1Elm.setDataType(row.get("DATATYPE").toString());
                level1Elm.setBizPartnerNo(row.get("BIZPARTNERNO").toString());
                level1Elm.setBizPartnerName(row.get("BIZPARTNERNAME").toString());
                level1Elm.setCorpName(row.get("CORPNAME").toString());
                level1Elm.setEstReceExpDay(row.get("ESTRECEEXPDAY").toString());
                level1Elm.setCurrReconAmt(row.get("CURRRECONAMT").toString());
                level1Elm.setPaidReceAmt(row.get("PAIDRECEAMT").toString());
                level1Elm.setNotPaidReceAmt(row.get("NOTPAIDRECEAMT").toString());
                level1Elm.setIsInvoiceIncl(row.get("ISINVOICEINCL").toString());

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
    protected String getQuerySql(DCP_ReconliationQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();

        String bdate = req.getRequest().getBdate();
        String corp = req.getRequest().getCorp();
        String corpName = req.getRequest().getCorpName();
        String dataType = req.getRequest().getDataType();
        String bizPartnerNo = req.getRequest().getBizPartnerNo();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with reconliation as ("
                + " select a.reconno from DCP_RECONLIATION a " +
                " left join dcp_org_lang b on a.eid=b.eid and a.corp=b.organizationno and b.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+eId+"' "
        );

        if(Check.NotNull(bdate)){
            sqlbuf.append(" and a.bdate='"+bdate+"' ");
        }

        if(Check.NotNull(corp)){
            sqlbuf.append(" and a.corp='"+corp+"' ");
        }
        //if(Check.NotNull(corpName)){
        //    sqlbuf.append(" and b.org_name='"+corpName+"' ");
        //}
        if(Check.NotNull(dataType)){
            sqlbuf.append(" and a.dataType='"+dataType+"' ");
        }
        if(Check.NotNull(bizPartnerNo)){
            sqlbuf.append(" and a.bizPartnerNo='"+bizPartnerNo+"' ");
        }

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getBeginDate())){
            sqlbuf.append(" and a.BDATE>='"+ DateFormatUtils.getPlainDate(req.getRequest().getBeginDate()) +"' ");
        }
        if (StringUtils.isNotEmpty(req.getRequest().getEndDate())){
            sqlbuf.append(" and a.BDATE<='"+ DateFormatUtils.getPlainDate(req.getRequest().getEndDate()) +"' ");
        }

        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.RECONNO desc) as rn,"
                + " a.*,c.sname as bizpartnername,d.org_name as corpname  "
                + " from DCP_RECONLIATION a"
                + " inner join reconliation b on a.reconno=b.reconno " +
                " left join dcp_bizpartner c on c.eid=a.eid and c.BIZPARTNERNO=a.BIZPARTNERNO " +
                " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.corp and d.lang_type='"+req.getLangType()+"' "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}

