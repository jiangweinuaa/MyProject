package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.config.SPosConfig;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_POrderQuery;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.xml.utils.ParseXml;

//********************门店采购收货通知单结案**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReceiptEcsflg extends InitJob {

	Logger logger = LogManager.getLogger(ReceiptUpdate.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public ReceiptEcsflg(){

	}
	
	public String doExe() 
	{
		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中
		if (bRun)
		{		
			logger.info("\r\n*********门店采购收货通知单结案receipt.ecsflg正在执行中,本次调用取消:************\r\n");
			sReturnInfo="门店采购收货通知单结案receipt.ecsflg正在执行中！";
			return sReturnInfo;
		}

		bRun=true;	
		logger.info("\r\n*********门店采购收货通知单结案receipt.ecsflg定时调用Start:************\r\n");

		try 
		{
			
			String sql = this.getQuerySql();

			logger.info("\r\n******门店采购收货通知单结案receipt.ecsflg 执行SQL语句："+sql+"******\r\n");

			List<Map<String, Object>> getQData = this.doQueryData(sql, null);

			if (getQData != null && getQData.isEmpty() == false) 
			{
				for (Map<String, Object> oneData : getQData) 
				{
					String eId = oneData.get("EID") == null ? ""	: oneData.get("EID").toString();
					String shopId = oneData.get("SHOPID") == null ? "" : oneData.get("SHOPID").toString();
					String loadDocNO = oneData.get("LOAD_DOCNO") == null ? "" : oneData.get("LOAD_DOCNO").toString();
					String receiptNO = oneData.get("LOAD_RECEIPTNO") == null ? "" : oneData.get("LOAD_RECEIPTNO").toString();
					String receivingNO = oneData.get("RECEIVINGNO") == null ? "" : oneData.get("RECEIVINGNO").toString();
					
					//payload对象
					JSONObject payload = new JSONObject();
					JSONObject std_data = new JSONObject();
					JSONObject parameter = new JSONObject();

					JSONArray request = new JSONArray();
					JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）

					// 给单头赋值
					header.put("shop_no", shopId);
					header.put("load_doc_no", loadDocNO);
					header.put("receipt_no", receiptNO);
					header.put("version", "3.0");
					
					request.put(header);
					parameter.put("request", request);
					std_data.put("parameter", parameter);
					payload.put("std_data", std_data);

					String str = payload.toString();// 将json对象转换为字符串

					logger.info("\r\n******门店采购收货通知单结案receipt.ecsflg请求T100传入参数：  " + str + "\r\n");
					String resbody="";
					//执行请求操作，并拿到结果（同步阻塞）
					try 
					{
						resbody=HttpSend.Send(str, "receipt.ecsflg", eId, shopId,shopId,loadDocNO);
						logger.info("\r\n******门店采购收货通知单结案receipt.ecsflg请求T100返回参数：  "+ "\r\nERP来源单号="+ loadDocNO + "\r\n" + resbody + "******\r\n");
						if(Check.Null(resbody) || resbody.isEmpty() )
						{
							continue;
						}
						JSONObject jsonres = new JSONObject(resbody);
						JSONObject std_data_res = jsonres.getJSONObject("std_data");
						JSONObject execution_res = std_data_res.getJSONObject("execution");

//						// parameter 节点，记录ERP对应的单号和 ERP 对应的组织
//						String docNo = "";
//						String orgNo = "";
//						if(std_data_res.has("parameter")){
//							JSONObject parameter_res = std_data_res.getJSONObject("parameter");
//							if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
//								docNo = parameter_res.getString("doc_no");
//								orgNo = parameter_res.getString("org_no");
//							}
//						}

						String code = execution_res.getString("code");
						String description ="";
						if  (!execution_res.isNull("description") )
						{
							description = execution_res.getString("description");
						}
						if (code.equals("0")) 
						{
							// values
							Map<String, DataValue> values = new HashMap<String, DataValue>();
							values.put("PROCESS_STATUS", new DataValue("Y", Types.VARCHAR));
							DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
							values.put("UPDATE_TIME", v1);
							values.put("TRAN_TIME", v1);
							// condition
							Map<String, DataValue> conditions = new HashMap<String, DataValue>();
							conditions.put("EID", new DataValue(eId, Types.VARCHAR));
							conditions.put("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
							conditions.put("SHOPID", new DataValue(shopId, Types.VARCHAR));
							conditions.put("RECEIVINGNO", new DataValue(receivingNO, Types.VARCHAR));

							this.doUpdate("DCP_RECEIVING", values, conditions);
							InsertWSLOG.delete_WSLOG(eId, shopId,"1", receivingNO);
							sReturnInfo="0";
						} 
						else 
						{			
							sReturnInfo="ERP返回错误信息:" + code + "," + description;
							InsertWSLOG.insert_WSLOG("receipt.ecsflg",receivingNO,eId,shopId,"1",str,resbody,code,description);
						}
					}
					catch (Exception e) 
					{
						InsertWSLOG.insert_WSLOG("receipt.ecsflg",receivingNO,eId,shopId,"1",str, resbody,"-1",e.getMessage());
						sReturnInfo="错误信息:" + e.getMessage();
						logger.error("\r\n******门店采购收货通知单结案receipt.ecsflg：企业编码=" +eId +",门店=" +shopId+",通知单号="+receivingNO + "\r\n报错信息："+e.getMessage()+"******\r\n");
					}

				}
			}
			else 
			{
				//
				sReturnInfo="错误信息:无单头数据！";

				logger.info("\r\n******门店采购收货通知单结案receipt.ecsflg没有要上传的单头数据******\r\n");
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

				logger.error("\r\n******门店采购收货通知单结案receipt.ecsflg报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******门店采购收货通知单结案receipt.ecsflg报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********门店采购收货通知单结案receipt.ecsflg定时调用End:************\r\n");
		}			

		return sReturnInfo;

	}
	
	
  //DCP_RECEIVING
	protected String getQuerySql() throws Exception 
	{
		String sql = " select * from DCP_RECEIVING "
				+ " where status='7' and process_status='N' and Doc_Type='2' " ;
		return sql;
	}
	
	
	
	
	
}
