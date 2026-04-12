package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ArWrtOffDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ArWrtOffDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class DCP_ArWrtOffDelete extends SPosAdvanceService<DCP_ArWrtOffDeleteReq, DCP_ArWrtOffDeleteRes> {

    @Override
    protected void processDUID(DCP_ArWrtOffDeleteReq req, DCP_ArWrtOffDeleteRes res) throws Exception {


        String arNo = req.getRequest().getArNo();

        if (CollectionUtils.isNotEmpty(req.getRequest().getWrtOffList())) {
            ColumnDataValue delCondition = new ColumnDataValue();

            delCondition.add("EID", DataValues.newString(req.geteId()));
            delCondition.add("ARNO", DataValues.newString(arNo));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARWRTOFF", delCondition)));

            for (DCP_ArWrtOffDeleteReq.WrtOffList oneWrtoff : req.getRequest().getWrtOffList()) {

                if (CollectionUtils.isNotEmpty(oneWrtoff.getArRecList())) {

                    for (DCP_ArWrtOffDeleteReq.ArRecList item : oneWrtoff.getArRecList()) {
                        ColumnDataValue itemDelCondition = delCondition.copyNew();
                        itemDelCondition.add("ITEM", DataValues.newString(item.getItem()));
                        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLREC", delCondition)));
                    }

                } else {
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLREC", delCondition)));
                }

                if (CollectionUtils.isNotEmpty(oneWrtoff.getArWFList())) {

                    for (DCP_ArWrtOffDeleteReq.ArWFList item : oneWrtoff.getArWFList()) {
                        ColumnDataValue delWrtOffCondition = new ColumnDataValue();
                        delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
                        delWrtOffCondition.add("WRTOFFBILLNO", DataValues.newString(arNo));
                        delWrtOffCondition.add("ITEM", DataValues.newString(item.getItem()));
                        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLWRTOFF", delWrtOffCondition)));

                    }

                } else {
                    ColumnDataValue delWrtOffCondition = new ColumnDataValue();
                    delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
                    delWrtOffCondition.add("WRTOFFBILLNO", DataValues.newString(arNo));
                    this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLWRTOFF", delWrtOffCondition)));

                }

            }

        } else {

            ColumnDataValue delCondition = new ColumnDataValue();

            delCondition.add("EID", DataValues.newString(req.geteId()));
            delCondition.add("ARNO", DataValues.newString(arNo));

            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARWRTOFF", delCondition)));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLREC", delCondition)));

            ColumnDataValue delWrtOffCondition = new ColumnDataValue();
            delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
            delWrtOffCondition.add("WRTOFFBILLNO", DataValues.newString(arNo));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_ARBILLWRTOFF", delWrtOffCondition)));

        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ArWrtOffDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ArWrtOffDeleteReq> getRequestType() {
        return new TypeToken<DCP_ArWrtOffDeleteReq>() {
        };
    }

    @Override
    protected DCP_ArWrtOffDeleteRes getResponseType() {
        return new DCP_ArWrtOffDeleteRes();
    }
}
