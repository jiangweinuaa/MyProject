package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProductGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProductGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProductGroupQuery extends SPosBasicService<DCP_ProductGroupQueryReq, DCP_ProductGroupQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProductGroupQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProductGroupQueryReq> getRequestType() {
        return new TypeToken<DCP_ProductGroupQueryReq>(){};
    }

    @Override
    protected DCP_ProductGroupQueryRes getResponseType() {
        return new DCP_ProductGroupQueryRes();
    }

    @Override
    protected DCP_ProductGroupQueryRes processJson(DCP_ProductGroupQueryReq req) throws Exception {
        DCP_ProductGroupQueryRes res = this.getResponse();
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
                DCP_ProductGroupQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setPGroupNo(row.get("PGROUPNO").toString());
                level1Elm.setPGroupName(row.get("PGROUPNAME").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setDepartId(row.get("DEPARTID").toString());
                level1Elm.setDepartName(row.get("DEPARTNAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setCreatorID(row.get("CREATORID").toString());
                level1Elm.setCreatorName(row.get("CREATORNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setLastModifyID(row.get("LASTMODIFYID").toString());
                level1Elm.setLastModifyName(row.get("LASTMODIFYNAME").toString());
                level1Elm.setLastModifyTime(row.get("LASTMODIFYTIME").toString());
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
    protected String getQuerySql(DCP_ProductGroupQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with pgroup as ("
                + " select a.pgroupno from MES_PRODUCT_GROUP a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.pgroupno like '%"+keyTxt+"%' or a.pgroupname like '%"+keyTxt+"%'"
                    + " ) ");
        }


        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.pgroupno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.pgroupno asc) as rn,"
                + " a.pgroupno,a.pgroupname,a.memo,a.status,a.createOpId as creatorID ,e1.name as creatorName,a.lastModiOpId as lastModifyID,e2.name as lastModifyNAME,to_char(a.createTime,'yyyy-MM-dd HH:mm:ss') as createtime ,to_char(a.lastModiTime,'yyyy-MM-dd HH:mm:ss') as lastModifyTime"
                + ",a.departid,d1.departname as departname  "
                + " from MES_PRODUCT_GROUP a"
                + " inner join pgroup b on a.pgroupno=b.pgroupno "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.DEPARTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


