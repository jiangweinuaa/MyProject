package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_SupplierGoodsOpenQryReq;
import com.dsc.spos.json.cust.res.DCP_SupplierGoodsOpenQryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_SupplierGoodsOpenQry extends SPosBasicService<DCP_SupplierGoodsOpenQryReq, DCP_SupplierGoodsOpenQryRes> {

    @Override
    protected boolean isVerifyFail(DCP_SupplierGoodsOpenQryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();


        if (req.getRequest() == null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        } else {
            //分页查询的服务，必须给值，不能为0
            int pageNumber = req.getPageNumber();
            int pageSize = req.getPageSize();
            if (pageNumber == 0) {
                isFail = true;
                errMsg.append("分页查询pageNumber不能为0,");
            }
            if (pageSize == 0) {
                isFail = true;
                errMsg.append("分页查询pageSize不能为0,");
            }

        }


        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_SupplierGoodsOpenQryReq> getRequestType() {
        return new TypeToken<DCP_SupplierGoodsOpenQryReq>(){};
    }

    @Override
    protected DCP_SupplierGoodsOpenQryRes getResponseType() {
        return new DCP_SupplierGoodsOpenQryRes();
    }

    @Override
    protected DCP_SupplierGoodsOpenQryRes processJson(DCP_SupplierGoodsOpenQryReq req) throws Exception {
        DCP_SupplierGoodsOpenQryRes res = this.getResponse();
        int totalRecords;		//总笔数
        int totalPages;
        //查询
        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        res.setDatas(new ArrayList<>());

        if(getQData!=null&&getQData.size()>0){

            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);
            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;


            String queryStockqty = req.getRequest().getQueryStockqty();
            List plunoDis=new ArrayList();
            List templateNos=new ArrayList();
            StringBuffer sJoinPluNo = new StringBuffer();
            StringBuffer sJoinTemplateNo = new StringBuffer();

            String orgNo=req.getOrganizationNO();
            if(Check.NotNull(req.getRequest().getOrgNo())){
                orgNo=req.getRequest().getOrgNo();
            }
            String orgSql="select a.*,b.taxrate as inputtaxrate,c.taxname as inputtaxname,b.incltax as inputincltax,b.taxcaltype as inputtaxcaltype" +
                    " from dcp_org a " +
                    " left join dcp_taxcategory b on a.eid=b.eid and a.INPUT_TAXCODE=b.taxcode and b.taxarea='CN' " +
                    " left join dcp_taxcategory_lang c on c.eid=a.eid and c.taxcode=a.input_taxcode and c.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+req.geteId()+"' and a.organizationno='"+orgNo+"'";
            List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);

            String taxPayerType="1";
            String inputTaxCode="";
            String inputTaxName="";
            String inputTaxRate="";
            String inputInclTax="";
            String inputTaxCalType="";
            if(CollUtil.isNotEmpty(orgList)){
                taxPayerType = orgList.get(0).get("TAXPAYER_TYPE").toString();
                inputTaxCode = orgList.get(0).get("INPUT_TAXCODE").toString();
                inputTaxName=orgList.get(0).get("INPUTTAXNAME").toString();
                inputTaxRate=orgList.get(0).get("INPUTTAXRATE").toString();
                inputInclTax=orgList.get(0).get("INPUTINCLTAX").toString();
                inputTaxCalType=orgList.get(0).get("INPUTTAXCALTYPE").toString();
            }

            for (Map<String, Object> row : getQData) {
                DCP_SupplierGoodsOpenQryRes.level1Elm level1Elm =res.new level1Elm();
                level1Elm.setPurTemplateNo(row.get("PURTEMPLATENO").toString());
                level1Elm.setPreDays(row.get("PREDAYS").toString());
                level1Elm.setSupplier(row.get("SUPPLIER").toString());
                level1Elm.setSupplierName(row.get("SUPPLIERNAME").toString());
                level1Elm.setPurType(row.get("PURTYPE").toString());
                level1Elm.setPluBarcode(row.get("PLUBARCODE").toString());
                level1Elm.setPluNo(row.get("PLUNO").toString());
                level1Elm.setPluName(row.get("PLUNAME").toString());
                level1Elm.setSpec(row.get("SPEC").toString());
                level1Elm.setPluType(row.get("PLUTYPE").toString());
                level1Elm.setTaxCode(row.get("TAXCODE").toString());
                level1Elm.setTaxName(row.get("TAXNAME").toString());
                level1Elm.setTaxRate(row.get("TAXRATE").toString());
                level1Elm.setInclTax(row.get("INCLTAX").toString());
                level1Elm.setPurUnit(row.get("PURUNIT").toString());
                level1Elm.setPurUnitName(row.get("PURUNITNAME").toString());
                level1Elm.setPriceType(row.get("PRICETYPE").toString());
                level1Elm.setPurPrice(row.get("PURPRICE").toString());
                level1Elm.setMinpurQty(row.get("MINPURQTY").toString());
                level1Elm.setMulpurQty(row.get("MULPURQTY").toString());
                level1Elm.setStatus(row.get("STATUS").toString());
                level1Elm.setWunit(row.get("WUNIT").toString());
                level1Elm.setWunitName(row.get("WUNITNAME").toString());
                level1Elm.setMinRate(row.get("MINRATE").toString());
                level1Elm.setMaxRate(row.get("MAXRATE").toString());
                level1Elm.setTaxCalType(row.get("TAXCALTYPE").toString());
                level1Elm.setStockQty("0");
                level1Elm.setNonArrivalQty("0");
                level1Elm.setLastStockInPrice("0");
                level1Elm.setLastStockInQty("0");
                level1Elm.setCategory(row.get("CATEGORY").toString());
                level1Elm.setCategoryName(row.get("CATEGORYNAME").toString());
                level1Elm.setBaseUnit(row.get("BASEUNIT").toString());
                level1Elm.setBaseUnitName(row.get("BASEUNITNAME").toString());
                level1Elm.setBaseUnitUdLength(row.get("BASEUNITUDLENGTH").toString());
                level1Elm.setPurUdLength(row.get("PURUDLENGTH").toString());
                level1Elm.setIsBatch(row.get("ISBATCH").toString());
                level1Elm.setShelfLife(row.get("SHELFLIFE").toString());
                level1Elm.setUnitRatio(row.get("UNITRATIO").toString());
                level1Elm.setIsQualityCheck(row.get("ISQUALITYCHECK").toString());
                level1Elm.setFeatureList(new ArrayList<>());

                BigDecimal purPriceBase = new BigDecimal(level1Elm.getPurPrice()).divide(new BigDecimal(level1Elm.getUnitRatio()), 6, RoundingMode.HALF_UP);
                level1Elm.setRefPurPriceBaseUnit(purPriceBase.toString());

                if("2".equals(taxPayerType)){
                    level1Elm.setTaxCode(inputTaxCode);
                    level1Elm.setTaxName(inputTaxName);
                    level1Elm.setTaxRate(inputTaxRate);
                    level1Elm.setInclTax(inputInclTax);
                    level1Elm.setTaxCalType(inputTaxCalType);
                }


                res.getDatas().add(level1Elm);

                if(!plunoDis.contains(level1Elm.getPluNo())){
                    plunoDis.add(level1Elm.getPluNo());
                    sJoinPluNo.append(level1Elm.getPluNo() + ",");
                }

                if(!templateNos.contains(level1Elm.getPurTemplateNo())){
                    templateNos.add(level1Elm.getPurTemplateNo());
                    sJoinTemplateNo.append(level1Elm.getPurTemplateNo() + ",");
                }

            }
            Map<String, String> map = new HashMap<>();
            map.put("PLUNO", sJoinPluNo.toString());

            MyCommon cm = new MyCommon();
            String withPlu = cm.getFormatSourceMultiColWith(map);

            Map<String, String> mapt = new HashMap<>();
            mapt.put("PURTEMPLATENO", sJoinTemplateNo.toString());
            String withTemplate = cm.getFormatSourceMultiColWith(mapt);


            //查询特征码列表
            StringBuffer featureSqlBuffer=new StringBuffer();
            featureSqlBuffer.append("select distinct a.pluno,a.featureno,c.featurename from DCP_GOODS_FEATURE a " +
                            " inner join ("+withPlu+") b on a.pluno=b.pluno" +
                            " inner join DCP_GOODS_FEATURE_LANG c on c.eid=a.eid and c.featureno=a.featureno and c.lang_type='"+req.getLangType()+"' " +
                            " where a.eid='"+req.geteId()+"' " );
            String featureSql=featureSqlBuffer.toString();
            List<Map<String, Object>> getFeature = this.doQueryData(featureSql, null);

            StringBuffer templateSqlBuffer=new StringBuffer();
            templateSqlBuffer.append(" select distinct a.*,c.pluno " +
                    " from DCP_PURCHASETEMPLATE_PRICE a " +
                    " inner join DCP_PURCHASETEMPLATE_GOODS c on c.eid=a.eid and c.purtemplateno=a.purtemplateno and c.item=a.item " +
                    " inner join ("+withTemplate+") b on  a.PURTEMPLATENO=b.PURTEMPLATENO " +
                    " where a.eid='"+req.geteId()+"' ");
            List<Map<String, Object>> getTemplatePrice = this.doQueryData(templateSqlBuffer.toString(), null);

            for ( DCP_SupplierGoodsOpenQryRes.level1Elm row :res.getDatas()){
                String pluNo = row.getPluNo();

                List<Map<String, Object>> features = getFeature.stream().filter(x -> pluNo.equals(x.get("PLUNO").toString())).collect(Collectors.toList());
                for (Map<String, Object> feature : features){
                    DCP_SupplierGoodsOpenQryRes.FeatureList featureList = res.new FeatureList();
                    featureList.setFeatureNo(feature.get("FEATURENO").toString());
                    featureList.setFeatureName(feature.get("FEATURENAME").toString());
                    featureList.setStockQty("0");
                    featureList.setNonArrivalQty("0");
                    featureList.setLastStockInPrice("0");
                    featureList.setLastStockInQty("0");
                    row.getFeatureList().add(featureList);
                }

                if("2".equals(row.getPriceType())) {
                    row.setPurPriceList(new ArrayList<>());
                    List<Map<String, Object>> purTemplatePriceList = getTemplatePrice.stream().filter(x -> x.get("PURTEMPLATENO").toString().equals(row.getPurTemplateNo())&&x.get("PLUNO").toString().equals(row.getPluNo())).collect(Collectors.toList());
                    for (Map<String, Object> purTemplatePrice : purTemplatePriceList) {

                        DCP_SupplierGoodsOpenQryRes.PurPriceList purPriceList = res.new PurPriceList();
                        purPriceList.setBeginQty(purTemplatePrice.get("BQTY").toString());
                        purPriceList.setEndQty(purTemplatePrice.get("EQTY").toString());
                        purPriceList.setPurPrice(purTemplatePrice.get("PURPRICE").toString());
                        row.getPurPriceList().add(purPriceList);
                    }
                }


            }

            List<Map<String, Object>> pluCollection=new ArrayList<>();
            sql = " select a.pluno,a.ounit,a.unit,a.unitratio from dcp_goods_unit a"
                    + " inner join (" + withPlu + ")b on a.pluno=b.pluno"
                    + " where a.eid='"+req.geteId()+"'";
            List<Map<String, Object>> getUnitRatio = this.doQueryData(sql, null);
            for ( DCP_SupplierGoodsOpenQryRes.level1Elm row :res.getDatas()){
                List<Map<String, Object>> unitRatio = getUnitRatio.stream().filter(
                        s -> s.get("PLUNO").toString().equals(row.getPluNo()) && s.get("OUNIT").toString().equals(row.getPurUnit())
                ).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(unitRatio)) {
                    row.setUnitRatio(unitRatio.get(0).get("UNITRATIO").toString());//采购单位和基准单位转换率
                }

                Map<String, Object> xx = new HashMap<>();
                xx.put("PLUNO", row.getPluNo());
                xx.put("PUNIT", row.getPurUnit());
                xx.put("BASEUNIT", row.getBaseUnit());
                xx.put("UNITRATIO",  row.getUnitRatio());
                pluCollection.add(xx);

            }

            String stockOrgNo=Check.NotNull(req.getRequest().getOrgNo())?req.getRequest().getOrgNo():req.getOrganizationNO();

            //String orgSql="select * from dcp_org a where a.eid='"+req.geteId()+"' and a.status='100' and a.ORGANIZATIONNO='"+stockOrgNo+"' ";
            //List<Map<String, Object>> orgData = dao.executeQuerySQL(orgSql, null);
            //if(orgData==null||orgData.size()<=0){
            //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "收货组织状态非[启用]！");
            //}
            String receiptCompany=orgList.get(0).get("BELFIRM").toString();

            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getPrice(dao, req.geteId(), receiptCompany, stockOrgNo, pluCollection,2);

            //price赋值
            res.getDatas().forEach(x->{
                List<Map<String, Object>> priceFilter = getPrice.stream().filter(a -> a.get("PLUNO").toString().equals(x.getPluNo())
                        && a.get("PUNIT").toString().equals(x.getPurUnit())).collect(Collectors.toList());
                BigDecimal price = new BigDecimal(0);
                if(priceFilter.size()>0){
                    price=new BigDecimal(priceFilter.get(0).get("PRICE").toString());
                }
                x.setPrice(price.toString());
            });


            if("Y".equals(queryStockqty)){
               sql = " select a.pluno,a.featureno,sum(a.qty-a.LOCKQTY-a.onlineqty) as baseqty "
                        + " from dcp_stock a"
                        + " inner join ("+withPlu+") b on a.pluno=b.pluno "
                        //+ " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.warehouse=c.warehouse and c.warehouse_type<>'3'"
                        + " where a.eid='"+req.geteId()+"' and a.organizationno='"+stockOrgNo+"'  "
                        + " group by a.pluno,a.featureno";
                List<Map<String, Object>> getStock = this.doQueryData(sql, null);

                sql=" select b.pluno,b.featureno,sum(nvl(c.purqty-c.RECEIVEQTY,0)) nonArrivalQty,c.purunit,d.unitratio " +
                        " from dcp_purorder a " +
                        " left join dcp_purorder_detail b on a.eid=b.eid and a.purorderno=b.purorderno " +
                        " left join  dcp_purorder_delivery c on a.eid=b.eid and a.purorderno=c.purorderno and b.item=c.item2 " +
                        " left join dcp_goods_unit d on d.eid=a.eid and d.pluno=b.pluno and d.ounit=c.purunit and d.unit=b.baseunit  "+
                        " inner join ("+withPlu+")p on c.pluno=p.pluno "+
                        " where a.eid='"+req.geteId()+"' and a.status='1'  " +
                        " and a.RECEIPTORGNO='"+stockOrgNo+"' and a.supplier='"+req.getRequest().getSupplier()+"' " +
                        " group by b.pluno,b.featureno,c.purunit,d.unitratio ";//b.purunit,b.wunit
                List<Map<String, Object>> getPurOrderDelivery = this.doQueryData(sql, null);

                //sql=" select b.pluno,b.featureno,sum(nvl(b.wqty,0)) wqty " +
                //        " from dcp_purorder a " +
                //        " left join dcp_purorder_detail b on a.eid=b.eid and a.purorderno=b.purorderno "+
                //        " inner join ("+withPlu+")p on b.pluno=p.pluno "+
                //        " where a.eid='"+req.geteId()+"' group by b.pluno,b.featureno ";
                //List<Map<String, Object>> getPurOrderDetail = this.doQueryData(sql, null);



                for ( DCP_SupplierGoodsOpenQryRes.level1Elm row :res.getDatas()){

                    //再套一层
                    BigDecimal stockQtySum=BigDecimal.ZERO;
                    BigDecimal nonArrivalQtySum=BigDecimal.ZERO;
                    //List<DCP_SupplierGoodsOpenQryRes.FeatureList> featureList = row.getFeatureList();
                    if(row.getFeatureList()!=null&&row.getFeatureList().size()>0){
                        for ( DCP_SupplierGoodsOpenQryRes.FeatureList feature :row.getFeatureList()){
                            String featureNo = feature.getFeatureNo();
                            List<Map<String, Object>> plunos = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())&&x.get("FEATURENO").toString().equals(featureNo)).distinct().collect(Collectors.toList());
                            String stockQty =plunos.size() > 0 ? plunos.get(0).get("BASEQTY").toString() : "0";
                            stockQtySum=stockQtySum.add(new BigDecimal(stockQty));
                            feature.setStockQty(stockQty);
                            List<Map<String, Object>> purOrderDelivery = getPurOrderDelivery.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())&&x.get("FEATURENO").toString().equals(featureNo)).distinct().collect(Collectors.toList());

                            BigDecimal nonArrivalQty = new BigDecimal(0);  //要转换成采购单位的数量
                            if(CollUtil.isNotEmpty(purOrderDelivery)){
                                for ( Map<String, Object> purOrderDeliveryMap : purOrderDelivery){

                                    BigDecimal nonArrivalQtyD =  new BigDecimal(purOrderDeliveryMap.get("NONARRIVALQTY").toString());
                                    BigDecimal unitRatioD = new BigDecimal(purOrderDeliveryMap.get("UNITRATIO").toString());
                                    BigDecimal nonArrivalQtyDBase = nonArrivalQtyD.multiply(unitRatioD);

                                    //nonArrivalQtyDBase 转采购单位数量
                                    BigDecimal nonArrivalQtyDp = nonArrivalQtyDBase.divide(new BigDecimal(row.getUnitRatio()), Integer.valueOf(row.getPurUdLength()), RoundingMode.HALF_UP);

                                    nonArrivalQty=nonArrivalQty.add(nonArrivalQtyDp);
                                }
                            }

                            feature.setNonArrivalQty(nonArrivalQty.toString());
                            nonArrivalQtySum = nonArrivalQtySum.add(nonArrivalQty);
                        }
                    }
                    else {
                        List<Map<String, Object>> plunos = getStock.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())).distinct().collect(Collectors.toList());
                        String stockQty =plunos.size() > 0 ? plunos.get(0).get("BASEQTY").toString() : "0";
                        stockQtySum=stockQtySum.add(new BigDecimal(stockQty));
                        List<Map<String, Object>> purOrderDelivery = getPurOrderDelivery.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())).distinct().collect(Collectors.toList());
                        BigDecimal nonArrivalQty=BigDecimal.ZERO;
                        for ( Map<String, Object> purOrderDeliveryMap : purOrderDelivery){

                            BigDecimal nonArrivalQtyD =  new BigDecimal(purOrderDeliveryMap.get("NONARRIVALQTY").toString());
                            BigDecimal unitRatioD = new BigDecimal(purOrderDeliveryMap.get("UNITRATIO").toString());
                            BigDecimal nonArrivalQtyDBase = nonArrivalQtyD.multiply(unitRatioD);

                            //nonArrivalQtyDBase 转采购单位数量
                            BigDecimal nonArrivalQtyDp = nonArrivalQtyDBase.divide(new BigDecimal(row.getUnitRatio()), Integer.valueOf(row.getPurUdLength()), RoundingMode.HALF_UP);

                            nonArrivalQty=nonArrivalQty.add(nonArrivalQtyDp);
                        }
                        nonArrivalQtySum = nonArrivalQtySum.add(nonArrivalQty);
                    }
                    row.setStockQty(stockQtySum.toString());
                    row.setNonArrivalQty(nonArrivalQtySum.toString());
                }
            }

            //lastStockInPrice  最近一次入库单价
            //lastStockInQty  最近一次入库数量

            sql="select b.pluno,b.featureno,b.DISTRIPRICE as purprice,b.pqty from DCP_SSTOCKIN a " +
                    " left join DCP_SSTOCKIN_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.SSTOCKINNO=b.SSTOCKINNO " +
                    " inner join ("+withPlu+") p on p.pluno=b.pluno "+
                    " where a.eid='"+req.geteId()+"' " +
                    " and a.organizationno='"+stockOrgNo+"' " +
                    " order by a.CONFIRM_TIME ";
            List<Map<String, Object>> lastStockIn = this.doQueryData(sql, null);
            if(lastStockIn!=null &&lastStockIn.size()>0){
                for ( DCP_SupplierGoodsOpenQryRes.level1Elm row :res.getDatas()){
                    if(row.getFeatureList()!=null&&row.getFeatureList().size()>0) {
                        for (DCP_SupplierGoodsOpenQryRes.FeatureList feature : row.getFeatureList()) {
                            String featureNo = feature.getFeatureNo();
                            List<Map<String, Object>> plunos = lastStockIn.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())&&x.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
                            if(plunos.size()>0){
                                feature.setLastStockInPrice(plunos.get(0).get("PURPRICE").toString());
                                feature.setLastStockInQty(plunos.get(0).get("PQTY").toString());
                                row.setLastStockInPrice(plunos.get(0).get("PURPRICE").toString());
                                row.setLastStockInQty(plunos.get(0).get("PQTY").toString());

                            }
                        }
                    }else{
                        List<Map<String, Object>> plunos = lastStockIn.stream().filter(x -> x.get("PLUNO").toString().equals(row.getPluNo())).collect(Collectors.toList());
                        if(plunos.size()>0){
                            row.setLastStockInPrice(plunos.get(0).get("PURPRICE").toString());
                            row.setLastStockInQty(plunos.get(0).get("PQTY").toString());
                        }
                    }
                }

               }
        }else{
            totalRecords = 0;
            totalPages = 0;
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_SupplierGoodsOpenQryReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        DCP_SupplierGoodsOpenQryReq.levelItem request = req.getRequest();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        //計算起啟位置
        int startRow = (pageNumber - 1) * pageSize;

        String eId=req.geteId();
        String shopId = req.getShopId();

        //在途量 可用库存量  后面集合处理
        String langType=req.getLangType();

        sqlbuf.append("SELECT * FROM ("
                + " SELECT COUNT( a.purTemplateNo ) OVER() NUM ,dense_rank() over(ORDER BY a.createtime desc) rn,a.*  "
                + " FROM ( "
                + "  SELECT distinct a.purTemplateNo,a.createtime,a.PRE_DAY as predays,d.BIZPARTNERNO as supplier,d.sname as suppliername,a.purtype,f.MAINBARCODE as PLUBARCODE,f.pluno,g.plu_name as pluname,gul.spec,f.PLUTYPE,"
                + "  i.taxcode,i.taxname,h.taxrate,h.INCLTAX ,b.PURUNIT as purUnit,l.uname as purunitname,b.PRICETYPE,b.PURBASEPRICE as purPrice,b.MINPQTY as minpurQty,b.MULPQTY as mulpurQty,b.status,j.unit as wunit, j.uname as wunitname,k.unit as baseunit,k.uname as baseunitname,"
                + "  b.minrate,b.maxrate,h.TAXCALTYPE,m.CATEGORY,m.CATEGORY_name as categoryname,k1.UDLENGTH as baseUnitUdLength,l1.udlength as purUdLength,f.isbatch,f.SHELFLIFE,gul0.unitratio,f.ISQUALITYCHECK  "
                + "  FROM DCP_PURCHASETEMPLATE  a "
                + "  LEFT JOIN DCP_PURCHASETEMPLATE_GOODS b ON a.EID = b.EID AND a.purtemplateno = b.purtemplateno "
                + "  LEFT JOIN DCP_PURCHASETEMPLATE_ORG c on c.EID=a.EID and c.purtemplateno=a.purtemplateno "
                + "  left join DCP_BIZPARTNER d on d.eid=a.eid and d.BIZPARTNERNO=a.SUPPLIERNO "
                //+ "  left join DCP_GOODS_BARCODE e on e.eid=a.eid and b.pluno=e.pluno   "
                + "  left join dcp_goods f on f.eid=a.eid and f.pluno=b.pluno "
                + "  left join dcp_goods_lang g on g.eid=f.eid and g.pluno=f.pluno and g.lang_type='"+langType+"'"
                + "  left join dcp_taxcategory h on h.eid=b.eid and b.taxcode=h.TAXCODE "
                + "  left join dcp_taxcategory_lang i on i.eid=h.eid and h.taxcode=i.taxcode and i.lang_type='"+langType+"'"
                + "  left join dcp_unit_lang j on j.unit=f.wunit and j.eid=f.eid and j.lang_type='"+langType+"' "
                + "  left join dcp_unit_lang k on k.unit=f.baseunit and k.eid=f.eid and k.lang_type='"+langType+"' "
                + "  left join dcp_unit k1 on k1.unit=f.baseunit and k1.eid=f.eid  "
                + "  left join dcp_unit_lang l on l.unit=b.PURUNIT and l.eid=b.eid and l.lang_type='"+langType+"' "
                + "  left join dcp_unit l1 on l1.unit=b.PURUNIT and l1.eid=b.eid  "
                + "  left join DCP_GOODS_UNIT_LANG gul on f.eid=gul.eid and f.pluno=gul.pluno and f.PURUNIT=gul.ounit and gul.lang_type='"+langType+"'"
                + "  left join DCP_GOODS_UNIT gul0 on f.eid=gul0.eid and f.pluno=gul0.pluno and f.PURUNIT=gul0.ounit  "
                + "  left join DCP_CATEGORY_LANG m on m.eid=f.eid and m.CATEGORY=f.CATEGORY and m.lang_type='"+langType+"' " +
                " where a.eid='"+eId+"' and f.pluno is not null and a.status='100'  and b.STATUS='100' and f.STATUS='100' "
        );

        if(!Check.Null(request.getSupplier())){
            sqlbuf.append(" and a.supplierno='"+request.getSupplier()+"' ");
        }
        if(!Check.Null(request.getPurTemplateNo())){
            sqlbuf.append(" and a.PURTEMPLATENO='"+request.getPurTemplateNo()+"' ");
        }
        if(!Check.Null(request.getOrgNo())){
            sqlbuf.append(" and c.ORGANIZATIONNO='"+request.getOrgNo()+"' ");
        }else{
            sqlbuf.append(" and c.ORGANIZATIONNO='"+req.getOrganizationNO()+"' ");
        }
        String[] category = request.getCategory();
        if(category!=null&&category.length>0){
            String categoryStr = "";
            for(String s:category){
                categoryStr+="'"+s+"',";
            }
            categoryStr=categoryStr.substring(0,categoryStr.length()-1);
            sqlbuf.append(" and f.category in ("+categoryStr+") ");
        }
        String[] pluBarcode = request.getPluBarcode();
        if(pluBarcode!=null&&pluBarcode.length>0){
            String pluBarcodeStr = "";
            for(String s:pluBarcode){
                pluBarcodeStr+="'"+s+"',";
            }
            pluBarcodeStr=pluBarcodeStr.substring(0,pluBarcodeStr.length()-1);
            sqlbuf.append(" and f.MAINBARCODE in ("+pluBarcodeStr+") ");
        }
        String[] pluNo = request.getPluNo();
        if(pluNo!=null&&pluNo.length>0){
            String pluNoStr = "";
            for(String s:pluNo){
                pluNoStr+="'"+s+"',";
            }
            pluNoStr=pluNoStr.substring(0,pluNoStr.length()-1);
            sqlbuf.append(" and b.pluno in ("+pluNoStr+") ");
        }
        String[] purType = request.getPurType();
        if(purType!=null&&purType.length>0){
            String purTypeStr = "";
            for(String s:purType){
                purTypeStr+="'"+s+"',";
            }
            purTypeStr=purTypeStr.substring(0,purTypeStr.length()-1);
            sqlbuf.append(" and a.purtype in ("+purTypeStr+") ");
        }

        if(!Check.Null(request.getKeyTxt())){
            sqlbuf.append(" and (f.pluno like '%%"+request.getKeyTxt()+"%%' or g.plu_name like '%%"+request.getKeyTxt()+"%%' or f.MAINBARCODE like '%%"+request.getKeyTxt()+"%%' ) ");
        }


        sqlbuf.append("  ) a  ") ;
        sqlbuf.append("  )  a "
                + "    WHERE   rn> " + startRow + " and rn<= " + (startRow + pageSize) + ""
                + " ORDER BY purTemplateNo ");


        return sqlbuf.toString();

    }

}
