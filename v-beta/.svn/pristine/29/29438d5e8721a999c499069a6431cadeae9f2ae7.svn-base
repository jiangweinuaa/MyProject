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


//********************其他库存调整单据上传**************************
//*****************ERP规格doc_type=2其他入库单*********************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InventoryAdjustCreateOtherIn extends InitJob {
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";
	String pOrganizationNO="";
	String pBillNo="";
	
	public InventoryAdjustCreateOtherIn() {
	
	}
	
	public InventoryAdjustCreateOtherIn(String eId,String shopId,String organizationNO, String billNo) {
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}
	
	Logger logger = LogManager.getLogger(InventoryAdjustCreateOtherIn.class.getName());
	
	static boolean bRun=false;//标记此服务是否正在执行中
	
	public String doExe() {
		//返回信息
		String sReturnInfo="";
		
		//此服务是否正在执行中
		if (bRun && pEId.equals("")) {
			logger.info("\r\n*********ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create正在执行中,本次调用取消:************\r\n");
			
			sReturnInfo="定时传输任务-ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create正在执行中！";
			return sReturnInfo;
		}
		
		bRun=true;//			
		
		logger.info("\r\n*********ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create定时调用Start:************\r\n");
		
		try {
			
			//*******************************************************************************
			//*****************开始ERP规格doc_type=2其他入库单开始***************
			//logger.info("\r\n*********开始ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create开始************\r\n");
			//*******************************************************************************
			
			String sql = this.getQuerySql();
			
			logger.debug("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create执行SQL语句："+sql+"******\r\n");
			
			String[] conditionValues100_06 = { }; // 查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues100_06);
			
			if (getQData != null && getQData.isEmpty() == false) {
				for (Map<String, Object> oneData : getQData) {
					String shopId = oneData.get("SHOPID") == null ? "" : oneData.get("SHOPID").toString();
					String eId = oneData.get("EID") == null ? "" : oneData.get("EID").toString();
					String organizationNO = oneData.get("ORGANIZATIONNO") == null ? "" : oneData.get("ORGANIZATIONNO").toString();
					String stockInNO100 = oneData.get("STOCKINNO") == null ? "" : oneData.get("STOCKINNO").toString();
					//String bDate = oneData.get("BDATE") == null ? "" : oneData.get("BDATE").toString();
					String memo = oneData.get("MEMO") == null ? "" : oneData.get("MEMO").toString();
					//String status = oneData.get("STATUS") == null ? "" : oneData.get("STATUS").toString();
					String docType = oneData.get("DOCTYPE") == null ? "" : oneData.get("DOCTYPE").toString();
					String loadDocNo = oneData.get("LOADDOCNO") == null ? "" : oneData.get("LOADDOCNO").toString();
					String createBy = oneData.get("CREATEBY") == null ? "" : oneData.get("CREATEBY").toString();
					String createDate = oneData.get("CREATEDATE") == null ? "" : oneData.get("CREATEDATE").toString();
					String createTime = oneData.get("CREATETIME") == null ? "" : oneData.get("CREATETIME").toString();
					String create_datetime = "";
					String create_date = oneData.get("BDATE").toString();
					if (createDate != null && createDate.length() > 0 && createTime != null
							&& createTime.length() > 0) {
						create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
					}
					String modifyBy = oneData.get("MODIFYBY") == null ? "" : oneData.get("MODIFYBY").toString();
					String modifyDate = oneData.get("MODIFYDATE") == null ? "" : oneData.get("MODIFYDATE").toString();
					String modifyTime = oneData.get("MODIFYTIME") == null ? "" : oneData.get("MODIFYTIME").toString();
					String modify_datetime = "";
					if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null
							&& modifyTime.length() > 0) {
						modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
					}
					String confirmBy = oneData.get("CONFIRMBY") == null ? "" : oneData.get("CONFIRMBY").toString();
					String confirmDate = oneData.get("CONFIRMDATE") == null ? "" : oneData.get("CONFIRMDATE").toString();
					String confirmTime = oneData.get("CONFIRMTIME") == null ? "" : oneData.get("CONFIRMTIME").toString();
					String approve_datetime = "";
					if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null
							&& confirmTime.length() > 0) {
						approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
					}
					String accountBy = oneData.get("ACCOUNTBY") == null ? "" : oneData.get("ACCOUNTBY").toString();
					String accountDate = oneData.get("ACCOUNTDATE") == null ? "" : oneData.get("ACCOUNTDATE").toString();
					String accountTime = oneData.get("ACCOUNTTIME") == null ? "" : oneData.get("ACCOUNTTIME").toString();
					String posted_datetime = "";
					if (accountDate != null && accountDate.length() > 0 && accountTime != null
							&& accountTime.length() > 0) {
						posted_datetime = DCP_ConversionTimeFormat.converToDatetime(accountDate + accountTime);
					}
					String cancelBy = oneData.get("CANCELBY") == null ? "" : oneData.get("CANCELBY").toString();
					String transferShop = oneData.get("TRANSFERSHOP") == null ? "" : oneData.get("TRANSFERSHOP").toString();
					
					String site_no = oneData.get("SHOPID").toString();
					String poPTemplateNO = oneData.get("PTEMPLATENO").toString();
					String bsNO = oneData.get("BSNO") == null ? "" : oneData.get("BSNO").toString();
					String oType = oneData.get("OTYPE").toString();
					
					// 获取单身数据
					sql = this.getQueryDetailSql(shopId, eId, organizationNO, stockInNO100);
					List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);
					
					if (getQDataDetail != null	&& getQDataDetail.isEmpty() == false) {
						// payload对象
						JSONObject payload = new JSONObject();
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();
						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject();
						JSONArray request_detail = new JSONArray();
						
						for (Map<String, Object> oneDataDetail : getQDataDetail) {
							// 获取单身数据并赋值
							JSONObject body = new JSONObject();
							
							String item = oneDataDetail.get("ITEM") == null ? "" : oneDataDetail.get("ITEM").toString();
							String oitem = oneDataDetail.get("OITEM") == null ? "" : oneDataDetail.get("OITEM").toString();
							String pluNO = oneDataDetail.get("PLUNO") == null ? "" : oneDataDetail.get("PLUNO").toString();
							String featureNo = oneDataDetail.get("FEATURENO") == null ? "" : oneDataDetail.get("FEATURENO").toString();
							String baseUnit = oneDataDetail.get("BASEUNIT") == null ? "" : oneDataDetail.get("BASEUNIT").toString();
							String punit = oneDataDetail.get("PUNIT") == null ? "" : oneDataDetail.get("PUNIT").toString();
							String pqty = oneDataDetail.get("PQTY") == null ? "0" : oneDataDetail.get("PQTY").toString();
							String baseQty = oneDataDetail.get("BASEQTY") == null ? "0" : oneDataDetail.get("BASEQTY").toString();
							String unitRatio =oneDataDetail.get("UNITRATIO") == null ? "0" :  oneDataDetail.get("UNITRATIO").toString();
							String price = oneDataDetail.get("PRICE") == null ? "0" : oneDataDetail.get("PRICE").toString();
							String amt = oneDataDetail.get("AMT") == null ? "0" : oneDataDetail.get("AMT").toString();
							String plu_barcode = oneDataDetail.get("PLUBARCODE") == null ? "" : oneDataDetail.get("PLUBARCODE").toString();
							String WAREHOUSE = oneDataDetail.get("WAREHOUSE") == null ? "" : oneDataDetail.get("WAREHOUSE").toString();
							String batchNO = oneDataDetail.get("BATCH_NO") == null ? "" : oneDataDetail.get("BATCH_NO").toString();
							String prodDate = oneDataDetail.get("PROD_DATE") == null ? "" : oneDataDetail.get("PROD_DATE").toString();
							String distriPrice = oneDataDetail.get("DISTRIPRICE") == null ? "0" : oneDataDetail.get("DISTRIPRICE").toString();
							String distriAmt = oneDataDetail.get("DISTRIAMT") == null ? "0" : oneDataDetail.get("DISTRIAMT").toString();
							
							
							body.put("seq", item);
							body.put("item_no", pluNO);
							body.put("feature_no", featureNo);
							body.put("warehouse_no", WAREHOUSE);
							body.put("packing_unit", punit);
							body.put("packing_qty", pqty);
							body.put("base_unit", baseUnit);
							body.put("base_qty", baseQty);
							body.put("reason_no", bsNO);
							body.put("item_batch_no", batchNO);
							body.put("prod_date", prodDate);
							body.put("price",price );
							body.put("amount",amt );
							body.put("distri_price",distriPrice );
							body.put("distri_amount",distriAmt );
							
							request_detail.put(body);
						}
						
						// 给单头赋值
						header.put("create_date", create_date);
						header.put("creator", createBy);
						header.put("posted_date", accountDate);
						header.put("site_no", site_no);
						header.put("front_no", stockInNO100);
						header.put("doc_type", "2");
						
						
						
						//【ID1022290】3.0其他出库上传接口增加来源菜单字段  by jinzma 20211210
						String source_menu="";
						
						//【ID1022937】3.0其他出入库上传时source_menu增加枚举值3-拼胚 by jinzma 20211227
						// 当入库单中DCP_STOCKIN.doc_type=3，otype=1的单据上传时source_menu传3-拼胚；
						if (!Check.Null(docType) && !Check.Null(oType)){
							if (docType.equals("3") && oType.equals("1")){
								source_menu="3";
							}
						}
						
						header.put("source_menu",source_menu);
						header.put("remark", memo);
						header.put("version", "3.0");
						
						header.put("request_detail", request_detail);
						request.put(header);
						
						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);
						
						String str = payload.toString();
						
						logger.info("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create请求T100传入参数：  " + str + "\r\n");
						String resbody ="";
						// 执行请求操作，并拿到结果（同步阻塞）
						try {
							resbody = HttpSend.Send(str, "inventory.adjust.create", eId,shopId,organizationNO,stockInNO100);
							
							logger.info("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create请求T100返回参数：  "+ "\r\n单号="+ stockInNO100 + "\r\n" + resbody + "******\r\n");
							if(Check.Null(resbody) || resbody.isEmpty() ) {
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
							
							String	code = execution_res.getString("code");
							//String sqlcode = execution_res.getString("sqlcode");
							//String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
							String description ="";
							if  (!execution_res.isNull("description") ) {
								description = execution_res.getString("description");
							}
							if (code.equals("0")) {
								// values
								Map<String, DataValue> values = new HashMap<String, DataValue>() ;
								DataValue v= new DataValue("Y", Types.VARCHAR);
								values.put("process_status", v);
								DataValue v1 = new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()) , Types.VARCHAR);
								values.put("UPDATE_TIME", v1);
								
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
								DataValue c4 = new DataValue(stockInNO100, Types.VARCHAR);
								conditions.put("stockInNO", c4);
								
								this.doUpdate("DCP_stockin", values, conditions);
								
								//删除WS日志 By jzma 20190524
								InsertWSLOG.delete_WSLOG(eId, shopId,"1",stockInNO100);
								sReturnInfo="0";
							}
							else
							{
								//
								sReturnInfo="ERP返回错误信息:" + code + "," + description;
								
								InsertWSLOG.insert_WSLOG("inventory.adjust.create",stockInNO100,eId,organizationNO,"1",str,resbody,code,description) ;
							}
						} catch (Exception e) {
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("inventory.adjust.create",stockInNO100,eId,shopId,"1",str,resbody,"-1",e.getMessage());
							sReturnInfo="错误信息:" + e.getMessage();
							
							//System.out.println(e.toString());
							
							logger.error("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stockInNO100 + "\r\n报错信息："+e.getMessage()+"******\r\n");
							
						}
					} else {
						//
						sReturnInfo="错误信息:无单身数据！";
						
						logger.info("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +stockInNO100 + "无单身数据！******\r\n");
					}
				}
			} else {
				//
				sReturnInfo="错误信息:无单头数据！";
				
				logger.info("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create没有要上传的单头数据******\r\n");
			}
			//*******************************************************************************
			//*****************结束ERP规格doc_type=2其他入库单结束***************************************
			//logger.info("\r\n*********结束ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create结束************\r\n");
			//*******************************************************************************
			
			
			
		} catch (Exception e) {
			try
			{
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);
				
				pw.flush();
				pw.close();
				
				errors.flush();
				errors.close();
				
				logger.error("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create报错信息" + e.getMessage() +"\r\n" + errors+ "******\r\n");
				
				pw=null;
				errors=null;
				
			} catch (IOException e1) {
				logger.error("\r\n******ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create报错信息" + e.getMessage() + "******\r\n");
			}
			
			//
			sReturnInfo="错误信息:" + e.getMessage();
			
		} finally {
			bRun=false;//
			logger.info("\r\n*********ERP规格doc_type=2其他入库单,其他库存调整inventory.adjust.create定时调用End:************\r\n");
		}
		
		
		return sReturnInfo;
		
	}
	
	
	
	
	//DCP_STOCKIN
	protected String getQuerySql() throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ "select EID,ORGANIZATIONNO,STOCKINNO,SHOPID,BDATE,MEMO,STATUS,DOCTYPE,OTYPE,OFNO,LOADDOCTYPE,LOADDOCNO,PTEMPLATENO,CREATEBY,CREATEDATE,CREATETIME,MODIFYBY,"
				+ " MODIFYDATE,MODIFYTIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME,CANCELBY,CANCELDATE,CANCELTIME,TRANSFERSHOP,"
				+ " TOTPQTY,TOTAMT,TOTCQTY,BSNO "
				+ " from ( "
				+ " SELECT A.EID,A.ORGANIZATIONNO,A.STOCKINNO as stockInNO,A.SHOPID as SHOPID,A.BDATE as bDate,A.MEMO as memo,A.STATUS as status,A.DOC_TYPE as docType,"
				+ " A.OTYPE as otype,A.OFNO as ofno,A.LOAD_DOCTYPE as loadDocType,A.LOAD_DOCNO as loadDocNO,A.PTEMPLATENO as pTemplateNO,A.CREATEBY as createBy,"
				+ " A.CREATE_DATE as createDate,A.CREATE_TIME as createTime,A.modifyBY as modifyBy,"
				+ " A.modify_DATE as modifyDate,A.modify_TIME as modifyTime,A.CONFIRMBY as confirmBy,A.CONFIRM_DATE as confirmDate,"
				+ " A.CONFIRM_TIME as confirmTime,A.ACCOUNTBY as accountBy,A.ACCOUNT_DATE as accountDate,A.ACCOUNT_TIME as accountTime,"
				+ " A.CANCELBY as cancelBy,A.CANCEL_DATE as cancelDate,A.CANCEL_TIME as cancelTime,A.TRANSFER_SHOP as transferShop,"
				+ " A.TOT_PQTY as totpqty,A.TOT_AMT as totamt,A.TOT_CQTY as totcqty,BSNO "
				+ " FROM DCP_STOCKIN A "
				+ " WHERE A.doc_type = '3' AND A.status = '2' AND A.process_status = 'N' "
		);//doc_type 0-配送收货  1-调拨收货 3-其他入库单
		
		
		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false) {
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and STOCKINNO='"+pBillNo+"' ");
			
		}
		
		sqlbuf.append(" ) TBL ");
		
		return sqlbuf.toString();
	}
	
	//DCP_STOCKIN_DETAIL
	protected String getQueryDetailSql(String shopId, String eId, String organizationNO, String stockInNO) throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ " select ITEM,OITEM,PLUNO,baseunit,PUNIT,PQTY,baseqty,UNITRATIO,PRICE,AMT,PLUBARCODE,WAREHOUSE,batch_no,"
				+ " prod_date,DISTRIPRICE,DISTRIAMT,featureno  "
				+ " from ("
				+ " SELECT a.item,a.oitem,a.pluNO,a.baseunit,a.punit,a.pqty,a.baseqty,a.UNIT_RATIO as unitRatio,"
				+ " a.price,a.amt,a.plu_barcode as pluBarcode,WAREHOUSE,a.batch_no,a.prod_date,DISTRIPRICE,DISTRIAMT,featureno  "
				+ " FROM DCP_STOCKIN_DETAIL A"
				+ " WHERE A.SHOPID = '"+shopId+"' AND A.EID = '"+eId+"' AND A.ORGANIZATIONNO = '"+organizationNO+"'  AND A.STOCKINNO = '"+stockInNO+"' "
		);
		
		sqlbuf.append(" ) TBL ");
		
		return sqlbuf.toString();
	}
	
	
}
