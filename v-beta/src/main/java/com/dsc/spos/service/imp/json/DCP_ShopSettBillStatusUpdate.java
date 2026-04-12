package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopSettBillStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ShopSettBillStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ShopSettBillStatusUpdate extends SPosAdvanceService<DCP_ShopSettBillStatusUpdateReq, DCP_ShopSettBillStatusUpdateRes> {


    @Override
    protected void processDUID(DCP_ShopSettBillStatusUpdateReq req, DCP_ShopSettBillStatusUpdateRes res) throws Exception {

        String querySql = " SELECT STATUS,ARNO,ARNO2 FROM" +
                " DCP_SHOPSETTBILL a WHERE a.EID='" + req.geteId() + "'" +
                " AND a.RECONNO='" + req.getRequest().getReconNo() + "'";
        List<Map<String, Object>> qData = doQueryData(querySql,null);

        if (CollectionUtils.isEmpty(qData)){
            throw  new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"无符合条件数据!");
        }
        String oStatus = qData.get(0).get("STATUS").toString();
        String arNo = qData.get(0).get("ARNO").toString();
        String arNo2 = qData.get(0).get("ARNO2").toString();


        String opType = req.getRequest().getOpType();

        ColumnDataValue condition = new ColumnDataValue();

        ColumnDataValue dcp_shopSettBill = new ColumnDataValue();
        ColumnDataValue dcp_shopSettBilDetail = new ColumnDataValue();
        ColumnDataValue dcp_shopReceive = new ColumnDataValue();

        condition.add("EID", req.geteId());
        condition.add("RECONNO", req.getRequest().getReconNo());

        String date = DateFormatUtils.getNowPlainDate();
        String time = DateFormatUtils.getNowPlainTime();

        if (Constant.OPR_TYPE_CONFIRM.equals(opType)) {
            dcp_shopSettBill.add("STATUS", "Y");
            dcp_shopSettBilDetail.add("STATUS", "Y");
            dcp_shopReceive.add("STATUS", "Y");

            dcp_shopSettBill.add("CONFIRMBY", req.getEmployeeNo());
            dcp_shopSettBill.add("CONFIRM_DATE", date);
            dcp_shopSettBill.add("CONFIRM_TIME", time);

            dcp_shopSettBilDetail.add("CONFIRMBY", req.getEmployeeNo());
            dcp_shopSettBilDetail.add("CONFIRM_DATE", date);
            dcp_shopSettBilDetail.add("CONFIRM_TIME", time);

            dcp_shopReceive.add("CONFIRMBY", req.getEmployeeNo());
            dcp_shopReceive.add("CONFIRM_DATE", date);
            dcp_shopReceive.add("CONFIRM_TIME", time);


        } else if (Constant.OPR_TYPE_UNCONFIRM.equals(opType)) {

            if ("Y".equals(oStatus) && (StringUtils.isNotEmpty(arNo) || StringUtils.isNotEmpty(arNo2) ) ){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"应收单号、会员/储值单号 非空，不得取消审核!");
            }

            dcp_shopSettBill.add("STATUS", "N");
            dcp_shopSettBilDetail.add("STATUS", "N");
            dcp_shopReceive.add("STATUS", "N");

            dcp_shopSettBill.add("CONFIRMBY", "");
            dcp_shopSettBill.add("CONFIRM_DATE", "");
            dcp_shopSettBill.add("CONFIRM_TIME", "");

            dcp_shopSettBilDetail.add("CONFIRMBY", "");
            dcp_shopSettBilDetail.add("CONFIRM_DATE", "");
            dcp_shopSettBilDetail.add("CONFIRM_TIME", "");

            dcp_shopReceive.add("CONFIRMBY", "");
            dcp_shopReceive.add("CONFIRM_DATE", "");
            dcp_shopReceive.add("CONFIRM_TIME", "");


        } else if (Constant.OPR_TYPE_CANCEL.equals(opType)) {
            dcp_shopSettBill.add("STATUS", "X");
            dcp_shopSettBilDetail.add("STATUS", "X");
            dcp_shopReceive.add("STATUS", "X");

            dcp_shopSettBill.add("CANCELBY", req.getEmployeeNo());
            dcp_shopSettBill.add("CANCEL_DATE", date);
            dcp_shopSettBill.add("CANCEL_TIME", time);

            dcp_shopSettBilDetail.add("CANCELBY", req.getEmployeeNo());
            dcp_shopSettBilDetail.add("CANCEL_DATE", date);
            dcp_shopSettBilDetail.add("CANCEL_TIME", time);

            dcp_shopReceive.add("CANCELBY", req.getEmployeeNo());
            dcp_shopReceive.add("CANCEL_DATE", date);
            dcp_shopReceive.add("CANCEL_TIME", time);

        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SHOPSETTBILL", condition, dcp_shopSettBill)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SHOPSETTBILLDETAIL", condition, dcp_shopSettBilDetail)));
        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SHOPRECEIVE", condition, dcp_shopReceive)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopSettBillStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopSettBillStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopSettBillStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_ShopSettBillStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_ShopSettBillStatusUpdateRes getResponseType() {
        return new DCP_ShopSettBillStatusUpdateRes();
    }
}
