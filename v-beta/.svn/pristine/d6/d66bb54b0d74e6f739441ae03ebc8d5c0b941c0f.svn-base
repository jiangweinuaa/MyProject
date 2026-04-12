package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CdsSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_CdsSetDeleteRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;

/**
 * @description: CDS叫号屏设置删除
 * @author: wangzyc
 * @create: 2022-05-25
 */
public class DCP_CdsSetDelete extends SPosAdvanceService<DCP_CdsSetDeleteReq, DCP_CdsSetDeleteRes> {
    @Override
    protected void processDUID(DCP_CdsSetDeleteReq req, DCP_CdsSetDeleteRes res) throws Exception {
        String eId = req.geteId();
        String baseSetNo = req.getRequest().getBaseSetNo();

        try {
            DelBean db1 = new DelBean("DCP_CDSSET");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TEMPLATEID", new DataValue(baseSetNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_CDSSET_ADVERT");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("TEMPLATEID", new DataValue(baseSetNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_CDSSET_SHOP");
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db3.addCondition("TEMPLATEID", new DataValue(baseSetNo,Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));


            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CdsSetDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CdsSetDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CdsSetDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CdsSetDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsSetDeleteReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (Check.Null(request.getBaseSetNo())) {
            errMsg.append("模板编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsSetDeleteReq> getRequestType() {
        return new TypeToken<DCP_CdsSetDeleteReq>(){};
    }

    @Override
    protected DCP_CdsSetDeleteRes getResponseType() {
        return new DCP_CdsSetDeleteRes();
    }
}
