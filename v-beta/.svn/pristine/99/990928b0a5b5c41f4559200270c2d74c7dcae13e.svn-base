package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_EtlRetransProcessReq;
import com.dsc.spos.json.cust.res.DCP_EtlRetransProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服务函数：DCP_EtlRetransProcess
 * 服务说明：ETL重传
 * @author jinzma
 * @since  2022-04-11
 */
public class DCP_EtlRetransProcess extends SPosAdvanceService<DCP_EtlRetransProcessReq, DCP_EtlRetransProcessRes> {
    @Override
    protected void processDUID(DCP_EtlRetransProcessReq req, DCP_EtlRetransProcessRes res) throws Exception {
        try {
            
            String procedure="SP_ETL_TRANS_REQUEST";
            Map<Integer,Object> parameter = new HashMap<Integer, Object>();
            parameter.put(1,req.geteId());               //--企业ID
            parameter.put(2,req.getDocType());           //--单据类型（表名）
            parameter.put(3,req.getShopId());            //--门店ID
            parameter.put(4,req.getDocNo());             //--单据编号
            
            ProcedureBean pdb = new ProcedureBean(procedure, parameter);
            this.addProcessData(new DataProcessBean(pdb));
            
            this.doExecuteDataToDB();
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            
        }catch (Exception e){
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,"存储过程(SP_ETL_TRANS_REQUEST)执行报错,单据类型可能错误");
        }
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_EtlRetransProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_EtlRetransProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_EtlRetransProcessReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_EtlRetransProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if (Check.Null(req.getDocType())) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        if(Check.Null(req.getDocNo())) {
            errMsg.append("单据编号不可为空值, ");
            isFail = true;
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_EtlRetransProcessReq> getRequestType() {
        return new TypeToken<DCP_EtlRetransProcessReq>(){};
    }
    
    @Override
    protected DCP_EtlRetransProcessRes getResponseType() {
        return new DCP_EtlRetransProcessRes();
    }
}
