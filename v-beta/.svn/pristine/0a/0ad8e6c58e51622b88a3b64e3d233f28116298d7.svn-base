package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceAdjustDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceAdjustDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * 服务名称：DCP_SalePriceAdjustDelete
 * 服务说明：自建门店调价单删除(零售价)
 * @author jinzma
 * @since  2022-02-24
 */
public class DCP_SalePriceAdjustDelete extends SPosAdvanceService<DCP_SalePriceAdjustDeleteReq, DCP_SalePriceAdjustDeleteRes> {
    @Override
    protected void processDUID(DCP_SalePriceAdjustDeleteReq req, DCP_SalePriceAdjustDeleteRes res) throws Exception {
        
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String salePriceAdjustNo = req.getRequest().getSalePriceAdjustNo();
            String sql = " select salepriceadjustno from dcp_salepriceadjust "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and salepriceadjustno='"+salePriceAdjustNo+"' and status='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData != null && !getQData.isEmpty()){
                
                //删除单据单头
                DelBean db = new DelBean("DCP_SALEPRICEADJUST");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db.addCondition("SALEPRICEADJUSTNO", new DataValue(salePriceAdjustNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
                
                //删除单据单身
                DelBean dbDetail = new DelBean("DCP_SALEPRICEADJUST_DETAIL");
                dbDetail.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                dbDetail.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                dbDetail.addCondition("SALEPRICEADJUSTNO", new DataValue(salePriceAdjustNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(dbDetail));
                
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新确认！ ");
            }
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceAdjustDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceAdjustDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceAdjustDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SalePriceAdjustDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else {
            if (Check.Null(req.getRequest().getSalePriceAdjustNo())) {
                isFail = true;
                errMsg.append("salePriceAdjustNo不能为空,");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_SalePriceAdjustDeleteReq> getRequestType() {
        return new TypeToken<DCP_SalePriceAdjustDeleteReq>(){};
    }
    
    @Override
    protected DCP_SalePriceAdjustDeleteRes getResponseType() {
        return new DCP_SalePriceAdjustDeleteRes();
    }
}
