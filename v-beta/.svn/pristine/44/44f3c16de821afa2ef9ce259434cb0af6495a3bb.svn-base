package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PrintTemplateCreateReq;
import com.dsc.spos.json.cust.res.DCP_PrintTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PrintTemplateCreate extends SPosAdvanceService<DCP_PrintTemplateCreateReq, DCP_PrintTemplateCreateRes> {
    @Override
    protected void processDUID(DCP_PrintTemplateCreateReq req, DCP_PrintTemplateCreateRes res) throws Exception {

        DCP_PrintTemplateCreateReq.Request request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        String modularNo = request.getModularNo();
        String sql="select * from DCP_MODULAR_PRINT a where a.eid='"+req.geteId()+"' and a.modularno='"+modularNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()>0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "重复创建！".toString());

        }

        List<DCP_PrintTemplateCreateReq.DetailInfo> detail = request.getDetail();

        for (DCP_PrintTemplateCreateReq.DetailInfo detailInfo : detail){
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
            printColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
            printColumns.add("CREATEDEPTID", DataValues.newString(req.getDefDepartNo()));
            printColumns.add("CREATETIME", DataValues.newDate(createTime));

            String[] printColumnNames = printColumns.getColumns().toArray(new String[0]);
            DataValue[] printDataValues = printColumns.getDataValues().toArray(new DataValue[0]);
            InsBean printIb=new InsBean("DCP_MODULAR_PRINT",printColumnNames);
            printIb.addValues(printDataValues);
            this.addProcessData(new DataProcessBean(printIb));

            List<DCP_PrintTemplateCreateReq.restrictOpList> restrictOpList = detailInfo.getRestrictOpList();
            for (DCP_PrintTemplateCreateReq.restrictOpList restrictOp : restrictOpList){

                ColumnDataValue opColumns=new ColumnDataValue();
                opColumns.add("EID", DataValues.newString(req.geteId()));
                opColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                opColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                opColumns.add("USERTYPE", DataValues.newString(restrictOp.getOpType()));
                opColumns.add("USERID", DataValues.newString(restrictOp.getId()));
                opColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                opColumns.add("CREATEDEPTID", DataValues.newString(req.getDefDepartNo()));
                opColumns.add("CREATETIME", DataValues.newDate(createTime));

                String[] opColumnNames = opColumns.getColumns().toArray(new String[0]);
                DataValue[] opDataValues = opColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MODULAR_PRINT_USER",opColumnNames);
                ib.addValues(opDataValues);
                this.addProcessData(new DataProcessBean(ib));
            }

            List<DCP_PrintTemplateCreateReq.restrictOrgList> restrictOrgList = detailInfo.getRestrictOrgList();
            for (DCP_PrintTemplateCreateReq.restrictOrgList restrictOrg : restrictOrgList){

                ColumnDataValue orgColumns=new ColumnDataValue();
                orgColumns.add("EID", DataValues.newString(req.geteId()));
                orgColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                orgColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                orgColumns.add("ORGANIZATIONNO", DataValues.newString(restrictOrg.getOrgNo()));
                orgColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                orgColumns.add("CREATEDEPTID", DataValues.newString(req.getDefDepartNo()));
                orgColumns.add("CREATETIME", DataValues.newDate(createTime));

                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib=new InsBean("DCP_MODULAR_PRINT_ORG",orgColumnNames);
                ib.addValues(orgDataValues);
                this.addProcessData(new DataProcessBean(ib));

            }
            List<DCP_PrintTemplateCreateReq.restrictCustList> restrictCustList = detailInfo.getRestrictCustList();
            for (DCP_PrintTemplateCreateReq.restrictCustList restrictCust : restrictCustList){

                ColumnDataValue cuColumns=new ColumnDataValue();
                cuColumns.add("EID", DataValues.newString(req.geteId()));
                cuColumns.add("MODULARNO", DataValues.newString(request.getModularNo()));
                cuColumns.add("PRINTNO", DataValues.newString(detailInfo.getPrintNo()));
                cuColumns.add("CUSTOMERNO", DataValues.newString(restrictCust.getCustomerNo()));
                cuColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                cuColumns.add("CREATEDEPTID", DataValues.newString(req.getDefDepartNo()));
                cuColumns.add("CREATETIME", DataValues.newDate(createTime));

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
    protected List<InsBean> prepareInsertData(DCP_PrintTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PrintTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PrintTemplateCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PrintTemplateCreateReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_PrintTemplateCreateReq> getRequestType() {
        return new TypeToken<DCP_PrintTemplateCreateReq>() {

        };
    }

    @Override
    protected DCP_PrintTemplateCreateRes getResponseType() {
        return new DCP_PrintTemplateCreateRes();
    }
}
