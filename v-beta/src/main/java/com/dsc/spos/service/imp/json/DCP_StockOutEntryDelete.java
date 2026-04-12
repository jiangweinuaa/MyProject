package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutEntryDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockOutEntryDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 服务函数：DCP_StockOutEntryDelete
 * 服务说明：退货录入删除
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryDelete extends SPosAdvanceService<DCP_StockOutEntryDeleteReq, DCP_StockOutEntryDeleteRes> {
    @Override
    protected void processDUID(DCP_StockOutEntryDeleteReq req, DCP_StockOutEntryDeleteRes res) throws Exception {
        try{
            String eId = req.geteId();
            String shopId = req.getShopId();
            String stockOutEntryNo = req.getRequest().getStockOutEntryNo();
            
            DCP_StockOutEntryDeleteReq.levelElm request = req.getRequest();
            String sql = " select stockoutentryno from dcp_stockout_entry "
                    + " where eid='"+eId+"' and shopid='"+shopId+"' and stockoutentryno='"+request.getStockOutEntryNo()+"' and status='0' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            if (CollectionUtils.isEmpty(getQData)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "退货录入单不存在 ");
            }
            
            //删除 DCP_STOCKOUT_ENTRY
            DelBean db1 = new DelBean("DCP_STOCKOUT_ENTRY");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db1.addCondition("STOCKOUTENTRYNO", new DataValue(stockOutEntryNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));
            
            //删除 DCP_STOCKOUT_ENTRY_DETAIL
            DelBean db2 = new DelBean("DCP_STOCKOUT_ENTRY_DETAIL");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db2.addCondition("STOCKOUTENTRYNO", new DataValue(stockOutEntryNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutEntryDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutEntryDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutEntryDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_StockOutEntryDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutEntryDeleteReq.levelElm request = req.getRequest();
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
    protected TypeToken<DCP_StockOutEntryDeleteReq> getRequestType() {
        return new TypeToken<DCP_StockOutEntryDeleteReq>(){};
    }
    
    @Override
    protected DCP_StockOutEntryDeleteRes getResponseType() {
        return new DCP_StockOutEntryDeleteRes();
    }
}
