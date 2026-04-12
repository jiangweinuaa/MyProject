package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_COACreateReq;
import com.dsc.spos.json.cust.req.DCP_COAUpdateReq;
import com.dsc.spos.json.cust.res.DCP_COAUpdateRes;
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

public class DCP_COAUpdate extends SPosAdvanceService<DCP_COAUpdateReq, DCP_COAUpdateRes> {

    @Override
    protected void processDUID(DCP_COAUpdateReq req, DCP_COAUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String modifyTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String modifyDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_COAUpdateReq.levelRequest request = req.getRequest();
        List<DCP_COAUpdateReq.AccountList> accountList = request.getAccountList();
        if(CollUtil.isNotEmpty(accountList)){
            req.getRequest().setAccType("2");//改成指定
        }else{
            req.getRequest().setAccType("1");
        }


        //先删除
        DelBean db1 = new DelBean("DCP_COA");
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("SUBJECTID", new DataValue(req.getRequest().getSubjectId(), Types.VARCHAR));
        db1.addCondition("COAREFID", new DataValue(req.getRequest().getCoaRefID(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        String coaSql="select * from DCP_COA a where a.eid='"+eId+"' and a.coarefid='"+ request.getCoaRefID()+"'" +
                " and a.subjectid='"+request.getSubjectId()+"'";
        List<Map<String, Object>> coaList = this.doQueryData(coaSql, null);
        String createDate=modifyDate;
        String createTime=modifyTime;
        String createBy=req.getEmployeeNo();
        if(coaList.size()>0){
            createDate=coaList.get(0).get("CREATE_DATE").toString();
            createTime=coaList.get(0).get("CREATE_TIME").toString();
            createBy=coaList.get(0).get("CREATEBY").toString();
        }


        //加一个ALLCOA  的账套  这个账套虚拟的没实际意义
        DCP_COAUpdateReq.AccountList tempAccount = req.new AccountList();
        tempAccount.setAccountId("ALLCOA");
        tempAccount.setAccountName("ALLCOA");
        accountList.add(tempAccount);

        if("1".equals(req.getRequest().getAccType())){
            String accountSql="select * from DCP_ACOUNT_SETTING a where a.eid='"+req.geteId()+"' and a.status='100'";
            List<Map<String, Object>> accountList1 = this.doQueryData(accountSql, null);
            for (Map<String, Object> account : accountList1) {
                DCP_COAUpdateReq.AccountList tempAccount1 = req.new AccountList();
                tempAccount1.setAccountId(account.get("ACCOUNTID").toString());
                tempAccount1.setAccountName(account.get("ACCOUNT").toString());
                accountList.add(tempAccount1);
            }
        }

        for (DCP_COAUpdateReq.AccountList account : accountList) {
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


            setColumns.add("CREATEBY", DataValues.newString(createBy));
            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
            setColumns.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            setColumns.add("MODIFY_DATE", DataValues.newString(modifyDate));
            setColumns.add("MODIFY_TIME", DataValues.newString(modifyTime));

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
    protected List<InsBean> prepareInsertData(DCP_COAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_COAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_COAUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_COAUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_COAUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_COAUpdateReq>(){};
    }

    @Override
    protected DCP_COAUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_COAUpdateRes();
    }

}

