package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PurInReconDetailDeleterReq;
import com.dsc.spos.json.cust.res.DCP_PurInReconDetailDeleteRes;
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

public class DCP_PurInReconDetailDelete extends SPosAdvanceService<DCP_PurInReconDetailDeleterReq, DCP_PurInReconDetailDeleteRes> {
    @Override
    protected void processDUID(DCP_PurInReconDetailDeleterReq req, DCP_PurInReconDetailDeleteRes res) throws Exception {

        List<Map<String, Object>> qData = doQueryData(getQuerySql(req), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无符合条件数据！");
        }
        String status = qData.get(0).get("STATUS").toString();
        if ("Y".equals(status)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已审核，不可删除！");
        }


        for (DCP_PurInReconDetailDeleterReq.ItemList oneItem : req.getRequest().getItemList()) {
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", req.geteId());
            condition.add("PURINVNO", req.getRequest().getPurInvNo());
            condition.add("ITEM", oneItem.getItem());


            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURINVDETAIL", condition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PURINVRECON", condition)));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurInReconDetailDeleterReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurInReconDetailDeleterReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurInReconDetailDeleterReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurInReconDetailDeleterReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurInReconDetailDeleterReq> getRequestType() {
        return new TypeToken<DCP_PurInReconDetailDeleterReq>() {
        };
    }

    @Override
    protected DCP_PurInReconDetailDeleteRes getResponseType() {
        return new DCP_PurInReconDetailDeleteRes();
    }

    @Override
    protected String getQuerySql(DCP_PurInReconDetailDeleterReq req) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append("select a.* ")
                .append(" FROM DCP_PURINV a ");
        sql.append(" WHERE a.EID='").append(req.geteId()).append("' ");

        if (StringUtils.isNotEmpty(req.getRequest().getPurInvNo())) {
            sql.append(" AND a.PURINVNO='").append(req.getRequest().getPurInvNo()).append("' ");
        }

        return sql.toString();
    }

}
