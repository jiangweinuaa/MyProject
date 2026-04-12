package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TaxSubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_TaxSubjectUpdateRes;
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

public class DCP_TaxSubjectUpdate extends SPosAdvanceService<DCP_TaxSubjectUpdateReq, DCP_TaxSubjectUpdateRes> {

    @Override
    protected void processDUID(DCP_TaxSubjectUpdateReq req, DCP_TaxSubjectUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_TaxSubjectUpdateReq.Level1Elm request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefID();

        String status = request.getStatus();

        String createDate="";
        String createTime="";
        String createBy="";

        String sql="select * from DCP_TAXSUBJECT   a " +
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
        DelBean db1 = new DelBean("DCP_TAXSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(request.getCoaRefID(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        List<DCP_TaxSubjectUpdateReq.SetUpList> setUpList = request.getSetupList();

        for (DCP_TaxSubjectUpdateReq.SetUpList detail : setUpList){
            ColumnDataValue setColumns=new ColumnDataValue();
            setColumns.add("EID", DataValues.newString(eId));
            setColumns.add("STATUS", DataValues.newString(status));
            setColumns.add("ACCOUNTID", DataValues.newString(detail.getAccountId()));
            setColumns.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
            setColumns.add("SETUPTYPE", DataValues.newString(detail.getSetupType()));
            setColumns.add("COAREFID", DataValues.newString(coaRefID));
            setColumns.add("SUBJECTID", DataValues.newString(detail.getSubjectId()));

            setColumns.add("CREATEBY", DataValues.newString(createBy));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            setColumns.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("MODIFY_DATE", DataValues.newString(modifyDate));
            setColumns.add("MODIFY_TIME", DataValues.newString(modifyTime));
            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
            InsBean setib=new InsBean("DCP_TAXSUBJECT",setColumnNames);
            setib.addValues(setDataValues);
            this.addProcessData(new DataProcessBean(setib));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_TaxSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_TaxSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_TaxSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_TaxSubjectUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_TaxSubjectUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_TaxSubjectUpdateReq>(){};
    }

    @Override
    protected DCP_TaxSubjectUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_TaxSubjectUpdateRes();
    }

}


