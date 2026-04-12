package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ExpenseDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ExpenseDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ExpenseDelete extends SPosAdvanceService<DCP_ExpenseDeleteReq, DCP_ExpenseDeleteRes> {
    @Override
    protected void processDUID(DCP_ExpenseDeleteReq req, DCP_ExpenseDeleteRes res) throws Exception {

        String deleteType = req.getRequest().getDelete_Type();

        List<Map<String, Object>> qData = doQueryData(" SELECT * FROM DCP_EXPSHEET WHERE BFEENO='" + req.getRequest().getBfeeNo() + "'", null);

        if (null == qData || qData.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无法查询到对应单据" + req.getRequest().getBfeeNo());
        }

        if (!"0".equals(qData.get(0).get("STATUS").toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据状态非[0-新建]不可删除！");
        }

        ColumnDataValue condition = new ColumnDataValue();
//        删除条件,1：结算法人+单据编号 2：结算法人+单据编号+项次")
        if ("1".equals(deleteType)) {
            condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            condition.add("BFEENO", DataValues.newString(req.getRequest().getBfeeNo()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_EXPSHEET", condition)));
        } else {
            condition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            condition.add("BFEENO", DataValues.newString(req.getRequest().getBfeeNo()));
            condition.add("ITEM", DataValues.newString(req.getRequest().getItem()));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_EXPDETAIL", condition)));

        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ExpenseDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ExpenseDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ExpenseDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ExpenseDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ExpenseDeleteReq> getRequestType() {
        return new TypeToken<DCP_ExpenseDeleteReq>() {
        };
    }

    @Override
    protected DCP_ExpenseDeleteRes getResponseType() {
        return new DCP_ExpenseDeleteRes();
    }
}
