package com.dsc.spos.scheduler.job;

import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.waimai.sftc.shop;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.json.JSONArray;
import com.dsc.spos.json.cust.req.DCP_OrderRedisProcessReq;
import com.dsc.spos.json.cust.req.DCP_ShopEDateProcessReq;
import com.dsc.spos.json.cust.req.DCP_ShopEDateProcessReq.level1Elm;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.TokenManagerRetail;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.entity.orderRedisKeyInfo;

/**
 * *****************这个类是负责调用自动日结功能的*****************
 * @author Administrator
 */

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ShopEDate extends InitJob {
	Logger logger = LogManager.getLogger(ShopEDate.class.getName());
	//一天执行一次
	static boolean bFirst=true;
	static String bFirstDate ="";
	
	public ShopEDate(){
	}
	
	public String doExe() {
		
		logger.error("\r\n***************ShopEDate日结START****************\r\n");
		
		///日结时间修改为参数 BY JZMA 20181229  大伙2019元旦快乐啊！
		String edateTimeStart = "020000";   //默认日结开始时间
		String edateTimeEnd = "035959";     //默认日结结束时间
		//系统日期和时间
		String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String sTime = new SimpleDateFormat("HHmmss").format(new Date());
		
		
		try {
			
			String sql = " select eid,organizationno,out_cost_warehouse from dcp_org "
					+ " where org_form='2' and status='100' "
					+ " order by eid,organizationno ";
			List<Map<String, Object>> getShop = StaticInfo.dao.executeQuerySQL(sql, null);
			if (getShop != null && !getShop.isEmpty()) {
				//【ID1022996】【冠生园3.0】门店没有中午闭店功能 by jinzma 20220105
				String para_ShopEDateTimeCustomize = PosPub.getPARA_SMS(StaticInfo.dao, getShop.get(0).get("EID").toString(), "", "ShopEDateTimeCustomize"); //是否启用门店自定义日结
				//logger.error("\r\n***************ShopEDate日结参数 ShopEDateTimeCustomize= " + para_ShopEDateTimeCustomize + " ****************\r\n");
                //非门店自定义日结  by jinzma 20220110
				if (Check.Null(para_ShopEDateTimeCustomize) || !para_ShopEDateTimeCustomize.equals("Y")) {
					edateTimeStart = PosPub.getPARA_SMS(StaticInfo.dao, getShop.get(0).get("EID").toString(), "", "ShopEDateTime"); //日结不区分企业编号，时间也不区分
					//logger.error("\r\n***************ShopEDate日结参数 edateTimeStart= " + edateTimeStart + " ****************\r\n");
					if (Check.Null(edateTimeStart)) {
						edateTimeStart = "020000";   //默认日结开始时间
						edateTimeEnd = "035959";     //默认日结结束时间
					} else {
						Calendar curdate = Calendar.getInstance();
						curdate.setTime(new SimpleDateFormat("HHmmss").parse(edateTimeStart));
						curdate.add(Calendar.HOUR, 2);
						edateTimeEnd = new SimpleDateFormat("HHmmss").format(curdate.getTime());
					}
				}

				//logger.error("\r\n***************ShopEDate日结 bFirstDate= " + bFirstDate + " ****************\r\n");
				//logger.error("\r\n***************ShopEDate日结 sDate= " + sDate + " ****************\r\n");
				//logger.error("\r\n***************ShopEDate日结 sTime= " + sTime + " ****************\r\n");
				//logger.error("\r\n***************ShopEDate日结 edateTimeStart= " + edateTimeStart + " ****************\r\n");
				//logger.error("\r\n***************ShopEDate日结 edateTimeEnd= " + edateTimeEnd + " ****************\r\n");

				if (sTime.compareTo(edateTimeStart) >= 0 && sTime.compareTo(edateTimeEnd) < 0) {
					
					//日结JOB一天只执行一次
					if(bFirstDate.equals(PosPub.GetStringDate(sDate, -1))) {
						bFirst=true;
						//logger.error("\r\n***************ShopEDate日结 bFirst=true   ****************\r\n");
					}
					if(!bFirst) {
						logger.error("\r\n***************ShopEDate日结 bFirst=false   ****************\r\n");
						return "";
					}
					bFirstDate =sDate;
					bFirst=false;
					
					sDate = PosPub.GetStringDate(sDate, -1);
					ParseJson pj = new ParseJson();
					
					logger.error("\r\n***************ShopEDate日结门店总数：" + getShop.size() + "个****************\r\n");
					
					for (Map<String, Object> shop : getShop)
					{
						Thread.sleep(1000);
						String json = "";
						String eId = shop.get("EID").toString();
						String shopId = shop.get("ORGANIZATIONNO").toString();
						try
						{
							List<level1Elm> datas = new ArrayList<>();
							DCP_ShopEDateProcessReq req = new DCP_ShopEDateProcessReq();
							DCP_ShopEDateProcessReq.level1Elm lv = req.new level1Elm();
							DCP_ShopEDateProcessReq.levelElm request = req.new levelElm();
							lv.setoEId(eId);
							lv.setoShopId(shopId);
							lv.seteDate(sDate);
							datas.add(lv);
							lv = null;
							request.seteType("0");        // 0-日结   1-反日结
							request.setDatas(datas);
							
							Map<String, Object> jsonMap = new HashMap<String, Object>();
							jsonMap.put("serviceId", "DCP_ShopEDateProcess");
							//这个token是无意义的
							jsonMap.put("token", "abecbc7b42eb286a0d1f8587a9df97e5");
							jsonMap.put("request", request);
							//json
							json = pj.beanToJson(jsonMap);
							DispatchService ds = DispatchService.getInstance();
							String resXML = ds.callService(json, StaticInfo.dao);
							JSONObject json_res = new JSONObject(resXML);
							JSONArray datas_res = json_res.optJSONArray("datas");
							
							if (datas_res == null) {
								//保存JOB异常日志  BY JZMA 20191016
								InsertWSLOG.insert_JOBLOG(eId, shopId, "ShopEDate", "门店日结", "ShopEDateProcessDCP 服务返回为空");
							} else {
								String result = datas_res.getJSONObject(0).optString("result");
								String description = datas_res.getJSONObject(0).optString("description");
								if (result.equals("N")) {
									//保存JOB异常日志  BY JZMA 20191016
									InsertWSLOG.insert_JOBLOG(eId, shopId, "ShopEDate", "门店日结", description);
								} else {
									//删除JOB异常日志    BY JZMA 20191016
									InsertWSLOG.delete_JOBLOG(eId, shopId, "ShopEDate");
								}
							}
							logger.error("\r\n***********ShopEDate日结已完成门店(企业编号:" + shop.get("EID").toString() + " 门店编号:" + shop.get("ORGANIZATIONNO").toString() + "\r\n");

							//清理订转销的流水号资料,删除30天内的缓存数据
							RedisPosPub redis=new RedisPosPub();
							try
							{
								String key="SALE:"+eId+":"+shopId+":";
								//删除30天内的缓存数据
								for(int i = 2; i <=30 ; i++)
								{
									String keyDate=PosPub.GetStringDate(sDate,0-i);
									redis.DeleteKey(key+keyDate);
								}
							}
							catch (Exception e)
							{
								logger.error("\r\n***********订转销流水号历史资料清理异常-门店(企业编号:" + shop.get("EID").toString() + " 门店编号:" + shop.get("ORGANIZATIONNO").toString() + e.getMessage()+ "\r\n");
							}
							finally
							{
								redis.Close();
							}

						}
						catch (Exception e)
						{
							//保存JOB异常日志  BY JZMA 20191016
							logger.error("\r\n***********ShopEDate日结异常,企业编号:" + shop.get("EID").toString() + " 门店编号:" + shop.get("ORGANIZATIONNO").toString() + "\r\n Json=" + json + "\r\n" + e.getMessage());
							try {
								InsertWSLOG.insert_JOBLOG(eId, shopId, "ShopEDate", "门店日结", e.getMessage());
							} catch (Exception ignored) {
							}
						}
						finally
						{
							clearOrderRedis(eId, shopId);
						}
					}
				}
				
			} else {
				logger.error("\r\n***************ShopEDate日结: 门店基本资料(dcp_org)为空****************\r\n");
			}
			
			//清理失效token  by康忽悠 20220110
			clearToken();
			
		}catch (Exception e){
			logger.error("\r\n***********ShopEDate日结发生异常:" + e.getMessage());
		}
		
		logger.error("\r\n***************ShopEDate日结END****************\r\n");
		return "";
		
	}
	
	
	//清理失效token
	private void clearToken() throws Exception {
		
		//清理失效token
		TokenManagerRetail tmr = new TokenManagerRetail();
		int iLoseCount = tmr.doClearLoseEffectiveness();
		tmr = null;
		PosPub.listmapJson.clear();
		logger.error("\r\n***************本次共清理失效token：" + iLoseCount + "个****************\r\n");
		System.gc();
		
		//删除 platform_token by jinzma 20210715
		try {
			String tokenDate = PosPub.GetStringDate(new SimpleDateFormat("yyyyMMdd").format(new Date()), -20);
			tokenDate = tokenDate+"000000000";
			ArrayList<DataProcessBean> dataProcessBean = new ArrayList<>();
			ExecBean exec = new ExecBean(" delete platform_token where update_time <'"+tokenDate+"' ");
			dataProcessBean.add(new DataProcessBean(exec));
			StaticInfo.dao.useTransactionProcessData(dataProcessBean);
			
		}catch (Exception e){
			logger.error("\r\n***************清理失效token失败:"+e.getMessage()+"****************\r\n");
		}
		
		
	}
	
	//加上删除订单中心redis缓存 + 加上通知消息删除缓存，目前直接删除整个key  by 陶日平
	private void clearOrderRedis(String eId,String shopId) throws Exception{
		//加上删除订单中心redis缓存
		String json="";
		String redis_key="";
		try {
			DCP_OrderRedisProcessReq reqRedisProcess = new DCP_OrderRedisProcessReq();
			reqRedisProcess.setToken("");
			reqRedisProcess.seteId(eId);
			reqRedisProcess.setServiceId("DCP_OrderRedisProcess");
			DCP_OrderRedisProcessReq.levelRequest reqRedis_request = reqRedisProcess.new levelRequest();
			reqRedis_request.seteId(eId);
			reqRedis_request.setShopId(shopId);
			reqRedisProcess.setRequest(reqRedis_request);
			ParseJson pj=new ParseJson();
			json = pj.beanToJson(reqRedisProcess);
			DispatchService ds = DispatchService.getInstance();
			ds.callService(json, StaticInfo.dao);
		} catch (Exception e) {
			logger.error("\r\n***********定时统一日结【删除订单中心缓存OrderRedisProcess】异常:EID=" +eId + " SHOPID=" +shopId + "\r\n Json=" + json + "\r\n"+ e.getMessage());
		}
		
		//加上通知消息删除缓存，目前直接删除整个key，
		try {
			redis_key = orderRedisKeyInfo.redis_OrderNotify+ ":" +  eId + ":" + shopId;
			RedisPosPub redis = new RedisPosPub();
			redis.DeleteKey(redis_key);
		} catch (Exception e) {
			logger.error("\r\n***********定时统一日结【删除订单中心通知消息缓存ORDERNOTIFY】异常:EID=" + eId + " SHOPID=" +shopId + "\r\n redis_key=" + redis_key + "\r\n"+ e.getMessage());
		}
	}
	
}
