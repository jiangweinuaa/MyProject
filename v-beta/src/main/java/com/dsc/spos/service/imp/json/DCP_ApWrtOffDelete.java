package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ApWrtOffDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ApWrtOffDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DCP_ApWrtOffDelete extends SPosAdvanceService<DCP_ApWrtOffDeleteReq, DCP_ApWrtOffDeleteRes> {

    @Override
    protected void processDUID(DCP_ApWrtOffDeleteReq req, DCP_ApWrtOffDeleteRes res) throws Exception {


        String wrtOffNo = req.getRequest().getWrtOffNo();
        List<DCP_ApWrtOffDeleteReq.WrtOffList> wrtOffList = req.getRequest().getWrtOffList();
        if(CollUtil.isEmpty(wrtOffList)){
            ColumnDataValue delWrtOffCondition = new ColumnDataValue();
            delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
            delWrtOffCondition.add("WRTOFFNO", DataValues.newString(wrtOffNo));
            delWrtOffCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APWRTOFF", delWrtOffCondition)));


            ColumnDataValue delWrtOffBillCondition = new ColumnDataValue();
            delWrtOffBillCondition.add("EID", DataValues.newString(req.geteId()));
            delWrtOffBillCondition.add("WRTOFFNO", DataValues.newString(wrtOffNo));
            delWrtOffBillCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLWRTOFF", delWrtOffBillCondition)));

            ColumnDataValue delApBillPmt = new ColumnDataValue();
            delApBillPmt.add("EID", DataValues.newString(req.geteId()));
            delApBillPmt.add("WRTOFFNO", DataValues.newString(wrtOffNo));
            delApBillPmt.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLPMT", delApBillPmt)));

        }else{
            for (DCP_ApWrtOffDeleteReq.WrtOffList wrtOff : wrtOffList) {

                if(CollUtil.isNotEmpty(wrtOff.getApWFList())){
                    for (DCP_ApWrtOffDeleteReq.ApWFList apWF : wrtOff.getApWFList()){
                        if(Check.NotNull(apWF.getItem())){
                            ColumnDataValue delWrtOffBillCondition = new ColumnDataValue();
                            delWrtOffBillCondition.add("EID", DataValues.newString(req.geteId()));
                            delWrtOffBillCondition.add("WRTOFFNO", DataValues.newString(wrtOffNo));
                            delWrtOffBillCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
                            delWrtOffBillCondition.add("ITEM", DataValues.newString(apWF.getItem()));

                            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLWRTOFF", delWrtOffBillCondition)));

                        }
                    }
                }

                if(CollUtil.isNotEmpty(wrtOff.getPmtList())){
                    for (DCP_ApWrtOffDeleteReq.PmtList pmt : wrtOff.getPmtList()) {
                        if (Check.NotNull(pmt.getItem())) {
                            ColumnDataValue delApBillPmt = new ColumnDataValue();
                            delApBillPmt.add("EID", DataValues.newString(req.geteId()));
                            delApBillPmt.add("WRTOFFNO", DataValues.newString(wrtOffNo));
                            delApBillPmt.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
                            delApBillPmt.add("ITEM", DataValues.newString(pmt.getItem()));
                            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APBILLPMT", delApBillPmt)));
                        }
                    }
                }


            }
        }

        //条件：账套+作业类型+核销单号+ent--》删除整单
        //条件：账套+作业类型+核销单号+ent+【wrtoffList】--》删除单据内对应【账款明细】或【付款明细】内对应的项次

        this.doExecuteDataToDB();

        String detailSql="select * from DCP_APBILLWRTOFF where eid='"+req.geteId()+"' and accountid='"+req.getRequest().getAccountId()+"' " +
                " and WRTOFFNO='"+wrtOffNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(detailSql, null);
        String pmtSql="select * from DCP_APBILLPMT where eid='"+req.geteId()+"' and accountid='"+req.getRequest().getAccountId()+"' " +
                " and WRTOFFNO='"+wrtOffNo+"' ";
        List<Map<String, Object>> list1 = this.doQueryData(pmtSql, null);
        if(list.size()<=0&&list1.size()<=0){
            ColumnDataValue delWrtOffCondition = new ColumnDataValue();
            delWrtOffCondition.add("EID", DataValues.newString(req.geteId()));
            delWrtOffCondition.add("WRTOFFNO", DataValues.newString(wrtOffNo));
            delWrtOffCondition.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
            this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_APWRTOFF", delWrtOffCondition)));
            this.doExecuteDataToDB();
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApWrtOffDeleteReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_ApWrtOffDeleteReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ApWrtOffDeleteReq> getRequestType() {
        return new TypeToken<DCP_ApWrtOffDeleteReq>() {
        };
    }

    @Override
    protected DCP_ApWrtOffDeleteRes getResponseType() {
        return new DCP_ApWrtOffDeleteRes();
    }
}
