package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dsc.spos.json.cust.req.DCP_GoodsBarcodeQueryReq;
import com.dsc.spos.json.cust.req.DCP_GoodsBarcodeQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsBarcodeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @apiNote 商品开窗-条码
 * @author jinzma
 * @since  2020-08-18
 */
public class DCP_GoodsBarcodeQuery extends SPosBasicService<DCP_GoodsBarcodeQueryReq,DCP_GoodsBarcodeQueryRes>{
    Logger logger = LogManager.getLogger(DCP_GoodsBarcodeQuery.class.getName());
    @Override
    protected boolean isVerifyFail(DCP_GoodsBarcodeQueryReq req) throws Exception {
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GoodsBarcodeQueryReq> getRequestType() {
        return new TypeToken<DCP_GoodsBarcodeQueryReq>(){};
    }
    
    @Override
    protected DCP_GoodsBarcodeQueryRes getResponseType() {
        return new DCP_GoodsBarcodeQueryRes();
    }
    
    @Override
    protected DCP_GoodsBarcodeQueryRes processJson(DCP_GoodsBarcodeQueryReq req) throws Exception {
        DCP_GoodsBarcodeQueryRes res = this.getResponse();
        try
        {
            //不传条件，就直接返回空，查全部商品，量太大超时，没必要。。。拦截掉
            if (req.getRequest()==null
                    ||(req.getRequest().getKeyTxt()==null
                    &&req.getRequest().getBillType()==null
                    &&req.getRequest().getBillNo()==null
                    &&req.getRequest().getSupplierId()==null
                    &&req.getRequest().getPluBarcodeList()==null
                    &&req.getRequest().getCategory()==null
                    &&req.getRequest().getBrand()==null
                    &&req.getRequest().getSeries()==null
                    &&req.getRequest().getAttrGroup()==null
                    &&req.getRequest().getAttrValue()==null
                    &&req.getRequest().getStockTakeNo()==null
                    &&req.getRequest().getFastImport()==null
                    &&req.getRequest().getIsHotGoods()==null
                    &&req.getRequest().getIsNewGoods()==null
                    &&req.getRequest().getISHTTPS()==null
                    &&req.getRequest().getDomainName()==null
                    &&req.getRequest().getCheckSuppGoods()==null
                    &&req.getRequest().getIsStockMultipleUnit()==null
                    &&req.getRequest().getSelfBuiltShopId()==null
                    &&req.getRequest().getSearchScope()==null
            
            ))
            {
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功！");
                res.setDatas(new ArrayList<>());
                res.setIllegalDatas(new ArrayList<>());
                return res;
            }
            
            String shopId = req.getShopId();
            String eId = req.geteId();
            String companyId = req.getBELFIRM();
            String orgForm = req.getOrg_Form();
            String queryStockqty=req.getRequest().getQueryStockqty();
            String queryStockWarehouse=req.getRequest().getQueryStockWarehouse();
            String fastImport = req.getRequest().getFastImport();  //急速导入，不查询单价和进货价
            if (Check.Null(fastImport))
                fastImport = "N";
            String billType = req.getRequest().getBillType();  //0-要货申请 6-计划报单 7-可退仓 9-盘点单 12-盘点子任务 14调拨出库单
            if (Check.Null(billType))
                billType = "-1"; //塞默认值
            //String supplierId = req.getRequest().getSupplierId();
            //if (Check.Null(supplierId)) {
            //    supplierId = "";
            //}
            if (Check.Null(companyId)) {
                ///组织类型 0-公司  1-组织  2-门店 3-其它
                if (orgForm.equals("0")){
                    companyId=shopId;
                }else {
                    String sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                    List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                    companyId = getQData.get(0).get("BELFIRM").toString();
                }
            }
            
            //是否启用盘点多单位盘点：Y是，N否，盘点作业
           /* String IsStockMultipleUnit = req.getRequest().getIsStockMultipleUnit();
            if (Check.Null(IsStockMultipleUnit) && billType.equals("9")) {  //仅盘点才需查询 by jinzma 20210719
                IsStockMultipleUnit = PosPub.getPARA_SMS(dao, eId, shopId, "IsStockMultipleUnit");
            }
            if (Check.Null(IsStockMultipleUnit)) {
                IsStockMultipleUnit = "N";
            }*/
            
            //查詢資料
            int totalRecords;         //总笔数
            int totalPages;	          //总页数
            String sql = this.getQBarcodeSql(req,companyId,billType,orgForm);
            //logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"条码SQL:"+sql+"\r\n");
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            res.setDatas(new ArrayList<>());
            res.setIllegalDatas(new ArrayList<>());
            
            //处理未查询到的条码,数据量大的时候，直接SQL查询失效的条码，不再走循环判断
            List<level1Elm> pluBarcodes = req.getRequest().getPluBarcodeList();
            if (pluBarcodes !=null && !pluBarcodes.isEmpty()) {
                if (fastImport.equals("Y") || pluBarcodes.size()>=6000) {
                    sql = this.getQIllegalBarcodeSql(req, sql);
                    List<Map<String, Object>> getQBarcode = this.doQueryData(sql, null);
                    for (Map<String, Object> oneBarcode : getQBarcode) {
                        String pluBarcode = oneBarcode.get("PLUBARCODE").toString();
                        if (!Check.Null(pluBarcode)) {
                            DCP_GoodsBarcodeQueryRes.illegal oneIllegal = res.new illegal();
                            oneIllegal.setPluBarcode(pluBarcode);
                            res.getIllegalDatas().add(oneIllegal);
                        }
                    }
                }else{
                    Map<String, Object> condiV= new HashMap<>();
                    for(level1Elm par :pluBarcodes) {
                        String pluBarcode = par.getPluBarcode();
                        condiV.put("PLUBARCODE",pluBarcode);
                        List<Map<String, Object>> pluBarcodeList= MapDistinct.getWhereMap(getQData, condiV, false);
                        if(pluBarcodeList ==null || pluBarcodeList.isEmpty() ) {
                            DCP_GoodsBarcodeQueryRes.illegal oneIllegal = res.new illegal();
                            oneIllegal.setPluBarcode(pluBarcode);
                            res.getIllegalDatas().add(oneIllegal);
                        }
                        condiV.clear();
                    }
                }
            }
            
            if (getQData != null && !getQData.isEmpty()) {
                // 拼接返回图片路径  by jinzma 20210705
                String isHttps = req.getRequest().getISHTTPS();
                if (Check.Null(isHttps)){
                    isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
                }
                String httpStr = isHttps.equals("1")?"https://":"http://";
                String domainName = req.getRequest().getDomainName();
                if (Check.Null(domainName)){
                    domainName = PosPub.getPARA_SMS(dao, eId, "", "DomainName");
                }
                if (domainName.endsWith("/")) {
                    domainName = httpStr + domainName + "resource/image/";
                }else{
                    domainName = httpStr + domainName + "/resource/image/";
                }
                
                ///【ID1028383】[货郎3.0]要货单上传报错，提示如图，查了商品340600061在DCP_GOODS是设置了要货价，
                // 在要货单YHSQ2022090300001中进货价是0  BY JZMA 20220909
                // 取零售价和供货价的小数位数参数
                String priceLength = PosPub.getPARA_SMS(StaticInfo.dao,eId,shopId,"priceLength");
                if (Check.Null(priceLength)||!PosPub.isNumeric(priceLength)){
                    priceLength="2";
                }
                int priceLengthInt = Integer.parseInt(priceLength);
                
                String distriPriceLength = PosPub.getPARA_SMS(StaticInfo.dao,eId,shopId,"distriPriceLength");
                if (Check.Null(distriPriceLength)||!PosPub.isNumeric(distriPriceLength)){
                    distriPriceLength="2";
                }
                int distriPriceLengthInt = Integer.parseInt(distriPriceLength);
                
                
                
                //算總頁數
                String num = getQData.get(0).get("NUM").toString();
                totalRecords=Integer.parseInt(num);
                totalPages = totalRecords / req.getPageSize();
                totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                
                List<Map<String, Object>> getPrice = new ArrayList<>();
                if (fastImport.equals("N")) {
                    //商品取价计算
                    List<Map<String, Object>> plus = new ArrayList<>();
                    Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                    condition.put("PLUNO", true);
                    List<Map<String, Object>> getQPlu = MapDistinct.getMap(getQData, condition);
                    for (Map<String, Object> onePlu : getQPlu) {
                        Map<String, Object> plu = new HashMap<>();
                        plu.put("PLUNO", onePlu.get("PLUNO").toString());
                        plu.put("PUNIT", onePlu.get("UNIT").toString());
                        plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
                        plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
                        plus.add(plu);
                    }
                    MyCommon mc = new MyCommon();
                    getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, plus, companyId);
                }
                
                for (Map<String, Object> oneData : getQData) {
                    DCP_GoodsBarcodeQueryRes.level1Elm oneLv1 = new DCP_GoodsBarcodeQueryRes().new level1Elm();
                    String pluBarcode = oneData.get("PLUBARCODE").toString();
                    String pluNo = oneData.get("PLUNO").toString();
                    String pluName = oneData.get("PLU_NAME").toString();
                    String pluType = oneData.get("PLUTYPE").toString();
                    String spec = oneData.get("SPEC").toString();
                    String featureNo = oneData.get("FEATURENO").toString();
                    String featureName = oneData.get("FEATURENAME").toString();
                    String category = oneData.get("CATEGORY").toString();
                    String categoryName = oneData.get("CATEGORY_NAME").toString();
                    String warmType = oneData.get("WARMTYPE").toString();
                    String virtual = oneData.get("VIRTUAL").toString();
                    String stockManageType = oneData.get("STOCKMANAGETYPE").toString();
                    String memo = oneData.get("MEMO").toString();
                    String status = oneData.get("STATUS").toString();
                    String unit = oneData.get("UNIT").toString();
                    String unitName = oneData.get("UNITNAME").toString();
                    String unitRatio = oneData.get("UNITRATIO").toString();
                    String unitUdLength = oneData.get("UDLENGTH").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String weight = oneData.get("WEIGHT").toString();
                    String volume = oneData.get("VOLUME").toString();
                    String isBatch = oneData.get("ISBATCH").toString();
                    String shelfLife = oneData.get("SHELFLIFE").toString();
                    String stockInValidDay = oneData.get("STOCKINVALIDDAY").toString();
                    String stockOutValidDay = oneData.get("STOCKOUTVALIDDAY").toString();
                    String checkValidDay = oneData.get("CHECKVALIDDAY").toString();
                    String warningQty = oneData.get("WARNINGQTY").toString();
                    String safeQty = oneData.get("SAFEQTY").toString();
                    String canPurchase = oneData.get("CANPURCHASE").toString();
                    String taxCode = oneData.get("TAXCODE").toString();
                    String canRequire = oneData.get("CANREQUIRE").toString();
                    String minQty = oneData.get("MINQTY").toString();
                    String maxQty = oneData.get("MAXQTY").toString();
                    String mulQty = oneData.get("MULQTY").toString();
                    String canRequireBack = oneData.get("CANREQUIREBACK").toString();
                    String canEstimate = oneData.get("CANESTIMATE").toString();
                    String clearType = oneData.get("CLEARTYPE").toString();
                    String listImage = oneData.get("LISTIMAGE").toString();
                    if (!Check.Null(listImage)){
                        listImage = domainName + listImage;
                    }
                    String isHoliday = oneData.get("ISHOLIDAY").toString();
                    
                    //【ID1018735】【3.0货郎】移动门店新增新品查看和筛选功能 by jinzma 20210702
                    String isNewGoods = oneData.get("ISNEWGOODS").toString();
                    if (Check.Null(isNewGoods)){
                        isNewGoods="N";
                    }
                    //【ID1018829】商品查询服务-新增爆品
                    String isHotGoods = oneData.get("ISHOTGOODS").toString();
                    if (Check.Null(isHotGoods)){
                        isHotGoods="N";
                    }
                    
                    String barcodeQty = "";
                    if (pluBarcodes !=null && !pluBarcodes.isEmpty()) {
                        barcodeQty = oneData.get("BARCODEQTY").toString();
                    }
                    
                    //12-盘点子任务单
                    BigDecimal historyStockQty_b = new BigDecimal("0");
                    if (billType.equals("12")){
						/*String subPunit=oneData.get("SUB_PUNIT").toString();
						String subPqty=oneData.get("SUB_PQTY").toString();
						String subUnitRatio=oneData.get("SUB_UNITRATIO").toString();
						if (Check.Null(subPqty) || Check.Null(subUnitRatio)){
							historyStockQty_b = new BigDecimal("0");
						}else {
							if (subPunit.equals(unit)) {
								historyStockQty_b = new BigDecimal(subPqty);
							} else {
								if (Check.Null(unitRatio) || unitRatio.equals("0")) {
									historyStockQty_b = new BigDecimal("0");
								} else {
									BigDecimal subPqty_b = new BigDecimal(subPqty);
									BigDecimal subUnitRatio_b = new BigDecimal(subUnitRatio);
									BigDecimal unitRatio_b = new BigDecimal(unitRatio);
									if (!PosPub.isNumeric(unitUdLength)) {
										unitUdLength = "6";
									}
									historyStockQty_b = subPqty_b.multiply(subUnitRatio_b.divide(unitRatio_b, 6, BigDecimal.ROUND_HALF_UP));
									historyStockQty_b = historyStockQty_b.setScale(Integer.parseInt(unitUdLength), BigDecimal.ROUND_HALF_UP);
								}
							}
						}*/
                        
                        //【ID1016322】【货郎3.0】多PDA盘点-优化项 BY jinzma 20210324
                        String historyStockQty = oneData.get("SUB_BASEQTY").toString();
                        if (Check.Null(historyStockQty)){
                            historyStockQty="0";
                        }
                        historyStockQty_b = new BigDecimal(historyStockQty);
                    }
                    
                    //获取成品的零售价和配送价
                    String price="0";
                    String distriPrice="0";
                    if (fastImport.equals("N")) {
                        Map<String, Object> condiV = new HashMap<>();
                        condiV.put("PLUNO", pluNo);
                        condiV.put("PUNIT", unit);
                        List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPrice, condiV, false);
                        if (priceList != null && priceList.size() > 0) {
                            price = priceList.get(0).get("PRICE").toString();
                            distriPrice = priceList.get(0).get("DISTRIPRICE").toString();
                        }
                    }


					/*20210316 红艳：
					返回增加punit，punitName，punitUdLength，punitRatio，pPrice，pDistriPrice，pSpec
					如入参billType为0-要货单请求时，（punit，punitName，punitUdLength，punitRatio，pPrice，pDistriPrice，pSpec）返回为品号对应的默认要货单位的信息
					要货单位异常时，写入未查询到的条码列表 by jinzma 20210317 */
                    String punit = "";
                    String punitName = "";
                    String punitUdLength = "";
                    String punitRatio = "";
                    String pPrice = "";
                    String pDistriPrice = "";
                    String pSpec = "";
                    
                    if(!Check.Null(billType) && billType.equals("0")){
                        punit = oneData.get("PUNIT").toString();
                        punitName = oneData.get("PUNITNAME").toString();
                        punitUdLength = oneData.get("PUNITUDLENGTH").toString();
                        if (!PosPub.isNumeric(punitUdLength)){
                            punitUdLength="0";
                        }
                        punitRatio = oneData.get("PUNITRATIO").toString();
                        if (!PosPub.isNumericType(punitRatio)){
                            punitRatio="0";
                        }
                        pSpec = oneData.get("PSPEC").toString();
                        
                        //1大箱  100瓶  100元    100
                        //1小箱   10瓶    ?元     10
                        //?==100 * 10/100
                        BigDecimal unitRatio_b = new BigDecimal(unitRatio);
                        BigDecimal punitRatio_b = new BigDecimal(punitRatio);
                        BigDecimal price_b = new BigDecimal(price);
                        BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                        
                        BigDecimal pPrice_b = price_b.multiply(punitRatio_b.divide(unitRatio_b,6, RoundingMode.HALF_UP));
                        BigDecimal pDistriPrice_b = distriPrice_b.multiply(punitRatio_b.divide(unitRatio_b,6, RoundingMode.HALF_UP));
                        
                        
                        pPrice_b = pPrice_b.setScale(priceLengthInt, RoundingMode.HALF_UP);
                        pDistriPrice_b = pDistriPrice_b.setScale(distriPriceLengthInt, RoundingMode.HALF_UP);
                        
                        pPrice = pPrice_b.toPlainString();
                        pDistriPrice = pDistriPrice_b.toPlainString();
                    }
                    
                    oneLv1.setPluBarcode(pluBarcode);
                    oneLv1.setPluNo(pluNo);
                    oneLv1.setPluName(pluName);
                    oneLv1.setPluType(pluType);
                    oneLv1.setSpec(spec);
                    oneLv1.setFeatureNo(featureNo);
                    oneLv1.setFeatureName(featureName);
                    oneLv1.setCategory(category);
                    oneLv1.setCategoryName(categoryName);
                    oneLv1.setWarmType(warmType);
                    oneLv1.setVirtual(virtual);
                    oneLv1.setStockManageType(stockManageType);
                    oneLv1.setMemo(memo);
                    oneLv1.setStatus(status);
                    oneLv1.setUnit(unit);
                    oneLv1.setUnitName(unitName);
                    oneLv1.setUnitRatio(unitRatio);
                    oneLv1.setUnitUdLength(unitUdLength);
                    oneLv1.setBaseUnit(baseUnit);
                    oneLv1.setBaseUnitName(baseUnitName);
                    oneLv1.setPrice(price);
                    oneLv1.setDistriPrice(distriPrice);
                    oneLv1.setWeight(weight);
                    oneLv1.setVolume(volume);
                    oneLv1.setIsBatch(isBatch);
                    oneLv1.setShelfLife(shelfLife);
                    oneLv1.setStockInValidDay(stockInValidDay);
                    oneLv1.setStockOutValidDay(stockOutValidDay);
                    oneLv1.setCheckValidDay(checkValidDay);
                    oneLv1.setWarningQty(warningQty);
                    oneLv1.setSafeQty(safeQty);
                    oneLv1.setCanPurchase(canPurchase);
                    oneLv1.setTaxCode(taxCode);
                    oneLv1.setCanRequire(canRequire);
                    oneLv1.setMinQty(minQty);
                    oneLv1.setMaxQty(maxQty);
                    oneLv1.setMulQty(mulQty);
                    oneLv1.setCanRequireBack(canRequireBack);
                    oneLv1.setCanEstimate(canEstimate);
                    oneLv1.setClearType(clearType);
                    oneLv1.setQty(barcodeQty);
                    oneLv1.setListImage(listImage);
                    oneLv1.setIsHoliday(isHoliday);
                    oneLv1.setHistoryStockQty(historyStockQty_b.toPlainString());
                    oneLv1.setPunit(punit);
                    oneLv1.setPunitName(punitName);
                    oneLv1.setPunitRatio(punitRatio);
                    oneLv1.setPunitUdLength(punitUdLength);
                    oneLv1.setpSpec(pSpec);
                    oneLv1.setpPrice(pPrice);
                    oneLv1.setpDistriPrice(pDistriPrice);
                    oneLv1.setMaxOrderSpec(oneData.get("MAXORDERSPEC").toString());
                    oneLv1.setIsHotGoods(isHotGoods);
                    oneLv1.setIsNewGoods(isNewGoods);
                    oneLv1.setStockqty("999999");
                    //【ID1027675】【荷家3.0】200001门店，完工单号WGRK2022070600001传到ERP完工数量是0   by jinzma 20221110
                    oneLv1.setBaseUnitUdLength(oneData.get("BASEUNITUDLENGTH").toString());
                    
                    res.getDatas().add(oneLv1);
                }
            } else {
                totalRecords = 0;
                totalPages = 0;
            }
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            
            
            //处理库存量
            if (queryStockqty!=null && queryStockqty.equals("Y"))
            {
                //
                JSONArray pluList=new JSONArray();
                for (DCP_GoodsBarcodeQueryRes.level1Elm p1 : res.getDatas())
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
                
                if (pluList.length()>0) {
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
                    
                    if (!resbody_SOLD.equals("")) {
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
                                    List<DCP_GoodsBarcodeQueryRes.level1Elm> goods=res.getDatas().stream().filter(c->c.getPluNo().equals(r_pluno)).collect(Collectors.toList());
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
            
            
            
            
            return res;
            
        }catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    
    }
    
    @Override
    protected String getQuerySql(DCP_GoodsBarcodeQueryReq req) throws Exception {
        return null;
    }
    
    protected String getQBarcodeSql(DCP_GoodsBarcodeQueryReq req,String companyId,String billType,String orgForm) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String langType = req.getLangType();
        String billNo = req.getRequest().getBillNo();     ///盘点计划单号或盘点子任务
        String[] category = req.getRequest().getCategory();
        String[] brand = req.getRequest().getBrand();
        String[] series = req.getRequest().getSeries();
        String[] attrGroup = req.getRequest().getAttrGroup();
        String[] attrValue = req.getRequest().getAttrValue();
        String keyTxt = req.getRequest().getKeyTxt();
        String categorys = getString(category);
        String brands = getString(brand);
        String seriess = getString(series);
        String attrGroups = getString(attrGroup);
        String attrValues = getString(attrValue);
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String isHotGoods = req.getRequest().getIsHotGoods();
        String isNewGoods = req.getRequest().getIsNewGoods();
        //searchScope：0、全部 1、总部和当前自建门店 2、仅总部 3、全部自建门店 4、仅当前自建门店  by jinzma 20220310
        String searchScope = req.getRequest().getSearchScope();
        if (Check.Null(searchScope)){
            searchScope = "0";
        }
        String selfBuiltShopId = req.getRequest().getSelfBuiltShopId(); //自建门店
        
        String virtual = req.getRequest().getVirtual();   // 是否虚拟商品 Y: 仅虚拟商品 N:过滤掉虚拟商品和套餐商品  不传：全部商品
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        ///处理条码集合
        List<level1Elm> pluBarcodes = req.getRequest().getPluBarcodeList();
        String withPluBarcode = "";
        if (pluBarcodes !=null && !pluBarcodes.isEmpty()) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluBarcode = "";
            String sJoinQty = "";
            for(level1Elm par :pluBarcodes) {
                sJoinPluBarcode += par.getPluBarcode()+",";
                sJoinQty += par.getQty()+",";
            }
            map.put("PLUBARCODE", sJoinPluBarcode);
            map.put("QTY", sJoinQty);
            withPluBarcode = mc.getFormatSourceMultiColWith(map);
        }
        
        StringBuffer sqlbuf = new StringBuffer();
        
        //【ID1030455】 with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
       /* sqlbuf.append(""
                + " with goodstemplate as ("
                + " select b.* from ("
                + " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
                + " from dcp_goodstemplate a"
                + " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
                + " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
                //and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店
                + " where a.eid='"+eId+"' and a.status='100' "
                + " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
                + " ) a"
                + " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
                + " where a.rn=1 "
                + " )"
                + " ");*/
        //【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
        sqlbuf.append(" "
                + " with goodstemplate as ( "
                + " select b.* from dcp_goodstemplate a"
                + " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
                + " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"') "
                + " )");
        
        if (!Check.Null(withPluBarcode))
            sqlbuf.append(" ,barcode as ("+withPluBarcode+")");
        
        sqlbuf.append(" select a.* "
                + " from ("
                + " select count(*) over() num,row_number() over (order by a.pluNo) rn ,"
                + " a.eid,a.plubarcode,a.pluNo,goods.plutype,gfeature.featureno,goods.category,"
                + " goods.warmType,goods.virtual,goods.stockmanagetype,goods.memo,goods.status,"
                + " a.unit,gu.unitratio,u.udlength,goods.baseunit,gu.weight,gu.volume,"
                + " goods.isbatch,goods.shelflife,goods.stockinvalidday,goods.stockoutvalidday,goods.checkvalidday,"
                + " goodstemplate.warningQty,goodstemplate.safeQty,goodstemplate.canPurchase,goods.taxcode,"
                + " goodstemplate.canRequire,goodstemplate.minQty,goodstemplate.maxQty,goodstemplate.mulQty,"
                + " goodstemplate.canRequireBack,goodstemplate.canEstimate,goodstemplate.clearType,goodstemplate.isnewgoods, "
                + " goods.isHoliday,goods.maxorderspec,goods.ishotgoods,"
                + " gl.plu_name,gul.spec,fn.featurename,"
                + " cl.category_name,image.listimage,pul.uname as unitname,"
                + " buludlength.udlength as baseunitudlength,bul.uname as baseunitname ");
        if (!Check.Null(withPluBarcode))
            sqlbuf.append(" ,barcode.qty as barcodeqty ");
        
        if(billType.equals("12")){
            sqlbuf.append(" ,substocktake.punit as sub_punit,substocktake.pqty as sub_pqty,substocktake.unit_ratio as sub_unitratio,"
                    + " substocktake.baseunit as sub_baseunit,substocktake.baseqty as sub_baseqty");
        }
        if(billType.equals("0")){
            sqlbuf.append(" ,goods.punit as punit,p1.uname as punitName,p2.unitratio as punitRatio,p3.udlength as punitUdLength,p4.spec as pspec");
        }
        
        sqlbuf.append(" from dcp_goods_barcode a");
        
        if (!Check.Null(withPluBarcode))
            sqlbuf.append(" inner join barcode on barcode.plubarcode = a.plubarcode");
        
        if (billType.equals("9") && !Check.Null(billNo))  // 9-盘点单
            sqlbuf.append(" inner join dcp_stocktask_list stock on stock.eid=a.eid and stock.shopid='"+shopId+"' "
                    + " and stock.stocktaskno='"+billNo+"' and stock.pluno=a.pluno");
        
        if (billType.equals("12") && !Check.Null(stockTakeNo))  // 12-盘点子任务单且是模板范围盘点
            sqlbuf.append(" inner join dcp_stocktake_detail stocktake on stocktake.eid=a.eid and stocktake.shopid='"+shopId+"' "
                    + " and stocktake.stocktakeno='"+stockTakeNo+"' and stocktake.pluno=a.pluno and stocktake.featureno=a.featureno");
        
        if (!Check.Null(attrValues))
            sqlbuf.append(" inner join (select eid,pluno from dcp_goods_attr_value "
                    + " where attrvalueid in ("+attrValues+") group by eid,pluno ) gav on gav.eid=a.eid and gav.pluno=a.pluno");
        
        sqlbuf.append(" inner join dcp_goods goods on a.eid=goods.eid and a.pluno=goods.pluno and goods.status='100'");
        
        //【ID1030115】【潮品3.0】打包一口价添加条码：选择不到条码6911400413069 by jinzma 20221201
        // 小凤：非门店类的商品查询都不关联商品模板
        
        if (billType.equals("-1") && !Check.Null(orgForm) && !orgForm.equals("2")){
            sqlbuf.append(" left  join goodstemplate on a.pluno=goodstemplate.pluno ");
        }else{
            sqlbuf.append(" inner join goodstemplate on a.pluno=goodstemplate.pluno ");
        }
        
        sqlbuf.append(" inner join dcp_goods_unit gu on gu.eid=a.eid and gu.pluno=a.pluno and gu.ounit=a.unit and gu.unit=goods.baseunit"
                + " inner join dcp_unit u on u.eid=a.eid and u.unit=a.unit and u.status='100'"
                + " left  join dcp_goods_lang gl on gl.eid=a.eid and gl.pluno=a.pluno and gl.lang_type='"+langType+"'"
                + " left  join dcp_goods_unit_lang gul on a.eid=gul.eid and a.pluno=gul.pluno and a.unit=gul.ounit and gul.lang_type='"+langType+"'"
                + " left  join dcp_goods_feature gfeature "
                + " on gfeature.eid=a.eid and gfeature.status='100' "
                + " and gfeature.pluno=goods.pluno "
                + " and (a.featureno=gfeature.featureno or a.featureno=' ') "
                + " and goods.plutype='FEATURE' and gfeature.featureno<>' ' "
                + " left  join dcp_goods_feature_lang fn on a.eid=fn.eid and a.pluno=fn.pluno and gfeature.featureno=fn.featureno  and fn.lang_type='"+langType+"'"
                + " left  join dcp_category_lang cl on cl.eid=a.eid and cl.category=goods.category and cl.lang_type='"+langType+"'"
                + " left  join dcp_goodsimage image on image.eid=a.eid and image.pluno=a.pluno and image.apptype='ALL'"
                + " left  join dcp_unit_lang pul on pul.eid=a.eid and pul.unit=a.unit and pul.lang_type='"+langType+"'"
                + " left  join dcp_unit_lang bul on bul.eid=a.eid and bul.unit=goods.baseunit and bul.lang_type='"+langType+"'"
                + " left  join dcp_unit buludlength on a.eid=buludlength.eid and goods.baseunit=buludlength.unit"
                //【ID1018894】 复制 【3.0货郎】盘点单无法提交 by jinzma 20210701
                //+ " left  join (select pluno from dcp_goods_feature where eid='"+eId+"' group by pluno) gfeature on gfeature.pluno=goods.pluno"
                + " ");
        
        if(billType.equals("12")){
            sqlbuf.append(" "
                    + " left join dcp_substocktake_detail substocktake on substocktake.eid='"+eId+"' and substocktake.shopid='"+shopId+"'"
                    + " and substocktake.substocktakeno='"+billNo+"' and substocktake.pluno=a.pluno"
                    + " and substocktake.featureno = a.featureno ");
        }
        
        if(billType.equals("0")){
            sqlbuf.append(" "
                    + " left  join dcp_unit_lang p1 on p1.eid=a.eid and p1.unit=goods.punit and p1.lang_type='"+langType+"'"
                    + " inner join dcp_goods_unit p2 on p2.eid=a.eid and p2.pluno=goods.pluno and p2.ounit=goods.punit and p2.unit=goods.baseunit and p2.punit_use='Y' "
                    + " inner join dcp_unit p3 on p3.eid=a.eid and p3.unit=goods.punit and p3.status='100'"
                    + " left  join dcp_goods_unit_lang p4 on p4.eid=a.eid and p4.pluno=goods.pluno and p4.ounit=goods.punit and p4.lang_type='"+langType+"' "
                    + " ");
        }
        sqlbuf.append(" where a.status='100' and a.eid='"+eId+"' ");
        //【ID1018894】 复制 【3.0货郎】盘点单无法提交 by jinzma 20210701
        //+ " and ((goods.plutype='FEATURE' and gfeature.pluno is not null) or  goods.plutype<>'FEATURE')"
        sqlbuf.append(" and ((goods.plutype='FEATURE' and a.featureno<>' ' and a.featureno=gfeature.featureno) or (goods.plutype<>'FEATURE' and a.featureno=' ')) ");
        
        //【ID1034817】【嘉华3.0】中台不能选择到虚拟商品和套餐商品-----服务端 by jinzma 20230724
        // 是否虚拟商品 Y: 仅虚拟商品 N:过滤掉虚拟商品和套餐商品  不传：全部商品
        //【ID1035264】【嘉华3.0】中台不能选择到虚拟商品和套餐商品-----服务端优化  by jinzma 20230807
        if (!Check.Null(virtual)) {
            if (virtual.equals("Y")){
                sqlbuf.append(" and (goods.virtual='Y' or goods.plutype='PACKAGE') ");   // Y：虚拟商品或套餐商品;
            }
            if (virtual.equals("N")){
                sqlbuf.append(" and goods.virtual='N' and goods.plutype<>'PACKAGE' ");   // N：过滤掉虚拟商品和套餐商品;
            }
        }
        
        if (billType.equals("0")) {  //0-要货类
            sqlbuf.append(" and goodstemplate.canrequire='Y' ");
        }
        if (billType.equals("6")) { // 6-计划报单
            sqlbuf.append(" and goodstemplate.cansale='Y' ");
        }
        if (billType.equals("7")) { // 7-可退仓
            sqlbuf.append(" and goodstemplate.canrequireback='Y' ");
        }
        if (billType.equals("14")) { // 7-可调拨
            sqlbuf.append(" and goodstemplate.isallot='Y' ");
        }
        
        if(!Check.Null(keyTxt))
            sqlbuf.append(" and (goods.PLuNo = '%%"+keyTxt+"%%' or gl.plu_name like '%%"+keyTxt+"%%' or a.plubarcode like '%%"+keyTxt+"%%' )");
        if (!Check.Null(categorys))
            sqlbuf.append(" and goods.category in ("+categorys+")");
        if (!Check.Null(brands))
            sqlbuf.append(" and goods.brand in ("+brands+")");
        if (!Check.Null(seriess))
            sqlbuf.append(" and goods.series in ("+seriess+")");
        if (!Check.Null(attrGroups))
            sqlbuf.append(" and goods.attrGroupid in ("+attrGroups+")");
        if (!Check.Null(isHotGoods))
            sqlbuf.append(" and goods.ishotgoods = '"+isHotGoods+"'");
        if (!Check.Null(isNewGoods))
            sqlbuf.append(" and goodstemplate.isnewgoods = '"+isNewGoods+"'");
        
        //【ID1023729】货郎3.0加盟店可以自己采购商品，定价，做促销，管理自己的会员（后端云中台服务） by jinzma 20220222
        //searchScope by jinzma 20220310
        switch (searchScope){
            case "0":    //0、全部
                break;
            case "1":    //1、总部和当前自建门店
                sqlbuf.append(" and (goods.selfbuiltshopid is null or goods.selfbuiltshopid='"+selfBuiltShopId+"') ");
                break;
            case "2":    //2、仅总部
                sqlbuf.append(" and goods.selfbuiltshopid is null");
                break;
            case "3":    //3、全部自建门店
                sqlbuf.append(" and goods.selfbuiltshopid is not null");
                break;
            case "4":    //4、仅当前自建门店
                sqlbuf.append(" and goods.selfbuiltshopid='"+selfBuiltShopId+"'");
                break;
        }
        
        sqlbuf.append(" )a ");
        sqlbuf.append(" where a.rn>"+startRow+" and a.rn<="+(startRow+pageSize));
        
        return sqlbuf.toString();
    }
    
