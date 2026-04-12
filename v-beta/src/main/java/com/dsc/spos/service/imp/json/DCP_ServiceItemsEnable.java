package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ServiceItemsEnableReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsEnableRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 服务项目启用/禁用
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ServiceItemsEnable extends SPosAdvanceService<DCP_ServiceItemsEnableReq, DCP_ServiceItemsEnableRes> {
    @Override
    protected void processDUID(DCP_ServiceItemsEnableReq req, DCP_ServiceItemsEnableRes res) throws Exception {
        String eId = req.geteId();

        try {
            DCP_ServiceItemsEnableReq.level1Elm request = req.getRequest();
            String oprType = request.getOprType(); //  操作类型：1-启用 2-禁用
            List<DCP_ServiceItemsEnableReq.level2Elm> itemsNoList = request.getItemsNoList();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = new Date();
            String lastmoditime = df.format(time);

            if(!CollectionUtils.isEmpty(itemsNoList)){
                for (DCP_ServiceItemsEnableReq.level2Elm lv2 : itemsNoList) {

                    UptBean ub1 = new UptBean("DCP_SERVICEITEMS");
                    ub1.addUpdateValue("STATUS", new DataValue(oprType.equals("1")?100:0, Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));

                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ITEMSNO", new DataValue(lv2.getItemsNo(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub1));
                }
            }
            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ServiceItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ServiceItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ServiceItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ServiceItemsEnableReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(CollectionUtils.isEmpty(request.getItemsNoList())){
            errMsg.append("服务项目编号不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsEnableReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsEnableReq>(){};
    }

    @Override
    protected DCP_ServiceItemsEnableRes getResponseType() {
        return new DCP_ServiceItemsEnableRes();
    }
}
