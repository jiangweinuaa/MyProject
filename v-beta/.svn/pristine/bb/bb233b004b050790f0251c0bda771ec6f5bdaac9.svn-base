package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsItem_OpenReq;
import com.dsc.spos.json.cust.req.DCP_GetMallGoodsItem_OpenReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsItem_OpenRes;
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
 * 服务函数：DCP_GetMallGoodsItem_Open
 * 服务说明：获取线上商品子商品
 * @author jinzma
 * @since  2020-10-12
 */
public class DCP_GetMallGoodsItem_Open extends SPosBasicService<DCP_GetMallGoodsItem_OpenReq,DCP_GetMallGoodsItem_OpenRes>{
    Logger logger = LogManager.getLogger(DCP_GetMallGoodsItem_Open.class);
    @Override
    protected boolean isVerifyFail(DCP_GetMallGoodsItem_OpenReq req) throws Exception {
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
        } else {
            if (Check.Null(request.getMallGoodsId())) {
                errMsg.append("mallGoodsId线上商品代号不能为空值 ");
                isFail=true;
            }
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_GetMallGoodsItem_OpenReq> getRequestType() {
        return new TypeToken<DCP_GetMallGoodsItem_OpenReq>(){};
    }

    @Override
    protected DCP_GetMallGoodsItem_OpenRes getResponseType() {
        return new DCP_GetMallGoodsItem_OpenRes();
    }

    @Override
    protected DCP_GetMallGoodsItem_OpenRes processJson(DCP_GetMallGoodsItem_OpenReq req) throws Exception {
        DCP_GetMallGoodsItem_OpenRes res = this.getResponse();
        try {
            String apiUserCode = req.getApiUserCode();
            String langType = req.getLangType();
            String eId = "";              //从apiUserCode 查询得到企业编号
            String appType = "";          //从apiUserCode 查询得到应用类型
            String channelId = "";        //从apiUserCode 查询得到渠道编码
            String shopId = "";           //门店编号
            String memberId = "";         //会员号
            String cardNo = "";           //会员卡号
            String promUrl = "";           //参数PromUrl
            //String periodNo = "";         //当前时段
            String promCategory="";       //活动类型
            String promNo="";             //活动代号
            String mallGoodsId="";        //线上商品代号
            String cardTypeId = "";      //卡类型id
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

            DCP_GetMallGoodsItem_OpenRes.level1Elm datas = res.new level1Elm();

            //【ID1021321】【深圳乐沙儿3.0】商城商品传特征码--接口  特征码商品判断  by jinzma 20211025
            boolean isFeature=false;
            sql = "select plutype from dcp_goods  where eid='"+eId+"' and pluno='"+mallGoodsId+"' ";
            List<Map<String, Object>> getQPluType = this.doQueryData(sql, null);
            if (getQPluType !=null && !getQPluType.isEmpty()){
                String pluType = getQPluType.get(0).get("PLUTYPE").toString();
                if (pluType.equals("FEATURE")){
                    isFeature=true;
                }
            }

            //线上商品多属性获取(attrs)
            if (isFeature){
                sql = getFeatureAttrs(req,eId,channelId,shopId,appType,mallGoodsId);
            }else {
                sql = getAttrs(req,eId,channelId,shopId,appType,mallGoodsId);
            }

            List<Map<String, Object>> getQData = this.doQueryData(sql, null);
            datas.setAttrs(new ArrayList<>());
            if (getQData!=null && !getQData.isEmpty()){

                List<Map<String, Object>> getQAttr = new ArrayList<>();
                if (isFeature) {
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
                    condition.put("ATTRID", true);
                    getQAttr = MapDistinct.getMap(getQData, condition);
                }else {
                    //解决属性1，属性2，属性3 返回顺序不一致的问题，参考了DCP_MultiSpecGoodsDetail服务，保持一致吧  蓝塔返回商品最低价的时候发现的一个BUG by jinzma 20240531
                    sql = " select a.attrid,b.sortid,c.attrname from DCP_MSPECGOODS_ATTR a"
                            + " inner join DCP_ATTRIBUTION b on a.eid=b.eid and a.attrid=b.attrid"
                            + " left  join DCP_ATTRIBUTION_LANG c on a.eid=c.eid and a.attrid=c.attrid and c.lang_type='" + req.getLangType() + "'"
                            //【ID1035523】 多规格商品图，原有逻辑是给属性1配图，但把属性2也展示出来了。期望：把属性2的属性值去掉不展示。 by jinzma 20230824
                            + " left  join (select distinct attrid1,attrid2,attrid3 from dcp_mspecgoods_subgoods "
                            + "    where eid='" + eId + "' and masterpluno='" + mallGoodsId + "')d on a.attrid=d.attrid1 "
                            + " where a.eid='" + eId + "' and a.masterpluno='" + mallGoodsId + "'"
                            + " order by d.attrid1,b.sortid";
                    getQAttr = this.doQueryData(sql, null);
                }
                int attrItem =1;
                for (Map<String, Object> oneAttr:getQAttr) {
                    DCP_GetMallGoodsItem_OpenRes.level2ElmAttrs lv2 = res.new level2ElmAttrs();
                    String attrId=oneAttr.get("ATTRID").toString();
                    String attrName=oneAttr.get("ATTRNAME").toString();

                    lv2.setValues(new ArrayList<DCP_GetMallGoodsItem_OpenRes.level3ElmValues>());
                    int valueItem=1;
                    for (Map<String, Object> oneData:getQData) {
                        if (attrId.equals(oneData.get("ATTRID").toString())) {
                            DCP_GetMallGoodsItem_OpenRes.level3ElmValues lv3 = res.new level3ElmValues();
                            String attrValueId = oneData.get("ATTRVALUEID").toString();
                            String attrValueName = oneData.get("ATTRVALUENAME").toString();
                            String picUrl = oneData.get("PICURL").toString();
                            if (!Check.Null(picUrl))
                                picUrl = imagePath+picUrl;
                            lv3.setSerialNo(String.valueOf(valueItem));
                            lv3.setAttrValueId(attrValueId);
                            lv3.setAttrValueName(attrValueName);
                            lv3.setPicUrl(picUrl);

                            lv2.getValues().add(lv3);
                            valueItem++;
                        }
                    }
                    lv2.setSerialNo(String.valueOf(attrItem));
                    lv2.setAttrId(attrId);
                    lv2.setAttrName(attrName);

                    datas.getAttrs().add(lv2);
                    attrItem++;
                }
            }

            //线上商品子商品获取(goods)
            if (isFeature){
                sql =getFeatureQuerySql(req,eId,appType,channelId,shopId,mallGoodsId);
            }else {
                sql =getQuerySql(req,eId,appType,channelId,shopId,mallGoodsId);
            }
            if (getQData != null) {
                getQData.clear();
            }
            getQData = this.doQueryData(sql, null);
            datas.setGoods(new ArrayList<DCP_GetMallGoodsItem_OpenRes.level2ElmGoods>());
            if (getQData!=null && !getQData.isEmpty()){
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
                for (Map<String, Object> oneData:getQData) {
                    DCP_GetMallGoodsItem_OpenRes.level2ElmGoods lv2 = res.new level2ElmGoods();
                    String getQpluNo =oneData.get("PLUNO").toString();
                    String getQpluType = oneData.get("PLUTYPE").toString();
                    String sUnit = "";
                    String originalPrice = "";
                    String price = "";
                    for (Map<String, Object> oneBasicProm:getBasicProm) {
                        String pluNo = oneBasicProm.get("PLUNO").toString();
                        String pluType = oneBasicProm.get("PLUTYPE").toString();
                        if (pluNo.equals(getQpluNo) && pluType.equals(getQpluType)) {
                            sUnit = oneBasicProm.get("UNITID").toString();
                            originalPrice = oneBasicProm.get("ORIGINALPRICE").toString();
                            price = oneBasicProm.get("PRICE").toString();
                            break;
                        }
                    }
                    if (!PosPub.isNumericType(originalPrice)) {
                        originalPrice = "-1";  ///促销只要取不到的全部维护成-1  BY 玲霞 20201026
                    }
                    if (!PosPub.isNumericType(price)) {
                        price = "-1";
                    }
                    //门店前端未给值，造成库存查询不准
                    if (Check.Null(shopId)) {
                        shopId = "";
                    }


                    //【ID1017245】【货郎】商城商品显示不了，报错 查询可售量时异常：error executing work
                    String totalStock="";
                    //【ID1021321】【深圳乐沙儿3.0】商城商品传特征码--接口   by jinzma 20211025
                    String featureNo = " ";
                    if (isFeature){
                        featureNo = oneData.get("FEATURENO").toString();
                    }
                    try{
                        totalStock = PosPub.queryStockQty(dao, eId, getQpluNo,featureNo, shopId, channelId, "", sUnit);
                        if (!PosPub.isNumericType(totalStock)) {
                            totalStock="0";
                        }
                    }catch (SPosCodeException e){
                        String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+getQpluNo+"','"+featureNo+"','"+shopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                        logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsItem_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                        totalStock="0";
                    }




                    //【ID1033508】货郎商城统一发货需求-服务 by jinzma 20230615
//                    sql=" SELECT DELIVERYSHOPID FROM CRM_EXPRESSSET "
//                            + " where EID='"+eId+"' and APPID='"+req.getApiUser().getUserCode()+"' and DELIVERYTYPE='2' and EXPRESS='1' ";
//                    List<Map<String, Object>> getDeliverShop = this.doQueryData(sql, null);
//                    if (!CollectionUtils.isEmpty(getDeliverShop)){
//                        String deliverShopId = getDeliverShop.get(0).get("DELIVERYSHOPID").toString();
//                    }
                    String deliverShopId = HelpTools.getDeliverShopId(eId, req.getApiUser().getUserCode(), shopId);

                    if (!Check.Null(deliverShopId)){
                        try{
                            String deliverShopTotalStock = PosPub.queryStockQty(dao, eId, getQpluNo, featureNo, deliverShopId, channelId,"", sUnit);
                            if (PosPub.isNumericType(deliverShopTotalStock)) {
                                if (new BigDecimal(totalStock).compareTo(new BigDecimal(deliverShopTotalStock))<0) {
                                    totalStock = deliverShopTotalStock;
                                }
                            }
                        }catch (SPosCodeException e){
                            String totalStockSql = " SELECT F_DCP_GET_SALEQTY ('"+eId+"','"+getQpluNo+"','"+featureNo+"','"+deliverShopId+"','"+channelId+"','','"+sUnit+"') as qty FROM dual";
                            logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"DCP_GetMallGoodsItem_Open在查询商品可售量时发生异常,sql语句: "+totalStockSql);
                        }
                    }

                    ////玲霞要求商品可售卖数量全部取整，小数一律抹去  2020/10/27
                    totalStock = new BigDecimal(totalStock).setScale(0, RoundingMode.DOWN).toPlainString();


                    String mallGoodsName = oneData.get("DISPLAYNAME").toString();
                    String goodsId = oneData.get("PLUNO").toString();
                    String subGoodsId = oneData.get("FEATURENO").toString();
                    String unit = oneData.get("UNIT").toString();
                    String attrId1 = oneData.get("ATTRID1").toString();
                    String attrValue1 = oneData.get("ATTRVALUEID1").toString();
                    String attrValueName1 = oneData.get("ATTRVALUENAME1").toString();
                    String attrId2 = oneData.get("ATTRID2").toString();
                    String attrValue2 = oneData.get("ATTRVALUEID2").toString();
                    String attrValueName2 = oneData.get("ATTRVALUENAME2").toString();
                    //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                    String attrId3 = oneData.get("ATTRID3").toString();
                    String attrValue3 = oneData.get("ATTRVALUEID3").toString();
                    String attrValueName3 = oneData.get("ATTRVALUENAME3").toString();




                    String picUrl = oneData.get("PICURL").toString();
                    String multiSpec = oneData.get("MULTISPEC").toString();
                    if (!Check.Null(picUrl))
                        picUrl = imagePath+picUrl;
                    lv2.setMallGoodsId(mallGoodsId);
                    lv2.setMallGoodsName(mallGoodsName);
                    lv2.setSubGoodsId(subGoodsId);
                    lv2.setGoodsId(goodsId);
                    lv2.setUnit(unit);
                    lv2.setAttrId1(attrId1);
                    lv2.setAttrValue1(attrValue1);
                    lv2.setAttrValue1Name(attrValueName1);
                    lv2.setAttrId2(attrId2);
                    lv2.setAttrValue2(attrValue2);
                    lv2.setAttrValue2Name(attrValueName2);
                    //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                    lv2.setAttrId3(attrId3);
                    lv2.setAttrValue3(attrValue3);
                    lv2.setAttrValue3Name(attrValueName3);
                    lv2.setOriPrice(originalPrice);
                    lv2.setPrice(price);
                    lv2.setStock(totalStock);
                    lv2.setPicUrl(picUrl);

                    lv2.setActivity(new ArrayList<Map<String,Object>>());
                    //promCategory=SHOP_SECKILL 秒杀 或 SHOP_GROUPBUY 拼团 时，activity返回空,否则调用PROM_GoodsPromQuery_Open返回
                    if (Check.Null(promCategory) || !(promCategory.equals("SHOP_SECKILL")||promCategory.equals("SHOP_GROUPBUY"))) {
                        List<Map<String, Object>> getActivity = new ArrayList<Map<String, Object>>();
                        //调促销的时候，多规格商品直接给子商品的PLUNO by jinzma 20210326
                        if (multiSpec.equals("Y")) {
                            getActivity = comm.getActivity(apiUserCode, req.getApiUser().getUserKey(), langType, requestId, req.getApiUser().getCompanyId(), shopId, memberId, promUrl, goodsId, getQpluType, sUnit, promCategory, promNo,cardTypeId);
                        }else{
                            getActivity = comm.getActivity(apiUserCode, req.getApiUser().getUserKey(), langType, requestId, req.getApiUser().getCompanyId(), shopId, memberId, promUrl, mallGoodsId, getQpluType, sUnit, promCategory, promNo,cardTypeId);
                        }

                        if (getActivity!=null){
                            for (Map<String, Object> par : getActivity) {
                                try {
                                    if (par.getOrDefault("payTypeList","").toString().equals("[]")) {
                                        par.replace("payTypeList", new ArrayList<>());
                                    } else {
                                        ///干掉"myArrayList" and "map"
                                        JSONArray payType = JSONArray.parseArray(par.getOrDefault("payTypeList","").toString());
                                        //List<Map<String,Object>> payTypeList = (List)payType;
                                        List<Object> payTypeList = payType;
                                        par.replace("payTypeList", payTypeList);
                                    }

                                    if (par.getOrDefault("couponTypeList","").toString().equals("[]")) {
                                        par.replace("couponTypeList", new ArrayList<>());
                                    } else {
                                        ///干掉"myArrayList" and "map"
                                        JSONArray couponType = JSONArray.parseArray(par.getOrDefault("couponTypeList","").toString());
                                        //List<Map<String,Object>> couponTypeList = (List)couponType;
                                        List<Object> couponTypeList = couponType;
                                        par.replace("couponTypeList", couponTypeList);
                                    }

                                    if (par.getOrDefault("giftList","").toString().equals("[]")) {
                                        par.replace("giftList", new ArrayList<>());
                                    } else {
                                        ///干掉"myArrayList" and "map"
                                        JSONArray gift = JSONArray.parseArray(par.getOrDefault("giftList","").toString());
                                        //List<Map<String,Object>> giftList = (List)gift;
                                        List<Object> giftList = gift;
                                        par.replace("giftList", giftList);
                                    }

                                } catch (Exception e) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品可参与促销列表(PROM_GoodsPromQuery_Open) JSON解析失败 "+e.getMessage());
                                }

                                lv2.getActivity().add(par);
                            }
                        }
                        //else{} throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品可参与促销列表(PROM_GoodsPromQuery_Open)调用失败 ");
                    }
                    datas.getGoods().add(lv2);
                }
            }

            res.setDatas(datas);
            return res;

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_GetMallGoodsItem_OpenReq req) throws Exception {
        return null;
    }

