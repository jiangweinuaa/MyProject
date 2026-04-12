package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;


import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class FeeCreate extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	Logger logger = LogManager.getLogger(FeeCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public FeeCreate()
	{

	}

	public FeeCreate(String eId,String shopId,String organizationNO, String billNo)
	{		
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}


	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********费用单fee.create正在执行中,本次调用取消:************\r\n");

			sReturnInfo="定时传输任务-费用单fee.create正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.info("\r\n*********费用单fee.create定时调用Start:************\r\n");

		try
		{
			
			String sql="";
			StringBuffer sqlbuf = new StringBuffer("");
			sqlbuf.append(" select * from DCP_BFEE A where A.STATUS='2' and A.process_status='N' ");

			//******兼容即时服务的,只查询指定的那张单据******
			if (pEId.equals("")==false) 
			{
				sqlbuf.append(" and EID='"+pEId+"' "
						+ " and SHOPID='"+pShop+"' "
						+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
						+ " and PORDERNO='"+pBillNo+"' ");  		

			}
			sqlbuf.append("order by A.eId,A.SHOPID,A.BFEENO  ");


			sql = sqlbuf.toString();

			List<Map<String, Object>> sqllist=this.doQueryData(sql, null);
			if (sqllist != null && sqllist.isEmpty() == false)
			{
				for (Map<String, Object> oneData100 : sqllist)
				{
					//payload对象
					JSONObject payload = new JSONObject();

					JSONObject std_data = new JSONObject();
					JSONObject parameter = new JSONObject();

					JSONArray request = new JSONArray();
					JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
					JSONArray request_detail = new JSONArray(); // 存所有单身

					String BFEENO=oneData100.get("BFEENO").toString();
					String shopId=oneData100.get("SHOPID").toString();
					String eId=oneData100.get("EID").toString();

					String B_CreateDate="";
					String CREATE_DATE=oneData100.get("CREATE_DATE").toString();					
					if (CREATE_DATE != null && CREATE_DATE.length() > 0) 
					{
						B_CreateDate = DCP_ConversionTimeFormat.converToDate(CREATE_DATE);
					}

					String CREATE_DATE_time="";
					String CREATE_TIME=oneData100.get("CREATE_TIME").toString();						
					if (CREATE_DATE != null && CREATE_DATE.length() > 0 && CREATE_TIME != null && CREATE_TIME.length() > 0) 
					{
						CREATE_DATE_time = DCP_ConversionTimeFormat.converToDatetime(CREATE_DATE + CREATE_TIME);
					}


					String MODIFY_DATE_time="";
					String MODIFY_DATE=oneData100.get("MODIFY_DATE").toString();	
					String MODIFY_TIME=oneData100.get("MODIFY_TIME").toString();	

					if (MODIFY_DATE != null && MODIFY_DATE.length() > 0 && MODIFY_TIME != null && MODIFY_TIME.length() > 0) 
					{
						MODIFY_DATE_time = DCP_ConversionTimeFormat.converToDatetime(MODIFY_DATE + MODIFY_TIME);
					}


					String CONFIRM_DATE_time="";
					String CONFIRM_DATE=oneData100.get("CONFIRM_DATE").toString();	
					String CONFIRM_TIME=oneData100.get("CONFIRM_TIME").toString();	

					if (CONFIRM_DATE != null && CONFIRM_DATE.length() > 0 && CONFIRM_TIME != null && CONFIRM_TIME.length() > 0) 
					{
						CONFIRM_DATE_time = DCP_ConversionTimeFormat.converToDatetime(CONFIRM_DATE + CONFIRM_TIME);
					}

					String SUBMIT_DATE_time="";
					String SUBMIT_DATE=oneData100.get("SUBMIT_DATE").toString();	
					String SUBMIT_TIME=oneData100.get("SUBMIT_TIME").toString();	

					if (SUBMIT_DATE != null && SUBMIT_DATE.length() > 0 && SUBMIT_TIME != null && SUBMIT_TIME.length() > 0) 
					{
						SUBMIT_DATE_time = DCP_ConversionTimeFormat.converToDatetime(SUBMIT_DATE + SUBMIT_TIME);
					}



					//此处多语言很无奈.....
					String langType="zh_CN";			
					String sqlLang="SELECT LANG_TYPE FROM PLATFORM_STAFFS WHERE EID='"+eId+"' AND status='100' AND OPNO='admin'";
					List<Map<String, Object>> getDataLang=this.doQueryData(sqlLang, null);
					if (getDataLang!=null && getDataLang.isEmpty()==false) 
					{
						langType=getDataLang.get(0).get("LANG_TYPE").toString();
					}					

					String sqldetail=" select B.*,A.FEE_NAME from  DCP_BFEE_DETAIL B LEFT JOIN DCP_FEE_LANG A ON A.EID=B.EID AND A.FEE=B.FEE AND A.LANG_TYPE='"+langType+"'  where B.BFEENO='"+BFEENO+"'  and B.EID='"+eId+"'  and B.SHOPID='"+shopId+"'  order by B.ITEM  ";
					List<Map<String, Object>> sqllistdetail=this.doQueryData(sqldetail, null);
					for (Map<String, Object> oneData101 : sqllistdetail) 
					{
						// 获取单身数据并赋值
						JSONObject body = new JSONObject(); // 存一笔单身
						body.put("seq", oneData101.get("ITEM").toString());
						body.put("item_no", oneData101.get("FEE").toString());
						body.put("item_name", oneData101.get("FEE_NAME").toString());
						body.put("amount", oneData101.get("AMT").toString());
						body.put("remark", oneData101.get("MEMO").toString());
						request_detail.put(body);
					}

					// 给单头赋值
					header.put("version","3.0");			
					header.put("site_no", shopId);						
					header.put("front_no", BFEENO);
					header.put("create_date", B_CreateDate);
					header.put("remark", oneData100.get("MEMO").toString());
					header.put("doc_type", oneData100.get("DOC_TYPE").toString());
					header.put("creator", oneData100.get("CREATEBY").toString());
					header.put("create_datetime", CREATE_DATE_time);
					header.put("modify_no", oneData100.get("MODIFYBY").toString());
					header.put("modify_datetime", MODIFY_DATE_time);
					header.put("approve_no", oneData100.get("CONFIRMBY").toString());
					header.put("approve_datetime", CONFIRM_DATE_time);
					header.put("posted_no", oneData100.get("SUBMITBY").toString());
					header.put("posted_datetime", SUBMIT_DATE_time);
					header.put("squad_no", oneData100.get("WORKNO").toString());
					header.put("tot_amt", oneData100.get("TOT_AMT").toString());		
					header.put("tax_code", oneData100.get("TAXCODE").toString());
					header.put("tax_rate", oneData100.get("TAXRATE").toString());				

					header.put("request_detail", request_detail);
					request.put(header);

					parameter.put("request", request);
					std_data.put("parameter", parameter);
					payload.put("std_data", std_data);

					String str = payload.toString();// 将json对象转换为字符串
					String resbody="";
					logger.info("\r\n******费用单fee.create请求T100传入参数：  " + str + "\r\n");
					//执行请求操作，并拿到结果（同步阻塞）
					try 
					{
					    resbody=HttpSend.Send(str, "fee.create", eId, shopId,shopId,BFEENO);

						logger.info("\r\n******费用单fee.create请求T100返回参数：  "+ "\r\n单号="+ BFEENO + "\r\n" + resbody + "******\r\n");
						if(Check.Null(resbody) || resbody.isEmpty() )
						{
							continue;
						}
						JSONObject jsonres = new JSONObject(resbody);
						JSONObject std_data_res = jsonres.getJSONObject("std_data");
						JSONObject execution_res = std_data_res.getJSONObject("execution");

						// parameter 节点，记录ERP对应的单号和 ERP 对应的组织
						String docNo = "";
						String orgNo = "";
						if(std_data_res.has("parameter")){
							JSONObject parameter_res = std_data_res.getJSONObject("parameter");
							if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
								docNo = parameter_res.get("doc_no").toString();
								orgNo = parameter_res.get("org_no").toString();
							}
						}
						
						String code = execution_res.getString("code");
						//String sqlcode = execution_res.getString("sqlcode");

						String description ="";
						if  (!execution_res.isNull("description") )
						{
							description = execution_res.getString("description");
						}
						if (code.equals("0")) 
						{
							// values
							Map<String, DataValue> values = new HashMap<String, DataValue>();
							DataValue v = new DataValue("Y", Types.VARCHAR);
							values.put("PROCESS_STATUS", v);

							//记录ERP 返回的单号和组织
							DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
							DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
							values.put("PROCESS_ERP_NO", docNoVal);
							values.put("PROCESS_ERP_ORG", orgNoVal);
							
							// condition
							Map<String, DataValue> conditions = new HashMap<String, DataValue>();
							DataValue c1 = new DataValue(shopId, Types.VARCHAR);
							conditions.put("ORGANIZATIONNO", c1);
							DataValue c2 = new DataValue(eId, Types.VARCHAR);
							conditions.put("EID", c2);
							DataValue c3 = new DataValue(shopId, Types.VARCHAR);
							conditions.put("SHOPID", c3);
							DataValue c4 = new DataValue(BFEENO, Types.VARCHAR);
							conditions.put("BFEENO", c4);

							this.doUpdate("DCP_BFEE", values, conditions);
							//
							sReturnInfo="0";

							//删除WS日志  By jzma 20190524
							InsertWSLOG.delete_WSLOG(eId, shopId,"1",BFEENO);
						} 
						else 
						{			
							//
							sReturnInfo="ERP返回错误信息:" + code + "," + description;
							InsertWSLOG.insert_WSLOG("fee.create",BFEENO,eId,shopId,"1",str,resbody,code,description);
						}
					}
					catch (Exception e) 
					{
						//记录WS日志 By jzma 20190524
						InsertWSLOG.insert_WSLOG("fee.create",BFEENO,eId,shopId,"1",str,resbody,"-1",e.getMessage());
						
						sReturnInfo="错误信息:" + e.getMessage();
						//System.out.println(e.toString());
						logger.error("\r\n******费用单fee.create：门店=" +shopId+",组织编码=" + shopId + ",公司编码=" +eId +",单号="  +BFEENO + "\r\n报错信息："+e.getMessage()+"******\r\n");
					}

				}
			}
			else 
			{
				sReturnInfo="错误信息:无单头数据！";
				logger.info("\r\n******费用单fee.create没有要上传的单头数据******\r\n");
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
				
				logger.error("\r\n******费用单fee.create报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
			
				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******费用单fee.create报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********费用单fee.create定时调用End:************\r\n");
		}
		return sReturnInfo;

	}

}
