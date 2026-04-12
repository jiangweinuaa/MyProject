package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockOutEntryDetailReq;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryDetailRes;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryDetailRes.*;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_StockOutEntryDetail
 * 服务说明：退货录入明细查询
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryDetail extends SPosBasicService<DCP_StockOutEntryDetailReq, DCP_StockOutEntryDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockOutEntryDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutEntryDetailReq.levelElm request = req.getRequest();
        if (request==null){
            errMsg.append("request不可为空值, ");
            isFail = true;
        }else{
            if (Check.Null(request.getStockOutEntryNo())){
                errMsg.append("退货录入单号不可为空值, ");
                isFail = true;
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_StockOutEntryDetailReq> getRequestType() {
        return new TypeToken<DCP_StockOutEntryDetailReq>(){};
    }
    
    @Override
    protected DCP_StockOutEntryDetailRes getResponseType() {
        return new DCP_StockOutEntryDetailRes();
    }
    
    @Override
    protected DCP_StockOutEntryDetailRes processJson(DCP_StockOutEntryDetailReq req) throws Exception {
        try{
            DCP_StockOutEntryDetailRes res = this.getResponse();
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            level1Elm oneLv1 = res.new level1Elm();
            oneLv1.setStockOutEntryNo(req.getRequest().getStockOutEntryNo());
            oneLv1.setPluList(new ArrayList<>());
            if (!CollectionUtils.isEmpty(getQData)){
                for (Map<String, Object> oneData : getQData) {
                    level2Elm oneLv2 = res.new level2Elm();
                    oneLv2.setItem(oneData.get("ITEM").toString());
                    oneLv2.setPluNo(oneData.get("PLUNO").toString());
                    oneLv2.setPluName(oneData.get("PLU_NAME").toString());
                    oneLv2.setFeatureNo(oneData.get("FEATURENO").toString());
                    oneLv2.setFeatureName(oneData.get("FEATURENAME").toString());
                    oneLv2.setBatchNo(oneData.get("BATCH_NO").toString());
                    oneLv2.setProdDate(oneData.get("PROD_DATE").toString());
                    oneLv2.setPqty(oneData.get("PQTY").toString());
                    oneLv2.setPunit(oneData.get("PUNIT").toString());
                    oneLv2.setPunitName(oneData.get("PUNITNAME").toString());
                    oneLv2.setPunitUdLength(oneData.get("PUNITUDLENGTH").toString());
                    oneLv2.setBaseQty(oneData.get("BASEQTY").toString());
                    oneLv2.setBaseUnit(oneData.get("BASEUNIT").toString());
                    oneLv2.setBaseUnitName(oneData.get("BASEUNITNAME").toString());
                    oneLv2.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    oneLv2.setUnitRatio(oneData.get("UNIT_RATIO").toString());
                    oneLv2.setPrice(oneData.get("PRICE").toString());
                    oneLv2.setDistriPrice(oneData.get("DISTRIPRICE").toString());
                    oneLv2.setAmt(oneData.get("AMT").toString());
                    oneLv2.setDistriAmt(oneData.get("DISTRIAMT").toString());
                    oneLv2.setIsBatch(oneData.get("ISBATCH").toString());
                    oneLv2.setStockManageType(oneData.get("STOCKMANAGETYPE").toString());
                    oneLv2.setBsNo(oneData.get("BSNO").toString());
                    oneLv2.setBsName(oneData.get("REASON_NAME").toString());
                    oneLv2.setStockQty(oneData.get("STOCKQTY").toString());
                    oneLv2.setMemo(oneData.get("MEMO").toString());
                    oneLv2.setReceiptOrg(oneData.get("RECEIPTORG").toString());
                    oneLv2.setReceiptOrgName(oneData.get("ORG_NAME").toString());
                    
                    oneLv1.getPluList().add(oneLv2);
                }
            }
            
            res.setDatas(oneLv1);
            
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_StockOutEntryDetailReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        StringBuffer sqlbuf=new StringBuffer();
        
        sqlbuf.append(" "
                + " select a.*,b.plu_name,c1.uname as punitname,c2.uname as baseunitname,d.featurename,"
                + " e.isbatch,e.stockmanagetype,h.org_name,g.reason_name,"
                + " f1.udlength as punitudlength,f2.udlength as baseunitudlength"
                + " from dcp_stockout_entry_detail a"
                + " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"'"
                + " left join dcp_unit_lang c1 on a.eid=c1.eid and a.punit=c1.unit and c1.lang_type='"+langType+"'"
                + " left join dcp_unit_lang c2 on a.eid=c2.eid and a.baseunit=c2.unit and c2.lang_type='"+langType+"'"
                + " left join dcp_goods_feature_lang d on a.eid=d.eid and a.pluno=d.pluno and a.featureno=d.featureno and d.lang_type='"+langType+"'"
                + " left join dcp_goods e on a.eid=e.eid and a.pluno=e.pluno"
                + " left join dcp_unit f1 on a.eid=f1.eid and a.punit=f1.unit"
                + " left join dcp_unit f2 on a.eid=f2.eid and a.baseunit=f2.unit"
                + " left join dcp_reason_lang g on a.eid=g.eid and a.bsno=g.bsno and g.bstype='2' and g.lang_type='"+langType+"'"
                + " left join dcp_org_lang h on a.eid=h.eid and a.receiptorg=h.organizationno and h.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stockoutentryno='"+req.getRequest().getStockOutEntryNo()+"'"
                + " "
                + " ");
        
        return sqlbuf.toString();
        
        
    }
}
