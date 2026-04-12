package com.dsc.spos.service.imp.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import com.dsc.spos.json.cust.req.DCP_TradeChangeUploadReq;
import com.dsc.spos.json.cust.res.DCP_TradeChangeUploadRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.SoapUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 高铁手动交易流水上传
 * @author yuanyy
 *
 */
public class DCP_TradeChangeUpload extends SPosBasicService<DCP_TradeChangeUploadReq, DCP_TradeChangeUploadRes> {

	Logger logger = LogManager.getLogger(this.getClass().getName());

	@Override
	protected boolean isVerifyFail(DCP_TradeChangeUploadReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_TradeChangeUploadReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TradeChangeUploadReq>(){};
	}

	@Override
	protected DCP_TradeChangeUploadRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TradeChangeUploadRes();
	}

	@Override
	protected DCP_TradeChangeUploadRes processJson(DCP_TradeChangeUploadReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_TradeChangeUploadRes res = this.getResponse();

		try {

			String eId = req.getoEId();
			String oShopId = req.getoShopId();
			String beginDate = req.getBeginDate();
			String endDate = req.getEndDate();


			String getJobTimeSql = "SELECT  job_time  FROM job_quartz WHERE job_Name = 'TradeChangeUpload'  AND status = '100'   "; 
			String getShopDatasSql = "   select * from (  "
					+ " SELECT  a.EID ,b.SHOPID ,  nvl(b.item,a.item)  AS item  ,  nvl(b.itemvalue,a.def) as def from platform_basesettemp a "
					+ " left join platform_baseset b on a.EID=b.EID and a.item=b.item and b.status='100'   "
					+ " WHERE  a.status='100' "
					+ " and  ( a.item  in ('TradeKey' , 'TradeScale' , 'TradeShopId',  "
					+ "'TradeStationId' , 'TradeStationName','TradeShopName','TradeUrl' )  "
					+ " OR  b.item  in ('TradeKey' , 'TradeScale' , 'TradeShopId',  "
					+ "'TradeStationId' , 'TradeStationName','TradeShopName','TradeUrl' )     )"
					+ ") "
					+ " ORDER BY SHOPID ";

			String jobTimeStr = "";
			int jobTime = 240; //jobTime 给个默认值240，保证job 一定能跑得动，且不会出现数据不上传的情况
			List<Map<String, Object>> getJobTimeDatas = this.doQueryData(getJobTimeSql, null);

			if (getJobTimeDatas != null && getJobTimeDatas.isEmpty() == false) 
			{
				for (Map<String, Object> map : getJobTimeDatas) {
					jobTimeStr = map.get("JOB_TIME").toString();
					jobTime = Integer.parseInt(jobTimeStr);
					jobTime = jobTime/(1000*60);
				}
			}

			// 查询出需要上传交易数据的门店
			List<Map<String, Object>> getShopDatas = this.doQueryData(getShopDatasSql, null);

			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("SHOPID", true);
			List<Map<String, Object>> getParamsDatas=MapDistinct.getMap(getShopDatas, condition);

			if (getParamsDatas != null && getParamsDatas.isEmpty() == false) 
			{
				for (Map<String, Object> map : getParamsDatas) {

					//得到 商铺id ， key 等参数
					String TradeKey = "";
					String TradeScale = "";
					String TradeShopId = "";
					String TradeStationId = "";
					String TradeStationName = "";
					String TradeShopName = "";
					String TradeUrl = "";
					for (Map<String, Object> oneData : getShopDatas) {

						if(oShopId.equals(oneData.getOrDefault("SHOPID", "如果没有肯定不会相等")) ){
							//得到当前门店的参数信息
							String item = oneData.getOrDefault("ITEM", "").toString();
							String def = oneData.getOrDefault("DEF", "").toString();

							if(item.equals("TradeKey")){
								TradeKey = def;
							}
							if(item.equals("TradeScale")){
								TradeScale = def;
							}
							if(item.equals("TradeShopId")){
								TradeShopId = def;
							}
							if(item.equals("TradeStationId")){
								TradeStationId = def;
							}
							if(item.equals("TradeStationName")){
								TradeStationName = def;
							}
							if(item.equals("TradeShopName")){
								TradeShopName = def;
							}
							if(item.equals("TradeUrl")){
								TradeUrl = def;
							}
						} //得到当前门店的 参数信息结束
					}

					//////**********开始调接口**********************
					String sql = this.getSaleDatas(oShopId, TradeScale, eId, jobTime, beginDate, endDate);
					logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp  SQL=" + sql + "******\r\n");
					String[] conditionValues = {}; // 查詢條件

					List<Map<String, Object>> getQDatas = this.doQueryData(sql, conditionValues);
					boolean more = true;
					int pageIndex = 1;
					if (getQDatas != null && getQDatas.isEmpty() == false) 
					{
						JSONArray tcArr = new JSONArray();
						//从参数表中 ，查出相关 店铺valiKey 和 店铺ID 
						String url  = "http://tempuri.org/";
						String valiKey = TradeKey; //"6EA576539AEB4E878946911DA4E0C6BD"   //测试key
						String method = "Save";

						String totalCountStr = getQDatas.get(0).get("TOTALCOUNT").toString();
						int totalCount = Integer.parseInt(totalCountStr);
						if(totalCount/30 > 0){
							more = true;
						}

						int totalPages = 0;

						totalPages = totalCount / 30;
						totalPages = (totalCount % 30 > 0) ? totalPages + 1 : totalPages;
						int index = 0 ;
						JSONObject tradeChangeJSON = new JSONObject();

						int numIndex = 1;
						for (int i = index; i < getQDatas.size(); i++) {
							Map<String, Object> oneData = getQDatas.get(i);
							String saleNo = oneData.get("SALENO").toString();
							String totAmt = oneData.get("TOT_AMT").toString();
							//订单时间 需要转换成 2019-05-20 12:00:00  形式
							String sDate = oneData.get("SDATE").toString();
							String sTime = oneData.get("STIME").toString();
							String type = oneData.get("TYPE").toString();
							String sourceNo = oneData.get("SOURCENO").toString();

							String billTime = "";
							String yy = sDate.substring(0, 4);
							String mm = sDate.substring(4, 6);
							String dd = sDate.substring(6, 8);
							String hh = sTime.substring(0, 2);
							String mi = sTime.substring(2, 4);
							String ss = sTime.substring(4, 6);

							billTime = yy+"-"+mm+"-"+dd+" "+hh+":"+mi+":"+ss;

							String transType = "销售";
							if(type.equals("0")){
								transType = "销售";
							}
							else if(type.equals("1") || type.equals("2")){
								transType = "退货";
							}

							tradeChangeJSON.put("STATIONNAME",TradeStationName); // 车站名称，统一提供，（固定值，可以做成门店参数）
							tradeChangeJSON.put("STATIONID",TradeStationId); // 车站ID，统一提供， （固定值，可以做成门店参数）
							tradeChangeJSON.put("SHOPNAME", TradeShopName); // 商铺名称， 可查数据库， 也可做成门店参数
							tradeChangeJSON.put("SHOPNO",TradeShopId); //商铺ID， def5b2d2-97fb-404b-a82f-29235c56fc2f
							tradeChangeJSON.put("BILLTYPE"," "); //商品分类 
							tradeChangeJSON.put("BILLNO", saleNo ); // 订单号
							tradeChangeJSON.put("BILLALLPRICES",totAmt);//商品总价 
							tradeChangeJSON.put("BILLTIME",billTime); //销售时间
							tradeChangeJSON.put("PAYMENT","非现金");//支付方式 
							tradeChangeJSON.put("TRANSTYPE",transType); //交易类型
							tradeChangeJSON.put("SOURCETYPE","POS机"); // 来源类型
							tradeChangeJSON.put("SOURCENO", sourceNo); //来源设备号
							tradeChangeJSON.put("BRANCH",eId); // 所属分公司

							tcArr.put(i ,tradeChangeJSON);

							String tcStr = tcArr.toString();
							Map<String, Object> param = new HashMap<String, Object>();
							param.put("tradeChange", tcStr);
							param.put("valiKey", valiKey);

							SoapObject req1 = SoapUtil.setRequestParam(url, method, param);
							String result = SoapUtil.request(req1,TradeUrl);
							// .返回信息是中文 ，并且还是一个长字符串。。。。。。难受
							// 里面可能包含失败的单号，需要记录下来
							// 返回的值不是-1说明test字符串中包含 “失败” ,相反如果包含返回的值必定是-1"

							if( result.length() > 0 && result.indexOf("失败") != -1){
								//若单据已上传， 也会返回失败信息 
								if(result.indexOf("违反唯一约束条件") != -1){
									String orderNo = result.substring(result.indexOf("[") + 1, result.indexOf("]"));
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp 失败 ，【"+orderNo+"】已上传单据"+"\r\n******\r\n");
								}
								else{
									logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp \r\n 报错信息："+result+"******\r\n");
								}
							}
							else{
								logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp 成功 "+"\r\n" + result + "******\r\n");
							}

						} // for循环结束

						//							if(pageIndex < totalPages){
						//								more = true;
						//								pageIndex += 1;
						//							}
						//							else{
						//								more = false;
						//							}

						//						} // while 循环结束

					} // 调用接口结束


				}
			}

		}
		catch (Exception e) 
		{					
			try 
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);	

				pw.flush();
				pw.close();			

				errors.flush();
				errors.close();

				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp报错信息" + e.getMessage() +"\r\n" + errors.toString()+ "******\r\n");

				pw=null;
				errors=null;
			}
			catch (IOException e1) 
			{					
				logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******手动交易流水上传TradeChangeUploadDCPServiceImp报错信息" + e.getMessage() + "******\r\n");
			}

		}


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_TradeChangeUploadReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取销售数据
	 * @return
	 */
	protected String getSaleDatas(String shopId, String TradeScale, String eId, int jobTime, String beginDate, String endDate){
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		if(TradeScale.equals("") || TradeScale == null){
			TradeScale = "0";
		} 

		Calendar now=Calendar.getInstance();
		now.set(Calendar.MINUTE,now.get(Calendar.MINUTE) - jobTime);  

		sqlbuf.append("SELECT COUNT(DISTINCT sd.saleNO ) OVER() TOTALCOUNT , sd.* FROM ( "
				+ " SELECT COUNT(DISTINCT a.saleNO ) OVER() NUM ,  ROWNUM rn  , "
				+ " a.EID ,  a.saleNO , a.SHOPID , a.tot_amt , a.sdate , a.sTime , a.sDate||a.sTime as billTime ,"
				+ " a.type , a.Machine AS  sourceNo "
				+ " FROM DCP_SALE a "
				+ " WHERE EID = '"+eId+"'  AND SHOPID = '"+shopId+"'"
				+ " AND a.sdate ||a.sTime > '"+beginDate+"'  and a.sdate ||a.sTime <= '"+endDate+"' "
				+ " order by dbms_random.value()  "
				+ " )sd  where rn > 0 and rn < num*"+TradeScale+" +1 "
				+ " order by rn , saleNo  ");

		sql = sqlbuf.toString();
		return sql;
	}
}
