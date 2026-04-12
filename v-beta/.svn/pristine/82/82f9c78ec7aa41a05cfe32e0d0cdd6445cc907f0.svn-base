package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurchaseApplyTrackReq;
import com.dsc.spos.json.cust.res.DCP_PurchaseApplyTrackRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurchaseApplyTrack  extends SPosBasicService<DCP_PurchaseApplyTrackReq, DCP_PurchaseApplyTrackRes> {

    @Override
    protected boolean isVerifyFail(DCP_PurchaseApplyTrackReq req) throws Exception {
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
    protected TypeToken<DCP_PurchaseApplyTrackReq> getRequestType() {
        return new TypeToken<DCP_PurchaseApplyTrackReq>(){};
    }

    @Override
    protected DCP_PurchaseApplyTrackRes getResponseType() {
        return new DCP_PurchaseApplyTrackRes();
    }

    @Override
    protected DCP_PurchaseApplyTrackRes processJson(DCP_PurchaseApplyTrackReq req) throws Exception {
        DCP_PurchaseApplyTrackRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        DCP_PurchaseApplyTrackRes.Level1Elm level1Elm = res.new Level1Elm();
        //单头查询
        BigDecimal totCqty=new BigDecimal(0);
        BigDecimal totPqty=new BigDecimal(0);
        BigDecimal totPurQty=new BigDecimal(0);
        BigDecimal totReceiveQty=new BigDecimal(0);
        BigDecimal totStockInQty=new BigDecimal(0);

        String detailSql="select a.*,b.plu_name as pluname,c.uname as punitname,d.featurename" +
                " from dcp_purchaseapply_detail a " +
                " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang c on a.eid=c.eid and a.punit=c.unit and c.lang_type='"+req.getLangType()+"' " +
                " LEFT JOIN dcp_goods_feature_lang d on a.eid=d.eid and a.pluno=d.pluno and d.featureno=a.featureno and d.lang_type='"+req.getLangType()+"' " +
                " where a.eid='"+req.geteId()+"'  and a.billno='"+req.getRequest().getBillNo()+"'";//and a.organizationno='"+req.getOrganizationNO()+"'
        List<Map<String, Object>> detailList = this.doQueryData(detailSql,null);

        String purSql="" +
                " select a.*,b.sname as suppliername,c.uname as punitname from ( " +
                " select a.eid,d.PURORDERNO,c.oitem,d.item,d.item2,sum(nvl(d.RECEIVEQTY,0)) as RECEIVEQTY,sum(nvl(d.STOCKINQTY,0)) as STOCKINQTY,sum(nvl(d.PURQTY,0)) as PURQTY," +
                " a.status,b.PURUNIT as punit,d.ARRIVALDATE,a.supplier " +
                " from DCP_PURORDER a" +
                " inner join DCP_PURORDER_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.purorderno=b.purorderno " +
                " inner join DCP_PURORDER_SOURCE c on c.eid=a.eid and c.organizationno=a.organizationno and c.purorderno=a.purorderno and c.oitem=b.item " +
                " inner join DCP_PURORDER_DELIVERY d on d.eid=a.eid and d.organizationno=a.organizationno and d.purorderno=a.purorderno " +
                " where a.eid='"+req.geteId()+"' and c.sourcebillno='"+req.getRequest().getBillNo()+"'" +
                " group by  a.eid,d.PURORDERNO,c.oitem,d.item,d.item2,a.status,b.purunit,d.ARRIVALDATE,a.supplier  ) a " +
                " left join dcp_bizpartner b on a.supplier=b.bizpartnerno and a.eid=b.eid " +
                " left join dcp_unit_lang c on a.eid=c.eid and a.punit=c.unit and c.lang_type='"+req.getLangType()+"'   " ;
        List<Map<String, Object>> purList = this.doQueryData(purSql,null);

        level1Elm.setDetail(new ArrayList<>());
        for (Map<String, Object> detailMap : detailList){
            DCP_PurchaseApplyTrackRes.Detail detail = res.new Detail();
            detail.setItem(detailMap.get("ITEM").toString());
            detail.setPluNo(detailMap.get("PLUNO").toString());
            detail.setPluName(detailMap.get("PLUNAME").toString());
            detail.setFeatureNo(detailMap.get("FEATURENO").toString());
            detail.setFeatureName(detailMap.get("FEATURENAME").toString());
            detail.setPUnit(detailMap.get("PUNIT").toString());
            detail.setPUnitName(detailMap.get("PUNITNAME").toString());
            detail.setPQty(detailMap.get("PQTY").toString());
            totPqty=totPqty.add(new BigDecimal(detailMap.get("PQTY").toString()));

            detail.setPurOrder(new ArrayList<>());

            List<Map<String, Object>> purFilter = purList.stream().filter(x -> x.get("OITEM").toString().equals(detail.getItem())).collect(Collectors.toList());
            for (Map<String, Object> purMap : purFilter){
                DCP_PurchaseApplyTrackRes.PurOrder purOrder = res.new PurOrder();
                purOrder.setSupplier(purMap.get("SUPPLIER").toString());
                purOrder.setSupplierName(purMap.get("SUPPLIERNAME").toString());
                purOrder.setPurOrderNo(purMap.get("PURORDERNO").toString());
                purOrder.setPurOrderStatus(purMap.get("STATUS").toString());
                purOrder.setArrivalDate(purMap.get("ARRIVALDATE").toString());
                purOrder.setPurUnit(purMap.get("PUNIT").toString());
                purOrder.setPurUnitName(purMap.get("PUNITNAME").toString());
                purOrder.setPurQty(purMap.get("PURQTY").toString());
                purOrder.setReceiveQty(purMap.get("RECEIVEQTY").toString());
                purOrder.setStockInQty(purMap.get("STOCKINQTY").toString());

                totPurQty=totPurQty.add(new BigDecimal(purOrder.getPurQty()));
                totReceiveQty=totReceiveQty.add(new BigDecimal(purOrder.getReceiveQty()));
                totStockInQty=totStockInQty.add(new BigDecimal(purOrder.getStockInQty()));

                detail.getPurOrder().add(purOrder);
            }

            level1Elm.getDetail().add( detail);
        }
        List<Map> collect = level1Elm.getDetail().stream().map(x -> {
            Map rm = new HashMap();
            rm.put("PLUNO", x.getPluNo());
            rm.put("FEATURENO", x.getFeatureNo());
            return rm;
        }).distinct().collect(Collectors.toList());
        totCqty=new BigDecimal(collect.size());
        level1Elm.setBillNo(req.getRequest().getBillNo());
        level1Elm.setTotCqty(totCqty.toString());
        level1Elm.setTotPqty(totPqty.toString());
        level1Elm.setTotPurQty(totPurQty.toString());
        level1Elm.setTotReceiveQty(totReceiveQty.toString());
        level1Elm.setTotStockInQty(totStockInQty.toString());

        res.getDatas().add(level1Elm);
        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PurchaseApplyTrackReq req) throws Exception {
        String eId = req.geteId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        String billNo = req.getRequest().getBillNo();



        return sqlbuf.toString();
    }
}


