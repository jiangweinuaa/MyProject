package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_ServiceItemsDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 服务项目删除
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ServiceItemsDelete extends SPosAdvanceService<DCP_ServiceItemsDeleteReq, DCP_ServiceItemsDeleteRes> {
    @Override
    protected void processDUID(DCP_ServiceItemsDeleteReq req, DCP_ServiceItemsDeleteRes res) throws Exception {

        try {
            List<String> itemsNo = req.getRequest().getItemsNo();
            String eId = req.geteId();
            if(!CollectionUtils.isEmpty(itemsNo)){
                for (String item : itemsNo) {
                    DelBean del = new DelBean("DCP_SERVICEITEMS");
                    del.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del.addCondition("ITEMSNO", new DataValue(item, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(del));
                }
            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ServiceItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ServiceItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ServiceItemsDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ServiceItemsDeleteReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(CollectionUtils.isEmpty(request.getItemsNo())){
            errMsg.append("服务项目编号不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsDeleteReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsDeleteReq>(){};
    }

    @Override
    protected DCP_ServiceItemsDeleteRes getResponseType() {
        return new DCP_ServiceItemsDeleteRes();
    }
}
