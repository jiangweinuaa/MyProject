package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BFeeErpUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BFeeErpUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * 服务函数：DCP_BFeeErpUpdate
 * 服务说明：费用单状态更新
 * @author jinzma
 * @since  2022-07-15
 */
public class DCP_BFeeErpUpdate extends SPosAdvanceService<DCP_BFeeErpUpdateReq, DCP_BFeeErpUpdateRes> {
    @Override
    protected void processDUID(DCP_BFeeErpUpdateReq req, DCP_BFeeErpUpdateRes res) throws Exception {
        try {
            
            String eId = req.getEnterprise_no();
            String shopId = req.getShop_no();
            String bfeeNo = req.getFront_no();
            String status = req.getOperation_type();  //4.待支付 5.部分支付 6.全部支付
            
            String sql = " select status from dcp_bfee where eid='"+eId+"' and shopid='"+shopId+"' and bfeeno='"+bfeeNo+"' " ;
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty() ){
                
                UptBean ub = new UptBean("DCP_BFEE");
                ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                //更新条件
                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("BFEENO", new DataValue(bfeeNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub));
                
            }else{
                
                res.setOrg_no(shopId);
                res.setDoc_no(bfeeNo);
                
                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription("费用单:"+bfeeNo+" 不存在! ");
                
                return;
            }
            
            this.doExecuteDataToDB();
            
            res.setOrg_no(shopId);
            res.setDoc_no(bfeeNo);
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
            
        }catch(Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_BFeeErpUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_BFeeErpUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_BFeeErpUpdateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_BFeeErpUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (Check.Null(req.getEnterprise_no())) {
            errMsg.append("企业编码(enterprise_no)不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getShop_no())) {
            errMsg.append("门店编号(shop_no)不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getFront_no())) {
            errMsg.append("前端单号(front_no)不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getOperation_type())) {
            errMsg.append("操作类型(operation_type)不可为空值, ");
            isFail = true;
        }
        
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_BFeeErpUpdateReq> getRequestType() {
        return new TypeToken<DCP_BFeeErpUpdateReq>(){};
    }
    
    @Override
    protected DCP_BFeeErpUpdateRes getResponseType() {
        return new DCP_BFeeErpUpdateRes();
    }
}
