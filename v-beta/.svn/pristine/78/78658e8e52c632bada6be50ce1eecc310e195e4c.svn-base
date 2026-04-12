package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurStockOutDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurStockOutDetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurStockOutDetailQuery extends SPosBasicService<DCP_PurStockOutDetailQueryReq, DCP_PurStockOutDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurStockOutDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(Check.Null(req.getRequest().getPStockOutNo())){
            errMsg.append("退货出库单号不能为空！");
            isFail = true;
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurStockOutDetailQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurStockOutDetailQueryReq>(){};
    }

    @Override
    protected DCP_PurStockOutDetailQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurStockOutDetailQueryRes();
    }

    @Override
    protected DCP_PurStockOutDetailQueryRes processJson(DCP_PurStockOutDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurStockOutDetailQueryRes res = this.getResponse();

        String DomainName= PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";

        String isBatchPara = PosPub.getPARA_SMS(dao, req.geteId(), "", "Is_BatchNO");
        if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
            isBatchPara="N";
        }
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_PurStockOutDetailQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            DCP_PurStockOutDetailQueryRes.level1Elm level1Elm = res.new level1Elm();
            Map<String, Object> oneData = getQData.get(0);
            level1Elm.setStatus(oneData.get("STATUS").toString());
            level1Elm.setPStockOutNo(oneData.get("PSTOCKINNO").toString());
            level1Elm.setBillType(oneData.get("BILLTYPE").toString());
            level1Elm.setOrgNo(oneData.get("ORGNO").toString());
            level1Elm.setOrgName(oneData.get("ORGNAME").toString());
            level1Elm.setBDate(oneData.get("BDATE").toString());
            level1Elm.setAccountDate(oneData.get("ACCOUNTDATE").toString());
            level1Elm.setSupplierNo(oneData.get("SUPPLIERNO").toString());
            level1Elm.setSupplierName(oneData.get("SUPPLIERNAME").toString());
            level1Elm.setPayType(oneData.get("PAYTYPE").toString());
            level1Elm.setPayOrgNo(oneData.get("PAYORGNO").toString());
            level1Elm.setPayOrgName(oneData.get("PAYORGNAME").toString());
            level1Elm.setBillDateNo(oneData.get("BILLDATENO").toString());
            level1Elm.setBillDateDesc(oneData.get("BILLDATEDESC").toString());
            level1Elm.setInvoiceCode(oneData.get("INVOICECODE").toString());
            level1Elm.setInvoiceName(oneData.get("INVOICENAME").toString());
            level1Elm.setCurrency(oneData.get("CURRENCY").toString());
            level1Elm.setCurrencyName(oneData.get("CURRENCYNAME").toString());
            level1Elm.setSourceType(oneData.get("SOURCETYPE").toString());
            level1Elm.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());
            level1Elm.setReceivingNo(oneData.get("RECEIVINGNO").toString());
            level1Elm.setRDate(oneData.get("RDATE").toString());
            level1Elm.setWareHouse(oneData.get("WAREHOUSE").toString());
            level1Elm.setWareHouseName(oneData.get("WAREHOUSENAME").toString());
            level1Elm.setTotCqty(oneData.get("TOTCQTY").toString());
            level1Elm.setTotPqty(oneData.get("TOTPQTY").toString());
            level1Elm.setTotPurAmt(oneData.get("TOTPURAMT").toString());
            level1Elm.setAccountBy(oneData.get("ACCOUNTBY").toString());
            level1Elm.setAccountDateTime(oneData.get("ACCOUNTDATETIME").toString());
            level1Elm.setCancelBy(oneData.get("CANCELBY").toString());
            level1Elm.setCancelDateTime(oneData.get("CANCELDATETIME").toString());
            level1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
            level1Elm.setConfirmBy(oneData.get("CONFIRMBY").toString());
            level1Elm.setConfirmDateTime(oneData.get("CONFIRMDATETIME").toString());
            level1Elm.setCreateBy(oneData.get("CREATEBY").toString());
            level1Elm.setCreateByName(oneData.get("CREATEBYNAME").toString());
            level1Elm.setCreateDateTime(oneData.get("CREATEDATETIME").toString());
            level1Elm.setDepartID(oneData.get("DEPARTID").toString());
            level1Elm.setDepartName(oneData.get("DEPARTNAME").toString());
            level1Elm.setEmployeeID(oneData.get("EMPLOYEEID").toString());
            level1Elm.setEmployeeName(oneData.get("EMPLOYEENAME").toString());
            level1Elm.setIsLocation(oneData.get("ISLOCATION").toString());
            level1Elm.setModifyBy(oneData.get("MODIFYBY").toString());
            level1Elm.setModifyDateTime(oneData.get("MODIFYDATETIME").toString());
            level1Elm.setModifyByName(oneData.get("MODIFYBYNAME").toString());
            level1Elm.setOwnDeptID(oneData.get("OWNDEPTID").toString());
            level1Elm.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
            level1Elm.setOwnOpID(oneData.get("OWNOPID").toString());
            level1Elm.setOwnOpName(oneData.get("OWNOPNAME").toString());
            level1Elm.setMultiLotsList(new ArrayList<>());
            level1Elm.setDataList(new ArrayList<>());

            String detailSql=this.getDetailSql(req);
            String lotsSql = this.getLotsSql(req);
            List<Map<String, Object>> getLotsData=this.doQueryData(lotsSql, null);
            List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);
            if (getDetailData != null && getDetailData.isEmpty() == false)
            {
                for (Map<String, Object> oneDetail : getDetailData)
                {
                    DCP_PurStockOutDetailQueryRes.dataList dataList = res.new dataList();
                    dataList.setItem(oneDetail.get("ITEM").toString());
                    dataList.setRItem(oneDetail.get("RITEM").toString());
                    dataList.setOItem(oneDetail.get("OITEM").toString());
                    dataList.setOItem2(oneDetail.get("OITEM2").toString());
                    //dataList.setListImage(oneDetail.get("LISTIMAGE").toString());
                    dataList.setPluNo(oneDetail.get("PLUNO").toString());
                    dataList.setPluName(oneDetail.get("PLUNAME").toString());
                    dataList.setPluBarcode(oneDetail.get("PLUBARCODE").toString());
                    dataList.setSpec(oneDetail.get("SPEC").toString());
                    dataList.setPoItem2(oneDetail.get("POITEM2").toString());
                    dataList.setPoItem(oneDetail.get("POITEM").toString());
                    dataList.setPurOrderNo(oneDetail.get("PURORDERNO").toString());
                    dataList.setPurPrice(oneDetail.get("PURPRICE").toString());
                    dataList.setPurAmt(oneDetail.get("PURAMT").toString());
                    dataList.setPreTaxAmt(oneDetail.get("PRETAXAMT").toString());
                    dataList.setTaxAmt(oneDetail.get("TAXAMT").toString());
                    dataList.setTaxCode(oneDetail.get("TAXCODE").toString());
                    dataList.setTaxName(oneDetail.get("TAXNAME").toString());
                    dataList.setTaxRate(oneDetail.get("TAXRATE").toString());
                    dataList.setInclTax(oneDetail.get("INCLTAX").toString());
                    dataList.setWUnit(oneDetail.get("WUNIT").toString());
                    dataList.setWUnitName(oneDetail.get("WUNITNAME").toString());
                    dataList.setWQty(oneDetail.get("WQTY").toString());
                    //dataList.setIsBatch(oneDetail.get("ISBATCH").toString());//todo
                    dataList.setIsFree(oneDetail.get("ISFREE").toString());
                    //dataList.setCanStockOutQty(oneDetail.get("CANSTOCKOUTQTY").toString());//todo
                    dataList.setBatchNo(oneDetail.get("BATCHNO").toString());
                    dataList.setProdDate(oneDetail.get("PRODDATE").toString());
                    dataList.setExpDate(oneDetail.get("EXPDATE").toString());
                    dataList.setBaseUnit(oneDetail.get("BASEUNIT").toString());
                    dataList.setBaseUnitName(oneDetail.get("BASEUNITNAME").toString());
                    dataList.setBaseQty(oneDetail.get("BASEQTY").toString());
                    dataList.setFeatureName(oneDetail.get("FEATURENAME").toString());
                    dataList.setFeatureNo(oneDetail.get("FEATURENO").toString());
                    dataList.setPUnit(oneDetail.get("PUNIT").toString());
                    dataList.setPUnitName(oneDetail.get("PUNITNAME").toString());
                    dataList.setPQty(oneDetail.get("PQTY").toString());
                    dataList.setWareHouse(oneDetail.get("WAREHOUSE").toString());
                    dataList.setProdDate(oneDetail.get("PRODDATE").toString());
                    dataList.setWarehouseName(oneDetail.get("WAREHOUSENAME").toString());
                    dataList.setProdDate(oneDetail.get("PRODDATE").toString());
                    dataList.setExpDate(oneDetail.get("EXPDATE").toString());


                    if (DomainName.endsWith("/")) {
                        dataList.setListImage(httpStr+DomainName+"resource/image/" +oneDetail.get("LISTIMAGE")==null?"":oneDetail.get("LISTIMAGE").toString());
                    } else {
                        dataList.setListImage(httpStr+DomainName+"/resource/image/"+oneDetail.get("LISTIMAGE")==null?"":oneDetail.get("LISTIMAGE").toString());
                    }

                    dataList.setMultiLotsList(new ArrayList<>());
                    List<Map<String, Object>> multiItem = getLotsData.stream().filter(x -> x.get("ITEM").toString().equals(dataList.getItem())).collect(Collectors.toList());
                    for (Map<String, Object> oneLot : multiItem)
                    {
                        DCP_PurStockOutDetailQueryRes.multiLotsListDetail multiLotsListDetail = res.new multiLotsListDetail();
                        multiLotsListDetail.setItem(oneLot.get("ITEM").toString());
                        multiLotsListDetail.setItem2(oneLot.get("ITEM2").toString());
                        multiLotsListDetail.setWareHouse(oneLot.get("WAREHOUSE").toString());
                        multiLotsListDetail.setWareHouseName(oneLot.get("WAREHOUSENAME").toString());
                        multiLotsListDetail.setLocation(oneLot.get("LOCATION").toString());
                        multiLotsListDetail.setLocationName(oneLot.get("LOCATIONNAME").toString());
                        multiLotsListDetail.setBatchNo(oneLot.get("BATCHNO").toString());
                        multiLotsListDetail.setProdDate(oneLot.get("PRODDATE").toString());
                        multiLotsListDetail.setExpDate(oneLot.get("EXPDATE").toString());
                        multiLotsListDetail.setPQty(oneLot.get("PQTY").toString());
                        dataList.getMultiLotsList().add(multiLotsListDetail);

                        DCP_PurStockOutDetailQueryRes.multiLotsList multiLotsList = res.new multiLotsList();
                        multiLotsList.setItem(oneLot.get("ITEM").toString());
                        multiLotsList.setItem2(oneLot.get("ITEM2").toString());
                        multiLotsList.setPluNo(oneDetail.get("PLUNO").toString());
                        multiLotsList.setPluName(oneDetail.get("PLUNAME").toString());
                        multiLotsList.setPUnit(oneDetail.get("PUNIT").toString());
                        multiLotsList.setPUnitName(oneDetail.get("PUNITNAME").toString());
                        multiLotsList.setWUnit(oneDetail.get("WUNIT").toString());
                        multiLotsList.setWQty(oneDetail.get("WQTY").toString());
                        multiLotsList.setWUnitName(oneDetail.get("WUNITNAME").toString());
                        multiLotsList.setExpDate(oneLot.get("EXPDATE").toString());
                        multiLotsList.setProdDate(oneLot.get("PRODDATE").toString());
                        multiLotsList.setFeatureNo(oneDetail.get("FEATURENO").toString());
                        multiLotsList.setFeatureName(oneDetail.get("FEATURENAME").toString());
                        multiLotsList.setBatchNo(oneLot.get("BATCHNO").toString());
                        multiLotsList.setLocation(oneLot.get("LOCATION").toString());
                        multiLotsList.setLocationName(oneLot.get("LOCATIONNAME").toString());
                        level1Elm.getMultiLotsList().add(multiLotsList);
                    }


                    level1Elm.getDataList().add(dataList);
                }
            }

            res.getDatas().add(level1Elm);
            level1Elm=null;

        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurStockOutDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String pStockOutNo = req.getRequest().getPStockOutNo();
        sqlbuf.append(" "
                + " select distinct a.status,a.pStockinNo,a.billType,b.ORGANIZATIONNO as orgNo ,b.org_name as orgName,to_char(a.bdate,'yyyy-MM-dd') bdate,to_char(a.ACCOUNT_DATE,'yyyy-MM-dd') as accountDate," +
                "   c.BIZPARTNERNO as supplierNo ,c.sname as supplierName,a.paytype,a.PAYORGNO,d.org_name as payOrgName,e.BILLDATENO,e.name as billDateDesc,f.PAYDATENO,f.name as payDateDesc, g.invoiceCode,g.INVOICE_NAME as invoicename,h.currency,h.name as currencyname," +
                "   a.sourceType,a.sourceBillNo,a.receivingNo,nvl(to_char(i.rdate,'yyyy-MM-dd'),'') rdate,j.warehouse,j.warehouse_name as warehousename ,a.TOT_CQTY as totcqty,a.TOT_PQTY as totpqty,a.TOT_PURAMT as totpuramt,k.islocation,a.employeeID,em0.name as employeename," +
                "   a.departID,dd0.DEPARTNAME as departName,em1.employeeno as createBy,em1.name as createByName ,a.CREATETIME as createDateTime, em2.employeeno as modifyBy,em2.name as modifyByName,a.LASTMODITIME as modifyDateTime, em3.employeeno as confirmBy,em3.name as confirmByName,a.CONFIRMTIME as confirmDateTime," +
                "   em5.employeeno as cancelBy,em5.name as cancelByName,a.CANCELTIME as cancelDateTime,em6.employeeno as ownOpID,em6.name as ownOpName,em4.employeeno as accountBy,em4.name as accountByName,a.ACCOUNTTIME as accountDateTime,dd1.DEPARTNO as ownDeptID,dd1.DEPARTNAME as ownDeptName  "
                + " from DCP_PURSTOCKIN a "
                + " left join dcp_org_lang b on a.eid=b.eid and b.ORGANIZATIONNO=a.PURORGNO and b.lang_type='"+req.getLangType()+"' "
                + " left join DCP_BIZPARTNER c on a.eid=c.eid and a.organizationno=c.organizationno and a.supplier=c.BIZPARTNERNO and (c.biztype='1' or c.biztype='3') "
                + " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.PAYORGNO and d.lang_type='"+req.getLangType()+"' "
                + " left join DCP_BILLDATE_LANG e on e.eid=a.eid and e.BILLDATENO=a.BILLDATENO and e.lang_type='"+req.getLangType()+"' "
                + " left join DCP_PAYDATE_LANG f on f.eid=a.eid and f.PAYDATENO=a.PAYDATENO and f.lang_type='"+req.getLangType()+"' "
                + " left join DCP_INVOICETYPE_LANG g on g.eid=a.eid and g.INVOICECODE=a.INVOICECODE and g.lang_type='"+req.getLangType()+"' "
                + " left join DCP_CURRENCY_LANG h on h.eid =a.eid and h.CURRENCY=a.CURRENCY and h.lang_type='"+req.getLangType()+"' "
                + " left join DCP_STOCKOUTNOTICE i on i.eid=a.eid and a.organizationno=i.organizationno and i.billno=a.receivingno "
                + " left join DCP_WAREHOUSE_LANG j on j.warehouse=i.warehouse and j.eid=a.eid and j.lang_type='"+req.getLangType()+"' "
                + " left join DCP_WAREHOUSE k on k.eid=j.eid and k.warehouse=j.warehouse "
                + " left join DCP_EMPLOYEE em0 on em0.eid=a.eid and em0.employeeno=a.employeeid "
                + " left join DCP_DEPARTMENT_LANG dd0 on dd0.eid=a.eid and dd0.DEPARTNO =a.DEPARTID "
                + " left join DCP_EMPLOYEE em1 on em1.eid=a.eid and em1.employeeno=a.CREATEOPID "
                + " left join DCP_EMPLOYEE em2 on em2.eid=a.eid and em2.employeeno=a.LASTMODIOPID "
                + " left join DCP_EMPLOYEE em3 on em3.eid=a.eid and em3.employeeno=a.CONFIRMBY "
                + " left join DCP_EMPLOYEE em4 on em4.eid=a.eid and em4.employeeno=a.ACCOUNTBY "
                + " left join DCP_EMPLOYEE em5 on em5.eid=a.eid and em5.employeeno=a.CANCELBY "
                + " left join DCP_EMPLOYEE em6 on em6.eid=a.eid and em6.employeeno=a.OWNOPID "
                + " left join DCP_DEPARTMENT_LANG dd1 on dd1.eid=a.eid and dd1.DEPARTNO =a.OWNDEPTID "
                + " where  a.eid= '"+ eId +"' and a.PSTOCKINNO='"+pStockOutNo+"' and a.organizationno='"+organizationNO+"' "
                + " "
                + " ");

        return sqlbuf.toString();
    }

    private String getDetailSql(DCP_PurStockOutDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String langType = req.getLangType();

        sqlbuf.append("select b.item,b.RECEIVINGITEM as ritem,b.OITEM as oitem ,b.OITEM2 as oitem2,image.LISTIMAGE,b.pluno,d.PLU_NAME as pluname,c.spec, e.PLUBARCODE,b.FEATURENO,f.featurename,g.warehouse,g.warehouse_name as warehousename ," +
                "  b.batchno,to_char(b.PROD_DATE,'yyyy-MM-dd') as proddate,to_char(b.EXP_DATE,'yyyy-MM-dd') as expdate,h.unit as punit, h.uname as punitname ,b.pqty,i.unit as baseunit,i.uname as baseunitname,b.baseqty,j.unit as wunit,j.uname as wunitname," +
                " b.wqty,k.taxcode,k.TAXNAME,l.TAXRATE,l.INCLTAX,b.PURPRICE,b.PURAMT,b.PRETAXAMT,b.TAXAMT,b.isfree,b.PURORDERNO,b.POITEM,b.POITEM2,c.ISBATCH " +
                " from DCP_PURSTOCKIN a" +
                " inner join DCP_PURSTOCKIN_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.PSTOCKINNO=b.PSTOCKINNO " +
                " left  join dcp_goods c on b.eid=c.eid and c.pluno=b.pluno " +
                " left join DCP_GOODS_LANG d on d.eid=c.eid and c.pluno=d.pluno and d.lang_type='"+langType+"' " +
                " left join DCP_GOODS_BARCODE e on e.eid =c.eid and e.pluno=c.pluno " +
                " left join DCP_GOODS_FEATURE_lang f on f.eid=b.eid and f.pluno=b.pluno and f.featureno=b.featureno and f.lang_type='"+langType+"' " +
                " left join dcp_warehouse_lang g on g.eid=b.eid and g.ORGANIZATIONNO=b.organizationno and g.warehouse=b.warehouse and g.lang_type='"+langType+"' " +
                " left join DCP_UNIT_LANG h on h.unit=b.punit and h.eid=b.eid and h.lang_type='"+langType+"' " +
                " left join DCP_UNIT_LANG i on i.unit=b.baseunit and i.eid=b.eid and i.lang_type='"+langType+"' " +
                " left join dcp_unit_lang j on j.unit=b.wunit and j.eid=b.eid and j.lang_type='"+langType+"' " +
                " left join DCP_TAXCATEGORY_LANG k on k.TAXCODE=b.taxcode and k.eid=b.eid and k.lang_type='"+langType+"' " +
                " left join dcp_taxcategory l on l.taxcode=b.taxcode and l.eid=b.eid  " +
                " left join dcp_goodsimage image on  image.eid=b.eid and image.pluno=b.pluno "+
                " where a.PSTOCKINNO='"+req.getRequest().getPStockOutNo()+"' and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' ");

        return sqlbuf.toString();
    }

    private String getLotsSql(DCP_PurStockOutDetailQueryReq req) throws Exception{
        StringBuffer sqlbuf=new StringBuffer();
        sqlbuf.append("select b.item,b.item2,b.warehouse,b.location,b.batchno,to_char(b.prod_date,'yyyy-MM-dd') as proddate,to_char(b.exp_date,'yyyy-MM-dd') as expdate, " +
                " c.warehouse_name as warehousename,d.locationname,b.pqty   " +
                " from DCP_PURSTOCKIN a" +
                " left join DCP_PURSTOCKIN_LOTS b on a.PSTOCKINNO=b.PSTOCKINNO " +
                " left join dcp_warehouse_LANG c on a.eid=c.eid and b.warehouse=c.warehouse and c.lang_type='"+req.getLangType()+"' " +
                " left join DCP_LOCATION d on d.location=b.location and b.eid=d.eid and a.organizationno=d.organizationno " +
                " where a.eid='"+req.geteId()+"' and a.PSTOCKINNO='"+req.getRequest().getPStockOutNo()+"' and a.organizationno='"+req.getOrganizationNO()+"' ");

        return sqlbuf.toString();
    }
}

