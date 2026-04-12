package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsInfo_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsInfo_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsInfo_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 服务函数：DCP_GetMallGoodsInfo_Open
 * 服务说明：获取线上商品信息
 * @author jinzma
 * @since  2020-10-10
 */
public class DCP_GetMallGoodsInfo_Open extends SPosBasicService<DCP_GetMallGoodsInfo_OpenReq,DCP_GetMallGoodsInfo_OpenRes>{
    Logger logger = LogManager.getLogger(DCP_GetMallGoodsInfo_Open.class);
    @Override
    protected boolean isVerifyFail(DCP_GetMallGoodsInfo_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String apiUserCode = req.getApiUserCode();
        String langType = req.getLangType();
        levelElm request =req.getRequest();
        if(apiUserCode==null) {
            errMsg.append("apiUserCode不能为空值 ");
            isFail=true;
        }
        if(langType==null) {
            errMsg.append("langType不能为空值 ");
            isFail=true;
        }
        if (request==null) {
            errMsg.append("request不能为空值 ");
            isFail=true;
        }else{
            if (Check.Null(request.getMallGoodsId())){
                errMsg.append("mallGoodsId线上商品代号不能为空值 ");
                isFail=true;
            }
        }
        
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
        
    }
    
    @Override
    protected TypeToken<DCP_GetMallGoodsInfo_OpenReq> getRequestType() {
        return new TypeToken<DCP_GetMallGoodsInfo_OpenReq>(){};
    }
    
    @Override
    protected DCP_GetMallGoodsInfo_OpenRes getResponseType() {
        return new DCP_GetMallGoodsInfo_OpenRes();
    }
    
