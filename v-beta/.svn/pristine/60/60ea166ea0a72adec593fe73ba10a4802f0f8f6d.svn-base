package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DualPlayEnableReq;
import com.dsc.spos.json.cust.req.DCP_DualPlayTimeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @description: 客显设置启用禁用
 * @author: wangzyc
 * @create: 2022-02-07
 */
public class DCP_DualPlayEnable extends SPosAdvanceService<DCP_DualPlayEnableReq, DCP_DualPlayEnableRes> {
    @Override
    protected void processDUID(DCP_DualPlayEnableReq req, DCP_DualPlayEnableRes res) throws Exception {
        DCP_DualPlayEnableReq.level1Elm request = req.getRequest();
        String oprType = request.getOprType(); // 操作类型：1-启用 2-禁用
        String dualPlayID = request.getDualPlayID();
        String eId = req.geteId();

        String status = "";
        if("1".equals(oprType)){
            status = "100";
        }else if("2".equals(oprType)){
            status = "0";
        }

        try {
            String sql = null;
            sql = getQuery(req);
            String[] conditionValues = {eId,dualPlayID}; //查詢條件
            List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
            if (getQData != null && getQData.isEmpty() == false) {
                UptBean ub1 = null;
                ub1 = new UptBean("DCP_DUALPLAY");
                ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                // condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("DUALPLAYID", new DataValue(dualPlayID, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();
            }else{
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "双屏播放记录不存在，请重新输入！");
            }
        } catch (Exception e) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DualPlayEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DualPlayEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DualPlayEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DualPlayEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型, ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getDualPlayID())) {
            errMsg.append("客显id不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DualPlayEnableReq> getRequestType() {
        return new TypeToken<DCP_DualPlayEnableReq>(){};
    }

    @Override
    protected DCP_DualPlayEnableRes getResponseType() {
        return new DCP_DualPlayEnableRes();
    }

    protected String getQuery(DCP_DualPlayEnableReq req) throws Exception {
        String sql = "";
        sql= " select *  from DCP_DUALPLAY  where EID= ? and dualplayid = ?  ";
        return sql;
    }
}
