package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_AdvisorDeleteReq;
import com.dsc.spos.json.cust.res.DCP_AdvisorDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 顾问删除
 * @author: wangzyc
 * @create: 2021-07-14
 */
public class DCP_AdvisorDelete extends SPosAdvanceService<DCP_AdvisorDeleteReq, DCP_AdvisorDeleteRes> {
    @Override
    protected void processDUID(DCP_AdvisorDeleteReq req, DCP_AdvisorDeleteRes res) throws Exception {
        try {
            String eId = req.geteId();
            List<String> opNos = req.getRequest().getOpNo();
            for (String opNo : opNos) {
                // DCP_ADVISORSET
                DelBean db1 = new DelBean("DCP_ADVISORSET");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                // DCP_ADVISORSET_SCHEDULING
                DelBean db2 = new DelBean("DCP_ADVISORSET_SCHEDULING");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                // DCP_ADVISORSET_RESTTIME
                DelBean db3 = new DelBean("DCP_ADVISORSET_RESTTIME");
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("OPNO", new DataValue(opNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));
            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AdvisorDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AdvisorDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AdvisorDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AdvisorDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_AdvisorDeleteReq.level1Elm request = req.getRequest();

        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<String> opNo = req.getRequest().getOpNo();

        if (CollectionUtils.isEmpty(opNo)) {
            errMsg.append("员工编号不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_AdvisorDeleteReq> getRequestType() {
        return new TypeToken<DCP_AdvisorDeleteReq>() {
        };
    }

    @Override
    protected DCP_AdvisorDeleteRes getResponseType() {
        return new DCP_AdvisorDeleteRes();
    }
}
