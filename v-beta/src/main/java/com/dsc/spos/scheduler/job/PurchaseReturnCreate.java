package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.utils.PosPub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

//********************采购退货出库单据上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class PurchaseReturnCreate extends InitJob {
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";
	String pOrganizationNO="";
	String pBillNo="";

	public PurchaseReturnCreate() {

	}

	public PurchaseReturnCreate(String eId,String shopId,String organizationNO, String billNo) {
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(PurchaseReturnCreate.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	public String doExe() {
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals("")) {
			logger.info("\r\n*********采购退货出库purchase.return.create正在执行中,本次调用取消:************\r\n");
			sReturnInfo="定时传输任务-采购退货出库purchase.return.create正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//
		logger.info("\r\n*********采购退货出库purchase.return.create定时调用Start:************\r\n");

		try {
			String sql = this.getQuerySql();
			//logger.info("\r\n******采购退货出库purchase.return.create 执行SQL语句："+sql+"******\r\n");
			List<Map<String, Object>> getQData100_09 = this.doQueryData(sql, null);
			if (getQData100_09 != null && getQData100_09.isEmpty() == false) {
				for (Map<String, Object> oneData100 : getQData100_09) {
					String bDate = oneData100.get("BDATE") == null ? "" : oneData100.get("BDATE").toString();
					String transfer_date = "";
					if (bDate != null && bDate.length() > 0) {
						transfer_date = DCP_ConversionTimeFormat.converToDate(bDate);
					}
					String loadDocType = oneData100.get("LOADDOCTYPE") == null ? "": oneData100.get("LOADDOCTYPE").toString();
					if (loadDocType == null || loadDocType.length() <= 0) {
						loadDocType = "1";
					} else {
						loadDocType = "2";
					}

					String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
					String eId = oneData100.get("EID") == null ? "": oneData100.get("EID").toString();
					String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "": oneData100.get("ORGANIZATIONNO").toString();
					String sStockOutNO100 = oneData100.get("SSTOCKOUTNO") == null ? "": oneData100.get("SSTOCKOUTNO").toString();
					String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();
					//String docType = oneData100.get("DOCTYPE") == null ? "" : oneData100.get("DOCTYPE").toString();
					//String loadDocNo = oneData100.get("LOADDOCNO") == null ? "" : oneData100.get("LOADDOCNO").toString();
					String createBy = oneData100.get("CREATEBY") == null ? "" : oneData100.get("CREATEBY").toString();
					String createDate = oneData100.get("CREATEDATE") == null ? "": oneData100.get("CREATEDATE").toString();
					String createTime = oneData100.get("CREATETIME") == null ? "": oneData100.get("CREATETIME").toString();
					String create_datetime = "";
					if (createDate != null && createDate.length() > 0 && createTime != null&& createTime.length() > 0) {
						create_datetime = DCP_ConversionTimeFormat.converToDatetime(createDate + createTime);
					}
					String modifyBy = oneData100.get("MODIFYBY") == null ? "" : oneData100.get("MODIFYBY").toString();
					String modifyDate = oneData100.get("MODIFYDATE") == null ? "": oneData100.get("MODIFYDATE").toString();
					String modifyTime = oneData100.get("MODIFYTIME") == null ? "": oneData100.get("MODIFYTIME").toString();
					String modify_datetime = "";
					if (modifyDate != null && modifyDate.length() > 0 && modifyTime != null && modifyTime.length() > 0) {
						modify_datetime = DCP_ConversionTimeFormat.converToDatetime(modifyDate + modifyTime);
					}
					String confirmBy = oneData100.get("CONFIRMBY") == null ? "": oneData100.get("CONFIRMBY").toString();
					String confirmDate = oneData100.get("CONFIRMDATE") == null ? "": oneData100.get("CONFIRMDATE").toString();
					String confirmTime = oneData100.get("CONFIRMTIME") == null ? "": oneData100.get("CONFIRMTIME").toString();
					String approve_datetime = "";
					if (confirmDate != null && confirmDate.length() > 0 && confirmTime != null&& confirmTime.length() > 0) {
						approve_datetime = DCP_ConversionTimeFormat.converToDatetime(confirmDate + confirmTime);
					}
					String accountBy = oneData100.get("ACCOUNTBY") == null ? "": oneData100.get("ACCOUNTBY").toString();
					String accountDate = oneData100.get("ACCOUNTDATE") == null ? "": oneData100.get("ACCOUNTDATE").toString();
					String accountTime = oneData100.get("ACCOUNTTIME") == null ? "": oneData100.get("ACCOUNTTIME").toString();
					String posted_datetime = "";
					if (accountDate != null && accountDate.length() > 0 && accountTime != null && accountTime.length() > 0) {
						posted_datetime = DCP_ConversionTimeFormat.converToDate(accountDate);
						//	converToDatetime(accountDate+accountTime);
					}
					String ofno       = oneData100.get("OFNO")==null ? "" :oneData100.get("OFNO").toString();
					String supplierNO = oneData100.get("SUPPLIER") == null ? "": oneData100.get("SUPPLIER").toString();
					String tax_code  = oneData100.get("TAXCODE") == null ? "": oneData100.get("TAXCODE").toString();

					// 拼接返回图片路径
					String ISHTTPS = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "ISHTTPS");
					String httpStr=ISHTTPS.equals("1")?"https://":"http://";
					String DomainName = PosPub.getPARA_SMS(StaticInfo.dao, eId, "", "DomainName");
					if (DomainName.endsWith("/")) {
						DomainName = httpStr+DomainName+"resource/shopimage/sstockout/";
					}else{
						DomainName = httpStr+DomainName+"/resource/shopimage/sstockout/";
					}

					// 获取单身数据
					sql = this.getQueryDetailSql(eId, organizationNO, sStockOutNO100);
					List<Map<String, Object>> getQData101_09 = this.doQueryData(sql, null);
					if (getQData101_09 != null && getQData101_09.isEmpty() == false) {
						//payload对象
						JSONObject payload = new JSONObject();
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();
						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						JSONArray request_detail = new JSONArray(); // 存所有单身
						JSONArray image_list = new JSONArray(); // 存所有图片

						for (Map<String, Object> oneData101 : getQData101_09) {
							// 获取单身数据并赋值
							JSONObject body = new JSONObject(); // 存一笔单身
							String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
							String oItem = oneData101.get("OITEM") == null ? "" : oneData101.get("OITEM").toString();
							String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
							String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
							String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
							String punit = oneData101.get("PUNIT") == null ? "" : oneData101.get("PUNIT").toString();
							String pqty = oneData101.get("PQTY") == null ? "0" : oneData101.get("PQTY").toString();
							String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
							String unitRatio = oneData101.get("UNITRATIO") == null ? "0" : oneData101.get("UNITRATIO").toString();
							String price = oneData101.get("PRICE") == null ? "0" : oneData101.get("PRICE").toString();
							String amt = oneData101.get("AMT") == null ? "0" : oneData101.get("AMT").toString();
							String distriPrice = oneData101.get("DISTRIPRICE") == null ? "0" : oneData101.get("DISTRIPRICE").toString();
							String distriAmt = oneData101.get("DISTRIAMT") == null ? "0" : oneData101.get("DISTRIAMT").toString();
							String pluBarcode = oneData101.get("PLUBARCODE") == null ? "" : oneData101.get("PLUBARCODE").toString();
							String bsNO = oneData101.get("BSNO") == null ? "" : oneData101.get("BSNO").toString();
							String WAREHOUSE=oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
							String batchNO = oneData101.get("BATCH_NO") == null ? ""	: oneData101.get("BATCH_NO").toString();
							String prodDate = oneData101.get("PROD_DATE") == null ? ""	: oneData101.get("PROD_DATE").toString();

							body.put("seq", item);
							body.put("source_seq", oItem);
							body.put("item_no", pluNO);
							body.put("feature_no",featureNo);
							body.put("warehouse_no", WAREHOUSE);
							body.put("packing_unit", punit);
							body.put("packing_qty", pqty);
							body.put("base_unit", baseUnit);
							body.put("base_qty", baseQty);
							body.put("reason_no", bsNO);
							body.put("price", price);
							body.put("amount", amt);
							body.put("item_batch_no",batchNO ) ;
							body.put("prod_date",prodDate ) ;
							body.put("distri_price", distriPrice);
							body.put("distri_amount", distriAmt);
							request_detail.put(body);
						}

						// 获取图片数据
						sql = this.getQueryImageSql(eId, organizationNO, sStockOutNO100);
						List<Map<String, Object>> getQDataImage = this.doQueryData(sql, null);
						if (getQDataImage != null && getQDataImage.isEmpty() == false) {
							for (Map<String, Object> oneImage : getQDataImage) {
								JSONObject body = new JSONObject(); // 存一笔单身
								String image_url = DomainName+oneImage.get("IMAGE").toString();
								body.put("image_url",image_url);
								image_list.put(body);
							}
						}

						// 给单头赋值
						header.put("version", "3.0");
						header.put("site_no", shopId);
						header.put("create_date", bDate);
						header.put("posted_date", posted_datetime);
						header.put("creator", confirmBy);
						header.put("source_no", ofno);//来源单号
						header.put("supplier_no", supplierNO);
						header.put("front_no", sStockOutNO100);
						header.put("remark", memo);
						header.put("tax_code",tax_code);
						header.put("request_detail", request_detail);
						header.put("image_list", image_list);
						request.put(header);

						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);

						String str = payload.toString();// 将json对象转换为字符串

						logger.info("\r\n******采购退货出库purchase.return.create请求T100传入参数：  " + str + "\r\n");
						String resbody="";

						//执行请求操作，并拿到结果（同步阻塞）
						try {
							resbody=HttpSend.Send(str, "purchase.return.create", eId, shopId,organizationNO,sStockOutNO100);
							logger.info("\r\n******采购退货出库purchase.return.create请求T100返回参数：  "+ "\r\n单号="+ sStockOutNO100 + "\r\n" + resbody + "******\r\n");
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

							String code = execution_res.getString("code");
							//String sqlcode = execution_res.getString("sqlcode");
							//String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");
							String description ="";
							if  (!execution_res.isNull("description") ) {
								description = execution_res.getString("description");
							}
							if (code.equals("0")) {
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
								DataValue c4 = new DataValue(sStockOutNO100, Types.VARCHAR);
								conditions.put("sStockOutNO", c4);

								this.doUpdate("DCP_SSTOCKOUT", values, conditions);
								//删除WS日志 By jzma 20190524
								InsertWSLOG.delete_WSLOG(eId, shopId,"1", sStockOutNO100);
								//
								sReturnInfo="0";
							} else {
								sReturnInfo="ERP返回错误信息:" + code + "," + description;
								InsertWSLOG.insert_WSLOG("purchase.return.create",sStockOutNO100,eId,organizationNO,"1",str,resbody,code,description);
							}
						} catch (Exception e) {
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("purchase.return.create",sStockOutNO100,eId,shopId,"1",str, resbody,"-1",e.getMessage());
							sReturnInfo="错误信息:" + e.getMessage();
							//System.out.println(e.toString());
							logger.error("\r\n******采购退货出库purchase.return.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockOutNO100 + "\r\n报错信息："+e.getMessage()+"******\r\n");
						}
					} else {
						sReturnInfo="错误信息:无单身数据！";
						logger.info("\r\n******采购退货出库purchase.return.create：门店=" +shopId+",组织编码=" + organizationNO + ",公司编码=" +eId +",单号="  +sStockOutNO100 + "无单身数据！******\r\n");
					}
				}
			} else {
				sReturnInfo="错误信息:无单头数据！";
				logger.info("\r\n******采购退货出库purchase.return.create没有要上传的单头数据******\r\n");
			}
		} catch (Exception e) {
			logger.error("\r\n******采购退货出库purchase.return.create报错信息" + e.getMessage()+"******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}finally {
			bRun=false;//
			logger.info("\r\n*********采购退货出库purchase.return.create定时调用End:************\r\n");
		}
		return sReturnInfo;
	}



	//DCP_SSTOCKOUT
	private String getQuerySql() throws Exception {
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(""
				+ "select EID,ORGANIZATIONNO,SSTOCKOUTNO,SHOPID,BDATE,MEMO,STATUS,LOADDOCTYPE,LOADDOCNO,CREATEBY, "
				+ " CREATEDATE,CREATETIME,CONFIRMBY,CONFIRMDATE,CONFIRMTIME,ACCOUNTBY,ACCOUNTDATE,ACCOUNTTIME, "
				+ " CANCELBY,CANCELDATE,CANCELTIME,TOTPQTY,TOTAMT,TOTCQTY,MODIFYBY,MODIFYDATE,MODIFYTIME,SUPPLIER, OFNO,LOAD_DOCNO,LOAD_DOCTYPE,TAXCODE  "
				+ " from ("
				+ " SELECT EID,ORGANIZATIONNO,SSTOCKOUTNO,SHOPID,BDATE,MEMO,STATUS, "
				+ " LOAD_DOCTYPE as LOADDOCTYPE, LOAD_DOCNO as LOADDOCNO,CREATEBY, "
				+ " CREATE_DATE as CREATEDATE,CREATE_TIME as CREATETIME,CONFIRMBY,CONFIRM_DATE as CONFIRMDATE, "
				+ " CONFIRM_TIME as CONFIRMTIME,ACCOUNTBY,ACCOUNT_DATE as ACCOUNTDATE,ACCOUNT_TIME as ACCOUNTTIME, "
				+ " CANCELBY,CANCEL_DATE as CANCELDATE,CANCEL_TIME as CANCELTIME, "
				+ " TOT_PQTY as TOTPQTY,TOT_AMT as TOTAMT,TOT_CQTY as TOTCQTY,"
				+ "	MODIFYBY,MODIFY_DATE as MODIFYDATE,MODIFY_TIME as MODIFYTIME,SUPPLIER, "
				+ " OFNO,LOAD_DOCNO,LOAD_DOCTYPE,TAXCODE "
				+ " FROM DCP_SSTOCKOUT "
				+ " WHERE process_status = 'N' and status = '2' "
		);


		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false) {
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and SSTOCKINNO='"+pBillNo+"' ");
		}

		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		return sql;
	}

	//DCP_SSTOCKOUT_DETAIL
	private String getQueryDetailSql(String eId,String organizationNO, String sStockOutNO100) throws Exception {
		String sqlbuf = ""
				+ " select item,pluNO,baseunit,punit,pqty,baseqty, "
				+ " unitRatio,price,amt,WAREHOUSE,OITEM,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,featureno  from ( "
				+ " SELECT ITEM,PLUNO,baseunit,PUNIT,PQTY,baseqty,"
				+ " UNIT_RATIO AS UNITRATIO,PRICE,AMT,WAREHOUSE,OITEM,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,featureno"
				+ " FROM DCP_SSTOCKOUT_DETAIL "
				+ " WHERE EID = '" + eId + "' AND ORGANIZATIONNO = '" + organizationNO + "' AND SSTOCKOUTNO = '" + sStockOutNO100 + "' "
				+ " ) TBL ";
		return sqlbuf;
	}

	//DCP_SSTOCKOUT_IMAGE
	private String getQueryImageSql(String eId,String organizationNO, String sStockOutNO100) throws Exception {
		String sqlbuf = ""
				+ " select image from dcp_sstockout_image "
				+ " where eid='"+eId+"' and shopid='"+organizationNO+"' and sstockoutno='"+sStockOutNO100+"' ";
		return sqlbuf;
	}

}
