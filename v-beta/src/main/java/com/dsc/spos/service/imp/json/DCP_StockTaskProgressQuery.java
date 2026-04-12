package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_StockTaskProgressQueryReq;
import com.dsc.spos.json.cust.res.DCP_StockTaskProgressQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_StockTaskProgressQuery  extends SPosBasicService<DCP_StockTaskProgressQueryReq, DCP_StockTaskProgressQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_StockTaskProgressQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockTaskProgressQueryReq> getRequestType() {
        return new TypeToken<DCP_StockTaskProgressQueryReq>(){};
    }

    @Override
    protected DCP_StockTaskProgressQueryRes getResponseType() {
        return new DCP_StockTaskProgressQueryRes();
    }

    @Override
    protected DCP_StockTaskProgressQueryRes processJson(DCP_StockTaskProgressQueryReq req) throws Exception {
        //查詢條件
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String langType = req.getLangType();
        String stockTaskNo = req.getRequest().getStockTaskNo();

        DCP_StockTaskProgressQueryRes res = null;
        res = this.getResponse();
        res.setDatas(new ArrayList<>());

        DCP_StockTaskProgressQueryRes.Level1Elm lv1 = res.new Level1Elm();
        lv1.setStockTaskNo(stockTaskNo);
        lv1.setDetail(new ArrayList<>());

        BigDecimal completeRate = new BigDecimal(0);
        BigDecimal totCqty = new BigDecimal(0);
        BigDecimal totTaskQty = new BigDecimal(0);
        BigDecimal totPendingTaskQty = new BigDecimal(0);
        BigDecimal totProcessTaskQty = new BigDecimal(0);
        BigDecimal totCompleteTaskQty = new BigDecimal(0);

        String detailSql="select * from DCP_STOCKTASK_DETAIL a where a.eid='"+eId+"' " +
                "and a.stocktaskno='"+stockTaskNo+"' ";
        List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
        totCqty=new BigDecimal(detailList.size());

        String pdSql="select a.subtaskno,b.status,a.organizationno,c.org_name as organizationname,d.warehouse_name as warehousename,a.warehouse,a.SUBTASKNO,e.STOCKTAKENO,b.tot_pqty as sumqty,b.tot_amt as sumamt,b.TOT_DISTRIAMT as sumdistriamt  " +
                "from DCP_STOCKTASK_ORG a " +
                " left join DCP_STOCKTAKE b on b.eid=a.eid and b.stocktakeno=a.stocktakeno and a.organizationno=b.organizationno " +
                " left join dcp_org_lang c on c.eid=a.eid and c.organizationno=a.organizationno and c.lang_type='"+langType+"' " +
                " left join dcp_warehouse_lang d on d.eid=a.eid and d.warehouse=a.warehouse and d.lang_type='"+langType+"' " +
                " left join DCP_STOCKTAKE e on e.ofno=a.subtaskno and a.eid=e.eid " +
                " where a.eid='"+eId+"' and a.stocktaskno='"+stockTaskNo+"'";

        List<Map<String, Object>> list = this.doQueryData(pdSql, null);
        if(CollUtil.isNotEmpty(list)){
            totTaskQty=new BigDecimal(list.size());
            List<Map<String,String>> orgList = list.stream().map(x -> {
                Map<String,String> map=new HashMap();
                map.put("org",x.get("ORGANIZATIONNO").toString());
                map.put("orgName",x.get("ORGANIZATIONNAME").toString());
                return map;
                    }

            ).distinct().collect(Collectors.toList());
            for (Map<String,String> orgMap : orgList) {
                DCP_StockTaskProgressQueryRes.Detail detail = res.new Detail();
                detail.setOrganizationNo(orgMap.get("org"));
                detail.setOrganizationName(orgMap.get("orgName"));
                detail.setWarehouseList(new ArrayList<>());

                BigDecimal totSubTaskQty=new BigDecimal(0);
                BigDecimal completeQty=new BigDecimal(0);
                List<Map<String, Object>> warehouseList = list.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(detail.getOrganizationNo())).distinct().collect(Collectors.toList());
                totSubTaskQty=new BigDecimal(warehouseList.size());
                for (Map<String, Object> warehouseMap : warehouseList) {
                    DCP_StockTaskProgressQueryRes.WarehouseList warehouse = res.new WarehouseList();
                    warehouse.setSubTaskNo(warehouseMap.get("SUBTASKNO").toString());
                    warehouse.setStatus(warehouseMap.get("STATUS").toString());
                    warehouse.setStockTakeNo(warehouseMap.get("STOCKTAKENO").toString());
                    warehouse.setWarehouse(warehouseMap.get("WAREHOUSE").toString());
                    warehouse.setWarehouseName(warehouseMap.get("WAREHOUSENAME").toString());
                    warehouse.setSumQty(warehouseMap.get("SUMQTY").toString());
                    warehouse.setSumAmt(warehouseMap.get("SUMAMT").toString());
                    warehouse.setSumDistriAmt(warehouseMap.get("SUMDISTRIAMT").toString());
                    detail.getWarehouseList().add(warehouse);
                    if(warehouse.getStatus().equals("0")){
                        totPendingTaskQty=totPendingTaskQty.add(new BigDecimal(1));
                    }else if (warehouse.getStatus().equals("1")){
                        totProcessTaskQty=totProcessTaskQty.add(new BigDecimal(1));
                    }
                    else if(warehouse.getStatus().equals("2")){
                        completeQty=completeQty.add(new BigDecimal(1));
                        totCompleteTaskQty=totCompleteTaskQty.add(new BigDecimal(1));
                    }
                }
                detail.setTotSubTaskQty(totSubTaskQty.toString());
                if(totSubTaskQty.compareTo(new BigDecimal(0))==0){
                    detail.setCompleteRate("0");
                }else{
                    BigDecimal completeRateDetail = completeQty.divide(totSubTaskQty, 2);
                    detail.setCompleteRate(completeRateDetail.multiply(new BigDecimal(1)).toString());
                }

                lv1.getDetail().add(detail);

            }

        }

        if(totTaskQty.compareTo(new BigDecimal(0))!=0){
            completeRate = totCompleteTaskQty.divide(totTaskQty, 2);
        }

        lv1.setTotCqty(totCqty.toString());
        lv1.setTotTaskQty(totTaskQty.toString());
        lv1.setTotPendingTaskQty(totPendingTaskQty.toString());
        lv1.setTotProcessTaskQty(totProcessTaskQty.toString());
        lv1.setTotCompleteTaskQty(totCompleteTaskQty.toString());
        lv1.setCompleteRate(completeRate.multiply(new BigDecimal(1)).toString());

        res.getDatas().add(lv1);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_StockTaskProgressQueryReq req) throws Exception {
        return null;
    }

}
