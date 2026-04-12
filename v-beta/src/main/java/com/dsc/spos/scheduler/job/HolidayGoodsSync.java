package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;


import com.dsc.spos.json.cust.req.DCP_HolidayGoodsSyncReq;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HolidayGoodsSync extends InitJob 
{
	static boolean bRun = false;// 标记此服务是否正在执行中
	Logger logger = LogManager.getLogger(ShopEDate.class.getName());
	public String doExe() throws Exception
	{
		
		//返回信息
		String sReturnInfo="";
		try
		{
			logger.info("\r\n***************【HolidayGoodsSync同步节日规划活动商品】START****************\r\n");
			log("【HolidayGoodsSync同步节日规划活动商品】START");
			
			if (bRun)
			{
				logger.info("\r\n*********【HolidayGoodsSync同步节日规划活动商品】正在执行中,本次调用取消:************\r\n");
				log("【HolidayGoodsSync同步节日规划活动商品】正在执行中,本次调用取消");
				sReturnInfo="【HolidayGoodsSync同步节日规划活动商品】正在执行中,本次调用取消";
				return sReturnInfo;
			}

			bRun = true;
			
			String sql = " select * from dcp_holidaygoods where status<>-1 and GOODSSYNC<>'E' order by billdate ";
			log("【HolidayGoodsSync同步节日规划活动商品】查询sql="+sql);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if(getQData!=null&&getQData.isEmpty()==false)
			{
				ParseJson pj=new ParseJson();
				for (Map<String, Object> map : getQData)
				{
					String eId = map.getOrDefault("EID", "").toString();
					String billNo = map.getOrDefault("BILLNO", "").toString();
					try
					{
						log("循环【调用DCP_HolidayGoodsSync同步商品接口】开始,企业编码eid="+eId+",活动编号billNo="+billNo);
						DCP_HolidayGoodsSyncReq req_HolidayGoodsSync= new DCP_HolidayGoodsSyncReq();
						DCP_HolidayGoodsSyncReq.level1Elm request = req_HolidayGoodsSync.new level1Elm();
						request.seteId(eId);
						request.setBillNo(billNo);
						
						Map<String,Object> jsonMap=new HashMap<String,Object>();
						jsonMap.put("serviceId", "DCP_HolidayGoodsSync");
						//这个token是无意义的
						jsonMap.put("token", "");
						jsonMap.put("request", request);
						String jsonReq = pj.beanToJson(jsonMap);
						log("【调用DCP_HolidayGoodsSync同步商品接口】请求req:"+jsonReq);

						DispatchService ds = DispatchService.getInstance();
						String resbody = ds.callService(jsonReq, StaticInfo.dao);
						log("【调用DCP_HolidayGoodsSync同步商品接口】返回res:"+resbody+",企业编码eid="+eId+",活动编号billNo="+billNo);						
						
						JSONObject jsonRes = new JSONObject(resbody);
						boolean success = jsonRes.getBoolean("success");
						if(success)
						{
							log("【调用DCP_HolidayGoodsSync同步商品接口】成功之后，调用POS的同步缓存接口开始");
							
							String posUrl = PosPub.getPOS_INNER_URL(eId);
							String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + eId + "'" +
									" AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
							log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】查询接口账号sql="+apiUserSql);
							
							List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
							String apiUserCode = "";
							String apiUserKey = "";
							if (result != null && result.size() == 2) {
								for (Map<String, Object> mapPara : result) {
									if (mapPara.get("ITEM") != null && mapPara.get("ITEM").toString().equals("ApiUserCode")) {
										apiUserCode = mapPara.get("ITEMVALUE").toString();
									} else {
										apiUserKey = mapPara.get("ITEMVALUE").toString();
									}
								}
							}
							
							String errorMessage = "";
							Boolean paraFlag = true;
							if(posUrl==null||posUrl.isEmpty())
							{
								paraFlag = false;
								errorMessage = "参数PosUrl未设置,";
							}
							if(apiUserCode==null||apiUserCode.isEmpty())
							{
								paraFlag = false;
								errorMessage = errorMessage + "参数apiUserCode未设置,";
							}
							if(apiUserKey==null||apiUserKey.isEmpty())
							{
								paraFlag = false;
								errorMessage = errorMessage + "参数apiUserKey未设置,";
							}
							if(paraFlag)
							{
								String sql_pluNo = "select * from dcp_holidaygoods_detail where eid='"+eId+"' and billno='"+billNo+"'";
								log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】查询需要同步的商品sql="+sql_pluNo);
								List<Map<String, Object>> getQDataPluNo = this.doQueryData(sql_pluNo, null);
								if(getQDataPluNo!=null&&getQDataPluNo.isEmpty()==false)
								{
									List<Map<String, Object>> pluNoList = new ArrayList<Map<String,Object>>();
									for (Map<String, Object> oneData : getQDataPluNo)
									{
										Map<String, Object> pluNoMap = new HashMap<>();
										pluNoMap.put("pluNo", oneData.getOrDefault("PLUNO", "").toString());
										pluNoList.add(pluNoMap);
									}
									
									 Map<String, Object> mapHeader = new HashMap<>();
									 mapHeader.put("serviceId", "POS_GoodsPriceRedisUpdate");
									 mapHeader.put("requestId", PosPub.getGUID(false));
									 mapHeader.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
									 mapHeader.put("version", "V3.0");
									 mapHeader.put("apiUserCode", apiUserCode);
									 
									 Map<String, Object> mapBody = new HashMap<>();
									 mapBody.put("pluList", pluNoList);
									 mapBody.put("memuType", "1");//作业类型：0-商品信息 1-节日活动规划单
									 mapBody.put("billNo", billNo);
									 String requestPos = pj.beanToJson(mapBody);
									 
									 mapHeader.put("sign", PosPub.encodeMD5(requestPos + apiUserKey));
									 
									 log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】POS服务请求url地址:"+posUrl+",接口账号apiUserCode="+apiUserCode+",接口秘钥apiUserKey="+apiUserKey);
									 log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】POS服务请求header:"+mapHeader.toString()+",请求body:"+requestPos);
									 String resPos = HttpSend.doPost(posUrl, requestPos, mapHeader,"");
									 log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】POS服务返回:"+resPos);
									/* if(resPos!=null&&resPos.isEmpty()==false)
									 {
										 JSONObject resPosObj = new JSONObject(resPos);
										 String success_pos = resPosObj.get("success").toString();
										 if(success_pos!=null&&success_pos.toLowerCase().equals("success"))
										 {
											 
										 }
									 }*/
									 
								}
								else
								{
									log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】查询需要同步的商品资料为空，无需调用POS接口,企业编码eid="+eId+",活动编号billNo="+billNo);
								}
							}
							else
							{
								log("【调用 POS_GoodsPriceRedisUpdate商品缓存接口】参数未设置:"+errorMessage);
							}
							
						}
																				 
					} 
					catch (Exception e)
					{
						// TODO: handle exception
						log("【HolidayGoodsSync同步节日规划活动商品】异常:"+e.getMessage()+",企业编码eid="+eId+",活动编号billNo="+billNo);
						continue;
					}
				}
			}
			else 
			{
				logger.info("\r\n*********【HolidayGoodsSync同步节日规划活动商品】查询没有需要同步的活动单数据************\r\n");
				log("【HolidayGoodsSync同步节日规划活动商品】查询没有需要同步的活动单数据");
				sReturnInfo="【HolidayGoodsSync同步节日规划活动商品】查询没有需要同步的活动单数据";
			}
			
			
		} 
		catch (Exception e)
		{
			logger.error("\r\n*********【HolidayGoodsSync同步节日规划活动商品】报错信息:" + e.getMessage() + "******\r\n");
			log("【HolidayGoodsSync同步节日规划活动商品】报错信息:" + e.getMessage());
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;
			logger.info("\r\n************【HolidayGoodsSync同步节日规划活动商品】END************\r\n");
			log("【HolidayGoodsSync同步节日规划活动商品】END");
			
		}
		
		return sReturnInfo;
	}
	
	public void log(String message)
	{
		try
		{
			HelpTools.writelog_fileName(message, "HolidayGoodsSync");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