    @Override
    protected DCP_GetMallGoodsInfo_OpenRes processJson(DCP_GetMallGoodsInfo_OpenReq req) throws Exception {
        DCP_GetMallGoodsInfo_OpenRes res = this.getResponse();
        try {
            String apiUserCode = req.getApiUserCode();
            String langType = req.getLangType();
            String eId = "";              //从apiUserCode 查询得到企业编号
            String appType = "";          //从apiUserCode 查询得到应用类型
            String channelId = "";        //从apiUserCode 查询得到渠道编码
            String shopId = "";           //门店编号
            String memberId = "";         //会员号
            String cardNo = "";           //会员卡号
            String promUrl = "";           //参数促销URL
            //String periodNo = "";         //当前时段
            String promCategory="";       //活动类型
            String promNo="";             //活动代号
            String mallGoodsId ="";        //线上商品代号
            String cardTypeId = "";       //主卡卡类型
            String sql="";
            String requestId = req.getRequestId();
            if (Check.Null(requestId))
                requestId = UUID.randomUUID().toString();
            
            //以下是云洋在基类里面进行赋值  20200915
            eId=req.geteId();
            appType = req.getApiUser().getAppType();
            channelId = req.getApiUser().getChannelId();
            if (Check.Null(eId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的eId");
            if (Check.Null(appType))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的appType");
            if (Check.Null(channelId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的channelId");
            
            levelElm request = req.getRequest();
            if (request!=null) {
                shopId = request.getShopId();
                memberId = request.getMemberId();
                cardNo = request.getCardNo();
                promCategory = request.getPromCategory();
                promNo = request.getPromNo();
                mallGoodsId = request.getMallGoodsId();
                cardTypeId = request.getCardTypeId();
            }
            
            ///默认仓库获取
            //String out_cost_warehouse="";
            //			if (!Check.Null(shopId))
            //			{
            //				sql=" select out_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
            //				List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
            //				if (getWarehouse!=null && getWarehouse.isEmpty()==false)
            //				{
            //					out_cost_warehouse = getWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
            //				}
            //				else
            //				{
            //					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店:"+shopId+ " 默认出货成本仓未设置");
            //				}
            //			}
            //前端给的时间戳可能有错，直接取后端的时间计算
            //Calendar cal = Calendar.getInstance();//获得当前时间
            //SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            //String periodTime = df.format(cal.getTime());
            
            //时段获取
            //			sql = " select a.periodno from dcp_period a"
            //					+ " left join dcp_period_range b on a.eid=b.eid and a.periodno=b.periodno and b.shopid='"+shopId+"'"
            //					+ " where a.eid='"+eId+"' and a.status='100' and (a.restrictshop='0' or (a.restrictshop='1' and b.shopid is not null))"
            //					+ " and a.begintime <='"+periodTime+"'"
            //					+ " and a.endtime >='"+periodTime+"'";
            //			List<Map<String, Object>> getPeriodNo = this.doQueryData(sql, null);
            //			if (getPeriodNo!=null && getPeriodNo.isEmpty()==false)
            //			{
            //				periodNo = getPeriodNo.get(0).get("PERIODNO").toString();
            //			}
            
            ///会员服务地址
            promUrl = PosPub.getPROM_INNER_URL(eId);
//            promUrl="http://retaildev.digiwin.com.cn/promService_3.0/openapi";//testtesttest
            if (Check.Null(promUrl))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数PromUrl未设置 ");
            
            ////图片地址参数获取
            String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            String httpStr = isHttps.equals("1") ? "https://" : "http://";
            String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
            String imagePath = "";
            if (domainName.endsWith("/")) {
                imagePath = httpStr + domainName + "resource/image/";
            } else {
                imagePath = httpStr + domainName + "/resource/image/";
            }
            
            //线上商品获取
            sql =getQueryGoodsOnlineSql(req,eId,appType,channelId,shopId,mallGoodsId);
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            DCP_GetMallGoodsInfo_OpenRes.level1Elm datas = res.new level1Elm();
            if (getQData!=null && !getQData.isEmpty()) {
                //调用基础促销价计算      // 20200929和玲霞沟通确认   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
                MyCommon comm = new MyCommon();
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                condition.put("PLUNO", true);
                condition.put("PLUTYPE", true);
                List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
                List<Map<String, Object>> getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId, memberId, cardNo, promUrl, getQPlu);
                if (getBasicProm==null || getBasicProm.isEmpty()){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
                }
                
                String getQpluNo =getQData.get(0).get("PLUNO").toString();
                String getQpluType = getQData.get(0).get("PLUTYPE").toString();
                String originalPrice = "";
                String price = "";
                String basicPrice = "";
                String promLable = "";
                String price_vip = "";
                String price_novip = "";
                
                for (Map<String, Object> oneBasicProm:getBasicProm) {
                    String pluNo = oneBasicProm.get("PLUNO").toString();
                    String pluType = oneBasicProm.get("PLUTYPE").toString();
                    if (pluNo.equals(getQpluNo) && pluType.equals(getQpluType)) {
                        originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
                        price = oneBasicProm.get("PRICE").toString();
                        basicPrice = oneBasicProm.get("BASICPRICE").toString();
                        promLable = oneBasicProm.get("PROMLABLE").toString();
                        price_vip = oneBasicProm.get("PRICE_VIP").toString();
                        price_novip = oneBasicProm.get("PRICE_NOVIP").toString();
                        break;
                    }
                }
                if (!PosPub.isNumericType(originalPrice))
                    originalPrice="-1";                  ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
                if (!PosPub.isNumericType(price))
                    price="-1";
                if (!PosPub.isNumericType(basicPrice))
                    basicPrice="-1";
                if (!PosPub.isNumericType(price_vip))
                    price_vip="-1";
                if (!PosPub.isNumericType(price_novip))
                    price_novip="-1";
                
                //可售量查询
                String sUnit = getQData.get(0).get("UNIT").toString();
                //门店前端未给值，造成库存查询不准
                if (Check.Null(shopId)) {
                    shopId = "";
                }
                
                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                //配送说明  	门店自提|同城配送|全国快递  繁体的同城叫[宅配到府]
                String deliveryDescription ="";
                String deliveryDescription2 ="";
                String shopPickup = getQData.get(0).get("SHOPPICKUP").toString();   // 是否支持自提0-否1-是
                String cityDeliver = getQData.get(0).get("CITYDELIVER").toString(); // 是否支持同城配送0-否1-是
                String expressDeliver = getQData.get(0).get("EXPRESSDELIVER").toString(); // 是否支持全国快递0-否1-是
                
                //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                String totalStock="0";
                //门店库存
                String shopStock="0";
                //配送门店库存
                String deliveryStock="0";
                if("1".equals(shopPickup)||"1".equals(cityDeliver)){
                	try{
                		totalStock = PosPub.queryStockQty(dao, eId, mallGoodsId, " ", shopId, channelId, "", sUnit);
                		shopStock=totalStock;
                		if (!PosPub.isNumericType(totalStock)) {
                			totalStock="0";
                		}
                	}catch (SPosCodeException e){
                		String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+mallGoodsId+"',' ','"+shopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                		logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsInfo_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                		totalStock="0";
                	}
                }
                
                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                //1、下单门店没有库存就不能自提
                //2、当开启了统一发货参数并且当发货门店没有库存就不能全国配送
                boolean isSelf = true;    //是否支持自提
                boolean isDelivery = true;   //是否支持统一配送
                
                if (new BigDecimal(totalStock).compareTo(BigDecimal.ZERO)<=0){
                    isSelf = false;  //门店库存<=0 不允许自提
                }
                
                
                //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615
//                sql=" SELECT DELIVERYSHOPID FROM CRM_EXPRESSSET "
//                        + " where EID='"+eId+"' and APPID='"+req.getApiUser().getUserCode()+"' and DELIVERYTYPE='2' and EXPRESS='1' ";
//                List<Map<String, Object>> getDeliverShop = this.doQueryData(sql, null);
//                if (!CollectionUtils.isEmpty(getDeliverShop)){
//                    String deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//                }
                String deliverShopId = HelpTools.getDeliverShopId(eId, req.getApiUser().getUserCode(), shopId);
                
                
                if (!Check.Null(deliverShopId)&&("1".equals(expressDeliver))){
                	try{
                		deliveryStock = PosPub.queryStockQty(dao, eId, getQpluNo, " ", deliverShopId, channelId,"", sUnit);
                		if (PosPub.isNumericType(deliveryStock)) {
                			if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliveryStock))<0) {
                				totalStock = deliveryStock;
                			}
                			
                			if (new BigDecimal(deliveryStock).compareTo(BigDecimal.ZERO)<=0){
                				isDelivery = false;  //统一配送门店库存<=0 不允许统一配送
                			}
                			
                		}else{
                			isDelivery = false;    //负数或非数值  统一配送门店没有库存，不允许统一配送
                		}
                	}catch (SPosCodeException e){
                		String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+getQpluNo+"',' ','"+deliverShopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                		logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsInfo_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                		isDelivery = false;
                	}
                }
                
                ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();
                
                
                String mallGoodsName = getQData.get(0).get("DISPLAYNAME").toString();
                String description = getQData.get(0).get("SIMPLEDESCRIPTION").toString();
                String shareDescription = getQData.get(0).get("SHAREDESCRIPTION").toString();
                String symbolDisplay = getQData.get(0).get("SYMBOLDISPLAY").toString();
                //GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
                if (Check.Null(symbolDisplay))
                    symbolDisplay="0";
                String symbolType = getQData.get(0).get("SYMBOLTYPE").toString();

				/* #20201127 V3.0  symbolType需要转化 玲霞要求修改      BY JZMA 20201127
				角标类型：1：新品2：热卖3：文本标签4：自定义 需转化 new|hot|text|pic 新品/热卖/文本/图标
				1>>new,2>>hot,3>>text,4>>pic*/
                switch (symbolType) {
                    case "1":
                        symbolType="new";
                        break;
                    case "2":
                        symbolType="hot";
                        break;
                    case "3":
                        symbolType="text";
                        break;
                    case "4":
                        symbolType="pic";
                        break;
                    default:
                        break;
                }
                
                String symbolIcon = getQData.get(0).get("SYMBOLICON").toString();
                String symbolText = getQData.get(0).get("SYMBOLTEXT").toString();
                String stockDisplay = getQData.get(0).get("STOCKDISPLAY").toString();
                
                
                if (shopPickup.equals("1") && isSelf) {
                    deliveryDescription = "门店自提|";
                    if (PosPub.isNumericType(shopStock)&&(new BigDecimal(shopStock)).compareTo(BigDecimal.ZERO)>0) {
                    	deliveryDescription2 = "门店自提|";
                    }
                }
                if (cityDeliver.equals("1")) {
                    deliveryDescription = deliveryDescription + "同城配送|";
                    if (PosPub.isNumericType(shopStock)&&(new BigDecimal(shopStock)).compareTo(BigDecimal.ZERO)>0) {
                    	deliveryDescription2 = deliveryDescription2 + "同城配送|";
                    }
                }
                if (expressDeliver.equals("1") && isDelivery) {
                    deliveryDescription = deliveryDescription + "全国快递|";
                    if (PosPub.isNumericType(deliveryStock)&&(new BigDecimal(deliveryStock)).compareTo(BigDecimal.ZERO)>0) {
                    	deliveryDescription2 = deliveryDescription2 + "全国快递|";
                    }
                }
                
                if (!Check.Null(deliveryDescription) && deliveryDescription.length()>0) {
                    deliveryDescription = deliveryDescription.substring(0, deliveryDescription.length() - 1);
                }
                if (!Check.Null(deliveryDescription2) && deliveryDescription2.length()>0) {
                	deliveryDescription2 = deliveryDescription2.substring(0, deliveryDescription2.length() - 1);
                }
                
                //商品状态 0.已失效（活动库存已售完，商品售完，下架等） 1.有效
                String status = getQData.get(0).get("STATUS").toString();
                if (status.equals("100") && Integer.parseInt(totalStock) >0) {
                    status = "1";
                }else {
                    status = "0";
                }
				/*
				 ************  preDayDesp逻辑处理:  BY 玲霞 20201010  ******************
				 DCP_GOODS_ONLINE.PRESALE=0, 返回空
				 PRESALE=1，
				 发货时机类型DELIVERYDATETYPE:
				 DELIVERYDATETYPE=2.指定日期发货，返回[预计DELIVERYDATE 发货]
				 DELIVERYDATETYPE=1. 付款成功后发货，返回[预计DELIVERYDATEVALUE天/小时后发货]
				 小时/天:DELIVERYDATETYPE2 （1：小时/2：天）)
				 *****************/
                String preDayDesp = "";  //预订提前期
                String preSale = getQData.get(0).get("PRESALE").toString();
                String deliveryDateType = getQData.get(0).get("DELIVERYDATETYPE").toString();
                String deliveryDateType2 = getQData.get(0).get("DELIVERYDATETYPE2").toString();
                String deliveryDate = getQData.get(0).get("DELIVERYDATE").toString();
                String deliveryDateValue = getQData.get(0).get("DELIVERYDATEVALUE").toString();
                
                if (preSale.equals("1") && deliveryDateType.equals("2"))
                    if (langType.equals("zh_CN")){
                        preDayDesp = "预计"+deliveryDate+" 发货";
                    }else {
                        preDayDesp = "預計" + deliveryDate + " 發貨";
                    }
                if (preSale.equals("1") && deliveryDateType.equals("1") && deliveryDateType2.equals("1"))
                    if (langType.equals("zh_CN")) {
                        preDayDesp = "预计" + deliveryDateValue + " 小时后发货";
                    }else {
                        preDayDesp = "預計" + deliveryDateValue + " 小時後發貨";
                    }
                if (preSale.equals("1") && deliveryDateType.equals("1") && deliveryDateType2.equals("2"))
                    if (langType.equals("zh_CN")) {
                        preDayDesp = "预计" + deliveryDateValue + " 天后发货";
                    }else {
                        preDayDesp = "預計" + deliveryDateValue + " 天後發貨";
                    }
                datas.setPictures(new ArrayList<DCP_GetMallGoodsInfo_OpenRes.level2Elm>());
                for (Map<String, Object> oneData:getQData) {
                    DCP_GetMallGoodsInfo_OpenRes.level2Elm lv2 = res.new level2Elm();
                    String picUrl = oneData.get("PICURL").toString();
                    if (!Check.Null(picUrl))
                        picUrl = imagePath + picUrl;
                    lv2.setPicUrl(picUrl);
                    datas.getPictures().add(lv2);
                }
                
                datas.setPromLable(promLable);
                datas.setDeliveryDescription(deliveryDescription);
                datas.setDeliveryDescription2(deliveryDescription2);
                datas.setDescription(description);
                datas.setGoodsId(mallGoodsId);
                datas.setMallGoodsId(mallGoodsId);
                datas.setMallGoodsName(mallGoodsName);
                datas.setOriPrice(originalPrice);
                datas.setPluType(getQpluType);
                datas.setPreDayDesp(preDayDesp);
                datas.setPreSale(preSale);
                datas.setPrice(price);
                datas.setShareDescription(shareDescription);
                datas.setStatus(status);
                datas.setStock(totalStock);
                datas.setStockDisplay(stockDisplay);
                datas.setSymbolDisplay(symbolDisplay);
                datas.setSymbolIcon(symbolIcon);
                datas.setSymbolText(symbolText);
                datas.setSymbolType(symbolType);
                datas.setUnit(sUnit);
                datas.setPrice_vip(price_vip);
                datas.setPrice_novip(price_novip);
                
                //promCategory=SHOP_SECKILL 秒杀 或 SHOP_GROUPBUY 拼团 时，activity返回空 	否则调用104-PROM_GoodsPromQuery_Open返回
                datas.setActivity(new ArrayList<Map<String, Object>>());
                if (Check.Null(promCategory) || !(promCategory.equals("SHOP_SECKILL")||promCategory.equals("SHOP_GROUPBUY"))) {
                    List<Map<String, Object>> getActivity = comm.getActivity(apiUserCode, req.getApiUser().getUserKey(),langType, requestId, req.getApiUser().getCompanyId(),shopId, memberId, promUrl, mallGoodsId,getQpluType,sUnit,promCategory,promNo,cardTypeId);
                    if (getActivity!=null && !getActivity.isEmpty()){
                        for (Map<String, Object> par : getActivity){
                            try {
                                if (par.getOrDefault("payTypeList","").toString().equals("[]")) {
                                    par.replace("payTypeList", new ArrayList<>());
                                }else{
                                    ///干掉"myArrayList" and "map"
                                    JSONArray payType = JSONArray.parseArray(par.getOrDefault("payTypeList","").toString());
                                    //List<Map<String,Object>> payTypeList = (List)payType;
                                    //List payTypeList = (List)payType;
                                    //par.replace("payTypeList", payTypeList);
                                    par.replace("payTypeList", payType);
                                }
                                
                                if (par.getOrDefault("couponTypeList","").toString().equals("[]")) {
                                    par.replace("couponTypeList", new ArrayList<>());
                                }else{
                                    ///干掉"myArrayList" and "map"
                                    JSONArray couponType = JSONArray.parseArray(par.getOrDefault("couponTypeList","").toString());
                                    //List<Map<String,Object>> couponTypeList = (List)couponType;
                                    //List couponTypeList = (List)couponType;
                                    //par.replace("couponTypeList", couponTypeList);
                                    par.replace("couponTypeList", couponType);
                                }
                                
                                if (par.getOrDefault("giftList","").toString().equals("[]")) {
                                    par.replace("giftList", new ArrayList<>());
                                }else{
                                    ///干掉"myArrayList" and "map"
                                    JSONArray gift = JSONArray.parseArray(par.getOrDefault("giftList","").toString());
                                    //List<Map<String,Object>> giftList = (List)gift;
                                    //List giftList = (List)gift;
                                    //par.replace("giftList", giftList);
                                    par.replace("giftList", gift);
                                }
                                
                            }catch (Exception e) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品可参与促销列表(PROM_GoodsPromQuery_Open) JSON解析失败 "+e.getMessage());
                            }
                            datas.getActivity().add(par);
                        }
                    }
                    //else {} throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品可参与促销列表(PROM_GoodsPromQuery_Open)调用失败 ");
                    
                }
            }
            
            res.setDatas(datas);
            return res;
            
        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_GetMallGoodsInfo_OpenReq req) throws Exception {
        return null;
    }
    
