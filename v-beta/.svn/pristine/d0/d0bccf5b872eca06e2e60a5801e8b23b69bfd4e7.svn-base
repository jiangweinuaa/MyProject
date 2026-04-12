package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CategorySubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_CategorySubjectCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_CategorySubjectCreate extends SPosAdvanceService<DCP_CategorySubjectCreateReq, DCP_CategorySubjectCreateRes> {

    @Override
    protected void processDUID(DCP_CategorySubjectCreateReq req, DCP_CategorySubjectCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_CategorySubjectCreateReq.Level1Elm request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefId();

        //accountid  和 coarefid 一一对应的
        String sql="select * from DCP_CATEGORYSUBJECT a " +
                " where a.eid='"+req.geteId()+"'" +
                " and a.accountid='"+accountId+"' " +
                "" ;
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "已存在不可保存！".toString());
        }

        String status = request.getStatus();
        List<DCP_CategorySubjectCreateReq.SetUpList> setUpList = request.getSetUpList();

        for (DCP_CategorySubjectCreateReq.SetUpList detail : setUpList){
            ColumnDataValue setColumns=new ColumnDataValue();

            setColumns.add("EID", DataValues.newString(eId));
            setColumns.add("STATUS", DataValues.newString(status));
            setColumns.add("ACCOUNTID", DataValues.newString(accountId));
            setColumns.add("COAREFID", DataValues.newString(coaRefID));
            setColumns.add("CATEGORY", DataValues.newString(detail.getCategory()));
            setColumns.add("REVSUBJECT", DataValues.newString(detail.getRevSubject()));
            setColumns.add("COSTSUBJECT", DataValues.newString(detail.getCostSubject()));
            setColumns.add("INVSUBJECT", DataValues.newString(detail.getInvSubject()));
            setColumns.add("DISCSUBJECT", DataValues.newString(detail.getDiscSubject()));
            setColumns.add("MEMO", DataValues.newString(detail.getMemo()));
            setColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
            InsBean setib=new InsBean("DCP_CATEGORYSUBJECT",setColumnNames);
            setib.addValues(setDataValues);
            this.addProcessData(new DataProcessBean(setib));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CategorySubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CategorySubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CategorySubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CategorySubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_CategorySubjectCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_CategorySubjectCreateReq>(){};
    }

    @Override
    protected DCP_CategorySubjectCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_CategorySubjectCreateRes();
    }

}

