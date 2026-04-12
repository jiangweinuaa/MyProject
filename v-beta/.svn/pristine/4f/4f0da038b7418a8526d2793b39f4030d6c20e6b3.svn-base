package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DeliveryManQueryReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryManQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_DeliveryManQuery
 *    說明：配送员查询
 * 服务说明：配送员查询
 * @author wangzyc
 * @since  2021/4/23
 */
public class DCP_DeliveryManQuery extends SPosBasicService<DCP_DeliveryManQueryReq, DCP_DeliveryManQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_DeliveryManQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DeliveryManQueryReq> getRequestType() {
        return new TypeToken<DCP_DeliveryManQueryReq>(){};
    }

    @Override
    protected DCP_DeliveryManQueryRes getResponseType() {
        return new DCP_DeliveryManQueryRes();
    }

    @Override
    protected DCP_DeliveryManQueryRes processJson(DCP_DeliveryManQueryReq req) throws Exception {
        DCP_DeliveryManQueryRes res = null;
        res = this.getResponse();

        res.setDatas(new ArrayList<DCP_DeliveryManQueryRes.level1Elm>());
        int totalRecords = 0;								//总笔数
        int totalPages = 0;									//总页数

        try {
            
            String sql  = this.getQuerySql(req);
            List<Map<String, Object>> getDatas = this.doQueryData(sql, null);

            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("EID", true);
            condition1.put("OPNO", true);
            //调用过滤函数
            List<Map<String, Object>> getHeaders= MapDistinct.getMap(getDatas, condition1);

            if(!CollectionUtils.isEmpty(getHeaders)){
                Map<String, Object> oneData_Count = getHeaders.get(0);
                String num = oneData_Count.get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                //算總頁數
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                for (Map<String, Object> getHeader : getHeaders) {
                    DCP_DeliveryManQueryRes.level1Elm level1Elm = res.new level1Elm();
                    String opNo = getHeader.get("OPNO").toString();
                    String opName = getHeader.get("OPNAME").toString();
                    String status = getHeader.get("STATUS").toString();
                    String viewableDay = getHeader.get("VIEWABLEDAY").toString();
                    String phone = getHeader.get("PHONE").toString();

                    level1Elm.setOpNo(opNo);
                    level1Elm.setOpName(opName);
                    level1Elm.setPhone(phone);
                    level1Elm.setStatus(status);
                    level1Elm.setViewAbleDay(viewableDay);
                    level1Elm.setOrgList(new ArrayList<DCP_DeliveryManQueryRes.level2Elm>());
                    if(!CollectionUtils.isEmpty(getDatas)){
                        for (Map<String, Object> getData : getDatas) {
                            String opno = getData.get("OPNO").toString();
                            if(opno.equals(opNo)){
                                DCP_DeliveryManQueryRes.level2Elm level2Elm = res.new level2Elm();
                                String orgNo = getData.get("ORG").toString();
                                String org_name = getData.get("ORG_NAME").toString();
                                level2Elm.setOrg(orgNo);
                                level2Elm.setOrgName(org_name);
                                level1Elm.getOrgList().add(level2Elm);
                            }
                        }
                    }
                    res.getDatas().add(level1Elm);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_DeliveryManQueryReq req) throws Exception {
        DCP_DeliveryManQueryReq.level1Elm request = req.getRequest();
        String status = request.getStatus();
        String org = request.getOrg();
        String keyTxt = request.getKeyTxt();
        String sql = "";

        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();

        if(pageNumber ==0 || pageSize == 0 ){
            pageNumber = 1;
            pageSize = 99999;
        }

        //分页起始位置
        int startRow=(pageNumber-1) * pageSize;

        StringBuffer  sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT a.OPNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY A.OPNO) AS rn , a.EID, a.OPNO, a.OPNAME, a.STATUS, a.VIEWABLEDAY , a.PHONE, b.ORG, c.ORG_NAME " +
                "  FROM DCP_DELIVERYMAN a " +
                "  LEFT JOIN DCP_DELIVERYMAN_ORG b ON a.eid = b.EID AND a.OPNO = b.OPNO " +
                " LEFT JOIN DCP_ORG_LANG c ON a.EID = c.EID AND c.ORGANIZATIONNO = b.ORG AND c.LANG_TYPE = '"+req.getLangType()+"' " +
                " WHERE a.eid = '"+req.geteId()+"'");
        if(!Check.Null(status)){
            sqlbuf.append(" And  a.status = '"+status+"'");
        }
        if(!Check.Null(org)){
            sqlbuf.append(" AND a.OPNO IN( SELECT OPNO  FROM DCP_DELIVERYMAN_ORG WHERE org ='"+org+"')");
        }
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" AND (a.OPNO  LIKE '%%"+keyTxt+"%%' OR a.OPNAME LIKE '%%"+keyTxt+"%%' OR a.PHONE LIKE '%%"+keyTxt+"%%')");
        }
        sqlbuf.append(" ) WHERE rn > '"+startRow+"' AND rn <= '"+(pageSize+startRow)+"'");
        sql = sqlbuf.toString();
        return sql;
    }
}
