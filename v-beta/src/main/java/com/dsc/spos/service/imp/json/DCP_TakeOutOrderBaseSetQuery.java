package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_TakeOutOrderBaseSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外卖基础设置查询
 * @author Huawei
 *
 */
public class DCP_TakeOutOrderBaseSetQuery extends SPosBasicService<DCP_TakeOutOrderBaseSetQueryReq, DCP_TakeOutOrderBaseSetQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_TakeOutOrderBaseSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_TakeOutOrderBaseSetQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TakeOutOrderBaseSetQueryReq>(){};
	}

	@Override
	protected DCP_TakeOutOrderBaseSetQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TakeOutOrderBaseSetQueryRes();
	}

	@Override
	protected DCP_TakeOutOrderBaseSetQueryRes processJson(DCP_TakeOutOrderBaseSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_TakeOutOrderBaseSetQueryRes res = null;
		res = this.getResponse();
		try {
			
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_TakeOutOrderBaseSetQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("BASESETNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
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
					DCP_TakeOutOrderBaseSetQueryRes.level1Elm lv1 = res.new level1Elm();
					DCP_TakeOutOrderBaseSetQueryRes.level3Elm lv3 =res.new level3Elm();

					String baseSetNo = map.get("BASESETNO").toString();
					if(Check.Null(baseSetNo)){
						continue;
					}
					String basesetname = map.get("BASESETNAME").toString();
					String choosableTime = map.get("CHOOSABLETIME").toString();
					String prepareTime = map.get("PREPARETIME").toString();
					String deliveryTime = map.get("DELIVERYTIME").toString();
					String lowestMoney = map.get("LOWESTMONEY").toString();
					String freightWay = map.get("FREIGHTWAY").toString();
					String freight = map.get("FREIGHT").toString();

					String isregister = map.get("ISREGISTER").toString();
					String coupon = map.get("COUPON").toString();
					String integral = map.get("INTEGRAL").toString();
					String restrictregister = map.get("RESTRICTREGISTER").toString();
					String status = map.get("STATUS").toString();
					String packpricetype = map.get("PACKPRICETYPE").toString();
					String packprice = map.get("PACKPRICE").toString();
//					String pluno = map.get("PLUNO").toString();
					String istableware = map.get("ISTABLEWARE").toString();
					String restrictshop = map.get("RESTRICTSHOP").toString();

                    String paycountdown = map.get("PAYCOUNTDOWN").toString();
                    String beforOrder = map.get("BEFORORDER").toString();
                    String isevaluateremind = map.get("ISEVALUATEREMIND").toString();
                    String remindtime = map.get("REMINDTIME").toString();
                    String ispaycard = Convert.toStr(map.get("ISPAYCARD"),"0");


                    // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
                    String isAutoRegister = map.get("ISAUTOREGISTER").toString();
                    String isAutoProm = map.get("ISAUTOPROM").toString();
                    String isAutoFold = map.get("ISAUTOFOLD").toString();
                    if(Check.Null(isAutoFold)){
						isAutoFold = "1";
					}
     
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
					lv1.setBaseSetName(basesetname);
					lv1.setChoosableTime(choosableTime);
					lv1.setPrepareTime(prepareTime);
					lv1.setDeliveryTime(deliveryTime);
					lv1.setLowestMoney(lowestMoney);
					lv1.setFreightWay(freightWay);
					lv1.setFreight(freight);
					lv1.setStatus(status);
					lv1.setPackPriceType(packpricetype);
					lv1.setPackPrice(packprice);
//					lv1.setPluNo(pluno);
					lv1.setIsTableware(istableware);
					lv1.setRestrictShop(restrictshop);
					lv1.setPayCountdown(paycountdown);
					lv1.setBeforOrder(beforOrder);
                    lv1.setFreeShippingPrice(freeShippingPrice);

					lv3.setIsRegister(isregister);
					lv3.setCoupon(coupon);
					lv3.setIntegral(integral);
					lv3.setRestrictRegister(restrictregister);
					lv3.setIsAutoProm(isAutoProm);
					lv3.setIsAutoRegister(isAutoRegister);
					lv3.setIsAutoFold(isAutoFold);
					lv3.setIsEvaluateRemind(isevaluateremind);
					lv3.setRemindTime(remindtime);
                    lv3.setIsPayCard(ispaycard);
					//【ID1035591】扫码点餐及外卖点单加详情配置项-服务 by jinzma 20230828
					lv3.setIsGoodsDetailDisplay(isGoodsDetailDisplay);

					lv1.setCityDeliverFreight(new ArrayList<DCP_TakeOutOrderBaseSetQueryRes.level2Elm>());
					lv1.setRangeList(new ArrayList<DCP_TakeOutOrderBaseSetQueryRes.level6Elm>());
					lv1.setOrderTimes(new ArrayList<DCP_TakeOutOrderBaseSetQueryRes.level4Elm>());
					lv1.setPaySet(new ArrayList<DCP_TakeOutOrderBaseSetQueryRes.level5Elm>());
					
					
					
					lv1.setPromSet(lv3);

					
					for (Map<String, Object> map2 : shopDatas) {
						if(baseSetNo.equals(map2.get("BASESETNO").toString())){
							
							if(Check.Null(map2.getOrDefault("SHOPID","").toString())){
								continue;
							}
							
							DCP_TakeOutOrderBaseSetQueryRes.level6Elm lv6 = res.new level6Elm();
							lv6.setShopId(map2.get("SHOPID").toString());
							lv6.setName(map2.get("SHOPNAME").toString());
							lv1.getRangeList().add(lv6);
						}
						
					}
					
					for (Map<String, Object> map2 : freightDatas) {
						if(baseSetNo.equals(map2.get("BASESETNO").toString())){
							
							if(Check.Null(map2.getOrDefault("SERIALNO","").toString())){
								continue;
							}
							
							DCP_TakeOutOrderBaseSetQueryRes.level2Elm lv2 = res.new level2Elm();
							lv2.setSerialNo( map2.get("SERIALNO").toString());
							lv2.setMaxDistance( map2.get("MAXDISTANCE").toString());
							lv2.setFreight( map2.get("CITYFREIGHT").toString());
							lv2.setLowestMoney(map2.getOrDefault("LOWESTMONEY_D","").toString());
                            lv2.setFreeShippingPrice(map2.getOrDefault("FREESHIPPINGPRICE_D","").toString());
                            lv2.setDeliveryTime(map2.getOrDefault("DELIVERYTIME_D","").toString());
							lv1.getCityDeliverFreight().add(lv2);
							
						}
					}

					for (Map<String, Object> map2 : orderTimesDatas) {
						if(baseSetNo.equals(map2.get("BASESETNO").toString())){

							if(Check.Null(map2.getOrDefault("ITEM","").toString())){
								continue;
							}

							DCP_TakeOutOrderBaseSetQueryRes.level4Elm lv4 = res.new level4Elm();
							lv4.setItem(map2.get("ITEM").toString());
							lv4.setStartTime( map2.get("STARTTIME").toString());
							lv4.setEndTime( map2.get("ENDTIME").toString());
							lv1.getOrderTimes().add(lv4);

						}
					}
					
					for (Map<String, Object> map2 : paySet) {
						if(baseSetNo.equals(map2.get("BASESETNO").toString())){
							
							if(Check.Null(map2.getOrDefault("SORTID","").toString())){
								continue;
							}
							
							DCP_TakeOutOrderBaseSetQueryRes.level5Elm lv5 = res.new level5Elm();
							lv5.setSortId(map2.get("SORTID").toString());
							lv5.setPayType( map2.get("PAYTYPE").toString());
							lv5.setPayName( map2.get("PAYNAME").toString());
							lv1.getPaySet().add(lv5);
							
						}
					}


					
					res.getDatas().add(lv1);
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
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
	protected String getQuerySql(DCP_TakeOutOrderBaseSetQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.geteId();
		String langType = req.getLangType();
		
		String keyTxt = req.getRequest().getKeyTxt();
		String shopId = req.getRequest().getShopId();
		
		sqlbuf.append(" select * from ( "
				+ " SELECT count(distinct a.baseSetNo ) OVER() AS NUM,  dense_rank() over (order BY  a.baseSetNO ) rn, "
				+ " a.baseSetNo, a.BASESETNAME, a.choosableTime, a.prepareTime, a.deliveryTime , a.lowestMoney, a.freightWay, a.freight, "
				+ "  b.ITEM AS serialNo, b.MAXDISTANCE , b.FREIGHT AS cityFreight, a.ISREGISTER, a.COUPON, a.INTEGRAL, a.RESTRICTREGISTER , "
				+ "  a.STATUS, a.PACKPRICE, a.PACKPRICETYPE,a.PAYCOUNTDOWN,a.ISAUTOREGISTER,a.ISAUTOPROM, ot.ITEM , ot.STARTTIME, ot.ENDTIME, a.ISTABLEWARE, e.SORTID,  "
				+ " e.PAYTYPE , e.PAYNAME, a.RESTRICTSHOP, c.SHOPID, c.SHOPNAME,a.ISAUTOFOLD,a.BEFORORDER,a.ISEVALUATEREMIND,a.REMINDTIME,a.ISPAYCARD,a.ISGOODSDETAILDISPLAY,  "
                + " a.FREESHIPPINGPRICE,b.LOWESTMONEY LOWESTMONEY_D,b.FREESHIPPINGPRICE FREESHIPPINGPRICE_D,b.DELIVERYTIME DELIVERYTIME_D "
				+ " FROM DCP_TakeOut_BaseSet a "
				+ " LEFT JOIN DCP_TakeOut_BaseSet_FREIGHT b ON a.EID = b.EID AND a.baseSetNo = b.baseSetNo "
				+ " LEFT JOIN DCP_TakeOut_BaseSet_DETAIL c ON a.EID = c.EID AND a.baseSetNo = c.baseSetNO "
				+ " LEFT JOIN DCP_TAKEOUT_BASESET_ORDERTIME ot on a.EID = ot.EID and a.basesetNO = ot.basesetNO "
				+ " LEFT JOIN DCP_ORG_lang d ON c.EID = d.EID AND c.SHOPID = d.organizationno AND d.lang_type = '"+langType+"'  and d.status='100'  " +
                " LEFT JOIN DCP_TAKEOUT_BASESET_PAYTYPE e ON a.EID = e.EID AND a.BASESETNO = e.BASESETNO "
				+ " WHERE a.EID = '"+eId+"'  " );
		
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.baseSetNo like '%%"+keyTxt+"%%' or a.BASESETNAME like '%%"+keyTxt+"%%') ");
		}
		
		if (!Check.Null(shopId))
		{
			sqlbuf.append(" and ( (c.SHOPID = '"+shopId+"' and a.RESTRICTSHOP ='1') OR a.RESTRICTSHOP = '0' OR (c.SHOPID != '"+shopId+"'and a.RESTRICTSHOP = '2')) ");
		}
		
		sqlbuf.append(" order by a.CREATETIME desc , serialNo,ot.ITEM ,e.SORTID "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " ");
		sql = sqlbuf.toString();
		return sql;
	}

}
