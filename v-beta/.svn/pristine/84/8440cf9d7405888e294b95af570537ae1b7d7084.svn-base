package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;

import com.dsc.spos.waimai.HelpTools;

public class OrderPayCreate extends InitJob  
{
	Logger logger = LogManager.getLogger(OrderPayCreate.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String warningLogFileName = "OrderPayCreate";
	String langType = "zh_CN";

	public OrderPayCreate()
	{
		
	}
	public String doExe() throws Exception
	{
	   // 返回信息
			String sReturnInfo = "";
			
			logger.info("\r\n***************OrderPayCreate同步START****************\r\n");
			HelpTools.writelog_fileName("【OrderPayCreate发送消息】同步START！",warningLogFileName);
			try 
			{
			//此服务是否正在执行中
				if (bRun)
				{		
					logger.info("\r\n*********OrderPayCreate同步正在执行中,本次调用取消:************\r\n");
					HelpTools.writelog_fileName("【OrderPayCreate循环处理】同步正在执行中,本次调用取消！",warningLogFileName);
					return sReturnInfo;
				}

				bRun=true;
				
				String sql ="";
				sql = this.getMsgLogSql();
				HelpTools.writelog_fileName("【OrderPayCreate循环处理】同步正在执行中,查询语句sql:"+sql,warningLogFileName);
				List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
				if(getQDataDetail!=null&&getQDataDetail.isEmpty()==false)
				{
				  //单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
					condition.put("EID", true);	
					condition.put("ORDERNO", true);	
					condition.put("LOAD_DOCTYPE", true);	
					//condition.put("SHOPID", true);
					//调用过滤函数
					List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
					for (Map<String, Object> oneData : getQHeader) 
					{
						JSONObject payload = new JSONObject();
						// 自定义payload中的json结构
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();

						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						//JSONArray orderpay_detail = new JSONArray(); // 商品单身
						
						JSONArray orderpay_detail = new JSONArray(); // 付款单身
						
						
						
						String eId = oneData.get("EID").toString();
						String orderNO = oneData.get("ORDERNO").toString();
						String shopId = oneData.get("SHOPID").toString();
						String loadDocType=oneData.get("LOAD_DOCTYPE").toString();
						for (Map<String, Object> oneData_pay : getQDataDetail) 
						{
							String companyNO_pay = oneData_pay.get("EID").toString();
							String orderNO_pay = oneData_pay.get("ORDERNO").toString();
							String shopNO_pay = oneData_pay.get("SHOPID").toString();
							String pay_item = oneData_pay.get("PAYITEM").toString();
							String loadDocType_pay=oneData_pay.get("LOAD_DOCTYPE").toString();
							if(pay_item==null||pay_item.isEmpty()||pay_item.length()==0)
							{
								continue;
							}
							if(eId.equals(companyNO_pay)&&orderNO.equals(orderNO_pay)&&loadDocType.equals(loadDocType_pay))
							{
								JSONObject body_pay = new JSONObject(); 
								body_pay.put("seq", pay_item);
								body_pay.put("paycode", oneData_pay.get("PAYCODE").toString());					
								body_pay.put("paycodeerp", oneData_pay.get("PAYCODEERP").toString());
								body_pay.put("payname", oneData_pay.get("PAYNAME").toString());
								body_pay.put("pay", oneData_pay.get("PAY").toString());
								body_pay.put("thirdtransno", oneData_pay.get("PAYSERNUM").toString());
								body_pay.put("bdate", oneData_pay.get("BDATE").toString());								
								body_pay.put("isinvoice", "");
								body_pay.put("invoicetype", "");
								body_pay.put("invoiceno", "");
								body_pay.put("loaddoctype", oneData_pay.get("LOAD_DOCTYPE_PAY").toString());
								body_pay.put("shoppay", oneData_pay.get("SHOP_PAY").toString());
								body_pay.put("changed", oneData_pay.get("CHANGED").toString());
								body_pay.put("extra", oneData_pay.get("EXTRA").toString());
								body_pay.put("cttype", oneData_pay.get("CTTYPE").toString());
								orderpay_detail.put(body_pay);
								
							}
			
						}
						
						header.put("modify_no", "");
						header.put("modify_datetime", "");
						header.put("site_no", shopId);
						header.put("front_no", orderNO);
						header.put("remark", "");
						
						
						header.put("orderpay_detail", orderpay_detail);//付款档
						
						
						request.put(header);

						parameter.put("orderpay", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);							
						String str = payload.toString();// 将json对象转换为字符串	
						
						
						HelpTools.writelog_fileName("订金增加orderpay.create请求ERP传入参数："+str,warningLogFileName);
						String resbody="";						
						try 
						{
							resbody=HttpSend.Send(str, "orderpay.create", eId, shopId,shopId,orderNO);

							HelpTools.writelog_fileName("订金增加orderpay.create请求ERP 返回："+resbody+" 单号orderno="+orderNO,warningLogFileName);
							if(Check.Null(resbody) || resbody.isEmpty() )
							{
								continue;
							}
							JSONObject jsonres = new JSONObject(resbody);
							JSONObject std_data_res = jsonres.getJSONObject("std_data");
							JSONObject execution_res = std_data_res.getJSONObject("execution");

							String code = execution_res.getString("code");
						
							String description ="";
							if  (!execution_res.isNull("description") )
							{
							     description = execution_res.getString("description");
							}
							if (code.equals("0"))
							{
								// values
								Map<String, DataValue> values = new HashMap<String, DataValue>() ;
								DataValue v= new DataValue("Y", Types.VARCHAR);
								values.put("process_status", v);
								
								// condition
								Map<String, DataValue> conditions = new HashMap<String, DataValue>() ;
								DataValue c1 = new DataValue(loadDocType, Types.VARCHAR);
								conditions.put("LOAD_DOCTYPE", c1);
								
								DataValue c2 = new DataValue(eId, Types.VARCHAR);
								conditions.put("EID", c2);			
								DataValue c4 = new DataValue(orderNO, Types.VARCHAR);
								conditions.put("ORDERNO", c4);				

								this.doUpdate("OC_ORDER_PAY", values, conditions);
								HelpTools.writelog_fileName("订金增加orderpay.create更新数据状态成功！单号orderNo="+orderNO,warningLogFileName);	
								//
								sReturnInfo="0";
													
							}
							else
							{ 
								//
								
								sReturnInfo="ERP返回错误信息:" + code + "," + description;
								//写数据库
								InsertWSLOG.insert_WSLOG("orderpay.create",orderNO,eId,shopId,"1",str,resbody,code,description) ;
							}
						} 
						catch (Exception e) 
						{
							
							sReturnInfo="错误信息:" + e.getMessage();
							InsertWSLOG.insert_WSLOG("orderpay.create",orderNO,eId,shopId,"1",str,resbody,"-1",e.getMessage());							
							HelpTools.writelog_fileName("订金增加orderpay.create请求ERP返回错误："+e.getMessage(),warningLogFileName);		
							continue;
						}
						
						
					}
							
				}
				else 
				{
					//
					sReturnInfo="无符合要求的数据！";
					HelpTools.writelog_fileName("【OrderPayCreate发送消息】没有需要处理的订单消息！",warningLogFileName);
					logger.info("\r\n******OrderPayCreate没有需要获取的订单ID******\r\n");
				}
		
			} 
			catch (Exception e) 
			{
				logger.error("\r\n***************OrderPayCreate异常"+e.getMessage()+"****************\r\n");
				sReturnInfo="错误信息:" + e.getMessage();
				HelpTools.writelog_fileName("【OrderPayCreate发送消息】异常！"+sReturnInfo,warningLogFileName);
		
			}
			finally 
			{	
				bRun=false;//
		
			}
			
			
			
			
			logger.info("\r\n***************OrderPayCreate同步END****************\r\n");
			HelpTools.writelog_fileName("【OrderPayCreate循环处理】同步END！",warningLogFileName);
			return sReturnInfo;	
	}
	
	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);

	}
	
	
	private String getMsgLogSql()
	{
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" select a.*,C.ITEM AS PAYITEM,C.PAYCODE,C.PAYCODEERP,C.PAYNAME,C.CARDNO AS PAYCARDNO,C.CTTYPE,C.PAYSERNUM,C.SERIALNO,C.REFNO,C.TERIMINALNO,C.DESCORE,C.PAY,C.EXTRA,C.CHANGED,C.BDATE,C.ISORDERPAY,C.ISONLINEPAY,C.ORDER_PAYCODE,C.RCPAY,C.SHOP_PAY,C.LOAD_DOCTYPE_PAY ");
		sqlbuf.append(" from OC_order a ");//已经启用
		sqlbuf.append(" inner join OC_order_pay C on a.EID=C.EID and a.load_doctype=C.load_doctype and a.orderno=C.orderno ");
		sqlbuf.append(" where a.status<>'3' and a.status<>'12' and C.process_status='N' and c.ISORDERPAY='Y'  order by a.create_datetime,a.orderno,c.item");
		sqlbuf.append(")");
		return sqlbuf.toString();		
	}
	

}
