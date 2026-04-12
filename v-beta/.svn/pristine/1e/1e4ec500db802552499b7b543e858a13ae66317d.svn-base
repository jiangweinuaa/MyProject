package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveItemsEnableReq;
import com.dsc.spos.json.cust.res.DCP_ReserveItemsEnableRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 预约项目启用/禁用
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ReserveItemsEnable extends SPosAdvanceService<DCP_ReserveItemsEnableReq, DCP_ReserveItemsEnableRes> {
    @Override
    protected void processDUID(DCP_ReserveItemsEnableReq req, DCP_ReserveItemsEnableRes res) throws Exception {
        DCP_ReserveItemsEnableReq.level1Elm request = req.getRequest();
        try {
            String eId = req.geteId();
            String shopId = request.getShopId();
            String oprType = request.getOprType();
            List<DCP_ReserveItemsEnableReq.level2Elm> itemsNoList = request.getItemsNoList();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = new Date();
            String lastmoditime = df.format(time);

            if(!CollectionUtils.isEmpty(itemsNoList)){
                for (DCP_ReserveItemsEnableReq.level2Elm lv2 : itemsNoList) {

                    UptBean ub1 = new UptBean("DCP_RESERVEITEMS");
                    ub1.addUpdateValue("STATUS", new DataValue(oprType.equals("1")?100:0, Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
                    ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));

                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("ITEMSNO", new DataValue(lv2.getItemsNo(), Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
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
    protected List<InsBean> prepareInsertData(DCP_ReserveItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveItemsEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveItemsEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveItemsEnableReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(Check.Null(request.getShopId())){
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }
        if(Check.Null(request.getOprType())){
            errMsg.append("操作类型不能为空,");
            isFail = true;
        }
        if(CollectionUtils.isEmpty(request.getItemsNoList())){
            errMsg.append("预约服务项目编号不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveItemsEnableReq> getRequestType() {
        return new TypeToken<DCP_ReserveItemsEnableReq>(){};
    }

    @Override
    protected DCP_ReserveItemsEnableRes getResponseType() {
        return new DCP_ReserveItemsEnableRes();
    }
}
