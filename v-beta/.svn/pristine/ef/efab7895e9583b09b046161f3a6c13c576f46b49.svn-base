package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ReserveOrderUpdate_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_ReserveOrderUpdate_OpenRes;
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
 * @description: 预约单修改
 * @author: wangzyc
 * @create: 2021-08-03
 */
public class DCP_ReserveOrderUpdate_Open extends SPosAdvanceService<DCP_ReserveOrderUpdate_OpenReq, DCP_ReserveOrderUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_ReserveOrderUpdate_OpenReq req, DCP_ReserveOrderUpdate_OpenRes res) throws Exception {
        DCP_ReserveOrderUpdate_OpenReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String reserveNo = request.getReserveNo();
        String shopId = req.getShopId();
        SimpleDateFormat dfh = new SimpleDateFormat("HH:mm");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // 查询下单据状态  仅有0.待审核，1.待消费状态可以修改，否则报错提示：当前订单为XX状态，不可修改；
            String sql  = "SELECT * FROM DCP_RESERVE WHERE eid= '"+eId+"'  AND RESERVENO = '"+reserveNo+"'";
            List<Map<String, Object>> data = this.doQueryData(sql, null);
            String status = "";
            if(!CollectionUtils.isEmpty(data)){
                status = data.get(0).get("STATUS").toString();
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "当前单号为:"+reserveNo+"的订单不存在！");
            }
            String statusName ="";
            if(!status.equals("0")&&!status.equals("1")&&Check.Null(statusName)){

                if(status.equals("2")){
                    statusName = "已完成";
                }else if(status.equals("3")){
                    statusName = "已取消";
                }else if(status.equals("4")){
                    statusName = "已过期";
                }
            }

            if(!Check.Null(statusName)){
                String msg = "当前订单为"+statusName+"状态，不可修改；";
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, msg);
            }


            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String lastmoditime = dfs.format(new Date());
            // 修改 预约单
            UptBean ub1 = null;
            ub1 = new UptBean("DCP_RESERVE");
            if(!Check.Null(request.getOpNo())){
                ub1.addUpdateValue("OPNO", new DataValue(request.getOpNo(), Types.VARCHAR));
            }
            if(!Check.Null(request.getDate())){
                ub1.addUpdateValue("BDATE", new DataValue(request.getDate(), Types.DATE));
            }
            if(!Check.Null(request.getTime())){
                ub1.addUpdateValue("\"TIME\"", new DataValue(request.getTime(), Types.VARCHAR));
            }
            if(!Check.Null(request.getMemo())){
                ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
            }
            if(!Check.Null(request.getOpNo())||!Check.Null(request.getDate())||!Check.Null(request.getTime())||!Check.Null(request.getMemo())){
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            }


            ub1.addCondition("RESERVENO", new DataValue(reserveNo, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveOrderUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveOrderUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveOrderUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveOrderUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveOrderUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getReserveNo())) {
            errMsg.append("预约单号不能为空");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ReserveOrderUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_ReserveOrderUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_ReserveOrderUpdate_OpenRes getResponseType() {
        return new DCP_ReserveOrderUpdate_OpenRes();
    }

}
