package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateUpdateRes;
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

public class DCP_ProdTemplateUpdate extends SPosAdvanceService<DCP_ProdTemplateUpdateReq, DCP_ProdTemplateUpdateRes> {

    @Override
    protected void processDUID(DCP_ProdTemplateUpdateReq req, DCP_ProdTemplateUpdateRes res) throws Exception {
        // TODO 自动生成的方法存根
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        DCP_ProdTemplateUpdateReq.LevelRequest request = req.getRequest();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        List<DCP_ProdTemplateUpdateReq.Detail> details = request.getDetail();
        List<DCP_ProdTemplateUpdateReq.OrgList> orgList = request.getOrgList();

        String templateId = request.getTemplateId();
        //2.1重复性检查：当restrictOrg=1指定组织时，按组织编号+商品编号检查若存在其他模板(restrictOrg=1指定组织)，返回报错提示商品对应xx组织已存在模板！
        String sql = "select a.templateid,b.pluno,c.organizationno,a.restrictOrg from DCP_PRODTEMPLATE a" +
                " left join DCP_PRODTEMPLATE_GOODS b on a.eid=b.eid and a.templateid=b.templateid " +
                " left join DCP_PRODTEMPLATE_RANGE c on a.eid=c.eid and a.templateid=c.templateid " +
                " where a.eid='"+eId+"' and a.templateid!='"+templateId+"' and a.status=100 and a.status='100' and b.status=100 and (a.restrictOrg='0' or (a.restrictOrg='1' and c.status='100')) ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);

        if (request.getRestrictOrg().equals("1")) {
            if(CollUtil.isNotEmpty(list)) {
                if(CollUtil.isNotEmpty(details)){
                    for (DCP_ProdTemplateUpdateReq.Detail detail : details){
                        String pluNo = detail.getPluNo();
                        List<Map<String, Object>> filter1Rows = list.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)&&"1".equals(x.get("RESTRICTORG").toString())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(filter1Rows)){

                            //判断是否存在全部组织的
                            //List<Map<String, Object>> restrictOrgFilter = filter1Rows.stream().filter(x -> x.get("RESTRICTORG").toString().equals("0")).collect(Collectors.toList());
                            //if(restrictOrgFilter.size()>0){
                             //   throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品"+pluNo+"已存在全部组织模板！");
                            //}

                            for (DCP_ProdTemplateUpdateReq.OrgList org:orgList){
                                String organizationNo = org.getOrganizationNo();
                                List<Map<String, Object>> filter2Rows = filter1Rows.stream().filter(x -> x.get("ORGANIZATIONNO").toString().equals(organizationNo)).collect(Collectors.toList());
                                if(CollUtil.isNotEmpty(filter2Rows)){
                                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品"+pluNo+"对应"+organizationNo+"组织已存在模板！");

                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            //当前是全部组织   只要存在一个就报错
            if (CollUtil.isNotEmpty(list)) {
                if (CollUtil.isNotEmpty(details)) {
                    for (DCP_ProdTemplateUpdateReq.Detail detail : details) {
                        String pluNo = detail.getPluNo();
                        List<Map<String, Object>> filter1Rows = list.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)&&"0".equals(x.get("RESTRICTORG").toString())).collect(Collectors.toList());
                        if(filter1Rows.size()>0){
                            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品"+pluNo+"已存在模板！");
                        }
                    }
                }
            }
        }

        DelBean db2 = new DelBean("DCP_PRODTEMPLATE_GOODS");
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_PRODTEMPLATE_RANGE");
        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));

        UptBean ub1 = new UptBean("DCP_PRODTEMPLATE");
        ub1.addUpdateValue("TEMPLATENAME", new DataValue(request.getTemplateName(), Types.VARCHAR));
        ub1.addUpdateValue("RESTRICTORG", new DataValue(request.getRestrictOrg(), Types.VARCHAR));
        ub1.addUpdateValue("MEMO", new DataValue(request.getMemo(), Types.VARCHAR));
        ub1.addUpdateValue("STATUS", new DataValue(request.getStatus(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        if(CollUtil.isNotEmpty(details)){
            for (DCP_ProdTemplateUpdateReq.Detail detail : details){
                ColumnDataValue detailColumns=new ColumnDataValue();
                detailColumns.add("EID", DataValues.newString(eId));
                detailColumns.add("TEMPLATEID", DataValues.newString(templateId));
                detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
                detailColumns.add("PRODUNIT", DataValues.newString(detail.getProdUnit()));
                detailColumns.add("PRODMINQTY", DataValues.newDecimal(detail.getProdMinQty()));
                detailColumns.add("PRODMULQTY", DataValues.newDecimal(detail.getProdMulQty()));
                detailColumns.add("REMAINTYPE", DataValues.newString(detail.getRemainType()));
                detailColumns.add("DISPTYPE", DataValues.newString(detail.getDispType()));
                detailColumns.add("PROCRATE", DataValues.newDecimal(detail.getProcRate()));
                detailColumns.add("SEMIWOTYPE", DataValues.newString(detail.getSemiWoType()));
                detailColumns.add("SEMIWODEPTTYPE", DataValues.newString(detail.getSemiWoDeptType()));
                detailColumns.add("SDLABORTIME", DataValues.newDecimal(detail.getSdLaborTime()));
                detailColumns.add("SDMACHINETIME", DataValues.newDecimal(detail.getSdMachineTime()));
                detailColumns.add("FIXPREDAYS", DataValues.newDecimal(detail.getFixPreDays()));
                detailColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                detailColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
                detailColumns.add("CREATETIME", DataValues.newDate(createTime));
                detailColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                detailColumns.add("LASTMODITIME", DataValues.newDate(createTime));
                detailColumns.add("STATUS",DataValues.newString(detail.getStatus()));

                detailColumns.add("ODDVALUE",DataValues.newString(detail.getOddValue()));
                detailColumns.add("PRODUCTEXCEED",DataValues.newString(detail.getProductExceed()));
                detailColumns.add("STANDARDHOURS",DataValues.newString(detail.getStandardHours()));

                String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                InsBean detailib=new InsBean("DCP_PRODTEMPLATE_GOODS",detailColumnNames);
                detailib.addValues(detailDataValues);
                this.addProcessData(new DataProcessBean(detailib));
            }
        }

        if(CollUtil.isNotEmpty(orgList)){
            for (DCP_ProdTemplateUpdateReq.OrgList org : orgList){
                ColumnDataValue orgColumns=new ColumnDataValue();
                orgColumns.add("EID", DataValues.newString(eId));
                orgColumns.add("TEMPLATEID", DataValues.newString(templateId));
                orgColumns.add("ORGANIZATIONNO", DataValues.newString(org.getOrganizationNo()));
                orgColumns.add("STATUS", DataValues.newString(org.getStatus()));
                orgColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                orgColumns.add("CREATEDEPTID", DataValues.newString(departmentNo));
                orgColumns.add("CREATETIME", DataValues.newDate(createTime));
                orgColumns.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
                orgColumns.add("LASTMODITIME", DataValues.newDate(createTime));
                String[] orgColumnNames = orgColumns.getColumns().toArray(new String[0]);
                DataValue[] orgDataValues = orgColumns.getDataValues().toArray(new DataValue[0]);
                InsBean orgib=new InsBean("DCP_PRODTEMPLATE_RANGE",orgColumnNames);
                orgib.addValues(orgDataValues);
                this.addProcessData(new DataProcessBean(orgib));
            }
        }


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdTemplateUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdTemplateUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdTemplateUpdateReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdTemplateUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_ProdTemplateUpdateReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_ProdTemplateUpdateReq>(){};
    }

    @Override
    protected DCP_ProdTemplateUpdateRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_ProdTemplateUpdateRes();
    }

}