    private String getQueryGoodsOnlineSql(DCP_GetMallGoodsInfo_OpenReq req,String eId,String appType,String channelId,String shopId,String mallGoodsId) throws Exception {
        String sql="";
        String langType=req.getLangType();
        StringBuffer sb = new StringBuffer();
        sb.append(" with plu as ("
                + " select a.pluno,N'100' as status"
                + " from dcp_goods_shelf_range a"
                + " left join (select a.pluno from dcp_goods_shelf_range a"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null and a.pluno='"+mallGoodsId+"'"
                + " group by a.pluno)"
                + " ");
        
        sb.append(" ,goodsimage as ("
                + " select pluno,picUrl,symbolDisplay from ("
                + " select a.*,row_number() over (partition by pluno order by indx) as rn from ("
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,1 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' and a.pluno='"+mallGoodsId+"'"
                + " union all"
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' and a.pluno='"+mallGoodsId+"' )a"
                + " ) where rn='1' )"
                + " ");
        
        sb.append(" ,prodimage as ("
                + " select pluno,picUrl from ("
                + " select a.*,row_number() over (partition by pluno,item order by indx) as rn from ("
                + " select a.pluno,a.prodimage as picUrl,a.item,1 as indx from dcp_goodsimage_prodimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' and a.pluno='"+mallGoodsId+"'"
                + " union all"
                + " select a.pluno,a.prodimage as picUrl,a.item,2 as indx from dcp_goodsimage_prodimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' and a.pluno='"+mallGoodsId+"' )a"
                + " ) where rn='1'"
                + " )"
                + " ");
        
        sb.append(" ,goodsimage_symbol as ("
                + " select pluno,symbolType,symbolText,symbolIcon from ("
                + " select a.*,row_number() over (partition by pluno order by indx) as rn from("
                + " select pluno,symbolType,symbolText,symbolIcon,1 as indx from ("
                + " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
                + " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
                + " where a.eid ='"+eId+"' and a.apptype='"+appType+"' "
                + " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate) and a.pluno='"+mallGoodsId+"'"
                + " )a where rn='1'"
                + " union all"
                + " select pluno,symbolType,symbolText,symbolIcon,2 as indx from ("
                + " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
                + " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
                + " where a.eid ='"+eId+"' and a.apptype='ALL' "
                + " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate) and a.pluno='"+mallGoodsId+"'"
                + " )a where rn='1')a "
                + " ) where rn='1')"
                + " ");
        
        sb.append(" select a.pluno,a.plutype,a.stockdisplay,a.shoppickup,a.citydeliver,a.expressdeliver,"
                + " a.presale,a.deliverydatetype,to_char(a.deliverydate,'YYYY-MM-DD') as deliverydate,"
                + " a.deliverydatevalue,a.deliverydatetype2,"
                + " b.sunit as unit,b.sunit as unitId,"   ///unitId给促销用的
                + " gol.displayname,gol.simpledescription,gol.shareDescription,"
                /* 【ID1024249】【货郎3.0】GetMallGoodsInfo，产品图为空时候的能不能用列表图当第一张图  by jinzma 20220331
                    接口修改 DCP_GetMallGoodsInfo_Open 接口，
                    如果表DCP_GOODSIMAGE_PRODIMAGE 表为空，
                    那么取列表dcp_goodsimage中的LISTIMAGE作为产品图。GetMallGoodsInfo 接口不变，前端不变*/
                //+ " prodimage.picUrl,"
                + " nvl(prodimage.picUrl,goodsimage.picUrl) as picUrl,"
                
                + " goodsimage.symbolDisplay,"
                + " goodsimage_symbol.symbolType,goodsimage_symbol.symbolIcon,goodsimage_symbol.symbolText,"
                + " nvl(plu.status,'0') as status"
                + " from dcp_goods_online a"
                + " left join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100'"
                + " inner join plu on plu.pluno = a.pluno"
                + " left join prodimage on a.pluno=prodimage.pluno"
                + " left join plu on a.pluno=plu.pluno"
                + " left join goodsimage on goodsimage.pluno=a.pluno"
                + " left join goodsimage_symbol on goodsimage_symbol.pluno=a.pluno"
                + " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"'"
                + " ");
        
        sql=sb.toString();
        return sql;
        
    }
}
