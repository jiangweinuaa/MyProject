package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_RouteQueryReq;
import com.dsc.spos.json.cust.res.DCP_RouteQueryRes;
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

public class DCP_RouteQuery extends SPosBasicService<DCP_RouteQueryReq, DCP_RouteQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_RouteQueryReq req) throws Exception {
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
    protected TypeToken<DCP_RouteQueryReq> getRequestType() {
        return new TypeToken<DCP_RouteQueryReq>(){};
    }

    @Override
    protected DCP_RouteQueryRes getResponseType() {
        return new DCP_RouteQueryRes();
    }

    @Override
    protected DCP_RouteQueryRes processJson(DCP_RouteQueryReq req) throws Exception {
        DCP_RouteQueryRes res = this.getResponse();
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

            StringBuffer sJoinno=new StringBuffer("");
            for (Map<String, Object> row : getQData){
                DCP_RouteQueryRes.level1Elm level1Elm = res.new level1Elm();
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
                level1Elm.setTotCqty("0");
                res.getDatas().add(level1Elm);
                sJoinno.append(level1Elm.getRouteNo()+",");

            }

            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("ROUTENO", sJoinno.toString());
            MyCommon cm=new MyCommon();
            String withasSql_mono=cm.getFormatSourceMultiColWith(mapOrder);
            if(withasSql_mono.length()>0){
                String detailSql="with p as ("+withasSql_mono+" ) " +
                        " select * " +
                        " from MES_ROUTE_DETAIL a " +
                        " inner join p on a.routeno=p.routeno " +
                        " ";
                List<Map<String, Object>> getQData_detail=this.doQueryData(detailSql, null);
                res.getDatas().forEach(x->{
                    String routeNo = x.getRouteNo();
                    List<Map<String, Object>> filterRoutes = getQData_detail.stream().filter(b -> b.get("ROUTENO").toString().equals(routeNo)).collect(Collectors.toList());
                    List<Map> collect = filterRoutes.stream().map(y -> {
                        Map map = new HashMap();
                        map.put("type",y.get("ROUTETYPE").toString());
                        map.put("code",y.get("CODE").toString());
                        return map;
                    }).distinct().collect(Collectors.toList());
                    x.setTotCqty(String.valueOf(collect.size()));
                });
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
    protected String getQuerySql(DCP_RouteQueryReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        String keyTxt = req.getRequest().getKeyTxt();
        StringBuffer sqlbuf=new StringBuffer();
        String status = req.getRequest().getStatus();
        //分页处理
        int pageSize=req.getPageSize();
        int startRow=(req.getPageNumber()-1) * pageSize;

        sqlbuf.append(""
                + " with route as ("
                + " select a.routeno from MES_ROUTE a"
                + " where a.eid='"+eId+"' "
        );
        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and (a.routeno like '%"+keyTxt+"%' or a.routename like '%"+keyTxt+"%'"
                    + " ) ");
        }
        if (!Check.Null(status)){
            sqlbuf.append(" and a.status='"+status+"' ");
        }

        sqlbuf.append(" group by a.routeno");
        sqlbuf.append(" )");


        sqlbuf.append(" "
                + " select * from ("
                + " select count(*) over () num,row_number() over (order by a.createtime desc) as rn,"
                + " a.routeno,a.routename,a.status,a.memo,a.createOpId ,e1.name as createopname,a.lastModiOpId,e2.name as lastModiOpName,a.createTime,a.lastModiTime,a.CREATEDEPTID,d1.departname as createdeptname  "
                + " from MES_ROUTE a"
                + " inner join route b on a.routeno=b.routeno "
                + " left join dcp_employee e1 on e1.eid=a.eid and e1.employeeno=a.createOpId "
                + " left join dcp_employee e2 on e2.eid=a.eid and e2.employeeno=a.lastModiOpId "
                + " left join dcp_department_lang d1 on d1.eid=a.eid and d1.departno=a.CREATEDEPTID and d1.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' "
                + " ) a where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize)+" order by a.rn "
                + " ");

        return sqlbuf.toString();
    }
}


