package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.waimai.HelpTools;

/**
 * 缴款单上传ERP
 * @author 86187
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PayInCreate_JKDJ extends InitJob
{
	Logger logger = LogManager.getLogger(OrderStatusLogCreate.class.getName());

	String goodsLogFileName = "PayInCreate_JKDJ";
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() throws Exception
	{
		String sReturnInfo = "";
		String logStartStr = "【同步任务PayInCreate_JKDJ】";

		HelpTools.writelog_fileName(logStartStr+"同步START！", goodsLogFileName);
		// 此服务是否正在执行中
		if (bRun)
		{
			logger.info("\r\n*********"+logStartStr+"正在执行中,本次调用取消:************\r\n");
			HelpTools.writelog_fileName(logStartStr+"正在执行中,本次调用取消！", goodsLogFileName);
			return sReturnInfo;
		}
		bRun = true;//
		try
		{
			//status状态（0新增 1已提交 2已上传  3已同意  4已驳回）
			String sql_header = "select * from dcp_payin where STATUS='1' and PROCESS_STATUS='N' and rownum<=100 order by TRAN_TIME desc";
			
			HelpTools.writelog_fileName("缴款单上传调用 payin.create接口通知ERP，查询单头SQL="+sql_header,goodsLogFileName);
			List<Map<String, Object>> getQDataHeader = this.doQueryData(sql_header, null);
			if (getQDataHeader != null && getQDataHeader.isEmpty() == false) 
			{
				for (Map<String, Object> map : getQDataHeader)
				{
					String eId = map.get("EID").toString();
					String shopId = map.get("SHOPID").toString();
					String payInNo = map.get("PAYINNO").toString();
					String sql_detail = " select * from DCP_PAYIN_DETAIL where EID='"+eId+"' AND SHOPID='"+shopId+"' AND PAYINNO='"+payInNo+"' ";
					
					List<Map<String, Object>> getQDataDetail = this.doQueryData(sql_detail, null);
					if(getQDataDetail==null||getQDataDetail.isEmpty())
					{
						HelpTools.writelog_fileName("缴款单上传调用 payin.create接口通知ERP，该单有单头没有单身明细！过滤掉！eId="+eId+",shopId="+shopId+",payInNo="+payInNo,goodsLogFileName);
						continue;
					}
					
					// t100req中的payload对象
					JSONObject payload = new JSONObject();
					// 自定义payload中的json结构
					JSONObject std_data = new JSONObject();
					JSONObject parameter = new JSONObject();

					JSONArray request = new JSONArray();
					JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
					JSONArray payIn_detail = new JSONArray(); // 商品单身
					
					header.put("version", "3.0");
					header.put("eId", eId);
					header.put("shopId", shopId);
					header.put("payInNo", payInNo);
					header.put("bDate", map.get("BDATE").toString());
					header.put("rDate", map.get("RDATE").toString());
					header.put("totAmt", map.get("TOTAMT").toString());
					header.put("memo", map.get("MEMO").toString());
					header.put("lastModiOpId", map.get("LASTMODIOPID").toString());
					header.put("lastModiOpName", map.get("LASTMODIOPNAME").toString());
					header.put("lastModiTime", map.get("LASTMODITIME").toString());
					

					for (Map<String, Object> mapDetail : getQDataDetail)
					{
						
						JSONObject body = new JSONObject();
						body.put("item", mapDetail.get("ITEM").toString());
						body.put("account", mapDetail.get("ACCOUNT").toString());
						body.put("bankNo", mapDetail.get("BANKNO").toString());
						body.put("bankDocNo", mapDetail.get("BANKDOCNO").toString());
						body.put("certificate", mapDetail.get("CERTIFICATE").toString());
						body.put("amt", mapDetail.get("AMT").toString());
						payIn_detail.put(body);
						
					}
					header.put("payIn_detail", payIn_detail);
					
					request.put(header);
					
					parameter.put("request", request);
					std_data.put("parameter", parameter);
					payload.put("std_data", std_data);
					
					String reqStr = payload.toString();
					
					HelpTools.writelog_fileName("缴款单上传调用 payin.create请求ERP传入参数："+reqStr,goodsLogFileName);
					String resbody="";
					try
					{

						resbody=HttpSend.Send(reqStr, "payin.create", eId, shopId,shopId,payInNo);
						HelpTools.writelog_fileName("缴款单上传调用 payin.create返回："+resbody,goodsLogFileName);
						if(Check.Null(resbody) || resbody.isEmpty() )
						{
							InsertWSLOG.insert_WSLOG("payin.create", payInNo, eId, shopId, "1", reqStr, resbody, "-1", "");
							continue;
						}
						JSONObject jsonres = new JSONObject(resbody);
						JSONObject std_data_res = jsonres.getJSONObject("std_data");
						JSONObject execution_res = std_data_res.getJSONObject("execution");

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
							maintainProcessStatus(eId,shopId,payInNo);
							sReturnInfo = "0";
																					
						}
					
						else 
						{
							sReturnInfo = "ERP返回错误信息:" + code + "," + description;
							//写数据库
							InsertWSLOG.insert_WSLOG("payin.create", payInNo, eId, shopId, "1", reqStr, resbody, code, description);
							HelpTools.writelog_fileName("缴款单上传调用 payin.create失败，ERP返回："+description,goodsLogFileName);			
						}
					
						
					} 
					catch (Exception e)
					{
						// TODO: handle exception
					}
				}
			}
			else
			{
				HelpTools.writelog_fileName("缴款单上传，查询无数据！",goodsLogFileName);
				logger.info("\r\n*********"+logStartStr+"查询无数据！************\r\n");
			}
			

		} catch (Exception e)
		{
			// TODO: handle exception
		} finally
		{
			bRun = false;
			logger.info("\r\n*********"+logStartStr+"同步END！************\r\n");
			HelpTools.writelog_fileName(logStartStr+"同步END！", goodsLogFileName);
		}

		return sReturnInfo;
	}
	
	private void maintainProcessStatus( String eId, String shopId, String payInNo) throws Exception
	{
		try
		{
			// values
			Map<String, DataValue> values = new HashMap<String, DataValue>();
			DataValue v = new DataValue("Y", Types.VARCHAR);
			values.put("PROCESS_STATUS", v);
			DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR);
			values.put("UPDATE_TIME", v1);
			values.put("TRAN_TIME", v1);

			values.put("STATUS", new DataValue("2", Types.VARCHAR));//状态（0新增 1已提交 2已上传  3已同意  4已驳回）
			// condition
			Map<String, DataValue> conditions = new HashMap<String, DataValue>();
			DataValue c1 = new DataValue(shopId, Types.VARCHAR);
			conditions.put("SHOPID", c1);
			DataValue c2 = new DataValue(eId, Types.VARCHAR);
			conditions.put("EID", c2);
			DataValue c4 = new DataValue(payInNo, Types.VARCHAR);
			conditions.put("PAYINNO", c4);

			this.doUpdate("DCP_PAYIN", values, conditions);

			//删除WS日志  By jzma 20201120
			String deleteShop =shopId;
			if (Check.Null(deleteShop))
				deleteShop=" ";
			InsertWSLOG.delete_WSLOG(eId, deleteShop,"1",payInNo);
		}
		catch (Exception e)
		{
			HelpTools.writelog_fileName("缴款单上传，更新处理状态异常！"+e.getMessage()+",eId="+eId+",shopId="+shopId+",payInNo="+payInNo,goodsLogFileName);			
		}
	}


}
