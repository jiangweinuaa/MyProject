package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ServiceItemsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ServiceItemsUpdateRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: 服务项目修改
 * @author: wangzyc
 * @create: 2021-07-21
 */
public class DCP_ServiceItemsUpdate extends SPosAdvanceService<DCP_ServiceItemsUpdateReq, DCP_ServiceItemsUpdateRes> {
    @Override
    protected void processDUID(DCP_ServiceItemsUpdateReq req, DCP_ServiceItemsUpdateRes res) throws Exception {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmodiopId = req.getOpNO();
            String lastmodiopName = req.getOpName();
            Date time = new Date();
            String lastmoditime = df.format(time);

            DCP_ServiceItemsUpdateReq.level1Elm request = req.getRequest();
            UptBean ub = new UptBean("DCP_SERVICEITEMS");
            ub.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub.addCondition("ITEMSNO", new DataValue(request.getItemsNo(), Types.VARCHAR));

            ub.addUpdateValue("ITEMSNAME ", new DataValue(request.getItemsName(), Types.VARCHAR));
            ub.addUpdateValue("SERVICETIME ", new DataValue(request.getServiceTime(), Types.VARCHAR));
            ub.addUpdateValue("COUPONTYPEID ", new DataValue(request.getCouponTypeId(), Types.VARCHAR));
            ub.addUpdateValue("QTY ", new DataValue(request.getQty(), Types.VARCHAR));
            ub.addUpdateValue("SERVICEINTRODUCTION ", new DataValue(request.getServiceIntroduction(), Types.VARCHAR));
            ub.addUpdateValue("SERVICENOTE ", new DataValue(request.getServiceNote(), Types.VARCHAR));
            ub.addUpdateValue("MEMO ", new DataValue(request.getMemo(), Types.VARCHAR));
            ub.addUpdateValue("STATUS ", new DataValue(request.getStatus(), Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPID ", new DataValue(lastmodiopId, Types.VARCHAR));
            ub.addUpdateValue("LASTMODIOPNAME ", new DataValue(lastmodiopName, Types.VARCHAR));
            ub.addUpdateValue("LASTMODITIME ", new DataValue(lastmoditime, Types.DATE));

            // 新增reserveType、price、vipPrice、cardPrice； By wangzyc
            ub.addUpdateValue("RESERVETYPE ", new DataValue(request.getReserveType(), Types.VARCHAR));
            ub.addUpdateValue("PRICE ", new DataValue(request.getPrice(), Types.VARCHAR));
            ub.addUpdateValue("VIPPRICE ", new DataValue(request.getVipPrice(), Types.VARCHAR));
            ub.addUpdateValue("CARDPRICE ", new DataValue(request.getCardPrice(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ServiceItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ServiceItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ServiceItemsUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ServiceItemsUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ServiceItemsUpdateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getItemsNo())) {
            errMsg.append("项目编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getReserveType())) {
            errMsg.append("支持预约方式不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getCouponTypeId())||Check.Null(request.getQty())) {
            errMsg.append("体验券或服务次数不可为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_ServiceItemsUpdateReq> getRequestType() {
        return new TypeToken<DCP_ServiceItemsUpdateReq>(){};
    }

    @Override
    protected DCP_ServiceItemsUpdateRes getResponseType() {
        return new DCP_ServiceItemsUpdateRes();
    }
}
