package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ISVWeComTagSyncReq;
import com.dsc.spos.json.cust.res.DCP_ISVWeComTagSyncRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.thirdpart.wecom.DCPWeComService;
import com.dsc.spos.thirdpart.wecom.DCPWeComUtils;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Types;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComTagSyncReq
 * 服务说明：企微标签同步到中台
 * @author jinzma
 * @since  2023-09-14
 */
public class DCP_ISVWeComTagSync extends SPosAdvanceService<DCP_ISVWeComTagSyncReq, DCP_ISVWeComTagSyncRes> {
    @Override
    protected void processDUID(DCP_ISVWeComTagSyncReq req, DCP_ISVWeComTagSyncRes res) throws Exception {
        try{
            String eId = req.geteId();

            //调企微查询
            DCPWeComService dcpWeComService = new DCPWeComService();

            if (!dcpWeComService.TagSync(dao,eId)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "企业微信获取企业标签库失败,详细原因请查询日志");
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
    protected List<InsBean> prepareInsertData(DCP_ISVWeComTagSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_ISVWeComTagSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_ISVWeComTagSyncReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_ISVWeComTagSyncReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_ISVWeComTagSyncReq> getRequestType() {
        return new TypeToken<DCP_ISVWeComTagSyncReq>(){};
    }
    
    @Override
    protected DCP_ISVWeComTagSyncRes getResponseType() {
        return new DCP_ISVWeComTagSyncRes();
    }
}
