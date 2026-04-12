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

import com.dsc.spos.service.imp.json.DCP_ConversionTimeFormat;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.HttpSend;


//********************拆解单据上传**************************
//***********************************************************
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class DisassemblyProcess extends InitJob {
	//******兼容即时服务的,只查询指定的那张单据******
	String pEId="";
	String pShop="";
	String pOrganizationNO="";
	String pBillNo="";

	public DisassemblyProcess() {

	}

	public DisassemblyProcess(String eId,String shopId,String organizationNO, String billNo) {
		pEId=eId;
		pShop=shopId;
		pOrganizationNO=organizationNO;
		pBillNo=billNo;
	}

	Logger logger = LogManager.getLogger(DisassemblyProcess.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	//0-代表成功  其他返回失败信息
	public String doExe() {
		//返回信息
		String sReturnInfo="";
		//此服务是否正在执行中
		if (bRun && pEId.equals("")) {
			logger.trace("\r\n*********拆解单处理Disassembly.process正在执行中,本次调用取消:************\r\n");
			sReturnInfo="定时传输任务-拆解单处理Disassembly.process正在执行中！";
			return sReturnInfo;
		}

		bRun=true;//			

		logger.trace("\r\n*********拆解单处理Disassembly.process定时调用Start:************\r\n");


		try {

			//DCP_PSTOCKIN
			String sql = this.getQuerySql100_11();

			//logger.trace("\r\n******拆解单处理Disassembly.process 执行SQL语句："+sql+"******\r\n");

			List<Map<String, Object>> getQData100_11 = this.doQueryData(sql, null);
			if (getQData100_11 != null && getQData100_11.isEmpty() == false) {
				for (Map<String, Object> oneData100 : getQData100_11) {
					String shopId = oneData100.get("SHOPID") == null ? "" : oneData100.get("SHOPID").toString();
					String eId = oneData100.get("EID") == null ? "" : oneData100.get("EID").toString();
					String organizationNO = oneData100.get("ORGANIZATIONNO") == null ? "" : oneData100.get("ORGANIZATIONNO").toString();
					String confirmBy = oneData100.get("CONFIRMBY") == null ? "" : oneData100.get("CONFIRMBY").toString();
					String confirmDate = oneData100.get("CONFIRM_DATE") == null ? "" : oneData100.get("CONFIRM_DATE").toString();
					String PSTOCKINNO = oneData100.get("PSTOCKINNO") == null ? "" : oneData100.get("PSTOCKINNO").toString();
					//【ID1022003】 3.0要货单、拆解单上传ERP少传对账日期 by jinzma 20211117
					String account_date = oneData100.get("ACCOUNT_DATE") == null ? "" : oneData100.get("ACCOUNT_DATE").toString();
					if (account_date != null && account_date.length() >0 ) {
						account_date = DCP_ConversionTimeFormat.converToDate(account_date);
					}
					String condate = "";
					if (confirmDate != null && confirmDate.length() >0 ) {
						condate= confirmDate.substring(0, 4)+"-"+confirmDate.substring(4,6)+"-"+confirmDate.substring(6,8);
						//condate = ConversionTimeFormat.converToDatetime(confirmDate); 20171222
					}
					String memo = oneData100.get("MEMO") == null ? "" : oneData100.get("MEMO").toString();

					// 获取单身数据
					String sqlDetail = this.getQuerySql101_11();
					String[] conditionValues101_11 = { eId, organizationNO, PSTOCKINNO }; // 查詢條件
					List<Map<String, Object>> getQData101_11 = this.doQueryData(sqlDetail, conditionValues101_11);
					//获取原料表的数据
					String sqladjustDetail = this.getQuerySql_adjustDetail();
					String[] conditionValues_adjustDetail = { PSTOCKINNO, eId, organizationNO, shopId };
					List<Map<String, Object>> getQData_adjustDetail = this.doQueryData(sqladjustDetail, conditionValues_adjustDetail);
					if (getQData101_11 != null && getQData101_11.isEmpty() == false) {
						// 定义t100传入json
						JSONObject t100req = new JSONObject();
						//t100req中的payload对象
						JSONObject payload = new JSONObject();
						//自定义payload中的json结构
						JSONObject std_data = new JSONObject();
						JSONObject parameter = new JSONObject();

						JSONArray request = new JSONArray();
						JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
						JSONArray request_detail = new JSONArray(); // 存所有单身
						for (Map<String, Object> oneData101 : getQData101_11) {
							String item = oneData101.get("ITEM") == null ? "" : oneData101.get("ITEM").toString();
							String pluNO = oneData101.get("PLUNO") == null ? "" : oneData101.get("PLUNO").toString();
							String featureNo = oneData101.get("FEATURENO") == null ? "" : oneData101.get("FEATURENO").toString();
							String baseUnit = oneData101.get("BASEUNIT") == null ? "" : oneData101.get("BASEUNIT").toString();
							String baseQty = oneData101.get("BASEQTY") == null ? "0" : oneData101.get("BASEQTY").toString();
							String WAREHOUSE = oneData101.get("WAREHOUSE") == null ? "" : oneData101.get("WAREHOUSE").toString();
							String batchNO = oneData101.get("BATCH_NO") == null ? ""	: oneData101.get("BATCH_NO").toString();
							String prodDate = oneData101.get("PROD_DATE") == null ? ""	: oneData101.get("PROD_DATE").toString();

							//获取库存调整的明细
							JSONArray material_detail = new JSONArray(); // 存所有单身
							if (getQData_adjustDetail != null && getQData_adjustDetail.isEmpty() == false) {
								for (Map<String, Object> oneData_adjustDeatil : getQData_adjustDetail) {
									//2018-09-11 修改原来的oitem 为mitem,其他的字段不动, 
									//字段来源从DCP_ADJUST_DETAIL(库存调整单) 换为DCP_PSTOCKIN_MATERIAL(组合拆解原料明细表)
									//MITEM,ITEM,PLUNO,FEATURENO,WUNIT,WQTY,WAREHOUSE 
									String material_Mitem = oneData_adjustDeatil.get("MITEM") == null ? "" : oneData_adjustDeatil.get("MITEM").toString();
									String material_item = oneData_adjustDeatil.get("ITEM") == null ? "" : oneData_adjustDeatil.get("ITEM").toString();
									String material_featureNo = oneData_adjustDeatil.get("FEATURENO") == null ? "" : oneData_adjustDeatil.get("FEATURENO").toString();
									String material_pluNO = oneData_adjustDeatil.get("PLUNO") == null ? "" : oneData_adjustDeatil.get("PLUNO").toString();
									String material_baseUnit = oneData_adjustDeatil.get("BASEUNIT") == null ? "" : oneData_adjustDeatil.get("BASEUNIT").toString();
									String material_baseQty = oneData_adjustDeatil.get("BASEQTY") == null ? "0" : oneData_adjustDeatil.get("BASEQTY").toString();
									String material_WAREHOUSE = oneData_adjustDeatil.get("WAREHOUSE") == null ? "" : oneData_adjustDeatil.get("WAREHOUSE").toString();
									String material_batchNO = oneData_adjustDeatil.get("BATCH_NO") == null ? "" : oneData_adjustDeatil.get("BATCH_NO").toString();
									String material_prodDate = oneData_adjustDeatil.get("PROD_DATE") == null ? "" : oneData_adjustDeatil.get("PROD_DATE").toString();
									String material_isBuckle = oneData_adjustDeatil.get("ISBUCKLE") == null ? "Y" : oneData_adjustDeatil.get("ISBUCKLE").toString();

									BigDecimal material_baseQty_b = new BigDecimal(material_baseQty);

									//拆解单处理的单身项次 ==库存调整单身的来源项次
									if(item.equals(material_Mitem)) {
										JSONObject body_detail = new JSONObject(); // 存一笔单身
										body_detail.put("material_seq", material_item);
										body_detail.put("material_item_no", material_pluNO);
										body_detail.put("material_feature_no",material_featureNo );
										body_detail.put("material_warehouse_no", material_WAREHOUSE);
										body_detail.put("material_base_unit", material_baseUnit);
										body_detail.put("material_base_qty", material_baseQty_b.toPlainString());
										body_detail.put("material_item_batch_no", material_batchNO);
										body_detail.put("material_prod_date", material_prodDate);
										body_detail.put("material_is_buckle", material_isBuckle);

										material_detail.put(body_detail);
									}
								}
							}

							// 获取单身数据并赋值
							JSONObject body = new JSONObject(); // 存一笔单身
							body.put("seq", item);
							body.put("item_no", pluNO);
							body.put("feature_no", featureNo);
							body.put("warehouse_no", WAREHOUSE);
							body.put("base_unit", baseUnit);
							body.put("base_qty", baseQty);
							body.put("remark", memo);
							body.put("item_batch_no",batchNO ) ;
							body.put("prod_date",prodDate ) ;
							body.put("material_detail", material_detail);
							request_detail.put(body);
						}

						// 给单头赋值 version
						header.put("version", "3.0");
						header.put("front_no", PSTOCKINNO);
						header.put("site_no", shopId);
						header.put("create_date", condate);
						header.put("creator", confirmBy);
						header.put("remark", memo);
						//header.put("operation_type", "1");
						header.put("reason_no", "");
						header.put("account_date",account_date);
						header.put("request_detail", request_detail);

						request.put(header);

						parameter.put("request", request);
						std_data.put("parameter", parameter);
						payload.put("std_data", std_data);

						String str = payload.toString();// 将json对象转换为字符串						

						logger.trace("\r\n******拆解单处理 Disassembly.process请求T100传入参数：  " + str + "\r\n");
						String	resbody = "";
						//执行请求操作，并拿到结果（同步阻塞）
						try {
							resbody=HttpSend.Send(str, "disassembly.process", eId, shopId,organizationNO,PSTOCKINNO);

							logger.trace("\r\n" + "拆解单处理Disassembly.process请求T100返回参数" + "\r\n单号="+ PSTOCKINNO + "\r\n" + resbody + "\r\n");
							if(Check.Null(resbody) || resbody.isEmpty() ) {
								continue;
							}
							// //将json字符串转换为json对象
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
							//description = execution_res.getString("description");
							String description = execution_res.getString("description") == null ? "" : execution_res.getString("description");

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
								DataValue c4 = new DataValue(PSTOCKINNO, Types.VARCHAR);
								conditions.put("PSTOCKINNO", c4);

								this.doUpdate("DCP_PSTOCKIN", values, conditions);
								//删除WS日志 By jzma 20190524
								InsertWSLOG.delete_WSLOG(eId, shopId,"1",PSTOCKINNO);
								//
								sReturnInfo="0";
							} else {
								//
								sReturnInfo="ERP返回错误信息:" + code + "," + description;
								InsertWSLOG.insert_WSLOG("disassembly.process",PSTOCKINNO,eId,organizationNO,"1",str,resbody,code,description);
							}

							//System.out.println("code字符串的值  " + code  + description);
						} catch (Exception e) {
							//记录WS日志 By jzma 20190524
							InsertWSLOG.insert_WSLOG("disassembly.process",PSTOCKINNO,eId,shopId,"1",str,resbody,"-1",e.getMessage());
							sReturnInfo="错误信息:" + e.getMessage();
							//System.out.println(e.toString());    					
							logger.error("\r\n服务***拆解单处理 Disassembly.process：门店=" +shopId+"组织编码=" + organizationNO + "公司编码=" +eId +" \n 单号="  + PSTOCKINNO  + e.getMessage());
						}
					} else {
						logger.trace("\r\n*** 拆解单处理Disassembly.process没单身数据****SQL="+sqlDetail+"********************************************************\r\n");
					}
				}
			} else {
				logger.trace("\r\n***拆解单处理 Disassembly.process单头没数据****SQL="+sql+"********************************************************\r\n");
			}


		} catch (Exception e) {
			try {
				StringWriter errors = new StringWriter();
				PrintWriter pw=new PrintWriter(errors);
				e.printStackTrace(pw);

				pw.flush();
				pw.close();

				errors.flush();
				errors.close();

				logger.error("\r\n******拆解单处理 Disassembly.process报错信息" + e.getMessage()+"\r\n" + errors + "******\r\n");

				pw=null;
				errors=null;
			} catch (IOException e1) {
				logger.error("\r\n******拆解单处理 Disassembly.process报错信息" + e.getMessage() + "******\r\n");
			}

			//
			sReturnInfo="错误信息:" + e.getMessage();
		} finally {
			bRun=false;//
			logger.trace("\r\n*********拆解单处理 Disassembly.process定时调用End:************\r\n");
		}

		//
		return sReturnInfo;
	}

	//DCP_PSTOCKIN 获取单头信息
	private String getQuerySql100_11() throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select SHOPID,CONFIRM_DATE,CONFIRMBY,PSTOCKINNO,organizationno,EID,MEMO,account_date from DCP_PSTOCKIN "
				+ " where  STATUS='2' and PROCESS_STATUS='N'  "
				//2018-11-09  新增 以下doc_Type ==2 过滤出拆解单 
				+ " and DOC_TYPE in ('2','4') AND (LOAD_DOCTYPE!='MES' or LOAD_DOCTYPE is null)  ");

		//******兼容即时服务的,只查询指定的那张单据******
		if (pEId.equals("")==false) {
			sqlbuf.append(" and EID='"+pEId+"' "
					+ " and SHOPID='"+pShop+"' "
					+ " and ORGANIZATIONNO='"+pOrganizationNO+"' "
					+ " and PSTOCKINNO='"+pBillNo+"' ");

		}

		return sqlbuf.toString();
	}

	//DCP_PSTOCKIN_DETAIL  获取二级单身信息
	private String getQuerySql101_11() throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select ITEM,PLUNO,baseunit,baseqty,WAREHOUSE,BATCH_NO,PROD_DATE,featureno  "
				+ " from DCP_PSTOCKIN_DETAIL where EID = ?  AND ORGANIZATIONNO = ? AND PSTOCKINNO = ?  ");
		return sqlbuf.toString();
	}

	//DCP_PSTOCKIN_MATERIAL  获取三级明细信息(子单身)
	private String getQuerySql_adjustDetail() throws Exception {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select MITEM,ITEM,PLUNO,baseunit,baseqty,WAREHOUSE,BATCH_NO,PROD_DATE,ISBUCKLE,featureno from DCP_PSTOCKIN_MATERIAL "
				+ " where PSTOCKINNO = ? AND EID=? AND ORGANIZATIONNO=? and SHOPID = ? ");
		return sqlbuf.toString();
	}


}
