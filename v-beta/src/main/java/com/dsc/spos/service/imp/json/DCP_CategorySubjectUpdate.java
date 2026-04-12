package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CategorySubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CategorySubjectUpdateRes;
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

public class DCP_CategorySubjectUpdate extends SPosAdvanceService<DCP_CategorySubjectUpdateReq, DCP_CategorySubjectUpdateRes> {

    @Override
    protected void processDUID(DCP_CategorySubjectUpdateReq req, DCP_CategorySubjectUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_CategorySubjectUpdateReq.Level1Elm request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefId();

        String status = request.getStatus();

        String createDate="";
        String createTime="";
        String createBy="";

        String sql="select * from DCP_CATEGORYSUBJECT  a " +
                " where a.eid='"+req.geteId()+"'" +
                " and a.accountid='"+accountId+"' " +
                " and a.coarefid='"+coaRefID+"' "
                ;
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){
            createBy=list.get(0).get("CREATEBY").toString();
            createDate=list.get(0).get("CREATE_DATE").toString();
            createTime=list.get(0).get("CREATE_TIME").toString();
        }

        //删除
        DelBean db1 = new DelBean("DCP_CATEGORYSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(request.getCoaRefId(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        List<DCP_CategorySubjectUpdateReq.SetUpList> setUpList = request.getSetUpList();

        for (DCP_CategorySubjectUpdateReq.SetUpList detail : setUpList){
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

            setColumns.add("CREATEBY", DataValues.newString(createBy));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            setColumns.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("MODIFY_DATE", DataValues.newString(modifyDate));
            setColumns.add("MODIFY_TIME", DataValues.newString(modifyTime));
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
    protected List<InsBean> prepareInsertData(DCP_CategorySubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CategorySubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CategorySubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CategorySubjectUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_CategorySubjectUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_CategorySubjectUpdateReq>(){};
    }

    @Override
    protected DCP_CategorySubjectUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_CategorySubjectUpdateRes();
    }

}


