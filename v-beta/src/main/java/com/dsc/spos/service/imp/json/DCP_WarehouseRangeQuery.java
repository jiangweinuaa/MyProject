package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_WarehouseRangeQueryReq;
import com.dsc.spos.json.cust.res.DCP_WarehouseRangeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WarehouseRangeQuery extends SPosBasicService<DCP_WarehouseRangeQueryReq, DCP_WarehouseRangeQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_WarehouseRangeQueryReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_WarehouseRangeQueryReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_WarehouseRangeQueryReq>() {
        };
    }

    @Override
    protected DCP_WarehouseRangeQueryRes getResponseType() {

        return new DCP_WarehouseRangeQueryRes();
    }

    @Override
    protected DCP_WarehouseRangeQueryRes processJson(DCP_WarehouseRangeQueryReq req) throws Exception {

        String eId = req.geteId();
        String langType = req.getLangType();
        DCP_WarehouseRangeQueryReq.LevelElm request = req.getRequest();
        if(Check.Null(request.getOrganizationNo())){
            request.setOrganizationNo(req.getOrganizationNO());
        }
        String organizationNo = request.getOrganizationNo();
        String warehouseNo = request.getWarehouseNo();

        DCP_WarehouseRangeQueryRes res = this.getResponseType();
        //查詢資料
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                                           //总页数
        res.setDatas(new ArrayList<DCP_WarehouseRangeQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false) {
            for (Map<String, Object> oneData : getQData) {
                DCP_WarehouseRangeQueryRes.level1Elm oneLv1 = res.new level1Elm();
                // 取得第一層資料庫搜尋結果
                String warehouse = oneData.get("WAREHOUSENO").toString();
                String warehouseName = oneData.get("WAREHOUSENAME").toString();

                // 處理調整回傳值
                oneLv1.setWarehouseNo(warehouse);
                oneLv1.setWarehouseName(warehouseName);
                oneLv1.setOrganizationNo(oneData.get("ORGANIZATIONNO").toString());
                oneLv1.setOrganizationName(oneData.get("ORGANIZATIONNAME").toString());
                oneLv1.setRangeList(new ArrayList<>());

                String rangeSql="select a.type,a.code,a.status,case when a.type='1' then c.category_name else b.plu_name end as name,a.createopid,d.name as createopname,to_char(a.createtime,'yyyy-MM-dd HH:mm:ss') as createtime," +
                        " a.LASTMODIOPID,e.name as LASTMODIOPname,to_char(a.LASTMODITIME,'yyyy-MM-dd HH:mm:ss') as LASTMODITIME " +
                        " from DCP_WAREHOUSE_RANGE a " +
                        " left join dcp_goods_lang b on a.eid=b.eid and  a.code=b.pluno and a.type='2' and b.lang_type='"+langType+"'" +
                        " left join dcp_category_lang c on a.eid=c.eid and a.code=c.category and c.lang_type='"+langType+"'  " +
                        " left join dcp_employee d on d.eid=a.eid and d.employeeno =a.createopid " +
                        " left join dcp_employee e on e.eid=a.eid and e.employeeno =a.LASTMODIOPID " +
                        " where a.eid='"+eId+"' and a.organizationno='"+organizationNo+"' " +
                        " AND a.warehouse='"+warehouseNo+"' ";
                List<Map<String, Object>> rangeData = this.doQueryData(rangeSql, null);
                if(CollUtil.isNotEmpty(rangeData)){
                    for (Map<String, Object> oneRange : rangeData){
                        DCP_WarehouseRangeQueryRes.RangeList range = res.new RangeList();
                        range.setType(oneRange.get("TYPE").toString());
                        range.setCode(oneRange.get("CODE").toString());
                        range.setName(oneRange.get("NAME").toString());
                        range.setStatus(oneRange.get("STATUS").toString());
                        range.setCreateOpId(oneRange.get("CREATEOPID").toString());
                        range.setCreateOpName(oneRange.get("CREATEOPNAME").toString());
                        range.setCreateTime(oneRange.get("CREATETIME").toString());
                        range.setLastModiOpId(oneRange.get("LASTMODIOPID").toString());
                        range.setLastModiOpName(oneRange.get("LASTMODIOPNAME").toString());
                        range.setLastModiTime(oneRange.get("LASTMODITIME").toString());
                        oneLv1.getRangeList().add(range);
                    }
                }

                res.getDatas().add(oneLv1);
                oneLv1 = null;
            }
        }
        res.setSuccess(true);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {


    }

    @Override
    protected String getQuerySql(DCP_WarehouseRangeQueryReq req) throws Exception {

        //查詢條件
        String eId = req.geteId();
        String langType = req.getLangType();
        String warehouseNo = req.getRequest().getWarehouseNo();
        String organizationNo = req.getRequest().getOrganizationNo();

        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        sqlbuf.append(" "
                + " select a.warehouse as warehouseno,b.warehouse_name as warehousename,a.organizationno,c.ORG_NAME as organizationname "
                + " from dcp_warehouse a"
                + " left join dcp_warehouse_lang b "
                + "  on a.eid=b.eid and a.organizationno=b.organizationno and a.warehouse=b.warehouse and b.lang_type='" + langType + "' "
                + " left join dcp_org_lang c on a.eid=c.eid and a.organizationno=c.ORGANIZATIONNO and c.LANG_TYPE='" + langType + "' "
                + " where a.eid='" + eId + "' and a.organizationno='"+organizationNo+"' and a.warehouse='"+warehouseNo+"' "
                + " ");

        sql = sqlbuf.toString();
        return sql;
    }
}
