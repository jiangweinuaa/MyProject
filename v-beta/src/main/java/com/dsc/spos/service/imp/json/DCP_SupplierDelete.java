package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SupplierDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SupplierDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SupplierDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
/**
 * 服务名称：DCP_SupplierDelete
 * 服务说明：供应商删除
 * @author jinzma
 * @since  2022-03-15
 */
public class DCP_SupplierDelete extends SPosAdvanceService<DCP_SupplierDeleteReq, DCP_SupplierDeleteRes> {
    @Override
    protected void processDUID(DCP_SupplierDeleteReq req, DCP_SupplierDeleteRes res) throws Exception {
        try{
            String eId = req.geteId();
            List<level1Elm> supplierList = req.getRequest().getSupplierList();
            
            for (level1Elm par:supplierList){
                String supplier = par.getSupplier();
                
                //删除DCP_SUPPLIER
                DelBean db1 = new DelBean("DCP_SUPPLIER");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("SUPPLIER", new DataValue(supplier, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                
                //删除DCP_SUPPLIER_LANG
                DelBean db2 = new DelBean("DCP_SUPPLIER_LANG");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("SUPPLIER", new DataValue(supplier, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));
            }
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
            
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SupplierDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SupplierDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SupplierDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SupplierDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (req.getRequest().getSupplierList()==null){
                isFail = true;
                errMsg.append("供应商列表不能为空,");
            }
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_SupplierDeleteReq> getRequestType() {
        return new TypeToken<DCP_SupplierDeleteReq>(){};
    }
    
    @Override
    protected DCP_SupplierDeleteRes getResponseType() {
        return new DCP_SupplierDeleteRes();
    }
}
