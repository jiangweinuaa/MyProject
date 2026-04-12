package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PurOrderDetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_PurOrderDetailQueryRes;
import com.dsc.spos.json.cust.res.DCP_SupplierGoodsOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurOrderDetailQuery extends SPosBasicService<DCP_PurOrderDetailQueryReq, DCP_PurOrderDetailQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_PurOrderDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest()==null)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(Check.Null(req.getRequest().getPurOrderNo())){
            errMsg.append("采购单号不能为空！");
            isFail = true;
        }

        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PurOrderDetailQueryReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PurOrderDetailQueryReq>(){};
    }

    @Override
    protected DCP_PurOrderDetailQueryRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PurOrderDetailQueryRes();
    }

    @Override
    protected DCP_PurOrderDetailQueryRes processJson(DCP_PurOrderDetailQueryReq req) throws Exception {
        // TODO 自动生成的方法存根
        DCP_PurOrderDetailQueryRes res = this.getResponse();

        String DomainName= PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");
        String ISHTTPS=PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
        String httpStr=ISHTTPS.equals("1")?"https://":"http://";
        //单头查询
        String sql=this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);
        res.setDatas(new ArrayList<DCP_PurOrderDetailQueryRes.level1Elm>());
        if (getQData != null && getQData.isEmpty() == false)
        {
            for (Map<String, Object> oneData : getQData)
            {
                DCP_PurOrderDetailQueryRes.level1Elm lev1Elm = res.new level1Elm();
                lev1Elm.setDetail(new ArrayList<>());
                lev1Elm.setPayList(new ArrayList<>());

                String receiveStatus="2";
                String detailSql=" "+
                            " select distinct a.*,b.spec,c.plu_name as pluname,f.featurename,e.category_name as categoryname,punit.uname as purunitname,wunit.uname as wunitname,image.LISTIMAGE,m.taxname as taxname,n.MULPQTY,a.arrivaldate arrivaldates,bunit.uname as baseunitname,n.pricetype," +
                        " nvl(punit1.udlength,0) as pudlength,gu.unitratio as punitratio  " +
                            " from DCP_PURORDER_DETAIL a" +
                            " inner join DCP_PURORDER dp on a.eid=dp.eid and a.PURORDERNO=dp.PURORDERNO  and a.organizationno=dp.organizationno " +
                            " left join dcp_goods b on b.eid=a.eid and  b.pluno=a.pluno  " +
                            " left join dcp_goods_lang c on b.eid=c.eid and  b.pluno=c.pluno and c.lang_type='"+req.getLangType()+"' " +
                            " left join DCP_GOODS_FEATURE_LANG f on f.eid=b.eid and f.pluno=b.pluno and f.featureno=a.featureno and f.lang_type='"+req.getLangType()+"'"+
                            " left join DCP_category d on d.eid=b.eid and d.category=b.category "+
                            " LEFT JOIN DCP_CATEGORY_LANG e ON a.EID=e.EID AND d.CATEGORY=e.CATEGORY  AND e.LANG_TYPE='"+req.getLangType()+"' "+
                            " LEFT JOIN DCP_UNIT_LANG  punit ON b.EID=punit.EID AND a.PurUNIT=punit.UNIT AND punit.LANG_TYPE='"+req.getLangType()+"' "+
                            " LEFT JOIN DCP_UNIT  punit1 ON b.EID=punit1.EID AND a.PurUNIT=punit1.UNIT " +
                        " left join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=a.pluno and gu.ounit=a.purunit "+

                            " LEFT JOIN DCP_UNIT_LANG  wunit ON b.EID=wunit.EID AND b.WUNIT=wunit.UNIT AND wunit.LANG_TYPE='"+req.getLangType()+"' "+
                            " LEFT JOIN DCP_UNIT_LANG  bunit ON b.EID=bunit.EID AND a.baseunit=bunit.UNIT AND bunit.LANG_TYPE='"+req.getLangType()+"' "+
                            " left join DCP_TAXCATEGORY l on l.eid=a.eid and a.taxcode=l.taxcode "+
                            " LEFT JOIN DCP_TaxCategory_LANG m on a.EID=m.EID  and a.taxCode=m.taxCode  AND m.LANG_TYPE='"+req.getLangType()+"' "+
                            " left join dcp_goodsimage image on  image.eid=b.eid and image.pluno=b.pluno "+
                            " left join DCP_PURCHASETEMPLATE_GOODS n on n.eid=a.eid and a.PURTEMPLATENO=n.PURTEMPLATENO and a.pluno=n.pluno  " +
                            " where a.eid='"+req.geteId()+"' and a.purorderno='"+req.getRequest().getPurOrderNo()+"' and dp.organizationno='"+req.getOrganizationNO()+"' " +
                            " order by a.item "
                            ;
                List<Map<String, Object>> getDetailData=this.doQueryData(detailSql, null);

                List<String> purTemplatenoList = getDetailData.stream().map(x -> x.get("PURTEMPLATENO").toString()).distinct().collect(Collectors.toList());
                String purTemplateNoStr = purTemplatenoList.stream().map(x -> "'" + x + "'").collect(Collectors.joining(","));

                StringBuffer templateSqlBuffer=new StringBuffer();
                templateSqlBuffer.append(" select distinct a.*,c.pluno,nvl(gu.unitratio,'1') as purunitratio " +
                        " from DCP_PURCHASETEMPLATE_PRICE a " +
                        " inner join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.item=a.item " +
                        " left join dcp_goods_unit gu on gu.eid=c.eid and gu.pluno=c.pluno and gu.ounit=c.purunit " +
                        " where a.eid='"+req.geteId()+"' and a.purtemplateno in ("+purTemplateNoStr+") ");
                List<Map<String, Object>> getTemplatePrice = this.doQueryData(templateSqlBuffer.toString(), null);


                //多交期明细
                String deliverySql="select a.*,nvl(a.RECEIVEQTY,0) as RECEIVEQTY_N,nvl(a.STOCKINQTY,0) as STOCKINQTY_N from DCP_PURORDER_DELIVERY a " +
                            "where eid='"+req.geteId()+"' and PURORDERNO='"+req.getRequest().getPurOrderNo()+"' and a.organizationno='"+req.getOrganizationNO()+"'";
                List<Map<String, Object>> getDeliveryData=this.doQueryData(deliverySql, null);

                //收货状态，收货量，入库量，到货率
                BigDecimal sh=new BigDecimal(0);//收货
                BigDecimal rk=new BigDecimal(0);//入库
                BigDecimal cg=new BigDecimal(0);//采购

                BigDecimal tot_purAmt=new BigDecimal(0);
                BigDecimal tot_preTaxAmt=new BigDecimal(0);
                BigDecimal tot_TaxAmt=new BigDecimal(0);
                //品项数合计

                List<Map<String,String>> pluNos = getDetailData.stream().map(x -> {
                    Map<String,String> map = new HashMap();
                    map.put("pluNo", x.get("PLUNO").toString());
                    map.put("featureNo",x.get("FEATURENO").toString());
                    return map;
                }).distinct().collect(Collectors.toList());

                String paySql=String.format("select * from DCP_PURORDER_PAY where eid='%s' and purorderno='%s'  and organizationno='%s'",
                        req.geteId(),req.getRequest().getPurOrderNo(),req.getOrganizationNO());
                List<Map<String, Object>> payData=this.doQueryData(paySql, null);


                List statusList=new ArrayList();//待收货、部分收货、收货结束
                for (Map<String, Object> oneDetail : getDetailData){
                    DCP_PurOrderDetailQueryRes.Detail detail = res.new Detail();
                    detail.setMultiDateDetail(new ArrayList<>());
                    BigDecimal purQtyDetail=new BigDecimal(0);//采购数量
                    BigDecimal purAmtDetail=new BigDecimal(0);//采购含税金额
                    BigDecimal preTaxAmtDetail=new BigDecimal(0);//采购税前金额
                    BigDecimal taxAmtDetail=new BigDecimal(0);//采购税额

                    List<Map<String, Object>> deliveryDetail = getDeliveryData.stream().
                            filter(x -> x.get("PLUNO").toString().equals(oneDetail.get("PLUNO").toString())
                                    && x.get("ITEM2").toString().equals(oneDetail.get("ITEM").toString())).collect(Collectors.toList());
                    for(Map<String, Object> oneDeliveryDetail : deliveryDetail){

                        String deliveryPurQty = oneDeliveryDetail.get("PURQTY").toString();
                        String deliveryPurAmt = oneDeliveryDetail.get("PURAMT").toString();
                        String deliveryPreTaxAmt = oneDeliveryDetail.get("PRETAXAMT").toString();
                        String deliveryTaxAmt = oneDeliveryDetail.get("TAXAMT").toString();
                        taxAmtDetail=taxAmtDetail.add(new BigDecimal(deliveryTaxAmt));
                        preTaxAmtDetail = preTaxAmtDetail.add(new BigDecimal(deliveryPreTaxAmt));
                        purAmtDetail = purAmtDetail.add(new BigDecimal(deliveryPurAmt));
                        purQtyDetail = purQtyDetail.add(new BigDecimal(deliveryPurQty));

                        sh=sh.add(new BigDecimal(oneDeliveryDetail.get("RECEIVEQTY_N").toString()));
                        rk=rk.add(new BigDecimal(oneDeliveryDetail.get("STOCKINQTY_N").toString()));
                        cg=cg.add(new BigDecimal(oneDeliveryDetail.get("PURQTY").toString()));

                        DCP_PurOrderDetailQueryRes.MultiDetail multiDetail = res.new MultiDetail();
                        multiDetail.setItem2(oneDeliveryDetail.get("ITEM2").toString());
                        multiDetail.setArrivalDate(oneDeliveryDetail.get("ARRIVALDATE").toString());
                        multiDetail.setPurQty(deliveryPurQty);
                        detail.getMultiDateDetail().add(multiDetail);
                    }

                    if(rk.compareTo(cg)>=0){
                        if (!statusList.contains(3)) {
                            statusList.add(3);
                        }
                    }else {

                        if (sh.compareTo(new BigDecimal(0)) == 0) {
                            if (!statusList.contains(1)) {
                                statusList.add(1);
                            }
                        } else {
                            int i = sh.compareTo(cg);
                            if (i < 0) {
                                if (!statusList.contains(2)) {
                                    statusList.add(2);
                                }
                            } else {
                                if (!statusList.contains(3)) {
                                    statusList.add(3);
                                }
                            }
                        }
                    }
                    //明细赋值
                    detail.setItem(oneDetail.get("ITEM").toString());
                    detail.setPluNo(oneDetail.get("PLUNO").toString());
                    detail.setPluName(oneDetail.get("PLUNAME").toString());
                    detail.setSpec(oneDetail.get("SPEC").toString());
                    detail.setFeatureNo(oneDetail.get("FEATURENO").toString());
                    detail.setFeatureName(oneDetail.get("FEATURENAME").toString());
                    detail.setPluBarCode(oneDetail.get("PLUBARCODE").toString());
                    detail.setCategory(oneDetail.get("CATEGORY").toString());
                    detail.setCategoryName(oneDetail.get("CATEGORYNAME").toString());
                    detail.setPurUnit(oneDetail.get("PURUNIT").toString());
                    detail.setPurUnitName(oneDetail.get("PURUNITNAME").toString());
                    detail.setPurQty(purQtyDetail.toString());
                    detail.setPurPrice(oneDetail.get("PURPRICE").toString());
                    detail.setPurAmt(purAmtDetail.toString());
                    detail.setPreTaxAmt(preTaxAmtDetail.toString());
                    detail.setTaxAmt(taxAmtDetail.toString());
                    detail.setTaxCode(oneDetail.get("TAXCODE").toString());
                    detail.setTaxName(oneDetail.get("TAXNAME").toString());
                    detail.setTaxRate(oneDetail.get("TAXRATE").toString());
                    detail.setInclTax(oneDetail.get("INCLTAX").toString());
                    detail.setArrivalDate(oneDetail.get("ARRIVALDATES").toString());
                    detail.setMultiDate(oneDetail.get("MULTIDATE").toString());
                    detail.setItem(oneDetail.get("ITEM").toString());
                    detail.setIs_qualityCheck(oneDetail.get("IS_QUALITYCHECK").toString());
                    detail.setStockQty(oneDetail.get("STOCKQTY").toString());
                    detail.setNonArrivalQty(oneDetail.get("NONARRIVALQTY").toString());
                    detail.setCloseStatus(oneDetail.get("CLOSE_STATUS").toString());
                    detail.setWunit(oneDetail.get("WUNIT").toString());
                    detail.setWunitName(oneDetail.get("WUNITNAME").toString());
                    detail.setMulPurQty(oneDetail.get("MULPQTY").toString());
                    detail.setTaxCalType(oneDetail.get("TAXCALTYPE").toString());
                    detail.setPurTemplateNo(oneDetail.get("PURTEMPLATENO").toString());
                    detail.setBaseUnit(oneDetail.get("BASEUNIT").toString());
                    detail.setBaseUnitName(oneDetail.get("BASEUNITNAME").toString());
                    detail.setUnitRatio(oneDetail.get("UNITRATIO").toString());
                    detail.setBaseQty(oneDetail.get("BASEQTY").toString());
                    detail.setIsGift(oneDetail.get("ISGIFT").toString());
                    if (DomainName.endsWith("/")) {
                        detail.setImage(httpStr+DomainName+"resource/image/" +oneDetail.get("LISTIMAGE")==null?"":oneDetail.get("LISTIMAGE").toString());
                    } else {
                        detail.setImage(httpStr+DomainName+"/resource/image/"+oneDetail.get("LISTIMAGE")==null?"":oneDetail.get("LISTIMAGE").toString());
                    }
                    detail.setReceivePrice(oneDetail.get("RECEIVEPRICE").toString());
                    detail.setReceiveAmt(oneDetail.get("RECEIVEAMT").toString());
                    detail.setSupPrice(oneDetail.get("SUPPRICE").toString());
                    detail.setSupAmt(oneDetail.get("SUPAMT").toString());

                    tot_purAmt=tot_purAmt.add(purAmtDetail);
                    tot_preTaxAmt=tot_preTaxAmt.add(preTaxAmtDetail);
                    tot_TaxAmt=tot_TaxAmt.add(taxAmtDetail);

                    String pUdlength = oneDetail.get("PUDLENGTH").toString();
                    String pUnitRatio = oneDetail.get("PUNITRATIO").toString();

                    detail.setPriceType(oneDetail.get("PRICETYPE").toString());
                    detail.setPurPriceList(new ArrayList<>());
                    if("2".equals(detail.getPriceType())){//分量计价
                        List<Map<String, Object>> purTemplatePriceList = getTemplatePrice.stream().filter(x -> x.get("PURTEMPLATENO").toString().equals(detail.getPurTemplateNo())&&x.get("PLUNO").toString().equals(detail.getPluNo())).collect(Collectors.toList());
                        for (Map<String, Object> purTemplatePrice : purTemplatePriceList) {

                            BigDecimal bQty = new BigDecimal(purTemplatePrice.get("BQTY").toString());
                            BigDecimal eQty = new BigDecimal(purTemplatePrice.get("EQTY").toString());
                            BigDecimal singlePurPrice = new BigDecimal(purTemplatePrice.get("PURPRICE").toString());

                            BigDecimal purUnitRatio = new BigDecimal(purTemplatePrice.get("PURUNITRATIO").toString());

                            BigDecimal singlePurPriceNew = singlePurPrice.multiply(new BigDecimal(pUnitRatio)).divide(purUnitRatio,Integer.parseInt(pUdlength), BigDecimal.ROUND_HALF_UP);
                            BigDecimal bQtyNew= bQty.multiply(purUnitRatio).divide( new BigDecimal(pUnitRatio),Integer.parseInt(pUdlength), BigDecimal.ROUND_HALF_UP);
                            BigDecimal eQtyNew= eQty.multiply(purUnitRatio).divide(new BigDecimal(pUnitRatio),Integer.parseInt(pUdlength), BigDecimal.ROUND_HALF_UP);


                            DCP_PurOrderDetailQueryRes.PurPriceList purPriceList =res.new PurPriceList();
                            purPriceList.setBeginQty(bQtyNew.toString());
                            purPriceList.setEndQty(eQtyNew.toString());
                            purPriceList.setPurPrice(singlePurPriceNew.toString());
                            detail.getPurPriceList().add(purPriceList);
                        }

                    }


                    lev1Elm.getDetail().add(detail);
                }

                BigDecimal dhl =new BigDecimal("0");
                if(cg.compareTo(new BigDecimal("0"))!=0){
                    dhl= sh.divide(cg, 4);
                }

                if(statusList.contains(0)||statusList.contains(1)){
                        if(statusList.contains(1)){
                            //部分收货
                            receiveStatus="0";
                        }else{
                            //待收货
                            receiveStatus="1";
                        }
                    }
                else
                {
                    //收货结束
                    receiveStatus="2";
                }

                //pay
                for (Map<String, Object> onePay : payData){
                    DCP_PurOrderDetailQueryRes.PayList payList = res.new PayList();

                    payList.setItem(onePay.get("ITEM").toString());
                    payList.setPayType(onePay.get("PAYTYPE").toString());
                    payList.setPayAmt(onePay.get("PAYAMOUNT").toString());
                    payList.setPayBillNo(onePay.get("PAYBILLNO").toString());
                    payList.setPurAmt(onePay.get("PURAMT").toString());
                    lev1Elm.getPayList().add(payList);
                }
                lev1Elm.setIsPrePay(lev1Elm.getPayList().size()>0?"Y":"N");

                lev1Elm.setReceiveStatus(receiveStatus);
                lev1Elm.setPurOrderNo(oneData.get("PURORDERNO").toString());
                lev1Elm.setPurOrgNo(oneData.get("ORGANIZATIONNO").toString());
                lev1Elm.setPurOrgName(oneData.get("ORGNAME").toString());
                lev1Elm.setBDate(oneData.get("BDATES").toString());
                lev1Elm.setSupplier(oneData.get("SUPPLIER").toString());
                lev1Elm.setSupplierName(oneData.get("SUPPLIERNAME").toString());
                lev1Elm.setPurType(oneData.get("PURTYPE").toString());
                lev1Elm.setReceiveOrgNo(oneData.get("RECEIPTORGNO").toString());
                lev1Elm.setReceiveOrgName(oneData.get("RECEIVEORGNAME").toString());
                lev1Elm.setVersion(oneData.get("VERSION").toString());
                lev1Elm.setMemo(oneData.get("MEMO").toString());
                lev1Elm.setExpireDate(oneData.get("EXPIREDATES").toString());
                lev1Elm.setPurEmpID(oneData.get("EMPLOYEEID").toString());
                lev1Elm.setPurDeptID(oneData.get("DEPARTID").toString());
                lev1Elm.setPayType(oneData.get("PAYTYPE").toString());

                lev1Elm.setDistriCenter(oneData.get("DISTRICENTER").toString());
                lev1Elm.setDistriCenterName(oneData.get("DISTRICENTERNAME").toString());//todo
                lev1Elm.setAddress(oneData.get("ADDRESS").toString());
                lev1Elm.setTelephone(oneData.get("TELEPHONE").toString());
                lev1Elm.setContact(oneData.get("CONTACT").toString());
                lev1Elm.setContactName(oneData.get("CONTACTNAME").toString());
                lev1Elm.setSourceType(oneData.get("SOURCETYPE").toString());
                lev1Elm.setSourceBillNo(oneData.get("SOURCEBILLNO").toString());

                lev1Elm.setPurEmpName(oneData.get("PUREMPNAME").toString());
                lev1Elm.setPurDeptName(oneData.get("PURDEPTNAME").toString());
                lev1Elm.setPayOrgNo(oneData.get("PAYORGNO").toString());
                lev1Elm.setPayOrgName(oneData.get("PAYORGNAME").toString());
                lev1Elm.setBilldateNo(oneData.get("BILLDATENO").toString());
                lev1Elm.setPaydateNo(oneData.get("PAYDATENO").toString());
                lev1Elm.setInvoiceCode(oneData.get("INVOICECODE").toString());
                lev1Elm.setInvoiceName(oneData.get("INVOICENAME").toString());
                lev1Elm.setBilldateDesc(oneData.get("BILLDATENAME").toString());
                lev1Elm.setPaydateDesc(oneData.get("PAYDATENAME").toString());
                lev1Elm.setCurrencyName(oneData.get("CURRENCYNAME").toString());
                lev1Elm.setCurrency(oneData.get("CURRENCY").toString());
                lev1Elm.setTotcQty(String.valueOf(pluNos.size()));
                lev1Elm.setTotpQty(cg.toString());
                lev1Elm.setTotnQty(sh.toString());//通知收货数量合计  todo
                lev1Elm.setTotrQty(sh.toString()); //收货量合计
                lev1Elm.setTotsQty(rk.toString()); //入库量合计

                lev1Elm.setTot_purAmt(tot_purAmt.toString());
                lev1Elm.setTot_preTaxAmt(tot_preTaxAmt.toString());
                lev1Elm.setTot_TaxAmt(tot_TaxAmt.toString());
                lev1Elm.setRate(dhl.toString()); //到货率
                lev1Elm.setStatus(oneData.get("STATUS").toString());
                lev1Elm.setCreatorID(oneData.get("CREATEOPID").toString());
                lev1Elm.setCreatorName(oneData.get("CREATEOPNAME").toString());
                lev1Elm.setCreatorDeptID(oneData.get("CREATEDEPTID").toString());
                lev1Elm.setCreatorDeptName(oneData.get("CREATEDEPTNAME").toString());
                lev1Elm.setCreate_datetime(oneData.get("CREATETIME").toString());
                lev1Elm.setLastmodifyID(oneData.get("LASTMODIOPID").toString());
                lev1Elm.setLastmodifyName(oneData.get("LASTMODIOPNAME").toString());
                lev1Elm.setLastmodify_datetime(oneData.get("LASTMODITIME").toString());
                lev1Elm.setConfirmID(oneData.get("CONFIRMBY").toString());
                lev1Elm.setConfirmName(oneData.get("CONFIRMNAME").toString());
                lev1Elm.setConfirm_datetime(oneData.get("CONFIRMTIME").toString());
                lev1Elm.setCancelBy(oneData.get("CANCELBY").toString());
                lev1Elm.setCancelByName(oneData.get("CANCELBYNAME").toString());
                lev1Elm.setCancel_datetime(oneData.get("CANCELTIME").toString());
                lev1Elm.setCloseBy(oneData.get("CLOSEBY").toString());
                lev1Elm.setCloseByName(oneData.get("CLOSEBYNAME").toString());
                lev1Elm.setCloseBy_datetime(oneData.get("CLOSETIME").toString());
                lev1Elm.setOwnerID(oneData.get("OWNOPID").toString());
                lev1Elm.setOwnerName(oneData.get("OWNERNAME").toString());
                lev1Elm.setOwnDeptID(oneData.get("OWNDEPTID").toString());
                lev1Elm.setOwnDeptName(oneData.get("OWNDEPTNAME").toString());
                lev1Elm.setTaxCode(oneData.get("TAXCODE").toString());
                lev1Elm.setTaxRate(oneData.get("TAXRATE").toString());
                lev1Elm.setTaxName(oneData.get("TAXNAME").toString());
                if(lev1Elm.getStatus().equals("0")||lev1Elm.getStatus().equals("3")){
                    lev1Elm.setReceiveStatus("");
                }
                lev1Elm.setPayee(oneData.get("PAYEE").toString());
                lev1Elm.setPayeeName(oneData.get("PAYEENAME").toString());
                lev1Elm.setCorp(oneData.get("CORP").toString());
                lev1Elm.setCorpName(oneData.get("CORPNAME").toString());
                lev1Elm.setReceiptCorp(oneData.get("RECEIPTCORP").toString());
                lev1Elm.setReceiptCorpName(oneData.get("RECEIPTCORPNAME").toString());
                lev1Elm.setReceiptOrgNo(oneData.get("RECEIPTORGNO").toString());
                lev1Elm.setReceiptOrgName(oneData.get("RECEIPTORGNAME").toString());
                lev1Elm.setTaxPayerType(oneData.get("TAXPAYER_TYPE").toString());
                lev1Elm.setInputTaxCode(oneData.get("INPUT_TAXCODE").toString());
                lev1Elm.setInputTaxRate(oneData.get("INPUT_TAXRATE").toString());
                res.getDatas().add(lev1Elm);
            }


        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PurOrderDetailQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String langType = req.getLangType();
        String purOrderNo = req.getRequest().getPurOrderNo();
        sqlbuf.append(" "
                + " select distinct a.*,b.fname as supplierName,c.org_name as orgName,dd0.departname as owndeptName,dd1.departname as CREATEDEPTNAME,em0.name as ownerName, em1.op_name as CREATEOPNAME,em2.op_name as LASTMODIOPNAME,em5.op_name as closeByName,em3.op_name as confirmName,em4.op_name as cancelByName," +
                "    em6.name as purEmpName,dd2.departname as  purDeptName,c1.org_name as payorgname," +
                "  invoice.INVOICE_NAME as INVOICENAME,cu.name as currencyname,billdate.name as billdatename ,paydate.name as paydatename,d.ORG_NAME as DISTRICENTERNAME,f.org_name as receiveorgname,a.bdate as bdates,a.expiredate as expiredateS,g.taxcode,g.taxname,h.sname as payeename,i.org_name as corpname,j.org_name as receiptcorpname,k.org_name as receiptorgname," +
                "  em7.name as contactname,g1.taxrate  " +
                "  from DCP_PURORDER a"
                + " left join DCP_PURORDER_DETAIL d on d.eid=a.eid and d.purorderno=a.purorderno "
                + " left join DCP_BIZPARTNER b  on b.bizpartnerno=a.SUPPLIER and b.eid=a.eid   "
                + " left join dcp_org_lang c on c.eid=a.eid and a.ORGANIZATIONNO=c.organizationno  and c.lang_type = '"+langType+"' "
                + " left join dcp_org_lang c1 on c1.eid=a.eid and a.PAYORGNO=c1.organizationno  and c1.lang_type = '"+langType+"' "
                + " left join DCP_employee em0 on em0.eid=a.eid and em0.employeeno=a.OWNOPID "
                + " left join PLATFORM_STAFFS_LANG em1 on em1.eid=a.eid and em1.opno=a.CREATEOPID and em1.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em2 on em2.eid=a.eid and em2.opno=a.LASTMODIOPID and em2.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em3 on em3.eid=a.eid and em3.opno=a.CONFIRMBY and em3.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em4 on em4.eid=a.eid and em4.opno=a.CANCELBY and em4.lang_type='"+langType+"'"
                + " left join PLATFORM_STAFFS_LANG em5 on em5.eid=a.eid and em5.opno=a.CLOSEBY and em5.lang_type='"+langType+"'"
                + " left join DCP_employee em6 on em6.eid=a.eid and em6.employeeno=a.EMPLOYEEID "
                + " left join DCP_employee em7 on em7.eid=a.eid and em7.employeeno=a.contact "
                + " left join dcp_department_lang dd0 on dd0.eid=a.eid and dd0.departno=a.OWNDEPTID and dd0.lang_type='"+req.getLangType()+"'  "
                + " left join dcp_department_lang dd1 on dd1.eid=a.eid and dd1.departno=a.createdeptid and dd1.lang_type='"+req.getLangType()+"'  "
                + " left join dcp_department_lang dd2 on dd2.eid=a.eid and dd2.departno=a.DEPARTID and dd2.lang_type='"+req.getLangType()+"'  "
                + " left join DCP_INVOICETYPE_LANG invoice on invoice.eid=a.eid and invoice.invoiceCode=a.invoiceCode and invoice.lang_type='"+req.getLangType()+"'"
                + " left join DCP_CURRENCY_LANG cu on cu.eid=a.eid and cu.currency=a.currency and cu.lang_type='"+req.getLangType()+"'"
                + " left join DCP_BILLDATE_LANG billdate on billdate.eid=a.eid and billdate.BILLDATENO=a.BILLDATENO and billdate.lang_type='"+req.getLangType()+"'"
                + " left join DCP_PAYDATE_LANG paydate on paydate.eid=a.eid and paydate.paydateno=a.paydateno and paydate.lang_type='"+req.getLangType()+"'"
                + " left join dcp_org_lang d on d.eid=a.eid and d.ORGANIZATIONNO=a.DISTRICENTER and d.lang_type='"+req.getLangType()+"' "
                + " left join dcp_org_lang f on f.eid=a.eid and a.RECEIPTORGNO=f.organizationno  and f.lang_type = '"+langType+"' "
                + " left join DCP_TAXCATEGORY_lang g on g.eid=a.eid and a.taxcode=g.taxcode and g.lang_type='"+req.getLangType()+"' " +
                " left join DCP_TAXCATEGORY g1 on g1.eid=a.eid and a.taxcode=g1.taxcode  " +
                " left join dcp_org_lang i on i.eid=a.eid and i.organizationno=a.corp and i.lang_type='"+req.getLangType()+"' " +
                " left join dcp_org_lang j on j.eid=a.eid and j.organizationno=a.receiptcorp and j.lang_type='"+req.getLangType()+"' " +
                " left join dcp_bizpartner h on h.eid=a.eid and h.bizpartnerno=a.payee" +
                " left join dcp_org_lang k on k.eid=a.eid and k.organizationno=a.receiptorgno and k.lang_type='"+req.getLangType()+"' "
                + " where  a.eid= '"+ eId +"' and a.purorderno='"+purOrderNo+"' "
                + " "
                + " ");

        return sqlbuf.toString();
    }

}

