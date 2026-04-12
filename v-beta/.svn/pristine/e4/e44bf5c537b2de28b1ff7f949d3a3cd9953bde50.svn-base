package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DistriOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurStockOutCreateReq;
import com.dsc.spos.json.cust.res.DCP_DistriOrderCreateRes;
import com.dsc.spos.json.cust.res.DCP_PurStockOutCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_DistriOrderCreate extends SPosAdvanceService<DCP_DistriOrderCreateReq, DCP_DistriOrderCreateRes> {
    @Override
    protected void processDUID(DCP_DistriOrderCreateReq req, DCP_DistriOrderCreateRes res) throws Exception {

        String orderNo = this.getOrderNO(req, "PHSQ");
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String employeeNo = req.getEmployeeNo();
        String departmentNo = req.getDepartmentNo();
        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        DCP_DistriOrderCreateReq.LevelElm request = req.getRequest();

        List<DCP_DistriOrderCreateReq.OrderList> orderList = request.getOrderList();
        if(orderList==null||orderList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "无商品明细，请检查!");
        }

        //1-供货方式/供货对象是否为空：根据【需求组织编号+商品编号】关联商品模板取供货方式、供货对象；若取不到按【需求组织所属公司编号+商品编号】关联查询公司级商品模板取供货方式、供货对象；若为空则返回失败报错提示“商品xx在需求组织xxx无有效的供货方式和供货对象！”；
        //2-若取出供货方式=“采购”，根据【需求组织编号+商品+供货对象ID】关联采购模板查询是否存在有效的采购模板；若不存在返回失败提示“商品xx在需求组织xxx下未找到供货对象xxxx有效的采购模板！”

        StringBuilder templateSb=new StringBuilder();
        templateSb.append("select a.TEMPLATEID,a.TEMPLATETYPE,b.pluno,b.SUPPLIERTYPE,b.SUPPLIERID,c.RANGETYPE,c.ID" +
                " from DCP_GOODSTEMPLATE a" +
                " left join DCP_GOODSTEMPLATE_GOODS b on a.eid=b.eid and a.TEMPLATEID=b.TEMPLATEID " +
                " left join DCP_GOODSTEMPLATE_RANGE c on a.eid=c.eid and a.TEMPLATEID=c.TEMPLATEID " +
                " where a.eid='"+eId+"' and a.status='100'  ");
        List<Map<String, Object>> getTemplateData=this.doQueryData(templateSb.toString(), null);

        StringBuilder purTemplateSb=new StringBuilder();
        purTemplateSb.append("select b.pluno,c.ORGANIZATIONNO from DCP_PURCHASETEMPLATE a " +
                "left join DCP_PURCHASETEMPLATE_GOODS b on a.eid=b.eid and a.PURTEMPLATENO=b.PURTEMPLATENO " +
                "left join DCP_PURCHASETEMPLATE_ORG c on a.eid=c.eid and a.PURTEMPLATENO=c.PURTEMPLATENO " +
                " where a.eid='"+eId+"' " +//and c.ORGANIZATIONNO='"+request.getDemandOrgNo()+"'
                " and a.status='100' and b.status='100' and c.status='100' ");
        List<Map<String, Object>> getPurTemplateData=this.doQueryData(purTemplateSb.toString(), null);

        String belCompanyNo="";
        StringBuilder orSb=new StringBuilder("select * from dcp_org where eid='"+eId+"' and organizationno='"+request.getDemandOrgNo()+"'");
        List<Map<String, Object>> orData=this.doQueryData(orSb.toString(), null);
        if(orData!=null&&orData.size()>0){
            belCompanyNo=orData.get(0).get("BELFIRM").toString();
        }

        int item=0;

        //没用了
        //BigDecimal totTqty=new BigDecimal(0);
        //BigDecimal totCqty=new BigDecimal(0);
        //BigDecimal totPqty=new BigDecimal(0);
        //BigDecimal totAmt=new BigDecimal(0);
        //BigDecimal totDisTriAmt=new BigDecimal(0);

        //List<String> plunos = orderList.stream().map(x -> x.getPluNo()).distinct().collect(Collectors.toList());
        //totCqty=new BigDecimal(plunos.size());

        for (DCP_DistriOrderCreateReq.OrderList detail : orderList){
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
                } else if (templatef2.size() > 0) {
                    supplierNo = templatef2.get(0).get("SUPPLIERID").toString();
                    supplierType = templatef2.get(0).get("SUPPLIERTYPE").toString();
                }
            }

            if("SUPPLIER".equals(supplierType)){
//，根据【需求组织编号+商品+供货对象ID】关联采购模板查询是否存在有效的采购模板；若不存在返回失败提示“商品xx在需求组织xxx下未找到供货对象xxxx有效的采购模板！”
                String finalSingleDemandOrg1 = singleDemandOrg;
                List<Map<String, Object>> pluTemplates = getPurTemplateData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())&&x.get("ORGANIZATIONNO").toString().equals(finalSingleDemandOrg1)).collect(Collectors.toList());
                if (pluTemplates.size()<=0){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"商品"+detail.getPluNo()+"在需求组织"+singleDemandOrg+"无有效的采购模板！");
                }
            }


            //List<Map<String, Object>> pluno = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(detail.getPluNo())).distinct().collect(Collectors.toList());
            //if(pluno.size()<=0){
            //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"品号"+ detail.getPluNo()+"不存在!");
            //}


            //totPqty = totPqty.add(new BigDecimal(detail.getPQty()));
           // totPurAmt=totPurAmt.add(new BigDecimal(detail.getPurAmt()));
           // totTaxAmt=totTaxAmt.add(new BigDecimal(detail.getTaxAmt()));
           // totPreTaxAmt=totPreTaxAmt.add(new BigDecimal(detail.getPreTaxAmt()));

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

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        mainColumns.add("BILLNO", DataValues.newString(orderNo));
        mainColumns.add("BDATE",DataValues.newString(request.getBDate()));
        mainColumns.add("RDATE",DataValues.newString(request.getRDate()));
        mainColumns.add("DEMANDORGNO",DataValues.newString(request.getDemandOrgNo()));
        mainColumns.add("EMPLOYEEID",DataValues.newString(employeeNo));
        mainColumns.add("DEPARTID",DataValues.newString(departmentNo));
        mainColumns.add("TOTOQTY",DataValues.newDecimal(request.getTotOqty()));
        mainColumns.add("TOTCQTY",DataValues.newDecimal(request.getTotCqty()));
        mainColumns.add("TOTPQTY",DataValues.newDecimal(request.getTotPqty()));
        mainColumns.add("TOTAMT",DataValues.newDecimal(request.getTotAmt()));
        mainColumns.add("TOTDISTRIAMT",DataValues.newDecimal(request.getTotDistriAmt()));
        mainColumns.add("STATUS",DataValues.newString("0"));
        mainColumns.add("MEMO",DataValues.newString(request.getMemo()));
        mainColumns.add("OWNOPID",DataValues.newString(req.getOpNO()));
        mainColumns.add("OWNDEPTID",DataValues.newString(departmentNo));
        mainColumns.add("CREATEOPID",DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATEDEPTID",DataValues.newString(departmentNo));
        mainColumns.add("CREATETIME",DataValues.newDate(lastmoditime));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib1=new InsBean("DCP_DITRIORDER",mainColumnNames);
        ib1.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib1));

        res.setBillNo(orderNo);

        this.doExecuteDataToDB();

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DistriOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DistriOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DistriOrderCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DistriOrderCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_DistriOrderCreateReq> getRequestType() {
        return new TypeToken<DCP_DistriOrderCreateReq>() {

        };
    }

    @Override
    protected DCP_DistriOrderCreateRes getResponseType() {
        return new DCP_DistriOrderCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_DistriOrderCreateReq req) throws Exception {
        return null;
    }
}
