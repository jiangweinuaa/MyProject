package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PrintTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PrintTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PrintTemplateQuery extends SPosBasicService<DCP_PrintTemplateQueryReq, DCP_PrintTemplateQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_PrintTemplateQueryReq req) throws Exception {
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
    protected TypeToken<DCP_PrintTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_PrintTemplateQueryReq>(){};
    }

    @Override
    protected DCP_PrintTemplateQueryRes getResponseType() {
        return new DCP_PrintTemplateQueryRes();
    }

    @Override
    protected DCP_PrintTemplateQueryRes processJson(DCP_PrintTemplateQueryReq req) throws Exception {
        DCP_PrintTemplateQueryRes res = this.getResponse();
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

            StringBuffer sJoinModularNo=new StringBuffer("");
            for (Map<String, Object> row : getQData){
                DCP_PrintTemplateQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setModularNo(row.get("MODULARNO").toString());
                level1Elm.setModularName(row.get("MODULARNAME").toString());
                level1Elm.setProName(row.get("PRONAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setOnSale(row.get("ONSALE").toString());
                level1Elm.setTotCqty("0");
                res.getDatas().add(level1Elm);

                sJoinModularNo.append(level1Elm.getModularNo()+",");

            }

            Map<String, String> mapModularNo=new HashMap<String, String>();
            mapModularNo.put("MODULARNO", sJoinModularNo.toString());

            String withasSql_modularno="";
            MyCommon cm=new MyCommon();
            withasSql_modularno=cm.getFormatSourceMultiColWith(mapModularNo);
            mapModularNo=null;

            String pringSql="with p AS ( " + withasSql_modularno + ") " +
                    " select * from DCP_MODULAR_PRINT a " +
                    " inner join p on p.modularno=a.modularno " +
                    " where a.eid='"+req.geteId()+"' ";
            List<Map<String, Object>> getQData_print=this.doQueryData(pringSql, null);
            res.getDatas().forEach(x->{
                List<Map<String, Object>> filterModular = getQData_print.stream().filter(y -> y.get("MODULARNO").toString().equals(x.getModularNo())).collect(Collectors.toList());
                x.setTotCqty(filterModular.size()+"");
            });

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
    protected String getQuerySql(DCP_PrintTemplateQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with modular as ("
                + " select a.modularno " +
                " from DCP_MODULAR a " +
                " inner join DCP_MODULAR_PRINT b on a.eid=b.eid and a.modularno=b.modularno "
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.modularno like '%"+keyTxt+"%' or a.CHSMSG like '%"+keyTxt+"%'  or a.CHTMSG like '%"+keyTxt+"%' "
                    + " ) ");
        }
      

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.modularno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.modularno desc) as rn,"
                + " a.modularno,a.CHSMSG as modularname,a.status,a.onSale,a.PRONAME "
                + " from DCP_MODULAR a"
                + " inner join modular b on a.modularno=b.modularno "
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


