package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GetGoodsGroup_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetGoodsGroup_OpenReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_GetGoodsGroup_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetGoodsGroup_OpenRes;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 服务函数：DCP_GetGoodsGroup_Open
 * 服务说明：获取线上商品分组信息(含商品）
 * @author jinzma
 * @since  2020-09-10
 */
public class DCP_GetGoodsGroup_Open extends SPosBasicService<DCP_GetGoodsGroup_OpenReq,DCP_GetGoodsGroup_OpenRes>{
    Logger logger = LogManager.getLogger(DCP_GetGoodsGroup_Open.class);
    @Override
    protected boolean isVerifyFail(DCP_GetGoodsGroup_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String apiUserCode = req.getApiUserCode();
        String langType = req.getLangType();
        
        if(apiUserCode==null) {
            errMsg.append("apiUserCode不能为空值 ");
            isFail=true;
        }
        if(langType==null) {
            errMsg.append("langType不能为空值 ");
            isFail=true;
        }
        if (req.getRequest()==null) {
            errMsg.append("request不能为空值 ");
            isFail=true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GetGoodsGroup_OpenReq> getRequestType() {
        return new TypeToken<DCP_GetGoodsGroup_OpenReq>(){};
    }
    
    @Override
    protected DCP_GetGoodsGroup_OpenRes getResponseType() {
        return new DCP_GetGoodsGroup_OpenRes();
    }
    
    @Override
    protected DCP_GetGoodsGroup_OpenRes processJson(DCP_GetGoodsGroup_OpenReq req) throws Exception {
        DCP_GetGoodsGroup_OpenRes res = this.getResponse();
        try {
            String apiUserCode = req.getApiUserCode();
            String periodNo = "";         //当前时段
            String requestId = UUID.randomUUID().toString();
            if(!Check.Null(req.getRequestId()))
            {
            	requestId+="|"+req.getRequestId();
            }
    		int pageSize = req.getPageSize();
    		int pageNumber = req.getPageNumber();
    		if(pageSize==0)
    		{
    			req.setPageSize(500);
    			req.setPageNumber(1);
    			pageSize=500;
    			pageNumber=1;
    		}
    		
            //以下是云洋在基类里面进行赋值  20200915
            String eId=req.geteId();                            //从apiUserCode 查询得到企业编号
            String appType = req.getApiUser().getAppType();     //从apiUserCode 查询得到应用类型
            String channelId = req.getApiUser().getChannelId(); //从apiUserCode 查询得到渠道编码
            
            //			sql=" select * from crm_apiuser where usercode='"+apiUserCode+"'";
            //			List<Map<String, Object>> getUserCode = this.doQueryData(sql, null);
            //			if (getUserCode!=null && getUserCode.isEmpty()==false)
            //			{
            //				eId = getUserCode.get(0).get("EID").toString();
            //				appType = getUserCode.get(0).get("APPTYPE").toString();
            //				channelId = getUserCode.get(0).get("CHANNELID").toString();
            //			}
            //			else
            //			{
            //				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中不存在");
            //			}
            
            if (Check.Null(eId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的eId");
            if (Check.Null(appType))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的appType");
            if (Check.Null(channelId))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:"+apiUserCode+ " 在crm_apiuser表中未查询到对应的channelId");
            
            levelElm request = req.getRequest();
            
            String shopId = request.getShopId();      //门店编号
            String memberId = request.getMemberId();  //会员号
            String cardNo = request.getCardNo();      //会员卡号
            
            String miniPrice = request.getMiniPrice(); // 最低价
            String maxPrice = request.getMaxPrice();   // 最高价
            
            
            if(!Check.Null(miniPrice)&&!PosPub.isNumericType(miniPrice)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "最低价不为空时，不可为除数字其他类型");
            }
            if(!Check.Null(maxPrice)&&!PosPub.isNumericType(maxPrice)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "最高价不为空时，不可为除数字其他类型");
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
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String periodTime = df.format(cal.getTime());
            
            //时段获取
            String sql = " select a.periodno from dcp_period a"
                    + " left join dcp_period_range b on a.eid=b.eid and a.periodno=b.periodno and b.shopid='"+shopId+"'"
                    + " where a.eid='"+eId+"' and a.status='100' and (a.restrictshop='0' or (a.restrictshop='1' and b.shopid is not null))"
                    + " and a.begintime <='"+periodTime+"'"
                    + " and a.endtime >='"+periodTime+"'";
            List<Map<String, Object>> getPeriodNo = this.doQueryData(sql, null);
            if (getPeriodNo!=null && !getPeriodNo.isEmpty()) {
                periodNo = getPeriodNo.get(0).get("PERIODNO").toString();
            }
            
            String promUrl = PosPub.getPROM_INNER_URL(eId);  ///促销服务地址
//            promUrl="http://retaildev.digiwin.com.cn/promService_3.0/openapi";//testtesttest
            if (Check.Null(promUrl))
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数PromUrl未设置 ");
            
            String orderType = request.getOrderType();  // 增加排序类型orderType ： 1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
            if(Check.Null(orderType)){
                orderType="1";
            }
            
            ////图片地址参数获取
            String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            String httpStr = isHttps.equals("1") ? "https://" : "http://";
            String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
            String imagePath = httpStr + domainName + "/resource/image/";
            if (domainName.endsWith("/")) {
                imagePath = httpStr + domainName + "resource/image/";
            }
            
            //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116
            //Map<String, Object> stockMap = new HashMap<>();
            List<PluStock> pluStockList = new ArrayList<>();
            
            //线上商品获取
            sql =getQuerySql1(req,eId,appType,channelId,shopId,periodNo);
            logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetGoodsGroup_Open 线上商品SQL:"+sql+"\r\n");
            List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
            List<Map<String, Object>> getQData = new CopyOnWriteArrayList<>(getQDatas);
            
            res.setDatas(new ArrayList<>());
            String pluOut = "Y";
            if (!getQData.isEmpty()) {                
                //【ID1031885】【货郎3.2.0.3】商城搜索商品【酒精】，点击购物车报错 by jinzma 20230316
                {
                    int num = Integer.parseInt(getQData.get(0).get("NUM").toString());
                    int goodsQty = 999;
                    List<level1Elm> QueryCondition = req.getRequest().getQueryCondition();
                    if (!CollectionUtils.isEmpty(QueryCondition) && QueryCondition.size() == 1) {
                        if (PosPub.isNumericType(QueryCondition.get(0).getGoodsQty())) {
                            goodsQty = Integer.parseInt(QueryCondition.get(0).getGoodsQty());
                        }
                    }
                    if (num <= getQData.size() || goodsQty <= 500) {
                        pluOut = "N";
                    }
                }
            	if(!Check.Null(miniPrice))//价格排序先调用促销否则后调用促销(效能优化)
            	{
            		///删除一级分类失效的资料（即：二级分类生效但一级分类失效的资料）
            		//getQData = deleteClass(getQData);   ///考虑效能不管一级是否生效了

            		//调用基础促销价计算      // 20200929和玲霞沟通确认   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
            		MyCommon comm = new MyCommon();
            		Map<String, Boolean> condition = new HashMap<>(); //查詢條件
            		condition.put("PLUNO", true);
            		condition.put("PLUTYPE", true);
            		List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);

            		///防止商品出现空的，调用促销发生异常
            		boolean isExist=false;
            		for (Map<String, Object> oneData:getQPlu) {
            			String pluNo= oneData.get("PLUNO").toString();
            			if (!Check.Null(pluNo)) {
            				isExist=true;
            				break;
            			}
            		}
            		List<Map<String, Object>> getBasicProm = new ArrayList<>();
            		if (isExist) {
            			getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId, memberId, cardNo, promUrl, getQPlu);
            			if (getBasicProm==null || getBasicProm.isEmpty()){
            				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
            			}
            		}
            		///促销处理
            		for (Map<String, Object> oneData:getQData) {
            			String getQpluNo =oneData.get("PLUNO").toString();
            			String getQpluType = oneData.get("PLUTYPE").toString();
            			String unitId = "";
            			String originalPrice = "";
            			String price = "";
            			String basicPrice = "";
            			String promLable = "";


            			for (Map<String, Object> oneBasicProm:getBasicProm) {
            				String pluNo = oneBasicProm.get("PLUNO").toString();
            				String pluType = oneBasicProm.get("PLUTYPE").toString();
            				if ((pluNo.equals(getQpluNo) && pluType.equals(getQpluType))) {
            					unitId = oneBasicProm.get("UNITID").toString();
            					originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
            					price = oneBasicProm.get("PRICE").toString();
            					basicPrice = oneBasicProm.get("BASICPRICE").toString();
            					promLable = oneBasicProm.get("PROMLABLE").toString();
            					break;
            				}
            			}


            			if (!PosPub.isNumericType(originalPrice))
            				originalPrice="-1";        ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
            			if (!PosPub.isNumericType(price))
            				price="-1";
            			if (!PosPub.isNumericType(basicPrice))
            				basicPrice="-1";

            			oneData.put("ORIPRICE",originalPrice);
            			oneData.put("PRICE",price);
            			oneData.put("BASICPRICE",basicPrice);
            			oneData.put("PROMLABLE",promLable );
            			oneData.put("SUNIT",unitId );
            		}
                    getQData = priceFilter(getQData,miniPrice,maxPrice);
            	}
                //一级分类和二级分类处理
                Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                condition.clear();
                condition.put("CLASSNO", true);		   //查詢條件
                List<Map<String, Object>> getQClass=MapDistinct.getMap(getQData, condition);
                
                //过滤二级分类
                Map<String, Object> condi2= new HashMap<>();
                condi2.put("LEVELID","2");
                List<Map<String, Object>> getQTwoClass = MapDistinct.getWhereMap(getQClass, condi2, true);
                
                //过滤一级分类
                Map<String, Object> condiV= new HashMap<>();
                condiV.put("LEVELID","1");
                getQClass= MapDistinct.getWhereMap(getQClass, condiV, true);
                
                
                //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615  判断是否存在统一发货的门店
//                String deliverShopId = "";
//                sql=" SELECT DELIVERYSHOPID FROM CRM_EXPRESSSET "
//                        + " where EID='"+eId+"' and APPID='"+req.getApiUser().getUserCode()+"' and DELIVERYTYPE='2' and EXPRESS='1' ";
//                List<Map<String, Object>> getDeliverShop = this.doQueryData(sql, null);
//                if (!CollectionUtils.isEmpty(getDeliverShop)){
//                    deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//                }
                String deliverShopId = HelpTools.getDeliverShopId(eId, req.getApiUser().getUserCode(), shopId);
                
                for (Map<String, Object> oneClass : getQClass){
                    DCP_GetGoodsGroup_OpenRes.level1Elm lv1 = res.new level1Elm();
                    String classNo =oneClass.get("CLASSNO").toString();
                    String className =oneClass.get("CLASSNAME").toString();
                    String goodsSortType =oneClass.get("GOODSSORTTYPE").toString();
                    String isShare =oneClass.get("ISSHARE").toString();
                    isShare = Check.Null(isShare)?"0":isShare;
                    
                    //商品显示数量 ex，传10，只返回10个商品
                    String goodsQty="";
                    {
                        List<level1Elm> groups =request.getQueryCondition();
                        if (groups!=null) {
                            for (level1Elm par:groups) {
                                if (classNo.equals(par.getGroupId())){
                                    goodsQty = par.getGoodsQty();
                                }
                            }
                        }
                    }
                    if (!PosPub.isNumeric(goodsQty))
                        goodsQty="";
                    
                    ///商品排序处理 orderType 为1 时 走 原来的排序 为其他时 走orderType 排序
                    List<Map<String, Object>> getQDataOneClass;
                    if(orderType.equals("1")){
                        getQDataOneClass = goodsSort(getQData,goodsSortType);
                    }else{
                        getQDataOneClass = getQData;
                    }
                    
                    int i = 1;  //处理商品显示数量
                    lv1.setGroupGoods(new ArrayList<>());
                    
                    //塞一级分类商品
                    for (Map<String, Object> oneData : getQDataOneClass){
                        if (classNo.equals(oneData.get("CLASSNO").toString()) && oneData.get("LEVELID").toString().equals("1")) {
                            String mallGoodsId = oneData.get("PLUNO").toString();
                            //一级分类下面有商品
                            if (!Check.Null(mallGoodsId)) {
                                DCP_GetGoodsGroup_OpenRes.level2Elm lv2 = res.new level2Elm();
                                String groupId = "";        //二级分类为空
                                String groupName = "";
                                String mallGoodsName = oneData.get("DISPLAYNAME").toString();
                                String pluType = oneData.get("PLUTYPE").toString();
                                String description = oneData.get("DESCRIPTION").toString();
                                String picUrl = oneData.get("PICURL").toString();
                                if (!Check.Null(picUrl)) {
                                    picUrl = imagePath + picUrl;
                                }
//                                String oriPrice = oneData.get("ORIPRICE").toString();
//                                String price = oneData.get("PRICE").toString();
//                                String basicPrice = oneData.get("BASICPRICE").toString();
//                                String promLable = oneData.get("PROMLABLE").toString();
                                String oriPrice = oneData.getOrDefault("ORIPRICE",99999999).toString();
                                String price = oneData.getOrDefault("PRICE",99999999).toString();
                                String basicPrice = oneData.getOrDefault("BASICPRICE",99999999).toString();
                                String promLable = oneData.getOrDefault("PROMLABLE","").toString();
                                String symbolDisplay = oneData.get("SYMBOLDISPLAY").toString();
                                //GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
                                if (Check.Null(symbolDisplay)) {
                                    symbolDisplay = "0";
                                }
                                String symbolType = oneData.get("SYMBOLTYPE").toString();
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
                                
                                String symbolIcon = oneData.get("SYMBOLICON").toString();
                                String symbolText = oneData.get("SYMBOLTEXT").toString();
                                String unit = oneData.getOrDefault("SUNIT",oneData.get("UNITID")).toString();    ///促销接口返回塞入的
                                //String unit = oneData.get("SUNIT").toString();  ///促销返回的
                                //可售量查询
                                String sUnit = oneData.get("UNITID").toString();
                                
                                //门店前端未给值，造成库存查询不准
                                if (Check.Null(shopId)) {
                                    shopId = "";
                                }
                                
                                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                //配送说明  	门店自提|同城配送|全国快递  繁体的同城叫[宅配到府]
                                String deliveryDescription ="";
                                String deliveryDescription2 ="";
                                String shopPickup = oneData.get("SHOPPICKUP").toString();   // 是否支持自提0-否1-是
                                String cityDeliver = oneData.get("CITYDELIVER").toString(); // 是否支持同城配送0-否1-是
                                String expressDeliver = oneData.get("EXPRESSDELIVER").toString(); // 是否支持全国快递0-否1-是
                                
                                
                                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                //1、下单门店没有库存就不能自提
                                //2、当开启了统一发货参数并且当发货门店没有库存就不能全国配送
                                boolean isSelf = true;       //是否支持自提
                                boolean isDelivery = true;   //是否支持统一配送
                                
                                
                                //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                                // 和玲霞确认，如果查询出现异常，返回库存为零  by jinzma 20210427
                                String totalStock="";
                                //门店库存
                                String shopStock2="0";
                                //配送门店库存
                                String deliveryStock2="0";
                                
                                if (!pluStockList.isEmpty() && pluStockList.size()>0){
                                    PluStock pluStock = pluStockList.stream().filter(p->p.getMallGoodsId().equals(mallGoodsId)).findFirst().orElse(null);
                                    if (pluStock !=null) {
                                        totalStock = pluStock.getTotalStock();
                                        BigDecimal shopStock = pluStock.getShopStock();
                                        BigDecimal deliverShopStock = pluStock.getDeliverShopStock();
                                        shopStock2=shopStock.toPlainString();
                                        if (shopStock.compareTo(BigDecimal.ZERO)<=0){
                                            isSelf = false;  //门店库存<=0 不允许自提
                                        }
                                        if (deliverShopStock.compareTo(BigDecimal.ZERO)<=0){
                                            isDelivery = false;  //门店库存<=0 不允许自提
                                        }
                                    }
                                }
                                
                                
                                if (Check.Null(totalStock)&&("1".equals(shopPickup)||"1".equals(cityDeliver))) {
                                    PluStock pluStock = new PluStock();
                                    pluStock.setMallGoodsId(mallGoodsId);
                                    try {
                                        totalStock = PosPub.queryStockQty(dao, eId, mallGoodsId, " ", shopId, channelId, "", sUnit);
                                        if (!PosPub.isNumericType(totalStock)) {
                                            totalStock="0";
                                        }
                                        shopStock2=totalStock;
                                    } catch (SPosCodeException e) {
                                        String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('" + eId + "','" + mallGoodsId + "',' ','" + shopId + "','" + channelId + "','','" + sUnit + "') as qty FROM dual";
                                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: " + totalStockSql);
                                        totalStock = "0";
                                    }
                                    pluStock.setShopStock(new BigDecimal(totalStock));
                                    //此处给默认值，避免后续取值出现异常
                                    pluStock.setDeliverShopStock(BigDecimal.ZERO);
                                    
                                    if (new BigDecimal(totalStock).compareTo(BigDecimal.ZERO)<=0){
                                        isSelf = false;  //门店库存<=0 不允许自提
                                    }
                                    
                                    String deliverShopTotalStock = "0";
                                    //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615
                                    if (!Check.Null(deliverShopId)) {
                                    	if("1".equals(expressDeliver)&&!deliverShopId.equals(shopId)){
                                    		try {
                                                deliverShopTotalStock = PosPub.queryStockQty(dao, eId, mallGoodsId, " ", deliverShopId, channelId, "", sUnit);
                                                
                                            } catch (SPosCodeException e) {
                                                String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('" + eId + "','" + mallGoodsId + "',' ','" + deliverShopId + "','" + channelId + "','','" + sUnit + "') as qty FROM dual";
                                                logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: " + totalStockSql);
                                                isDelivery = false;
                                            }
                                    	}else if("1".equals(expressDeliver)&&deliverShopId.equals(shopId)){
                                    		deliverShopTotalStock=totalStock;
                                    	}
                                    }
                                    
                                    
                                    {
                                    	deliveryStock2=deliverShopTotalStock;
                                        if (PosPub.isNumericType(deliverShopTotalStock)) {
                                            if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliverShopTotalStock)) < 0) {
                                                totalStock = deliverShopTotalStock;
                                            }
                                            if (new BigDecimal(deliverShopTotalStock).compareTo(BigDecimal.ZERO)<=0){
                                                isDelivery = false;  //统一配送门店库存<=0 不允许统一配送
                                            }
                                            pluStock.setDeliverShopStock(new BigDecimal(deliverShopTotalStock));
                                            
                                        }else{
                                            isDelivery = false;    //负数或非数值  统一配送门店没有库存，不允许统一配送
                                        }
                                    }
                                    
                                    ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                                    totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();
                                    shopStock2 = new BigDecimal(shopStock2).setScale(0, RoundingMode.DOWN).toPlainString();
                                    deliveryStock2 = new BigDecimal(deliveryStock2).setScale(0, RoundingMode.DOWN).toPlainString();
                                    
                                    pluStock.setTotalStock(totalStock);
                                    
                                    pluStockList.add(pluStock);
                                }
                                
                                
                                //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116 以下注释
                                // 累计销量 如果无 则为0
                                //String saleQty = oneData.get("SALEQTY").toString();
                                //String baseunit = oneData.get("BASEUNIT").toString();
                                //String totalSales = "";
                                /*try {
                                    // 单位换算 基准单位换算 单位
                                    totalSales = PosPub.getUnitConvert(dao, eId, mallGoodsId, baseunit, unit, saleQty);
                                } catch (Exception e) {
                                    totalSales="";
                                }
                                if (Check.Null(totalSales) || !PosPub.isNumericType(totalSales)) {
                                    totalSales="0";
                                } else {
                                    // 数量全部取整，小数一律抹去
                                    BigDecimal totalSales_b = new BigDecimal(totalSales);
                                    totalSales_b = totalSales_b.setScale(0, RoundingMode.DOWN);
                                    totalSales = totalSales_b.toPlainString();
                                }*/
                                
                                //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下  by jinzma 20211116
                                String totalSales = oneData.get("SALEQTY").toString();
                                
                                
                                
                                if (shopPickup.equals("1") && isSelf) {
                                    deliveryDescription = "门店自提|";
                                    if (PosPub.isNumericType(shopStock2)&&(new BigDecimal(shopStock2)).compareTo(BigDecimal.ZERO)>0) {
                                    	deliveryDescription2 = "门店自提|";
                                    }
                                }
                                if (cityDeliver.equals("1")) {
                                    deliveryDescription = deliveryDescription + "同城配送|";
                                    if (PosPub.isNumericType(shopStock2)&&(new BigDecimal(shopStock2)).compareTo(BigDecimal.ZERO)>0) {
                                    	deliveryDescription2 = deliveryDescription2 + "同城配送|";
                                    }
                                }
                                if (expressDeliver.equals("1") && isDelivery) {
                                    deliveryDescription = deliveryDescription + "全国快递|";
                                    if (PosPub.isNumericType(deliveryStock2)&&(new BigDecimal(deliveryStock2)).compareTo(BigDecimal.ZERO)>0) {
                                    	deliveryDescription2 = deliveryDescription2 + "全国快递|";
                                    }
                                }
                                if (!Check.Null(deliveryDescription) && deliveryDescription.length()>0) {
                                    deliveryDescription = deliveryDescription.substring(0, deliveryDescription.length() - 1);
                                }
                                if (!Check.Null(deliveryDescription2) && deliveryDescription2.length()>0) {
                                	deliveryDescription2 = deliveryDescription2.substring(0, deliveryDescription2.length() - 1);
                                }
                                
                                lv2.setGroupId(groupId);
                                lv2.setGroupName(groupName);
                                lv2.setMallGoodsId(mallGoodsId);
                                lv2.setMallGoodsName(mallGoodsName);
                                lv2.setPluType(pluType);
                                lv2.setDescription(description);
                                lv2.setPicUrl(picUrl);
                                lv2.setOriPrice(oriPrice);
                                lv2.setPrice(price);
                                lv2.setBasicPrice(basicPrice);
                                lv2.setPromLable(promLable);
                                lv2.setSymbolDisplay(symbolDisplay);
                                lv2.setSymbolType(symbolType);
                                lv2.setSymbolIcon(symbolIcon);
                                lv2.setSymbolText(symbolText);
                                lv2.setTotalStock(totalStock);
                                lv2.setUnit(unit);///促销接口：101基础促销价计算 返回unit, 不是从DB里查询返回
                                lv2.setTotalSales(totalSales);
                                lv2.setCreateTime(oneData.get("CREATETIME").toString());
                                lv2.setDeliveryDescription(deliveryDescription);
                                lv2.setDeliveryDescription2(deliveryDescription2);
                                
                                
                                lv1.getGroupGoods().add(lv2);
                                if (!Check.Null(goodsQty) && PosPub.isNumeric(goodsQty)) {
                                    if (i == Integer.parseInt(goodsQty)) {
                                        break;
                                    }
                                }
                                i++;
                            }
                        }
                    }
                    
                    //塞二级分类商品
                    for (Map<String, Object> oneData : getQTwoClass){
                        if (classNo.equals(oneData.get("UPCLASSNO").toString()) && oneData.get("LEVELID").toString().equals("2")) {
                            String detailClassNo = oneData.get("CLASSNO").toString();
                            String detailGoodsSortType = oneData.get("GOODSSORTTYPE").toString();
                            
                            ///商品排序处理 orderType 为1 时 走 原来的排序 为其他时 走orderType 排序
                            List<Map<String, Object>> getQDataTwoClass;
                            if(orderType.equals("1")){
                                getQDataTwoClass = goodsSort(getQData,detailGoodsSortType);
                            }else{
                                getQDataTwoClass = getQData;
                            }
//                            List<Map<String, Object>> getQDataTwoClass = goodsSort(getQData,detailGoodsSortType);
                            
                            {
                                List<level1Elm> groups =request.getQueryCondition();
                                if (groups!=null) {
                                    for (level1Elm par:groups) {
                                        if (detailClassNo.equals(par.getGroupId())) {
                                            goodsQty = par.getGoodsQty();
                                        }
                                    }
                                }
                            }
                            if (!PosPub.isNumeric(goodsQty))
                                goodsQty="";
                            
                            for (Map<String, Object> detailOneData : getQDataTwoClass){
                                if (detailClassNo.equals(detailOneData.get("CLASSNO").toString())) {
                                    String detailPluNo = detailOneData.get("PLUNO").toString();
                                    //二级分类下面有商品
                                    if (!Check.Null(detailPluNo)) {
                                        DCP_GetGoodsGroup_OpenRes.level2Elm lv2 = res.new level2Elm();
                                        String groupId = detailOneData.get("CLASSNO").toString();
                                        String groupName = detailOneData.get("CLASSNAME").toString();
                                        String mallGoodsName = detailOneData.get("DISPLAYNAME").toString();
                                        String pluType = detailOneData.get("PLUTYPE").toString();
                                        String description = detailOneData.get("DESCRIPTION").toString();
                                        String picUrl = detailOneData.get("PICURL").toString();
                                        if (!Check.Null(picUrl)) {
                                            picUrl = imagePath + picUrl;
                                        }                                        
//                                        String oriPrice = detailOneData.get("ORIPRICE").toString();
//                                        String price = detailOneData.get("PRICE").toString();
//                                        String basicPrice = detailOneData.get("BASICPRICE").toString();
//                                        String promLable = detailOneData.get("PROMLABLE").toString();
                                        String oriPrice = detailOneData.getOrDefault("ORIPRICE",99999999).toString();
                                        String price = detailOneData.getOrDefault("PRICE",99999999).toString();
                                        String basicPrice = detailOneData.getOrDefault("BASICPRICE",99999999).toString();
                                        String promLable = detailOneData.getOrDefault("PROMLABLE","").toString();
                                        String unit = detailOneData.getOrDefault("SUNIT",detailOneData.get("UNITID")).toString();    ///促销接口返回塞入的
                                        String symbolDisplay = detailOneData.get("SYMBOLDISPLAY").toString();
                                        //GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
                                        if (Check.Null(symbolDisplay)) {
                                            symbolDisplay = "0";
                                        }
                                        String symbolType = detailOneData.get("SYMBOLTYPE").toString();
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
                                        
                                        String symbolIcon = detailOneData.get("SYMBOLICON").toString();
                                        String symbolText = detailOneData.get("SYMBOLTEXT").toString();
                                       
                                        
                                        //可售量查询
                                        String sUnit = detailOneData.get("UNITID").toString();
                                        //门店前端未给值，造成库存查询不准
                                        if (Check.Null(shopId)) {
                                            shopId = "";
                                        }
                                        
                                        //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                        //1、下单门店没有库存就不能自提
                                        //2、当开启了统一发货参数并且当发货门店没有库存就不能全国配送
                                        boolean isSelf = true;       //是否支持自提
                                        boolean isDelivery = true;   //是否支持统一配送
                                        
                                        //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                                        // 和玲霞确认，如果查询出现异常，返回库存为零  by jinzma 20210427
                                        String totalStock="";
                                        if (!pluStockList.isEmpty() && pluStockList.size()>0){
                                            PluStock pluStock = pluStockList.stream().filter(p->p.getMallGoodsId().equals(detailPluNo)).findFirst().orElse(null);
                                            if (pluStock !=null) {
                                                totalStock = pluStock.getTotalStock();
                                                BigDecimal shopStock = pluStock.getShopStock();
                                                BigDecimal deliverShopStock = pluStock.getDeliverShopStock();
                                                
                                                if (shopStock.compareTo(BigDecimal.ZERO)<=0){
                                                    isSelf = false;  //门店库存<=0 不允许自提
                                                }
                                                if (deliverShopStock.compareTo(BigDecimal.ZERO)<=0){
                                                    isDelivery = false;  //门店库存<=0 不允许自提
                                                }
                                            }
                                        }
                                        
                                        if (Check.Null(totalStock)){
                                            PluStock pluStock = new PluStock();
                                            pluStock.setMallGoodsId(detailPluNo);
                                            try{
                                                totalStock = PosPub.queryStockQty(dao, eId, detailPluNo, " ", shopId, channelId, "", sUnit);
                                                if (!PosPub.isNumericType(totalStock)) {
                                                    totalStock="0";
                                                }
                                                
                                            }catch (SPosCodeException e){
                                                String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+detailPluNo+"',' ','"+shopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                                                logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                                                totalStock="0";
                                            }
                                            pluStock.setShopStock(new BigDecimal(totalStock));
                                            //此处给默认值，避免后续取值出现异常
                                            pluStock.setDeliverShopStock(BigDecimal.ZERO);
                                            
                                            if (new BigDecimal(totalStock).compareTo(BigDecimal.ZERO)<=0){
                                                isSelf = false;  //门店库存<=0 不允许自提
                                            }
                                            
                                            //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615
                                            if (!Check.Null(deliverShopId)) {
                                                try {
                                                    String deliverShopTotalStock = PosPub.queryStockQty(dao, eId, detailPluNo, " ", deliverShopId, channelId, "", sUnit);
                                                    if (PosPub.isNumericType(deliverShopTotalStock)) {
                                                        if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliverShopTotalStock)) < 0) {
                                                            totalStock = deliverShopTotalStock;
                                                        }
                                                        if (new BigDecimal(deliverShopTotalStock).compareTo(BigDecimal.ZERO)<=0){
                                                            isDelivery = false;  //统一配送门店库存<=0 不允许统一配送
                                                        }
                                                        pluStock.setDeliverShopStock(new BigDecimal(deliverShopTotalStock));
                                                    }else{
                                                        isDelivery = false;    //负数或非数值  统一配送门店没有库存，不允许统一配送
                                                    }
                                                } catch (SPosCodeException e) {
                                                    String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('" + eId + "','" + detailPluNo + "',' ','" + deliverShopId + "','" + channelId + "','','" + sUnit + "') as qty FROM dual";
                                                    logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: " + totalStockSql);
                                                    isDelivery = false;
                                                }
                                            }
                                            
                                            
                                            ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                                            totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();
                                            
                                            pluStock.setTotalStock(totalStock);
                                            
                                            pluStockList.add(pluStock);
                                        }
                                        
                                        
                                        //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116 以下注释
                                        // 累计销量 如果无 则为0
                                        /*String saleQty = detailOneData.get("SALEQTY").toString();
                                        String baseunit = detailOneData.get("BASEUNIT").toString();
                                        String totalSales = "";
                                        try {
                                            // 单位换算 基准单位换算 单位
                                            totalSales = PosPub.getUnitConvert(dao, eId, detailPluNo, baseunit, unit, saleQty);
                                        } catch (Exception e) {
                                            totalSales="";
                                        }
                                        if (Check.Null(totalSales) || !PosPub.isNumericType(totalSales)) {
                                            totalSales="0";
                                        } else {
                                            // 数量全部取整，小数一律抹去
                                            BigDecimal totalSales_b = new BigDecimal(totalSales);
                                            totalSales_b = totalSales_b.setScale(0, RoundingMode.DOWN);
                                            totalSales = totalSales_b.toPlainString();
                                        }*/
                                        
                                        //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116
                                        String totalSales = detailOneData.get("SALEQTY").toString();
                                        
                                        //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                        //配送说明  	门店自提|同城配送|全国快递  繁体的同城叫[宅配到府]
                                        String deliveryDescription ="";
                                        String shopPickup = detailOneData.get("SHOPPICKUP").toString();   // 是否支持自提0-否1-是
                                        String cityDeliver = detailOneData.get("CITYDELIVER").toString(); // 是否支持同城配送0-否1-是
                                        String expressDeliver = detailOneData.get("EXPRESSDELIVER").toString(); // 是否支持全国快递0-否1-是
                                        
                                        if (shopPickup.equals("1") && isSelf) {
                                            deliveryDescription = "门店自提|";
                                        }
                                        if (cityDeliver.equals("1")) {
                                            deliveryDescription = deliveryDescription + "同城配送|";
                                        }
                                        if (expressDeliver.equals("1") && isDelivery) {
                                            deliveryDescription = deliveryDescription + "全国快递|";
                                        }
                                        if (!Check.Null(deliveryDescription) && deliveryDescription.length()>0) {
                                            deliveryDescription = deliveryDescription.substring(0, deliveryDescription.length() - 1);
                                        }
                                        
                                        lv2.setGroupId(groupId);
                                        lv2.setGroupName(groupName);
                                        lv2.setMallGoodsId(detailPluNo);
                                        lv2.setMallGoodsName(mallGoodsName);
                                        lv2.setPluType(pluType);
                                        lv2.setDescription(description);
                                        lv2.setPicUrl(picUrl);
                                        lv2.setOriPrice(oriPrice);
                                        lv2.setPrice(price);
                                        lv2.setBasicPrice(basicPrice);
                                        lv2.setPromLable(promLable);
                                        lv2.setSymbolDisplay(symbolDisplay);
                                        lv2.setSymbolType(symbolType);
                                        lv2.setSymbolIcon(symbolIcon);
                                        lv2.setSymbolText(symbolText);
                                        lv2.setTotalStock(totalStock);
                                        lv2.setUnit(unit);///促销接口：101基础促销价计算 返回unit, 不是从DB里查询返回
                                        lv2.setTotalSales(totalSales);
                                        lv2.setCreateTime(oneData.get("CREATETIME").toString());
                                        lv2.setDeliveryDescription(deliveryDescription);
                                        
                                        lv1.getGroupGoods().add(lv2);
                                        if (!Check.Null(goodsQty) && PosPub.isNumeric(goodsQty)) {
                                            if (i == Integer.parseInt(goodsQty)) {
                                                break;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                    //促销计算赋值
                	if(Check.Null(miniPrice))
                	{
                		List<Map<String, Object>> getQPlu =new ArrayList<>();
                		for(DCP_GetGoodsGroup_OpenRes.level2Elm par:lv1.getGroupGoods())
                		{
                			Map<String, Object> pluMap=new HashMap<>();
                			pluMap.put("PLUNO", par.getMallGoodsId());
                			pluMap.put("PLUTYPE", par.getPluType());
                			pluMap.put("UNITID", par.getUnit());
                			getQPlu.add(pluMap);
                		}
                		MyCommon comm = new MyCommon();
                		List<Map<String, Object>> getBasicProm = new ArrayList<>();
                		getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId,
                				memberId, cardNo, promUrl,getQPlu);
                		if (getBasicProm==null || getBasicProm.isEmpty()){
                			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
                		}
                		///促销处理
                		for (DCP_GetGoodsGroup_OpenRes.level2Elm par:lv1.getGroupGoods()) {
                			String getQpluNo =par.getMallGoodsId();
                			String getQpluType = par.getPluType();
                			String unitId = "";
                			String originalPrice = "";
                			String price = "";
                			String basicPrice = "";
                			String promLable = "";
                			for (Map<String, Object> oneBasicProm:getBasicProm) {
                				String pluNo = oneBasicProm.get("PLUNO").toString();
                				String pluType = oneBasicProm.get("PLUTYPE").toString();
                				if ((pluNo.equals(getQpluNo) && pluType.equals(getQpluType))) {
                					unitId = oneBasicProm.get("UNITID").toString();
                					originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
                					price = oneBasicProm.get("PRICE").toString();
                					basicPrice = oneBasicProm.get("BASICPRICE").toString();
                					promLable = oneBasicProm.get("PROMLABLE").toString();
                					break;
                				}
                			}


                			if (!PosPub.isNumericType(originalPrice))
                				originalPrice="-1";        ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
                			if (!PosPub.isNumericType(price))
                				price="-1";
                			if (!PosPub.isNumericType(basicPrice))
                				basicPrice="-1";
                			
                			par.setOriPrice(originalPrice);
                			par.setPrice(basicPrice);
                			par.setBasicPrice(basicPrice);
                			par.setPromLable(promLable);
                			par.setUnit(unitId);               			
//                			oneData.put("ORIPRICE",originalPrice);
//                			oneData.put("PRICE",price);
//                			oneData.put("BASICPRICE",basicPrice);
//                			oneData.put("PROMLABLE",promLable );
//                			oneData.put("SUNIT",unitId );
                		}
                	}
                    lv1.setGroupId(classNo);
                    lv1.setGroupName(className);
                    lv1.setIsShare(isShare);
                    lv1.setLevelId("1");
                    
                    res.getDatas().add(lv1);
                }
                
                /////把二级分类塞回到一级分类处理
                {
                    List<level1Elm> groups =request.getQueryCondition();
                    if (groups!=null) {
                        for (level1Elm par:groups) {
                            String queryGroupId = par.getGroupId();
                            String goodsQty = par.getGoodsQty();
                            if (!PosPub.isNumeric(goodsQty))
                                goodsQty="";
                            
                            condition.clear();
                            condition.put("CLASSNO", true);
                            List<Map<String, Object>> getQDetailClass=MapDistinct.getMap(getQData, condition);
                            
                            for (Map<String, Object> oneData : getQDetailClass){
                                if (queryGroupId.equals(oneData.get("CLASSNO").toString()) && oneData.get("LEVELID").toString().equals("2")) {
                                    DCP_GetGoodsGroup_OpenRes.level1Elm lv1 = res.new level1Elm();
                                    String detailGoodsSortType = oneData.get("GOODSSORTTYPE").toString();
                                    String detailGroupId = oneData.get("CLASSNO").toString();
                                    String detailGroupName = oneData.get("CLASSNAME").toString();
                                    String isShare =oneData.get("ISSHARE").toString();
                                    isShare = Check.Null(isShare)?"0":isShare;
                                    
                                    ///商品排序处理 orderType 为1 时 走 原来的排序 为其他时 走orderType 排序
                                    List<Map<String, Object>> getQDataDetail;
                                    if(orderType.equals("1")){
                                        getQDataDetail = goodsSort(getQData,detailGoodsSortType);
                                    }else{
                                        getQDataDetail = getQData;
                                    }
                                    
                                    //List<Map<String, Object>> getQDataDetail = goodsSort(getQData,detailGoodsSortType);
                                    int i = 1;  //处理商品显示数量
                                    lv1.setGroupGoods(new ArrayList<>());
                                    for (Map<String, Object> detailOneData : getQDataDetail){
                                        if (queryGroupId.equals(detailOneData.get("CLASSNO").toString()) && oneData.get("LEVELID").toString().equals("2")) {
                                            String detailPluNo = detailOneData.get("PLUNO").toString();
                                            //二级分类下面有商品
                                            if (!Check.Null(detailPluNo)) {
                                                DCP_GetGoodsGroup_OpenRes.level2Elm lv2 = res.new level2Elm();
                                                String mallGoodsName = detailOneData.get("DISPLAYNAME").toString();
                                                String pluType = detailOneData.get("PLUTYPE").toString();
                                                String description = detailOneData.get("DESCRIPTION").toString();
                                                String picUrl = detailOneData.get("PICURL").toString();
                                                if (!Check.Null(picUrl)) {
                                                    picUrl = imagePath + picUrl;
                                                }
                                                String oriPrice = detailOneData.get("ORIPRICE").toString();
                                                String price = detailOneData.get("PRICE").toString();
                                                String basicPrice = detailOneData.get("BASICPRICE").toString();
                                                String promLable = detailOneData.get("PROMLABLE").toString();
                                                String symbolDisplay = detailOneData.get("SYMBOLDISPLAY").toString();
                                                //GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
                                                if (Check.Null(symbolDisplay))
                                                    symbolDisplay="0";
                                                
                                                String symbolType = detailOneData.get("SYMBOLTYPE").toString();
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
                                                
                                                String symbolIcon = detailOneData.get("SYMBOLICON").toString();
                                                String symbolText = detailOneData.get("SYMBOLTEXT").toString();
                                                String unit = detailOneData.get("SUNIT").toString();  ///促销接口返回塞入的
                                                
                                                //可售量查询
                                                String sUnit = detailOneData.get("UNITID").toString();
                                                //门店前端未给值，造成库存查询不准
                                                if (Check.Null(shopId)) {
                                                    shopId = "";
                                                }
                                                
                                                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                                //1、下单门店没有库存就不能自提
                                                //2、当开启了统一发货参数并且当发货门店没有库存就不能全国配送
                                                boolean isSelf = true;       //是否支持自提
                                                boolean isDelivery = true;   //是否支持统一配送
                                                
                                                //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                                                // 和玲霞确认，如果查询出现异常，返回库存为零  by jinzma 20210427
                                                String totalStock="";
                                                if (!pluStockList.isEmpty() && pluStockList.size()>0){
                                                    PluStock pluStock = pluStockList.stream().filter(p->p.getMallGoodsId().equals(detailPluNo)).findFirst().orElse(null);
                                                    if (pluStock !=null) {
                                                        totalStock = pluStock.getTotalStock();
                                                        BigDecimal shopStock = pluStock.getShopStock();
                                                        BigDecimal deliverShopStock = pluStock.getDeliverShopStock();
                                                        
                                                        if (shopStock.compareTo(BigDecimal.ZERO)<=0){
                                                            isSelf = false;  //门店库存<=0 不允许自提
                                                        }
                                                        if (deliverShopStock.compareTo(BigDecimal.ZERO)<=0){
                                                            isDelivery = false;  //门店库存<=0 不允许自提
                                                        }
                                                    }
                                                }
                                                
                                                if (Check.Null(totalStock)) {
                                                    PluStock pluStock = new PluStock();
                                                    pluStock.setMallGoodsId(detailPluNo);
                                                    
                                                    try {
                                                        totalStock = PosPub.queryStockQty(dao, eId, detailPluNo, " ", shopId, channelId,"", sUnit);
                                                        if (!PosPub.isNumericType(totalStock)) {
                                                            totalStock="0";
                                                        }
                                                    } catch (SPosCodeException e) {
                                                        String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('" + eId + "','" + detailPluNo + "',' ','" + shopId + "','" + channelId + "','','" + sUnit + "') as qty FROM dual";
                                                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: " + totalStockSql);
                                                        totalStock = "0";
                                                    }
                                                    
                                                    pluStock.setShopStock(new BigDecimal(totalStock));
                                                    //此处给默认值，避免后续取值出现异常
                                                    pluStock.setDeliverShopStock(BigDecimal.ZERO);
                                                    
                                                    if (new BigDecimal(totalStock).compareTo(BigDecimal.ZERO)<=0){
                                                        isSelf = false;  //门店库存<=0 不允许自提
                                                    }
                                                    
                                                    //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615
                                                    if (!Check.Null(deliverShopId)) {
                                                        try {
                                                            String deliverShopTotalStock = PosPub.queryStockQty(dao, eId, detailPluNo, " ", deliverShopId, channelId, "", sUnit);
                                                            if (PosPub.isNumericType(deliverShopTotalStock)) {
                                                                if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliverShopTotalStock)) < 0) {
                                                                    totalStock = deliverShopTotalStock;
                                                                }
                                                                if (new BigDecimal(deliverShopTotalStock).compareTo(BigDecimal.ZERO)<=0){
                                                                    isDelivery = false;  //统一配送门店库存<=0 不允许统一配送
                                                                }
                                                                pluStock.setDeliverShopStock(new BigDecimal(deliverShopTotalStock));
                                                            }else{
                                                                isDelivery = false;    //负数或非数值  统一配送门店没有库存，不允许统一配送
                                                            }
                                                        } catch (SPosCodeException e) {
                                                            String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('" + eId + "','" + detailPluNo + "',' ','" + deliverShopId + "','" + channelId + "','','" + sUnit + "') as qty FROM dual";
                                                            logger.error("\r\nrequestId=" + req.getRequestId() + ",plantType=" + req.getPlantType() + ",version=" + req.getVersion() + "," + "DCP_GetGoodsGroup_Open在查询商品可售量时发生异常,sql语句: " + totalStockSql);
                                                            isDelivery = false;
                                                        }
                                                    }
                                                    
                                                    
                                                    ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                                                    totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();
                                                    
                                                    pluStock.setTotalStock(totalStock);
                                                    
                                                    pluStockList.add(pluStock);
                                                }
                                                
                                                
                                                //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116 以下注释
                                                // 累计销量 如果无 则为0
                                                /*String saleQty = detailOneData.get("SALEQTY").toString();
                                                String baseunit = detailOneData.get("BASEUNIT").toString();
                                                String totalSales = "";
                                                try {
                                                    // 单位换算 基准单位换算 单位
                                                    totalSales = PosPub.getUnitConvert(dao, eId, detailPluNo, baseunit, unit, saleQty);
                                                } catch (Exception e) {
                                                    totalSales="";
                                                }
                                                if (Check.Null(totalSales) || !PosPub.isNumericType(totalSales)) {
                                                    totalSales="0";
                                                } else {
                                                    // 数量全部取整，小数一律抹去
                                                    BigDecimal totalSales_b = new BigDecimal(totalSales);
                                                    totalSales_b = totalSales_b.setScale(0, RoundingMode.DOWN);
                                                    totalSales = totalSales_b.toPlainString();
                                                }*/
                                                
                                                //【ID1021993】【货郎先生3.0.0.7】小程序从商品分类中进入商品列表比较慢，需要优化下 by jinzma 20211116
                                                String totalSales = detailOneData.get("SALEQTY").toString();
                                                
                                                //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                                                //配送说明  	门店自提|同城配送|全国快递  繁体的同城叫[宅配到府]
                                                String deliveryDescription ="";
                                                String shopPickup = detailOneData.get("SHOPPICKUP").toString();   // 是否支持自提0-否1-是
                                                String cityDeliver = detailOneData.get("CITYDELIVER").toString(); // 是否支持同城配送0-否1-是
                                                String expressDeliver = detailOneData.get("EXPRESSDELIVER").toString(); // 是否支持全国快递0-否1-是
                                                
                                                if (shopPickup.equals("1") && isSelf) {
                                                    deliveryDescription = "门店自提|";
                                                }
                                                if (cityDeliver.equals("1")) {
                                                    deliveryDescription = deliveryDescription + "同城配送|";
                                                }
                                                if (expressDeliver.equals("1") && isDelivery) {
                                                    deliveryDescription = deliveryDescription + "全国快递|";
                                                }
                                                if (!Check.Null(deliveryDescription) && deliveryDescription.length()>0) {
                                                    deliveryDescription = deliveryDescription.substring(0, deliveryDescription.length() - 1);
                                                }
                                                
                                                lv2.setGroupId(detailGroupId);
                                                lv2.setGroupName(detailGroupName);
                                                lv2.setMallGoodsId(detailPluNo);
                                                lv2.setMallGoodsName(mallGoodsName);
                                                lv2.setPluType(pluType);
                                                lv2.setDescription(description);
                                                lv2.setPicUrl(picUrl);
                                                lv2.setOriPrice(oriPrice);
                                                lv2.setPrice(price);
                                                lv2.setBasicPrice(basicPrice);
                                                lv2.setPromLable(promLable);
                                                lv2.setSymbolDisplay(symbolDisplay);
                                                lv2.setSymbolType(symbolType);
                                                lv2.setSymbolIcon(symbolIcon);
                                                lv2.setSymbolText(symbolText);
                                                lv2.setTotalStock(totalStock);
                                                lv2.setUnit(unit);///促销接口：101基础促销价计算 返回unit, 不是从DB里查询返回
                                                lv2.setTotalSales(totalSales);
                                                lv2.setCreateTime(oneData.get("CREATETIME").toString());
                                                lv2.setDeliveryDescription(deliveryDescription);
                                                
                                                
                                                lv1.getGroupGoods().add(lv2);
                                                if (!Check.Null(goodsQty)) {
                                                    if (i == Integer.parseInt(goodsQty)) {
                                                        break;
                                                    }
                                                }
                                                i++;
                                            }
                                        }
                                    }
                                    
                                    lv1.setGroupId(detailGroupId);
                                    lv1.setGroupName(detailGroupName);
                                    lv1.setIsShare(isShare);
                                    lv1.setLevelId("2");
                                    
                                    res.getDatas().add(lv1);
                                }
                            }
                        }
                    }
                }
                
            }
            
            if (!orderType.equals("1")){
                // 如果不为默认排序 则1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
                res.setDatas(goodsSort2(res.getDatas(), orderType));
            }
            
            //【ID1032450】 //【老约翰】小程序登录首页报错  by jinzma 20230411 CRM取不到pluOut报错
            res.setPluOut(pluOut);
            
            return res;
            
        } catch (Exception e) {
        	logger.error(e);
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
        
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_GetGoodsGroup_OpenReq req) throws Exception {
        return null;
    }
    
    private String getQuerySql1(DCP_GetGoodsGroup_OpenReq req,String eId,String appType,String channelId,String shopId,String periodNo) {
        String langType=req.getLangType();
        levelElm request = req.getRequest();
        StringBuffer sb = new StringBuffer();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        String groupId="";
        String decodeGroupId = "";
        String brand = "";  // 品牌
        String searchString = ""; //  搜索(支持宝贝名称)
        List<level1Elm> groups = new ArrayList<>();
        String classType=request.getClassType();
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        if (request!=null) {
            groups =request.getQueryCondition();
            if (groups!=null) {
                int i = 1;
                for (level1Elm par:groups) {
                    groupId = groupId+"'"+par.getGroupId()+"',";
                    decodeGroupId = decodeGroupId + "'"+par.getGroupId()+"',"+i+",";
                    i++;
                }
                if (groupId.length()>1) {
                    groupId = groupId.substring(0, groupId.length() - 1);
                    decodeGroupId = decodeGroupId.substring(0, decodeGroupId.length() - 1);
                }
            }
            brand = request.getBrand();
            searchString  = request.getSearchString();
        }
        
        if(Check.Null(classType)&&Check.Null(groupId)){
        	classType="ONLINE";        
        }
        
        sb.append(" with invalidclass as ("
                + " select a.classno from dcp_class a"
                + " inner join dcp_class_range b on a.eid=b.eid and a.classno=b.classno and a.classtype=b.classtype and b.rangetype='2' and b.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"' and a.restrictshop=2 " );// and a.status='100'
        
        if(classType!=null&&classType.length()>0){
        	sb.append(" and a.classtype='"+classType+"' ");
        }else{
        	sb.append(" and a.classtype <>'POS' ");
        }
        
        sb.append(" group by a.classno)"
                + " ");
        
        sb.append(" ,goodsimage_symbol as ("
                + " select pluno,symbolType,symbolText,symbolIcon from ("
                + " select a.*,row_number() over (partition by pluno order by indx) as rn from("
                + " select pluno,symbolType,symbolText,symbolIcon,1 as indx from ("
                + " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
                + " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
                + " where a.eid ='"+eId+"' and a.apptype='"+appType+"' "
                + " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
                + " )a where rn='1'"
                + " union all"
                + " select pluno,symbolType,symbolText,symbolIcon,2 as indx from ("
                + " select pluno,symbolType,symboltag as symbolText,symbolimage as symbolIcon,item,"
                + " row_number() over (partition by pluno order by item desc) as rn from dcp_goodsimage_symbol a"
                + " where a.eid ='"+eId+"' and a.apptype='ALL' "
                + " and trunc(begindate)<=trunc(sysdate) and trunc(enddate)>=trunc(sysdate)"
                + " )a where rn='1')a "
                + " ) where rn='1')"
                + " ");
        
        sb.append(" ,goodsimage as ("
                + " select pluno,picUrl,symbolDisplay from ("
                + " select a.*,row_number() over (partition by pluno order by indx) as rn from ("
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,1 as indx from dcp_goodsimage a "
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' "
                + " union all"
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' )a"
                + " ) where rn='1' )"
                + " ");
        
        sb.append(" ,plu as ("
                + " select a.pluno,max(a.createtime) as createtime from dcp_goods_shelf_range a"
                + " left join (select a.pluno from dcp_goods_shelf_range a "
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
                + " group by a.pluno) "
                + " ");
        
        sb.append(" ,masterplu as ("
                + " select a.masterpluno from dcp_mspecgoods_subgoods a"
                + " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100'"
                + " where a.eid='"+eId+"'");
        sb.append(" group by a.masterpluno )");
        
        sb.append(" ,class as ("
                + " select a.eid,a.classno,a.classtype,a.levelid,a.upclassno,a.goodssorttype,a.sortid,a.restrictshop,a.restrictchannel,a.restrictperiod,a.ISSHARE from dcp_class a"
                + " left join dcp_class_range b1 on a.eid=b1.eid and a.classno=b1.classno and a.classtype=b1.classtype and b1.rangetype='2' and b1.id='"+shopId+"'"
                + " left join dcp_class_range b2 on a.eid=b2.eid and a.classno=b2.classno and a.classtype=b2.classtype and b2.rangetype='3' and b2.id='"+channelId+"'"
                + " left join dcp_class_range b3 on a.eid=b3.eid and a.classno=b3.classno and a.classtype=b3.classtype and b3.rangetype='5' and b3.id='"+periodNo+"'"
                + " left join invalidclass on a.classno=invalidclass.classno"
                + " where a.eid='"+eId+"' and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"'"
                + " and (a.restrictshop=0 or (a.restrictshop=1 and b1.id is not null))"
                + " and (a.restrictchannel=0 or (a.restrictchannel=1 and b2.id is not null))"
                + " and (a.restrictperiod=0 or (a.restrictperiod=1 and b3.id is not null))"
                + " and invalidclass.classno is null");
        
        if(classType!=null&&classType.length()>0){
        	sb.append(" and a.classtype='"+classType+"'");
        }else{
        	sb.append(" and a.classtype <>'POS' ");
        }
        if (!Check.Null(groupId)) {
            sb.append(" and (a.classno in ("+groupId+") or a.upclassno in ("+groupId+"))");
        }
        sb.append(" )" );
        
        
        //sb.append( " ,stock as ( "
        //        + " SELECT /*+ USE_HASH(a)*/ "            ///*+ index(a TW_STOCK_DAY_STATIC_IDX03)*/  by jinzma 20211220
        //        + " a.pluno,SUM(a.SALE_QTY) AS sale_qty FROM DCP_STOCK_DAY_STATIC a "
        //        + " inner join plu on a.Pluno=plu.pluno "
        //        + " WHERE a.eid = '"+eId+"' AND a.EDATE >= TO_CHAR( add_months(trunc(sysdate),-1), 'yyyymmdd') "
        //        + " and a.EDATE <= TO_CHAR( TRUNC(SYSDATE - 1), 'yyyymmdd') ");
        //if(!Check.Null(shopId)){
        //   sb.append(" and a.ORGANIZATIONNO = '"+shopId+"'");
        //}
        //if(!Check.Null(out_cost_warehouse)){
        //    sb.append(" AND a.WAREHOUSE = '"+out_cost_warehouse+"'");
        //}
        //sb.append(" GROUP BY a.pluno )");



		/*【ID1015340】 V3--DCP_GetGoodsGroup_Open,禁用商品会传空单位给促销接口,导致服务报错 玲霞 BY JZMA 20210207
		单规格,商品不能失效(DCP_GOODS.STATUS=100)
		多规格不能明细都失效,
		商城商品不能失效DCP_GOODS_ONLINE.STATUS<>90
		表说明：
		DCP_MSPECGOODS_SUBGOODS.PLUNO
		DCP_GOOD_ONLINE里都是商城商品
		plutype来区分单规格多规格*/
        
        //【ID1031885】【货郎3.2.0.3】商城搜索商品【酒精】，点击购物车报错 by jinzma 20230316
   //     if (groups!=null && groups.size()==1) {
            sb.append(" select * from (");
            if (!Check.Null(groupId)) {
              //  sb.append(" order by decode(a.classno,"+decodeGroupId+"),b.sortid ");
            	sb.append(" select count(*) over() num,row_number() over (order by decode(a.classno,"+decodeGroupId+"),b.sortid,b.pluno) rn ,");
            }else
            {
            	sb.append(" select count(*) over() num,row_number() over (order by a.sortid,b.sortid,b.pluno) rn ,");
            }
//        }else{
//            sb.append(" select N'0' as num,");
//        }
        
        
        sb.append(" a.classno,a.goodssorttype,a.levelid,a.upclassno,"
                + " b.pluno,b.plutype,b.sortid,b.createtime,b.displayname,b.description,b.picUrl,b.symbolDisplay,"
                + " b.symbolType,b.symbolIcon,b.symbolText,b.unitId,"
                + " cl1.classname,a.ISSHARE,"
                + " nvl(sale.qty,0) as saleQty,"
                + " b.BASEUNIT,b.shoppickup,b.citydeliver,b.expressdeliver  "
                + " from class a"
                + " left join ("
                + " select a.eid,a.classno,a.classtype,a.pluno,a.plutype,a.sortid,"
                + " to_char(nvl(plu.createtime,sysdate),'yyyymmddhh24miss') as createtime,"
                + " gol.displayname,gol.simpledescription as description,"
                + " goodsimage.picUrl,goodsimage.symbolDisplay,"
                + " goodsimage_symbol.symbolType,goodsimage_symbol.symbolIcon,goodsimage_symbol.symbolText,"
                + " goods.sunit as unitId,goods.BRAND,goods.BASEUNIT,"
                + " go.shoppickup,go.citydeliver,go.expressdeliver "
                + " from dcp_class_goods a"
                + " inner join dcp_goods_online go on a.eid=go.eid and a.pluno=go.pluno and go.status<>'90'"
                + " inner join plu on a.pluno=plu.pluno"
                + " left join goodsimage on goodsimage.pluno=a.pluno"
                + " left join goodsimage_symbol on goodsimage_symbol.pluno=a.pluno"
                + " left join dcp_goods goods on goods.eid=a.eid and goods.pluno=a.pluno and goods.status='100'"
                + " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
                + " left join masterplu on masterplu.masterpluno = a.pluno"
                + " where (a.plutype<>'MULTISPEC' and goods.pluno is not null) or (a.plutype='MULTISPEC' and masterplu.masterpluno is not null) "
                + " ) b on a.eid=b.eid and a.classno=b.classno and a.classtype=b.classtype"
                + " left join dcp_class_lang cl1 on cl1.eid=a.eid and cl1.classno=a.classno and cl1.lang_type='"+langType+"'"
                + "  ");
        
        if(classType!=null&&classType.length()>0){
        	sb.append(" and cl1.classtype='"+classType+"' ");
        }else{
        	sb.append(" and cl1.classtype <>'POS' ");
        }
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230220
        if(!Check.Null(shopId)){
            sb.append(" left join dcp_sale_goods sale on sale.eid=a.eid and sale.shopid='"+shopId+"' and sale.pluno=b.pluno ");
        }else{
            sb.append(" left join dcp_sale_goods sale on sale.eid=a.eid and sale.shopid=' ' and sale.pluno=b.pluno ");
        }
        
        sb.append(" left join DCP_BRAND_LANG dbl on b.eid = dbl.eid and dbl.BRANDNO = b.BRAND and dbl.LANG_TYPE = '"+langType+"' "
                + " where a.eid='"+eId+"' " );
        if(!Check.Null(searchString)){
            sb.append(" AND b.DISPLAYNAME LIKE '%%"+searchString+"%%'");
        }
        if(!Check.Null(brand)){
            sb.append(" AND dbl.BRAND_NAME LIKE '%%"+brand+"%%'");
        }
        //【ID1017053】【货郎】小程序里页面和【会员商城页面】预览效果不同，且两级商品分组没办法做出区分 by jinzma 20210506
//        if (!Check.Null(groupId)) {
//            sb.append(" order by decode(a.classno,"+decodeGroupId+"),b.sortid ");
//        }else{
//            sb.append(" order by a.sortid,b.sortid ");
//        }
        
        //【ID1031885】【货郎3.2.0.3】商城搜索商品【酒精】，点击购物车报错 by jinzma 20230316
//        if (groups!=null && groups.size()==1) {
//            sb.append(" )a where rn<=500");
//        }else{
//            sb.append(" ");
//        }
        sb.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
        return sb.toString();
    }
    
    private List<Map<String, Object>> deleteClass( List<Map<String, Object>> getQData) {
        
        List<Map<String, Object>> returnQData = new ArrayList<>();
        for (Map<String, Object> oneData: getQData)
        {
            String upClassNo = oneData.get("UPCLASSNO").toString();
            if (!Check.Null(upClassNo))
            {
                boolean isExist = false;
                for (Map<String, Object> classData: getQData)
                {
                    if (upClassNo.equals(classData.get("CLASSNO").toString()))
                    {
                        isExist=true;
                        break;
                    }
                }
                if (isExist)
                    returnQData.add(oneData);
            }
            else
            {
                returnQData.add(oneData);
            }
        }
        return returnQData;
    }
    
    private List<Map<String, Object>> goodsSort( List<Map<String, Object>> getQData,String goodsSortType) {
        ///商品排序方式：1-默认顺序 2-销量降序(玲霞说暂时不考虑)
        switch (goodsSortType){
            case "3":  //3-价格升序     (需要jdk1.8以上)
                getQData.sort((o1, o2) -> {
                    String d1 = o1.get("PRICE").toString();
                    String d2 = o2.get("PRICE").toString();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumericType(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumericType(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d1).compareTo(Double.valueOf(d2));

            /*if (Check.Null(o1.get("PRICE").toString()) || Check.Null(o2.get("PRICE").toString())) {
                return -1;
            }
            Double d1 = Double.parseDouble(o1.get("PRICE").toString());
            Double d2 = Double.parseDouble(o2.get("PRICE").toString());
            return d1.compareTo(d2);*/
                
                });
                break;
            case "4":  //4-价格降序     (需要jdk1.8以上)
                getQData.sort((o1, o2) -> {
                    String d1 = o1.get("PRICE").toString();
                    String d2 = o2.get("PRICE").toString();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumericType(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumericType(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d2).compareTo(Double.valueOf(d1));

            /*if (Check.Null(o1.get("PRICE").toString()) || Check.Null(o2.get("PRICE").toString())) {
                return -1;
            }
            Double d1 = Double.parseDouble(o1.get("PRICE").toString());
            Double d2 = Double.parseDouble(o2.get("PRICE").toString());
            return d2.compareTo(d1);*/
                
                });
                break;
            case "5":  //5-上架时间降序   (需要jdk1.8以上)
                getQData.sort((o1, o2) -> {
                    String d1 = o1.get("CREATETIME").toString();
                    String d2 = o2.get("CREATETIME").toString();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumeric(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumeric(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d2).compareTo(Double.valueOf(d1));
            /*if (Check.Null(o1.get("CREATETIME").toString()) || Check.Null(o2.get("CREATETIME").toString())) {
                return -1;
            }
            Double d1 = Double.parseDouble(o1.get("CREATETIME").toString());
            Double d2 = Double.parseDouble(o2.get("CREATETIME").toString());
            return d2.compareTo(d1);*/
                
                });
                break;
            default:
                break;
        }
        
        return getQData;
        
    }
    
    private List<DCP_GetGoodsGroup_OpenRes.level1Elm> goodsSort2(List<DCP_GetGoodsGroup_OpenRes.level1Elm> datas,String orderType){
        for (DCP_GetGoodsGroup_OpenRes.level1Elm data : datas) {
            List<DCP_GetGoodsGroup_OpenRes.level2Elm> level2Elms = goodsSort3(data.getGroupGoods(), orderType);
            data.setGroupGoods(level2Elms);
        }
        return datas;
    }
    
    private  List<DCP_GetGoodsGroup_OpenRes.level2Elm> goodsSort3(List<DCP_GetGoodsGroup_OpenRes.level2Elm> groupGoods,String orderType){
        ///商品排序方式：1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
        switch (orderType) {
            case "2":  // 2-销量降序    (需要jdk1.8以上)
                groupGoods.sort((o1, o2) -> {
                    String d1 = o1.getTotalSales();
                    String d2 = o2.getTotalSales();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumeric(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumeric(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d2).compareTo(Double.valueOf(d1));
                });
                break;
            case "3":  // 3-价格降序     (需要jdk1.8以上)
                groupGoods.sort((o1, o2) -> {
                    String d1 = o1.getPrice();
                    String d2 = o2.getPrice();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumericType(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumericType(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d2).compareTo(Double.valueOf(d1));
                });
                break;
            case "4":  // 3-价格升序     (需要jdk1.8以上)
                groupGoods.sort((o1, o2) -> {
                    String d1 = o1.getPrice();
                    String d2 = o2.getPrice();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumericType(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumericType(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d1).compareTo(Double.valueOf(d2));
                });
                break;
            case "5":  // 3-上架时间降序     (需要jdk1.8以上)
                groupGoods.sort((o1, o2) -> {
                    String d1 = o1.getCreateTime();
                    String d2 = o2.getCreateTime();
                    if (d1 == null && d2 == null) {
                        return 0;
                    }
                    if (!PosPub.isNumeric(d1)) {
                        return 1;
                    }
                    if (!PosPub.isNumeric(d2)) {
                        return -1;
                    }
                    return Double.valueOf(d2).compareTo(Double.valueOf(d1));
                });
                break;
            default:
                break;
        }
        return groupGoods;
    }
    
    /**
     * 价格区间筛选
     * @param getQData  资料集合
     * @param miniPrice 最低价
     * @param maxPrice  最高价
     * @return List 排序后的资料
     */
    public static List<Map<String,Object>> priceFilter(List<Map<String, Object>> getQData,String miniPrice,String maxPrice){
        List<Map<String,Object>> getd = new ArrayList<>();
        if (Check.Null(miniPrice) && !Check.Null(maxPrice)) {
            // 只有最高价 price <= maxPrice
            BigDecimal maxPriceb = new BigDecimal(maxPrice);
            getQData = getQData.stream().filter(data -> new BigDecimal(data.get("PRICE").toString()).compareTo(maxPriceb) < 1).collect(Collectors.toList());
        } else if (!Check.Null(miniPrice) && Check.Null(maxPrice)) {
            // 只有最低价 price >= miniPrice
            BigDecimal miniPriceb = new BigDecimal(miniPrice);
            getQData = getQData.stream().filter(data -> new BigDecimal(data.get("PRICE").toString()).compareTo(miniPriceb) > -1).collect(Collectors.toList());
        } else if (!Check.Null(miniPrice) && !Check.Null(maxPrice)) {
            // 最低价 && 最高价 price >= miniPrice && price <= maxPrice
            BigDecimal miniPriceb = new BigDecimal(miniPrice);
            BigDecimal maxPriceb = new BigDecimal(maxPrice);
            getQData = getQData.stream().filter(data -> new BigDecimal(data.get("PRICE").toString()).compareTo(miniPriceb) > -1 && new BigDecimal(data.get("PRICE").toString()).compareTo(maxPriceb) < 1).collect(Collectors.toList());
            
        }
        
        return getQData;
    }
    
    private class PluStock{
        String mallGoodsId;
        String totalStock;
        BigDecimal shopStock;
        BigDecimal deliverShopStock;
        
        public String getMallGoodsId() {
            return mallGoodsId;
        }
        public void setMallGoodsId(String mallGoodsId) {
            this.mallGoodsId = mallGoodsId;
        }
        public String getTotalStock() {
            return totalStock;
        }
        public void setTotalStock(String totalStock) {
            this.totalStock = totalStock;
        }
        public BigDecimal getShopStock() {
            return shopStock;
        }
        public void setShopStock(BigDecimal shopStock) {
            this.shopStock = shopStock;
        }
        public BigDecimal getDeliverShopStock() {
            return deliverShopStock;
        }
        public void setDeliverShopStock(BigDecimal deliverShopStock) {
            this.deliverShopStock = deliverShopStock;
        }
    }
    
}