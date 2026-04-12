package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.cust.req.DCP_PTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_PTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @implNote 要货模板明细查询
 * @author jinzma
 * @since 2017-03-09
 */
public class DCP_PTemplateDetail extends SPosBasicService<DCP_PTemplateDetailReq,DCP_PTemplateDetailRes>  {
    public String getCategory="";
    @Override
    protected boolean isVerifyFail(DCP_PTemplateDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if (Check.Null(req.getRequest().getDocType())){
            errMsg.append("查询类型不可为空值! ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getpTemplateNo())){
            errMsg.append("模板编号不可为空值! ");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_PTemplateDetailReq> getRequestType(){
        return new TypeToken<DCP_PTemplateDetailReq>(){};
    }
    
    @Override
    protected DCP_PTemplateDetailRes getResponseType() {
        return new DCP_PTemplateDetailRes();
    }
    
    @Override
    protected DCP_PTemplateDetailRes processJson(DCP_PTemplateDetailReq req) throws Exception {
        DCP_PTemplateDetailRes res = this.getResponse();
        //try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String companyId = req.getBELFIRM();
            String queryStockqty=req.getRequest().getQueryStockqty();
            String queryStockWarehouse=req.getRequest().getQueryStockWarehouse();
            String docType = req.getRequest().getDocType();  //0.要货模板  1.盘点模板  2.加工模板  3.自采模板  5.调拨模板
            if (Check.Null(companyId)) {
                String sqlshop = " select * from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' and status='100' ";
                List<Map<String, Object>> slshop = this.doQueryData(sqlshop, null);
                if (slshop != null && !slshop.isEmpty()) {
                    companyId =  slshop.get(0).get("BELFIRM").toString();
                }
                else {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店:"+shopId+"在组织表中不存在或未生效！");
                }
            }
            //是否开启多单位盘点
            String IsStockMultipleUnit = req.getRequest().getIsStockMultipleUnit();
            if (Check.Null(IsStockMultipleUnit)) {
                IsStockMultipleUnit = "N";
            }
            ///处理单价和金额小数位数  BY JZMA 20200401
            String priceLength = PosPub.getPARA_SMS(dao, eId, shopId, "priceLength");
            String distriPriceLength = PosPub.getPARA_SMS(dao, eId, shopId, "distriPriceLength");
            String sql;
            if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)) {
                priceLength="2";
            }
            if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)) {
                distriPriceLength="2";
            }
            int priceLengthInt = Integer.parseInt(priceLength);
            int distriPriceLengthInt = Integer.parseInt(distriPriceLength);
            
            //处理==绑定变量SQL的写法
            List<DataValue> lstDV = new ArrayList<>();
            
            //查询模板明细
            switch (docType) {
                case "0":
                    sql = getPorderSql(req,companyId);    //0.要货模板
                    break;
                case "1":
                    sql = getStockTakeSql(req,companyId,IsStockMultipleUnit,lstDV); //1.盘点模板
                    break;
                case "2":
                    sql = getPstockInSql(req,companyId);  //2.加工模板
                    break;
                case "3":
                    sql = getSstockInSql(req,companyId);  //3.自采模板
                    break;
                case "5":
                    sql = getStockOutSql(req,companyId);  //5.调拨模板
                    break;
                default:
                    sql = getQuerySql(req,companyId);     //普通模板
                    break;
            }
            List<Map<String, Object>> getQData;
            //先改1个盘点模板看看效能咋样
            if (docType.equals("1")) {
                getQData=this.executeQuerySQL_BindSQL(sql,lstDV);
            }else {
                getQData=this.doQueryData(sql, null);
            }
            
            res.setDatas(new ArrayList<>());
            if (getQData != null && !getQData.isEmpty()) {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps=PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                String httpStr=isHttps.equals("1")?"https://":"http://";
                String domainName=PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                String pTemplateNO = getQData.get(0).get("PTEMPLATENO").toString();
                String ptemplate_name = getQData.get(0).get("PTEMPLATE_NAME").toString();
                String isBTake = getQData.get(0).get("IS_BTAKE").toString();
                String receiptOrg = getQData.get(0).get("RECEIPT_ORG").toString();
                String preDay = getQData.get(0).get("PRE_DAY").toString();
                String receiptOrgName = getQData.get(0).get("RECEIPTORGNAME").toString();
                String optionalTime=getQData.get(0).get("OPTIONAL_TIME").toString();
                String taskWay = getQData.get(0).get("TASKWAY").toString();
                String supplier = getQData.get(0).get("SUPPLIER").toString();
                String supplierName = getQData.get(0).get("SUPPLIER_NAME").toString();
                String abbr = getQData.get(0).get("ABBR").toString();
                String goodsWarehouseNO =  getQData.get(0).get("GOODSWAREHOUSE").toString();
                String goodsWarehouseName =  getQData.get(0).get("GOODSWAREHOUSENAME").toString();
                String materialWarehouseNO = getQData.get(0).get("MATERIALWAREHOUSE").toString();
                String materialWarehouseName =  getQData.get(0).get("MATERIALWAREHOUSENAME").toString();
                String cal_type =getQData.get(0).get("CAL_TYPE").toString();
                String materal_type=getQData.get(0).get("MATERAL_TYPE").toString();
                String stockInAllowType = getQData.get(0).get("STOCKINALLOWTYPE").toString();
                String stockOutAllowType = getQData.get(0).get("STOCKOUTALLOWTYPE").toString();
                String isAdjustStock = getQData.get(0).get("IS_ADJUST_STOCK").toString();   //是否调整库存Y/N/X Y转库存 N转销售 X不异动
                String isAddGoods = getQData.get(0).get("ISADDGOODS").toString();
                String isShowHeadStockQty = getQData.get(0).get("ISSHOWHEADSTOCKQTY").toString();
                String supplierType = getQData.get(0).get("SUPPLIERTYPE").toString();
                String supplierId = "";
                String supplierDName = "";
                
                //【ID1039808】【金贝儿3403】盘点单审核增加处理不异动库存情况 by jinzma 20240327
                if (Check.Null(isAdjustStock)) {
                    isAdjustStock = "Y";
                }

                if (Check.Null(stockInAllowType)||!stockInAllowType.equals("1"))
                    stockInAllowType="0";
                if (Check.Null(stockOutAllowType)||!stockOutAllowType.equals("1"))
                    stockOutAllowType="0";
                if (Check.Null(isAddGoods) || !isAddGoods.equals("Y"))
                    isAddGoods="N" ;
                if (Check.Null(isShowHeadStockQty)||!isShowHeadStockQty.equals("Y"))
                    isShowHeadStockQty="N";
                
                DCP_PTemplateDetailRes.level1Elm oneLv1 = res.new level1Elm();
                //设置响应
                oneLv1.setpTemplateNo(pTemplateNO);
                oneLv1.setpTemplateName(ptemplate_name);
                oneLv1.setReceiptOrg(receiptOrg);
                oneLv1.setPreDay(preDay);
                oneLv1.setReceiptOrgName(receiptOrgName);
                oneLv1.setIsBTake(isBTake);
                oneLv1.setOptionalTime(optionalTime);
                oneLv1.setTaskWay(taskWay);
                oneLv1.setGoodsWarehouseNo(goodsWarehouseNO);
                oneLv1.setGoodsWarehouseName(goodsWarehouseName);
                oneLv1.setMaterialWarehouseNo(materialWarehouseNO);
                oneLv1.setMaterialWarehouseName(materialWarehouseName);
                oneLv1.setSupplier(supplier);
                oneLv1.setSupplierName(supplierName);
                oneLv1.setAbbr(abbr);
                oneLv1.setCal_type(cal_type);
                oneLv1.setMateral_type(materal_type);
                oneLv1.setStockInAllowType(stockInAllowType);
                oneLv1.setStockOutAllowType(stockOutAllowType);
                oneLv1.setIsAdjustStock(isAdjustStock);
                oneLv1.setRdate_Type(getQData.get(0).get("RDATE_TYPE").toString());
                oneLv1.setRdate_Add(getQData.get(0).get("RDATE_ADD").toString());
                oneLv1.setRdate_Values(getQData.get(0).get("RDATE_VALUES").toString());
                oneLv1.setRevoke_Day(getQData.get(0).get("REVOKE_DAY").toString());
                oneLv1.setRevoke_Time(getQData.get(0).get("REVOKE_TIME").toString());
                oneLv1.setRdate_Times(getQData.get(0).get("RDATE_TIMES").toString());
                oneLv1.setIsAddGoods(isAddGoods);
                oneLv1.setIsShowHeadStockQty(isShowHeadStockQty);
                oneLv1.setDocType(docType);
                
                //处理成品零售价和配送价
                List<Map<String, Object>> plus = new ArrayList<>();
                Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                condition.put("PLUNO", true);
                List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
                for (Map<String, Object> onePlu :getQPlu ) {
                    Map<String, Object> plu = new HashMap<>();
                    plu.put("PLUNO", onePlu.get("PLUNO").toString());
                    plu.put("PUNIT", onePlu.get("PUNIT").toString());
                    plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                    plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
                    plu.put("SUPPLIERID",onePlu.get("RECEIPT_ORG").toString());
                    plus.add(plu);
                }
                
                MyCommon mc = new MyCommon();
                List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,companyId);
//				if( docType.equals("3"))      //3.自采模板
//				{
//					getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,2,supplier);
//				}
//				else if(docType.equals("0"))  //0.要货模板
//				{
//					getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,3,supplier);
//				}
//				else
//				{
//					getPluPrice = mc.getSalePrice_distriPrice(dao,eId,companyId, shopId,plus,1,companyId);
//				}
                
