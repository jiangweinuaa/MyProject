package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TopupSetCreateReq;
import com.dsc.spos.json.cust.res.DCP_TopupSetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_TopupSetCreate extends SPosAdvanceService<DCP_TopupSetCreateReq, DCP_TopupSetCreateRes> {

    @Override
    protected void processDUID(DCP_TopupSetCreateReq req, DCP_TopupSetCreateRes res) throws Exception {

        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT TOPUPPRODID FROM DCP_TOPUPSET WHERE EID = '").append(req.geteId()).append("'")
                .append(" AND CORP='").append(req.getRequest().getCorp()).append("'");

        builder.append(" AND ( 1=2 ");
        for (DCP_TopupSetCreateReq.SetList oneSet : req.getRequest().getSetList()) {
            builder.append(" OR TOPUPPRODID = '").append(oneSet.getTopupProdID()).append("'");
        }
        builder.append(" )");
        List<Map<String, Object>> qData = this.doQueryData(builder.toString(), null);

        if (CollectionUtils.isNotEmpty(qData)) {
            StringBuilder errorMsg = new StringBuilder();
            for (Map<String, Object> map : qData) {
                errorMsg.append(map.get("TOPUPPRODID").toString()).append("\n");
            }
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errorMsg + "已存在,不可保存!");
        }

        for (DCP_TopupSetCreateReq.SetList oneSet : req.getRequest().getSetList()) {
            ColumnDataValue dcp_topupset = new ColumnDataValue();

            dcp_topupset.add("EID", req.geteId());
            dcp_topupset.add("CORP", req.getRequest().getCorp());
            dcp_topupset.add("TOPUPORG", req.getRequest().getTopupOrg());

            dcp_topupset.add("STATUS", "100");

            dcp_topupset.add("TOPUPPRODID", oneSet.getTopupProdID());
            dcp_topupset.add("TOPUPPAYTYPE", oneSet.getTopupPayType());
            dcp_topupset.add("CONSPAYTYPE", oneSet.getConsPayType());
            dcp_topupset.add("CONSRPODID", oneSet.getConsProdID());

            dcp_topupset.add("CREATEBY", req.getEmployeeNo());
            dcp_topupset.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_topupset.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_TOPUPSET", dcp_topupset)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TopupSetCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TopupSetCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TopupSetCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_TopupSetCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TopupSetCreateReq> getRequestType() {
        return new TypeToken<DCP_TopupSetCreateReq>() {
        };
    }

    @Override
    protected DCP_TopupSetCreateRes getResponseType() {
        return new DCP_TopupSetCreateRes();
    }
}