    protected String getQIllegalBarcodeSql(DCP_GoodsBarcodeQueryReq req,String getQBarcodeSql) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        ///处理条码集合
        List<level1Elm> pluBarcodes = req.getRequest().getPluBarcodeList();
        String withPluBarcode = "";
        if (pluBarcodes !=null && !pluBarcodes.isEmpty()) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            String sJoinPluBarcode = "";
            String sJoinQty = "";
            for(level1Elm par :pluBarcodes) {
                sJoinPluBarcode += par.getPluBarcode()+",";
                sJoinQty += par.getQty()+",";
            }
            map.put("PLUBARCODE", sJoinPluBarcode);
            map.put("QTY", sJoinQty);
            withPluBarcode = mc.getFormatSourceMultiColWith(map);
        }
        
        sqlbuf.append(" with barcode as ("+withPluBarcode+")");
        sqlbuf.append(" select barcode.plubarcode from barcode "
                + " left join (" +getQBarcodeSql+ " )a on barcode.plubarcode = a.plubarcode "
                + " where a.plubarcode is null ");
        return sqlbuf.toString();
    }
    
    protected String getString(String[] str) {
        String str2 = "";
        if (str!=null && str.length>0) {
            for (String s:str) {
                str2 = str2 + "'" + s + "'"+ ",";
            }
            str2=str2.substring(0,str2.length()-1);
        }
        return str2;
    }
    
}
