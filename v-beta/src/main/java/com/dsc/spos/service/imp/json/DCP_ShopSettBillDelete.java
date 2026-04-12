package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ShopSettBillDelectReq;
import com.dsc.spos.json.cust.res.DCP_ShopSettBillDelectRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ShopSettBillDelete extends SPosAdvanceService<DCP_ShopSettBillDelectReq, DCP_ShopSettBillDelectRes> {
    @Override
    protected void processDUID(DCP_ShopSettBillDelectReq req, DCP_ShopSettBillDelectRes res) throws Exception {


        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据!");
        }
        String status = qData.get(0).get("STATUS").toString();

        if ("Y".equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据已审核，不可删除!");
        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("RECONNO", req.getRequest().getReconNo());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SHOPSETTBILL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SHOPSETTBILLDETAIL", condition)));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_SHOPRECEIVE", condition)));


        ColumnDataValue dcp_sale = new ColumnDataValue();
        ColumnDataValue saleCondition = new ColumnDataValue();

        String saleNo = qData.get(0).get("ARNO").toString();

        saleCondition.add("EID", req.geteId());
        saleCondition.add("SALENO", saleNo);

        dcp_sale.add("ISUPLOADED", "N");

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SALE", saleCondition, dcp_sale)));

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ShopSettBillDelectReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ShopSettBillDelectReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ShopSettBillDelectReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ShopSettBillDelectReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ShopSettBillDelectReq> getRequestType() {
        return new TypeToken<DCP_ShopSettBillDelectReq>() {
        };
    }

    @Override
    protected DCP_ShopSettBillDelectRes getResponseType() {
        return new DCP_ShopSettBillDelectRes();
    }

    @Override
    protected String getQuerySql(DCP_ShopSettBillDelectReq req) throws Exception {
        StringBuilder querySql = new StringBuilder();

        querySql.append("SELECT a.* ")
                .append("FROM DCP_SHOPSETTBILL a ")
        ;
        querySql.append("WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getReconNo())) {
            querySql.append(" AND a.RECONNO = '").append(req.getRequest().getReconNo()).append("'");
        }

        if (StringUtils.isNotEmpty(req.getRequest().getShopId())) {
            querySql.append(" AND a.SHOPID = '").append(req.getRequest().getShopId()).append("'");
        }

        return querySql.toString();
    }
}
