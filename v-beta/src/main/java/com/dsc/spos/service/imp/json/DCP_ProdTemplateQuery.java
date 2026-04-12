package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProdTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ProdTemplateQuery extends SPosBasicService<DCP_ProdTemplateQueryReq, DCP_ProdTemplateQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProdTemplateQueryReq req) throws Exception {
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
    protected TypeToken<DCP_ProdTemplateQueryReq> getRequestType() {
        return new TypeToken<DCP_ProdTemplateQueryReq>(){};
    }

    @Override
    protected DCP_ProdTemplateQueryRes getResponseType() {
        return new DCP_ProdTemplateQueryRes();
    }

    @Override
    protected DCP_ProdTemplateQueryRes processJson(DCP_ProdTemplateQueryReq req) throws Exception {
        DCP_ProdTemplateQueryRes res = this.getResponse();
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
                    DCP_ProdTemplateQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                    level1Elm.setTemplateId(row.get("TEMPLATEID").toString());
                    level1Elm.setTemplateName(row.get("TEMPLATENAME").toString());
                    level1Elm.setStatus(row.get("STATUS").toString());
                    level1Elm.setRestrictOrg(row.get("RESTRICTORG").toString());
                    level1Elm.setMemo(row.get("MEMO").toString());
                    level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                    level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                    level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                    level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                    level1Elm.setCreateTime(row.get("CREATETIME").toString());
                    level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                    level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                    level1Elm.setLastModiTime(row.get("LASTMODITIME").toString());
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
    protected String getQuerySql(DCP_ProdTemplateQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String restrictOrg = req.getRequest().getRestrictOrg();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with template as ("
                + " select a.templateid from DCP_PRODTEMPLATE a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.templateid like '%"+keyTxt+"%' or a.templatename like '%"+keyTxt+"%'"
                    + " ) ");
        }
        if(Check.NotNull(restrictOrg)){
            sqlbuf.append(" and a.restrictOrg='"+restrictOrg+"' ");
        }

        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.templateid");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.templateid,a.templateName,a.status,a.restrictOrg,a.memo,a.createOpId ,e1.op_name as createopname,a.lastModiOpId,e2.op_name as lastModiOpName,a.createTime,a.lastModiTime,a.CREATEDEPTID,d1.departname as createdeptname  "
                + " from DCP_PRODTEMPLATE a"
                + " inner join template b on a.templateid=b.templateid "
                + " left join PLATFORM_STAFFS_LANG e1 on e1.eid=a.eid and e1.opno=a.createOpId and e1.lang_type='"+req.getLangType()+"'"
                + " left join PLATFORM_STAFFS_LANG e2 on e2.eid=a.eid and e2.opno=a.lastModiOpId and e2.lang_type='"+req.getLangType()+"' "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


