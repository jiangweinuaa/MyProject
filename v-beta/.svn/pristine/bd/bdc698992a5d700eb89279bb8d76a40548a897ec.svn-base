package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DistriOrderUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DistriOrderUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DistriOrderUpdate extends SPosAdvanceService<DCP_DistriOrderUpdateReq, DCP_DistriOrderUpdateRes> {
    @Override
    protected void processDUID(DCP_DistriOrderUpdateReq req, DCP_DistriOrderUpdateRes res) throws Exception {

        String orderNo = req.getRequest().getBillNo();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        DCP_DistriOrderUpdateReq.LevelElm request = req.getRequest();

        StringBuilder templateSb=new StringBuilder();
        templateSb.append("select a.TEMPLATEID,a.TEMPLATETYPE,b.pluno,b.SUPPLIERTYPE,b.SUPPLIERID,c.RANGETYPE,c.ID" +
                " from DCP_GOODSTEMPLATE a" +
                " left join DCP_GOODSTEMPLATE_GOODS b on a.eid=b.eid and a.TEMPLATEID=b.TEMPLATEID " +
                " left join DCP_GOODSTEMPLATE_RANGE c on a.eid=c.eid and a.TEMPLATEID=c.TEMPLATEID " +
                " where a.eid='"+eId+"' and a.status='100'  ");
        List<Map<String, Object>> getTemplateData=this.doQueryData(templateSb.toString(), null);

        StringBuilder purTemplateSb=new StringBuilder();
        purTemplateSb.append("select b.pluno from DCP_PURCHASETEMPLATE a " +
                "left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                "left join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and a.PURTEMPLATENO=c.PURTEMPLATENO " +
                " where a.eid='"+eId+"' and c.ORGANIZATIONNO='"+request.getDemandOrgNo()+"'" +
                " and a.status='100' and b.status='100' and c.status='100' ");
        List<Map<String, Object>> getPurTemplateData=this.doQueryData(purTemplateSb.toString(), null);

        String belCompanyNo="";
        StringBuilder orSb=new StringBuilder("select * from dcp_org where eid='"+eId+"' and organizationno='"+request.getDemandOrgNo()+"'");
        List<Map<String, Object>> orData=this.doQueryData(orSb.toString(), null);
        if(orData!=null&&orData.size()>0){
            belCompanyNo=orData.get(0).get("BELFIRM").toString();
        }

        List<DCP_DistriOrderUpdateReq.OrderList> orderList = request.getOrderList();
        if(orderList==null||orderList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无商品明细，请检查!");
        }
        String billNo = request.getBillNo();
        DelBean db1 = new DelBean("DCP_DITRIORDER_DETAIL");
        db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));


        int item=0;

        //BigDecimal totTqty=new BigDecimal(0);
        //BigDecimal totCqty=new BigDecimal(0);
        //BigDecimal totPqty=new BigDecimal(0);
        //BigDecimal totAmt=new BigDecimal(0);
        //BigDecimal totDisTriAmt=new BigDecimal(0);

        //List<String> plunos = orderList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());
        //totCqty=new BigDecimal(plunos.size());



        for (DCP_DistriOrderUpdateReq.OrderList detail : orderList){
            item++;
            String supplierNo=detail.getSupplierId();//商品模板
            String supplierType=detail.getSupplierType();
            String singleDemandOrg = "";
            if(Check.Null(supplierNo)||" ".equals(supplierNo)) {
                if (Check.Null(detail.getDemandOrgNo())) {
                    if (!Check.Null(request.getDemandOrgNo())) {
                        singleDemandOrg = request.getDemandOrgNo();
                    }
                } else {
                    singleDemandOrg = detail.getDemandOrgNo();
                }

                List<Map<String, Object>> templatef1 = getTemplateData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                if (templatef1.size() <= 0) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品" + detail.getPluNo() + "未找到有效的商品模板！");
                }
                List<Map<String, Object>> templatef2 = templatef1;
                if (!Check.Null(singleDemandOrg)) {
                    String finalSingleDemandOrg = singleDemandOrg;
                    templatef2 = templatef1.stream().filter(x -> x.get("ID").toString().equals(finalSingleDemandOrg)).collect(Collectors.toList());
                }
                if (templatef2.size() <= 0 && !Check.Null(belCompanyNo)) {
                    //过滤公司的
                    String finalBelCompanyNo = belCompanyNo;
                    List<Map<String, Object>> templatef3 = templatef1.stream()
                            .filter(x -> x.get("ID").toString().equals(finalBelCompanyNo.toString())).collect(Collectors.toList());
                    if (templatef3.size() <= 0) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品" + detail.getPluNo() + "在需求组织" + singleDemandOrg + "无有效的供货方式和供货对象！");
                    } else {
                        supplierNo = templatef3.get(0).get("SUPPLIERID").toString();
                        supplierType = templatef3.get(0).get("SUPPLIERTYPE").toString();
                    }
                } else {
                    if (templatef2.size() > 0) {
                        supplierNo = templatef2.get(0).get("SUPPLIERID").toString();
                        supplierType = templatef2.get(0).get("SUPPLIERTYPE").toString();
                    }

                }
            }

            if("SUPPLIER".equals(supplierType)){
//，根据【需求组织编号+商品+供货对象ID】关联采购模板查询是否存在有效的采购模板；若不存在返回失败提示“商品xx在需求组织xxx下未找到供货对象xxxx有效的采购模板！”
                List<Map<String, Object>> pluTemplates = getPurTemplateData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                if (pluTemplates.size()<=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"商品"+detail.getPluNo()+"在需求组织"+singleDemandOrg+"无有效的采购模板！");
                }
            }

            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("BILLNO", DataValues.newString(orderNo));
            detailColumns.add("ITEM", DataValues.newString(item));
            detailColumns.add("DEMANDORGNO", DataValues.newString(detail.getDemandOrgNo()));
            detailColumns.add("RDATE", DataValues.newString(detail.getRDate()));
            detailColumns.add("PLUBARCODE", DataValues.newString(detail.getPluBarcode()));
            detailColumns.add("PLUNO", DataValues.newString(detail.getPluNo()));
            detailColumns.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
            detailColumns.add("PUNIT", DataValues.newString(detail.getPUnit()));
            detailColumns.add("PQTY", DataValues.newDecimal(detail.getPQty()));
            detailColumns.add("PRICE", DataValues.newDecimal(detail.getPrice()));
            detailColumns.add("AMT", DataValues.newDecimal(detail.getAmt()));
            detailColumns.add("DISTRIPRICE", DataValues.newDecimal(detail.getDistriPrice()));
            detailColumns.add("FDISTRIAMOUNT", DataValues.newDecimal(detail.getDistriAmt()));
            detailColumns.add("SUPPLIERID", DataValues.newString(supplierNo));
            detailColumns.add("SUPPLIERTYPE", DataValues.newString(supplierType));
            detailColumns.add("BASEUNIT", DataValues.newString(detail.getBaseUnit()));
            detailColumns.add("BASEQTY", DataValues.newDecimal(detail.getBaseQty()));
            detailColumns.add("UNITRATIO", DataValues.newDecimal(detail.getUnitRatio()));
            //detailColumns.add("WUNIT", DataValues.newString(wunit));
            //detailColumns.add("WQTY", DataValues.newDecimal(wqty));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean ib1=new InsBean("DCP_DITRIORDER_DETAIL",detailColumnNames);
            ib1.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(ib1));
        }

        UptBean ub1 = new UptBean("DCP_DITRIORDER");
        //add Value
        ub1.addUpdateValue("TOTOQTY", DataValues.newDecimal(request.getTotOqty()));
        ub1.addUpdateValue("TOTCQTY", DataValues.newDecimal(request.getTotCqty()));
        ub1.addUpdateValue("TOTPQTY", DataValues.newDecimal(request.getTotPqty()));
        ub1.addUpdateValue("TOTAMT", DataValues.newDecimal(request.getTotAmt()));
        ub1.addUpdateValue("TOTDISTRIAMT", DataValues.newDecimal(request.getTotDistriAmt()));
        ub1.addUpdateValue("BDATE", DataValues.newString(request.getBDate()));
        ub1.addUpdateValue("RDATE", DataValues.newString(request.getRDate()));
        ub1.addUpdateValue("DEMANDORGNO", DataValues.newString(request.getDemandOrgNo()));
        ub1.addUpdateValue("EMPLOYEEID", DataValues.newString(request.getEmployeeId()));
        ub1.addUpdateValue("DEPARTID", DataValues.newString(request.getDepartId()));
        ub1.addUpdateValue("MEMO", DataValues.newString(request.getMemo()));
        ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));


        //condition
        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
        ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DistriOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DistriOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DistriOrderUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DistriOrderUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DistriOrderUpdateReq> getRequestType() {
        return new TypeToken<DCP_DistriOrderUpdateReq>() {

        };
    }

    @Override
    protected DCP_DistriOrderUpdateRes getResponseType() {
        return new DCP_DistriOrderUpdateRes();
    }

    @Override
    protected String getQuerySql(DCP_DistriOrderUpdateReq req) throws Exception {
        return null;
    }
}
