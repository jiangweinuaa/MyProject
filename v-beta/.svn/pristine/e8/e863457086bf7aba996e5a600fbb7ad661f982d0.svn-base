package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_HolidayDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ReserveTimeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReserveTimeDeleteRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.List;

/**
 * @description: 预约时间删除
 * @author: wangzyc
 * @create: 2021-07-27
 */
public class DCP_ReserveTimeDelete extends SPosAdvanceService<DCP_ReserveTimeDeleteReq, DCP_ReserveTimeDeleteRes> {
    @Override
    protected void processDUID(DCP_ReserveTimeDeleteReq req, DCP_ReserveTimeDeleteRes res) throws Exception {
        DCP_ReserveTimeDeleteReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        String item = request.getItem();
        String shopId = request.getShopId();

        try {
            DelBean db1 = new DelBean("DCP_RESERVETIME");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功！");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ReserveTimeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ReserveTimeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ReserveTimeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ReserveTimeDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_ReserveTimeDeleteReq.level1Elm request = req.getRequest();

        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (Check.Null(request.getShopId())) {
            errMsg.append("所属门店不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getItem())) {
            errMsg.append("项次不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ReserveTimeDeleteReq> getRequestType() {
        return new TypeToken<DCP_ReserveTimeDeleteReq>() {
        };
    }

    @Override
    protected DCP_ReserveTimeDeleteRes getResponseType() {
        return new DCP_ReserveTimeDeleteRes();
    }
}