    private String getAttrs(DCP_GetMallGoodsItem_OpenReq req,String eId,String channelId,String shopId,String appType,String mallGoodsId){
        String langType=req.getLangType();
        StringBuffer sb = new StringBuffer();
        sb.append(" with specimage as ("
                + " select pluno,attrid1,attrvalueid1,picUrl from ("
                + " select a.*,row_number() over (partition by pluno,attrvalueid1,attrid1 order by indx ) as rn from ("
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,1 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' and a.pluno='"+mallGoodsId+"'"
                + " union all"
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,2 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' and a.pluno='"+mallGoodsId+"' )a"
                + " ) where rn='1'"
                + " )"
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


        sb.append(" ,plu as ("
                + " select a.pluno"
                + " from dcp_goods_shelf_range a"
                + " left join (select a.pluno from dcp_goods_shelf_range a"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
                + " group by a.pluno)"
                + " ");

        sb.append(" select a.masterpluno,b.attrid,attrlang.attrname,c.attrvalueid,valuelang.attrvaluename,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl from dcp_mspecgoods a"
                + " inner join dcp_mspecgoods_attr b on a.eid=b.eid and a.masterpluno=b.masterpluno"
                + " inner join dcp_mspecgoods_attr_value c on a.eid=c.eid and a.masterpluno=c.masterpluno and b.attrid=c.attrid"
                + " inner join plu on a.masterpluno=plu.pluno"
                + " left  join dcp_attribution_lang attrlang on a.eid=attrlang.eid and b.attrid=attrlang.attrid and attrlang.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang valuelang on a.eid=valuelang.eid and c.attrid=valuelang.attrid and c.attrvalueid=valuelang.attrvalueid and valuelang.lang_type='"+langType+"'"
                + " left  join specimage on a.masterpluno=specimage.pluno and b.attrid=specimage.attrid1 and c.attrvalueid=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=a.masterpluno"
                //【ID1036985】【罗森3.0】商城规格排序没有按顺序排  by jinzma 20231031
                + " left  join dcp_attribution_value e on a.eid=e.eid and b.attrid=e.attrid and c.attrvalueid=e.attrvalueid"
                + " where a.eid='"+eId+"' and a.masterpluno='"+mallGoodsId+"' and a.status='100' order by b.sortid,e.sortid,c.attrvalueid"
                + " ");
        return sb.toString();

    }

    private String getQuerySql(DCP_GetMallGoodsItem_OpenReq req,String eId,String appType,String channelId,String shopId,String mallGoodsId){
        String langType=req.getLangType();
        StringBuffer sb = new StringBuffer();
        sb.append(" with specimage as ("
                + " select pluno,attrid1,attrvalueid1,picUrl from ("
                + " select a.*,row_number() over (partition by pluno,attrvalueid1,attrid1 order by indx ) as rn from ("
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,1 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' "
                + " union all"
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,2 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' )a"
                + " ) where rn='1'"
                + " )"
                + " ");

        sb.append(" ,plu as ("
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
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' "  ////and a.pluno='"+mallGoodsId+"'
                + " union all"
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' )a"   ////and a.pluno='"+mallGoodsId+"'
                + " ) where rn='1' )"
                + " ");

        sb.append(" select a.masterpluno,gol.displayname,"
                + " b.featureno,b.pluno,b.unit,"
                + " c.plutype,c.sunit as unitId,"
                + " b.attrid1,b.attrvalueid1,v1.attrvaluename as attrvaluename1,"
                + " b.attrid2,b.attrvalueid2,v2.attrvaluename as attrvaluename2,"
                //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                + " b.attrid3,b.attrvalueid3,v3.attrvaluename as attrvaluename3,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl,"
                + " N'Y' as multispec"
                + " from dcp_mspecgoods a"
                + " inner join dcp_mspecgoods_subgoods b on a.eid=b.eid and a.masterpluno=b.masterpluno"
                + " inner join dcp_goods c on a.eid=c.eid and b.pluno=c.pluno and c.status='100'"
                + " left  join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.masterpluno and gol.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang v1 on a.eid=v1.eid and b.attrid1=v1.attrid and b.attrvalueid1=v1.attrvalueid and v1.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang v2 on a.eid=v2.eid and b.attrid2=v2.attrid and b.attrvalueid2=v2.attrvalueid and v2.lang_type='"+langType+"'"
                //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                + " left  join dcp_attribution_value_lang v3 on a.eid=v3.eid and b.attrid3=v3.attrid and b.attrvalueid3=v3.attrvalueid and v3.lang_type='"+langType+"'"
                + " left  join specimage on b.pluno=specimage.pluno and b.attrid1=specimage.attrid1 and b.attrvalueid1=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=b.pluno"
                + " where a.eid='"+eId+"' and a.masterpluno='"+mallGoodsId+"' and a.status='100'"
                + " ");

        sb.append(" "
                + " union all"
                + " select a.pluno as masterpluno,gol.displayname,N'' as featureno,a.pluno,a.sunit as unit, a.plutype,a.sunit as unitId,"
                + " N'' as attrid1,N'' as attrvalueid1,N'' as attrvaluename1,"
                + " N'' as attrid2,N'' as attrvalueid2,N'' as attrvaluename2,"
                + " N'' as attrid3,N'' as attrvalueid3,N'' as attrvaluename3,"
                + " goodsimage.picUrl,"
                + " N'N' as multispec"
                + " from dcp_goods a"
                + " inner join dcp_goods_online goodsOnline on a.eid=goodsOnline.eid and a.pluno=goodsOnline.pluno "
                + " inner join plu on a.pluno=plu.pluno"
                + " left  join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
                + " left  join goodsimage on goodsimage.pluno=a.pluno"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"' and a.status='100' and goodsOnline.plutype<>'MULTISPEC' "
                + " ");

        return sb.toString();
    }

    private String getFeatureAttrs(DCP_GetMallGoodsItem_OpenReq req,String eId,String channelId,String shopId,String appType,String mallGoodsId){
        String langType=req.getLangType();
        StringBuffer sb = new StringBuffer();
        sb.append(" with specimage as ("
                + " select pluno,attrid1,attrvalueid1,picUrl from ("
                + " select a.*,row_number() over (partition by pluno,attrvalueid1,attrid1 order by indx ) as rn from ("
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,1 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' and a.pluno='"+mallGoodsId+"'"
                + " union all"
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,2 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' and a.pluno='"+mallGoodsId+"' )a"
                + " ) where rn='1'"
                + " )"
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

        sb.append(" ,plu as ("
                + " select a.pluno"
                + " from dcp_goods_shelf_range a"
                + " left join (select a.pluno from dcp_goods_shelf_range a"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and shopid='"+shopId+"' and a.status='0') b on a.pluno = b.pluno"
                + " where a.eid='"+eId+"' and a.channelid='"+channelId+"' and a.shopid='ALL' and a.status='100' and b.pluno is null"
                + " group by a.pluno)"
                + " ");

        sb.append(" select * from ("
                + " select a.pluno as masterpluno,b.attrid1 as attrid,b.attrvalueid1 as attrvalueid,attrlang.attrname,valuelang.attrvaluename,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl from dcp_goods a"
                + " inner join dcp_goods_feature b on a.eid=b.eid and a.pluno=b.pluno"
                + " left  join dcp_attribution_lang attrlang on a.eid=attrlang.eid and b.attrid1=attrlang.attrid and attrlang.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang valuelang on a.eid=valuelang.eid and b.attrid1=valuelang.attrid and b.attrvalueid1=valuelang.attrvalueid and valuelang.lang_type='"+langType+"'"
                + " inner join plu on a.pluno=plu.pluno"
                + " left  join specimage on a.pluno=specimage.pluno and b.attrid1=specimage.attrid1 and b.attrvalueid1=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=a.pluno"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"' and a.status='100' and b.attrid1 is not null"
                + " union all"
                + " select a.pluno as masterpluno,b.attrid2 as attrid,b.attrvalueid2 as attrvalueid,attrlang.attrname,valuelang.attrvaluename,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl from dcp_goods a"
                + " inner join dcp_goods_feature b on a.eid=b.eid and a.pluno=b.pluno"
                + " left  join dcp_attribution_lang attrlang on a.eid=attrlang.eid and b.attrid2=attrlang.attrid and attrlang.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang valuelang on a.eid=valuelang.eid and b.attrid2=valuelang.attrid and b.attrvalueid2=valuelang.attrvalueid and valuelang.lang_type='"+langType+"'"
                + " inner join plu on a.pluno=plu.pluno"
                + " left  join specimage on a.pluno=specimage.pluno and b.attrid2=specimage.attrid1 and b.attrvalueid2=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=a.pluno"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"' and a.status='100' and b.attrid2 is not null"

                //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                + " union all"
                + " select a.pluno as masterpluno,b.attrid3 as attrid,b.attrvalueid3 as attrvalueid,attrlang.attrname,valuelang.attrvaluename,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl from dcp_goods a"
                + " inner join dcp_goods_feature b on a.eid=b.eid and a.pluno=b.pluno"
                + " left  join dcp_attribution_lang attrlang on a.eid=attrlang.eid and b.attrid3=attrlang.attrid and attrlang.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang valuelang on a.eid=valuelang.eid and b.attrid3=valuelang.attrid and b.attrvalueid3=valuelang.attrvalueid and valuelang.lang_type='"+langType+"'"
                + " inner join plu on a.pluno=plu.pluno"
                + " left  join specimage on a.pluno=specimage.pluno and b.attrid3=specimage.attrid1 and b.attrvalueid3=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=a.pluno"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"' and a.status='100' and b.attrid3 is not null"
                + " ) group by masterpluno,attrid,attrvalueid,attrname,attrvaluename,picurl"
                + " ");
        return sb.toString();

    }

    private String getFeatureQuerySql(DCP_GetMallGoodsItem_OpenReq req,String eId,String appType,String channelId,String shopId,String mallGoodsId){
        String langType=req.getLangType();
        StringBuffer sb = new StringBuffer();
        sb.append(" with specimage as ("
                + " select pluno,attrid1,attrvalueid1,picUrl from ("
                + " select a.*,row_number() over (partition by pluno,attrvalueid1,attrid1 order by indx ) as rn from ("
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,1 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' "
                + " union all"
                + " select a.pluno,a.attrvalueid1,a.attrid1,a.specimage as picUrl,2 as indx from dcp_goodsimage_specimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL')a"
                + " ) where rn='1')"
                + " ");

        sb.append(" ,plu as ("
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
                + " where a.eid='"+eId+"' and a.apptype='"+appType+"' "  ////and a.pluno='"+mallGoodsId+"'
                + " union all"
                + " select a.pluno,a.listimage as picUrl,a.symbolDisplay,2 as indx from dcp_goodsimage a"
                + " where a.eid='"+eId+"' and a.apptype='ALL' )a"   ////and a.pluno='"+mallGoodsId+"'
                + " ) where rn='1' )"
                + " ");

        sb.append(" select a.pluno as masterpluno,gol.displayname,b.featureno,a.pluno,a.sunit as unit, a.plutype,a.sunit as unitId,"
                + " b.attrid1,b.attrvalueid1,v1.attrvaluename as attrvaluename1,"
                + " b.attrid2,b.attrvalueid2,v2.attrvaluename as attrvaluename2,"
                //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格  by jinzma 20231208
                + " b.attrid3,b.attrvalueid3,v3.attrvaluename as attrvaluename3,"
                + " nvl(specimage.picUrl,goodsimage.picUrl) as picUrl, N'N' as multispec from dcp_goods a"
                + " inner join dcp_goods_feature b on a.eid=b.eid and a.pluno=b.pluno"
                + " left  join dcp_goods_online_lang gol on gol.eid=a.eid and gol.pluno=a.pluno and gol.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang v1 on a.eid=v1.eid and b.attrid1=v1.attrid and b.attrvalueid1=v1.attrvalueid and v1.lang_type='"+langType+"'"
                + " left  join dcp_attribution_value_lang v2 on a.eid=v2.eid and b.attrid2=v2.attrid and b.attrvalueid2=v2.attrvalueid and v2.lang_type='"+langType+"'"
                //【ID1036966】【阿哆诺斯3310】小程序商城商品，需要用到3个规格 by jinzma 20231208
                + " left  join dcp_attribution_value_lang v3 on a.eid=v3.eid and b.attrid3=v3.attrid and b.attrvalueid3=v3.attrvalueid and v3.lang_type='"+langType+"'"
                + " left  join specimage on b.pluno=specimage.pluno and b.attrid1=specimage.attrid1 and b.attrvalueid1=specimage.attrvalueid1"
                + " left  join goodsimage on goodsimage.pluno=b.pluno"
                + " where a.eid='"+eId+"' and a.pluno='"+mallGoodsId+"' and a.status='100'"
                + " ");

        return sb.toString();
    }



}
