package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TopupSetUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TopupSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_TopupSetUpdate extends SPosAdvanceService<DCP_TopupSetUpdateReq, DCP_TopupSetUpdateRes> {
    @Override
    protected void processDUID(DCP_TopupSetUpdateReq req, DCP_TopupSetUpdateRes res) throws Exception {


        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT STATUS FROM DCP_TOPUPSET WHERE EID = '").append(req.geteId()).append("'")
                .append(" AND CORP='").append(req.getRequest().getCorp()).append("'");

        List<Map<String, Object>> qData = this.doQueryData(builder.toString(), null);
        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "配置不存在,不可编辑!");
        }
//        String qStatus = qData.get(0).get("STATUS").toString();

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", req.geteId());
        condition.add("CORP", req.getRequest().getCorp());
        condition.add("TOPUPORG", req.getRequest().getTopupOrg());

        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_TOPUPSET", condition)));

        for (DCP_TopupSetUpdateReq.SetList oneSet : req.getRequest().getSetList()) {
            ColumnDataValue dcp_topupset = new ColumnDataValue();

            dcp_topupset.add("EID", req.geteId());
            dcp_topupset.add("CORP", req.getRequest().getCorp());
            dcp_topupset.add("TOPUPORG", req.getRequest().getTopupOrg());

            if (StringUtils.isNotEmpty(req.getRequest().getStatus())) {
                dcp_topupset.add("STATUS", req.getRequest().getStatus());
            } else {
                dcp_topupset.add("STATUS", "100");
            }

            dcp_topupset.add("TOPUPPRODID", oneSet.getTopupProdID());
            dcp_topupset.add("TOPUPPAYTYPE", oneSet.getTopupPayType());
            dcp_topupset.add("CONSPAYTYPE", oneSet.getConsPayType());
            dcp_topupset.add("CONSRPODID", oneSet.getConsProdID());

            dcp_topupset.add("CREATEBY", req.getEmployeeNo());
            dcp_topupset.add("CREATE_DATE", DateFormatUtils.getNowPlainDate());
            dcp_topupset.add("CREATE_TIME", DateFormatUtils.getNowPlainTime());

            dcp_topupset.add("MODIFYBY", req.getEmployeeNo());
            dcp_topupset.add("MODIFY_DATE", DateFormatUtils.getNowPlainDate());
            dcp_topupset.add("MODIFY_TIME", DateFormatUtils.getNowPlainTime());

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_TOPUPSET", dcp_topupset)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TopupSetUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TopupSetUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TopupSetUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_TopupSetUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_TopupSetUpdateReq> getRequestType() {
        return new TypeToken<DCP_TopupSetUpdateReq>() {
        };
    }

    @Override
    protected DCP_TopupSetUpdateRes getResponseType() {
        return new DCP_TopupSetUpdateRes();
    }
}
