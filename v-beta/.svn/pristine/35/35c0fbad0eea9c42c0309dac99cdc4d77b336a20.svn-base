package com.dsc.spos.scheduler.job;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
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

//********************采购收货入库单据上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ReceiptUpdate extends InitJob
{
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";
	String pOrganizationNO="";
	String pBillNo="";
	
	public ReceiptUpdate()
	{
	
	}
	
	public ReceiptUpdate(String eId,String shopId,String organizationNO, String billNo)
	{
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}
	
	Logger logger = LogManager.getLogger(ReceiptUpdate.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe()
	{
		//返回信息
		String sReturnInfo="";
		
		//此服务是否正在执行中
		if (bRun && pEId.equals(""))
		{
			logger.info("\r\n*********采购收货入库receipt.update正在执行中,本次调用取消:************\r\n");
			
			sReturnInfo="定时传输任务-采购收货入库receipt.update正在执行中！";
			return sReturnInfo;
		}
		
		bRun=true;//			
		
		logger.info("\r\n*********采购收货入库receipt.update定时调用Start:************\r\n");
		
		try
		{
			
			String sql = this.getQuerySql();
			
			logger.info("\r\n******采购收货入库receipt.update 执行SQL语句："+sql+"******\r\n");
			
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			
			if (getQData != null && getQData.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQData)
				{
					
					String shopId = oneData.get("SHOPID") == null ? "" : oneData.get("SHOPID").toString();
					String eId = oneData.get("EID") == null ? ""	: oneData.get("EID").toString();
					String organizationNO = oneData.get("ORGANIZATIONNO") == null ? "" : oneData.get("ORGANIZATIONNO").toString();
					String sStockInNO = oneData.get("SSTOCKINNO") == null ? "" : oneData.get("SSTOCKINNO").toString();
					String memo = oneData.get("MEMO") == null ? "" : oneData.get("MEMO").toString();
					String docType = oneData.get("DOC_TYPE") == null ? "" : oneData.get("DOC_TYPE").toString();
					String createBy = oneData.get("CREATEBY") == null ? "" : oneData.get("CREATEBY").toString();
					String createDate = oneData.get("CREATE_DATE") == null ? "" : oneData.get("CREATE_DATE").toString();
					String createTime = oneData.get("CREATE_TIME") == null ? "" : oneData.get("CREATE_TIME").toString();
					String create_datetime = "";
					if (createDate != null && createDate.length() > 0 && createTime != null	&& createTime.length() > 0)
					{
						create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
					}
					String confirmBy = oneData.get("CONFIRMBY") == null ? "" : oneData.get("CONFIRMBY").toString();
					String confirmDate = oneData.get("CONFIRM_DATE") == null ? "" : oneData.get("CONFIRM_DATE").toString();
					String confirmTime = oneData.get("CONFIRM_TIME") == null ? "" : oneData.get("CONFIRM_TIME").toString();
					String approve_datetime = "";
					if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null && confirmTime.length() > 0)
					{
						approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
					}
					String accountBy = oneData.get("ACCOUNTBY") == null ? "" : oneData.get("ACCOUNTBY").toString();
					String accountDate = oneData.get("ACCOUNT_DATE") == null ? "" : oneData.get("ACCOUNT_DATE").toString();
					String accountTime = oneData.get("ACCOUNT_TIME") == null ? "" : oneData.get("ACCOUNT_TIME").toString();
					String posted_datetime = "";
					if (accountDate != null && accountDate.length() > 0 && accountTime != null && accountTime.length() > 0)
					{
						posted_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate + accountTime);
					}
					String LOAD_RECEIPTNO = oneData.get("LOAD_RECEIPTNO") == null ? "" : oneData.get("LOAD_RECEIPTNO").toString();
					String supplierNO = oneData.get("SUPPLIER") == null ? "" : oneData.get("SUPPLIER").toString();
					
					//2018-11-08 yyy 添加 PtemplateNo 模板编码
					String ptemplateNo  = oneData.get("PTEMPLATENO") == null ? "" : oneData.get("PTEMPLATENO").toString();
					String ofNO  = oneData.get("OFNO") == null ? "" : oneData.get("OFNO").toString();
					
					String ecsflg="Y";
					if (!Check.Null(ofNO))
					{
						sql = this.getReceivingQuerySql(eId,shopId,ofNO);
						
						List<Map<String, Object>> getReceivingQData = this.doQueryData(sql, null);
						if (getReceivingQData!=null && getReceivingQData.isEmpty()==false)
						{
							String status=getReceivingQData.get(0).get("STATUS").toString();
							if (status.equals("6")) ecsflg="N";
						}
					}
					
					String buyerno = oneData.get("BUYERNO") == null ? "" : oneData.get("BUYERNO").toString();
					String buyername = oneData.get("BUYERNAME") == null ? "" : oneData.get("BUYERNAME").toString();
					String tax_code  = oneData.get("TAXCODE") == null ? "" : oneData.get("TAXCODE").toString();
					// 获取单身数据
					sql = this.getQueryDetailSql(eId, organizationNO, sStockInNO);
					List<Map<String, Object>> getQDetailData = this.doQueryData(sql, null);
					if (getQDetailData != null && getQDetailData.isEmpty() == false)
					{
						//payload对象
						JSONObject payload = new JSONObject();
						
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();
						
						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						JSONArray request_detail = new JSONArray(); // 存所有单身
						
						for (Map<String, Object> oneData101 : getQDetailData)
						{
							// 获取单身数据并赋值
							JSONObject body = new JSONObject(); // 存一笔单身
							String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
							String oItem = oneData101.get("OITEM") == null ? "" : oneData101.get("OITEM").toString();
							String pluNO = oneData101.get("PLUNO") == null ? ""	: oneData101.get("PLUNO").toString();
							String pluMemo = oneData101.get("PLUMEMO") == null ? "" : oneData101.get("PLUMEMO").toString();
							String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
							String punit = oneData101.get("PUNIT") == null ? ""	: oneData101.get("PUNIT").toString();
							String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
							String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
							String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
							//String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
							String distriPrice = oneData101.get("DISTRIPRICE") == null ? "0" : oneData101.get("DISTRIPRICE").toString();
							BigDecimal pqty_b = new BigDecimal(pqty);
							if (Check.Null(distriPrice)) distriPrice="0";
							BigDecimal distriPrice_b = new BigDecimal(distriPrice);
							if (distriPrice_b.compareTo(BigDecimal.ZERO)==0 && docType.equals("2"))//进货价为零时，取单价作为进货价，考虑老的版本，进货价都保存为单价了
							{
								distriPrice_b =  new BigDecimal(price);
							}
							// 不要四舍五入，因为位数不确定所以直接取数据库  BY JZMA 20200401
							//distriPrice_b=distriPrice_b.setScale(2, BigDecimal.ROUND_HALF_UP);//四舍五入保留两位小数
							distriPrice=distriPrice_b.toString();
							
							//BigDecimal amt_b = pqty_b.multiply(distriPrice_b).setScale(2, BigDecimal.ROUND_HALF_UP);
							String WAREHOUSE=oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
							String batchNO = oneData101.get("BATCH_NO") == null ? "" : oneData101.get("BATCH_NO").toString();
							String prodDate = oneData101.get("PROD_DATE") == null ? "" : oneData101.get("PROD_DATE").toString();
							
							// 新增进货单价和进货金额  BY JZMA 2019-09-12
							String distriAmt = oneData101.get("DISTRIAMT") == null ? "0" : oneData101.get("DISTRIAMT").toString();
							String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
							
							//【ID1023332】【货郎先生3.0.0.8】( C7004 )乌兰察布万达广场店的采购退货单ZCTH2022010400001，
							// 从采购上原单做的，与原单采购的项次不一致，导致上ERP失败 by jinzma 20220114
							/*if(docType.equals("1")||docType.equals("3")) {
								body.put("seq", item);
							} else {
								body.put("seq", oItem);
							}*/
							
							body.put("seq", item);
							body.put("source_seq", oItem);
							body.put("item_no", pluNO);
							body.put("item_memo", pluMemo);
							body.put("packing_unit", punit);
							body.put("packing_qty", pqty);
							body.put("base_unit", baseUnit);
							body.put("base_qty", baseQty);
							body.put("warehouse_no", WAREHOUSE);
							body.put("price", distriPrice);  //传ERP的单价改成进货价
							body.put("amount", distriAmt); //传ERP的金额改成PQTY*进货价
							body.put("item_batch_no", batchNO);
							body.put("prod_date", prodDate);
							body.put("distri_price", distriPrice);
							body.put("distri_amount", distriAmt);
							body.put("feature_no", featureNo);
							
							request_detail.put(body);
						}
						
						// 给单头赋值
						header.put("front_no", sStockInNO);
						header.put("site_no", shopId);
						header.put("receipt_no", LOAD_RECEIPTNO);
						header.put("remark", memo);
						header.put("doc_type", docType);
						//2018-11-08 yyy 添加以下四个字段
						header.put("supplier", supplierNO); // 供应商编码 supplier 和 下面的supplier_no 相同
						header.put("po_template_no", ptemplateNo);
						header.put("creator", createBy);
						header.put("create_datetime", create_datetime);
						header.put("ecsflg", ecsflg);
						header.put("approve_no", confirmBy);
						header.put("approve_datetime", approve_datetime);
						header.put("posted_no", accountBy);
						header.put("posted_datetime", posted_datetime);
						header.put("supplier_no", supplierNO);
						header.put("buyer_no", buyerno);
						header.put("version", "3.0");
						//header.put("buyername", buyername);
						header.put("tax_code",tax_code);
						header.put("request_detail", request_detail);
						request.put(header);
						
						
						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);
						
						String str = payload.toString();// 将json对象转换为字符串
						
						logger.info("\r\n******采购收货入库receipt.update请求T100传入参数：  " + str + "\r\n");
						String resbody="";
						//执行请求操作，并拿到结果（同步阻塞）
						try
						{
							resbody=HttpSend.Send(str, "receipt.update", eId, shopId,organizationNO,sStockInNO);
							
							logger.info("\r\n******采购收货入库receipt.update请求T100返回参数：  "+ "\r\n单号="+ sStockInNO + "\r\n" + resbody + "******\r\n");
							if(Check.Null(resbody) || resbody.isEmpty() )
							{
								continue;
							}
							JSONObject jsonres = new JSONObject(resbody);
							JSONObject std_data_res = jsonres.getJSONObject("std_data");
							JSONObject execution_res = std_data_res.getJSONObject("execution");
							String code = "";
							if(!execution_res.isNull("code"))
							{
								code = execution_res.getString("code");
							}else
							{
								sReturnInfo="返回code的值不可为null" ;
								InsertWSLOG.insert_WSLOG("receipt.update",sStockInNO,eId,shopId,"1",str, resbody,"-1",sReturnInfo);
								logger.error("\r\n******采购收货入库receipt.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockInNO + "\r\n报错信息："+sReturnInfo+"******\r\n");
								continue;
							}
							// parameter 节点，记录ERP对应的单号和 ERP 对应的组织
							String docNo = "";
							String orgNo = "";
							if(std_data_res.has("parameter")){
								JSONObject parameter_res = std_data_res.getJSONObject("parameter");
								if(parameter_res.has("doc_no") && parameter_res.has("org_no")){
									if(!parameter_res.isNull("doc_no"))
									{
										docNo = parameter_res.get("doc_no").toString();
									}else
									{
										sReturnInfo="返回docno的值不可为null" ;
										InsertWSLOG.insert_WSLOG("receipt.update",sStockInNO,eId,shopId,"1",str, resbody,"-1",sReturnInfo);
										logger.error("\r\n******采购收货入库receipt.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockInNO + "\r\n报错信息："+sReturnInfo+"******\r\n");
										continue;
									}
									if(!parameter_res.isNull("org_no"))
									{
										orgNo = parameter_res.get("org_no").toString();
									}else
									{
										sReturnInfo="返回docno的值不可为null" ;
										InsertWSLOG.insert_WSLOG("receipt.update",sStockInNO,eId,shopId,"1",str, resbody,"-1",sReturnInfo);
										logger.error("\r\n******采购收货入库receipt.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockInNO + "\r\n报错信息："+sReturnInfo+"******\r\n");
										continue;
									}
								}
							}
							
							
							
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
								Map<String, DataValue> values = new HashMap<String, DataValue>();
								DataValue v = new DataValue("Y", Types.VARCHAR);
								values.put("process_status", v);
								DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
								values.put("UPDATE_TIME", v1);
								values.put("TRAN_TIME", v1);
								//记录ERP 返回的单号和组织
								DataValue docNoVal = new DataValue(docNo, Types.VARCHAR);
								DataValue orgNoVal = new DataValue(orgNo, Types.VARCHAR);
								values.put("PROCESS_ERP_NO", docNoVal);
								values.put("PROCESS_ERP_ORG", orgNoVal);
								
								// condition
								Map<String, DataValue> conditions = new HashMap<String, DataValue>();
								DataValue c1 = new DataValue(organizationNO, Types.VARCHAR);
								conditions.put("OrganizationNO", c1);
								DataValue c2 = new DataValue(eId, Types.VARCHAR);
								conditions.put("EID", c2);
								DataValue c3 = new DataValue(shopId, Types.VARCHAR);
								conditions.put("SHOPID", c3);
								DataValue c4 = new DataValue(sStockInNO, Types.VARCHAR);
								conditions.put("sStockInNO", c4);
								
								this.doUpdate("DCP_SSTOCKIN", values, conditions);
								
								//删除WS日志 By jzma 20190524
								InsertWSLOG.delete_WSLOG(eId, shopId,"1",sStockInNO);
								sReturnInfo="0";
							}
							else
							{
								//
								sReturnInfo="ERP返回错误信息:" + code + "," + description;
								
								InsertWSLOG.insert_WSLOG("receipt.update",sStockInNO,eId,organizationNO,"1",str,resbody,code,description);
							}
						}
						catch (Exception e)
						{
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("receipt.update",sStockInNO,eId,shopId,"1",str, resbody,"-1",e.getMessage());
							sReturnInfo="错误信息:" + e.getMessage();
							
							//System.out.println(e.toString());
							
							logger.error("\r\n******采购收货入库receipt.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockInNO + "\r\n报错信息："+e.getMessage()+"******\r\n");
							
						}
					}
					else
					{
						//
						sReturnInfo="错误信息:无单身数据！";
						
						logger.info("\r\n******采购收货入库receipt.update：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockInNO + "无单身数据！******\r\n");
					}
				}
			}
			else
			{
				//
				sReturnInfo="错误信息:无单头数据！";
				
				logger.info("\r\n******采购收货入库receipt.update没有要上传的单头数据******\r\n");
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
				
				logger.error("\r\n******采购收货入库receipt.update报错信息" + e.getMessage()+"\r\n" + errors.toString() + "******\r\n");
				
				pw=null;
				errors=null;
			}
			catch (IOException e1)
			{
				logger.error("\r\n******采购收货入库receipt.update报错信息" + e.getMessage() + "******\r\n");
			}
			
			//
			sReturnInfo="错误信息:" + e.getMessage();
			
		}
		finally
		{
			bRun=false;//
			logger.info("\r\n*********采购收货入库receipt.update定时调用End:************\r\n");
		}
		
		//
		return sReturnInfo;
		
	}
	
	
	
	//DCP_SSTOCKIN
	protected String getQuerySql() throws Exception
	{
		String sql = null;
		
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append(""
				+ " select * FROM DCP_SSTOCKIN "
				+ " WHERE process_status ='N' and status = '2' "
		);
		
		
		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false)
		{
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and SSTOCKINNO='"+pBillNo+"' ");
		}
		
		sql = sqlbuf.toString();
		
		return sql;
	}
	
	//DCP_SSTOCKIN_DETAIL
	protected String getQueryDetailSql(String eId,String organizationNO, String sStockInNO100) throws Exception
	{
		String sql = null;
		
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append(""
				+ "select item,oItem,pluNO,PLUMEMO,baseunit,punit,pqty,baseqty, "
				+ " unitRatio,price,amt,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno from ("
				+ " SELECT ITEM,OITEM,PLUNO,plu_memo as PLUMEMO,baseunit,PUNIT,PQTY,baseqty,"
				+ " UNIT_RATIO AS UNITRATIO,PRICE,AMT,WAREHOUSE,batch_no,prod_date,DISTRIPRICE,DISTRIAMT,featureno FROM DCP_SSTOCKIN_DETAIL "
				+ " WHERE EID = '"+eId+"'  " + " AND ORGANIZATIONNO = '"+organizationNO+"'  " + " AND SSTOCKINNO = '"+sStockInNO100+"' "
		);
		
		sqlbuf.append(" ) TBL ");
		
		sql = sqlbuf.toString();
		
		return sql;
	}
	
	//DCP_receiving
	protected String getReceivingQuerySql(String eId,String shopId,String ofNO) throws Exception
	{
		String sql = null;
		sql=" select STATUS from DCP_receiving "
				+ " where EID='"+eId+"' and  SHOPID='"+shopId+"' and doc_type='2' and receivingno='"+ofNO+"'  ";
		
		return sql;
	}
	
	
}
