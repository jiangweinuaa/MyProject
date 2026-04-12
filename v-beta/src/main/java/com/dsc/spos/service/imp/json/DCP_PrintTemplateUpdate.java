package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PrintTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PrintTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PrintTemplateUpdate  extends SPosAdvanceService<DCP_PrintTemplateUpdateReq, DCP_PrintTemplateUpdateRes> {
    @Override
    protected void processDUID(DCP_PrintTemplateUpdateReq req, DCP_PrintTemplateUpdateRes res) throws Exception {

        DCP_PrintTemplateUpdateReq.Request request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String sql="select a.printno,a.CREATEOPID,a.CREATEDEPTID,to_char(a.createtime,'yyyyMMdd HH:mm:ss') as createtime from DCP_MODULAR_PRINT a where a.eid='"+req.geteId()+"' and a.modularno='"+request.getModularNo()+"'";
        List<Map<String, Object>> printList = this.doQueryData(sql, null);

        DelBean db1 = new DelBean("DCP_MODULAR_PRINT");
        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db1.addCondition("MODULARNO", new DataValue(request.getModularNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        DelBean db2 = new DelBean("DCP_MODULAR_PRINT_USER");
        db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db2.addCondition("MODULARNO", new DataValue(request.getModularNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_MODULAR_PRINT_ORG");
        db3.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db3.addCondition("MODULARNO", new DataValue(request.getModularNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));
        DelBean db4 = new DelBean("DCP_MODULAR_PRINT_CUSTOMER");
        db4.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        db4.addCondition("MODULARNO", new DataValue(request.getModularNo(), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db4));


        List<DCP_PrintTemplateUpdateReq.DetailInfo> detail = request.getDetail();

        for (DCP_PrintTemplateUpdateReq.DetailInfo detailInfo : detail){

            List<Map<String, Object>> filterRows = printList.stream().filter(map -> map.get("PRINTNO").equals(detailInfo.getPrintNo())).collect(Collectors.toList());

            ColumnDataValue printColumns=new ColumnDataValue();
            printColumns.add("EID", DataValues.newString(req.geteId()));
            printColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
            printColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
            printColumns.add("PRINTNAME", DataValues.newString(detailInfo.getPrintName()));
            printColumns.add("PRONAME", DataValues.newString(detailInfo.getProName()));
            printColumns.add("PARAMETER", DataValues.newString(detailInfo.getParameter()));
            printColumns.add("ISSTANDARD", DataValues.newString(detailInfo.getIsStandard()));
            printColumns.add("ISDEFAULT", DataValues.newString(detailInfo.getIsDefault()));
            printColumns.add("PRINTTYPE", DataValues.newString(detailInfo.getPrintType()));
            printColumns.add("RESTRICTOP", DataValues.newString(detailInfo.getRestrictOp()));
            printColumns.add("RESTRICTORG", DataValues.newString(detailInfo.getRestrictOrg()));
            printColumns.add("RESTRICTCUST", DataValues.newString(detailInfo.getRestrictCust()));
            printColumns.add("STATUS", DataValues.newString(detailInfo.getStatus()));
            if(filterRows.size()>0){
                printColumns.add("CREATEOPID", DataValues.newString(filterRows.get(0).get("CREATEOPID").toString()));
                printColumns.add("CREATEDEPTID", DataValues.newString(filterRows.get(0).get("CREATEDEPTID").toString()));
                printColumns.add("CREATETIME", DataValues.newDate(filterRows.get(0).get("CREATETIME").toString()));
            }else{
                printColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                printColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                printColumns.add("CREATETIME", DataValues.newDate(createTime));
            }
            printColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            printColumns.add("LASTMODITIME", DataValues.newDate(createTime));


            String[] printColumnNames = printColumns.getColumns().toArray(new String[0]);
            DataValue[] printDataValues = printColumns.getDataValues().toArray(new DataValue[0]);
            InsBean printIb=new InsBean("DCP_MODULAR_PRINT",printColumnNames);
            printIb.addValues(printDataValues);
            this.addProcessData(new DataProcessBean(printIb));

            List<DCP_PrintTemplateUpdateReq.restrictOpList> restrictOpList = detailInfo.getRestrictOpList();
            for (DCP_PrintTemplateUpdateReq.restrictOpList restrictOp : restrictOpList){

                ColumnDataValue opColumns=new ColumnDataValue();
                opColumns.add("EID", DataValues.newString(req.geteId()));
                opColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                opColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                opColumns.add("USERTYPE", DataValues.newString(restrictOp.getOpType()));
                opColumns.add("USERID", DataValues.newString(restrictOp.getId()));
                opColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                opColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                opColumns.add("CREATETIME", DataValues.newDate(createTime));

                opColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                opColumns.add("LASTMODITIME", DataValues.newDate(createTime));


                String[] opColumnNames = opColumns.getColumns().toArray(new String[0]);
                DataValue[] opDataValues = opColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MODULAR_PRINT_USER",opColumnNames);
                ib.addValues(opDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }

            List<DCP_PrintTemplateUpdateReq.restrictOrgList> restrictOrgList = detailInfo.getRestrictOrgList();
            for (DCP_PrintTemplateUpdateReq.restrictOrgList restrictOrg : restrictOrgList){

                ColumnDataValue orgColumns=new ColumnDataValue();
                orgColumns.add("EID", DataValues.newString(req.geteId()));
                orgColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                orgColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                orgColumns.add("ORGANIZATIONNO", DataValues.newString(restrictOrg.getOrgNo()));
                orgColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                orgColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                orgColumns.add("CREATETIME", DataValues.newDate(createTime));

                orgColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                orgColumns.add("LASTMODITIME", DataValues.newDate(createTime));

                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MODULAR_PRINT_ORG",orgColumnNames);
                ib.addValues(orgDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }
            List<DCP_PrintTemplateUpdateReq.restrictCustList> restrictCustList = detailInfo.getRestrictCustList();
            for (DCP_PrintTemplateUpdateReq.restrictCustList restrictCust : restrictCustList){

                ColumnDataValue cuColumns=new ColumnDataValue();
                cuColumns.add("EID", DataValues.newString(req.geteId()));
                cuColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                cuColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                cuColumns.add("CUSTOMERNO", DataValues.newString(restrictCust.getCustomerNo()));
                cuColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                cuColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                cuColumns.add("CREATETIME", DataValues.newDate(createTime));

                cuColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                cuColumns.add("LASTMODITIME", DataValues.newDate(createTime));

                String[] cuColumnNames = cuColumns.getColumns().toArray(new String[0]);
                DataValue[] cuDataValues = cuColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MODULAR_PRINT_CUSTOMER",cuColumnNames);
                ib.addValues(cuDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }

        }



        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PrintTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PrintTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PrintTemplateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PrintTemplateUpdateReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_PrintTemplateUpdateReq> getRequestType() {
        return new TypeToken<DCP_PrintTemplateUpdateReq>() {

        };
    }

    @Override
    protected DCP_PrintTemplateUpdateRes getResponseType() {
        return new DCP_PrintTemplateUpdateRes();
    }
}
