package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApSetupSubjectUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ApSetupSubjectUpdateRes;
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
import java.util.stream.Collectors;

public class DCP_ApSetupSubjectUpdate extends SPosAdvanceService<DCP_ApSetupSubjectUpdateReq, DCP_ApSetupSubjectUpdateRes> {

    @Override
    protected void processDUID(DCP_ApSetupSubjectUpdateReq req, DCP_ApSetupSubjectUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_ApSetupSubjectUpdateReq.LevelRequest request = req.getRequest();

        String accountId = request.getAccountId();
        String coaRefID = request.getCoaRefID();

        String status = request.getStatus();

        String createDate="";
        String createTime="";
        String createBy="";

        String sql="select * from DCP_APSETUPSUBJECT   a " +
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

        DelBean db1 = new DelBean("DCP_APSETUPSUBJECT");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ACCOUNTID", new DataValue(accountId, Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(request.getCoaRefID(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        List<DCP_ApSetupSubjectUpdateReq.SetupList> setupList = req.getRequest().getSetupList();
        if(CollUtil.isNotEmpty(setupList)){
            for (DCP_ApSetupSubjectUpdateReq.SetupList singleSetup : setupList){

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("STATUS", DataValues.newString(req.getRequest().getStatus()));
                mainColumns.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountId()));
                mainColumns.add("SETUPTYPE", DataValues.newString(singleSetup.getSetupType()));
                mainColumns.add("SETUPITEM", DataValues.newString(singleSetup.getSetupItem()));
                mainColumns.add("SETUPDISCRIP", DataValues.newString(singleSetup.getSetupDiscrip()));
                mainColumns.add("COAREFID", DataValues.newString(req.getRequest().getCoaRefID()));
                mainColumns.add("SUBJECTID", DataValues.newString(singleSetup.getSubjectId()));
                mainColumns.add("SUBJECTDBCR", DataValues.newString(singleSetup.getSubjectDBCR()));
                mainColumns.add("SUBJECTSUMTYPE", DataValues.newString(singleSetup.getSubjectSumType()));
                mainColumns.add("DISCSUBJECT", DataValues.newString(singleSetup.getDiscSubject()));
                mainColumns.add("MEMO", DataValues.newString(singleSetup.getMemo()));

                mainColumns.add("CREATEBY", DataValues.newString(createBy));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));
                mainColumns.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
                mainColumns.add("MODIFY_DATE", DataValues.newString(modifyDate));
                mainColumns.add("MODIFY_TIME", DataValues.newString(modifyTime));

                String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_APSETUPSUBJECT",mainColumnNames);
                ib.addValues(mainDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ApSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApSetupSubjectUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApSetupSubjectUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ApSetupSubjectUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ApSetupSubjectUpdateReq>(){};
    }

    @Override
    protected DCP_ApSetupSubjectUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ApSetupSubjectUpdateRes();
    }

}


