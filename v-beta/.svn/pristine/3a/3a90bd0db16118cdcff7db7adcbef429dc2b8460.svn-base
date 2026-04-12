package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FullInseartCoaReq;
import com.dsc.spos.json.cust.res.DCP_FullInseartCoaRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_FullInseartCoa extends SPosAdvanceService<DCP_FullInseartCoaReq, DCP_FullInseartCoaRes> {

    @Override
    protected void processDUID(DCP_FullInseartCoaReq req, DCP_FullInseartCoaRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();

        if("1".equals(req.getRequest().getOprType())||"2".equals(req.getRequest().getOprType())){
            //启用 禁用  accountId 必填
            if(Check.Null(req.getRequest().getAccountId())){
                res.setSuccess(false);
                res.setServiceDescription("accountId 必填");
                return;
            }

            // 更新status操作 放到后面更新 可以保证科目有了

        }

        //查询所有的 ALLCOA
        String allSql="select * from DCP_COA where eid='"+req.geteId()+"' ";
        List<Map<String, Object>> allCoaList = this.doQueryData(allSql, null);

        //这个过滤了有多少个科目
        List<Map<String, Object>> collect = allCoaList.stream().filter(x -> x.get("ACCOUNTID").toString().equals("ALLCOA")).distinct().collect(Collectors.toList());

        List<String> subjectIds = collect.stream().map(x -> x.get("SUBJECTID").toString()).distinct().collect(Collectors.toList());

        String acSql="select distinct accountid from DCP_ACOUNT_SETTING where eid='"+req.geteId()+"' ";

        if(Check.NotNull(req.getRequest().getAccountId())){
            acSql+=" and accountid='"+req.getRequest().getAccountId()+"' ";
        }

        List<Map<String, Object>> acList = this.doQueryData(acSql, null);
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        if(collect.size()>0&&acList.size()>0){
            for (Map<String, Object> map : collect){
                String subjectId = map.get("SUBJECTID").toString();
                for (Map<String, Object> accountMap : acList){
                    String accountId = accountMap.get("ACCOUNTID").toString();
                    List<Map<String, Object>> filterRows = allCoaList.stream().filter(x -> x.get("SUBJECTID").toString().equals(subjectId)
                            && x.get("ACCOUNTID").toString().equals(accountId)).collect(Collectors.toList());
                    if(filterRows.size()<=0){
                        //不存在新增一笔

                        ColumnDataValue setColumns=new ColumnDataValue();

                        setColumns.add("EID", DataValues.newString(eId));
                        setColumns.add("STATUS", DataValues.newString("100"));
                        setColumns.add("ACCTYPE", DataValues.newString(map.get("ACCTYPE").toString()));
                        setColumns.add("SUBJECTID", DataValues.newString(map.get("SUBJECTID").toString()));
                        setColumns.add("SUBJECTNAME", DataValues.newString(map.get("SUBJECTNAME").toString()));
                        setColumns.add("AUXILIARYTYPE", DataValues.newString(map.get("AUXILIARYTYPE").toString()));
                        setColumns.add("MEMO", DataValues.newString(map.get("MEMO").toString()));
                        setColumns.add("COAREFID", DataValues.newString(map.get("COAREFID").toString()));
                        setColumns.add("ACCOUNTID", DataValues.newString(accountId));
                        setColumns.add("SUBJECTCAT", DataValues.newString(map.get("SUBJECTCAT").toString()));
                        setColumns.add("UPSUBJECTID", DataValues.newString(map.get("UPSUBJECTID").toString()));
                        setColumns.add("FIRSTSUBJECTID", DataValues.newString(map.get("FIRSTSUBJECTID").toString()));
                        setColumns.add("LEVELID", DataValues.newString(map.get("LEVELID").toString()));
                        setColumns.add("SUBJECTPROPERTY", DataValues.newString(map.get("SUBJECTPROPERTY").toString()));
                        setColumns.add("SUBJECTTYPE", DataValues.newString(map.get("SUBJECTTYPE").toString()));
                        setColumns.add("DIRECTION", DataValues.newString(map.get("DIRECTION").toString()));
                        setColumns.add("ISDIRECTION", DataValues.newString(map.get("ISDIRECTION").toString()));
                        setColumns.add("EXPTYPE", DataValues.newString(map.get("EXPTYPE").toString()));
                        setColumns.add("FINANALSOURCE", DataValues.newString(map.get("FINANALSOURCE").toString()));
                        setColumns.add("ISCASHSUBJECT", DataValues.newString(map.get("ISCASHSUBJECT").toString()));
                        setColumns.add("ISENABLEDPTMNG", DataValues.newString(map.get("ISENABLEDPTMNG").toString()));
                        setColumns.add("ISENABLETRADOBJMNG", DataValues.newString(map.get("ISENABLETRADOBJMNG").toString()));
                        setColumns.add("ISENABLEPRODCATMNG", DataValues.newString(map.get("ISENABLEPRODCATMNG").toString()));
                        setColumns.add("ISENABLEMANMNG", DataValues.newString(map.get("ISENABLEMANMNG").toString()));
                        setColumns.add("ISMULTICURMNG", DataValues.newString(map.get("ISMULTICURMNG").toString()));
                        setColumns.add("ISSUBSYSSUBJECT", DataValues.newString(map.get("ISSUBSYSSUBJECT").toString()));
                        setColumns.add("DRCASHCHGCODE", DataValues.newString(map.get("DRCASHCHGCODE").toString()));
                        setColumns.add("CRCASHCHGCODE", DataValues.newString(map.get("CRCASHCHGCODE").toString()));
                        setColumns.add("ISFREECHARS1", DataValues.newString(map.get("ISFREECHARS1").toString()));
                        setColumns.add("FREECHARS1_TYPEID", DataValues.newString(map.get("FREECHARS1_TYPEID").toString()));
                        setColumns.add("FREECHARS1_CTRLMODE", DataValues.newString(map.get("FREECHARS1_CTRLMODE").toString()));
                        setColumns.add("ISFREECHARS2", DataValues.newString(map.get("ISFREECHARS2").toString()));
                        setColumns.add("FREECHARS2_TYPEID", DataValues.newString(map.get("FREECHARS2_TYPEID").toString()));
                        setColumns.add("FREECHARS2_CTRLMODE", DataValues.newString(map.get("FREECHARS2_CTRLMODE").toString()));
                        setColumns.add("ISFREECHARS3", DataValues.newString(map.get("ISFREECHARS3").toString()));
                        setColumns.add("FREECHARS3_TYPEID", DataValues.newString(map.get("FREECHARS3_TYPEID").toString()));
                        setColumns.add("FREECHARS3_CTRLMODE", DataValues.newString(map.get("FREECHARS3_CTRLMODE").toString()));
                        setColumns.add("FULLINSERT", DataValues.newString("Y"));


                        setColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                        setColumns.add("CREATE_DATE", DataValues.newString(createDate));
                        setColumns.add("CREATE_TIME", DataValues.newString(createTime));
                        String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
                        DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean setib=new InsBean("DCP_COA",setColumnNames);
                        setib.addValues(setDataValues);
                        this.addProcessData(new DataProcessBean(setib));

                    }
                }


            }
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);

        if("1".equals(req.getRequest().getOprType())||"2".equals(req.getRequest().getOprType())){
            //启用 禁用  accountId 必填
            if(Check.Null(req.getRequest().getAccountId())){
                res.setSuccess(false);
                res.setServiceDescription("accountId 必填");
                return;
            }

            // 更新status操作
            UptBean ub1 = new UptBean("DCP_COA");
            ub1.addUpdateValue("STATUS", new DataValue("1".equals(req.getRequest().getOprType())?"100":"0", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ACCOUNTID", new DataValue(req.getRequest().getAccountId(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            this.doExecuteDataToDB();
            res.setSuccess(true);
            return;
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FullInseartCoaReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FullInseartCoaReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FullInseartCoaReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FullInseartCoaReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_FullInseartCoaReq> getRequestType() {
        return new TypeToken<DCP_FullInseartCoaReq>() {
        };
    }

    @Override
    protected DCP_FullInseartCoaRes getResponseType() {
        return new DCP_FullInseartCoaRes();
    }
}

