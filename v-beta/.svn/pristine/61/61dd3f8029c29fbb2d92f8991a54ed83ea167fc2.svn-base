package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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


import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;


//********************差异调整单据上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DifferenceCreate extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";	
	String pOrganizationNO="";
	String pBillNo="";

	public DifferenceCreate()
	{

	}

	public DifferenceCreate(String eId,String shopId,String organizationNO, String billNo)
	{		
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(DifferenceCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe() 
	{		
		//返回信息
		String sReturnInfo="";

		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{		
			logger.info("\r\n*********差异调整difference.create正在执行中,本次调用取消:************\r\n");
			sReturnInfo="定时传输任务-差异调整difference.create正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			
		logger.info("\r\n*********差异调整difference.create定时调用Start:************\r\n");

		try 
		{
			String sql = this.getQuerySql100_05();
			logger.debug("\r\n******差异调整difference.create 执行SQL语句："+sql+"******\r\n");
			String[] conditionValues100_05 = { }; // 查詢條件
			List<Map<String, Object>> getQData100_05 = this.doQueryData(sql, conditionValues100_05);
			if (getQData100_05 != null && getQData100_05.isEmpty() == false) 
			{
				for (Map<String, Object> oneData100: getQData100_05) 
				{
					String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
					String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
					String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
					String differenceNO100 = oneData100.get("DIFFERENCENO") == null ? "" : oneData100.get("DIFFERENCENO").toString();
					String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
					String requisition_date  = "";
					if (bDate != null && bDate.length() >0)
					{ 
						requisition_date  = DCP_ConversionTimeFormat.converToDate(bDate);
					}
					String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
					String status = oneData100.get("STATUS") == null ? "" : oneData100.get("STATUS").toString();
					String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
					String createDate = oneData100.get("CREATEDATE") == null ? "" : oneData100.get("CREATEDATE").toString();
					String createTime = oneData100.get("CREATETIME") == null ? "" : oneData100.get("CREATETIME").toString();
					String create_datetime = "";
					if (createDate != null && createDate.length() >0 && createTime != null && createTime.length() >0)
					{ 
						create_datetime  = DCP_ConversionTimeFormat.converToDatetime(createDate+createTime);
					}
					String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
					String modifyDate = oneData100.get("MODIFYDATE") == null ? "" : oneData100.get("MODIFYDATE").toString();
					String modifyTime = oneData100.get("MODIFYTIME") == null ? "" : oneData100.get("MODIFYTIME").toString();
					String modify_datetime = "";
					if (modifyDate != null && modifyDate.length() >0 && modifyTime != null && modifyTime.length() >0)
					{ 
						modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate+modifyTime);
					}

					String confirmBy = oneData100.get("CONFIRMBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
					String confirmDate = oneData100.get("CONFIRMDATE") == null ? "" : oneData100.get("CONFIRMDATE").toString();
					String confirmTime = oneData100.get("CONFIRMTIME") == null ? "" : oneData100.get("CONFIRMTIME").toString();
					String confirm_datetime = "";
					if (confirmDate != null && confirmDate.length() >0 && confirmTime != null && confirmTime.length() >0)
					{ 
						confirm_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate+confirmTime);
					}

					String submitBy = oneData100.get("SUBMITBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
					String submitDate = oneData100.get("SUBMITDATE") == null ? "" : oneData100.get("CONFIRMDATE").toString();
					String submitTime = oneData100.get("SUBMITTIME") == null ? "" : oneData100.get("CONFIRMTIME").toString();
					String submit_datetime = "";
					if (submitDate != null && submitDate.length() >0 && submitTime != null && submitTime.length() >0)
					{ 
						submit_datetime = DCP_ConversionTimeFormat.converToDatetime(submitDate+submitTime);
					}

					String loadDocType = oneData100.get("LOADDOCTYPE") == null ? "" : oneData100.get("LOADDOCTYPE").toString();
					String loadDocNO = oneData100.get("LOADDOCNO") == null ? "" : oneData100.get("LOADDOCNO").toString();
					String docType = oneData100.get("DOCTYPE") == null ? "" : oneData100.get("DOCTYPE").toString();
                    String load_org = oneData100.get("TRANSFERSHOP") == null ? "" :oneData100.get("TRANSFERSHOP").toString();
					// 获取单身数据
					sql = this.getQuerySql101_05(shopId, eId, organizationNO, differenceNO100);
					String[] conditionValues101_05 = {  }; // 查詢條件
					List<Map<String, Object>> getQData101_05 = this.doQueryData(sql, conditionValues101_05);

					Calendar cal = Calendar.getInstance();// 获得当前时间
					SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
					String timestamp = dfDate.format(cal.getTime()) + dfTime.format(cal.getTime());  

					if (getQData101_05 != null  && getQData101_05.isEmpty() == false) 
					{						
						// payload对象
						JSONObject payload = new JSONObject();
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();
						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						JSONArray request_detail = new JSONArray(); // 存所有单身


						// 获取单身数据并赋值
						for (Map<String, Object> oneData101 : getQData101_05) 
						{ 
							JSONObject body = new JSONObject(); // 存一笔单身
							String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
							String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
							String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
							String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
							String pqty = oneData101.get("REQQTY") == null ? "0" : oneData101.get("REQQTY").toString();
							String bsNO = oneData101.get("BSNO") == null ? "0" : oneData101.get("BSNO").toString();
							String WAREHOUSE = oneData101.get("WAREHOUSE") == null ? "0" : oneData101.get("WAREHOUSE").toString();
							String batchNO = oneData101.get("BATCH_NO") == null ? ""	: oneData101.get("BATCH_NO").toString();
							String prodDate = oneData101.get("PROD_DATE") == null ? ""	: oneData101.get("PROD_DATE").toString();
							String load_item = oneData101.get("LOAD_ITEM") == null ? "" : oneData101.get("LOAD_ITEM").toString();
							
							String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
							String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
							
							body.put("seq", item);
							body.put("load_doc_seq",load_item);
							body.put("item_no", pluNO);
							body.put("feature_no", featureNo);
							body.put("packing_unit", punit);
							body.put("packing_qty", pqty);
							body.put("reason_no", bsNO);
							body.put("warehouse_no", WAREHOUSE);
							body.put("item_batch_no",batchNO ) ;
							body.put("prod_date",prodDate ) ;
							body.put("base_unit",baseUnit ) ;
							body.put("base_qty",baseQty ) ;
							request_detail.put(body);
						}

						// 给单头赋值
						header.put("version", "3.0");
						header.put("front_no", differenceNO100);
						header.put("remark", memo);
						header.put("creator", createBy);
						header.put("create_datetime", create_datetime);
						header.put("modify_no", modifyBy);
						header.put("modify_datetime", modify_datetime);
						header.put("approve_no", submitBy);
						header.put("approve_datetime", submit_datetime);
						header.put("site_no", shopId);
						header.put("load_doc_type", loadDocType);
						header.put("load_doc_no", loadDocNO);
						header.put("load_org", load_org);
						header.put("request_detail", request_detail);

						request.put(header);

						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);

						String str = payload.toString();// 将json对象转换为字符串

						logger.info("\r\n******差异调整difference.create请求T100传入参数：  " + str + "\r\n");
						String	resbody = "";	
						// 执行请求操作，并拿到结果（同步阻塞）
						try 
						{
							resbody=HttpSend.Send(str, "difference.create", eId, shopId,organizationNO,differenceNO100);

							logger.info("\r\n******差异调整difference.create请求T100返回参数：  "+ "\r\n单号="+ differenceNO100 + "\r\n" + resbody + "******\r\n");
							if(Check.Null(resbody) || resbody.isEmpty() )
							{
								continue;
							}
							JSONObject jsonres = new JSONObject(resbody);
							//JSONObject payload_res = jsonres.getJSONObject("payload");
							//JSONObject std_data_res = payload_res.getJSONObject("std_data");
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

							//String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
							String description ="";
							if  (!execution_res.isNull("description") )
							{
								description = execution_res.getString("description");
							}

							if (code.equals("0"))
							{
								// values
								Map<String, DataValue> values = new HashMap<String, DataValue>() ;
								DataValue v = new DataValue("Y", Types.VARCHAR);
								values.put("process_status", v);
								
								//记录ERP 返回的单号和组织
								DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
								DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
								values.put("PROCESS_ERP_NO", docNoVal);
								values.put("PROCESS_ERP_ORG", orgNoVal);
								
								// condition
								Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
								DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
								conditions.put("OrganizationNO", c1);
								DataValue c2 = new DataValue(eId, Types.VARCHAR);
								conditions.put("EID", c2);      
								DataValue c3 = new DataValue(shopId, Types.VARCHAR);
								conditions.put("SHOPID", c3);    
								DataValue c4 = new DataValue(differenceNO100, Types.VARCHAR);
								conditions.put("differenceNO", c4);        

								this.doUpdate("DCP_difference", values, conditions);
								//删除WS日志 By jzma 20190524
								InsertWSLOG.delete_WSLOG(eId, shopId,"1",differenceNO100);
								sReturnInfo="0";
							}
							else
							{ 
								//
								sReturnInfo="ERP返回错误信息:" + code + "," + description;

								//写数据库
								InsertWSLOG.insert_WSLOG("difference.create",differenceNO100,eId,organizationNO,"1",str,resbody,code,description) ;
							}	
						} 
						catch (Exception e) 
						{	
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("difference.create",differenceNO100,eId,shopId,"1",str,resbody,"-1",e.getMessage());
							
							sReturnInfo="错误信息:" + e.getMessage();
							//System.out.println(e.toString());
							logger.error("\r\n******差异调整difference.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +differenceNO100 + "\r\n报错信息："+e.getMessage()+"******\r\n");
						} 							
					}
					else 
					{
						//
						sReturnInfo="错误信息:无单身数据！";

						logger.info("\r\n******差异调整difference.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +differenceNO100 + "无单身数据！******\r\n");
					}
				}
			}
			else 
			{
				//
				sReturnInfo="错误信息:无单头数据！";

				logger.info("\r\n******差异调整difference.create没有要上传的单头数据******\r\n");
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

				logger.error("\r\n******差异调整difference.create报错信息" + e.getMessage() +"\r\n" + errors.toString()+ "******\r\n");

				pw=null;
				errors=null;
			} 
			catch (IOException e1) 
			{					
				logger.error("\r\n******差异调整difference.create报错信息" + e.getMessage() + "******\r\n");
			}		

			//
			sReturnInfo="错误信息:" + e.getMessage();

		}
		finally 
		{
			bRun=false;//
			logger.info("\r\n*********差异调整difference.create定时调用End:************\r\n");
		}			

		//
		return sReturnInfo;
	}


	//DCP_DIFFERENCE
	protected String getQuerySql100_05() throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select organizationNO,EID,DifferenceNO,SHOPID,bDate,memo,status,createBy,createDate,createTime,modifyBy,modifyDate,modifyTime,"
				+ "confirmBy,confirmDate,confirmTime,submitBy,submitDate,submitTime,completeDate,totqty,totamt,totcqty,loadDocType,loadDocNO,docType,transferShop"
				+ " from ("
				+ "select A.ORGANIZATIONNO as organizationNO,A.EID as EID,A.DifferenceNO as DifferenceNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,"
				+ " A.STATUS as status,  A.CREATEBY as createBy,A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.modifyBy as modifyBy,A.modify_Date as modifyDate,A.modify_Time as modifyTime,"
				+ " A.confirmBy as confirmBy,A.confirm_Date as confirmDate,A.confirm_Time as confirmTime,A.SUBMITBY as submitBy, A.SUBMIT_DATE as submitDate, A.SUBMIT_TIME as submitTime, A.COMPLETE_DATE as completeDate,"
				+ " A.TOT_PQTY as totqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty,A.load_DocType as loadDocType,A.load_DocNO as loadDocNO,A.Doc_Type as docType,A.transfer_shop as transferShop"
				+ " from DCP_Difference A WHERE "
				+ " A.status = '1' AND A.process_status = 'N' "
				);

		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false) 
		{
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and DifferenceNO='"+pBillNo+"' ");  		

		}

		sqlbuf.append(" ) TBL ");

		sql = sqlbuf.toString();
		return sql;
	}

	//DCP_Difference_Detail
	protected String getQuerySql101_05(String shopId, String eId, String organizationNO, String differenceNO100) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(""
				+ " select item,pluNO,baseunit,punit,pqty,baseqty,unitRatio,price,amt,reqQty,bsno,"
				+ " WAREHOUSE,BATCH_NO,PROD_DATE,LOAD_ITEM,featureno  "
				+ " from ("
				+ " SELECT a.item,a.pluNO ,a.baseunit, a.punit,a.pqty,a.baseqty,"
				+ " a.UNIT_RATIO as unitRatio,"
				+ " a.price,a.amt, "
				+ " a.req_qty as reqQty,a.bsno as bsno,a.WAREHOUSE,a.BATCH_NO,a.PROD_DATE,"
				+ " case when nvl(a.load_item,0)=0 then oitem else  load_item end  as load_item,"
				+ " featureno "
				+ " FROM DCP_Difference_Detail  a "
				+ " WHERE a.SHOPID = '"+shopId+"'  AND a.EID = '"+eId+"' AND "
				+ " A.ORGANIZATIONNO = '"+organizationNO+"' AND a.differenceNO = '"+differenceNO100+"' "
				);
		sqlbuf.append(" ) TBL ");

		sql = sqlbuf.toString();
		return sql;
	}    

}
