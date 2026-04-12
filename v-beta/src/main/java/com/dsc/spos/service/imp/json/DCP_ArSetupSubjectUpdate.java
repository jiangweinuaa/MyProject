package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ArSetupSubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ArSetupSubjectUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_ArSetupSubjectUpdate extends SPosAdvanceService<DCP_ArSetupSubjectUpdateReq, DCP_ArSetupSubjectUpdateRes> {

    @Override
    protected void processDUID(DCP_ArSetupSubjectUpdateReq req, DCP_ArSetupSubjectUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_ArSetupSubjectUpdateReq.Level1Elm request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefID();
        String setupType = request.getSetupType();
        String status = request.getStatus();

        String createDate="";
        String createTime="";
        String createBy="";

        String sql="select * from DCP_ARSETUPSUBJECT a " +
                " where a.eid='"+req.geteId()+"'" +
                " and a.accountid='"+accountId+"' " +
                " and a.coarefid='"+coaRefID+"' " +
                " and a.setuptype='"+setupType+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){
            createBy=list.get(0).get("CREATEBY").toString();
            createDate=list.get(0).get("CREATE_DATE").toString();
            createTime=list.get(0).get("CREATE_TIME").toString();
        }

        //删除
        DelBean db1 = new DelBean("DCP_ARSETUPSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(request.getCoaRefID(), Types.VARCHAR));
        db1.addCondition("SETUPTYPE", new DataValue(setupType, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        List<DCP_ArSetupSubjectUpdateReq.SetUpList> setUpList = request.getSetUpList();

        for (DCP_ArSetupSubjectUpdateReq.SetUpList detail : setUpList){
            ColumnDataValue setColumns=new ColumnDataValue();
            setColumns.add("EID", DataValues.newString(eId));
            setColumns.add("STATUS", DataValues.newString(status));
            setColumns.add("ACCOUNTID", DataValues.newString(accountId));
            setColumns.add("SETUPTYPE", DataValues.newString(setupType));
            setColumns.add("SETUPITEM", DataValues.newString(detail.getSetupItem()));
            setColumns.add("SETUPDISCRIP", DataValues.newString(detail.getSetupDiscrip()));
            setColumns.add("COAREFID", DataValues.newString(coaRefID));
            setColumns.add("SUBJECTID", DataValues.newString(detail.getSubjectId()));
            setColumns.add("SUBJECTDBCR", DataValues.newString(detail.getSubjectDBCR()));
            setColumns.add("SUBJECTSUMTYPE", DataValues.newString(detail.getSubjectSumType()));
            setColumns.add("DISCSUBJECT", DataValues.newString(detail.getDiscSubject()));
            setColumns.add("MEMO", DataValues.newString(detail.getMemo()));
            setColumns.add("CREATEBY", DataValues.newString(createBy));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            setColumns.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("MODIFY_DATE", DataValues.newString(modifyDate));
            setColumns.add("MODIFY_TIME", DataValues.newString(modifyTime));
            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
            InsBean setib=new InsBean("DCP_ARSETUPSUBJECT",setColumnNames);
            setib.addValues(setDataValues);
            this.addProcessData(new DataProcessBean(setib));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ArSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ArSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ArSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ArSetupSubjectUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ArSetupSubjectUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ArSetupSubjectUpdateReq>(){};
    }

    @Override
    protected DCP_ArSetupSubjectUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ArSetupSubjectUpdateRes();
    }

}


