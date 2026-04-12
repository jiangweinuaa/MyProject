package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_COAOpenQueryReq;
import com.dsc.spos.json.cust.res.DCP_COAOpenQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_COAOpenQuery extends SPosBasicService<DCP_COAOpenQueryReq, DCP_COAOpenQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_COAOpenQueryReq req) throws Exception {
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
    protected TypeToken<DCP_COAOpenQueryReq> getRequestType() {
        return new TypeToken<DCP_COAOpenQueryReq>(){};
    }

    @Override
    protected DCP_COAOpenQueryRes getResponseType() {
        return new DCP_COAOpenQueryRes();
    }

    @Override
    protected DCP_COAOpenQueryRes processJson(DCP_COAOpenQueryReq req) throws Exception {
        DCP_COAOpenQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        String sql="select * from dcp_coa a where a.eid='"+req.geteId()+"' " +
                " and a.accountid='"+req.getRequest().getAccountId()+"' " +
                " and a.coarefid='"+req.getRequest().getCoaRefID()+"' ";
        if(Check.NotNull(req.getRequest().getStatus())){
            sql+=" and a.status='"+req.getRequest().getStatus()+"' ";
        }
        List<Map<String, Object>> list = this.doQueryData(sql, null);

        if(list.size()>0){
            List<String> subjectTypeList = list.stream().map(x -> x.get("SUBJECTTYPE").toString()).distinct().collect(Collectors.toList());
            for (String subjectType : subjectTypeList){
                DCP_COAOpenQueryRes.Level1Elm level1Elm = res.new Level1Elm();
                level1Elm.setSubjectType(subjectType);
                level1Elm.setSubjectList(new ArrayList<>());

                List<Map<String, Object>> subjectList = list.stream().filter(x -> x.get("SUBJECTTYPE").toString().equals(subjectType)).collect(Collectors.toList());
                for (Map<String, Object> subject : subjectList){
                    DCP_COAOpenQueryRes.SubjectList sub = res.new SubjectList();
                    sub.setSubjectId(subject.get("SUBJECTID").toString());
                    sub.setSubjectName(subject.get("SUBJECTNAME").toString());
                    sub.setSubjectCat(subject.get("SUBJECTCAT").toString());
                    level1Elm.getSubjectList().add(sub);
                }

                res.getDatas().add(level1Elm);

            }
        }

        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_COAOpenQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String accountId = req.getRequest().getAccountId();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;


        return sqlbuf.toString();
    }
}