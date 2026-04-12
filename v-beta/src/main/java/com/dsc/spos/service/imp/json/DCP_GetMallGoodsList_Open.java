package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsList_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsList_OpenReq.levelElm;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsList_OpenReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsList_OpenRes;
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
 * 服务函数：DCP_GetMallGoodsList_Open
 * 服务说明：获取线上商品列表
 * @author jinzma
 * @since  2020-10-09
 */
public class DCP_GetMallGoodsList_Open extends SPosBasicService<DCP_GetMallGoodsList_OpenReq,DCP_GetMallGoodsList_OpenRes>{
    Logger logger = LogManager.getLogger(DCP_GetMallGoodsList_Open.class);
    @Override
    protected boolean isVerifyFail(DCP_GetMallGoodsList_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String apiUserCode = req.getApiUserCode();
        String langType = req.getLangType();
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        levelElm request =req.getRequest();
        
        if(apiUserCode==null) {
            errMsg.append("apiUserCode不能为空值 ");
            isFail=true;
        }
        
        if(langType==null) {
            errMsg.append("langType不能为空值 ");
            isFail=true;
        }
        
        if (pageNumber==0 || pageSize==0) {
            errMsg.append("pageNumber或pageSize不可为零 ");
            isFail=true;
        }
        
        if (request==null) {
            errMsg.append("request不能为空值 ");
            isFail=true;
        } else {
            if (request.getQueryCondition()==null) {
                errMsg.append("queryCondition不能为空值 ");
                isFail=true;
            }
            /*else {
                				String queryType = request.getQueryCondition().getQueryType();
                				if (Check.Null(queryType))
                				{
                					errMsg.append("queryType查询类型不能为空值 ");
                					isFail=true;
                				}
            }*/
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }
    
    @Override
    protected TypeToken<DCP_GetMallGoodsList_OpenReq> getRequestType() {
        return new TypeToken<DCP_GetMallGoodsList_OpenReq>(){};
    }
    
    @Override
    protected DCP_GetMallGoodsList_OpenRes getResponseType() {
        return new DCP_GetMallGoodsList_OpenRes();
    }
    
    @Override
    protected DCP_GetMallGoodsList_OpenRes processJson(DCP_GetMallGoodsList_OpenReq req) throws Exception {
        DCP_GetMallGoodsList_OpenRes res = this.getResponse();
        try {
            String apiUserCode = req.getApiUserCode();
            String shopId = "";           //门店编号
            String memberId = "";         //会员号
            String cardNo = "";           //会员卡号
            
            String periodNo = "";         //当前时段
            String requestId = req.getRequestId();
            if (Check.Null(requestId)) {
                requestId = UUID.randomUUID().toString();
            }
            //以下是云洋在基类里面进行赋值  20200915
            String eId=req.geteId();                              //从apiUserCode 查询得到企业编号
            String appType = req.getApiUser().getAppType();       //从apiUserCode 查询得到应用类型
            String channelId = req.getApiUser().getChannelId();   //从apiUserCode 查询得到渠道编码
            
            if (Check.Null(eId)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的eId");
            }
            if (Check.Null(appType)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的appType");
            }
            if (Check.Null(channelId)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "apiUserCode:" + apiUserCode + " 在crm_apiuser表中未查询到对应的channelId");
            }
            levelElm request = req.getRequest();
            if (request.getQueryCondition()!=null) {
                shopId = request.getQueryCondition().getShopId();
                memberId = request.getQueryCondition().getMemberId();
                cardNo = request.getQueryCondition().getCardNo();
            }
            
            ///默认仓库获取
            //String out_cost_warehouse="";
            /*			if (!Check.Null(shopId))
            			{
            				sql=" select out_cost_warehouse from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
            				List<Map<String, Object>> getWarehouse = this.doQueryData(sql, null);
            				if (getWarehouse!=null && getWarehouse.isEmpty()==false)
            				{
            					out_cost_warehouse = getWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
            				}
            				else
            				{
            					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店:"+shopId+ " 默认出货成本仓未设置");
            				}
            			}*/
            
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
            
            ///会员促销地址
            String promUrl = PosPub.getPROM_INNER_URL(eId);        //参数PromUrl
//            promUrl="http://retaildev.digiwin.com.cn/promService_3.0/openapi";//testtesttest
            if (Check.Null(promUrl)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数PromUrl未设置 ");
            }
            ////图片地址参数获取
            String isHttps = PosPub.getPARA_SMS(dao, eId, "", "ISHTTPS");
            String httpStr = isHttps.equals("1") ? "https://" : "http://";
            String domainName = PosPub.getPARA_SMS(this.dao, eId, "", "DomainName");
            String imagePath = httpStr + domainName + "/resource/image/";
            if (domainName.endsWith("/")) {
                imagePath = httpStr + domainName + "resource/image/";
            }
            
            // 查询类型: 1=一般查询 2=商品推荐
            String queryType=request.getQueryCondition().getQueryType();
            if (Check.Null(queryType)) {
                queryType = "1";    // 鼎捷-黄玲霞  14:04:56 	queryType查询类型不能为空值,帮我把这个限制去掉,默认按1查
            }
            int totalRecords = 0;								//总笔数
            int totalPages = 0;									//总页数
            res.setDatas(new ArrayList<>());
            if (queryType.equals("1")) {
                
                //【ID1033183】【标准产品3.0】商品标签及商城副标题展示---商城服务  by jinzma  20230531
                String searchString = request.getQueryCondition().getSearchString();
                if (!Check.Null(searchString)){
                    sql = " select a.pluno from dcp_goods_online a"
                            + " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='zh_CN'"
                            + "   and (gol.displayname like '%"+searchString+"%' or gol.simpledescription like '%"+searchString+"%')"
                            + " left join (select distinct a.id from dcp_tagtype_detail a"
                            + "   inner join dcp_tagtype_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.tagno=b.tagno and b.lang_type='zh_CN'"
                            + "   where a.eid='"+eId+"' and a.taggrouptype='GOODS' and b.tagname like '%"+searchString+"%'"
                            + " )e on a.pluno=e.id"
                            + " where a.eid='"+eId+"' and (a.pluno=gol.pluno or a.pluno=e.id)";
                    List<Map<String, Object>> getSearch = this.doQueryData(sql, null);
                    if (!CollectionUtils.isEmpty(getSearch)){
                        if (req.getRequest().getQueryCondition().getMallGoodsList()==null){
                            req.getRequest().getQueryCondition().setMallGoodsList(new ArrayList<>());
                        }
                        
                        for (Map<String, Object> oneSearch : getSearch){
                            level2Elm mallGoods = req.new level2Elm();
                            mallGoods.setMallGoodsId(oneSearch.get("PLUNO").toString());
                            
                            req.getRequest().getQueryCondition().getMallGoodsList().add(mallGoods);
                        }
                        req.getRequest().getQueryCondition().setSearchString("");
                    }else {
                        res.setPageNumber(req.getPageNumber());
                        res.setPageSize(req.getPageSize());
                        res.setTotalRecords(totalRecords);
                        res.setTotalPages(totalPages);
                        return res;
                    }
                }
                
                
                sql =getQuerySql1(req,eId,appType,channelId,shopId,periodNo,memberId);   //线上商品一般查询
                //logger.error("DCP_GetMallGoodsList_Open sql: "+ sql + "\r\n" );
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                if (getQData!=null && !getQData.isEmpty()) {
                    //算總頁數
                    String num = getQData.get(0).get("NUM").toString();
                    totalRecords=Integer.parseInt(num);
                    totalPages = totalRecords / req.getPageSize();
                    totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                    
                    //调用基础促销价计算      // 20200929和玲霞沟通确认   促销接口未返回价格时，后端返回空给前端，库存数为空时返回0给前端
                    MyCommon comm = new MyCommon();
                    Map<String, Boolean> condition = new HashMap<>(); //查詢條件
                    condition.put("PLUNO", true);
                    condition.put("PLUTYPE", true);
                    List<Map<String, Object>> getQPlu=MapDistinct.getMap(getQData, condition);
                    List<Map<String, Object>> getBasicProm = comm.getBasicProm(apiUserCode, req.getApiUser().getUserKey(), req.getLangType(), requestId, req.getApiUser().getCompanyId(), shopId, memberId, cardNo, promUrl, getQPlu);
                    
                    if (getBasicProm==null || getBasicProm.isEmpty()){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "会员基础促销价(PROM_BasicPromotionCalc_Open)调用失败 ");
                    }
                    
                    ///【ID1029768】【货郎3.0】小程序商品加载超时  by jinzma 20221123  促销处理
                    for (Map<String, Object> oneData:getQData) {
                        String getQpluNo =oneData.get("PLUNO").toString();
                        String getQpluType = oneData.get("PLUTYPE").toString();
                        String unitId = "";
                        String originalPrice = "";
                        String price = "";
                        //String basicPrice = "";
                        String promLable = "";
                        
                        for (Map<String, Object> oneBasicProm:getBasicProm) {
                            String pluNo = oneBasicProm.get("PLUNO").toString();
                            String pluType = oneBasicProm.get("PLUTYPE").toString();
                            if ((pluNo.equals(getQpluNo) && pluType.equals(getQpluType))) {
                                unitId = oneBasicProm.get("UNITID").toString();
                                originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
                                price = oneBasicProm.get("PRICE").toString();
                                //basicPrice = oneBasicProm.get("BASICPRICE").toString();
                                promLable = oneBasicProm.get("PROMLABLE").toString();
                                break;
                            }
                        }
                        
                        if (!PosPub.isNumericType(originalPrice)) {
                            originalPrice = "-1";        ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
                        }
                        if (!PosPub.isNumericType(price)) {
                            price = "-1";
                        }
                        /*if (!PosPub.isNumericType(basicPrice)) {
                            basicPrice = "-1";
                        }*/
                        
                        oneData.put("ORIPRICE",originalPrice);
                        oneData.put("PRICE",price);
                        //oneData.put("BASICPRICE",basicPrice);
                        oneData.put("PROMLABLE",promLable );
                        oneData.put("SUNIT",unitId );
                    }
                    
                    //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
                    String miniPrice = req.getRequest().getQueryCondition().getMiniPrice();
                    String maxPrice = req.getRequest().getQueryCondition().getMaxPrice();
                    if (!Check.Null(miniPrice) || !Check.Null(maxPrice)) {
                        getQData = DCP_GetGoodsGroup_Open.priceFilter(getQData, miniPrice, maxPrice);
                        if (getQData.size()==0){
                            totalRecords = 0;
                            totalPages = 0;
                        }else{
                            num = String.valueOf(getQData.size());
                            totalRecords=Integer.parseInt(num);
                            totalPages = totalRecords / req.getPageSize();
                            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
                        }
                    }
                    
                    for (Map<String, Object> oneData:getQData) {
                        DCP_GetMallGoodsList_OpenRes.level1Elm lv1 = res.new level1Elm();
                        String getQpluNo =oneData.get("PLUNO").toString();
                        String getQpluType = oneData.get("PLUTYPE").toString();
                        String sUnit = oneData.get("UNITID").toString();
                        
                        //【ID1034636】【货郎3.3.0.7】商城商品列表增加配送方式标签--服务  by jinzma 20230720
                        //配送说明  	门店自提|同城配送|全国快递
                        String deliveryDescription ="";
                        String deliveryDescription2 ="";
                        String shopPickup = oneData.get("SHOPPICKUP").toString();   // 是否支持自提0-否1-是
                        String cityDeliver = oneData.get("CITYDELIVER").toString(); // 是否支持同城配送0-否1-是
                        String expressDeliver = oneData.get("EXPRESSDELIVER").toString(); // 是否支持全国快递0-否1-是
                        
                        
                        //门店前端未给值，造成库存查询不准
                        if (Check.Null(shopId)) {
                            shopId = "";
                        }
                        
                        //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                        String totalStock="0";
                        //门店库存
                        String shopStock="0";
                        //配送门店库存
                        String deliveryStock="0";
                        //配送门店
                        String deShopId=shopId;
                        if("1".equals(shopPickup)||"1".equals(cityDeliver)){
                        	
                        	try{
                        		totalStock = PosPub.queryStockQty(dao, eId, getQpluNo, " ", shopId, channelId, "", sUnit);
                        		shopStock=totalStock;
                        		if (!PosPub.isNumericType(totalStock)) {
                        			totalStock="0";
                        		}
                        	}catch (SPosCodeException e){
                        		String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+getQpluNo+"',' ','"+shopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                        		logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsList_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
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
//                        sql=" SELECT DELIVERYSHOPID FROM CRM_EXPRESSSET "
//                                + " where EID='"+eId+"' and APPID='"+req.getApiUser().getUserCode()+"' and DELIVERYTYPE='2' and EXPRESS='1' ";
//                        List<Map<String, Object>> getDeliverShop = this.doQueryData(sql, null);
//                        if (!CollectionUtils.isEmpty(getDeliverShop)){
//                            String deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//                        }
                        String deliverShopId = HelpTools.getDeliverShopId(eId, req.getApiUser().getUserCode(), shopId);
                        
                        if (!Check.Null(deliverShopId)
                        		&&("1".equals(expressDeliver))
                        		){
                        	try{
                        		deShopId=deliverShopId;
                        		String deliverShopTotalStock = PosPub.queryStockQty(dao, eId, getQpluNo, " ", deliverShopId, channelId,"", sUnit);
                        		if (PosPub.isNumericType(deliverShopTotalStock)) {
                        			deliveryStock=deliverShopTotalStock;
                        			if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliverShopTotalStock))<0) {
                        				totalStock = deliverShopTotalStock;
                        			}
                        			
                        			if (new BigDecimal(deliverShopTotalStock).compareTo(BigDecimal.ZERO)<=0){
                        				isDelivery = false;  //统一配送门店库存<=0 不允许统一配送
                        			}
                        			
                        		}else{
                        			isDelivery = false;    //负数或非数值  统一配送门店没有库存，不允许统一配送
                        		}
                        	}catch (SPosCodeException e){
                        		String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+getQpluNo+"',' ','"+deliverShopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                        		logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsList_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                        		isDelivery = false;
                        	}
                        }
                        
                        ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                        totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();
                        deliveryStock = new BigDecimal(deliveryStock).setScale(0, RoundingMode.DOWN).toPlainString();
                        shopStock = new BigDecimal(shopStock).setScale(0, RoundingMode.DOWN).toPlainString();
                        
                        String description = oneData.get("DESCRIPTION").toString();
                        String groupId = oneData.get("CLASSNO").toString();
                        String groupName = oneData.get("CLASSNAME").toString();
                        String mallGoodsName = oneData.get("DISPLAYNAME").toString();
                        String picUrl = oneData.get("PICURL").toString();
                        if (!Check.Null(picUrl)) {
                            picUrl = imagePath + picUrl;
                        }
                        String symbolDisplay = oneData.get("SYMBOLDISPLAY").toString();
                        //GetMallGoodsRecommend中symbolDisplay 取不到设定,给0  BY 玲霞 20201026
                        if (Check.Null(symbolDisplay)) {
                            symbolDisplay = "0";
                        }
                        String symbolIcon = oneData.get("SYMBOLICON").toString();
                        //【ID1022502】【货郎先生3.0.0.7】 商品角标自定义不能用，更改后无显示 by jinzma 20211203
                        if (!Check.Null(symbolIcon)) {
                            symbolIcon = imagePath + symbolIcon;
                        }
                        String symbolText = oneData.get("SYMBOLTEXT").toString();
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
                        
                        ///【ID1029768】【货郎3.0】小程序商品加载超时  by jinzma 20221123
                        lv1.setPromLable(oneData.get("PROMLABLE").toString());
                        lv1.setOriPrice(oneData.get("ORIPRICE").toString());
                        lv1.setPrice(oneData.get("PRICE").toString());
                        lv1.setUnit(oneData.get("SUNIT").toString());             ///促销接口：101基础促销价计算 返回unit, 不是从DB里查询返回
                        //lv1.setBasicPrice(oneData.get("BASICPRICE").toString());
                        lv1.setCreateTime(oneData.get("CREATETIME").toString());
                        lv1.setTotalSales(oneData.get("SALEQTY").toString());
                        
                        lv1.setDescription(description);
                        lv1.setGroupId(groupId);
                        lv1.setGroupName(groupName);
                        lv1.setMallGoodsId(getQpluNo);
                        lv1.setMallGoodsName(mallGoodsName);
                        
                        lv1.setPicUrl(picUrl);
                        lv1.setPluType(getQpluType);
                        lv1.setSymbolDisplay(symbolDisplay);
                        lv1.setSymbolIcon(symbolIcon);
                        lv1.setSymbolText(symbolText);
                        lv1.setSymbolType(symbolType);
                        lv1.setTotalStock(totalStock);
                        lv1.setDeliveryStock(deliveryStock);
                        lv1.setDeliveryShopId(deShopId);
                        lv1.setShopStock(shopStock);
                        
                        
                        //【ID1033310】 增加商品收藏-服务 by jinzma 20230525
                        lv1.setIsCollect("0");   //0否1是
                        lv1.setCollectQty("0");
                        if (!Check.Null(memberId)){
                            String isCollect = request.getQueryCondition().getIsCollect();  //0否 1是
                            if(!Check.Null(isCollect) && isCollect.equals("1") ){
                                sql = " select count(*) as num from crm_goods_collect a"
                                        + " where a.eid='"+eId+"' and a.mallgoodsid='"+getQpluNo+"' ";
                                List<Map<String, Object>> getCollect = this.doQueryData(sql, null);
                                lv1.setIsCollect("1");   //0否1是
                                lv1.setCollectQty(getCollect.get(0).get("NUM").toString());
                            }else {
                                sql = " select a.mallgoodsid from crm_goods_collect a "
                                        + " where a.eid='"+eId+"' and a.memberid='"+memberId+"' and a.mallgoodsid='"+getQpluNo+"' ";
                                List<Map<String, Object>> getCollect = this.doQueryData(sql, null);
                                if (!CollectionUtils.isEmpty(getCollect)){
                                    sql = " select count(*) as num from crm_goods_collect a"
                                            + " where a.eid='"+eId+"' and a.mallgoodsid='"+getQpluNo+"' ";
                                    getCollect = this.doQueryData(sql, null);
                                    lv1.setIsCollect("1");   //0否1是
                                    lv1.setCollectQty(getCollect.get(0).get("NUM").toString());
                                }
                            }
                        }
                        
                        
                        
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
                        
                        lv1.setDeliveryDescription(deliveryDescription);
                        lv1.setDeliveryDescription2(deliveryDescription2);
                        
                        
                        res.getDatas().add(lv1);
                    }
                    
                    //处理排序
                    String orderType = req.getRequest().getQueryCondition().getOrderType();
                    if (!Check.Null(orderType) && (!orderType.equals("1"))){
                        ///商品排序方式：1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
                        switch (orderType) {
                            case "2":  // 2-销量降序    (需要jdk1.8以上)
                                res.getDatas().sort((o1, o2) -> {
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
                                res.getDatas().sort((o1, o2) -> {
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
                                res.getDatas().sort((o1, o2) -> {
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
                                res.getDatas().sort((o1, o2) -> {
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
                    }
                    //处理分页
                    if (!Check.Null(miniPrice) || !Check.Null(maxPrice) || (!Check.Null(orderType) && (orderType.equals("3")||orderType.equals("4"))) ){
                        //分页处理
                        int pageSize=req.getPageSize();
                        int startRow=(req.getPageNumber()-1) * pageSize;
                        List<DCP_GetMallGoodsList_OpenRes.level1Elm> lv1 = new ArrayList<>();
                        if (res.getDatas().size()>startRow){
                            for (DCP_GetMallGoodsList_OpenRes.level1Elm oneLv1:res.getDatas() ) {
                                if (res.getDatas().indexOf(oneLv1)>=startRow && res.getDatas().indexOf(oneLv1)< startRow+pageSize ){
                                    lv1.add(oneLv1);
                                }
                            }
                        }
                        
                        res.setDatas(lv1);
                    }
                    
                    
                    
                    
                }
                
            }
            
            //else {} 鼎捷-黄玲霞 2020/10/29 20:19:06 GetMallGoodsList 查询类型仅支持一般查询 这个不要报错,返回空就好
            
            
            res.setPageNumber(req.getPageNumber());
            res.setPageSize(req.getPageSize());
            res.setTotalRecords(totalRecords);
            res.setTotalPages(totalPages);
            return res;
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
        }
    }
    
    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }
    
    @Override
    protected String getQuerySql(DCP_GetMallGoodsList_OpenReq req) throws Exception {
        return null;
    }
    
    private String getQuerySql1(DCP_GetMallGoodsList_OpenReq req,String eId,String appType,String channelId,String shopId,String periodNo,String memberId) throws Exception {
        
        String langType=req.getLangType();
        levelElm request = req.getRequest();
        StringBuffer sb = new StringBuffer();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String sDate = df.format(cal.getTime());
        String searchString = request.getQueryCondition().getSearchString();
        String goodsGroupId = request.getQueryCondition().getGoodsGroupId();
        String sort = request.getQueryCondition().getSort();  //serialNo| saledate 序号，上架时间
        List<level2Elm> mallGoodsList=request.getQueryCondition().getMallGoodsList();
        
        //【ID1026143】【3.0】乐沙儿线下扫码购 by jinzma 20220524
        String barCode = request.getQueryCondition().getBarCode();
        
        //【ID1033310】增加商品收藏-服务 by jinzma 20230525
        String isCollect = request.getQueryCondition().getIsCollect();
        
        String classType=request.getQueryCondition().getClassType();
        
        //分页处理
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        int startRow=(pageNumber-1) * pageSize;
        
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        String miniPrice = req.getRequest().getQueryCondition().getMiniPrice();
        String maxPrice = req.getRequest().getQueryCondition().getMaxPrice();
        String brand = req.getRequest().getQueryCondition().getBrand();
        String orderType = req.getRequest().getQueryCondition().getOrderType(); ///商品排序方式：1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
        String groupType = req.getRequest().getQueryCondition().getGroupType(); ///0. 商品所属分组  1. 商品所属分组和上级分组
        if (!Check.Null(miniPrice) || !Check.Null(maxPrice)){
            startRow = 0;
            pageSize = 9999;
        }
        if (!Check.Null(orderType) && (orderType.equals("3")||orderType.equals("4"))){
            startRow = 0;
            pageSize = 9999;
        }
        if (Check.Null(groupType)){
            groupType="1";
        }
        ///处理商品集合
        String withMallGoodsList = "";
        if (mallGoodsList !=null && !mallGoodsList.isEmpty()) {
            MyCommon mc = new MyCommon();
            Map<String,String> map = new HashMap<>();
            StringBuffer sJoinMallGoodsList=new StringBuffer();
            StringBuffer sJoinMallGoodsItem=new StringBuffer();
            int pluItem=1;
            for(level2Elm par :mallGoodsList) {
                sJoinMallGoodsList .append( par.getMallGoodsId()+",");
                sJoinMallGoodsItem .append( pluItem+",");
                pluItem++;
            }
            map.put("PLUNO", sJoinMallGoodsList.toString());
            map.put("PLUITEM", sJoinMallGoodsItem.toString());
            
            withMallGoodsList = mc.getFormatSourceMultiColWith(map);
        }
        
        if (!Check.Null(withMallGoodsList)) {
            sb.append(" with mallgoods as (" + withMallGoodsList + ")");
        }
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230216
        if (!Check.Null(withMallGoodsList)) {
            sb.append(" ,plu as (");
        }else{
            sb.append(" with plu as (");
        }
        
        sb.append(" select a.pluno,"
                + " to_char(nvl(max(a.createtime),sysdate),'yyyymmddhh24miss') as createtime"
                + " from dcp_goods_shelf_range a");
        
        //【ID1026143】【3.0】乐沙儿线下扫码购 by jinzma 20220524
        if (!Check.Null(barCode)){
            sb.append(" inner join dcp_goods_barcode c on c.eid=a.eid and a.pluno=c.pluno and c.plubarcode='"+barCode+"' ");
        }
        sb.append(" left join ("
                + " select a.pluno from dcp_goods_shelf_range a"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0'"
                + " ) b on a.pluno = b.pluno");
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230216
        if (!Check.Null(withMallGoodsList)) {
            sb.append(" inner join mallgoods on a.pluno = mallgoods.pluno ");
        }
        
        sb.append(" where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
                + " group by a.pluno)"
                + " ");
        
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        sb.append(" ,masterplu as ("
                + " select a.masterpluno from dcp_mspecgoods_subgoods a"
                + " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno and b.status='100'");
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230216
        //【ID1034500】【货郎3.0】商城查询问题  标签查询时，会把多规格主商品编号存入mallgoods，此处关联条件要新增主商品 by jinzma 20230706
        if (!Check.Null(withMallGoodsList)) {
            sb.append(" inner join mallgoods on a.pluno=mallgoods.pluno or a.masterpluno=mallgoods.pluno ");
        }
        
        sb.append(" where a.eid='"+eId+"'");
        sb.append(" group by a.masterpluno )");
        
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        //sb.append(" ,stock as ( "
        //        + " SELECT "            ///*+ index(a TW_STOCK_DAY_STATIC_IDX03)*/  by jinzma 20211220  /*+ USE_HASH(plu)*/ 20230217
        //        + " a.pluno,SUM(a.SALE_QTY) AS sale_qty FROM DCP_STOCK_DAY_STATIC a ");
        //if (!Check.Null(withMallGoodsList)) {
        //    sb.append(" inner join mallgoods on a.pluno = mallgoods.pluno ");
        //}else{
        //    sb.append(" inner join plu on a.Pluno=plu.pluno ");
        //}
        //sb.append( " "
        //        + " WHERE a.eid = '"+eId+"' AND a.EDATE >= TO_CHAR( add_months(trunc(sysdate),-1), 'yyyymmdd') "
        //        + " and a.EDATE <= TO_CHAR( TRUNC(SYSDATE - 1), 'yyyymmdd') ");
        //if(!Check.Null(shopId)){
        //    sb.append(" and a.ORGANIZATIONNO = '"+shopId+"'");
        //}
        //sb.append(" GROUP BY a.pluno )");
        
        
        sb.append(" ,invalidclass as ("
                + " select a.classno from dcp_class a"
                + " inner join dcp_class_range b on a.eid=b.eid and a.classno=b.classno and a.classtype=b.classtype and b.rangetype='2' and b.id='"+shopId+"'"
                + " where a.eid='"+eId+"' and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"' and a.restrictshop=2" // and a.status='100'
                );
        
        if(classType!=null&&classType.length()>0){
			sb.append(" and a.classtype='"+classType+"'");
		}
//        else{
//			sb.append(" and a.classtype='ONLINE'");
//		}
        
        sb.append(""
        		+ " group by a.classno)"
        		+ " ");
        
        sb.append(" ,class as ("
                + " select max(a.classno) as classno,b.pluno,max(b.sortid) as sortid from dcp_class a"
                + " inner join dcp_class_goods b on a.eid=b.eid and a.classno=b.classno and a.classtype=b.classtype"
                + " left join dcp_class_range b1 on a.eid=b1.eid and a.classno=b1.classno and a.classtype=b1.classtype and b1.rangetype='2' and b1.id='"+shopId+"'"
                + " left join dcp_class_range b2 on a.eid=b2.eid and a.classno=b2.classno and a.classtype=b2.classtype and b2.rangetype='3' and b2.id='"+channelId+"'"
                + " left join dcp_class_range b3 on a.eid=b3.eid and a.classno=b3.classno and a.classtype=b3.classtype and b3.rangetype='5' and b3.id='"+periodNo+"'"
                + " left join invalidclass on a.classno=invalidclass.classno"
                //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
                + " left join dcp_goods goods on goods.eid=a.eid and goods.pluno=b.pluno and goods.status='100'"
                + " left join masterplu on masterplu.masterpluno = b.pluno"
                + " where a.eid='"+eId+"' " // and a.levelid='2' and a.status='100'
                + " and a.begindate<='"+sDate+"' and a.enddate>='"+sDate+"' "
                + " and (a.restrictshop=0 or (a.restrictshop=1 and b1.id is not null))"
                + " and (a.restrictchannel=0 or (a.restrictchannel=1 and b2.id is not null))"
                + " and (a.restrictperiod=0 or (a.restrictperiod=1 and b3.id is not null))"
                //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
                + " and ((b.plutype<>'MULTISPEC' and goods.pluno is not null) or (b.plutype='MULTISPEC' and masterplu.masterpluno is not null)) "
                + " and invalidclass.classno is null" );
        
        if(classType!=null&&classType.length()>0){
			sb.append(" and a.classtype='"+classType+"'");
		}
//        else{
//			sb.append(" and a.classtype='ONLINE'");
//		}
        
        if (!Check.Null(goodsGroupId)) {
            //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
            if (groupType.equals("1")){
                sb.append(" and (a.classno ='"+goodsGroupId+"' or a.upclassno='"+goodsGroupId+"' )");
            }else{
                sb.append(" and a.classno ='"+goodsGroupId+"' ");
            }
        }
        sb.append(" group by b.pluno ) ");
        
        sb.append(" ,goodsimage as ("
                + " select pluno,picUrl,symbolDisplay from ("
                + " select a.*,row_number() over (partition by pluno order by indx) as rn from ("
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,1 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"'"
                + " union all"
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' )a"
                + " ) where rn='1' )"
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
        
        
        
        sb.append(" select * from ("
                + " select count(*) over() num,");
        
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        // 商品排序方式：1.默认排序 2.销量降序 3.价格降序 4.价格升序 5.上架时间降序（未实现），不传默认1
        if (!Check.Null(orderType) && (orderType.equals("1"))){
            sb.append(" row_number() over (order by class.sortid) rn,");
        }else {
            if (Check.Null(sort) && !Check.Null(withMallGoodsList)) {
                sb.append(" row_number() over (order by mallgoods.pluitem) rn,");
            } else if (!Check.Null(sort) && sort.equals("serialNo")) { //序号排列  saledate
                sb.append(" row_number() over (order by a.pluno) rn,");
                //【ID1022842】 //【希悦3.0】商城商品维护显示顺序，小程序上未正确显示 by jinzma 20211224 sort.equals("4")
            } else if (!Check.Null(sort) && sort.equals("4")) {
                sb.append(" row_number() over (order by class.sortid) rn,");
            } else {   //默认排序 (sort.equals("saledate"))
                sb.append(" row_number() over (order by plu.createtime) rn,");
            }
        }
        
        sb.append(" class.classno,cl1.classname,"
                + " a.pluno,a.plutype,"
                + " a.shoppickup,a.citydeliver,a.expressdeliver,"
                + " gol.displayname,gol.simpledescription as description,"
                + " goodsimage.picUrl,goodsimage.symbolDisplay,"
                + " goodsimage_symbol.symbolType,goodsimage_symbol.symbolIcon,goodsimage_symbol.symbolText,"
                + " goods.sunit as unitId,plu.createtime,"
                + " nvl(sale.qty,0) as saleqty"
                + " from dcp_goods_online a");
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230217
        if (!Check.Null(withMallGoodsList)) {
            sb.append(" inner join mallgoods on a.pluno = mallgoods.pluno");
        }
        sb.append(" inner join plu on a.pluno=plu.pluno"
                + " inner join class on a.pluno=class.pluno"
                + " left join goodsimage on goodsimage.pluno=a.pluno"
                + " left join goodsimage_symbol on goodsimage_symbol.pluno=a.pluno"
                + " left join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
                + " left join dcp_class_lang cl1 on cl1.eid=a.eid and cl1.classno=class.classno and cl1.lang_type='"+langType+"'");
               
        if(classType!=null&&classType.length()>0){
			sb.append(" and cl1.classtype='"+classType+"' ");
		}
//        else{
//			sb.append(" and cl1.classtype='ONLINE' ");
//		}
        
        
        sb.append(" "
                ////玲霞说失效的商品直接不返回  BY JZMA 20200104   //玲霞说多规格商品不管dcp_goods by JZMA 20210114
                + " left join dcp_goods goods on goods.eid=a.eid and goods.pluno=a.pluno and goods.status='100'"
                
                //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
                // + " left join stock on stock.pluno = a.pluno  "
                // + " left join dcp_goods_unit gu on a.eid=gu.eid and a.pluno=gu.pluno and gu.ounit=goods.sunit "
                
                
                
                + " ");
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        if(!Check.Null(brand)){
            sb.append( " left join dcp_brand_lang dbl on dbl.eid=goods.eid and dbl.brandno=goods.brand and dbl.lang_type='"+langType+"' ");
        }
        
        //【ID1031255】【货郎3.3.0.1】11：02 ，顾客小程序进入后转圈，加载很慢，然后报错 by jinzma 20230220
        if(!Check.Null(shopId)){
            sb.append(" left join dcp_sale_goods sale on sale.eid=a.eid and sale.shopid='"+shopId+"' and sale.pluno=a.pluno ");
        }else{
            sb.append(" left join dcp_sale_goods sale on sale.eid=a.eid and sale.shopid=' ' and sale.pluno=a.pluno ");
        }
        
        //【ID1033310】增加商品收藏-服务 by jinzma 20230525
        if(!Check.Null(isCollect) && isCollect.equals("1") && !Check.Null(memberId)){
            sb.append(" inner join crm_goods_collect collect on collect.eid=a.eid and collect.memberid='"+memberId+"' and collect.mallgoodsid=a.pluno ");
        }
        
        ///【嘉华3.0】多规格商品不能在全部商品的分组中显示  by JZMA 20210114
        //		1、if 商品类型是MULTISPEC，就不管dcp_goods，反过来必须在dcp_goods里面有效
        //		2、dcp_goods_online 过滤掉status==90的资料
        sb.append(" where a.eid='"+eId+"' " +
                " and a.status<>'90' " +
                " and (a.plutype='MULTISPEC' or (a.plutype<>'MULTISPEC' and goods.sunit is not null)) ");
        
        if (!Check.Null(searchString)) {
            sb.append(" and (gol.displayname like '%%" + searchString + "%%' or gol.simpledescription like '%%" + searchString + "%%')");
        }
        
        //【ID1029768】 【货郎3.0】小程序商品加载超时  by jinzma
        if(!Check.Null(brand)){
            sb.append(" and dbl.brand_name like '%"+brand+"%'  ");
        }
        
        sb.append(" ) ");
        sb.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));
        
        return sb.toString();
    }
    
    
}
