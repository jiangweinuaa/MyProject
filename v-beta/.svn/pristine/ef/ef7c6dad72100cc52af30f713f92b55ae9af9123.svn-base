package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_AccCOACreateReq;
import com.dsc.spos.json.cust.res.DCP_AccCOACreateRes;
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

public class DCP_AccCOACreate  extends SPosAdvanceService<DCP_AccCOACreateReq, DCP_AccCOACreateRes> {

    @Override
    protected void processDUID(DCP_AccCOACreateReq req, DCP_AccCOACreateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        //String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String createTime =	new SimpleDateFormat("HHmmss").format(new Date());
        String createDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        DCP_AccCOACreateReq.levelRequest request = req.getRequest();
        String coaRefID = request.getCoaRefID();
        String subjectId = request.getSubjectId();
        //String accountId = request.getAccountId();
        List<DCP_AccCOACreateReq.AccList> accList = request.getAccList();
        String coaSql="select * from dcp_coa a where a.eid='"+req.geteId()+"' " +
                " and a.coarefId='"+coaRefID+"' and a.subjectid='"+subjectId+"'  ";
        List<Map<String, Object>> coaList = this.doQueryData(coaSql, null);
        if(coaList.size()>0){
            List<Map<String, Object>> allCoaList = coaList.stream().filter(x -> x.get("ACCOUNTID").toString().equals("ALLCOA")).collect(Collectors.toList());
            if(allCoaList.size()>0) {
                if (accList.size() > 0) {
                    for (DCP_AccCOACreateReq.AccList acc : accList) {
                        List<Map<String, Object>> filterRows = coaList.stream().filter(x -> x.get("ACCOUNTID").toString().equals(acc.getAccountId())).collect(Collectors.toList());
                        if (filterRows.size() <= 0) {
                            ColumnDataValue setColumns = new ColumnDataValue();

                            setColumns.add("EID", DataValues.newString(eId));
                            setColumns.add("STATUS", DataValues.newString(allCoaList.get(0).get("STATUS").toString()));
                            setColumns.add("SUBJECTID", DataValues.newString(request.getSubjectId()));
                            setColumns.add("SUBJECTNAME", DataValues.newString(allCoaList.get(0).get("SUBJECTNAME").toString()));
                            setColumns.add("AUXILIARYTYPE", DataValues.newString(allCoaList.get(0).get("AUXILIARYTYPE").toString()));
                            setColumns.add("MEMO", DataValues.newString(allCoaList.get(0).get("MEMO").toString()));
                            setColumns.add("COAREFID", DataValues.newString(request.getCoaRefID()));
                            setColumns.add("ACCOUNTID", DataValues.newString(acc.getAccountId()));
                            setColumns.add("SUBJECTCAT", DataValues.newString(allCoaList.get(0).get("SUBJECTCAT").toString()));
                            setColumns.add("UPSUBJECTID", DataValues.newString(allCoaList.get(0).get("UPSUBJECTID").toString()));
                            setColumns.add("LEVELID", DataValues.newString(allCoaList.get(0).get("LEVELID").toString()));
                            setColumns.add("SUBJECTPROPERTY", DataValues.newString(allCoaList.get(0).get("SUBJECTPROPERTY").toString()));
                            setColumns.add("SUBJECTTYPE", DataValues.newString(allCoaList.get(0).get("SUBJECTTYPE").toString()));
                            setColumns.add("DIRECTION", DataValues.newString(allCoaList.get(0).get("DIRECTION").toString()));
                            setColumns.add("ISDIRECTION", DataValues.newString(allCoaList.get(0).get("ISDIRECTION").toString()));
                            setColumns.add("EXPTYPE", DataValues.newString(allCoaList.get(0).get("EXPTYPE").toString()));
                            setColumns.add("FINANALSOURCE", DataValues.newString(allCoaList.get(0).get("FINANALSOURCE").toString()));
                            setColumns.add("ISCASHSUBJECT", DataValues.newString(allCoaList.get(0).get("ISCASHSUBJECT").toString()));
                            setColumns.add("ISENABLEDPTMNG", DataValues.newString(allCoaList.get(0).get("ISENABLEDPTMNG").toString()));
                            setColumns.add("ISENABLETRADOBJMNG", DataValues.newString(allCoaList.get(0).get("ISENABLETRADOBJMNG").toString()));
                            setColumns.add("ISENABLEPRODCATMNG", DataValues.newString(allCoaList.get(0).get("ISENABLEPRODCATMNG").toString()));
                            setColumns.add("ISENABLEMANMNG", DataValues.newString(allCoaList.get(0).get("ISENABLEMANMNG").toString()));
                            setColumns.add("ISMULTICURMNG", DataValues.newString(allCoaList.get(0).get("ISMULTICURMNG").toString()));
                            setColumns.add("ISSUBSYSSUBJECT", DataValues.newString(allCoaList.get(0).get("ISSUBSYSSUBJECT").toString()));
                            setColumns.add("DRCASHCHGCODE", DataValues.newString(allCoaList.get(0).get("DRCASHCHGCODE").toString()));
                            setColumns.add("CRCASHCHGCODE", DataValues.newString(allCoaList.get(0).get("CRCASHCHGCODE").toString()));
                            setColumns.add("ISFREECHARS1", DataValues.newString(allCoaList.get(0).get("ISFREECHARS1").toString()));
                            setColumns.add("FREECHARS1_TYPEID", DataValues.newString(allCoaList.get(0).get("FREECHARS1_TYPEID").toString()));
                            setColumns.add("FREECHARS1_CTRLMODE", DataValues.newString(allCoaList.get(0).get("FREECHARS1_CTRLMODE").toString()));
                            setColumns.add("ISFREECHARS2", DataValues.newString(allCoaList.get(0).get("ISFREECHARS2").toString()));
                            setColumns.add("FREECHARS2_TYPEID", DataValues.newString(allCoaList.get(0).get("FREECHARS2_TYPEID").toString()));
                            setColumns.add("FREECHARS2_CTRLMODE", DataValues.newString(allCoaList.get(0).get("FREECHARS2_CTRLMODE").toString()));
                            setColumns.add("ISFREECHARS3", DataValues.newString(allCoaList.get(0).get("ISFREECHARS3").toString()));
                            setColumns.add("FREECHARS3_TYPEID", DataValues.newString(allCoaList.get(0).get("FREECHARS3_TYPEID").toString()));
                            setColumns.add("FREECHARS3_CTRLMODE", DataValues.newString(allCoaList.get(0).get("FREECHARS3_CTRLMODE").toString()));


                            setColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                            setColumns.add("CREATE_DATE", DataValues.newString(createDate));
                            setColumns.add("CREATE_TIME", DataValues.newString(createTime));
                            String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
                            DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean setib = new InsBean("DCP_COA", setColumnNames);
                            setib.addValues(setDataValues);
                            this.addProcessData(new DataProcessBean(setib));
                        }
                    }
                }
            }

        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_AccCOACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_AccCOACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_AccCOACreateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_AccCOACreateReq req) throws Exception {
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
    protected TypeToken<DCP_AccCOACreateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_AccCOACreateReq>(){};
    }

    @Override
    protected DCP_AccCOACreateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_AccCOACreateRes();
    }

}