                List<Map<String, Object>> getQDetail = getQData;
                //多单位造成单身资料重复，需要过滤
                if (docType.equals("1") && IsStockMultipleUnit.equals("Y")) {
                    condition.clear();
                    condition.put("PLUNO", true);
                    condition.put("FEATURENO", true);
                    getQDetail = MapDistinct.getMap(getQData, condition);
                }
                
                oneLv1.setDatas(new ArrayList<>());
                for (Map<String, Object> oneData : getQDetail) {
                    DCP_PTemplateDetailRes.level2Elm oneLv2 = res.new level2Elm();
                    String item = oneData.get("ITEM").toString();
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String standardPrice = oneData.get("STANDARDPRICE").toString();
                    String category = oneData.get("CATEGORY").toString();
                    String categoryName = oneData.get("CATEGORYNAME").toString();
                    String spec = oneData.get("SPEC").toString();
                    String shortCutCode = oneData.get("SHORTCUT_CODE").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String punit = oneData.get("PUNIT").toString();
                    String punitName = oneData.get("PUNITNAME").toString();
                    String minQty = oneData.get("MIN_QTY").toString();
                    String maxQty = oneData.get("MAX_QTY").toString();
                    String mulQty = oneData.get("MUL_QTY").toString();
                    String defQty = oneData.get("DEFAULT_QTY").toString();
                    String pMulQty = oneData.get("PMULQTY").toString();
                    String pStockInQty = oneData.get("PSTOCKINQTY").toString();
                    String groupNo = oneData.get("GROUPNO").toString();
                    String groupType = oneData.get("GROUPTYPE").toString();
                    String groupReachCount = oneData.get("GROUPREACHCOUNT").toString();
                    String punitUDLength = oneData.get("PUNIT_UDLENGTH").toString();
                    String listImage = oneData.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName + listImage;
                    }
                    String featureNo= oneData.get("FEATURENO").toString();
                    String featureName = oneData.get("FEATURENAME").toString();
                    String unitRatio = oneData.get("UNITRATIO").toString();
                    String isBatch = oneData.get("ISBATCH").toString();
                    String stockInValidDay = oneData.get("STOCKINVALIDDAY").toString();
                    String stockOutValidDay = oneData.get("STOCKOUTVALIDDAY").toString();
                    String shelfLife = oneData.get("SHELFLIFE").toString();
                    String isHoliday = oneData.getOrDefault("ISHOLIDAY","").toString();
                    //【ID1018821】商品查询服务 by jinzma 20210702
                    String isHotGoods = oneData.get("ISHOTGOODS").toString();
                    if (Check.Null(isHotGoods)){
                        isHotGoods="N";
                    }
                    //【ID1018735】【3.0货郎】移动门店新增新品查看和筛选功能 by jinzma 20210702
                    String isNewGoods = oneData.get("ISNEWGOODS").toString();
                    if (Check.Null(isNewGoods)){
                        isNewGoods="N";
                    }
                    
                    if( Check.Null(stockInValidDay))
                        stockInValidDay="0";
                    if( Check.Null(stockOutValidDay))
                        stockOutValidDay="0";
                    if( Check.Null(shelfLife))
                        shelfLife="0";
                    if (Check.Null(stockOutAllowType) || stockOutAllowType.equals("0"))
                        stockOutValidDay="0"; //退货效期==0 不管控
                    if (Check.Null(stockInAllowType) || stockInAllowType.equals("0"))
                        stockInValidDay="0";  //进货效期==0 不管控
                    
                    //计算零售价及进货价
                    String price="0";
                    String distriPrice="0";
                    Map<String, Object> condiV= new HashMap<>();
                    condiV.put("PLUNO",pluNo);
                    condiV.put("PUNIT",punit);
                    List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                    if(priceList!=null && priceList.size()>0 ) {
                        price=priceList.get(0).get("PRICE").toString();
                        distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                        BigDecimal price_b=new BigDecimal(price);
                        price_b=price_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                        price=price_b.toPlainString();
                        BigDecimal distriPrice_b=new BigDecimal(distriPrice);
                        distriPrice_b=distriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                        distriPrice=distriPrice_b.toPlainString();
                    }
                    
