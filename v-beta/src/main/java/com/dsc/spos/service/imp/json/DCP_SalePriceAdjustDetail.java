package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustDetailReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务名称：DCP_SalePriceAdjustDetail
 * 服务说明：自建门店调价明细查询(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustDetail extends SPosBasicService<DCP_SalePriceAdjustDetailReq, DCP_SalePriceAdjustDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (req.getRequest() == null){
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getSalePriceAdjustNo())){
                isFail = true;
                errMsg.append("调价单号不能为空,");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SalePriceAdjustDetailReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustDetailReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustDetailRes getResponseType() {
        return new DCP_SalePriceAdjustDetailRes();
    }
    
    @Override
    protected DCP_SalePriceAdjustDetailRes processJson(DCP_SalePriceAdjustDetailReq req) throws Exception {
        
        try{
            DCP_SalePriceAdjustDetailRes res = this.getResponse();
            String sql = this.getQuerySql(req);
            List<Map<String,Object>> getQData = this.doQueryData(sql,null);
            res.setDatas(new ArrayList<DCP_SalePriceAdjustDetailRes.level1Elm>());
            if (getQData!=null && !getQData.isEmpty()){
                for (Map<String, Object> oneData : getQData) {
                    DCP_SalePriceAdjustDetailRes.level1Elm oneLv1 = res.new level1Elm();
                    oneLv1.setItem(oneData.get("ITEM").toString());
                    oneLv1.setPluNo(oneData.get("PLUNO").toString());
                    oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                    oneLv1.setUnit(oneData.get("UNIT").toString());
                    oneLv1.setUnitName(oneData.get("UNAME").toString());
                    oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                    oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
                    oneLv1.setStandardPrice(oneData.get("STANDARDPRICE").toString());
                    oneLv1.setPrice(oneData.get("PRICE").toString());
                    oneLv1.setMinPrice(oneData.get("MINPRICE").toString());
                    oneLv1.setStatus(oneData.get("STATUS").toString());
                    oneLv1.setIsProm(oneData.get("ISPROM").toString());
                    oneLv1.setIsDiscount(oneData.get("ISDISCOUNT").toString());
                    oneLv1.setBeginDate(oneData.get("BEGINDATE").toString());
                    oneLv1.setEndDate(oneData.get("ENDDATE").toString());
                    
                    res.getDatas().add(oneLv1);
                }
            }
            
            return res;
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_SalePriceAdjustDetailReq req) throws Exception {
        StringBuffer sb = new StringBuffer();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String salePriceAdjustNo = req.getRequest().getSalePriceAdjustNo();
        String langType = req.getLangType();
        
        sb.append(""
                + " select a.item,a.pluno,b.plu_name,a.unit,c.uname,a.featureno,d.featurename,e.price as standardprice,"
                + " a.price,a.minprice,a.status,a.isprom,a.isdiscount,a.begindate,a.enddate"
                + " from dcp_salepriceadjust_detail a"
                + " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langType+"'"
                + " left join dcp_unit_lang c on a.eid=c.eid and a.unit=c.unit and c.lang_type='"+langType+"'"
                + " left join dcp_goods_feature_lang d on a.eid=d.eid and a.pluno=d.pluno and a.featureno=d.featureno and d.lang_type='"+langType+"'"
                + " left join dcp_goods e on a.eid=e.eid and a.pluno=e.pluno"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.salepriceadjustno='"+salePriceAdjustNo+"'"
                + " order by a.item"
                + " ");
        
        return sb.toString();
        
    }
}
