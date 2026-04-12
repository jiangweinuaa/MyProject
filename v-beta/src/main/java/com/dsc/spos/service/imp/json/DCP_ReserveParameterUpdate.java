package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveParameterUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ReserveParameterUpdateRes;
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
import java.util.Map;

/**
 * @description: 预约参数更新
 * @author: wangzyc
 * @create: 2021-07-23
 */
public class DCP_ReserveParameterUpdate extends SPosAdvanceService<DCP_ReserveParameterUpdateReq, DCP_ReserveParameterUpdateRes> {
    @Override
    protected void processDUID(DCP_ReserveParameterUpdateReq req, DCP_ReserveParameterUpdateRes res) throws Exception {
        String eId = req.geteId();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastmodiopId = req.getOpNO();
        String lastmodiopName = req.getOpName();
        Date time = new Date();
        String lastmoditime = df.format(time);

        DCP_ReserveParameterUpdateReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String reserveAudit = request.getReserveAudit();
        String cancelTime = request.getCancelTime();
        String cancelType = request.getCancelType();
        String appointmentDays = request.getAppointmentDays();
        String shopAppointments = request.getShopAppointments();
        String shopDistribution = request.getShopDistribution();
        String smsAlerts = request.getSmsAlerts();

        try {
            if (checkShop(req)) {
                // 有配置 则修改
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_RESERVEPARAMETER");
                ub1.addUpdateValue("RESERVEAUDIT", new DataValue(reserveAudit, Types.VARCHAR));
                ub1.addUpdateValue("SMSALERTS", new DataValue(smsAlerts, Types.VARCHAR));
                ub1.addUpdateValue("SHOPDISTRIBUTION", new DataValue(shopDistribution, Types.VARCHAR));
                ub1.addUpdateValue("APPOINTMENTDAYS", new DataValue(appointmentDays, Types.VARCHAR));
                ub1.addUpdateValue("APPOINTMENTS", new DataValue(shopAppointments, Types.VARCHAR));
                ub1.addUpdateValue("CANCELTYPE", new DataValue(cancelType, Types.VARCHAR));
                ub1.addUpdateValue("CANCELTIME", new DataValue(cancelTime, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(lastmodiopId, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(lastmodiopName, Types.VARCHAR));
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));
            } else {
                // 无配置 则新增
                String[] columns = {
                        "EID", "SHOPID", "RESERVEAUDIT", "SMSALERTS", "SHOPDISTRIBUTION", "APPOINTMENTDAYS", "APPOINTMENTS",
                        "CANCELTYPE", "CANCELTIME", "LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME"
                };
                DataValue[] insValue1 = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(reserveAudit, Types.VARCHAR),
                                new DataValue(smsAlerts, Types.VARCHAR),
                                new DataValue(shopDistribution, Types.VARCHAR),
                                new DataValue(appointmentDays, Types.VARCHAR),
                                new DataValue(shopAppointments, Types.VARCHAR),
                                new DataValue(cancelType, Types.VARCHAR),
                                new DataValue(cancelTime, Types.VARCHAR),
                                new DataValue(lastmodiopId, Types.VARCHAR),
                                new DataValue(lastmodiopName, Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };
                InsBean ib1 = new InsBean("DCP_RESERVEPARAMETER", columns);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveParameterUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveParameterUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveParameterUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveParameterUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveParameterUpdateReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空 ,");
            isFail = true;
        }

        if (Check.Null(request.getReserveAudit())) {
            errMsg.append("预约审核不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getSmsAlerts())) {
            errMsg.append("短信提醒不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getShopDistribution())) {
            errMsg.append("预约到店分配不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getAppointmentDays())) {
            errMsg.append("最大预约天数分配不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getShopAppointments())) {
            errMsg.append("到店分配最大预约数分配不能为空, ");
            isFail = true;
        }

        if (Check.Null(request.getCancelType())) {
            errMsg.append("取消预约限制规则不能为空, ");
            isFail = true;
        }else if(request.getCancelType().equals("N")) {
            if(Check.Null(request.getCancelTime())){
                errMsg.append("如已限制取消预约规则,则取消预约时间限制不能为空");
                isFail = true;
            }
        }

//        if (Check.Null(request.getCancelTime())) {
//            errMsg.append("取消预约时间限制不能为空, ");
//            isFail = true;
//        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveParameterUpdateReq> getRequestType() {
        return new TypeToken<DCP_ReserveParameterUpdateReq>() {
        };
    }

    @Override
    protected DCP_ReserveParameterUpdateRes getResponseType() {
        return new DCP_ReserveParameterUpdateRes();
    }

    /**
     * 查询门店是否配置
     *
     * @param req
     * @return
     */
    public boolean checkShop(DCP_ReserveParameterUpdateReq req) throws Exception {
        boolean repeat = false;
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" select * from DCP_RESERVEPARAMETER where EID = '" + req.geteId() + "' and SHOPID = '" + req.getRequest().getShopId() + "'");
        List<Map<String, Object>> data = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(data)) {
            repeat = true;
        }
        return repeat;
    }
}
