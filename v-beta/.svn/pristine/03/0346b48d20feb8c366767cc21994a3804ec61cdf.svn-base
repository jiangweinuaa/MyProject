package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BsNoSetSubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_BsNoSetSubjectCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_BsNoSetSubjectCreate extends SPosAdvanceService<DCP_BsNoSetSubjectCreateReq, DCP_BsNoSetSubjectCreateRes> {

    @Override
    protected void processDUID(DCP_BsNoSetSubjectCreateReq req, DCP_BsNoSetSubjectCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_BsNoSetSubjectCreateReq.Level1Elm request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefId();

        //accountid  和 coarefid 一一对应的
        String sql="select * from DCP_BSNOSETUPSUBJECT a " +
                " where a.eid='"+req.geteId()+"'" +
                " and a.accountid='"+accountId+"' " +
                //" and a.coarefid='"+coaRefID+"' " +
                "" ;
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, "已存在不可保存！".toString());
        }

        String status = request.getStatus();
        List<DCP_BsNoSetSubjectCreateReq.SetUpList> setUpList = request.getSetUpList();

        for (DCP_BsNoSetSubjectCreateReq.SetUpList detail : setUpList){
            ColumnDataValue setColumns=new ColumnDataValue();

            setColumns.add("EID", DataValues.newString(eId));
            setColumns.add("STATUS", DataValues.newString(status));
            setColumns.add("ACCOUNTID", DataValues.newString(accountId));
            setColumns.add("COAREFID", DataValues.newString(coaRefID));
            setColumns.add("BSNO", DataValues.newString(detail.getBsNo()));
            setColumns.add("MANUEXPSUBJECT", DataValues.newString(detail.getManuExpSubject()));
            setColumns.add("SALESEXPSUBJECT", DataValues.newString(detail.getSalesExpSubject()));
            setColumns.add("MGMTEXPSUBJECT", DataValues.newString(detail.getMgmtExpSubject()));
            setColumns.add("RDEXPSUBJECT", DataValues.newString(detail.getRdExpSubject()));
            setColumns.add("MEMO", DataValues.newString(detail.getMemo()));
            setColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
            InsBean setib=new InsBean("DCP_BSNOSETUPSUBJECT",setColumnNames);
            setib.addValues(setDataValues);
            this.addProcessData(new DataProcessBean(setib));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BsNoSetSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BsNoSetSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BsNoSetSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BsNoSetSubjectCreateReq req) throws Exception {
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
    protected TypeToken<DCP_BsNoSetSubjectCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_BsNoSetSubjectCreateReq>(){};
    }

    @Override
    protected DCP_BsNoSetSubjectCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_BsNoSetSubjectCreateRes();
    }

}

