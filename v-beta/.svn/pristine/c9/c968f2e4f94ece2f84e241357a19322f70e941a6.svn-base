package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_MoQueryReq;
import com.dsc.spos.json.cust.res.DCP_MoQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_MoQuery extends SPosBasicService<DCP_MoQueryReq, DCP_MoQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_MoQueryReq req) throws Exception {
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
    protected TypeToken<DCP_MoQueryReq> getRequestType() {
        return new TypeToken<DCP_MoQueryReq>(){};
    }

    @Override
    protected DCP_MoQueryRes getResponseType() {
        return new DCP_MoQueryRes();
    }

    @Override
    protected DCP_MoQueryRes processJson(DCP_MoQueryReq req) throws Exception {
        DCP_MoQueryRes res = this.getResponse();
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
                DCP_MoQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setBDate(row.get("BDATE").toString());
                level1Elm.setPDate(row.get("PDATE").toString());
                level1Elm.setMoNo(row.get("MONO").toString());
                level1Elm.setPGroupNo(row.get("PGROUPNO").toString());
                level1Elm.setPGroupName(row.get("PGROUPNAME").toString());
                level1Elm.setLoadDocNo(row.get("LOADDOCNO").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreateBy(row.get("CREATEBY").toString());
                level1Elm.setCreateByName(row.get("CREATEBYNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setModifyBy(row.get("MODIFYBY").toString());
                level1Elm.setModifyByName(row.get("MODIFYBYNAME").toString());
                level1Elm.setModifyTime(row.get("MODIFYTIME").toString());
                level1Elm.setConfirmBy(row.get("CONFIRMBY").toString());
                level1Elm.setConfirmByName(row.get("CONFIRMBYNAME").toString());
                level1Elm.setConfirmTime(row.get("CONFIRMTIME").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setOType(row.get("OTYPE").toString());
                level1Elm.setOfNo(row.get("OFNO").toString());
                level1Elm.setSourceMoNo(row.get("SOURCEMONO").toString());
                level1Elm.setProdType(row.get("PRODTYPE").toString());
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
    protected String getQuerySql(DCP_MoQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();


        String dateType = req.getRequest().getDateType();
        String status = req.getRequest().getStatus();
        String beginDate = req.getRequest().getBeginDate();
        String endDate = req.getRequest().getEndDate();
        String pGroupNo = req.getRequest().getPGroupNo();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with mo as ("
                + " select a.mono from mes_mo a"
                + " where a.eid='"+eId+"' and a.organizationno='"+ req.getOrganizationNO()+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.mono like '%"+keyTxt+"%' "
                    + " ) ");
        }
        if(Check.NotNull(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        if (!Check.Null(pGroupNo)){
            sqlbuf.append(" and a.pGroupNo='"+pGroupNo+"' ");
        }
        if(Check.NotNull(dateType)){
            if("0".equals(dateType)){
                if(Check.NotNull(beginDate)){
                    sqlbuf.append(" and a.bdate>='"+beginDate+"' ");
                }
                if(Check.NotNull(endDate)){
                    sqlbuf.append(" and a.bdate<='"+endDate+"' ");
                }
            }
            if("1".equals(dateType)){
                if(Check.NotNull(beginDate)){
                    sqlbuf.append(" and a.pdate>='"+beginDate+"' ");
                }
                if(Check.NotNull(endDate)){
                    sqlbuf.append(" and a.pdate<='"+endDate+"' ");
                }
            }
        }

        sqlbuf.append(" group by a.mono");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.bdate,a.status,a.pdate,a.mono,a.memo,a.PGROUPNo,c.PGROUPNAME,a.LOAD_DOCNO as loadDocNo,a.otype,a.ofno," +
                " a.sourceMoNo,a.prodType,a.CREATEOPID as createby ,e1.name as createbyname,a.LASTMODIOPID as modifyby,e2.name as modifybyname,a.CREATETIME as createTime,a.LASTMODITIME as modifyTime," +
                " a.departId,d1.departname as departNAME,a.ACCOUNTOPID as confirmby,a.ACCOUNTTIME as confirmtime,e3.name as confirmbyname  "
                + " from mes_mo a"
                + " inner join mo b on a.mono=b.mono "
                + " left join MES_PRODUCT_GROUP c on c.eid=a.eid and a.pgroupno=c.PGROUPNO "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.CREATEOPID "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.LASTMODIOPID "
                + " left join dcp_employee e3 on e3.eid=a.eid and e3.employeeno=a.ACCOUNTOPID "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.departid and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


