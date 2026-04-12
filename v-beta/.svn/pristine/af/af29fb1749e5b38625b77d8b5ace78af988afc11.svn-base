package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_RouteDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_RouteDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_RouteDetailQuery extends SPosBasicService<DCP_RouteDetailQueryReq, DCP_RouteDetailQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_RouteDetailQueryReq req) throws Exception {
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
    protected TypeToken<DCP_RouteDetailQueryReq> getRequestType() {
        return new TypeToken<DCP_RouteDetailQueryReq>(){};
    }

    @Override
    protected DCP_RouteDetailQueryRes getResponseType() {
        return new DCP_RouteDetailQueryRes();
    }

    @Override
    protected DCP_RouteDetailQueryRes processJson(DCP_RouteDetailQueryReq req) throws Exception {
        DCP_RouteDetailQueryRes res = this.getResponse();
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<>());

        if (getQData != null && !getQData.isEmpty()) {
            for (Map<String, Object> row : getQData){
                DCP_RouteDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
                level1Elm.setRouteNo(row.get("ROUTENO").toString());
                level1Elm.setRouteName(row.get("ROUTENAME").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setMemo(row.get("MEMO").toString());
                level1Elm.setCreateOpId(row.get("CREATEOPID").toString());
                level1Elm.setCreateOpName(row.get("CREATEOPNAME").toString());
                level1Elm.setCreateDeptId(row.get("CREATEDEPTID").toString());
                level1Elm.setCreateDeptName(row.get("CREATEDEPTNAME").toString());
                level1Elm.setCreateTime(row.get("CREATETIME").toString());
                level1Elm.setLastModiOpId(row.get("LASTMODIOPID").toString());
                level1Elm.setLastModiOpName(row.get("LASTMODIOPNAME").toString());
                level1Elm.setLastModiTime(row.get("LASTMODITIME").toString());

                level1Elm.setDetail(new ArrayList<>());

                String detailSql="select a.routeno,a.routetype,a.code,a.name ,a.address,a.status,a.SORTING" +
                        " from MES_ROUTE_DETAIL a " +
                        " where a.eid='"+ req.geteId()+"' and a.routeno='"+req.getRequest().getRouteNo()+"'" +
                        " order by sorting ASC ";
                List<Map<String, Object>> getQData2=this.doQueryData(detailSql, null);
                for (Map<String, Object> row2 : getQData2){
                    DCP_RouteDetailQueryRes.Detail level2Elm = res.new Detail();
                    level2Elm.setSorting(row2.get("SORTING").toString());
                    level2Elm.setRouteType(row2.get("ROUTETYPE").toString());
                    level2Elm.setCode(row2.get("CODE").toString());
                    level2Elm.setName(row2.get("NAME").toString());
                    level2Elm.setAddress(row2.get("ADDRESS").toString());
                    level2Elm.setStatus(row2.get("STATUS").toString());
                    level1Elm.getDetail().add(level2Elm);
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
    protected String getQuerySql(DCP_RouteDetailQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String routeNo = req.getRequest().getRouteNo();
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append(" "
                + " select "
                + " a.routeno,a.routename,a.status,a.memo,a.createOpId ,e1.name as createopname,a.lastModiOpId,e2.name as lastModiOpName,a.createTime,a.lastModiTime,a.CREATEDEPTID,d1.departname as createdeptname  "
                + " from MES_ROUTE a"
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"'  and a.routeno='"+routeNo+"'"
                + " ");

        return sqlbuf.toString();
    }
}


