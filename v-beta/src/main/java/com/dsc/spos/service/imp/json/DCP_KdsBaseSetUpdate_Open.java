package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_KdsBaseSetUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_KdsBaseSetUpdate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * @description: KDS基础设置修改
 * @author: wangzyc
 * @create: 2021-09-22
 */
public class DCP_KdsBaseSetUpdate_Open extends SPosAdvanceService<DCP_KdsBaseSetUpdate_OpenReq, DCP_KdsBaseSetUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_KdsBaseSetUpdate_OpenReq req, DCP_KdsBaseSetUpdate_OpenRes res) throws Exception {
        String eId = req.geteId();
        DCP_KdsBaseSetUpdate_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String machineId = request.getMachineId();
        String overTime = request.getOverTime();
        String miniSendMsg = request.getMiniSendMsg();

        try {
            // DCP_KDSBASICSET
            DelBean db1 = new DelBean("DCP_KDSBASICSET");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db1.addCondition("MACHINEID", new DataValue(machineId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            String[] columns = {"EID","SHOPID","MACHINEID","OVERTIME","MINISENDMSG"};

            DataValue[] insValue = new DataValue[]
                    {
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(machineId, Types.VARCHAR),
                            new DataValue(overTime, Types.VARCHAR),
                            new DataValue(miniSendMsg, Types.VARCHAR)
                    };
            InsBean ib1 = new InsBean("DCP_KDSBASICSET", columns);
            ib1.addValues(insValue);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_KdsBaseSetUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_KdsBaseSetUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_KdsBaseSetUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_KdsBaseSetUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsBaseSetUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsBaseSetUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsBaseSetUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_KdsBaseSetUpdate_OpenRes getResponseType() {
        return new DCP_KdsBaseSetUpdate_OpenRes();
    }
}
