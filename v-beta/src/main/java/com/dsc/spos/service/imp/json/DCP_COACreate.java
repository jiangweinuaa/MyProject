package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_COACreateReq;
import com.dsc.spos.json.cust.res.DCP_COACreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_COACreate extends SPosAdvanceService<DCP_COACreateReq, DCP_COACreateRes> {

    @Override
    protected void processDUID(DCP_COACreateReq req, DCP_COACreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_COACreateReq.levelRequest request = req.getRequest();
        List<DCP_COACreateReq.AccountList> accountList = request.getAccountList();

        //前端传指定  又不传accountList  会有问题
        if(CollUtil.isNotEmpty(accountList)){
            req.getRequest().setAccType("2");//改成指定
        }else{
            req.getRequest().setAccType("1");//改成全部
        }

        String valSql1="select * from dcp_coa a where a.eid='"+req.geteId()+"' " +
                " and a.coarefid='"+request.getCoaRefID()+"' " +
                " and a.subjectid='"+request.getSubjectId()+"' ";
        List<Map<String, Object>> valList = this.doQueryData(valSql1, null);
        if(valList.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "已存在".toString());
        }



        //加一个ALLCOA  的账套  这个账套虚拟的没实际意义
        DCP_COACreateReq.AccountList tempAccount = req.new AccountList();
        tempAccount.setAccountId("ALLCOA");
        tempAccount.setAccountName("ALLCOA");
        accountList.add(tempAccount);

        if("1".equals(req.getRequest().getAccType())){
            String accountSql="select * from DCP_ACOUNT_SETTING a where a.eid='"+req.geteId()+"' and a.status='100'";
            List<Map<String, Object>> accountList1 = this.doQueryData(accountSql, null);
            for (Map<String, Object> account : accountList1) {
                DCP_COACreateReq.AccountList tempAccount1 = req.new AccountList();
                tempAccount1.setAccountId(account.get("ACCOUNTID").toString());
                tempAccount1.setAccountName(account.get("ACCOUNT").toString());
                accountList.add(tempAccount1);
            }
        }

        for (DCP_COACreateReq.AccountList account : accountList) {
            ColumnDataValue setColumns=new ColumnDataValue();

            setColumns.add("EID", DataValues.newString(eId));
            setColumns.add("STATUS", DataValues.newString(request.getStatus()));
            setColumns.add("ACCTYPE", DataValues.newString(request.getAccType()));
            setColumns.add("SUBJECTID", DataValues.newString(request.getSubjectId()));
            setColumns.add("SUBJECTNAME", DataValues.newString(request.getSubjectName()));
            setColumns.add("AUXILIARYTYPE", DataValues.newString(request.getAuxiliaryType()));
            setColumns.add("MEMO", DataValues.newString(request.getMemo()));
            setColumns.add("COAREFID", DataValues.newString(request.getCoaRefID()));
            setColumns.add("ACCOUNTID", DataValues.newString(account.getAccountId()));
            setColumns.add("SUBJECTCAT", DataValues.newString(request.getSubjectCat()));
            setColumns.add("UPSUBJECTID", DataValues.newString(request.getUpSubjectId()));
            setColumns.add("FIRSTSUBJECTID", DataValues.newString(request.getFirstSubjectId()));
            setColumns.add("LEVELID", DataValues.newString(request.getLevelID()));
            setColumns.add("SUBJECTPROPERTY", DataValues.newString(request.getSubjectProperty()));
            setColumns.add("SUBJECTTYPE", DataValues.newString(request.getSubjectType()));
            setColumns.add("DIRECTION", DataValues.newString(request.getDirection()));
            setColumns.add("ISDIRECTION", DataValues.newString(request.getIsDirection()));
            setColumns.add("EXPTYPE", DataValues.newString(request.getExpType()));
            setColumns.add("FINANALSOURCE", DataValues.newString(request.getFinAnalSource()));
            setColumns.add("ISCASHSUBJECT", DataValues.newString(request.getIsCashSubject()));
            setColumns.add("ISENABLEDPTMNG", DataValues.newString(request.getIsEnableDptMng()));
            setColumns.add("ISENABLETRADOBJMNG", DataValues.newString(request.getIsEnableTradObjMng()));
            setColumns.add("ISENABLEPRODCATMNG", DataValues.newString(request.getIsEnableProdCatMng()));
            setColumns.add("ISENABLEMANMNG", DataValues.newString(request.getIsEnableManMng()));
            setColumns.add("ISMULTICURMNG", DataValues.newString(request.getIsMultiCurMng()));
            setColumns.add("ISSUBSYSSUBJECT", DataValues.newString(request.getIsSubsysSubject()));
            setColumns.add("DRCASHCHGCODE", DataValues.newString(request.getDrCashChgCode()));
            setColumns.add("CRCASHCHGCODE", DataValues.newString(request.getCrCashChgCode()));
            setColumns.add("ISFREECHARS1", DataValues.newString(request.getIsFreeChars1()));
            setColumns.add("FREECHARS1_TYPEID", DataValues.newString(request.getFreeChars1TypeId()));
            setColumns.add("FREECHARS1_CTRLMODE", DataValues.newString(request.getFreeChars1CtrlMode()));
            setColumns.add("ISFREECHARS2", DataValues.newString(request.getIsFreeChars2()));
            setColumns.add("FREECHARS2_TYPEID", DataValues.newString(request.getFreeChars2TypeId()));
            setColumns.add("FREECHARS2_CTRLMODE", DataValues.newString(request.getFreeChars2CtrlMode()));
            setColumns.add("ISFREECHARS3", DataValues.newString(request.getIsFreeChars3()));
            setColumns.add("FREECHARS3_TYPEID", DataValues.newString(request.getFreeChars3TypeId()));
            setColumns.add("FREECHARS3_CTRLMODE", DataValues.newString(request.getFreeChars3CtrlMode()));


            setColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
            InsBean setib=new InsBean("DCP_COA",setColumnNames);
            setib.addValues(setDataValues);
            this.addProcessData(new DataProcessBean(setib));
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_COACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_COACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_COACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_COACreateReq req) throws Exception {
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
    protected TypeToken<DCP_COACreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_COACreateReq>(){};
    }

    @Override
    protected DCP_COACreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_COACreateRes();
    }

}

