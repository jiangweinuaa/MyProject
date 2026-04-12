package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderDeleteUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderDeleteUpdate_OpenRes;
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
 * @description: 订单假删除（C端隐藏）
 * @author: wangzyc
 * @create: 2021-07-16
 */
public class DCP_OrderDeleteUpdate_Open extends SPosAdvanceService<DCP_OrderDeleteUpdate_OpenReq, DCP_OrderDeleteUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderDeleteUpdate_OpenReq req, DCP_OrderDeleteUpdate_OpenRes res) throws Exception {
        DCP_OrderDeleteUpdate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String orderNo = request.getOrderNo();
        String loadDocType = request.getLoadDocType();
        String channelId = req.getApiUser().getChannelId();


        try {
            UptBean ub2 = null;
            ub2 = new UptBean("DCP_ORDER");
            ub2.addUpdateValue("ISDELETE", new DataValue("Y", Types.VARCHAR));
            ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
            ub2.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
            if(!Check.Null(channelId)){
                ub2.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
            }
            this.addProcessData(new DataProcessBean(ub2));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("500");
            res.setServiceDescription("服务执行失败:"+e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderDeleteUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderDeleteUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderDeleteUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderDeleteUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(Check.Null(req.getRequest().getOrderNo())){
            isFail = true;
            errMsg.append("订单号不能为空 ");
        }

        if(Check.Null(req.getRequest().getLoadDocType())){
            isFail = true;
            errMsg.append("来源类型不能为空 ");
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderDeleteUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderDeleteUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_OrderDeleteUpdate_OpenRes getResponseType() {
        return new DCP_OrderDeleteUpdate_OpenRes();
    }
}
