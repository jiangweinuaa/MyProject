package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_GoodsOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import javafx.geometry.Pos;
import org.apache.cxf.common.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DCP_GoodsOpenQry extends SPosBasicService<DCP_GoodsOpenQryReq, DCP_GoodsOpenQryRes> {
    @Override
    protected boolean isVerifyFail(DCP_GoodsOpenQryReq req) throws Exception {
        boolean isFail = false;
        DCP_GoodsOpenQryReq.levelElm request = req.getRequest();
        StringBuffer errMsg = new StringBuffer("");
        if (request == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String searchScope = request.getSearchScope();
        String orgNo = request.getOrgNo();
        if("3".equals(searchScope)){
            if(Check.Null(orgNo)){
                isFail = true;
                errMsg.append("orgNo不能为空 ");
            }
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_GoodsOpenQryReq> getRequestType() {
        return new TypeToken<DCP_GoodsOpenQryReq>() {
        };
    }

    @Override
    protected DCP_GoodsOpenQryRes getResponseType() {
        return new DCP_GoodsOpenQryRes();
    }

    @Override
    protected DCP_GoodsOpenQryRes processJson(DCP_GoodsOpenQryReq req) throws Exception {
        DCP_GoodsOpenQryRes res = null;
        res = this.getResponse();
        String eId = req.geteId();
        String organizationNo = req.getOrganizationNO();
        String customer = req.getRequest().getCustomer();
        String bType = req.getRequest().getBType();
        String queryStock = req.getRequest().getQueryStockqty();
        String queryStockWarehouse = req.getRequest().getQueryStockWarehouse();

        int totalRecords = 0;                                //总笔数
        int totalPages = 0;

        res.setDatas(new ArrayList<DCP_GoodsOpenQryRes.level1Elm>());
       // try {
        String sql ="";
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getGoodList = this.doQueryData(sql, null);
        List<String> pluNos = getGoodList.stream().map(x -> x.get("PLUNO").toString()).distinct().collect(Collectors.toList());
        List<String> categorys = getGoodList.stream().map(x -> x.get("CATEGORY").toString()).distinct().collect(Collectors.toList());

        String detailSql=getGoodsUnitSql(req,pluNos);
        List<Map<String, Object>> getGoodListDetail = this.doQueryData(detailSql, null);
        String featureSql = this.getFeatureSql(req, pluNos);
        List<Map<String, Object>> featureList = this.doQueryData(featureSql, null);

        String prodSql = this.getProdSql(req, pluNos);
        List<Map<String, Object>> prodList = this.doQueryData(prodSql, null);

        String bomSql = this.getBomSql(req, pluNos);
        List<Map<String, Object>> bomList = this.doQueryData(bomSql, null);

        //税率少 先查一下
        String taxCodeSql="select * from DCP_TAXCATEGORY_LANG a where a.eid='"+req.geteId()+"' and a.lang_type='"+req.getLangType()+"'";
        List<Map<String, Object>> taxCodeList = this.doQueryData(taxCodeSql, null);

        if(!CollectionUtils.isEmpty(getGoodList)){

            String num = getGoodList.get(0).get("NUM").toString();
            totalRecords = Integer.parseInt(num);

                // 计算页数
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            List<Map<String, Object>> plus = new ArrayList<Map<String, Object>>();

            //btype=0  要货模板
            List<Map<String, Object>> yhList=new ArrayList<>();
            if("0".equals(bType)){
                String yhSql="select c.pluno,a.RECEIPT_ORG,a.ptemplateno,c.min_qty,c.mul_qty,c.max_qty " +
                        " from DCP_PTEMPLATE a" +
                        " left join DCP_PTEMPLATE_SHOP b on a.eid=b.eid and a.ptemplateno=b.ptemplateno " +
                        " left join DCP_PTEMPLATE_DETAIL c on c.eid=a.eid and c.ptemplateno=a.ptemplateno " +
                        " where a.eid='"+eId+"' and a.DOC_TYPE='0' and a.status='100' and c.status='100'";
                if(Check.Null(req.getRequest().getOrgNo())){
                    yhSql+=" and (a.SHOPTYPE='1' or (b.SHOPID='"+req.getRequest().getOrgNo()+"' and b.status='100')) ";
                }else{
                    yhSql+=" and (a.SHOPTYPE='1' or (b.SHOPID='"+req.getOrganizationNO()+"' and b.status='100')) ";
                }
               yhList = this.doQueryData(yhSql, null);
            }

            for (Map<String, Object> onePlu :getGoodList) {
                String pluno = onePlu.get("PLUNO").toString();
                List<Map<String, Object>> unitList = getGoodListDetail.stream().filter(x -> x.get("PLUNO").toString().equals(pluno))
                            .collect(Collectors.toList());
                String unit = onePlu.get("UNIT").toString();
                Map<String, Object> plu = new HashMap<String, Object>();
                plu.put("PLUNO", pluno);
                plu.put("PUNIT", unit);
                plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());

                BigDecimal unitRatio=BigDecimal.ZERO;//业务单位比基础单位
                List<Map<String, Object>> unitRatioL1 = unitList.stream().filter(x -> x.get("OUNIT").toString().equals(unit)).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(unitRatioL1)){
                    unitRatio=new BigDecimal(unitRatioL1.get(0).get("UNITRATIO").toString());
                }

                plu.put("UNITRATIO", unitRatio);

                if(yhList.size()>0){
                    List<Map<String, Object>> filterRows = yhList.stream().filter(x -> x.get("PLUNO").toString().equals(pluno)).collect(Collectors.toList());
                    if(filterRows.size()>0){
                        plu.put("SUPPLIERID", filterRows.get(0).get("RECEIPT_ORG").toString());
                    }else if(Check.NotNull(req.getRequest().getSupplierId())){
                        plu.put("SUPPLIERID", req.getRequest().getSupplierId());
                    }
                    else{
                        plu.put("SUPPLIERID", "");
                    }
                }else if(Check.NotNull(req.getRequest().getSupplierId())){
                    plu.put("SUPPLIERID", req.getRequest().getSupplierId());
                }else{
                    plu.put("SUPPLIERID", "");
                }

                plus.add(plu);
            }
            MyCommon mc = new MyCommon();
            String orgNo = req.getOrganizationNO();
            String searchScope = req.getRequest().getSearchScope();
            if("2".equals(searchScope)){
                orgNo = req.getRequest().getOrgNo();
            }
            //查询当前组织
            String orgSql="select a.*,b.taxrate as inputtaxrate from dcp_org a " +
                    " left join dcp_taxcategory b on a.eid=b.eid and a.INPUT_TAXCODE=b.taxcode and b.taxarea='CN' " +
                    " where a.eid='"+eId+"' and a.organizationno='"+orgNo+"'";
            List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);

            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, req.geteId(), req.getBELFIRM(), orgNo, plus,req.getBELFIRM());

            for (Map<String, Object> good : getGoodList) {
                DCP_GoodsOpenQryRes.level1Elm level1Elm = res.new level1Elm();
                String pluno = good.get("PLUNO").toString();
                List<Map<String, Object>> unitList = getGoodListDetail.stream().filter(x -> x.get("PLUNO").toString().equals(pluno))
                            .collect(Collectors.toList());

                level1Elm.setStatus(good.get("STATUS").toString());
                level1Elm.setPluNo(pluno);
                level1Elm.setListImage(good.get("LISTIMAGE").toString());
                level1Elm.setPluName(good.get("PLUNAME").toString());
                level1Elm.setPluBarcode(good.get("BARCODE").toString());
                level1Elm.setCategory(good.get("CATEGORY").toString());
                level1Elm.setCategoryName(good.get("CATEGORYNAME").toString());
                level1Elm.setSpec(good.get("SPEC").toString());
                level1Elm.setWUnit(good.get("WUNIT").toString());
                level1Elm.setWUnitName(good.get("WUNITNAME").toString());
                level1Elm.setBaseUnit(good.get("BASEUNIT").toString());
                level1Elm.setBaseUnitName(good.get("BASEUNITNAME").toString());
                level1Elm.setPurUnit(good.get("PUNIT").toString());
                level1Elm.setPurUnitName(good.get("PUNITNAME").toString());
                level1Elm.setSUnit(good.get("SUNIT").toString());
                level1Elm.setSUnitName(good.get("SUNITNAME").toString());
                level1Elm.setUnit(good.get("UNIT").toString());
                level1Elm.setUnitName(good.get("UNITNAME").toString());

                level1Elm.setUnitUdLength(good.get("UNITUDLENGTH").toString());
                level1Elm.setUnitRoundType(good.get("UNITROUNDTYPE").toString());
                level1Elm.setBaseUnitUdLength(good.get("BASEUNITUDLENGTH").toString());
                level1Elm.setBaseUnitRoundType(good.get("BASEUNITROUNDTYPE").toString());

                level1Elm.setProdMulQty(good.get("PRODMULQTY").toString());
                level1Elm.setProdMinQty(good.get("PRODMINQTY").toString());
                level1Elm.setRemainType(good.get("REMAINTYPE").toString());


                //给个空值
                level1Elm.setOddValue("");
                level1Elm.setProductExceed("");
                level1Elm.setProcRate("");
                level1Elm.setDispType("");
                level1Elm.setSemiWoType("");
                level1Elm.setSemiWoDeptType("");
                level1Elm.setFixPreDays("");
                level1Elm.setBomNo("");
                level1Elm.setBomVersion("");

                level1Elm.setMinQty(good.get("MINQTY").toString());
                level1Elm.setMulQty(good.get("MULQTY").toString());
                level1Elm.setMaxQty(good.get("MAXQTY").toString());
                level1Elm.setTemplateNo("");

                if("0".equals(bType)){
                    if(yhList.size()>0){
                        List<Map<String, Object>> collect = yhList.stream().filter(y -> y.get("PLUNO").toString().equals(level1Elm.getPluNo()) ).collect(Collectors.toList());
                        if(collect.size()>0){
                            level1Elm.setMinQty(collect.get(0).get("MIN_QTY").toString());
                            level1Elm.setMulQty(collect.get(0).get("MUL_QTY").toString());
                            level1Elm.setMaxQty(collect.get(0).get("MAX_QTY").toString());
                            level1Elm.setTemplateNo(collect.get(0).get("PTEMPLATENO").toString());
                        }
                    }
                }



                if("4".equals(bType)) {//"2".equals(bType)||
                    List<Map<String, Object>> prodFilter = prodList.stream().filter(p -> p.get("PLUNO").toString().equals(level1Elm.getPluNo())).collect(Collectors.toList());

                    if(prodFilter.size()>0){
                        level1Elm.setProdMulQty(prodFilter.get(0).get("PRODMULQTY").toString());
                        level1Elm.setProdMinQty(prodFilter.get(0).get("PRODMINQTY").toString());
                        level1Elm.setRemainType(prodFilter.get(0).get("REMAINTYPE").toString());
                        level1Elm.setUnit(prodFilter.get(0).get("PRODUNIT").toString());
                        level1Elm.setUnitName(prodFilter.get(0).get("PRODUNITNAME").toString());
                    }else{
                        List<Map<String, Object>> bomFilter = bomList.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo())).collect(Collectors.toList());
                        if(bomFilter.size()>0){
                            level1Elm.setProdMulQty(bomFilter.get(0).get("MULQTY").toString());
                            level1Elm.setProdMinQty(bomFilter.get(0).get("MINQTY").toString());
                            level1Elm.setUnit(bomFilter.get(0).get("UNIT").toString());
                            level1Elm.setUnitName(bomFilter.get(0).get("UNITNAME").toString());
                            level1Elm.setRemainType(bomFilter.get(0).get("REMAINTYPE").toString());
                        }
                    }

                    if(Check.Null(req.getRequest().getSearchScope())||"0".equals(req.getRequest().getSearchScope())){
                        List<Map<String, Object>> bomFilter = bomList.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo())).collect(Collectors.toList());
                        if(bomFilter.size()>0){
                            level1Elm.setOddValue(bomFilter.get(0).get("ODDVALUE").toString());
                            level1Elm.setProductExceed(bomFilter.get(0).get("PRODUCTEXCEED").toString());
                            level1Elm.setProcRate(bomFilter.get(0).get("PROCRATE").toString());
                            level1Elm.setDispType(bomFilter.get(0).get("DISPTYPE").toString());
                            level1Elm.setSemiWoType(bomFilter.get(0).get("SEMIWOTYPE").toString());
                            level1Elm.setSemiWoDeptType(bomFilter.get(0).get("SEMIWOTYPE").toString());
                            level1Elm.setFixPreDays(bomFilter.get(0).get("FIXPREDAYS").toString());
                            level1Elm.setBomNo(bomFilter.get(0).get("BOMNO").toString());
                            level1Elm.setBomVersion(bomFilter.get(0).get("VERSIONNUM").toString());

                        }
                    }
                    else if("1".equals(req.getRequest().getSearchScope())||"2".equals(req.getRequest().getSearchScope())) {

                        List<Map<String, Object>> bomFilter = bomList.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo())).collect(Collectors.toList());
                        if (prodFilter.size() > 0) {
                            level1Elm.setOddValue(prodFilter.get(0).get("ODDVALUE").toString());
                            level1Elm.setProductExceed(prodFilter.get(0).get("PRODUCTEXCEED").toString());
                            level1Elm.setProcRate(prodFilter.get(0).get("PROCRATE").toString());
                            level1Elm.setDispType(prodFilter.get(0).get("DISPTYPE").toString());
                            level1Elm.setSemiWoType(prodFilter.get(0).get("SEMIWOTYPE").toString());
                            level1Elm.setSemiWoDeptType(prodFilter.get(0).get("SEMIWOTYPE").toString());
                            level1Elm.setFixPreDays(prodFilter.get(0).get("FIXPREDAYS").toString());
                            if (bomFilter.size() > 0) {
                                level1Elm.setBomNo(bomFilter.get(0).get("BOMNO").toString());
                                level1Elm.setBomVersion(bomFilter.get(0).get("VERSIONNUM").toString());
                            }
                        } else {
                            if (bomFilter.size() > 0) {
                                level1Elm.setOddValue(bomFilter.get(0).get("ODDVALUE").toString());
                                level1Elm.setProductExceed(bomFilter.get(0).get("PRODUCTEXCEED").toString());
                                level1Elm.setProcRate(bomFilter.get(0).get("PROCRATE").toString());
                                level1Elm.setDispType(bomFilter.get(0).get("DISPTYPE").toString());
                                level1Elm.setSemiWoType(bomFilter.get(0).get("SEMIWOTYPE").toString());
                                level1Elm.setSemiWoDeptType(bomFilter.get(0).get("SEMIWOTYPE").toString());
                                level1Elm.setFixPreDays(bomFilter.get(0).get("FIXPREDAYS").toString());
                                level1Elm.setBomNo(bomFilter.get(0).get("BOMNO").toString());
                                level1Elm.setBomVersion(bomFilter.get(0).get("VERSIONNUM").toString());


                            }
                        }
                    }
                }
                level1Elm.setStockQty("");
                level1Elm.setSafeQty(good.get("SAFEQTY").toString());
                level1Elm.setWarningQty(good.get("WARNINGQTY").toString());
                level1Elm.setSourceType(good.get("SOURCETYPE").toString());
                level1Elm.setIsQualityCheck(good.get("ISQUALITYCHECK").toString());

                BigDecimal unitRatio=BigDecimal.ZERO;//业务单位比基础单位
                List<Map<String, Object>> unitRatioL1 = unitList.stream().filter(x -> x.get("OUNIT").toString().equals(level1Elm.getUnit())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(unitRatioL1)){
                    unitRatio=new BigDecimal(unitRatioL1.get(0).get("UNITRATIO").toString());
                }
                BigDecimal unitRatio2=BigDecimal.ZERO;//库存单位比基础单位
                List<Map<String, Object>> unitRatioL2 = unitList.stream().filter(x -> x.get("OUNIT").toString().equals(level1Elm.getWUnit())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(unitRatioL2)){
                    unitRatio2=new BigDecimal(unitRatioL2.get(0).get("UNITRATIO").toString());
                }

                BigDecimal unitRatio3=BigDecimal.ZERO;//采购单位比基础单位
                List<Map<String, Object>> unitRatioL3 = unitList.stream().filter(x -> x.get("OUNIT").toString().equals(level1Elm.getPurUnit())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(unitRatioL3)){
                    unitRatio3=new BigDecimal(unitRatioL3.get(0).get("UNITRATIO").toString());
                }

                BigDecimal wunitRatio=new BigDecimal(0);
                if(unitRatio.compareTo(BigDecimal.ZERO)>0&&unitRatio2.compareTo(BigDecimal.ZERO)>0){
                    wunitRatio=unitRatio.divide(unitRatio2,2,BigDecimal.ROUND_HALF_UP);
                }

                BigDecimal cgunitRatio=new BigDecimal(0);
                if(unitRatio.compareTo(BigDecimal.ZERO)>0&&unitRatio3.compareTo(BigDecimal.ZERO)>0){
                    cgunitRatio=unitRatio.divide(unitRatio3,2,BigDecimal.ROUND_HALF_UP);
                }
                String price="0";
                String distriPrice="0";

                Map<String, Object> condiV = new HashMap<String, Object>();
                condiV.put("PLUNO", pluno);
                condiV.put("PUNIT", level1Elm.getUnit());
                List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                if (priceList != null && priceList.size() > 0) {
                    price = priceList.get(0).get("PRICE").toString();
                    distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                }

                level1Elm.setUnitRatio(unitRatio.toString());
                level1Elm.setWUnitRatio(wunitRatio.toString());
                level1Elm.setPrice(price);
                level1Elm.setStandardPrice(good.get("PRICE").toString());//标准零售价
                level1Elm.setPurPrice(Check.Null(good.get("PURPRICE").toString())?"0":good.get("PURPRICE").toString());//标准进货价格
                level1Elm.setSdPurPrice((new BigDecimal(level1Elm.getPurPrice())).multiply(cgunitRatio).toString());

                level1Elm.setDistriPrice(distriPrice);

                level1Elm.setIsBatch(good.get("ISBATCH").toString());
                level1Elm.setShelfLife(good.get("SHELFLIFE").toString());
                level1Elm.setVirtual(good.get("VIRTUAL").toString());
                level1Elm.setPluType(good.get("PLUTYPE").toString());
                level1Elm.setStockManageType(good.get("STOCKMANAGETYPE").toString());
                level1Elm.setProducer(good.get("PRODUCER").toString());
                level1Elm.setManufacturer(good.get("MANUFACTURER").toString());

                level1Elm.setSupplierType(good.get("SUPPLIERTYPE").toString());
                level1Elm.setSupplierId(good.get("SUPPLIERID").toString());
                level1Elm.setGtSupplier(good.get("SUPPLIERID").toString());
                level1Elm.setGoodsSupplier(good.get("GOODSSUPPLIER").toString());
                level1Elm.setSupplierName(good.get("SUPPLIER_NAME").toString());
                level1Elm.setCustPrice("");
                level1Elm.setCateDiscRate("");
                level1Elm.setCustLastPrice("");
                level1Elm.setOutPutTaxCode(good.get("TAXCODE").toString());//dcp_goods上面的taxcode
                level1Elm.setOutPutTaxRate(good.get("TAXRATE").toString());
                level1Elm.setOutPutInclTax(good.get("INCLTAX").toString());
                level1Elm.setOutPutTaxCalType(good.get("TAXCALTYPE").toString());
                level1Elm.setPickMinQty(good.get("PICKMINQTY").toString());
                level1Elm.setPickMulQty(good.get("PICKMULQTY").toString());

                level1Elm.setUnitlist(new ArrayList<>());
                for (Map<String, Object> unit : unitList){
                    DCP_GoodsOpenQryRes.Unitlist level2Elm = res.new Unitlist();
                    level2Elm.setUnit(unit.get("OUNIT").toString());
                    level2Elm.setUnitName(unit.get("OUNITNAME").toString());
                    level2Elm.setSUnitUse(unit.get("SUNIT_USE").toString());
                    level2Elm.setPUnitUse(unit.get("PUNIT_USE").toString());
                    level2Elm.setProdUnitUse(unit.get("PROD_UNIT_USE").toString());
                    level2Elm.setPurUnitUse(unit.get("PUNIT_USE").toString());
                    level2Elm.setCUnitUse(unit.get("CUNIT_USE").toString());
                    level2Elm.setBomUnitUse(unit.get("BOM_UNIT_USE").toString());
                    level2Elm.setUnitRatio(unit.get("UNITRATIO").toString());
                    level1Elm.getUnitlist().add(level2Elm);
                }

                level1Elm.setFeatureList(new ArrayList<>());
                List<Map<String, Object>> singleFeatureList = featureList.stream().filter(x -> x.get("PLUNO").toString().equals(level1Elm.getPluNo())&&(!Check.Null(x.get("FEATURENO").toString()))).distinct().collect(Collectors.toList());
                for (Map<String, Object> feature : singleFeatureList){
                    DCP_GoodsOpenQryRes.FeatureList fl = res.new FeatureList();
                    fl.setFeatureNo(feature.get("FEATURENO").toString());
                    fl.setFeatureName(feature.get("FEATURENAME").toString());
                    level1Elm.getFeatureList().add(fl);
                }

                if(Check.NotNull(level1Elm.getOutPutTaxCode())){
                    List<Map<String, Object>> taxFilterRows = taxCodeList.stream().filter(x -> x.get("TAXCODE").toString().equals(level1Elm.getOutPutTaxCode())).collect(Collectors.toList());
                    if(taxFilterRows.size()>0){
                        level1Elm.setOutPutTaxName(taxFilterRows.get(0).get("TAXNAME").toString());
                    }
                }

                level1Elm.setInputTaxCode(good.get("INPUTTAXCODE").toString());
                level1Elm.setInputTaxRate(good.get("INPUTTAXRATE").toString());
                if(Check.NotNull(level1Elm.getInputTaxCode())){
                    List<Map<String, Object>> taxFilterRows = taxCodeList.stream().filter(x -> x.get("TAXCODE").toString().equals(level1Elm.getInputTaxCode())).collect(Collectors.toList());
                    if(taxFilterRows.size()>0){
                        level1Elm.setInputTaxName(taxFilterRows.get(0).get("TAXNAME").toString());
                    }
                }
                res.getDatas().add(level1Elm);
            }

            if("1".equals(bType)&&("1".equals(searchScope)||"2".equals(searchScope))){
                //部分值取采购模板的
                String isPurTemplateRequire = req.getRequest().getIsPurTemplateRequire();
                //-isPurTemplateRequire=Y或null时，返回存在采购模板商品明细（null值默认必须按模板采购）--供应商编号非必传
                //-isPurTemplateRequire=N时，优先取供应商采购模板商品，供应商无采购模板则返回商品模板中【供货类型supplierType】<>"FACTORY统配"且【允许采购canPurchase】=Y且状态有效的商品--供应商编号必传

                String ptSql="select a.SUPPLIERNO,e.sname as suppliername,a.purtemplateno,c.pluno,c.MINPQTY,c.MULPQTY,a.PURTYPE" +
                        " ,a.pre_day,C.MAXRATE,a.purcenter,d.org_name as purcentername,c.purbaseprice,c.purunit,f.uname as purunitname,c.taxcode as inputtaxcode,g.taxrate as inputtaxrate " +
                        " from DCP_PURCHASETEMPLATE a " +
                        " inner join DCP_PURCHASETEMPLATE_ORG b on a.eid=b.eid and a.purtemplateno=b.purtemplateno and b.status='100' " +
                        " inner join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.status='100' " +
                        " left join dcp_org_lang d on d.eid=a.eid and d.organizationno=a.purcenter and d.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_bizpartner e on e.eid=a.eid and e.bizpartnerno=a.supplierno " +
                        " LEFT JOIN DCP_UNIT_LANG f on f.eid=a.eid and f.unit=c.purunit and f.lang_type='"+req.getLangType()+"' " +
                        " left join dcp_taxcategory g on g.eid=a.eid and g.taxcode=c.taxcode and g.taxarea='CN' " +
                        " where a.eid='"+eId+"' and a.status='100' ";
                if(Check.NotNull(req.getRequest().getOrgNo())){
                    ptSql+=" and b.organizationno='"+req.getRequest().getOrgNo()+"' ";
                }else{
                    ptSql+=" and b.organizationno='"+req.getOrganizationNO()+"' ";
                }
                if(Check.NotNull(req.getRequest().getSupplierId())){
                    ptSql+=" and a.SUPPLIERNO='"+req.getRequest().getSupplierId()+"' ";
                }

                List<Map<String, Object>> getPtDataList = this.doQueryData(ptSql, null);

                for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()){
                    List<Map<String, Object>> filterRows = getPtDataList.stream().filter(x -> x.get("PLUNO").toString().equals(lv1.getPluNo())).collect(Collectors.toList());

                    //选供应商  先选商品模板的 再选商品的  最后随便选一个
                    String thisSupplier="";
                    if(Check.NotNull(lv1.getGtSupplier())){
                        List<Map<String, Object>> filterRows1 = filterRows.stream().filter(y -> y.get("SUPPLIERNO").toString().equals(lv1.getGtSupplier())).collect(Collectors.toList());
                        if(filterRows1.size()>0){
                            thisSupplier=lv1.getGtSupplier();
                        }
                    }

                    if(Check.Null(thisSupplier)&&Check.NotNull(lv1.getGoodsSupplier())){
                        List<Map<String, Object>> filterRows1 = filterRows.stream().filter(y -> y.get("SUPPLIERNO").toString().equals(lv1.getGoodsSupplier())).collect(Collectors.toList());
                        if(filterRows1.size()>0){
                            thisSupplier=lv1.getGoodsSupplier();
                        }
                    }

                    if(Check.Null(thisSupplier)&&filterRows.size()>0){
                        thisSupplier=filterRows.get(0).get("SUPPLIERNO").toString();
                    }

                    String finalThisSupplier = thisSupplier;
                    List<Map<String, Object>> filterRows2 = filterRows.stream().filter(x -> x.get("SUPPLIERNO").toString().equals(finalThisSupplier)).collect(Collectors.toList());

                    //if("N".equals(isPurTemplateRequire)&&filterRows.size()>0){
                   //     List<Map<String, Object>> filterRows1 = filterRows.stream().filter(y -> y.get("SUPPLIERNO").toString().equals(lv1.getSupplierId())).collect(Collectors.toList());
                    //    if(filterRows1.size()>0){
                    //        filterRows=filterRows1;
                    //    }
                    //}

                    if(filterRows2.size()>0){
                        lv1.setSupplierName(filterRows2.get(0).get("SUPPLIERNAME").toString());
                        lv1.setSupplierId(filterRows2.get(0).get("SUPPLIERNO").toString());
                        lv1.setPurMinQty(filterRows2.get(0).get("MINPQTY").toString());
                        lv1.setPurMulQty(filterRows2.get(0).get("MULPQTY").toString());
                        lv1.setPurType(filterRows2.get(0).get("PURTYPE").toString());
                        lv1.setPurPreDays(filterRows2.get(0).get("PRE_DAY").toString());
                        lv1.setTemplateNo(filterRows2.get(0).get("PURTEMPLATENO").toString());
                        lv1.setPurPriceMaxRate(filterRows2.get(0).get("MAXRATE").toString());
                        lv1.setPurCenter(filterRows2.get(0).get("PURCENTER").toString());
                        lv1.setPurCenterName(filterRows2.get(0).get("PURCENTERNAME").toString());
                        lv1.setPurPrice(filterRows2.get(0).get("PURBASEPRICE").toString());
                        lv1.setPurUnit(filterRows2.get(0).get("PURUNIT").toString());
                        lv1.setPurUnitName(filterRows2.get(0).get("PURUNITNAME").toString());

                        //采购模板不为空
                        if(Check.NotNull(filterRows2.get(0).get("PURTEMPLATENO").toString())){
                            if(Check.NotNull(filterRows2.get(0).get("INPUTTAXCODE").toString())){
                                lv1.setInputTaxCode(filterRows2.get(0).get("INPUTTAXCODE").toString());
                                lv1.setInputTaxRate(filterRows2.get(0).get("INPUTTAXRATE").toString());
                            }
                            //之后可能会根据入参supplierid 及法人再覆盖一次
                        }


                    }
                }
            }

            if(!Check.Null(customer)&&("8".equals(bType)||"2".equals(bType))){
                //DCP_CUSTOMER_PRICE 大客户价格（含客户组价格）
                //DCP_CUSTOMER_CATE_DISC大客户折扣（含客户组折扣）
                //先找客户价格  再找客户组价格
                String cgSql="select a.CUSTGROUPNO from DCP_CUSTGROUP a " +
                        " inner join DCP_CUSTGROUP_DETAIL b on a.eid=b.eid and b.ATTRTYPE='2' and a.CUSTGROUPNO=b.CUSTGROUPNO " +
                        " inner join DCP_BIZPARTNER c ON c.eid=a.eid and c.BIZTYPE in ('2','3') and b.ATTRID=c.BIZPARTNERNO " +
                        " where a.status='100' and c.BIZPARTNERNO='"+customer+"' " +
                        " union all  (" +
                        " select a.CUSTGROUPNO from DCP_CUSTGROUP a  " +
                        " inner join DCP_CUSTGROUP_DETAIL b on a.eid=b.eid and b.ATTRTYPE='1' and a.CUSTGROUPNO=b.CUSTGROUPNO " +
                        " inner join DCP_TAGTYPE c on a.eid=c.eid and b.ATTRID=c.tagno and c.TAGGROUPTYPE='CUST' AND c.status='100' " +
                        " inner join DCP_TAGTYPE_detail d on c.eid=d.eid and c.TAGGROUPTYPE=d.TAGGROUPTYPE and c.tagno=d.tagno " +
                        " inner join DCP_BIZPARTNER e ON e.eid=a.eid and e.BIZTYPE in ('2','3') and d.id=e.BIZPARTNERNO " +
                        " where a.status='100' and e.BIZPARTNERNO='"+customer+"'" +
                        " )" +
                        "";
                List<Map<String, Object>> cgList = this.doQueryData(cgSql, null);
                List<String> custGroupNos = cgList.stream().distinct().map(x -> x.get("CUSTGROUPNO").toString()).collect(Collectors.toList());

                if(!custGroupNos.contains(customer)){
                    custGroupNos.add(customer);
                }
                String custGroupStr="";
                for (String custGroupNo : custGroupNos){
                    custGroupStr+="'"+custGroupNo+"',";
                }
                custGroupStr=custGroupStr.substring(0,custGroupStr.length()-1);
                String cpSql="select * from DCP_CUSTOMER_PRICE a where a.eid='"+eId+"' and a.customerno in ("+custGroupStr+") and a.status='100' and to_char(sysdate,'yyyyMMdd')<=a.ENDDATE  order by a.lastmoditime ";
                List<Map<String, Object>> cpList = this.doQueryData(cpSql, null);
                String crSql="select * from DCP_CUSTOMER_CATE_DISC a where a.eid='"+eId+"' and a.customerno in ("+custGroupStr+") and a.status='100' order by a.lastmoditime ";
                List<Map<String, Object>> crList = this.doQueryData(crSql, null);


                String ssSql="select * from DCP_SSTOCKOUT a" +
                        " left join  DCP_SSTOCKOUT_detail b on a.eid=b.eid and a.sstockoutno=b.sstockoutno " +
                        " where a.eid='"+eId+"' and a.STOCKOUTTYPE='2' " +
                        " and a.customer='"+customer+"' and a.status='2' and b.pqty-nvl(b.returnqty,0)>0 " +
                        " order by a.update_time ";
                List<Map<String, Object>> ssList = this.doQueryData(ssSql, null);

                for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()){
                    String pluNo = lv1.getPluNo();
                    String category = lv1.getCategory();
                    List<Map<String, Object>> priceList = cpList.stream().filter(x -> x.get("CUSTOMERNO").toString().equals(customer) && x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                    if(priceList.size()>0){
                        lv1.setCustPrice(priceList.get(0).get("PRICE").toString());
                    }
                    else{
                        List<Map<String, Object>> priceList2 = cpList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                        if(priceList2.size()>0){
                            lv1.setCustPrice(priceList2.get(0).get("PRICE").toString());
                        }
                    }

                    List<Map<String, Object>> rateList = crList.stream().filter(x -> x.get("CUSTOMERNO").toString().equals(customer) && x.get("CATEGORYID").toString().equals(category)).collect(Collectors.toList());
                    if(rateList.size()>0){
                        lv1.setCateDiscRate(rateList.get(0).get("DISCRATE").toString());
                    }
                    else{
                        List<Map<String, Object>> rateList2 = crList.stream().filter(x -> x.get("CATEGORYID").toString().equals(category)).collect(Collectors.toList());
                        if(rateList2.size()>0){
                            lv1.setCateDiscRate(rateList2.get(0).get("DISCRATE").toString());
                        }
                    }

                    List<Map<String, Object>> ssPs = ssList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
                    if(ssPs.size()>0){
                        lv1.setCustLastPrice(ssPs.get(0).get("PRICE").toString());
                    }

                }


            }

            if("2".equals(bType)||"8".equals(bType)){
                String taxPayerType="1";
                String outPutTax="";
                if(CollUtil.isNotEmpty(orgList)){
                    taxPayerType = orgList.get(0).get("TAXPAYER_TYPE").toString();
                    outPutTax = orgList.get(0).get("OUTPUTTAX").toString();
                }

                //逻辑：销售类业务查询返回商品在当前组织销项税别
                //1.searchScope查询范围=”1.当前组织可用商品"或"2.指定组织可用商品"时，先判断组织所属法人纳税人性质；
                //当前组织所属法人纳税人性质TAXPAYER_TYPE=“2.小规模纳税人”，则outPutTaxCode=所属法人指定税别DCP_ORG.OUTPUTTAX；
                //当前组织所属法人纳税人性质TAXPAYER_TYPE=“1.一般纳税人”，则取商品对应税种；
                //2.searchScope查询范围=”0.集团所有商品“，取商品对应税种--》2.取商品对应税种优先顺序：商品指定销项税DCP_GOODS.TAXCODE > 税分类，以上均未设置返回null

                if(Check.Null(req.getRequest().getSearchScope())||"0".equals(req.getRequest().getSearchScope())){

                    String withPlu = "";
                    if (pluNos !=null && pluNos.size()>0 ) {

                        Map<String,String> map = new HashMap<>();
                        String sJoinPlu = "";
                        for(String s :pluNos) {
                            sJoinPlu += s +",";
                        }
                        map.put("PLU", sJoinPlu);
                        withPlu = mc.getFormatSourceMultiColWith(map);
                    }

                    String withCategory = "";
                    if (categorys !=null && categorys.size()>0 ) {
                        Map<String,String> map = new HashMap<>();
                        String sJoinCategory = "";
                        for(String s :categorys) {
                            sJoinCategory += s+",";
                        }
                        map.put("CATEGORY", sJoinCategory);
                        withCategory = mc.getFormatSourceMultiColWith(map);
                    }

                    String goodsTaxSql=" with p as ("+withPlu+") " +
                            " select distinct p.plu as pluno,a.taxcode,a.taxrate,a.INCLTAX,a.TAXCALTYPE from DCP_TAXCATEGORY a " +
                            " left join DCP_TAXGROUP b on a.eid=b.eid and a.taxcode=b.taxcode " +
                            " left join DCP_TAXGROUP_DETAIL c on a.eid=c.eid and b.taxgroupno=c.taxgroupno " +
                            " inner join p on p.plu=c.attrid and c.attrtype='2' " +
                            " where a.eid='"+eId+"'";
                    List<Map<String, Object>> goodsTaxList = this.doQueryData(goodsTaxSql, null);

                    String categoryTaxSql=" with p as ("+withCategory+") " +
                            " select distinct p.CATEGORY as CATEGORY,a.taxcode,a.taxrate,a.INCLTAX,a.TAXCALTYPE from DCP_TAXCATEGORY a " +
                            " left join DCP_TAXGROUP b on a.eid=b.eid and a.taxcode=b.taxcode " +
                            " left join DCP_TAXGROUP_DETAIL c on a.eid=c.eid and b.taxgroupno=c.taxgroupno " +
                            " inner join p on p.CATEGORY=c.attrid and c.attrtype='1' " +
                            " where a.eid='"+eId+"'";
                    List<Map<String, Object>> categoryTaxList = this.doQueryData(categoryTaxSql, null);

                    for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()) {
                        if(Check.NotNull(lv1.getOutPutTaxCode())){
                            continue;//商品上自带了就不管了
                        }
                        List<Map<String, Object>> pluTaxs = goodsTaxList.stream().filter(x -> x.get("PLUNO").toString().equals(lv1.getPluNo())).collect(Collectors.toList());
                        if(CollUtil.isNotEmpty(pluTaxs)){
                            lv1.setOutPutTaxCode(pluTaxs.get(0).get("TAXCODE").toString());
                            lv1.setOutPutTaxRate(pluTaxs.get(0).get("TAXRATE").toString());
                            lv1.setOutPutInclTax(pluTaxs.get(0).get("INCLTAX").toString());
                            lv1.setOutPutTaxCalType(pluTaxs.get(0).get("TAXCALTYPE").toString());
                        }else{
                            List<Map<String, Object>> categoryTaxs = categoryTaxList.stream().filter(x -> x.get("CATEGORY").toString().equals(lv1.getCategory())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(categoryTaxs)){
                                lv1.setOutPutTaxCode(categoryTaxs.get(0).get("TAXCODE").toString());
                                lv1.setOutPutTaxRate(categoryTaxs.get(0).get("TAXRATE").toString());
                                lv1.setOutPutInclTax(categoryTaxs.get(0).get("INCLTAX").toString());
                                lv1.setOutPutTaxCalType(categoryTaxs.get(0).get("TAXCALTYPE").toString());
                            }
                        }

                    }



                }
                else if("1".equals(req.getRequest().getSearchScope())||"2".equals(req.getRequest().getSearchScope())){
                    if(Check.NotNull(outPutTax)&&"2".equals(taxPayerType)){
                        String taxSql="select * from DCP_TAXCATEGORY a where a.eid='"+eId+"' and a.taxcode='"+outPutTax+"'";
                        List<Map<String, Object>> taxList = this.doQueryData(taxSql, null);

                        for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()) {
                            lv1.setOutPutTaxCode(outPutTax);
                            if(CollUtil.isNotEmpty(taxList)){
                                lv1.setOutPutTaxRate(taxList.get(0).get("TAXRATE").toString());
                                lv1.setOutPutInclTax(taxList.get(0).get("INCLTAX").toString());
                                lv1.setOutPutTaxCalType(taxList.get(0).get("TAXCALTYPE").toString());
                            }
                        }
                    }
                    //||"1".equals(taxPayerType)  取商品的 就不管了
                }

            }

            if("Y".equals(queryStock)){

                String withPlu = "";
                if (pluNos !=null && pluNos.size()>0 ) {

                    Map<String,String> map = new HashMap<>();
                    String sJoinPlu = "";
                    for(String s :pluNos) {
                        sJoinPlu += s +",";
                    }
                    map.put("PLU", sJoinPlu);
                    withPlu = mc.getFormatSourceMultiColWith(map);
                }

                if(Check.NotNull(withPlu)) {
                    sql = " select a.pluno,sum(a.qty) as baseqty,sum(a.qty-a.lockqty-a.onlineqty) as availableqty from dcp_stock a"
                            + " inner join (" + withPlu + ") b on a.pluno=b.plu "
                            + " where a.eid='" + req.geteId() + "' and a.organizationno='" + req.getOrganizationNO() + "'  ";
                    if(Check.NotNull(queryStockWarehouse)){
                        sql+= " and a.WAREHOUSE='"+queryStockWarehouse+"' ";
                    }

                    sql+=" group by a.pluno";
                    List<Map<String, Object>> getStock = this.doQueryData(sql, null);

                    if(CollUtil.isNotEmpty(getStock)){
                        for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()){
                            String pluNo = lv1.getPluNo();
                            List<Map<String, Object>> pluStock = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(lv1.getPluNo())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(pluStock)){
                                lv1.setStockQty(pluStock.get(0).get("BASEQTY").toString());
                                lv1.setAvailableStockQty(pluStock.get(0).get("AVAILABLEQTY").toString());
                            }
                        }
                    }

                }
            }

            {
                //判断当前组织所属法人纳税人性质：
                //1.一般纳税人：商品进项税，入参供应商supplierId不为空，优先级：采购模板商品进项税别>供应商默认税别；入参供应商为空：取商品进项税别DCP_GOODS.INPUTTAXCODE;
                //2.小规模纳税人：取法人进项税别DCP_ORG.INPUT_TAXCODE;
                String taxPayerType="1";
                if(CollUtil.isNotEmpty(orgList)){
                    taxPayerType = orgList.get(0).get("TAXPAYER_TYPE").toString();
                }

                if("2".equals(taxPayerType)){
                    for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()) {
                        lv1.setInputTaxCode(orgList.get(0).get("INPUT_TAXCODE").toString());
                        lv1.setInputTaxRate(orgList.get(0).get("INPUTTAXRATE").toString());
                        if(Check.NotNull(lv1.getInputTaxCode())){
                            List<Map<String, Object>> taxFilterRows = taxCodeList.stream().filter(x -> x.get("TAXCODE").toString().equals(lv1.getInputTaxCode())).collect(Collectors.toList());
                            if(taxFilterRows.size()>0){
                                lv1.setInputTaxName(taxFilterRows.get(0).get("TAXNAME").toString());
                            }
                        }
                    }
                }
                else{
                    if(Check.NotNull(req.getRequest().getSupplierId())){
                        String bizpartnerSql="select a.*,b.taxrate as inputtaxrate,c.taxname as inputtaxname from dcp_bizpartner a " +
                                " left join dcp_taxcategory b on a.eid=b.eid and a.taxcode=b.taxcode " +
                                " left join dcp_taxcategory_lang c on c.eid=a.eid and c.taxcode=a.taxcode and c.lang_type='"+req.getLangType()+"' " +
                                " where a.eid='"+req.geteId()+"' and a.BIZPARTNERNO='"+req.getRequest().getSupplierId()+"' ";
                        List<Map<String, Object>> list = this.doQueryData(bizpartnerSql, null);
                        if(list.size()>0){
                            for (DCP_GoodsOpenQryRes.level1Elm lv1:res.getDatas()) {
                                lv1.setInputTaxCode(list.get(0).get("TAXCODE").toString());
                                lv1.setInputTaxRate(list.get(0).get("INPUTTAXRATE").toString());
                                lv1.setInputTaxName(list.get(0).get("INPUTTAXNAME").toString());
                            }
                        }
                    }

                    //有采购模板的取采购模板的  在前面给值
                }

            }

        }


        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        //} catch (Exception e) {
       //     res.setSuccess(false);
       //     res.setServiceStatus("200");
       //     res.setServiceDescription("服务执行失败!"+e.getMessage());
       // }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_GoodsOpenQryReq req) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String orgForm = req.getOrg_Form();   ////组织类型 0-公司  1-组织  2-门店 3-其它
        String langType = req.getLangType();
        DCP_GoodsOpenQryReq.levelElm request = req.getRequest();
        //查詢條件
        String keyTxt = request.getKeyTxt();
        String virtual = request.getVirtual();
        String billType = request.getBType();//0-要货/铺货 1-采购 2-销售 3-库存 4-生产 5-配方 6-盘点 7-退货 8-销退 9领料  10 调拨
        String[] brand = request.getBrand();
        String[] series = request.getSeries();
        String[] attrGroup = request.getAttrGroup();
        String[] attrValue = request.getAttrValue();
        String customer = request.getCustomer();
        String pluType = request.getPluType();  // 普通商品NORMAL 特征商品FEATURE 套餐商品 PACKAGE
        String[] plu  = request.getPluNo();
        String queryStockWarehouse = request.getQueryStockWarehouse();

        String paraSupplierType = request.getSupplierType();
        String paraSupplierId = request.getSupplierId();

        String sysDate =	new SimpleDateFormat("yyyyMMdd").format(new Date());

        String taxPayerType="1";//商品对应税种
        String orgNo=req.getOrganizationNO();
        if(Check.NotNull(req.getRequest().getOrgNo())){
            orgNo=req.getRequest().getOrgNo();
        }

        String stockTakeType = request.getStockTakeType();
        String stockTakeBDate = request.getStockTakebDate();//yyyymmdd 格式
        String innerStock="";
        //List<String> differPluNos=new ArrayList<>();//异动商品 后面贴plu上去
        if(Check.Null(stockTakeType)||"0".equals(stockTakeType)){
            //即当前组织可用商品（取数源：商品模板）
        }
        else if("1".equals(stockTakeType)){
            if(Check.Null(stockTakeBDate)||sysDate.equals(stockTakeBDate)){
                innerStock=" inner join dcp_stock ds on ds.eid=a.eid and a.pluno=ds.pluno and ds.warehouse='"+queryStockWarehouse+"' ";
            }
            else {
                innerStock=" inner join dcp_stock ds on ds.eid=a.eid and a.pluno=ds.pluno and ds.warehouse='"+queryStockWarehouse+"' and to_char(ds.createtime,'yyyyMMdd')<='"+stockTakeBDate+"' ";
            }
        }
        else if("2".equals(stockTakeType)){
            if(Check.Null(stockTakeBDate)||sysDate.equals(stockTakeBDate)){
                innerStock=" inner join dcp_stock ds on ds.eid=a.eid and a.pluno=ds.pluno and ds.qty<>0 and ds.warehouse='"+queryStockWarehouse+"' ";
            }else{
                //算期末库存不等于0 的数据
                String differSql="select a.pluno,a.qty-nvl(b.qty,0)+nvl(c.qty,0) as differqty from ( " +
                        "select a.pluno, sum(a.qty) as qty from dcp_stock a where a.eid='"+eId+"' and   to_char(a.createtime,'yyyyMMdd')<='"+stockTakeBDate+"' and a.warehouse='"+queryStockWarehouse+"' " +
                        "group by a.pluno ) a left join ( " +
                        "select  a.pluno,sum(a.baseqty*a.STOCKTYPE) as qty from DCP_STOCK_DETAIL_STATIC a  where a.eid='"+eId+"' and   to_char(a.ACCOUNTDATE,'yyyyMMdd')<='"+stockTakeBDate+"' and a.warehouse='"+queryStockWarehouse+"' " +
                        "group by a.pluno) b on a.pluno=b.pluno " +
                        "left join ( " +
                        "select  a.pluno,sum(a.baseqty*a.STOCKTYPE) as qty from DCP_STOCK_DETAIL a  where a.eid='"+eId+"' and   to_char(a.ACCOUNTDATE,'yyyyMMdd')<='"+stockTakeBDate+"' and a.warehouse='"+queryStockWarehouse+"' " +
                        "group by a.pluno) c on a.pluno=c.pluno ";
                innerStock=" inner join ("+differSql+") ds on ds.pluno=a.pluno and ds.differqty<>0 ";
            }
        }
        else if("3".equals(stockTakeType)){
            //仅有异动商品：（备注默认近30天内有库存异动商品，不包含盘点日期当天
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            if(Check.Null(stockTakeBDate)){
                stockTakeBDate=sDate;
            }
            sDate = PosPub.GetStringDate(stockTakeBDate, -30);
            //获取库存不为零
            //String sql1 = " select pluno from dcp_stock "
             //       + " where eid='"+eId+"' and organizationno='"+shopId+"'  and qty<>0 ";
            //List<Map<String, Object>> getQDataPlu1 = this.doQueryData(sql1, null);

            //获取异动指定日期之后
            String sql1 = " select b.pluno from ("
                    + " select a.pluno,max(b.confirm_date||b.confirm_time) as confirmdate from dcp_stocktake_detail a"
                    + " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"'  and b.status='2' and b.bdate>='"+sDate+"' and a.bdate<'"+stockTakeBDate+"'"
                    + " group by a.pluno"
                    + " )a"
                    + " inner join dcp_stock b on b.eid='"+eId+"' and b.organizationno='"+shopId+"' and a.pluno=b.pluno"
                    + " where nvl(b.lastmoditime,b.createtime)>to_date(a.confirmdate,'yyyymmddhh24miss') and b.qty='0' "
                    + " ";
            //List<Map<String, Object>> getQDataPlu2 = this.doQueryData(sql1, null);
            //for(Map<String, Object> map : getQDataPlu2){
            //    if(!differPluNos.contains(map.get("PLUNO").toString())){
            //        differPluNos.add(map.get("PLUNO").toString());
            //   }
            //}
            innerStock=" inner join ("+sql1+") ds on ds.pluno=a.pluno ";
        }

        //采购模板的商品范围
        //入参bType="1.采购"，searchScope="1.当前组织可用商品"或者"2指定组织可用商品
        List<String> cgPlunos=new ArrayList();//采购的商品
        if("1".equals(billType)&&("1".equals(request.getSearchScope())||"2".equals(request.getSearchScope()))){
            String isPurTemplateRequire = request.getIsPurTemplateRequire();
            //-isPurTemplateRequire=Y或null时，返回存在采购模板商品明细（null值默认必须按模板采购）--供应商编号非必传
            //-isPurTemplateRequire=N时，优先取供应商采购模板商品，供应商无采购模板则返回商品模板中【供货类型supplierType】<>"FACTORY统配"且【允许采购canPurchase】=Y且状态有效的商品--供应商编号必传

            String ptSql="select c.pluno " +
                    " from DCP_PURCHASETEMPLATE a " +
                    " inner join DCP_PURCHASETEMPLATE_ORG b on a.eid=b.eid and a.purtemplateno=b.purtemplateno and b.status='100' " +
                    " inner join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.status='100' " +
                    " inner join dcp_bizpartner d on a.eid=d.eid and a.SUPPLIERNO=d.bizpartnerno " +
                    " where a.eid='"+eId+"' and a.status='100' ";

            ptSql+=   " and to_char(d.begindate,'yyyyMMdd')<='"+sysDate+"' " +
                    " and to_char(d.enddate,'yyyyMMdd')>='"+sysDate+"' ";

            if(Check.NotNull(request.getOrgNo())){
                ptSql+=" and b.organizationno='"+request.getOrgNo()+"' ";
            }else{
                ptSql+=" and b.organizationno='"+req.getOrganizationNO()+"' ";
            }
            if(Check.NotNull(paraSupplierId)){
                ptSql+=" and a.SUPPLIERNO='"+paraSupplierId+"' ";
            }
            if("Y".equals(isPurTemplateRequire)||Check.Null(isPurTemplateRequire)){

            }else if("N".equals(isPurTemplateRequire)){
                if(Check.Null(paraSupplierId)){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "supplierId不可为空".toString());
                }
                //加上商品模板的商品
                ptSql+="union all " +
                        " SELECT c.pluno FROM DCP_GOODSTEMPLATE a " +
                        " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                        " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                        " WHERE a.EID = '"+eId+"' AND ((b.RANGETYPE = 2 and b.ID = '"+req.getShopId()+"')" +
                        " or (b.RANGETYPE = 1  AND b.ID = '"+req.getBELFIRM()+"') ) AND a.STATUS = 100 " +
                        " and c.SUPPLIERTYPE!='FACTORY' and C.CANPURCHASE='Y' and C.status='100' ";

            }
            List<Map<String, Object>> getPtDataList = this.doQueryData(ptSql, null);
            cgPlunos=getPtDataList.stream().map(x->x.get("PLUNO").toString()).distinct().collect(Collectors.toList());
        }
        if(cgPlunos.size()<=0){
            cgPlunos.add("++++++++++++");
        }
        String withCgPlu="";
        MyCommon mc = new MyCommon();
        if (cgPlunos !=null && cgPlunos.size()>0 ) {

            Map<String,String> map = new HashMap<>();
            String sJoinPlu = "";
            for(String s :cgPlunos) {
                sJoinPlu += s +",";
            }
            map.put("PLU", sJoinPlu);
            withCgPlu = mc.getFormatSourceMultiColWith(map);
        }

        String withPlu = "";
        if (plu !=null && plu.length>0 ) {

            Map<String,String> map = new HashMap<>();
            String sJoinPlu = "";
            for(String s :plu) {
                sJoinPlu += s +",";
            }
            map.put("PLU", sJoinPlu);
            withPlu = mc.getFormatSourceMultiColWith(map);
        }

        String[] category = request.getCategory();
        String withCategory = "";
        if (category !=null && category.length>0 ) {
            //MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinCategory = "";
            for(String s :category) {
                sJoinCategory += s+",";
            }
            map.put("CATEGORY", sJoinCategory);
            withCategory = mc.getFormatSourceMultiColWith(map);
        }

        String[] pluBarcode = request.getPluBarcode();  //["pluBarcode1","pluBarcode2"]
        String withPluBarcode = "";
        if (pluBarcode !=null && pluBarcode.length>0 ) {
            //MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluBarcode = "";
            for(String s :pluBarcode) {
                sJoinPluBarcode += s+",";
            }
            map.put("PLUBARCODE", sJoinPluBarcode);
            withPluBarcode = mc.getFormatSourceMultiColWith(map);
        }

        String templateNo = request.getTemplateNo();
        String templateType = request.getTemplateType();
        String brands = getString(brand);
        String seriess = getString(series);
        String attrGroups = getString(attrGroup);
        String attrValues = getString(attrValue);
        String sourceType = request.getSourceType();

        //searchScope：0、全部 1、模板商品
        String searchScope = request.getSearchScope();
        if (Check.Null(searchScope)){
            searchScope="0";
        }


        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //分页起始位置
        int startRow = (pageNumber - 1) * pageSize;
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");

        String unit = "a.baseunit";
        String unitUse="";
        if (!Check.Null(billType)){
            switch (billType) {
                case "0":
                    unit="a.punit";
                    unitUse = " and goodsunit.punit_use='Y' ";
                    break;
                case "7":
                    unit="nvl(a.runit,a.wunit) ";
                    unitUse = " ";
                    break;
                case "2"://销售
                    unit="a.sunit";
                    //unitUse = " and goodsunit.prod_unit_use='Y' ";
                    break;
                case "5":
                    unit="a.bom_unit";
                    break;
                case "4":
                    unit="a.prod_unit";
                    //unitUse = " and goodsunit.prod_unit_use='Y' ";
                    break;
                case "3":
                    unit="a.purunit";
                    unitUse = " and goodsunit.purunit_use='Y' ";
                    break;
                case "9":
                    unit="a.pickunit";
                case "12":
                    unit="a.cunit";
                    unitUse = " and goodsunit.cunit_use='Y' ";
                    break;
                default:
                    break;
            }
            if (billType.equals("0") && !Check.Null(templateNo)){
                unit="a.punit";
                unitUse = " ";
            }

        } else {
            billType="-1"; // 单据类型为空得时候，塞个默认值
        }

        //公司模板 + 门店模板
        /**
        String tempSql=" select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID FROM DCP_GOODSTEMPLATE a " +
                " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getShopId()+"' AND a.STATUS = 100 " +
                " AND b.RANGETYPE = 2  " +
                " order by a.CREATETIME desc " +
                " ) " +
                " union all" +
                " select * from ( SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno,c.SUPPLIERTYPE,c.SUPPLIERID FROM DCP_GOODSTEMPLATE a " +
                " JOIN DCP_GOODSTEMPLATE_RANGE b on a.EID = b.EID and a.TEMPLATEID = b.TEMPLATEID " +
                " inner join dcp_goodstemplate_goods c on c.eid=a.eid and c.TEMPLATEID=a.TEMPLATEID and c.status='100'" +
                " WHERE a.EID = '"+eId+"' AND b.ID = '"+req.getBELFIRM()+"' AND a.STATUS = 100 " +
                " AND b.RANGETYPE = 1  " +
                " order by a.CREATETIME desc " +
                " ) " +
                "";
         **/

        String tempSql="SELECT TEMPLATEID, TEMPLATETYPE,pluno,SUPPLIERTYPE,SUPPLIERID" +
                " FROM (" +
                "    SELECT a.TEMPLATEID,a.TEMPLATETYPE,c.pluno," +
                "        c.SUPPLIERTYPE,c.SUPPLIERID," +
                "        b.RANGETYPE,ROW_NUMBER() OVER (" +
                "            PARTITION BY c.pluno " +
                "            ORDER BY " +
                "                CASE WHEN b.RANGETYPE = 2 THEN 0 ELSE 1 END," +
                "                a.CREATETIME DESC" +
                "        ) AS rn" +
                "    FROM " +
                "        DCP_GOODSTEMPLATE a  " +
                "        JOIN DCP_GOODSTEMPLATE_RANGE b " +
                "            ON a.EID = b.EID AND a.TEMPLATEID = b.TEMPLATEID  " +
                "        INNER JOIN dcp_goodstemplate_goods c " +
                "            ON c.eid = a.eid AND c.TEMPLATEID = a.TEMPLATEID AND c.status = '100' " +
                "    WHERE " +
                "        a.EID = '99' " +
                "        AND b.ID IN ('"+req.getShopId()+"', '"+req.getBELFIRM()+"') " +
                "        AND a.STATUS = 100  " +
                "        AND b.RANGETYPE IN (1, 2)" +
                " )" +
                " WHERE rn = 1 " +
                " ORDER BY pluno ";



        String isAddGoods="N";//要货会用到
        Boolean isInnerPtemplate=true;
        if("0".equals(billType)){

            if(Check.NotNull(templateNo)){
                String pSql="select * from dcp_ptemplate a where a.eid='"+eId+"' and a.PTEMPLATENO='"+templateNo+"' ";
                List<Map<String, Object>> pList = this.doQueryData(pSql, null);
                if(pList.size() >0){
                    isAddGoods=pList.get(0).get("ISADDGOODS").toString();
                }

                //1️⃣有模板templateNo&【允许新增商品ISADDGOODS=N】	要货模板+商品模板（交集）	可要货=Y	供货价	模板要货单位
                //2️⃣有模板templateNo&【允许新增商品ISADDGOODS=Y】	要货模板+商品模板（并集）	可要货=Y	供货价	模板要货单位（要货模板）


            }else{
                //后面加条件

                //无模板templateNo为空	商品模板（匹配supplierId)
                //✅商品模板（公司+机构）	可要货=Y	供货价distriPrice	标准要货单位
                //DCP_GOODS.PUNIT
                //1️⃣无模板templateNo&供货方式supplierType=“FACTORY”【统配】	商品模板：
                //1.商品模板：supplierId=指定供货组织supplierId
                //2.商品模板：supplierType&supplierId为空（自有商品）--允许要货/采购
                //3.商品模板：supplierType=“PRODUCTION”(自制）	可要货=Y	供货价	标准要货单位（商品模板）
                //2️⃣无模板templateNo&供货方式supplierType=“SUPPLIER”【采购】（统采）	商品模板+采购模板（交集）
                //1.商品模板：供货类型supplierType&supplierId为空（自有商品）-->允许要货/采购；
                //2.商品模板：供货类型supplierType="SUPPLIER"(采购）

                //第三种也是要存在商品模板里面的
                //3.采购模板（统采）：采购类型非“自订货”+[采购中心PURCENTER]=入参supplierld(供货组织)
                // +要货组织存在模板收货组织范围	可要货=Y	供货价	标准要货单位（商品模板）

            }
        }

        sqlbuf.append(" with goodstemplate as (select * from ("+tempSql+") )");


        if("Y".equals(isAddGoods)){
            isInnerPtemplate=false;
        }

        if(Check.NotNull(withCgPlu)){
            sqlbuf.append(" ,cgplu as (" + withCgPlu + ")");
        }
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" ,plu as (" + withPlu + ")");
        }
        if (!Check.Null(withCategory)) {
            sqlbuf.append(" ,category as (" + withCategory + ")");
        }
        if (!Check.Null(withPluBarcode)) {
            sqlbuf.append(" ,barcode as (" + withPluBarcode + ")");
        }

        sqlbuf.append("SELECT * FROM ( " +
                " SELECT COUNT(DISTINCT PLUNO) OVER () AS num, DENSE_RANK() OVER (ORDER BY PLUNO) AS rn , a.* FROM ( " +
                " SELECT distinct a.PLUNO,a.status,b.PLU_NAME as pluname,a.MAINBARCODE as barcode,a.category," +
                " dcl.CATEGORY_NAME as categoryname,a.spec,a.wunit,a.baseunit,a.punit,wunit.uname as wunitname,baseunit.uname as baseunitname,punit.uname as punitname,a.sunit,sunit.uname as sunitname,a.ISBATCH,a.SHELFLIFE," +
                " a.VIRTUAL,a.PLUTYPE,a.STOCKMANAGETYPE,a.PRODUCER,a.MANUFACTURER,"+unit+"  as unit,unit.uname as unitname,a.price,a.purprice,gt.supplierid,gt.suppliertype,case when gt.suppliertype='FACTORY' THEN sd.org_name ELSE SD1.SNAME END as SUPPLIER_NAME," +
                " baseunit1.udlength as baseUnitUdLength,baseunit1.roundtype as baseUnitRoundType,unit1.udlength as unitUdLength,unit1.roundtype as unitRoundType," +
                " a.taxcode,tax.TAXRATE,a.inputtaxcode,tax1.taxrate as inputtaxrate,tax.TAXCALTYPE,tax.INCLTAX,'' as PRODMULQTY,'' as PRODMINQTY,'' as REMAINTYPE,a.sourcetype,a.isQualityCheck,gg.safeqty,gg.WARNINGQTY,a.pickminqty,a.pickmulqty,a.supplier as goodsSupplier,image.listImage,a.minqty,a.mulqty,a.maxqty    " +
                " FROM DCP_GOODS a " +
                " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PLUNO = b.PLUNO AND b.LANG_TYPE = '"+langType+"'  " +
                " left join DCP_CATEGORY_LANG dcl on dcl.category=a.category and dcl.eid=a.eid and dcl.lang_type='"+langType+"' "+
                "");

        sqlbuf.append( " left join DCP_UNIT_LANG wunit on wunit.eid=a.eid and wunit.unit=a.wunit and wunit.lang_type='"+langType+"'"+
                " left join DCP_UNIT_LANG baseunit on baseunit.eid=a.eid and baseunit.unit=a.baseunit and baseunit.lang_type='"+langType+"'"+
                " left join DCP_UNIT baseunit1 on baseunit1.eid=a.eid and baseunit1.unit=a.baseunit "+
                " left join DCP_UNIT_LANG punit on punit.eid=a.eid and punit.unit=a.punit and punit.lang_type='"+langType+"'"+
                " left join DCP_UNIT_LANG sunit on sunit.eid=a.eid and sunit.unit=a.sunit and sunit.lang_type='"+langType+"'"+
                " left join DCP_UNIT_LANG unit on unit.eid=a.eid and unit.unit="+unit+" and unit.lang_type='"+langType+"' " +
                " left join DCP_UNIT unit1 on unit1.eid=a.eid and unit1.unit="+unit+"  " +
                " left join DCP_TAXCATEGORY tax on tax.eid=a.eid and tax.taxcode=a.taxcode "+
                " left join DCP_TAXCATEGORY tax1 on tax1.eid=a.eid and tax1.taxcode=a.inputtaxcode "
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL' "
        );



        if("4".equals(billType)&&("1".equals(searchScope)||"2".equals(searchScope))){
            //要存在生管模板里面 这个不要了
            //sqlbuf.append(" inner join ( " +
           //         " select distinct b.pluno " +
           //         " from DCP_PRODTEMPLATE a " +
          //          " inner join DCP_PRODTEMPLATE_GOODS b on a.eid=b.eid and a.templateid=b.templateid " +
           //         " left join dcp_prodtemplate_range c on a.eid=c.eid and a.templateid=c.templateid " +
           //         " left join dcp_unit_lang e on e.eid=a.eid and e.unit=b.produnit and e.lang_type='"+req.getLangType()+"'");
            //if(Check.NotNull(withPlu)) {
             //   sqlbuf.append(" inner join plu on plu.plu=b.pluno ");
          //  }
          //  sqlbuf.append(" where (a.RESTRICTORG='0' or (a.RESTRICTORG='1' and c.organizationno='" + orgNo + "')) and a.status='100' and b.status='100'" +
          //          " ) pd on pd.pluno=a.pluno ");

            sqlbuf.append(" inner join ( " +
                     " select distinct a.pluno " +
                     " from dcp_bom a "
                     );
            if(Check.NotNull(withPlu)) {
               sqlbuf.append(" inner join plu on plu.plu=b.pluno ");
              }
              sqlbuf.append(" where  a.status='100' " +
                      " ) pd on pd.pluno=a.pluno ");


        }


        if (!Check.Null(withPluBarcode)) {
            sqlbuf.append(" "
                    + " inner join ("
                    + " select a.pluno from dcp_goods_barcode a"
                    + " inner join barcode b on a.plubarcode=b.plubarcode"
                    + " where a.eid='"+eId+"' and a.status='100' group by a.pluno"
                    + " ) goodsbarcode on goodsbarcode.pluno=a.pluno"
                    + " ");
        }
        if (!Check.Null(withPlu)) {
            sqlbuf.append(" inner join plu on plu.plu=a.pluno ");
        }

        if (!Check.Null(withCategory)) {
            sqlbuf.append(" inner join category on category.category=a.category ");
        }


        if("1".equals(billType)&&("1".equals(request.getSearchScope())||"2".equals(request.getSearchScope()))){
            sqlbuf.append(" inner join cgplu cp on cp.plu=a.pluno ");//采购模板
            sqlbuf.append(" left  join goodstemplate gt on gt.pluno=a.pluno ");//商品模板
        }else {
            if (searchScope.equals("1")) {
                sqlbuf.append(" inner join goodstemplate gt on gt.pluno=a.pluno ");
            } else {
                sqlbuf.append(" left  join goodstemplate gt on gt.pluno=a.pluno ");
            }
        }
        sqlbuf.append(" left join DCP_GOODSTEMPLATE_GOODS gg on gg.eid=a.eid and gg.templateid=gt.templateid and gg.pluno=gt.pluno ");

        sqlbuf.append(" left join dcp_org_lang sd on sd.eid=a.eid and  sd.organizationno=gt.SUPPLIERid and sd.lang_type='"+langType+"' " );
        sqlbuf.append(" left join DCP_BIZPARTNER sd1 on  sd1.eid=a.eid and sd1.BIZPARTNERNO=gt.SUPPLIERid and  sd1.biztype in ('1','3') " );


        if (!Check.Null(attrValues)) {
            sqlbuf.append(" inner join ("
                    + " select eid,pluno from dcp_goods_attr_value"
                    + " where attrvalueid in ("+attrValues+") group by eid,pluno "
                    + " ) gav on gav.eid=a.eid and gav.pluno=a.pluno");
        }
        if(Check.NotNull(innerStock)){
            sqlbuf.append(innerStock);
        }

        //0.要货模板 1.盘点模板 2.生产模板 3.采购模板 5调拨模板
        if (!Check.Null(templateNo) && !Check.Null(templateType) && !billType.equals("3")) {
            if("0".equals(billType)&&!isInnerPtemplate){
            }else {
                sqlbuf.append(" "
                        + " inner join dcp_ptemplate p1 on p1.eid=a.eid and p1.ptemplateno='" + templateNo + "' and p1.doc_type='" + templateType + "' "
                        + " inner join dcp_ptemplate_detail p2 on p2.eid=p1.eid and p2.ptemplateno=p1.ptemplateno and p2.doc_type=p1.doc_type "
                        + " and (p2.pluno=a.pluno or (p2.pluno=a.category and p1.doc_type='1' and p1.rangeway='1')) "
                );
            }
        }

        sqlbuf.append(" WHERE a.EID = '"+eId+"'");

        sqlbuf.append(" AND a.status = '100' ");
        if(Check.NotNull(sourceType)){
            sqlbuf.append(" AND a.sourcetype = '"+sourceType+"'");
        }

        //if(!Check.Null(templateNo)){
        //    sqlbuf.append(" and gt.templateid='"+templateNo+"'");
        //}
        //if(!Check.Null(templateType)){
        //    sqlbuf.append(" and gt.templatetype='"+templateType+"'");
        //}

        if(!Check.Null(keyTxt)){
            String upperKeyTxt = keyTxt.toUpperCase();
            sqlbuf.append(" AND (upper(b.pluno) LIKE '%%"+upperKeyTxt+"%%' " +
                    " or b.PLU_NAME LIKE '%%"+keyTxt+"%%' " +
                    ")");
        }
        if(!Check.Null(pluType)){
            sqlbuf.append(" AND a.plutype = '"+pluType+"'");
        }

        if(!Check.Null(virtual)){
            sqlbuf.append(" AND a.virtual = '"+virtual+"'");
        }

        if (!Check.Null(brands))
            sqlbuf.append(" and a.brand in ("+brands+")");
        if (!Check.Null(seriess))
            sqlbuf.append(" and a.series in ("+seriess+")");
        if (!Check.Null(attrGroups))
            sqlbuf.append(" and a.attrGroupid in ("+attrGroups+")");
        if(!Check.Null(pluType))
            sqlbuf.append(" and a.plutype = '"+pluType+"'");

        if("1".equals(billType)&&("1".equals(request.getSearchScope())||"2".equals(request.getSearchScope()))){
            //这边取采购模板的商品+上屏模板可采购的商品 上面供应商是采购模板的供应商 不是商品模板的供应商

        }
        else if("0".equals(billType)){
            //会在下面  不匹配 supplierid  和 supplierType
        }
        else {
            if (Check.NotNull(paraSupplierType)) {
                sqlbuf.append(" and gt.supplierType='" + paraSupplierType + "'");
            }
            if (Check.NotNull(paraSupplierId)) {
                sqlbuf.append(" and gt.supplierId='" + paraSupplierId + "'");
            }
        }

        switch (searchScope){
            case "0":    //0、全部
                break;
            case "1":    //1、总部和当前自建门店
                sqlbuf.append(" and (a.selfbuiltshopid is null)");
                break;
            case "2":    //2、仅总部
                sqlbuf.append(" and a.selfbuiltshopid is null");
                break;
            case "3":    //3、全部自建门店
                sqlbuf.append(" and a.selfbuiltshopid is not null");
                break;
        }



        if (billType.equals("0") && Check.Null(templateNo) &&("1".equals(searchScope)||"2".equals(searchScope))) {  //0-要货类


            if("FACTORY".equals(paraSupplierType)){
                sqlbuf.append(" and (gt.supplierid='"+paraSupplierId+"' or ((gt.suppliertype='' or gt.suppliertype is null)" +
                        " and ( gt.supplierid='' or  gt.supplierid is null) ) or gt.suppliertype='PRODUCTION')");
                sqlbuf.append(" and gg.canrequire='Y' ");
                //                //1️⃣无模板templateNo&供货方式supplierType=“FACTORY”【统配】	商品模板：
                //                //1.商品模板：supplierId=指定供货组织supplierId
                //                //2.商品模板：supplierType&supplierId为空（自有商品）--允许要货/采购
                //                //3.商品模板：supplierType=“PRODUCTION”(自制）	可要货=Y	供货价	标准要货单位（商品模板）


            }
            else if("SUPPLIER".equals(paraSupplierType)){
                sqlbuf.append(" and (gt.suppliertype='SUPPLIER' or (gt.suppliertype='' and gt.supplierid='' )" +
                        "or ( exists (" +
                        "select c.pluno " +
                        "                     from DCP_PURCHASETEMPLATE a " +
                        "                     inner join DCP_PURCHASETEMPLATE_ORG b on a.eid=b.eid and a.purtemplateno=b.purtemplateno and b.status='100' " +
                        "                     inner join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.status='100' " +
                        "                     inner join dcp_bizpartner d on a.eid=d.eid and a.SUPPLIERNO=d.bizpartnerno " +
                        "                     where a.eid='99' and a.status='100' and a.purtype!='0' and a.purcenter='"+paraSupplierId+"' and b.organizationno ='"+orgNo+"' and c.pluno=a.pluno" +
                        "))" +
                        "" +
                        ")");
                //                //2️⃣无模板templateNo&供货方式supplierType=“SUPPLIER”【采购】（统采）	商品模板+采购模板（交集）
                //                //1.商品模板：供货类型supplierType&supplierId为空（自有商品）-->允许要货/采购；
                //                //2.商品模板：供货类型supplierType="SUPPLIER"(采购）

                //3.采购模板（统采）：采购类型非“自订货”+[采购中心PURCENTER]=入参supplierld(供货组织)
                // +要货组织存在模板收货组织范围	可要货=Y	供货价	标准要货单位（商品模板）

            }
            sqlbuf.append(" and gg.canrequire='Y' ");

            // //无模板templateNo为空	商品模板（匹配supplierId)
            //                //✅商品模板（公司+机构）	可要货=Y	供货价distriPrice	标准要货单位
            //                //DCP_GOODS.PUNIT


        }

        if (billType.equals("6")&&searchScope.equals("1")) // 6-计划报单
            sqlbuf.append(" and gg.cansale='Y' ");

        if (billType.equals("7")&&searchScope.equals("1")) // 7-可退仓
            sqlbuf.append(" and gg.canrequireback='Y' ");

        if (billType.equals("14")) // 14调拨出库单
            sqlbuf.append(" and b.isallot='Y' ");

        if (billType.equals("10")&&searchScope.equals("1")) // 10销售单
            sqlbuf.append(" and gg.cansale='Y' ");


        sqlbuf.append("  ) a ) WHERE RN > " + startRow + " AND rn <= " + (startRow + pageSize) + " ORDER BY pluno DESC ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getGoodsUnitSql(DCP_GoodsOpenQryReq req,List<String> plunos ) throws Exception{
        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();

        String withPluNo = "";
        if (plunos !=null && plunos.size()>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluNo = "";
            for(String s :plunos) {
                sJoinPluNo += s+",";
            }
            map.put("PLUNO", sJoinPluNo);
            withPluNo = mc.getFormatSourceMultiColWith(map);
        }
        StringBuffer sqlbuf = new StringBuffer("");

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" with plu as (" + withPluNo + ")");
        }

        sqlbuf.append(" " +
                "  " +
                " SELECT a.PLUNO,dgu.ounit,ounit.uname as ounitname,dgu.sunit_use,dgu.punit_use,dgu.bom_unit_use,dgu.prod_unit_use,dgu.purunit_use,dgu.cunit_use,dgu.BOM_UNIT_USE,dgu.UNITRATIO " +
                " FROM DCP_GOODS a " +
                " LEFT JOIN DCP_GOODS_LANG b ON a.EID = b.EID AND a.PLUNO = b.PLUNO AND b.LANG_TYPE = '"+langType+"'  " +
                " left join DCP_GOODS_UNIT dgu on dgu.eid=a.eid and dgu.pluno=a.pluno "+
                " left join DCP_UNIT_LANG ounit on ounit.eid=dgu.eid and ounit.unit=dgu.ounit and ounit.lang_type='"+langType+"'"+
                " ");

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" inner join plu on a.pluno=plu.pluno ");
        }

        sqlbuf.append(   " WHERE a.EID = '"+eid+"'  " );

        sqlbuf.append(" AND a.status = '100'");
        if(!Check.Null(keyTxt)){
            sqlbuf.append(" AND (b.pluno LIKE '%%"+keyTxt+"%%' " +
                    " or b.PLU_NAME LIKE '%%"+keyTxt+"%%' " +
                    ")");
        }


        return sqlbuf.toString();
    }

    private String getFeatureSql(DCP_GoodsOpenQryReq req,List<String> plunos ) throws Exception{
        String langType = req.getLangType();
        String eid = req.geteId();
        String keyTxt = req.getRequest().getKeyTxt();
        String withPluNo = "";
        if (plunos !=null && plunos.size()>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluNo = "";
            for(String s :plunos) {
                sJoinPluNo += s+",";
            }
            map.put("PLUNO", sJoinPluNo);
            withPluNo = mc.getFormatSourceMultiColWith(map);
        }
        StringBuffer sqlbuf = new StringBuffer("");

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" with plu as (" + withPluNo + ")");
        }

        sqlbuf.append(" " +
                "  " +
                " SELECT a.PLUNO,fl.featureno,fl.FEATURENAME " +
                " FROM DCP_GOODS a " +
                " LEFT JOIN DCP_GOODS_FEATURE b ON a.EID = b.EID AND a.PLUNO = b.PLUNO   " +
                " left join DCP_GOODS_FEATURE_lang fl on fl.eid=a.eid and fl.pluno=a.pluno and b.featureno=fl.featureno AND fl.LANG_TYPE = '"+langType+"'"+
                " ");

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" inner join plu on a.pluno=plu.pluno ");
        }

        sqlbuf.append(   " WHERE a.EID = '"+eid+"'  " );

        sqlbuf.append(" AND a.status = '100'");

        return sqlbuf.toString();
    }

    private String getProdSql(DCP_GoodsOpenQryReq req,List<String> plunos) throws Exception{
        String withPluNo = "";
        if (plunos !=null && plunos.size()>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluNo = "";
            for(String s :plunos) {
                sJoinPluNo += s+",";
            }
            map.put("PLUNO", sJoinPluNo);
            withPluNo = mc.getFormatSourceMultiColWith(map);
        }
        StringBuffer sqlbuf = new StringBuffer("");

        String orgNo=req.getOrganizationNO();
        if(Check.NotNull(req.getRequest().getOrgNo())){
            orgNo=req.getRequest().getOrgNo();
        }

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" with plu as (" + withPluNo + ")");
        }
        sqlbuf.append(" " +
                " select b.eid,b.pluno,b.PRODUNIT,b.PRODMULQTY,b.PRODMINQTY,b.REMAINTYPE,e.uname as produnitname ," +
                " b.oddValue,b.productExceed,b.procRate,b.dispType,b.semiWoType,b.semiWoDeptType,b.fixPreDays " +
                " from DCP_PRODTEMPLATE a " +
                " inner join DCP_PRODTEMPLATE_GOODS b on a.eid=b.eid and a.templateid=b.templateid " +
                " left join dcp_prodtemplate_range c on a.eid=c.eid and a.templateid=c.templateid " +
                " left join dcp_unit_lang e on e.eid=a.eid and e.unit=b.produnit and e.lang_type='"+req.getLangType()+"'");
        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" inner join plu on plu.PLUNO=b.pluno ");
        }
        sqlbuf.append(" where (a.RESTRICTORG='0' or (a.RESTRICTORG='1' and c.organizationno='" + orgNo + "')) and a.status='100' and b.status='100'" +
                " order by a.RESTRICTORG desc ");

        return sqlbuf.toString();
    }

    private String getBomSql(DCP_GoodsOpenQryReq req,List<String> plunos) throws Exception{
        String withPluNo = "";

        if(plunos.size()<=0){
            plunos.add("###");
        }

        if (plunos !=null && plunos.size()>0 ) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluNo = "";
            for(String s :plunos) {
                sJoinPluNo += s+",";
            }
            map.put("PLUNO", sJoinPluNo);
            withPluNo = mc.getFormatSourceMultiColWith(map);
        }
        StringBuffer sqlbuf = new StringBuffer("");

        if (!Check.Null(withPluNo)) {
            sqlbuf.append(" with plu as (" + withPluNo + ")");
        }

        if(Check.Null(req.getRequest().getSearchScope())||"0".equals(req.getRequest().getSearchScope())) {

            sqlbuf.append("" +
                    " select a.*,c.uname as unitname from dcp_bom a " +
                    " inner join plu on plu.pluno=a.pluno " +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.unit and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='" + req.geteId() + "' and a.status='100' and a.RESTRICTSHOP='0' ");

        }else{
            sqlbuf.append("" +
                    " select a.*,c.uname as unitname from dcp_bom a " +
                    " inner join plu on plu.pluno=a.pluno " +
                    " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno" +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.unit and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='" + req.geteId() + "' and a.status='100' and a.RESTRICTSHOP='1' and b.organizationno='"+req.getOrganizationNO()+"' "+
                    " union all " +
                    " select a.*,c.uname as unitname from dcp_bom a " +
                    " inner join plu on plu.pluno=a.pluno " +
                    " left join dcp_unit_lang c on c.eid=a.eid and c.unit=a.unit and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='" + req.geteId() + "' and a.status='100' and a.RESTRICTSHOP='0' ");
        }

        return sqlbuf.toString();
    }

    private String getString(String[] str) {
        StringBuffer str2 = new StringBuffer();
        if (str!=null && str.length>0) {
            for (String s:str) {
                str2.append("'").append(s).append("'").append(",");
            }
            if (str2.length()>0) {
                str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
            }
        }
        return str2.toString();
    }

}
