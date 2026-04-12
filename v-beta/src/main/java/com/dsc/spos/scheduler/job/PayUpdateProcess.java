package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;

/**
 * 定时调用 CRM 的支付查询接口（接口名 Query），
 * 根据预支付订单（DCP_PreOrder）状态为未完成的支付订单号去查询， 若已支付完成，就更新订单  和 预支付订单 的支付状态 为 “3” （已付清）。
 * 
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PayUpdateProcess extends InitJob
{

	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	public PayUpdateProcess()
	{

	}

	public PayUpdateProcess(String eId,String shopId,String organizationNO, String billNo)
	{				
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(PayUpdateProcess.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	//0-代表成功  其他返回失败信息
	public String doExe() 
	{

		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********预支付订单状态查询PayUpdateProcess正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-预支付订单状态查询PayUpdateProcess正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********预支付订单状态查询PayUpdateProcess定时调用Start:************\r\n");

		try 
		{
			Calendar cal = Calendar.getInstance();//获得当前时间		
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sysDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sysTime = df.format(cal.getTime());
			List<DataProcessBean> data = new ArrayList<DataProcessBean>();
			// key 先写死测试用
			String crmPayUrl = PosPub.getPARA_SMS(StaticInfo.dao, pEId, pShop, "Mobile_Url");
			
			String prePaySql = " select a.orderNo , a.SHOPID, a.prePay_Id, a.payAmt , a.payStatus,  a.createTime , "
					+ " b.pay_type, b.pos_code , b.appid "
					+ " from DCP_preOrder a "
					+ " LEFT JOIN DCP_PreOrder_pay b  ON a.eid = b.eid AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderNo AND a.prepay_id = b.prepay_id "
					+ " where a.eid = '"+pEId+"' "
					+ " and a.payStatus in( '1' ,'2' ) " ;
			
			List<Map<String, Object>> prePayDatas = this.doQueryData(prePaySql, null);
			
			if(prePayDatas != null && !prePayDatas.isEmpty()){
				for (Map<String, Object> map : prePayDatas) {
					String orderNo = map.get("ORDERNO").toString();
					String shopId = map.get("SHOPID").toString();
					String prePay_Id = map.get("PREPAY_ID").toString();
					String pay_type = map.get("PAY_TYPE").toString();
					String pos_code = map.get("POS_CODE").toString();
					String createTime = map.get("CREATETIME").toString();
					String payAmt = map.get("PAYAMT").toString() == null ? "0" : map.get("PAYAMT").toString();
					
					String key =  map.get("APPID").toString() == null ? "0" : map.get("APPID").toString();
					
					// 加个限制，比如说 允许查一个小时内的预订单， 超过一个小时还没更新支付状态的就算作废 
					if(Check.Null(createTime)){
						
					}
					
					///******************* 开始更新 ***********************
					
					JSONObject QueryReq = new JSONObject();	
					QueryReq.put("serviceId", "Query");
					
					JSONObject payReq = new JSONObject();
					payReq.put("pay_type", pay_type);
					payReq.put("shop_code", shopId);
					payReq.put("pos_code", pos_code);
					payReq.put("order_id", prePay_Id);
					payReq.put("trade_no", "");
					payReq.put("operation_id", "");
					payReq.put("ip", "");
					
					QueryReq.put("request", payReq);
					
					String reqStr = payReq.toString();
					String sign = PosPub.encodeMD5(reqStr + key); 
					
					JSONObject signJson = new JSONObject();
					signJson.put("sign", sign);
					signJson.put("key", key);
					
					QueryReq.put("sign", signJson);
					
					//********** 已经准备好CreatePay的json，开始调用 *************
					String payResStr = HttpSend.Sendcom(QueryReq.toString(), crmPayUrl).trim();
					
					JSONObject payResJson = new JSONObject();
					payResJson = JSON.parseObject(payResStr);//String转json
					
					String paySuccess = payResJson.getString("success").toUpperCase(); // TRUE 或 FALSE
					String payServiceStatus = payResJson.getString("serviceStatus").toUpperCase();
					String payServiceDescription = payResJson.getString("serviceDescription").toUpperCase();
					
					String datasStr = payResJson.getString("datas");
					
					JSONObject datasJson = new JSONObject();
					datasJson =JSONObject.parseObject(datasStr);
					
//					String out_trade_no = datasJson.getString("trade_no").toString();
					
					boolean isSuc = false;
					if(!Check.Null(paySuccess) && paySuccess.equals("TRUE")){
						isSuc = true;

						// 调用回写订单的接口 
						// ************* 写付款档开始 *****************
						
						if(Check.Null(prePay_Id)){
							logger.error("\r\n未查到"+prePay_Id+"对应订单号");
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未查到"+prePay_Id+"对应的预支付订单号");
						}else{
							
							
							UptBean ubec = new UptBean("OC_ORDER");
							ubec.addCondition("EID", new DataValue(pEId, Types.VARCHAR));
							ubec.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							ubec.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							
							ubec.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
							ubec.addUpdateValue("PAYAMT", new DataValue(payAmt, Types.VARCHAR));
							
							UptBean ubec2 = new UptBean("DCP_PREORDER");
							ubec2.addCondition("EID", new DataValue(pEId, Types.VARCHAR));
							ubec2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
							ubec2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
							ubec2.addCondition("PREPAY_ID", new DataValue(prePay_Id, Types.VARCHAR));
							
							ubec2.addUpdateValue("PAYSTATUS", new DataValue("3", Types.VARCHAR)); // 3：已付清
							
							data.add(new DataProcessBean(ubec));
							data.add(new DataProcessBean(ubec2));
							ubec = null;
							ubec2 = null;
							
						}
						
					}
					
					///******************* 更新结束 ************************
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
				
				logger.error("\r\n******预支付订单状态查询PayUpdateProcess报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******预支付订单状态查询PayUpdateProcess报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********预支付订单状态查询PayUpdateProcess定时调用End:************\r\n");
		}			

		//
		return sReturnInfo;

	}
	
	protected String getGuQingSql() throws Exception 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String _SysDATE = df.format(cal.getTime());
		
		sqlbuf.append(" SELECT a.eid ,  a.SHOPID , a.guqingNo , a.bdate , a.rDate , b.pluNO , b.pUnit  , b.guqingType, b.pfNo, b.pfOrderType , "
				+ " c.dtNo , c.dtName ,c.begin_Time AS beginTime  , c.end_time AS endTime , c.qty , c.saleQty , c.restqty , c.isClear, "
				+ " c.modify_date , c.modify_time  "
				+ " FROM DCP_guqingorder a "
				+ " LEFT JOIN DCP_guqingorder_detail b ON a.eid = b.eid AND a.SHOPID = b.SHOPID AND a.guqingNo = b.guqingNo "
				+ " LEFT JOIN DCP_guqingorder_dinnertime c ON b.eid = c.eid AND b.SHOPID = c.SHOPID AND b.guqingNo = c.guqingNo AND b.pluNo = c.pluNo "
				+ " where a.rDate = '"+_SysDATE+"'  " //查询需求日是当天的沽清数据
				+ " and c.isClear = 'N' "
				+ " ORDER BY  a.eid ,  a.SHOPID , a.guqingNo  , c.modify_date desc , c.modify_time desc "
				+ " " 
		  );
		
		sql = sqlbuf.toString();
		return sql;
	}

}