                    //盘点模板取实时库存
                    String batchNo="";
                    String prodDate="";
                    String refBaseQty="0";
                    if (docType.equals("1")){
                        batchNo = oneData.get("BATCHNO").toString();
                        prodDate = oneData.get("PRODDATE").toString();
                        refBaseQty = oneData.get("STOCKBASEQTY").toString();
                    }
                    if (Check.Null(refBaseQty)) {
                        refBaseQty = "0";
                    }
                    //生产单位为空，抛异常
                    if (Check.Null(punit)) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+pluNo+" 未维护默认盘点单位或生产单位");
                    }
                    
                    //【ID1018077】 【3.0货郎】门店自采获取外部供应商采购价   by jinzma 20210618
                    if (docType.equals("3")) {
                        distriPrice = oneData.get("DISTRIPRICE").toString();
                    }
                    
                    String stockManageType=oneData.get("STOCKMANAGETYPE").toString();
                    
                    //支持多单位盘点
                    oneLv2.setUnitList(new ArrayList<>());
                    if (docType.equals("1") && IsStockMultipleUnit.equals("Y")) {
                        int i=0;
                        for (Map<String, Object> oneDataUnit : getQData) {
                            String unitList_unit = oneDataUnit.get("UNITLIST_UNIT").toString();
                            String unitList_pluNo = oneDataUnit.get("PLUNO").toString();
                            String unitList_featureNo = oneDataUnit.get("FEATURENO").toString();
                            
                            if(pluNo.equals(unitList_pluNo) && featureNo.equals(unitList_featureNo) && !Check.Null(unitList_unit)) {
                                
                                if (i==0){
                                    DCP_PTemplateDetailRes.level3Elm oneLv3 = new DCP_PTemplateDetailRes().new level3Elm();
                                    oneLv3.setPunit(punit);
                                    oneLv3.setPunitName(punitName);
                                    oneLv3.setUnitRatio(unitRatio);
                                    oneLv3.setPunitUDLength(punitUDLength);
                                    oneLv2.getUnitList().add(oneLv3);
                                    i=1;
                                }
                                
                                if (!punit.equals(unitList_unit)) {
                                    DCP_PTemplateDetailRes.level3Elm oneLv3 = new DCP_PTemplateDetailRes().new level3Elm();
                                    oneLv3.setPunit(unitList_unit);
                                    oneLv3.setPunitName(oneDataUnit.get("UNITLIST_UNITNAME").toString());
                                    oneLv3.setUnitRatio(oneDataUnit.get("UNITLIST_UNITRATIO").toString());
                                    oneLv3.setPunitUDLength(oneDataUnit.get("UNITLIST_UNITUDLENGTH").toString());
                                    oneLv2.getUnitList().add(oneLv3);
                                }
                                
                            }
                        }
                    }
                    
                    oneLv2.setItem(item);
                    oneLv2.setPluNo(pluNo);
                    oneLv2.setPluName(pluName);
                    oneLv2.setSpec(spec);
                    oneLv2.setShortCutCode(shortCutCode);
                    oneLv2.setPrice(price);
                    oneLv2.setStandardPrice(standardPrice);
                    oneLv2.setCategory(category);
                    oneLv2.setCategoryName(categoryName);
                    oneLv2.setDistriPrice(distriPrice);
                    oneLv2.setUnitRatio(unitRatio);
                    oneLv2.setBaseUnit(baseUnit);
                    oneLv2.setBaseUnitName(baseUnitName);
                    oneLv2.setPunit(punit);
                    oneLv2.setPunitName(punitName);
                    oneLv2.setMinQty(minQty);
                    oneLv2.setMaxQty(maxQty);
                    oneLv2.setMulQty(mulQty);
                    oneLv2.setDefQty(defQty);
                    oneLv2.setpMulQty(pMulQty);
                    oneLv2.setpStockInQty(pStockInQty);
                    oneLv2.setIsBatch(isBatch);
                    oneLv2.setStockInValidDay(stockInValidDay);
                    oneLv2.setStockOutValidDay(stockOutValidDay);
                    oneLv2.setShelfLife(shelfLife);
                    oneLv2.setBatchNo(batchNo);
                    oneLv2.setProdDate(prodDate);
                    oneLv2.setPunitUdLength(punitUDLength);
                    oneLv2.setRefBaseQty(refBaseQty);
                    oneLv2.setListImage(listImage);
                    oneLv2.setFeatureNo(featureNo);
                    oneLv2.setFeatureName(featureName);
                    oneLv2.setGroupNo(groupNo);
                    oneLv2.setGroupType(groupType);
                    oneLv2.setGroupReachCount(groupReachCount);
                    oneLv2.setIsHoliday(isHoliday);
                    oneLv2.setIsNewGoods(isNewGoods);
                    oneLv2.setIsHotGoods(isHotGoods);
                    oneLv2.setStockManageType(stockManageType);
                    oneLv2.setStockqty("999999");
                    
                    oneLv2.setSupplierType(supplierType); // DCP_PTemplateDetail：商品列表增加返回 supplierType、supplierId、supplierName  01029
                    if (StringUtils.equals("FACTORY", supplierType)){
                    	supplierId = receiptOrg;
                    	supplierDName = receiptOrgName;
                    }else if (StringUtils.equals("SUPPLIER", supplierType)){
                    	supplierId = oneData.get("SUP").toString();
                    	supplierDName = oneData.get("SUPNAME").toString();
                    }
                    oneLv2.setSupplierId(supplierId);
                    oneLv2.setSupplierName(supplierDName);
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221110
                    oneLv2.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    //【ID1033707】【潮品3.0】门店要货量管控：当前补货申请量+当前库存+配送在途+要货在途量不可超过设定库存上限值----服务端
                    oneLv2.setWarningQty(oneData.get("WARNINGQTY").toString());
                    
                    
                    //添加单身
                    oneLv1.getDatas().add(oneLv2);
                }
                //添加单头
                res.getDatas().add(oneLv1);
            }
            
            
            //处理库存量===
            if (queryStockqty!=null && queryStockqty.equals("Y"))
            {
                if (res.getDatas() != null && res.getDatas().size()>0)
                {
                    //
                    JSONArray pluList=new JSONArray();
                    for (DCP_PTemplateDetailRes.level2Elm p1 : res.getDatas().get(0).getDatas())
                    {
                        if (p1.getStockManageType()!=null && (p1.getStockManageType().equals("2") || p1.getStockManageType().equals("3")))
                        {
                            //无特征码
                            JSONObject plu=new JSONObject();
                            plu.put("pluNo",p1.getPluNo());
                            plu.put("featureNo",p1.getFeatureNo());
                            pluList.put(plu);
                        }
                    }
                    if (pluList != null && pluList.length()>0)
                    {
                        JSONObject req_SOLD = new JSONObject();
                        req_SOLD.put("serviceId","DCP_GoodsStockQuery");
                        req_SOLD.put("token", req.getToken());
                        // //JOB调订转销用到，没token
                        req_SOLD.put("eId",eId);
                        req_SOLD.put("eShop",shopId);
                        
                        //
                        JSONObject request_SOLD = new JSONObject();
                        request_SOLD.put("eId", eId);
                        request_SOLD.put("queryOrgId", shopId);
                        request_SOLD.put("queryType", "DCP");
                        
                        //【ID1034198】【罗森尼娜3.0】移仓单、调拨出库单等单据的库存量计算错误  by jinzma 20230621
                        if (!Check.Null(queryStockWarehouse)){
                            request_SOLD.put("warehouse",queryStockWarehouse);
                        }
                        
                        request_SOLD.put("pluList", pluList);
                        req_SOLD.put("request", request_SOLD);
                        
                        String str_SOLD = req_SOLD.toString();// 将json对象转换为字符串
                        
                        //内部调内部
                        DispatchService ds = DispatchService.getInstance();
                        String resbody_SOLD = ds.callService(str_SOLD, this.dao);
                        
                        if (resbody_SOLD.equals("")==false)
                        {
                            JSONObject jsonres_SOLD = new JSONObject(resbody_SOLD);
                            boolean success = jsonres_SOLD.getBoolean("success");
                            if (success)
                            {
                                JSONObject datas_SOLD=jsonres_SOLD.getJSONObject("datas");
                                JSONArray res_pluList=datas_SOLD.getJSONArray("pluList");
                                if (res_pluList!=null && res_pluList.length()>0)
                                {
                                    for (int ri = 0; ri < res_pluList.length(); ri++)
                                    {
                                        String r_pluno=res_pluList.getJSONObject(ri).get("pluNo").toString();
                                        String r_baseunit=res_pluList.getJSONObject(ri).get("baseUnit").toString();
                                        String r_baseqty=res_pluList.getJSONObject(ri).get("baseQty").toString();
                                        //汇总特征码库存设置
                                        List<DCP_PTemplateDetailRes.level2Elm> goods=res.getDatas().get(0).getDatas().stream().filter(c->c.getPluNo().equals(r_pluno)).collect(Collectors.toList());
                                        goods.stream().forEach(d->{d.setStockqty(r_baseqty);});
                                        //特征码库量存设置
                                        JSONArray res_featureList=res_pluList.getJSONObject(ri).getJSONArray("featureList");
                                        if (res_featureList!=null && res_featureList.length()>0)
                                        {
                                            for (int si = 0; si < res_featureList.length(); si++)
                                            {
                                                String r_featureNo=res_featureList.getJSONObject(si).get("featureNo").toString();
                                                String r_baseQty=res_featureList.getJSONObject(si).get("baseQty").toString();
                                                goods.stream().filter(c->c.getFeatureNo().equals(r_featureNo)).forEach(d->{
                                                    d.setStockqty(r_baseQty);
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
            }
            
            
            return res;
            
        //}catch (Exception e) {
        //    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        //}
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_PTemplateDetailReq req) throws Exception {
        return  null;
    }
    
    private String getQuerySql(DCP_PTemplateDetailReq req,String companyId) throws Exception {
        String sql;
        String docType = req.getRequest().getDocType();        //0.要货模板  1.盘点模板  2.加工模板  3.自采模板
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        
        StringBuffer sqlbuf=new StringBuffer();
        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/
    
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        //sqlbuf.append(" with goodstemplate as ("
        //        + " select b.* from dcp_goodstemplate a"
         //       + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
         //       + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')  "
        //        + " )"
        //        + " ");

        String tempSql="SELECT * " +
                " FROM (" +
                "    SELECT a.TEMPLATETYPE,c.*," +
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
        sqlbuf.append(" with goodstemplate as ("+tempSql+")");
        
        
        sqlbuf.append(""
                + " select a.ptemplateno,lower(c.shortcut_code) as shortcut_code,a.pre_day,a.optional_time,a.taskway,"
                + " '0' as pstockinqty,a.receipt_org,'' as receiptorgname,a.is_btake,ptemplate_name,b.item,b.pluno,b.punit,b.min_qty,"
                + " b.mul_qty,b.max_qty,d.plu_name,gul.spec,image.listimage,'0' as pmulqty,f.uname as punitname,c.baseunit,c.ishotgoods,"
                + " fw.uname as baseunitname,a.time_type, a.time_value,a.supplier,s.supplier_name,s.abbr,"
                + " h.goodswarehouse,i.warehouse_name as goodswarehousename,h.materialwarehouse,j.warehouse_name as materialwarehousename,"
                + " nvl(a.cal_type,'1') as cal_type,nvl(materal_type,'1') as materal_type,nvl(b.default_qty,0) as default_qty,"
                + " c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,c.ISHOLIDAY,c.STOCKMANAGETYPE,t.stockinallowtype,t.stockoutallowtype,a.is_adjust_stock,"
                + " N'' as groupno,N'' as grouptype,'0' as groupreachcount,a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,"
                + " a.rdate_times,a.isshowzstock,a.isaddgoods,a.isshowheadstockqty,"
                + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                + " gf.featureno,gflang.featurename,"
                + " gu.unitratio,goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE,c.price as standardPrice,c.category,cl.category_name as categoryname "
                + " from dcp_ptemplate a"
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                + " inner join dcp_goods c on b.pluno=c.pluno and b.eid=c.eid  and c.status='100'"
                + " left join dcp_goods_lang d  on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='"+ langType +"'"
                + " left join dcp_unit_lang f  on b.punit=f.unit and a.eid=f.eid and f.lang_type='"+ langType +"'"
                + " left join dcp_unit_lang fw  on c.baseunit=fw.unit and c.eid=fw.eid and fw.lang_type='"+ langType +"'"
                + " left join dcp_ptemplate_shop h on b.eid=h.eid and b.ptemplateno=h.ptemplateno and b.doc_type=h.doc_type and h.shopid='"+shopId+"' "
                + " left join dcp_warehouse_lang i on i.eid=h.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='"+ langType +"'"
                + " left join dcp_warehouse_lang j on j.eid=h.eid and j.warehouse=h.materialwarehouse  and j.organizationno=h.shopid and j.lang_type='"+ langType +"'"
                + " left join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '"+langType+"'"
                + " left join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
                + " left  join dcp_unit u on u.eid=a.eid and b.punit=u.unit and u.status='100'"
                + " left  join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE'"
                + " left  join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='"+langType+"'"
                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.punit and gu.unit=c.baseunit"
                + " left join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                + " where a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"' and a.ptemplateno = '"+ pTemplateNO +"'"
                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')"
                + " order by b.pluno,gf.featureno"
                + " ");

        sql = sqlbuf.toString();
        return sql;

    }

    private String getPorderSql(DCP_PTemplateDetailReq req,String companyId) {
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String docType = req.getRequest().getDocType();        //0.要货模板  1.盘点模板  2.加工模板  3.自采模板

        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
        StringBuffer sqlbuf=new StringBuffer();
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                + " where a.eid='" + eId + "' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )");*/

        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        //sqlbuf.append(" with goodstemplate as ("
        //        + " select b.* from dcp_goodstemplate a"
        //        + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
        //        +   " left join DCP_GOODSTEMPLATE_RANGE c on a.eid=c.eid and a.templateid=c.templateid "
        //        + " where a.eid='"+eId+"' and c.id='"+shopId+"' "
        //        + " )"
        //        + " ");


        String tempSql="SELECT * " +
                " FROM (" +
                "    SELECT a.TEMPLATETYPE,c.*," +
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
        sqlbuf.append(" with goodstemplate as ("+tempSql+")");


        sqlbuf.append(" "
                + " select distinct a.ptemplateno,a.pre_day,a.optional_time,a.taskway,a.receipt_org,a.is_btake,a.ptemplate_name,a.time_type,"
                + " a.time_value,a.supplier,a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,a.rdate_times,"
                + " a.isaddgoods,a.isshowheadstockqty,nvl(a.cal_type,'1') as cal_type,a.is_adjust_stock,"
                + " b.item,b.pluno,b.punit,b.min_qty,b.mul_qty,nvl(b.max_qty,99999) as max_qty,"
                + " nvl(b.default_qty,0) as default_qty,lower(c.shortcut_code) as shortcut_code,c.isbatch,c.stockinvalidday,"
                + " c.stockoutvalidday,c.shelflife,image.listImage,c.baseunit,c.ISHOLIDAY,c.STOCKMANAGETYPE,c.ishotgoods,"
                + " '0' as pstockinqty,f1.org_name as receiptOrgName,'0' as pmulqty,"
                + " d.plu_name,gul.spec,f.uname as punitname,fw.uname as baseunitname,"
                + " h.goodswarehouse,h.materialwarehouse,i.warehouse_name as goodswarehousename,"
                + " j.warehouse_name as materialwarehousename,s.supplier_name,s.abbr,t.stockinallowtype,t.stockoutallowtype,"
                + " nvl(a.materal_type,'1') as materal_type,gr.groupno,gr.grouptype,nvl(gr.reachcount,0) as groupreachcount,"
                + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                + " gf.featureno,gflang.featurename,"
                + " gu.unitratio,t1.BIZPARTNERNO as sup,t1.SNAME as supname ,"
                + " goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE,c.price as standardPrice,c.category,cl.category_name as categoryname "
                + " from dcp_ptemplate a "
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                + " inner join dcp_goods c on a.eid=c.eid and b.pluno=c.pluno and c.status='100'"
                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.punit and gu.unit=c.baseunit"
                + " left  join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and  d.lang_type='" + langType + "' "
                + " left  join dcp_unit_lang f  on a.eid=f.eid and b.punit=f.unit and f.lang_type='" + langType + "' "
                + " left  join dcp_unit_lang fw on a.eid=fw.eid and c.baseunit=fw.unit and fw.lang_type='" + langType + "' "
                + " left  join dcp_ptemplate_detail_group gr on a.eid=gr.eid and b.porder_group=gr.groupno "
                + " left  join dcp_ptemplate_shop h on a.eid=h.eid and a.ptemplateno=h.ptemplateno and a.doc_type=h.doc_type and h.shopid='" + shopId + "' "
                + " left  join dcp_warehouse_lang i on a.eid=i.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='" + langType + "' "
                + " left  join dcp_warehouse_lang j on a.eid=j.eid and j.warehouse=h.materialwarehouse and j.organizationno=h.shopid and j.lang_type='" + langType + "'"
                + " left  join dcp_supplier_lang s  on a.eid=s.eid and a.supplier = s.supplier and s.lang_type = '" + langType + "'"
                + " left  join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100' "
                + " left  join DCP_BIZPARTNER t1 on a.eid=t1.eid and b.supplier=t1.BIZPARTNERNO  "
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='" + langType + "' "
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " left  join dcp_unit u on u.eid=a.eid and b.punit=u.unit"
                + " left  join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE'"
                + " left  join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='" + langType + "'"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                + "  inner join DCP_ORG_LANG f1 on f1.eid=a.eid and f1.ORGANIZATIONNO=a.receipt_org and f1.lang_type='"+langType+"' " 
                + " where a.eid= '" + eId + "' and a.doc_type= '" + docType + "' and a.ptemplateno = '" + pTemplateNO + "'"
                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')"
                + " order by b.item,gf.featureno "
                + " ");
        return sqlbuf.toString();
    }

    private String getSstockInSql(DCP_PTemplateDetailReq req,String companyId) {
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String docType = req.getRequest().getDocType();        //0.要货模板  1.盘点模板  2.加工模板  3.自采模板

        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
        StringBuffer sqlbuf=new StringBuffer();
        /*sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='" + companyId + "'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='" + shopId + "'"
                + " where a.eid='" + eId + "' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )");*/

        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        //sqlbuf.append(" with goodstemplate as ("
        //        + " select b.* from dcp_goodstemplate a"
        //        + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
         //       + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')  "
         //       + " )"
        //        + " ");

        String tempSql="SELECT * " +
                " FROM (" +
                "    SELECT a.TEMPLATETYPE,c.*," +
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
        sqlbuf.append(" with goodstemplate as ("+tempSql+")");

        sqlbuf.append(""
                + " select a.ptemplateno,a.pre_day, a.optional_time,a.taskway,'0' as pstockinqty,a.receipt_org,'' as receiptOrgName,"
                + " a.is_btake,ptemplate_name,b.item,b.pluno,b.punit,b.min_qty,b.mul_qty,b.max_qty,b.price as distriPrice,"
                + " d.plu_name,gul.spec,'0' as pmulqty,"
                + " lower(c.shortcut_code) as shortcut_code,f.uname as punitname,c.baseunit,fw.uname as baseunitname,a.time_type,a.time_value,"
                + " h.goodswarehouse,i.warehouse_name as goodswarehousename,h.materialwarehouse,j.warehouse_name as materialwarehousename,"
                + " a.supplier,s.supplier_name,s.abbr,nvl(a.cal_type,'1') as cal_type,nvl(a.materal_type,'1') as materal_type,"
                + " nvl(b.default_qty,0) as default_qty,c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,c.ISHOLIDAY,c.STOCKMANAGETYPE,c.ishotgoods,"
                + " image.listImage,"
                + " t.stockinallowtype,t.stockoutallowtype,a.is_adjust_stock,N'' as groupno,N'' as grouptype,'0' as groupreachcount,"
                + " a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,a.rdate_times,a.isaddgoods,a.isshowheadstockqty,"
                + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                + " gf.featureno,gflang.featurename,"
                + " gu.unitratio,goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE,c.price as standardPrice,c.category,cl.category_name as categoryname "
                + " from dcp_ptemplate a"
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                + " inner join dcp_goods c on b.pluno=c.pluno and b.eid=c.eid and c.status='100'"
                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.punit and gu.unit=c.baseunit"
                + " left  join dcp_goods_lang d  on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang f  on b.punit=f.unit and a.eid=f.eid and f.lang_type='" + langType + "'"
                + " left  join dcp_unit_lang fw  on c.baseunit=fw.unit and c.eid=fw.eid and fw.lang_type='" + langType + "'"
                + " left  join dcp_ptemplate_shop h on b.eid=h.eid and b.ptemplateno=h.ptemplateno and b.doc_type=h.doc_type and h.shopid='" + shopId + "'"
                + " left  join dcp_warehouse_lang i on i.eid=h.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='" + langType + "'"
                + " left  join dcp_warehouse_lang j on j.eid=h.eid and j.warehouse=h.materialwarehouse  and j.organizationno=h.shopid and j.lang_type='" + langType + "'"
                + " left  join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '" + langType + "'"
                + " left  join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='" + langType + "'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " left  join dcp_unit u on u.eid=a.eid and b.punit=u.unit and u.status='100'"
                + " left  join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE'"
                + " left  join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='" + langType + "'"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                + " where a.eid= '" + eId + "' and a.doc_type= '" + docType + "' and a.ptemplateno = '" + pTemplateNO + "' "
                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')"
                + " order by b.item,gf.featureno "
                + " ");
        return sqlbuf.toString();
    }

    private String getStockTakeSql(DCP_PTemplateDetailReq req,String companyId,String IsStockMultipleUnit,List<DataValue> lstDV) throws Exception {
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String docType = req.getRequest().getDocType();
        String pTemplateNO = req.getRequest().getpTemplateNo();
        StringBuffer sqlbuf=new StringBuffer();

        //处理==绑定变量SQL的写法
        if (lstDV==null) {
            lstDV = new ArrayList<>();
        }
        DataValue dv;

        //是否返回库存数
        String isrefBaseQty = req.getRequest().getIsrefBaseQty();
        if (Check.Null(isrefBaseQty) || !isrefBaseQty.equals("Y"))
            isrefBaseQty="N";
        //0.按商品盘点 1.按分类盘点
        String sql;
        String rangeWay=req.getRequest().getRangeWay();
        if (Check.Null(rangeWay)){
            sql="select  nvl(RANGEWAY,'0') as RANGEWAY from dcp_ptemplate a where a.eid='"+eId+"'"
                    + " and a.doc_type='"+docType+"' and a.ptemplateno='"+pTemplateNO+"' ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                rangeWay=getQData.get(0).get("RANGEWAY").toString();
            }else{
                rangeWay="0";
            }
        }
        //是否显示零库存商品  0不显示 1显示  BY JZMA 20200206
        String isShowZStock=req.getRequest().getIsShowZStock();
        if (Check.Null(isShowZStock) || !isShowZStock.equals("0"))
            isShowZStock="1";
        //是否启用批号管理
        String paraIsBatch= PosPub.getPARA_SMS(dao, eId, shopId, "Is_BatchNO");
        if (Check.Null(paraIsBatch) || !paraIsBatch.equals("Y"))
            paraIsBatch="N";
        //盘点时仓库取前端传入的值  BY JZMA 20190919
        String warehouse =req.getRequest().getWarehouse();
        if (Check.Null(warehouse))
            warehouse= req.getIn_cost_warehouse();
        //按商品盘点
        if (rangeWay.equals("0")){
            //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
            /*sqlbuf.append(" "
                    + " with goodstemplate as ("
                    + " select b.* from ("
                    + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                    + " from dcp_goodstemplate a"
                    + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id=? "
                    + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id=? "
                    + " where a.eid='"+eId+"' and a.status='100' "
                    + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                    + " ) a"
                    + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                    + " where a.rn=1 "
                    + " )"
                    + " ");*/

            //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
            // 商品模板表
            //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
            //sqlbuf.append(" with goodstemplate as ("
            //        + " select b.* from dcp_goodstemplate a"
             //       + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
            //        + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"',?)  "
            //        + " )"
            //        + " ");

            String tempSql="SELECT * " +
                    " FROM (" +
                    "    SELECT a.TEMPLATETYPE,c.*," +
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
            sqlbuf.append(" with goodstemplate as ("+tempSql+")");

           /* //?问号参数赋值处理
            dv=new DataValue(companyId,Types.VARCHAR);
            lstDV.add(dv);*/

            //?问号参数赋值处理
            dv=new DataValue(shopId,Types.VARCHAR);
            lstDV.add(dv);

            sqlbuf.append(""
                    + " select a.ptemplateno,lower(c.shortcut_code) as shortcut_code,a.pre_day,a.optional_time,"
                    + " a.taskway,'0' as pstockinqty,a.receipt_org,'' as receiptorgname,a.is_btake,ptemplate_name,"
                    + " b.item,b.pluno,b.punit,b.min_qty,b.mul_qty,b.max_qty,plu_name,gul.spec,'0' as pmulqty,"
                    + " f.uname as punitname,c.baseunit,fw.uname as baseunitname,a.time_type, a.time_value,"
                    + " h.goodswarehouse,i.warehouse_name as goodswarehousename,h.materialwarehouse,"
                    + " j.warehouse_name as materialwarehousename,"
                    + " a.supplier,s.supplier_name,s.abbr,nvl(a.cal_type,'1') as cal_type,nvl(a.materal_type,'1') as materal_type,"
                    + " nvl(b.default_qty,0) as default_qty,c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,c.ISHOLIDAY,c.STOCKMANAGETYPE,"
                    + " c.ishotgoods,"
                    + " image.listImage,"
                    + " t.stockinallowtype,t.stockoutallowtype,a.is_adjust_stock,N'' as groupno,N'' as grouptype,"
                    + " '0' as groupreachcount,a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,"
                    + " a.rdate_times,a.isshowzstock,a.isaddgoods,a.isshowheadstockqty,"
                    + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                    + " gf.featureno,gflang.featurename,"
                    + " gu.unitratio,goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE,c.price as standardPrice,c.category,cl.category_name as categoryname "
                    + " ");
            //盘点模板增加商品批号
            if (isrefBaseQty.equals("Y") || paraIsBatch.equals("Y") || isShowZStock.equals("0") ) {
                sqlbuf.append(" ,sk.stockbaseqty,sk.batchno,sk.proddate");
            }else{
                sqlbuf.append(" ,N'' as stockbaseqty,N'' as batchno,N'' as proddate");
            }

            //开启多单位盘点
            if (IsStockMultipleUnit.equals("Y")){
                sqlbuf.append(",unitlist.unitlist_unit,unitlist.unitlist_unitratio,unitlist.unitlist_unitudlength,unitlist.unitlist_unitname");
            }else{
                sqlbuf.append(",N'' as unitlist_unit,N'' as unitlist_unitratio,N'' as unitlist_unitudlength,N'' as unitlist_unitname");
            }

            sqlbuf.append(""
                    + " from dcp_ptemplate a"
                    //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                    + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                    //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                    + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                    + " inner join dcp_goods c on b.pluno=c.pluno and b.eid=c.eid  and c.status='100'"
                    + " left  join dcp_goods_lang d on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='"+langType+"' "
                    + " left  join dcp_unit_lang f  on b.punit=f.unit and a.eid=f.eid  and f.lang_type='"+langType+"' "
                    + " left  join dcp_unit_lang fw  on c.baseunit=fw.unit and c.eid=fw.eid and fw.lang_type='"+langType+"' "
                    + " left  join dcp_ptemplate_shop h on b.eid=h.eid and b.ptemplateno=h.ptemplateno and b.doc_type=h.doc_type and h.shopid=? "
                    + " left  join dcp_warehouse_lang i on i.eid=h.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='"+langType+"' "
                    + " left  join dcp_warehouse_lang j on j.eid=h.eid and j.warehouse=h.materialwarehouse  and j.organizationno=h.shopid and j.lang_type='"+langType+"' "
                    + " left  join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '"+langType+"' "
                    + " left  join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100'"
                    + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"' "
                    + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
                    + " left  join dcp_unit u on u.eid=a.eid and b.punit=u.unit and u.status='100' and u.status='100'"
                    + " left  join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE'"
                    + " left  join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='"+langType+"' "
                    + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.punit and gu.unit=c.baseunit"
                    + " left  join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                    + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                    + " ");


            //?问号参数赋值处理
            dv=new DataValue(shopId,Types.VARCHAR);
            lstDV.add(dv);

            if (isrefBaseQty.equals("Y") || paraIsBatch.equals("Y") || isShowZStock.equals("0")) {
                // 库存为零的不显示
                if (isShowZStock.equals("0")) {
                    sqlbuf.append("INNER JOIN ( ");
                } else {
                    sqlbuf.append("LEFT JOIN ( ");
                }

                if (paraIsBatch.equals("Y")){
                    sqlbuf.append( " select a.pluno,a.featureno,sum(a.baseqty) as stockbaseqty,a.batchno,max(a.proddate) as proddate ");
                }else{
                    sqlbuf.append( " select a.pluno,a.featureno,sum(a.baseqty) as stockbaseqty,N'' as batchno,N'' as proddate ");
                }

                sqlbuf.append(""
                        + " from ("
                        + " select a.pluno,a.featureno,a.qty as baseqty,a.batchno,a.proddate from DCP_Stock_Day a"
                        + " where a.EID='"+eId+"' and a.organizationno=? and a.warehouse =? "
                        + " union all"
                        + " select a.pluno,a.featureno,stocktype*baseqty as baseqty,a.batchno,a.proddate from DCP_STOCK_DETAIL a"
                        + " where a.EID='"+eId+"' and a.organizationno=? and a.warehouse =? "
                        + " and a.billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42' )");

                //?问号参数赋值处理
                dv=new DataValue(shopId,Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(warehouse,Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(shopId,Types.VARCHAR);
                lstDV.add(dv);
                //?问号参数赋值处理
                dv=new DataValue(warehouse,Types.VARCHAR);
                lstDV.add(dv);
                // IF 启用批号 group by trim(a.batch_no) 会返回库存数不等于零的所有批号
                if (paraIsBatch.equals("Y")) {
                    sqlbuf.append(" ) a group by a.pluno,a.featureno,a.batchno");
                }else {
                    sqlbuf.append(" ) a group by a.pluno,a.featureno");
                }
                sqlbuf.append(""
                        + " ) sk on sk.pluno=b.pluno and (sk.featureno=nvl(gf.featureno,' ')) and sk.stockbaseqty<>'0' "
                        + " ");
            }


            //【ID1019116】【霸王餐饮】茶颜悦色的个案功能移植到3.0-盘点时候支持多单位盘点（后端服务） by jinzma 20210719
            if (IsStockMultipleUnit.equals("Y")){
                sqlbuf.append(""
                        + " left join ("
                        + " select a.pluno,a.ounit as unitlist_unit,a.unit,a.unitratio as unitlist_unitratio,"
                        + " b.udlength as unitlist_unitudlength,"
                        + " c.uname as unitlist_unitname"
                        + " from dcp_goods_unit a"
                        + " left join dcp_unit b on a.eid=b.eid and a.ounit=b.unit and b.status='100'"
                        + " left join dcp_unit_lang c on a.eid=c.eid and a.ounit=c.unit and c.lang_type='"+langType+"'"
                        + " where a.eid='"+eId+"' and a.cunit_use='Y'"
                        + " ) unitlist on unitlist.pluno = b.pluno and unitlist.unit=c.baseunit"
                        + "");
            }

            sqlbuf.append(""
                    + " where a.eid= '"+eId+"' and a.doc_type= ? and a.ptemplateno =? "
                    //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                    + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')");

            //多单位盘点按照单位编号排序
            if (IsStockMultipleUnit.equals("Y")){
                sqlbuf.append(" order by b.item,unitlist.unitlist_unit");
            }else{
                //+ " order by b.pluno,gf.featureno "    //盘点模板依据PLUNO排序 BY JZMA 20190226 麦都需求
                sqlbuf.append(" order by b.item");   //商品顺序由原来的pluno改为item 嘉华需求
            }

            //?问号参数赋值处理
            dv=new DataValue(docType,Types.VARCHAR);
            lstDV.add(dv);
            //?问号参数赋值处理
            dv=new DataValue(pTemplateNO,Types.VARCHAR);
            lstDV.add(dv);

        } else if (rangeWay.equals("1")){   //按商品分类盘点
            String getSql = " select * from dcp_category where eid='"+eId+"' and status='100' and category<>up_category ";
            List<Map<String, Object>> allDatas = this.doQueryData(getSql, null);
            if(allDatas != null && !allDatas.isEmpty()) {
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                getSql=" select categoryno from dcp_ptemplate_detail "
                        + " where eid='"+eId+"' and ptemplateno='"+pTemplateNO+"' and doc_type='1' and status='100' ";
                List<Map<String, Object>> datas = this.doQueryData(getSql, null);
                if(datas != null && !datas.isEmpty()) {
                    for (Map<String, Object> oneData : datas) {
                        String categoryNo = oneData.get("CATEGORYNO").toString();
                        if (Check.Null(categoryNo)) continue;
                        setChildrenDatas(categoryNo,allDatas);
                    }
                    if (!Check.Null(getCategory)) {
                        getCategory = getCategory.substring(0,getCategory.length()-1);
                        getCategory = "("+getCategory+")";
                        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                       /* sqlbuf.append(" "
                                + " with goodstemplate as ("
                                + " select b.* from ("
                                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                                + " from dcp_goodstemplate a"
                                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id=? "
                                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id=? "
                                + " where a.eid='"+eId+"' and a.status='100' "
                                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                                + " ) a"
                                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                                + " where a.rn=1 "
                                + " )"
                                + " ");*/

                        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
                        // 商品模板表
                        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
                        sqlbuf.append(" with goodstemplate as ("
                                + " select b.* from dcp_goodstemplate a"
                                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"',?)  "
                                + " )"
                                + " ");

                      /*  //?问号参数赋值处理
                        dv=new DataValue(companyId,Types.VARCHAR);
                        lstDV.add(dv);*/

                        //?问号参数赋值处理
                        dv=new DataValue(shopId,Types.VARCHAR);
                        lstDV.add(dv);

                        sqlbuf.append(""
                                + " select a.ptemplateno,lower(b.shortcut_code) as shortcut_code,a.pre_day,a.optional_time,a.taskway,"
                                + " '0' as pstockinqty ,a.receipt_org,'' as receiptorgname,a.is_btake,ptemplate_name,"
                                + " dense_rank() over(partition by a.eid order by b.pluno,gf.featureno) as item,"
                                //+ " rownum item,"
                                + " b.pluno,b.cunit as punit,'0' as min_qty,image.listimage,'0' as mul_qty,'0' as max_qty,b.ishotgoods,"
                                + " d.plu_name,gul.spec,'0' as pmulqty,f.uname as punitname,b.baseunit,fw.uname as baseunitname,"
                                + " a.time_type,a.time_value,h.goodswarehouse,i.warehouse_name as goodswarehousename,h.materialwarehouse,"
                                + " j.warehouse_name as materialwarehousename,a.supplier,s.supplier_name,s.abbr,"
                                + " nvl(materal_type,'1') as materal_type,'0' as default_qty,b.isbatch,b.stockinvalidday,"
                                + " b.stockoutvalidday,b.shelflife,b.ISHOLIDAY,t.stockinallowtype,t.stockoutallowtype,a.is_adjust_stock,"
                                + " N'' as groupno,N'' as grouptype,'0' as groupreachcount,nvl(a.cal_type,'1') as cal_type,"
                                + " a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,a.rdate_times,"
                                + " a.isshowzstock,a.isaddgoods,a.isshowheadstockqty,u.udlength as punit_udlength,"
                                + " gf.featureno,gflang.featurename,buludlength.udlength as baseunitudlength,"
                                + " gu.unitratio,goodstemplate.isnewgoods,b.STOCKMANAGETYPE,goodstemplate.warningqty,a.SUPPLIERTYPE,b.price as standardPrice,b.category,cl.CATEGORY_NAME as categoryname  "
                                + " ");

                        //盘点模板增加商品批号
                        if (isrefBaseQty.equals("Y") || paraIsBatch.equals("Y") || isShowZStock.equals("0") ) {
                            sqlbuf.append(" ,sk.stockbaseqty,sk.batchno,sk.proddate");
                        }else{
                            sqlbuf.append(" ,N'' as stockbaseqty,N'' as batchno,N'' as proddate");
                        }
                        //开启多单位盘点
                        if (IsStockMultipleUnit.equals("Y")){
                            sqlbuf.append(",unitlist.unitlist_unit,unitlist.unitlist_unitratio,unitlist.unitlist_unitudlength,unitlist.unitlist_unitname");
                        }else{
                            sqlbuf.append(",N'' as unitlist_unit,N'' as unitlist_unitratio,N'' as unitlist_unitudlength,N'' as unitlist_unitname");
                        }

                        sqlbuf.append(""
                                + " from dcp_ptemplate a"
                                + " left join dcp_goods b on a.eid=b.eid and b.status='100' and b.CATEGORY in "+getCategory+" "
                                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                                + " left join dcp_goods_lang d on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='"+langType+"' "
                                + " left join dcp_unit_lang f on b.cunit=f.unit and a.eid=f.eid and f.lang_type='"+langType+"' "
                                + " left join dcp_unit_lang fw on b.baseunit=fw.unit and a.eid=fw.eid and fw.lang_type='"+langType+"' "
                                + " left join dcp_ptemplate_shop h on h.eid=a.eid and a.ptemplateno=h.ptemplateno and a.doc_type=h.doc_type and h.shopid=? "
                                + " left join dcp_warehouse_lang i on i.eid=a.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='"+langType+"' "
                                + " left join dcp_warehouse_lang j on j.eid=a.eid and j.warehouse=h.materialwarehouse and j.organizationno=h.shopid and j.lang_type='"+langType+"' "
                                + " left join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '"+langType+"' "
                                + " left join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100'"
                                + " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.cunit=gul.ounit and gul.lang_type='"+langType+"' "
                                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
                                + " left join dcp_unit u on u.eid=a.eid and b.cunit=u.unit and u.status='100'"
                                + " left join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and b.plutype='FEATURE'"
                                + " left join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='"+langType+"' "
                                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.cunit and gu.unit=b.baseunit"
                                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and b.baseunit=buludlength.unit"
                                + " left join DCP_CATEGORY_LANG  cl on cl.category=b.category and cl.eid=b.eid and cl.lang_type='"+langType+"' "

                                + " ");


                        //?问号参数赋值处理
                        dv=new DataValue(shopId,Types.VARCHAR);
                        lstDV.add(dv);

                        if (isrefBaseQty.equals("Y") || paraIsBatch.equals("Y") || isShowZStock.equals("0") ) {
                            // 库存为零的不显示
                            if (isShowZStock.equals("0")) {
                                sqlbuf.append("INNER JOIN ( ");
                            }else {
                                sqlbuf.append("LEFT JOIN ( ");
                            }

                            if (paraIsBatch.equals("Y")){
                                sqlbuf.append( " select a.pluno,a.featureno,sum(a.baseqty) as stockbaseqty,a.batchno,max(a.proddate) as proddate ");
                            }else {
                                sqlbuf.append( " select a.pluno,a.featureno,sum(a.baseqty) as stockbaseqty,N'' as batchno,N'' as proddate ");
                            }

                            sqlbuf.append(""
                                    + " from ("
                                    + " select a.pluno,a.featureno,a.qty as baseqty,a.batchno,a.proddate from DCP_Stock_Day a"
                                    + " where a.EID='"+eId+"' and a.organizationno=? and a.warehouse =? "
                                    + " union all"
                                    + " select a.pluno,a.featureno,stocktype*baseqty as baseqty,a.batchno,a.proddate from DCP_STOCK_DETAIL a"
                                    + " where a.EID='"+eId+"' and a.organizationno=? and a.warehouse =? "
                                    + " and a.billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42' )");

                            //?问号参数赋值处理
                            dv=new DataValue(shopId,Types.VARCHAR);
                            lstDV.add(dv);
                            //?问号参数赋值处理
                            dv=new DataValue(warehouse,Types.VARCHAR);
                            lstDV.add(dv);
                            //?问号参数赋值处理
                            dv=new DataValue(shopId,Types.VARCHAR);
                            lstDV.add(dv);
                            //?问号参数赋值处理
                            dv=new DataValue(warehouse,Types.VARCHAR);
                            lstDV.add(dv);
                            // IF 启用批号 group by trim(a.batch_no) 会返回库存数不等于零的所有批号
                            if (paraIsBatch.equals("Y")) {
                                sqlbuf.append(" ) a group by a.pluno,a.featureno,a.batchno");
                            }else{
                                sqlbuf.append(" ) a group by a.pluno,a.featureno");
                            }

                            sqlbuf.append(" ) sk on sk.pluno=b.pluno and (sk.featureno=nvl(gf.featureno,' ')) and sk.stockbaseqty<>'0' ");
                        }

                        //【ID1019116】【霸王餐饮】茶颜悦色的个案功能移植到3.0-盘点时候支持多单位盘点（后端服务） by jinzma 20210719
                        if (IsStockMultipleUnit.equals("Y")){
                            sqlbuf.append(""
                                    + " left join ("
                                    + " select a.pluno,a.ounit as unitlist_unit,a.unit,a.unitratio as unitlist_unitratio,"
                                    + " b.udlength as unitlist_unitudlength,"
                                    + " c.uname as unitlist_unitname"
                                    + " from dcp_goods_unit a"
                                    + " left join dcp_unit b on a.eid=b.eid and a.ounit=b.unit and b.status='100'"
                                    + " left join dcp_unit_lang c on a.eid=c.eid and a.ounit=c.unit and c.lang_type='"+langType+"'"
                                    + " where a.eid='"+eId+"' and a.cunit_use='Y'"
                                    + " ) unitlist on unitlist.pluno = b.pluno and unitlist.unit=b.baseunit"
                                    + "");
                        }

                        sqlbuf.append(""
                                + " where a.eid= '"+eId+"' and a.doc_type= ? and a.ptemplateno =? "
                                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                                + " and ((b.plutype='FEATURE' and gf.pluno is not null) or b.plutype<>'FEATURE')");
                        //多单位盘点按照单位编号排序
                        if (IsStockMultipleUnit.equals("Y")){
                            sqlbuf.append(" order by b.pluno,gf.featureno,unitlist.unitlist_unit");
                        }else{
                            sqlbuf.append("order by b.pluno,gf.featureno");   //盘点模板依据PLUNO排序 BY JZMA 20190226 麦都需求
                        }

                        //?问号参数赋值处理
                        dv=new DataValue(docType,Types.VARCHAR);
                        lstDV.add(dv);
                        //?问号参数赋值处理
                        dv=new DataValue(pTemplateNO,Types.VARCHAR);
                        lstDV.add(dv);
                    }else{
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "分类末极资料不存在 ");
                    }
                }else{
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板单身资料为空 ");
                }
            }else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "分类基本资料未维护 ");
            }
        }
        return sqlbuf.toString();
    }

    private String getPstockInSql(DCP_PTemplateDetailReq req,String companyId ) {
        String sql;
        String docType = req.getRequest().getDocType();        //0.要货模板  1.盘点模板  2.加工模板  3.自采模板
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String pTemplateNO = req.getRequest().getpTemplateNo();

        StringBuffer sqlbuf=new StringBuffer();
        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
       /* sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/

        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlbuf.append(" with goodstemplate as ("
                + " select * from ("
                + " select  row_number() over (partition by b.pluno order by c.RANGETYPE desc ) as rn, b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'" +
                " left join DCP_GOODSTEMPLATE_RANGE c on a.eid=c.eid and a.templateid=c.templateid "
                + " where a.eid='"+eId+"' and (c.id='"+req.getOrganizationNO()+"' or c.id='"+req.getBELFIRM()+"') " //and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')
                + "  ) a where a.rn=1"
                + " )"
                + " ");


        sqlbuf.append(" ,bom as ("
                //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609  // distinct a.pluno
                + " select * from ("
                + " select row_number() over (partition by a.pluno,a.unit order by restrictshop desc) as rn,a.pluno,a.unit,a.mulqty "
                + " from dcp_bom a "
                + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"' "
                + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                + " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(c.material_edate)>=trunc(sysdate)"
                + " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y'"
                + " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and status='100' and a.bomtype = '0' "
                + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))"
                + "  ) a where a.rn=1"
                + " )"
                + " ");

        sqlbuf.append(""
                + " select a.ptemplateno,lower(c.shortcut_code) as shortcut_code,a.pre_day,a.optional_time,a.taskway,"
                + " '0' as pstockinqty,a.receipt_org,'' as receiptorgname,a.is_btake,ptemplate_name,b.item,b.pluno,"
                //加工模板的单位取商品表的生产单位,模板明细中的单位暂时作废
                + " c.prod_unit as punit,"
                //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609  //b.mul_qty,
                + " b.min_qty,bom.mulqty as mul_qty,b.max_qty,d.plu_name,gul.spec,'0' as pmulqty,f.uname as punitname,c.baseunit,"
                + " fw.uname as baseunitname,a.time_type,a.time_value,h.goodswarehouse,i.warehouse_name as goodswarehousename,"
                + " h.materialwarehouse,j.warehouse_name as materialwarehousename,a.supplier,s.supplier_name,s.abbr,"
                + " nvl(a.cal_type,'1') as cal_type,nvl(materal_type,'1') as materal_type,nvl(b.default_qty,0) as default_qty,"
                + " c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,c.ISHOLIDAY,c.STOCKMANAGETYPE,c.ishotgoods,"
                + " image.listimage,t.stockinallowtype,"
                + " t.stockoutallowtype,a.is_adjust_stock,N'' as groupno,N'' as grouptype,'0' as groupreachcount,"
                + " a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,a.rdate_times,a.isshowzstock,"
                + " a.isaddgoods,a.isshowheadstockqty,"
                + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                + " gf.featureno,gflang.featurename,"
                + " gu.unitratio,goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE"
                + " from dcp_ptemplate a "
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                + " inner join dcp_goods c on b.pluno=c.pluno and b.eid=c.eid and c.status='100' and c.pluno=b.pluno"
                + " left  join dcp_goods_lang d on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='"+ langType +"' "
                //加工模板需要关联BOM
                //BOM生产倍量（mulqty）取值错误修正 by jinzma 20220609
                + " inner join bom on bom.pluno=b.pluno and bom.unit=c.prod_unit"
                //加工模板的单位取商品表的生产单位,模板明细中的单位暂时作废
                + " left  join dcp_unit_lang f on c.prod_unit=f.unit and a.eid=f.eid and f.lang_type='"+ langType +"' "
                + " left join dcp_unit_lang fw  on c.baseunit=fw.unit and c.eid=fw.eid and fw.lang_type='"+ langType +"'"
                + " left join dcp_ptemplate_shop h on b.eid=h.eid and b.ptemplateno=h.ptemplateno and b.doc_type=h.doc_type and h.shopid='"+shopId+"' "
                + " left join dcp_warehouse_lang i on i.eid=h.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='"+ langType +"'"
                + " left join dcp_warehouse_lang j on j.eid=h.eid and j.warehouse=h.materialwarehouse and j.organizationno=h.shopid and j.lang_type='"+ langType +"' "
                + " left join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '"+langType+"' "
                + " left join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100' "
                + " left join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and c.prod_unit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL' "
                + " left join dcp_unit u on u.eid=a.eid and c.prod_unit=u.unit and u.status='100'"
                + " left join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE'"
                + " left join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='"+langType+"'"
                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=c.prod_unit and gu.unit=c.baseunit"
                + " left join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                + " where a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"' and a.ptemplateno = '"+ pTemplateNO +"'"
                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')"
                + " order by b.item,gf.featureno"
                + " ");

        sql = sqlbuf.toString();
        return sql;
    }

    private String getStockOutSql(DCP_PTemplateDetailReq req,String companyId) {
        String sql;
        String docType = req.getRequest().getDocType();        //0.要货模板 1.盘点模板 2.加工模板 3.自采模板 5.调拨模板
        String eId = req.geteId();
        String shopId= req.getShopId();
        String langType=req.getLangType();
        String pTemplateNO = req.getRequest().getpTemplateNo();

        StringBuffer sqlbuf=new StringBuffer();

        //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
       /* sqlbuf.append(" "
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where b.isallot='Y' and a.rn=1 "
                + " )"
                + " ");*/

        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
        // 商品模板表
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        //sqlbuf.append(" with goodstemplate as ("
        //        + " select b.* from dcp_goodstemplate a"
       //         + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
        //        + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')  "
         //       + " )"
        //        + " ");

        String tempSql="SELECT * " +
                " FROM (" +
                "    SELECT a.TEMPLATETYPE,c.*," +
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
        sqlbuf.append(" with goodstemplate as ("+tempSql+")");

        sqlbuf.append(""
                + " select a.ptemplateno,lower(c.shortcut_code) as shortcut_code,a.pre_day,a.optional_time,a.taskway,"
                + " '0' as pstockinqty,a.receipt_org,'' as receiptorgname,a.is_btake,ptemplate_name,b.item,b.pluno,b.punit,b.min_qty,"
                + " b.mul_qty,b.max_qty,d.plu_name,gul.spec,image.listimage,'0' as pmulqty,f.uname as punitname,c.baseunit,c.ishotgoods,"
                + " fw.uname as baseunitname,a.time_type, a.time_value,a.supplier,s.supplier_name,s.abbr,"
                + " h.goodswarehouse,i.warehouse_name as goodswarehousename,h.materialwarehouse,j.warehouse_name as materialwarehousename,"
                + " nvl(a.cal_type,'1') as cal_type,nvl(materal_type,'1') as materal_type,nvl(b.default_qty,0) as default_qty,"
                + " c.isbatch,c.stockinvalidday,c.stockoutvalidday,c.shelflife,c.ISHOLIDAY,c.STOCKMANAGETYPE,t.stockinallowtype,t.stockoutallowtype,a.is_adjust_stock,"
                + " N'' as groupno,N'' as grouptype,'0' as groupreachcount,a.rdate_type,a.rdate_add,a.rdate_values,a.revoke_day,a.revoke_time,"
                + " a.rdate_times,a.isshowzstock,a.isaddgoods,a.isshowheadstockqty,"
                + " u.udlength as punit_udlength,buludlength.udlength as baseunitudlength,"
                + " gf.featureno,gflang.featurename,"
                + " gu.unitratio,goodstemplate.isnewgoods,goodstemplate.warningqty,a.SUPPLIERTYPE,c.price as standardPrice,c.category,cl.category_name as categoryname "
                + " from dcp_ptemplate a"
                //ID 20240517006 亮点3404，要货模板失效的产品，门店管理端仍然可以要货 by jinzma add status='100'
                + " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.status='100' "
                //模板查询关联商品模板 【ID1016422】【大连大万3.0】移动门店选择模板要货  by jinzma 20210330
                + " inner join goodstemplate on goodstemplate.eid=b.eid and goodstemplate.pluno=b.pluno"
                + " inner join dcp_goods c on b.pluno=c.pluno and b.eid=c.eid  and c.status='100'"
                + " left join dcp_goods_lang d  on b.pluno=d.pluno and b.eid=d.eid and d.lang_type='"+ langType +"'"
                + " left join dcp_unit_lang f  on b.punit=f.unit and a.eid=f.eid and f.lang_type='"+ langType +"'"
                + " left join dcp_unit_lang fw  on c.baseunit=fw.unit and c.eid=fw.eid and fw.lang_type='"+ langType +"'"
                + " left join dcp_ptemplate_shop h on b.eid=h.eid and b.ptemplateno=h.ptemplateno and b.doc_type=h.doc_type and h.shopid='"+shopId+"' "
                + " left join dcp_warehouse_lang i on i.eid=h.eid and i.warehouse=h.goodswarehouse and i.organizationno=h.shopid and i.lang_type='"+ langType +"'"
                + " left join dcp_warehouse_lang j on j.eid=h.eid and j.warehouse=h.materialwarehouse  and j.organizationno=h.shopid and j.lang_type='"+ langType +"'"
                + " left join dcp_supplier_lang s on a.eid = s.eid and a.supplier = s.supplier and s.lang_type = '"+langType+"'"
                + " left join dcp_supplier t on a.eid=t.eid and a.supplier=t.supplier and t.status='100'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and b.pluno=gul.pluno and b.punit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=b.pluno and image.apptype='ALL'"
                + " left  join dcp_unit u on u.eid=a.eid and b.punit=u.unit and u.status='100'"
                + " left  join dcp_goods_feature gf on gf.eid=a.eid and gf.pluno=b.pluno and gf.status='100' and c.plutype='FEATURE' "
                + " left  join dcp_goods_feature_lang gflang on gflang.eid=a.eid and gflang.pluno=b.pluno and gflang.featureno=gf.featureno and gflang.lang_type='"+langType+"'"
                + " inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=b.pluno and gu.ounit=b.punit and gu.unit=c.baseunit"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and c.baseunit=buludlength.unit"
                + " left join DCP_CATEGORY_LANG  cl on cl.category=c.category and cl.eid=c.eid and cl.lang_type='"+langType+"' "
                + " where a.eid= '"+ eId +"' and a.doc_type= '"+ docType +"' and a.ptemplateno = '"+ pTemplateNO +"'"
                //【ID1018894】【3.0货郎】盘点单无法提交 by jinzma 20210701
                + " and ((c.plutype='FEATURE' and gf.pluno is not null) or c.plutype<>'FEATURE')"
                + " order by b.pluno,gf.featureno"
                + " ");
        
        sql = sqlbuf.toString();
        return sql;
        
    }
    
    
    /**
     * 循环添加分类层级
     */
    private void setChildrenDatas(String categoryNo,List<Map<String, Object>> allDatas) {
        try {
            List<Map<String, Object>> nextDatas  = getChildDatas(allDatas,categoryNo);
            if(nextDatas != null && !nextDatas.isEmpty()) {
                for (Map<String, Object> datas : nextDatas) {
                    String category = datas.get("CATEGORY").toString();
                    setChildrenDatas(category,allDatas);
                }
            }else {
                //最末极分类，需要添加
                getCategory = getCategory +"'"+ categoryNo+"'," ;
            }
        }catch (Exception ignored) {
        }
    }
    
    /**
     * 获取下一层级信息
     */
    private List<Map<String, Object>> getChildDatas (List<Map<String, Object>> allDatas,String categoryNo){
        List<Map<String, Object>> datas =new ArrayList<>();
        for (Map<String, Object> map : allDatas) {
            if(map.get("UP_CATEGORY").toString().equals(categoryNo)) {
                datas.add(map);
            }
        }
        return datas;
    }
    
}










