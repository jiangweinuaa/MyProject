package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SupplierCreateReq;
import com.dsc.spos.json.cust.res.DCP_SupplierCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务名称：DCP_SupplierCreate
 * 服务说明：供应商创建
 * @author jinzma
 * @since  2022-03-15
 */
public class DCP_SupplierCreate extends SPosAdvanceService<DCP_SupplierCreateReq, DCP_SupplierCreateRes> {
    @Override
    protected void processDUID(DCP_SupplierCreateReq req, DCP_SupplierCreateRes res) throws Exception {
        try {
            String eId = req.geteId();
            String supplier = req.getRequest().getSupplier();
            String sql = " select supplier from dcp_supplier where eid='"+eId+"' and supplier='"+supplier+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()){
                String createBy = req.getOpNO();
                String createByName = req.getOpName();
                String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                //新增DCP_SUPPLIER
                String[] columns = {
                        "EID","SUPPLIER","MOBILE","RANGETYPE","STATUS",
                        "CREATEOPID","CREATEOPNAME","CREATETIME","SELFBUILTSHOPID"
                };
                InsBean ib = new InsBean("DCP_SUPPLIER", columns);
                DataValue[]	insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(supplier, Types.VARCHAR),
                        new DataValue(req.getRequest().getMobile(), Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),          //适用范围  0.全部 1.门店管理 2.ERP总部
                        new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createByName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE),
                        new DataValue(req.getRequest().getSelfBuiltShopId(), Types.VARCHAR),
                };
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
                
                
                //删除DCP_SUPPLIER_LANG
                DelBean db = new DelBean("DCP_SUPPLIER_LANG");
                db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db.addCondition("SUPPLIER", new DataValue(supplier, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db));
                
                //新增DCP_SUPPLIER_LANG
                String[] columnsLang = {
                        "EID","SUPPLIER","LANG_TYPE","SUPPLIER_NAME","ABBR","ADDRESS",
                };
                InsBean ibLang = new InsBean("DCP_SUPPLIER_LANG", columnsLang);
                DataValue[]	insValueLang = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(supplier, Types.VARCHAR),
                        new DataValue(req.getLangType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getSupplierName(), Types.VARCHAR),
                        new DataValue(req.getRequest().getAbbr(), Types.VARCHAR),
                        new DataValue(req.getRequest().getAddress(), Types.VARCHAR),
                };
                ibLang.addValues(insValueLang);
                this.addProcessData(new DataProcessBean(ibLang));
                
                this.doExecuteDataToDB();
                
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                
            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "供应商编号"+supplier+"已存在,请重新确认!");
            }
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_SupplierCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_SupplierCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_SupplierCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_SupplierCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else{
            if (Check.Null(req.getRequest().getSupplier())){
                isFail = true;
                errMsg.append("供应商编码不能为空,");
            }
            if (Check.Null(req.getRequest().getSupplierName())){
                isFail = true;
                errMsg.append("供应商全称不能为空,");
            }
            if (Check.Null(req.getRequest().getSelfBuiltShopId())){
                isFail = true;
                errMsg.append("自建门店不能为空,");
            }
            if (Check.Null(req.getRequest().getStatus())){
                isFail = true;
                errMsg.append("状态不能为空,");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_SupplierCreateReq> getRequestType() {
        return new TypeToken<DCP_SupplierCreateReq>(){};
    }
    
    @Override
    protected DCP_SupplierCreateRes getResponseType() {
        return new DCP_SupplierCreateRes();
    }
}
