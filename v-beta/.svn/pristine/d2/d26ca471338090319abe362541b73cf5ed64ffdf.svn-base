package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ReserveParameterQueryReq;
import com.dsc.spos.json.cust.res.DCP_ReserveParameterQueryRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @description: 预约参数查询
 * @author: wangzyc
 * @create: 2021-07-23
 */
public class DCP_ReserveParameterQuery extends SPosBasicService<DCP_ReserveParameterQueryReq, DCP_ReserveParameterQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_ReserveParameterQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveParameterQueryReq.level1Elm request = req.getRequest();
        if(request==null)
        {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if(Check.Null(request.getShopId())){
            errMsg.append("所属门店不能为空 ,");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveParameterQueryReq> getRequestType() {
        return new TypeToken<DCP_ReserveParameterQueryReq>(){};
    }

    @Override
    protected DCP_ReserveParameterQueryRes getResponseType() {
        return new DCP_ReserveParameterQueryRes();
    }

    @Override
    protected DCP_ReserveParameterQueryRes processJson(DCP_ReserveParameterQueryReq req) throws Exception {
        DCP_ReserveParameterQueryRes res  = this.getResponseType();
        DCP_ReserveParameterQueryRes.level1Elm level1Elm = res.new level1Elm();
        res.setDatas(level1Elm);

        try {
            String sql = this.getQuerySql(req);
            List<Map<String, Object>> data = this.doQueryData(sql, null);

            if(!CollectionUtils.isEmpty(data)){
                for (Map<String, Object>  oneData : data) {
                    String reserveaudit = oneData.get("RESERVEAUDIT").toString();
                    String smsalerts = oneData.get("SMSALERTS").toString();
                    String shopDistribution = oneData.get("SHOPDISTRIBUTION").toString();
                    String appointmentDays = oneData.get("APPOINTMENTDAYS").toString();
                    String shopAppointments = oneData.get("APPOINTMENTS").toString();
                    String cancelType = oneData.get("CANCELTYPE").toString();
                    String cancelTime = oneData.get("CANCELTIME").toString();

                    if(Check.Null(appointmentDays)|| !PosPub.isNumericType(appointmentDays)){
                        appointmentDays = "1";
                    }
                    if(Check.Null(shopAppointments)|| !PosPub.isNumericType(shopAppointments)){
                        shopAppointments = "1";
                    }

                    DCP_ReserveParameterQueryRes.level1Elm lv1 = res.new level1Elm();

                    lv1.setReserveAudit(reserveaudit);
                    lv1.setSmsAlerts(smsalerts);
                    lv1.setShopAppointments(shopAppointments);
                    lv1.setShopDistribution(shopDistribution);
                    lv1.setAppointmentDays(appointmentDays);
                    lv1.setCancelType(cancelType);
                    lv1.setCancelTime(cancelTime);
                    res.setDatas(lv1);
                }
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }catch (Exception e){
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ReserveParameterQueryReq req) throws Exception {
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select * from DCP_RESERVEPARAMETER where EID = '"+req.geteId()+"' and SHOPID = '"+req.getRequest().getShopId()+"'");
        sql = sqlbuf.toString();
        return sql;
    }
}
