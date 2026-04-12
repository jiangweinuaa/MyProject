package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BankPayDeleteReq;
import com.dsc.spos.json.cust.res.DCP_BankPayDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_BankPayDelete extends SPosAdvanceService<DCP_BankPayDeleteReq, DCP_BankPayDeleteRes> {
    @Override
    protected void processDUID(DCP_BankPayDeleteReq req, DCP_BankPayDeleteRes res) throws Exception {


        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isNotEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到数据!");
        }

        String status = qData.get(0).get("STATUS").toString();
        if (!Constant.STATUS_0.equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "数据状态不为新建，不可删除！");
        }

//        List<String> apNos = new ArrayList<>();
//        for (Map<String, Object> map : qData) {
//            //删除预估待抵单
//            String pendoffsetno = map.get("PENDOFFSETNO").toString();
//            if (Check.isNotEmpty(pendoffsetno) && !apNos.contains(pendoffsetno)) {
//                apNos.add(pendoffsetno);
//
//                ColumnDataValue delApBill = new ColumnDataValue();
//                delApBill.add("EID", DataValues.newString(req.geteId()));
//                delApBill.add("APNO", DataValues.newString(pendoffsetno));
//
//                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILL", delApBill)));
//                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APPERD", delApBill)));
//            }
//        }


        ColumnDataValue delCondition = new ColumnDataValue();
        delCondition.add("EID", DataValues.newString(req.geteId()));
        delCondition.add("CMNO", DataValues.newString(req.getRequest().getCmNo()));

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BANKPAY", delCondition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_BANKPAYDETAIL", delCondition)));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BankPayDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BankPayDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BankPayDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_BankPayDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_BankPayDeleteReq> getRequestType() {
        return new TypeToken<DCP_BankPayDeleteReq>() {
        };
    }

    @Override
    protected DCP_BankPayDeleteRes getResponseType() {
        return new DCP_BankPayDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_BankPayDeleteReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT a.STATUS,b.PENDOFFSETNO  ")
                .append(" FROM DCP_BANKPAY a ")
                .append(" LEFT JOIN DCP_BANKPAYDETAIL b on a.eid=b.eid and a.CMNO=b.CMNO ")
        ;

        querySql.append("WHERE a.EID='").append(req.geteId()).append("'");

        if (Check.isNotEmpty(req.getRequest().getCmNo())) {
            querySql.append(" AND a.CMNO='").append(req.getRequest().getCmNo()).append("'");
        }

        return querySql.toString();
    }
}
