package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ApSetupSubjectCreateReq;
import com.dsc.spos.json.cust.res.DCP_ApSetupSubjectCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ApSetupSubjectCreate  extends SPosAdvanceService<DCP_ApSetupSubjectCreateReq, DCP_ApSetupSubjectCreateRes> {

    @Override
    protected void processDUID(DCP_ApSetupSubjectCreateReq req, DCP_ApSetupSubjectCreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        DCP_ApSetupSubjectCreateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("HHmmss").format(new Date());String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String sql="select * from DCP_APSETUPSUBJECT a " +
                " where a.eid='"+req.geteId()+"'" +
                " and a.accountid='"+req.getRequest().getAccountId()+"' " +
                "" ;
        List<Map<String, Object>> list = this.doQueryData(sql, null);


        List<DCP_ApSetupSubjectCreateReq.SetupList> setupList = req.getRequest().getSetupList();
        if(CollUtil.isNotEmpty(setupList)){
            for (DCP_ApSetupSubjectCreateReq.SetupList singleSetup : setupList){

                List<Map<String, Object>> collect = list.stream().filter(x -> x.get("SETUPTYPE").toString().equals(singleSetup.getSetupType())
                        && x.get("SETUPITEM").toString().equals(singleSetup.getSetupItem())).collect(Collectors.toList());
                if(CollUtil.isNotEmpty(collect)){
                    continue;
                }

                ColumnDataValue mainColumns=new ColumnDataValue();
                mainColumns.add("EID", DataValues.newString(eId));
                mainColumns.add("STATUS", DataValues.newString("1"));
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

                mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                mainColumns.add("CREATE_DATE", DataValues.newString(createDate));
                mainColumns.add("CREATE_TIME", DataValues.newString(createTime));



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
    protected List<InsBean> prepareInsertData(DCP_ApSetupSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ApSetupSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ApSetupSubjectCreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ApSetupSubjectCreateReq req) throws Exception {
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
    protected TypeToken<DCP_ApSetupSubjectCreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ApSetupSubjectCreateReq>(){};
    }

    @Override
    protected DCP_ApSetupSubjectCreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ApSetupSubjectCreateRes();
    }

}


