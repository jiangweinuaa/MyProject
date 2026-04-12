package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_TakeOutOrderBaseSetQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外卖基础设置查询
 *
 * @author Huawei
 */
public class DCP_TakeOutOrderBaseSetQuery_Open extends SPosBasicService<DCP_TakeOutOrderBaseSetQuery_OpenReq, DCP_TakeOutOrderBaseSetQuery_OpenRes> {

    @Override
    protected boolean isVerifyFail(DCP_TakeOutOrderBaseSetQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        String eId = req.getRequest().getEId();

        if (Check.Null(eId)) {
            errMsg.append("企业编号不可为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;

    }

    @Override
    protected TypeToken<DCP_TakeOutOrderBaseSetQuery_OpenReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_TakeOutOrderBaseSetQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_TakeOutOrderBaseSetQuery_OpenRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_TakeOutOrderBaseSetQuery_OpenRes();
    }

    @Override
    protected DCP_TakeOutOrderBaseSetQuery_OpenRes processJson(DCP_TakeOutOrderBaseSetQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        DCP_TakeOutOrderBaseSetQuery_OpenRes res = null;
        res = this.getResponse();

        DCP_TakeOutOrderBaseSetQuery_OpenReq.level1Elm request = req.getRequest();
        DCP_TakeOutOrderBaseSetQuery_OpenReq.level2Elm freight1 = request.getFreight();
        String eId = request.getEId();
        String shopId = request.getShopId();

        String longitude = ""; // 经度
        String latitude = ""; // 维度
        String address = ""; // 详细配送地址
        if(freight1!=null){
            longitude =  freight1.getLongitude();
            latitude =  freight1.getLatitude();
            address = freight1.getAddress();
        }


        try {

            String sql = "";
            sql = this.getQuerySql(req);
            List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

//			int totalRecords = 0;								//总笔数
//			int totalPages = 0;
            res.setDatas(new ArrayList<DCP_TakeOutOrderBaseSetQuery_OpenRes.level1Elm>());

            if (queryDatas != null && queryDatas.size() > 0) {
//				String num = queryDatas.get(0).get("NUM").toString();
//				totalRecords=Integer.parseInt(num);
//				totalPages = totalRecords / Integer.parseInt(req.getRequest().getPageSize());
//				totalPages = (totalRecords % Integer.parseInt(req.getRequest().getPageSize()) > 0) ? totalPages + 1 : totalPages;

                //单头主键字段
                Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("BASESETNO", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader = MapDistinct.getMap(queryDatas, condition);

                //单头主键字段
                Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
                condition2.put("BASESETNO", true);
                condition2.put("SERIALNO", true);
                //调用过滤函数
                List<Map<String, Object>> freightDatas = MapDistinct.getMap(queryDatas, condition2);

                //单头主键字段
                Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查询条件
                condition3.put("BASESETNO", true);
                condition3.put("SHOPID", true);
                //调用过滤函数
                List<Map<String, Object>> shopDatas = MapDistinct.getMap(queryDatas, condition3);

                //单头主键字段
                Map<String, Boolean> condition4 = new HashMap<String, Boolean>(); //查询条件
                condition4.put("BASESETNO", true);
                condition4.put("ITEM", true);
                //调用过滤函数
                List<Map<String, Object>> orderTimesDatas = MapDistinct.getMap(queryDatas, condition4);

                //单头主键字段
                Map<String, Boolean> condition5 = new HashMap<String, Boolean>(); //查询条件
                condition5.put("BASESETNO", true);
                condition5.put("SORTID", true);
                //调用过滤函数
                List<Map<String, Object>> paySet = MapDistinct.getMap(queryDatas, condition5);


                for (Map<String, Object> map : getQHeader) {
                    DCP_TakeOutOrderBaseSetQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
                    DCP_TakeOutOrderBaseSetQuery_OpenRes.level5Elm lv5 = res.new level5Elm();


                    String baseSetNo = map.get("BASESETNO").toString();
                    if (Check.Null(baseSetNo)) {
                        continue;
                    }
                    String basesetname = map.get("BASESETNAME").toString();
                    String choosableTime = map.get("CHOOSABLETIME").toString();
                    String prepareTime = map.get("PREPARETIME").toString();
                    String deliveryTime = map.get("DELIVERYTIME").toString();
                    String lowestMoney = map.get("LOWESTMONEY").toString();
                    String freightWay = map.get("FREIGHTWAY").toString();
                    String strfreight = map.get("FREIGHT").toString();

                    String isregister = map.get("ISREGISTER").toString();
                    String coupon = map.get("COUPON").toString();
                    String integral = map.get("INTEGRAL").toString();
                    String restrictregister = map.get("RESTRICTREGISTER").toString();
                    String status = map.get("STATUS").toString();
                    String packpricetype = map.get("PACKPRICETYPE").toString();
                    String packprice = map.get("PACKPRICE").toString();
//                    String pluno = map.get("PLUNO").toString();
                    String istableware = map.get("ISTABLEWARE").toString();
                    String restrictshop = map.get("RESTRICTSHOP").toString();
                    String paycountdown = map.get("PAYCOUNTDOWN").toString();
                    String beforOrder = map.get("BEFORORDER").toString();

                    String isAutoRegister = map.get("ISAUTOREGISTER").toString();
                    String isAutoProm = map.get("ISAUTOPROM").toString();
                    String isAutoFold = map.get("ISAUTOFOLD").toString();
                    if(Check.Null(isAutoFold)){
                        isAutoFold = "1";
                    }
                    String ispaycard = Convert.toStr(map.get("ISPAYCARD"),"0");
                    
                    //【ID1035591】扫码点餐及外卖点单加详情配置项-服务 by jinzma 20230828
                    String isGoodsDetailDisplay = map.get("ISGOODSDETAILDISPLAY").toString();
                    if(Check.Null(isGoodsDetailDisplay)){
                        isGoodsDetailDisplay = "0";
                    }

                    String freeShippingPrice = map.getOrDefault("FREESHIPPINGPRICE","0").toString();
                    if(Check.Null(freeShippingPrice)){
                        freeShippingPrice = "0";
                    }

                    lv1.setBaseSetNo(baseSetNo);
                    lv1.setChoosableTime(choosableTime);
                    lv1.setPrepareTime(prepareTime);
                    lv1.setDeliveryTime(deliveryTime);
                    lv1.setLowestMoney(lowestMoney);
                    lv1.setFreightWay(freightWay);
                    lv1.setPackPriceType(packpricetype);
                    lv1.setPackPrice(packprice);
//                    lv1.setPluNo(pluno);
                    lv1.setIsTableware(istableware);
                    lv1.setRestrictShop(restrictshop);
                    lv1.setPayCountdown(paycountdown);
                    lv1.setBeforOrder(beforOrder);
                    lv1.setFreeShippingPrice(freeShippingPrice);

                    lv5.setIsRegister(isregister);
                    lv5.setCoupon(coupon);
                    lv5.setIntegral(integral);
                    lv5.setRestrictRegister(restrictregister);
                    lv5.setIsAutoRegister(isAutoRegister);
                    lv5.setIsAutoProm(isAutoProm);
                    lv5.setIsAutoFold(isAutoFold);
                    lv5.setIsPayCard(ispaycard);
                    //【ID1035591】扫码点餐及外卖点单加详情配置项-服务 by jinzma 20230828
                    lv5.setIsGoodsDetailDisplay(isGoodsDetailDisplay);
                    
                    lv1.setFreight("");

                    lv1.setCityDeliverFreight(new ArrayList<DCP_TakeOutOrderBaseSetQuery_OpenRes.level2Elm>());
                    lv1.setRangeList(new ArrayList<DCP_TakeOutOrderBaseSetQuery_OpenRes.level3Elm>());
                    lv1.setOrderTimes(new ArrayList<DCP_TakeOutOrderBaseSetQuery_OpenRes.level4Elm>());
                    lv1.setPaySet(new ArrayList<DCP_TakeOutOrderBaseSetQuery_OpenRes.level6ELm>());
                    lv1.setPromSet(lv5);

                    for (Map<String, Object> map2 : shopDatas) {
                        if (baseSetNo.equals(map2.get("BASESETNO").toString())) {

                            if (Check.Null(map2.getOrDefault("SHOPID", "").toString())) {
                                continue;
                            }

                            DCP_TakeOutOrderBaseSetQuery_OpenRes.level3Elm lv3 = res.new level3Elm();
                            lv3.setShopId(map2.get("SHOPID").toString());
                            lv3.setName(map2.get("SHOPNAME").toString());
                            lv1.getRangeList().add(lv3);
                        }

                    }

                    for (Map<String, Object> map2 : freightDatas) {
                        if (baseSetNo.equals(map2.get("BASESETNO").toString())) {

                            if (Check.Null(map2.getOrDefault("SERIALNO", "").toString())) {
                                continue;
                            }

                            DCP_TakeOutOrderBaseSetQuery_OpenRes.level2Elm lv2 = res.new level2Elm();
                            lv2.setSerialNo(map2.get("SERIALNO").toString());
                            lv2.setMaxDistance(map2.get("MAXDISTANCE").toString());
                            lv2.setFreight(map2.get("CITYFREIGHT").toString());
                            lv2.setLowestMoney(map2.getOrDefault("LOWESTMONEY_D","").toString());
                            lv2.setFreeShippingPrice(map2.getOrDefault("FREESHIPPINGPRICE_D","").toString());
                            lv2.setDeliveryTime(map2.getOrDefault("DELIVERYTIME_D","").toString());
                            lv1.getCityDeliverFreight().add(lv2);

                        }
                    }

                    for (Map<String, Object> map2 : orderTimesDatas) {
                        if (baseSetNo.equals(map2.get("BASESETNO").toString())) {

                            if (Check.Null(map2.getOrDefault("ITEM", "").toString())) {
                                continue;
                            }

                            DCP_TakeOutOrderBaseSetQuery_OpenRes.level4Elm lv4 = res.new level4Elm();
                            lv4.setItem(map2.get("ITEM").toString());
                            lv4.setStartTime(map2.get("STARTTIME").toString());
                            lv4.setEndTime(map2.get("ENDTIME").toString());
                            lv1.getOrderTimes().add(lv4);

                        }
                    }

                    for (Map<String, Object> map2 : paySet) {
                        if (baseSetNo.equals(map2.get("BASESETNO").toString())) {

                            if (Check.Null(map2.getOrDefault("SORTID", "").toString())) {
                                continue;
                            }

                            DCP_TakeOutOrderBaseSetQuery_OpenRes.level6ELm lv6 = res.new level6ELm();
                            lv6.setSortId(map2.get("SORTID").toString());
                            lv6.setPayType(map2.get("PAYTYPE").toString());
                            lv6.setPayName(map2.get("PAYNAME").toString());
                            lv1.getPaySet().add(lv6);

                        }
                    }
                    // ****************** 若shopId不为空&freightWay=1，配送费返回 *****************
                    if(freight1!=null) {
                        String freight = "0";

                        if (!Check.Null(shopId) && freightWay.equals("1")) {
                            // 经纬度为空的时候，根据配送地址查询对应经纬度
                            if (Check.Null(latitude) || Check.Null(longitude)) {
                                MyCommon mc = new MyCommon();
                                String point = mc.getLngLat(address);

                                if (!Check.Null(point)) {
                                    longitude = point.substring(0, point.indexOf(","));
                                    // 截取逗号后的字符串
                                    latitude = point.substring(longitude.length() + 1, point.length());
                                }

                                mc = null;
                            }

                            // 以经纬度为第一优先级， 没有经纬度就根据收货地址去调接口查
                            String distanceSql = " SELECT a.organizationno  AS shop ,  F_CRM_GetDistance( " + latitude + " , " + longitude + ",a.latitude,a.longitude ) as distance "
                                    + " FROM DCP_ORG a WHERE EID  = '" + eId + "' AND organizationNo = '" + shopId + "' ";
                            List<Map<String, Object>> distanceDatas = this.doQueryData(distanceSql, null);
                            String distance = null;
                            if (distanceDatas != null && !distanceDatas.isEmpty()) {
                                distance = distanceDatas.get(0).get("DISTANCE").toString();
                            }

                            if (Check.Null(distance)) { //如果获取门店经纬度和距离失败，给提示信息
                                res.setSuccess(false);
                                res.setServiceStatus("200");
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取" + shopId + "和配送地址经纬度失败");
                            }

                            String baseSql = "";
                            StringBuffer sqlbuf = new StringBuffer();
                            sqlbuf.append("SELECT a.basesetno , a.freightway , a.freight  AS comFreight  , b.item , b.maxDistance , b.freight AS difFreight "
                                    + " FROM Dcp_Takeout_Baseset a "
                                    + " LEFT JOIN dcp_takeout_baseset_freight b ON a.eid = b.eid AND a.basesetno = b.basesetno "
                                    + " WHERE a.EID = '" + eId + "'  AND  a.baseSetNo = '" + baseSetNo + "' "
                                    + " order by a.freightway , b.item ");
                            baseSql = sqlbuf.toString();
                            List<Map<String, Object>> freightData2 = this.doQueryData(baseSql, null);

                            if (freightData2 != null && !freightData2.isEmpty()) {

                                int num = freightData2.size();
                                Double[] dArr = new Double[num];

                                if (freightWay.equals("0")) { //统一运费
                                    freight = freightData2.get(0).get("FREIGHT").toString();
                                } else {


                                    for (int i = 0; i < freightData2.size(); i++) {

                                        String maxDistanceStr = freightData2.get(i).get("MAXDISTANCE").toString();
                                        dArr[i] = new Double(Double.parseDouble(maxDistanceStr));
                                    }

                                    // 排序
                                    //冒泡
                                    for (int i = 0; i < dArr.length; i++) {
                                        //外层循环，遍历次数
                                        for (int j = 0; j < dArr.length - i - 1; j++) {
                                            //内层循环，升序（如果前一个值比后一个值大，则交换）
                                            //内层循环一次，获取一个最大值
                                            if (dArr[j] > dArr[j + 1]) {
                                                double temp = dArr[j + 1];
                                                dArr[j + 1] = dArr[j];
                                                dArr[j] = temp;
                                            }
                                        }
                                    }

                                    double min = 0; //匹配的配送距离
                                    double distanceDou = Double.parseDouble(distance);
                                    for (int i = 0; i < dArr.length; i++) {
                                        double d = dArr[i];

                                        if (d - distanceDou > 0) {
                                            min = d;
                                            break;
                                        }
                                    }

                                    for (Map<String, Object> map3 : freightData2) {


                                        if (Check.Null(map3.get("MAXDISTANCE").toString())) {
                                            continue;
                                        } else {
                                            double id = Double.parseDouble(map3.get("MAXDISTANCE").toString());

                                            if (id == min) {
//				        		System.out.println("最小配送费："+ map.get("ID").toString() + "    "+ map.get("money").toString());
                                                freight = map3.get("DIFFREIGHT").toString();
                                            }
                                        }

                                    }

                                }

                            }

                        } else if (freightWay.equals("0")) {
                            freight = strfreight;
                        } else if (freightWay.equals("1") && Check.Null(shopId)) {
                            freight = "";
                        }
                        lv1.setFreight(freight);
                    }

                    res.getDatas().add(lv1);
                }
            }

//			res.setPageNumber(Integer.parseInt(req.getRequest().getPageNumber()));
//			res.setPageSize(Integer.parseInt(req.getRequest().getPageSize()));
//			res.setTotalRecords(totalRecords);
//			res.setTotalPages(totalPages);

        } catch (Exception e) {
            // TODO: handle exception
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常！");

        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getQuerySql(DCP_TakeOutOrderBaseSetQuery_OpenReq req) throws Exception {
        // TODO Auto-generated method stub
        StringBuffer sqlbuf = new StringBuffer();
        String eId = req.geteId();
        String EID = req.getRequest().getEId();
        String sql = "";
        //分页处理
//		int pageNumber = Integer.parseInt(req.getRequest().getPageNumber());
//		int pageSize = Integer.parseInt(req.getRequest().getPageSize());
//		int startRow=(pageNumber-1) * pageSize;
        String langType = req.getLangType();

        if (Check.Null(langType)) {
            langType = "zh_CN";
        }

        String keyTxt = req.getRequest().getKeyTxt();
        String shopId = req.getRequest().getShopId();

        sqlbuf.append(" select * from ( "
                + " SELECT count(distinct a.baseSetNo ) OVER() AS NUM,  dense_rank() over (order BY  a.baseSetNO ) rn, "
                + " a.baseSetNo, a.BASESETNAME, a.choosableTime, a.prepareTime, a.deliveryTime , a.lowestMoney, a.freightWay, a.freight,  "
                + " b.ITEM AS serialNo, b.MAXDISTANCE , b.FREIGHT AS cityFreight, a.ISREGISTER, a.COUPON, a.INTEGRAL, a.RESTRICTREGISTER , "
                + " a.STATUS, a.PACKPRICE, a.PACKPRICETYPE,a.payCountdown,a.ISAUTOREGISTER,a.ISAUTOPROM, ot.ITEM , ot.STARTTIME, ot.ENDTIME, a.ISTABLEWARE, e.SORTID,  "
                + " e.PAYTYPE , e.PAYNAME, a.RESTRICTSHOP, c.SHOPID, c.SHOPNAME,a.ISAUTOFOLD,a.BEFORORDER,a.ISPAYCARD,a.ISGOODSDETAILDISPLAY, "
                + " a.FREESHIPPINGPRICE,b.LOWESTMONEY LOWESTMONEY_D,b.FREESHIPPINGPRICE FREESHIPPINGPRICE_D,b.DELIVERYTIME DELIVERYTIME_D "
                + " FROM DCP_TakeOut_BaseSet a "
                + " LEFT JOIN DCP_TakeOut_BaseSet_FREIGHT b ON a.EID = b.EID AND a.baseSetNo = b.baseSetNo "
                + " LEFT JOIN DCP_TakeOut_BaseSet_DETAIL c ON a.EID = c.EID AND a.baseSetNo = c.baseSetNO"
                + " LEFT JOIN DCP_TAKEOUT_BASESET_ORDERTIME ot on a.EID = ot.EID and a.basesetNO = ot.basesetNO  "
                + " LEFT JOIN DCP_ORG_lang d ON c.EID = d.EID AND c.SHOPID = d.organizationno AND d.lang_type = '" + langType + "'  and d.status='100' " +
                " LEFT JOIN DCP_TAKEOUT_BASESET_PAYTYPE e ON a.EID = e.EID AND a.BASESETNO = e.BASESETNO"
                + " WHERE a.EID = '" + EID + "' and a.STATUS = '100'");

        if (!Check.Null(keyTxt)) {
            sqlbuf.append(" and ( a.baseSetNo like '%%" + keyTxt + "%%' ) ");
        }

        if (!Check.Null(shopId)) {
            sqlbuf.append(" and ( (c.SHOPID = '"+shopId+"' and a.RESTRICTSHOP ='1') OR a.RESTRICTSHOP = '0' OR (c.SHOPID != '"+shopId+"'and a.RESTRICTSHOP = '2')) ");
        }

//		sqlbuf.append(" order by a.baseSetNO   "
//				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
//				+ " order BY baseSetNO");
        sqlbuf.append(" order by  a.CREATETIME desc,serialNo,ot.ITEM ,e.SORTID )");
        sql = sqlbuf.toString();
        return sql;
    }

}
