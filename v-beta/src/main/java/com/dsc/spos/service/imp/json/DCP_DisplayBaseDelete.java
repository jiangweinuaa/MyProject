package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseDeleteReq;
import com.dsc.spos.json.cust.req.DCP_DisplayBaseDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_DisplayBaseDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.sql.Types;
import java.util.List;

/**
 * 服务名称：DCP_DisplayBaseDelete
 * 服务说明：客显基础资料删除
 * @author jinzma
 * @since  2022-04-26
 */
public class DCP_DisplayBaseDelete extends SPosAdvanceService<DCP_DisplayBaseDeleteReq, DCP_DisplayBaseDeleteRes> {
    @Override
    protected void processDUID(DCP_DisplayBaseDeleteReq req, DCP_DisplayBaseDeleteRes res) throws Exception {
        try{
            String eId = req.geteId();
            List<level1Elm> templateList = req.getRequest().getTemplateList();
            for (level1Elm par : templateList) {
               String templateNo = par.getTemplateNo();
    
                //删除DCP_DUALPLAY_BASEINFO
                DelBean db1 = new DelBean("DCP_DUALPLAY_BASEINFO");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                
                //删除DCP_DUALPLAY_BASE_SHOP
                DelBean db2 = new DelBean("DCP_DUALPLAY_BASE_SHOP");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("TEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
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
    protected List<InsBean> prepareInsertData(DCP_DisplayBaseDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_DisplayBaseDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_DisplayBaseDeleteReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_DisplayBaseDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空,");
        }else {
            List<level1Elm> templateList = req.getRequest().getTemplateList();
            if (templateList == null) {
                isFail = true;
                errMsg.append("templateList不能为空,");
            } else {
                for (level1Elm par : templateList) {
                    if (Check.Null(par.getTemplateNo())) {
                        isFail = true;
                        errMsg.append("模板编号不能为空,");
                    }
                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_DisplayBaseDeleteReq> getRequestType() {
        return new TypeToken<DCP_DisplayBaseDeleteReq>(){};
    }
    
    @Override
    protected DCP_DisplayBaseDeleteRes getResponseType() {
        return new DCP_DisplayBaseDeleteRes();
    }

}
