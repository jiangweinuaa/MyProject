package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_APInvCreateReq;
import com.dsc.spos.json.cust.res.DCP_APInvCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;

public class DCP_APInvCreate extends SPosAdvanceService<DCP_APInvCreateReq, DCP_APInvCreateRes> {

    @Override
    protected void processDUID(DCP_APInvCreateReq req, DCP_APInvCreateRes res) throws Exception {

        ColumnDataValue dcp_purinv = new ColumnDataValue();

        dcp_purinv.add("EID", DataValues.newString(req.geteId()));
        dcp_purinv.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        dcp_purinv.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
        dcp_purinv.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));

        dcp_purinv.add("STATUS", DataValues.newString(0));
        dcp_purinv.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        dcp_purinv.add("PURINVNO", DataValues.newString(req.getRequest().getPurInvNo()));


        for (DCP_APInvCreateReq.ApInvList oneApInv:req.getRequest().getApInvList()){
            ColumnDataValue dcp_purinvdetail = new ColumnDataValue();

            dcp_purinvdetail.add("EID", DataValues.newString(req.geteId()));
            dcp_purinvdetail.add("PURINVNO", DataValues.newString(req.getRequest().getPurInvNo()));
            dcp_purinvdetail.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            dcp_purinvdetail.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
            dcp_purinvdetail.add("CREATE_TIME", DataValues.newString(DateFormatUtils.getNowPlainTime()));
            dcp_purinvdetail.add("STATUS", DataValues.newString(0));
            dcp_purinvdetail.add("ITEM", DataValues.newString(oneApInv.getItem()));
//            dcp_purinvdetail.add("SOURCETYPE", DataValues.newString(oneApInv.getItem()));
//            dcp_purinvdetail.add("SOURCENO", DataValues.newString(oneApInv.getItem()));
//            dcp_purinvdetail.add("SOURCEORG", DataValues.newString(oneApInv.getItem()));
//            dcp_purinvdetail.add("SOURCENOSEQ", DataValues.newString(oneApInv.getItem()));


        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");





    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_APInvCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_APInvCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_APInvCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_APInvCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_APInvCreateReq> getRequestType() {
        return new TypeToken<DCP_APInvCreateReq>() {
        };
    }

    @Override
    protected DCP_APInvCreateRes getResponseType() {
        return new DCP_APInvCreateRes();
    }
}
